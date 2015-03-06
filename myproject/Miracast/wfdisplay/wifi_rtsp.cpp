#include <stdio.h>
#include <stdlib.h>
#include <stddef.h>
#include <sys/stat.h>

#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/stat.h>
#include <unistd.h>
#include <fcntl.h>
#include <string.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <sys/time.h>
#include <sys/resource.h>
#include <time.h>
#include <pthread.h>

#include <netinet/tcp.h>
//#include <directfb.h>
#include <errno.h>
#include <ctype.h>
#include <sys/select.h>

#include "wifi_rtsp.h"
#include "wifi_rtsp_cmds.h"
#include "wifi_hdcp2.h"
#include "spu/include/hdcp2_session.h"
#include "spu/include/hdmi.h"
#include "spu/hdcp2_hal.h"
#include "deliver_info_to_dvdplayer.h"

#define LOG_TAG "MIRACAST"
#include <utils/Log.h>
#if 1
#define NP(args...)
#else
#define NP(args...) ALOGI(args)
#endif

int wifi_get_role();
int wifi_get_status(char *sPattern);

static struct thread_arg * argth = NULL;
static HDCP_THREAD_STATE g_hdcpThreadState = EM_HDCP_THREAD_STATE_PENDING;

static void send_ge_kboard_ie(unsigned char type, unsigned short ascii);

static void send_ge_mouse_ie(unsigned char type, unsigned short x, unsigned short y);

static void send_ge_mt_ie(unsigned char type, unsigned short pointer_num, unsigned short* x, unsigned short* y);

struct thread_arg
{
    int listen_port;
    bool quit;
    volatile WIFI_TRIGGER_METHOD method;
    void (*change_callback)(WIFI_SOURCE_STATE, WIFI_SOURCE_STATE);
	char rtsp_ip[512];
	int rtsp_port;
};

bool rtsp_parse_for_pattern(int nfd, char *buffer, int blen, const char * pattern, int plen, int timeout_sec) 
{
    int matched = 0;
    int read = 0;
    int eol = 0;  // end of line?
    buffer[0] = '\0';
    time_t enter = time(NULL);
    int total_out = timeout_sec;
	while (1) {
			fd_set rfds;
			struct timeval tv;
			tv.tv_sec = timeout_sec;
			tv.tv_usec = 0;
			FD_ZERO(&rfds);
			FD_SET(nfd, &rfds);
			int retval = select(nfd+1, &rfds, NULL, NULL, &tv);

			if (retval <= 0) {
					time_t now = time(NULL);
					// have we exhausted the time yet?
					if ((now - enter) >= total_out)
					{
							ALOGI("[%s %d] timeout", __FILE__, __LINE__);
							return false;  // timeout or error.
					}
					timeout_sec -= (now - enter);
					continue;
			}

			ssize_t len = recv(nfd, buffer + read, 1, 0);
			if (len<0) {
					ALOGI("READ error!!!!!!!!!!!  socket bad!,errno=%d,errstr=%s\n",errno,strerror(errno));
					buffer[read] = '\0';
					return false;
			}
			if (len == 0) {
					NP("tandy: [%s %d] errno=%d errstr=%s\n", __FILE__, __LINE__, errno, strerror(errno));
					time_t now = time(NULL);
					// have we exhausted the time yet?
					if ((now - enter) >= total_out)
					{
							ALOGI("[%s %d] timeout", __FILE__, __LINE__);
							return false;  // timeout or error.
					}
					timeout_sec -= (now - enter);
					continue;
			}
			printf("%c", *(buffer+read));
			if (*(buffer+read) == pattern[matched]) {
					matched++;
			}

			if (eol == 0 && *(buffer+read) == '\r') {
					eol ++;
			}
			else if (eol == 1 && *(buffer+read) == '\n') {
					eol ++;
			}
			else 
					eol = 0;

			if (matched == plen) {
					// found the buffer
					*(buffer + read + 1 -plen) = '\0';
					//ALOGI("found buffer1: %s\n", buffer);
					return true;
			}
			if (eol == 2) {
					// end of line.
					*(buffer + read + 1 -2) = '\0';
					//ALOGI("found buffer2 %s\n", buffer);
					return true;
			}

			read ++;
			if (read >= (blen-1)) {
					ALOGI("insufficient buffer to contain buffer\n");
					buffer[blen-1] = '\0';
					return false;
			}
	}
}

void wifi_rtsp_header_parse(int nfd, std::map<std::string, std::string, ltstr> &result) 
{
    char key[512];
    char value[512];
    result.clear();
    
    while (true) {
        if (!rtsp_parse_for_pattern(nfd, key, 512, ": ", 2)) {
            return;
        }
        if (strlen(key) == 0) {
            return;
        }
        
        rtsp_parse_for_pattern(nfd, value, 512, "\r\n", 2);
        for (unsigned int i=0; i<strlen(key); i++) 
            key[i] = tolower(key[i]);
        
        std::string mapkey(key);
        std::string mapval(value);
        
        result[mapkey] = mapval;
		ALOGI("wifi_rtsp_header_parse-- %s: %s\n", key, value);
    }    
}

void rtsp_payload_parse(int nfd, int contentLength, std::vector<std::string> &result) 
{
    int read = 0; 
    int line = 0;
    std::string val;
    char b;
    int eol = 0;
    
    while (read < contentLength) {
        ssize_t len = recv(nfd, &b, 1, 0);
        if (!len) {
            if (val.length() > 0) {
                result.push_back(val);
            }
            
            return;
        }
        read ++;
	printf("%c", b);
        val.append(1, b);
        if (eol == 0 && b == '\r')
            eol ++;
        else if (eol == 1 && b == '\n')
            eol ++;
        else 
            eol = 0;
        
        if (eol == 2) {
            val.erase(val.length() - 2, 2);
            result.push_back(val);
            line = 0;
            val.clear();
            continue;
        }
        
    }
    if (val.length() > 0) {
        ALOGI("payload line: %s\n", val.c_str());
        result.push_back(val);
    }
}

static bool rtsp_parse_command(char * cmd, int cmdlen, char* options, int optlen, struct wifi_source * src) 
{
    int nfd = src->srcnfd;
	//ALOGI("rtsp_parse_command nfd=%d\n",nfd);
    
    // first line of RTSP is the command
    if (!rtsp_parse_for_pattern(nfd, cmd, cmdlen, " ", 1) /* || strlen(cmd) == 0*/) {
    //if (!rtsp_parse_for_pattern(nfd, cmd, cmdlen, " ", 1)) {
        ALOGI("unable to figure out the RTSP command, quit!,nfd=%d cmd=%s\n",nfd,cmd);
        //return true;
        return false;
    }
   
    if (strlen(cmd) == 0) {
        // empty line, ignore
		ALOGI("I got an empty line, expecint a command, ignore!!\n");
		return true;
    }
 
    if (!rtsp_parse_for_pattern(nfd, options, optlen, "\r\n", 2)) {
        ALOGI("unable to figure out the option line\n");
        return false;
    }
	ALOGI("rtsp_parse_command-- %s: %s\n", cmd, options);
    
    if (strcasecmp(cmd, "OPTIONS") == 0) {
        return wifi_rtsp_cmd_options(src, options);
    }
    else if (strcasecmp(cmd, "GET_PARAMETER") == 0) {
	if (strstr(options, "localhost/wfd1.0") == NULL) {
		ALOGI("you are missing the RTSP:// URI\n");
		return false;
	}
        return wifi_rtsp_cmd_getparameter(src, options);
    }
    else if (strcasecmp(cmd, "SET_PARAMETER") == 0) {
	if (strstr(options, "localhost/wfd1.0") == NULL) {
		ALOGI("missing the RTSP:// URI\n");
		return  false;
	}
        return wifi_rtsp_cmd_setparameter(src, options);    
    }
        // 
    ALOGI("I don't know how to handle wifi cmd - x%sx\n", cmd);
    return false;
}

static int wifi_rtsp_select(struct wifi_source * src,
                            fd_set *rfds, fd_set *efds)
{
    int count = 0;
    int nfds;
    struct timeval tval;
    
    tval.tv_sec = 0;
    tval.tv_usec = 100000;

    FD_ZERO(rfds);
    FD_ZERO(efds);

    if (src->state > WIFI_SOURCE_DISCONNECTED && src->state != WIFI_SOURCE_TEARDOWN) {
        FD_SET(src->srcnfd, rfds);
        FD_SET(src->srcnfd, efds);
        if (src->srcnfd > count) 
            count = src->srcnfd;
    }

    if (src->i2c_listenport > 0) {
        FD_SET(src->i2c_listenport, rfds);
        FD_SET(src->i2c_listenport, efds);
        if (src->i2c_listenport > count) 
            count = src->i2c_listenport;
    }
    
    if (src->i2c_dataport > 0) {
        FD_SET(src->i2c_dataport, rfds);
        FD_SET(src->i2c_dataport, efds);
        if (src->i2c_dataport > count) 
            count = src->i2c_dataport;
    }

	/*
    if (src->hdcp2_listenport > 0) {
        FD_SET(src->hdcp2_listenport, rfds);
        FD_SET(src->hdcp2_listenport, efds);
        if (src->hdcp2_listenport > count) 
            count = src->hdcp2_listenport;
    }
    if (src->hdcp2_dataport > 0) {
        FD_SET(src->hdcp2_dataport, rfds);
        FD_SET(src->hdcp2_dataport, efds);
        if (src->hdcp2_dataport > count) 
            count = src->hdcp2_dataport;
    }
	*/

    nfds = select(count + 1, rfds, NULL, efds, &tval);
    return nfds;
}


#define MAX_WIFI_DISPLAY_SOURCE                    1
static struct wifi_source sources = {WIFI_SOURCE_DISCONNECTED, 0};
static int irtsp_thread_run=0;
static int ihdcp_thread_run=0;

static int wfdisplay_parse_port() {
	return 8554;
}

static bool wfdisplay_parse_udhcpd_release(char* ip) {
  FILE* fd = fopen("/tmp/udhcpd.leases", "r");
  if (fd == NULL)
    return false;

  // we have a udhcpd.lease file, do we have a lease?
  fseek(fd, 0, SEEK_END);
  long size = ftell(fd);

  if (size < 24) {
  	fclose(fd);
	return false;
	}
  long offset = 16 + 24 * ((size / 24) - 1);  // look for last record
  if (fseek(fd, offset, SEEK_SET) != 0) {
    fclose(fd);
    return false;
  }
  unsigned char ipv4[4];
  for (int i =0; i< 4; i++) {
    int ret = fgetc(fd);
    if (ret == EOF) {
      fclose(fd);
      return false;
    }
    ipv4[i] = (unsigned char) ret;
  }

  sprintf(ip, "%d.%d.%d.%d", ipv4[0], ipv4[1], ipv4[2], ipv4[3]);
  ALOGI("from udhcpd lease, found IP %s\n", ip);
  fclose(fd);
  return true;
}

static int ip_once = 0;
static bool wfdisplay_parse_ip(char * ip) {
	snprintf(ip, 512, "172.29.51.160");
	return true;
}

static int wifi_rtsp_outbound(unsigned short port, in_addr_t addr) {
    struct sockaddr_in out;
    //NP("tandylo: [%s:%d:%s],port=%d\n",__FILE__,__LINE__,__func__,port);
    int netfd = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    if (netfd < 0) {
    	NP("tandylo: [%s:%d:%s],port=%d\n",__FILE__,__LINE__,__func__,port);
      return netfd;
    }
    bzero(&out,sizeof(out));
    out.sin_port = htons(port);
    out.sin_family = AF_INET;
    out.sin_addr.s_addr = addr;
    //NP("tandylo: [%s:%d:%s],port=%d\n",__FILE__,__LINE__,__func__,port);
    if (connect (netfd, (struct sockaddr*) &out, sizeof(out)) < 0) {
    	NP("tandylo: [%s:%d:%s],error %s(%d)\n",__FILE__,__LINE__,__func__,strerror(errno),errno);
      close(netfd);      
      return -1;
    }
    return netfd;
}

static void wifi_rtsp_clearsource(struct wifi_source * src) 
{
    WIFI_SOURCE_STATE old = src->state;
    NP("tandy: [%s:%d:%s], time=%d,src->state=%d\n",__FILE__,__LINE__,__func__,time(NULL),src->state);
    //if (src->state < WIFI_SOURCE_TEARDOWN && 
	//	src->state > WIFI_SOURCE_DISCONNECTED) {
	//	src->state = WIFI_SOURCE_TEARDOWN;
	g_hdcpThreadState = EM_HDCP_THREAD_STATE_STOP;
	if (1) {//the wifi_rtsp_clearsource come from
		if (src->change_callback) 
	    	src->change_callback(old, WIFI_SOURCE_TEARDOWN);
    }    
	NP("tandy: [%s:%d:%s], time=%d\n",__FILE__,__LINE__,__func__,time(NULL));
    src->audiotype = S_MEDIA_TYPE_NONE;
    src->channelnum = -1;
    src->sampleRate = -1;
    src->bitspersample = -1;
    src->rtsp_pending_method = WIFI_TRIGGER_NONE;
    src->intflag = 0;
    close(src->srcnfd);
    src->srcnfd = -1;
    if (src->presentation_url) {
        free(src->presentation_url);
        src->presentation_url = NULL;
    }
    if (src->session_id) {
        free(src->session_id);
        src->session_id = NULL;
    }
    if (src->uibcfd > 0) {
        close(src->uibcfd);
        src->uibcfd = -1;
    }
    if (src->i2c_listenport > 0) {
        close(src->i2c_listenport);
        src->i2c_listenport = -1;
    }
    if (src->i2c_dataport > 0) {
        close(src->i2c_dataport);
        src->i2c_dataport = -1;
    }
    if (src->hdcp2_listenport > 0) {
        close(src->hdcp2_listenport);
        src->hdcp2_listenport = -1;
    }
    if (src->hdcp2_dataport > 0) {
        close(src->hdcp2_dataport);
        src->hdcp2_dataport = -1;
    }
	if (src->friendly_name) {
		free(src->friendly_name);
		src->friendly_name = NULL;
	}
	remove("/tmp/p2pstatus");
	src->state = WIFI_SOURCE_DISCONNECTED;    
	src->badSocket = 0;
}


static void uibc_trigger(char * line, FILE* fd) 
{
    unsigned char type = 0;
    int x, y;
    
    while (1) {
        if (strncmp(line, "MOUSE ", 6) == 0) {
	    ALOGI("oooooo, MOUSE trigger! %s\n", line);
            line += 6;
            if (strncmp(line, "mo ", 3) == 0) {
                type = MOUSE_MOTION;
                line += 3;
            }
            else if (strncmp(line, "bd ", 3) == 0) {
                type = MOUSE_BUTTON_DOWN;
                line += 3;
            }
            else if (strncmp(line, "bu ", 3) == 0) {
                type = MOUSE_BUTTON_UP;
                line +=3 ;
            }
            else 
                goto nextline;
            sscanf(line, "%d %d", &x, &y);
	    ALOGI("trigger send mouse %d, %d, %d\n", type, x, y);
            send_mouse_action(type, (unsigned short)x, (unsigned short) y);
            goto nextline;
        }
	if (strncmp(line, "KEYBOARD ", 9) == 0) {
	    char x ;
	    unsigned short send;
	    line += 9;
	    sscanf(line, "%c", &x);
	    send = x;
	    ALOGI("trigger send kb %c\n", x);
	    send_keyboard_action(KEYBOARD_DOWN, send);
	    send_keyboard_action(KEYBOARD_UP, send);
	}
        else {
            return;
        }
        
      nextline:
        if (fgets(line, 1000, fd) == NULL) {
            return;
        }
    }
}    

static WIFI_TRIGGER_METHOD file_method(void) {
    FILE *fd = fopen(SIGMA_TRIGGER_FILE, "r");
    WIFI_TRIGGER_METHOD method = WIFI_TRIGGER_NONE;
    if (fd == NULL) {
        return WIFI_TRIGGER_NONE;
    }
    char line[1024];
    if (fgets(line, 1000, fd) == NULL) {
    	fclose(fd);
        return WIFI_TRIGGER_NONE;
    }
    ALOGI("file trigger string %s\n", line);
    if (strstr(line, "PAUSE") != NULL) {
	method = WIFI_TRIGGER_PAUSE;
    }
    if (strstr(line, "PLAY") != NULL) {
        method = WIFI_TRIGGER_PLAY;
    }
    if (strstr(line, "TEARDOWN") != NULL) {
        method = WIFI_TRIGGER_TEARDOWN;
    }
    if (strstr(line, "STANDBY") != NULL) {
	method = WIFI_TRIGGER_STANDBY;
    }
    if (strstr(line, "IDR") != NULL) {
	method = WIFI_TRIGGER_IDR;
    }
    if (strstr(line, "ROUTEAUDIO") != NULL) {
	method = WIFI_TRIGGER_ROUTEAUDIO;
    }
    if (strstr(line, "HIDCREMOTE") != NULL) {
	method = WIFI_TRIGGER_UIBC_HID_REMOTE;
    }
    if (strstr(line, "UIBCREMOTE") != NULL) {
	method = WIFI_TRIGGER_UIBC_GENERIC;
    }
    if (strstr(line, "MOUSE") != NULL || 
	strstr(line, "KEYBOARD") != NULL) {
        uibc_trigger(line, fd);
    }
    ALOGI("file trigger detect %d\n", method);

    fclose(fd);
    unlink(SIGMA_TRIGGER_FILE);
    return method;
}
 
static int wifi_hdcp_select(struct wifi_source * src,
                            fd_set *rfds, fd_set *efds, int ustimeout)
{
    int count = 0;
    int nfds;
    struct timeval tval;
    
    tval.tv_sec = 0;
    tval.tv_usec = ustimeout;

    FD_ZERO(rfds);
    FD_ZERO(efds);

    if (src->hdcp2_dataport > 0) {
        FD_SET(src->hdcp2_dataport, rfds);
        FD_SET(src->hdcp2_dataport, efds);
        if (src->hdcp2_dataport > count) 
            count = src->hdcp2_dataport;
    }
    else if (src->hdcp2_listenport > 0) {
        FD_SET(src->hdcp2_listenport, rfds);
        FD_SET(src->hdcp2_listenport, efds);
        if (src->hdcp2_listenport > count) 
            count = src->hdcp2_listenport;
    }

    nfds = select(count + 1, rfds, NULL, efds, &tval);
    return nfds;
}

static void* wifi_hdcp_thread(void * arg)
{
	fd_set rfds, efds;
	int nfds=0;
	static int displayinfo=0;
	bool bOut=false;
	static int forceSocketDisconnectCount;
	
	ihdcp_thread_run=1;
	ALOGI("[LY] wifi_hdcp_thread start\n");
	while(1)
	{
		switch (g_hdcpThreadState)
		{
			case EM_HDCP_THREAD_STATE_START:
				if (displayinfo==0)
				{
					ALOGI("[LY] HDCP2 listenport=%d, dataport=%d, state=%d\n", sources.hdcp2_listenport, sources.hdcp2_dataport, g_hdcpThreadState);
					displayinfo++;
				}				
				nfds=0;
				break;
			case EM_HDCP_THREAD_STATE_STOP:	
				ALOGI("[LY] wifi_hdcp_thread stop\n");
				if (sources.hdcp2_listenport > 0) {
					close(sources.hdcp2_listenport);
					sources.hdcp2_listenport = -1;
				}
				if (sources.hdcp2_dataport > 0) {
					close(sources.hdcp2_dataport);
					sources.hdcp2_dataport = -1;
				}
				bOut = true;
				break;
			case EM_HDCP_THREAD_STATE_PENDING:
			default:
				continue;
		}
		if (bOut == true) {
			NP("tandy: [%s, %s, %d]\n", __FILE__, __func__, __LINE__);
			break;
		}
		// HDCP2 port handling
		nfds = wifi_hdcp_select(&sources, &rfds, &efds, 2000);
		if (nfds <= 0)
			continue;
        if (sources.hdcp2_dataport < 0 && listen(sources.hdcp2_listenport, 1)<0)
       	{
            		ALOGI("HDCP2 Rx is waiting for connection..........\n");
            		continue;
       	}
		if (nfds > 0)
		{
			if (sources.hdcp2_dataport > 0 )
			{
				//WiDi_UDHCPC_Enable = false;
				if (FD_ISSET(sources.hdcp2_dataport, &rfds)) {
					NP("tandylo: [%s:%d:%s] data in\n",__FILE__,__LINE__,__func__);
					if (wifi_hdcp2_process_msg(sources.hdcp2_dataport) == false)
					{
					    /*kelly retry auth
						if (forceSocketDisconnectCount++ > 30)
						{
							//sources.WDP_Disonnect = true;
							NP("tandylo: [%s:%d:%s] force disconnect hdcp thread\n",__FILE__,__LINE__,__func__);
							break;
						}kelly retry auth*/
						
						//kelly add for retry auth form source
						close(sources.hdcp2_dataport);
					    sources.hdcp2_dataport = -1;
					    //kelly add for retry auth form source
						NP("tandylo: [%s:%d:%s] read 0 byte in connection\n",__FILE__,__LINE__,__func__);
						continue;
					}
					forceSocketDisconnectCount = 0;
				}
				else
					NP("tandylo: [%s:%d:%s] no data in connection\n",__FILE__,__LINE__,__func__);

				if (FD_ISSET(sources.hdcp2_dataport, &efds)) {
					NP("tandylo: [%s:%d:%s] error data connection\n",__FILE__,__LINE__,__func__);
					close(sources.hdcp2_dataport);
					sources.hdcp2_dataport = -1;
				}
			}
			else if (sources.hdcp2_listenport > 0 && sources.hdcp2_dataport <= 0 &&
				FD_ISSET(sources.hdcp2_listenport, &rfds)) {
				struct sockaddr_in clntAddr;
				socklen_t clntLen = sizeof(clntAddr);
   			    int newfd = accept(sources.hdcp2_listenport, 
								   (struct sockaddr*)&clntAddr, 
								   &clntLen);
				if (newfd <= 0) {
					continue;
				}
				NP("tandylo: [%s:%d:%s] newfd=%d\n",__FILE__,__LINE__,__func__, newfd);
				int nodelay = 1;
				setsockopt(newfd, SOL_SOCKET, TCP_NODELAY, &nodelay, sizeof(nodelay));
				sources.hdcp2_dataport = newfd;
			}
		}
	}
	ALOGI("[LY] wifi_hdcp_thread exit\n");	
	ihdcp_thread_run=0;
	return NULL;
}

#ifdef __cplusplus
extern "C" {
#endif
H2status hdcp2_init( void );
#ifdef __cplusplus
}
#endif



static void* wifi_rtsp_thread (void * arg) 
{
	sources.state = WIFI_SOURCE_DISCONNECTED;
    struct thread_arg * targ = (struct thread_arg*) arg;
    int servSock = 0;
    WIFI_SOURCE_STATE old = sources.state;
	struct timeval org30s_tv;
    struct timeval org60s_tv; 
    struct timeval now30s_tv; 
	struct timeval now60s_tv;
	int count = 0;
	bool WiDi_UDHCPC_Enable = false;
	bool WiDi_Disonnect = false;
	int timeout = 0;
	struct stat stat_buf;
	
	memset(&org30s_tv, 0x00, sizeof(struct timeval));
	memset(&org60s_tv, 0x00, sizeof(struct timeval));
	memset(&now30s_tv, 0x00, sizeof(struct timeval));
	memset(&now60s_tv, 0x00, sizeof(struct timeval));
	
	hdcp2_init();
  
    ALOGI("wifi rtsp thread is lanched .... state is %d\n", sources.state);
    if (sources.state == WIFI_SOURCE_DISCONNECTED) {
            // detect connection first.
        while (1) {
            char buffer[512];
			
			if (stat("/tmp/WiDi_UDHCPC_Enable", &stat_buf) == 0){
				system("rm /tmp/WiDi_UDHCPC_Enable");
				WiDi_UDHCPC_Enable = true;
				ALOGI("\n[WST_Debug]==========================>[%s:%d]\n\n", __FILE__,__LINE__);
			}

			if (WiDi_UDHCPC_Enable){
				usleep(100000);
				timeout++;
				if (timeout > 100){
					ALOGI("\n[WST_Debug]==========================>[%s:%d]\n\n", __FILE__,__LINE__);
					timeout = 0;
					WiDi_Disonnect = true;
					//goto Connect_cancel;
					break;
				}
			}
			
			if (targ->quit) {
				NP("tandylo: [%s:%d:%s]\n",__FILE__,__LINE__,__func__);
				WiDi_Disonnect = true;
				//delete targ;
				//irtsp_thread_run = 0;
                //return NULL;  //clean up time
                break;
            }
            // now detect connection
            in_addr_t servip = inet_addr(targ->rtsp_ip);
            int port = targ->rtsp_port;
            if (port > 0) {
                //NP("tandylo: [%s:%d:%s]\n",__FILE__,__LINE__,__func__);
                sources.state = WIFI_SOURCE_IPOK;
                int sourcefd = wifi_rtsp_outbound(port, servip);
                if (sourcefd < 0){
                    ALOGI("sorry, unable to connect to %s:%d,time=%d\n", targ->rtsp_ip, targ->rtsp_port, time(NULL));
					usleep(100000);
					/*count++;
					if (count > 100){
						ALOGI("\n[WST_Debug]==========================>[%s:%d]\n\n", __FILE__,__LINE__);
						WiDi_Disonnect = true;
						goto Connect_cancel;
					}
					else*/
                    	continue;                        
                }
                NP("tandy: [%s:%d:%s]\n",__FILE__,__LINE__,__func__);
                targ->listen_port = sourcefd;
                targ->method = WIFI_TRIGGER_NONE;
                servSock = targ->listen_port;
		    	memset((void*) &sources, 0, sizeof(struct wifi_source));
                wifi_uibc_init();
		    	sources.address.sin_addr.s_addr = servip;
		    	ip_once = 0;
		    	NP("tandy: [%s:%d:%s]\n",__FILE__,__LINE__,__func__);
                break;
            }
            usleep(100000);
        }
    }
Connect_cancel:
	unlink("/tmp/port.log");
	unlink("/tmp/udhcpc.result");
	unlink("/tmp/udhcpd.leases");
	unlink("/tmp/p2pstatus"); // p2p script status file
	unlink(SIGMA_TRIGGER_FILE); // remove any left-over sigma cmds
		
	if (WiDi_Disonnect) {
		NP("tandylo: [%s:%d:%s]\n",__FILE__,__LINE__,__func__);
		delete targ;
		memset((void*) &sources, 0, sizeof(struct wifi_source));
		irtsp_thread_run = 0;
		return NULL;  //clean up time
	}
	NP("tandylo: [%s:%d:%s]\n",__FILE__,__LINE__,__func__);
    sources.srcnfd = servSock;
    sources.uibcfd = -1;
    sources.state = WIFI_SOURCE_CONNECTED;
    sources.audiotype = S_MEDIA_TYPE_NONE;
    sources.channelnum = -1;
    sources.sampleRate = -1;
    sources.bitspersample = -1;
    sources.intflag = 0;

    sources.change_callback = targ->change_callback;
    if (targ->change_callback) {
        targ->change_callback(old, WIFI_SOURCE_CONNECTED);
    }
    
    //sources.i2c_listenport = wifi_i2c_init();
    sources.i2c_dataport = -1;

	ALOGI("[baili]before wifi_hdcp2_init\n");
    if (access("/tmp/factory/hdcp2.1.bin", F_OK)    == 0 ||
		access("/tmp/factory_ro/hdcp2.1.bin", F_OK) == 0 ||
		access("/usr/local/bin/hdcp2.1.bin", F_OK)  == 0 ||
		access("/tmp/factory/hdcp2.2.bin", F_OK)    == 0 ||
		access("/tmp/factory_ro/hdcp2.2.bin", F_OK) == 0 ||
		access("/usr/local/bin/hdcp2.2.bin", F_OK)  == 0
	   )
    {
		ALOGI("[baili]found key in factory or factory_ro\n");
		sources.hdcp2_listenport = wifi_hdcp2_init();
    }
	else
	{
		ALOGI("[baili]can not find hdcp key, start without hdcp\n");
		sources.hdcp2_listenport = -1;
	}
	ALOGI("[baili]after wifi_hdcp2_init\n");
    sources.hdcp2_dataport = -1;
    sources.rtsp_pending_method = WIFI_TRIGGER_NONE;
	ALOGI("[baili]hdcp2_listenport %d\n", sources.hdcp2_listenport);

	timeout = 0;
	struct timeval tTime0,tTime1;
	gettimeofday(&tTime0, NULL);

	//hdcp init
    ALOGI("#### HDCP start.... #####\n");
    pthread_attr_t  hdcp2_attr;
    pthread_attr_init(&hdcp2_attr);
    pthread_attr_setdetachstate(&hdcp2_attr, PTHREAD_CREATE_DETACHED);
    if (pthread_create(&sources.hdcp2_thread_tid, &hdcp2_attr, wifi_hdcp_thread, NULL) != 0) {
        ALOGI("unable to launch hdcp thread\n");
        return false;
    }

       pthread_attr_destroy(&hdcp2_attr);	
	
	ALOGI("[LY] HDCP1 listenport=%d, dataport=%d\n", sources.hdcp2_listenport, sources.hdcp2_dataport);
	g_hdcpThreadState = EM_HDCP_THREAD_STATE_START;
	ALOGI("[LY] g_hdcpThreadState=%d\n", g_hdcpThreadState);

	/*
	int nSendBuf=32*1024;
	int nRecvBuf=32*1024;
	int nZero=0;
	setsockopt(sources.srcnfd,SOL_SOCKET,SO_SNDBUF,(const char*)&nSendBuf,sizeof(int));
	setsockopt(sources.srcnfd,SOL_SOCKET,SO_RCVBUF,(const char*)&nRecvBuf,sizeof(int));
	setsockopt(sources.srcnfd,SOL_SOCKET,SO_SNDBUF,(const char *)&nZero,sizeof(nZero));
	setsockopt(sources.srcnfd,SOL_SOCKET,SO_RCVBUF,(const char *)&nZero,sizeof(nZero));
	*/
    while (1) {
        fd_set rfds, efds;
        
		if (WiDi_Disonnect){
			ALOGI("\n[WST_Debug]==========================>[%s:%d]\n\n", __FILE__,__LINE__);
			WiDi_UDHCPC_Enable = false;
			WiDi_Disonnect = false;
			//set_wifi_rtsp_state(WIFI_SOURCE_TEARDOWN); 
			//wifi_rtsp_teardown();
			wifi_rtsp_cmd_trigger(&sources, WIFI_TRIGGER_TEARDOWN);
	        wifi_rtsp_clearsource(&sources);
			break;
		}
		int nfds = wifi_rtsp_select(&sources, &rfds, &efds);

		if (targ->quit) {
	        // time to quit!	        
	        NP("tandylo: [%s:%d:%s]\n",__FILE__,__LINE__,__func__);
			//delete targ;			
			WiDi_Disonnect= true;
			continue;		  	
			//irtsp_thread_run = 0;						
		}		
		if (targ->method == WIFI_TRIGGER_NONE) {
			targ->method = file_method();
		}

		if (targ->method != WIFI_TRIGGER_NONE) {
			ALOGI("active method %d, state %d\n", targ->method, sources.state);
			if (targ->method == WIFI_TRIGGER_STANDBY) {
				if (sources.state == WIFI_SOURCE_PLAY || 
					sources.state == WIFI_SOURCE_PAUSE ) {
					if (!wifi_rtsp_cmd_trigger(&sources, targ->method)) {
						NP("tandylo: [%s:%d:%s],method=%d\n",__FILE__,__LINE__,__func__,targ->method);
						WiDi_Disonnect= true;						
					}
					else {
						targ->method = WIFI_TRIGGER_NONE;
					}
					continue;
				}
			}
		    else if (sources.state == WIFI_SOURCE_PLAY && 
		      	targ->method == WIFI_TRIGGER_IDR) {
			    if (!wifi_rtsp_cmd_trigger(&sources, WIFI_TRIGGER_IDR)) {
					NP("tandylo: [%s:%d:%s],method=%d\n",__FILE__,__LINE__,__func__,targ->method);
					WiDi_Disonnect= true;					
				}
				else {
			    	targ->method = WIFI_TRIGGER_NONE;
				}
				continue;
		  	}
		  	else if (targ->method == WIFI_TRIGGER_PLAY) {
				ALOGI("thread active PLAY!\n");
			    if (sources.state == WIFI_SOURCE_PAUSE ||
					sources.state == WIFI_SOURCE_STANDBY) {
				    if (!wifi_rtsp_cmd_trigger(&sources, WIFI_TRIGGER_PLAY)) {
						NP("tandylo: [%s:%d:%s],method=%d\n",__FILE__,__LINE__,__func__,targ->method);
						WiDi_Disonnect= true;
						continue;
					}
		  		}
		    	targ->method = WIFI_TRIGGER_NONE;
		    	continue;
		  	}
		  	else if (sources.state == WIFI_SOURCE_PLAY &&
				targ->method == WIFI_TRIGGER_PAUSE) {
			    ALOGI("thread active PAUSE!!!\n");			    
			    if (!wifi_rtsp_cmd_trigger(&sources, WIFI_TRIGGER_PAUSE)) {
					NP("tandylo: [%s:%d:%s],method=%d\n",__FILE__,__LINE__,__func__,targ->method);
					WiDi_Disonnect= true;					
				}
				else {
			    	targ->method = WIFI_TRIGGER_NONE;			    	
				}
				continue;
		  	}
		  	else if (targ->method == WIFI_TRIGGER_TEARDOWN && 
			      (sources.state == WIFI_SOURCE_PLAY || 
			       sources.state == WIFI_SOURCE_PAUSE || 
			       sources.state == WIFI_SOURCE_STANDBY)) {
		    	ALOGI("thread active teardown\n");
		    	//wifi_rtsp_cmd_trigger(&sources, WIFI_TRIGGER_TEARDOWN);
	            //wifi_rtsp_clearsource(&sources);
	            WiDi_Disonnect= true;
				continue;
		  	}
		  	else { 
				// for all other trigger
			    ALOGI("trigger %d\n", targ->method);
			    if (!wifi_rtsp_cmd_trigger(&sources, targ->method)) {
					NP("tandylo: [%s:%d:%s],method=%d\n",__FILE__,__LINE__,__func__,targ->method);
					WiDi_Disonnect= true;
					continue;
				}
		  	}

		  	targ->method = WIFI_TRIGGER_NONE;
		}

		/*
		if (nfds <= 0 && sources.rtsp_pending_method == WIFI_TRIGGER_NONE) {
			gettimeofday(&tTime1, NULL);
			if (tTime1.tv_sec-tTime0.tv_sec >= 1) {
				if (wifi_get_status("COMPLETED")!=1) {            	
            		WiDi_Disonnect = true;
            	}
            	else {
            		tTime0 = tTime1;
            	}
        	}
        	continue;
		}
		*/
        if (sources.state > WIFI_SOURCE_DISCONNECTED && 
            (FD_ISSET(sources.srcnfd, &rfds) || 
		    sources.rtsp_pending_method != WIFI_TRIGGER_NONE)) {
		    // source send us something over rtsp, are we in the middle
		    // of hdcp nego?
		    ALOGI("hdcp2 nego %d , pending met = %d\n",wifi_hdcp2_status(NULL, NULL), sources.rtsp_pending_method);
		    if (wifi_hdcp2_status(NULL, NULL) == HDCP2_NEGO_STATUS_STARTED && 
			sources.state < WIFI_SOURCE_PLAY) {
				ALOGI("wait on hdcp2_status to change\n");
				// do nothing and wait
		    }
		    else {
		      	if (FD_ISSET(sources.srcnfd, &rfds) && nfds > 0){
	                char command[512];
	                char options[1024];
					memset(command,0,sizeof(command));
					memset(options,0,sizeof(options));
	                if (!rtsp_parse_command(command, 512, options, 1024, &sources)) 
					{
						sources.badSocket = 1;
						NP("[%s:%d]rtsp_parse_command failed.\n",__FILE__,__LINE__);
						//some bogus stuff over network?
						//delete targ;
						//wifi_rtsp_clearsource(&sources);
						//ALOGI("closed with source... goodbye!!!\n");
						
						//irtsp_thread_run = 0;						
						//return NULL;
						//break;
						WiDi_Disonnect= true;
		  				continue;
                	}
	      		}
				if (sources.rtsp_pending_method) {
					ALOGI("rtsp trigger medho\n");
					WIFI_TRIGGER_METHOD method = sources.rtsp_pending_method;
					sources.rtsp_pending_method = WIFI_TRIGGER_NONE;
					if (method==WIFI_TRIGGER_TEARDOWN) {
						NP("tandylo: [%s:%d:%s],method=%d\n",__FILE__,__LINE__,__func__,method);
						WiDi_Disonnect= true;
						continue;
					}
					if (!wifi_rtsp_cmd_trigger(&sources, method)) {
						NP("tandylo: [%s:%d:%s],method=%d\n",__FILE__,__LINE__,__func__,method);
						WiDi_Disonnect= true;
						continue;
						//delete targ;
		  				//wifi_rtsp_clearsource(&sources);
		  				//ALOGI("trigger method from rtsp failed...\n");
		  				//irtsp_thread_run = 0;		  				
		  				//return NULL;		  						  				
					}
				}

	    	}
        }
		if (nfds <=0) 
	  		continue;
            // I2C port handling
        if (sources.i2c_listenport > 0 && 
            FD_ISSET(sources.i2c_listenport, &rfds)) {
            struct sockaddr_in clntAddr;
            socklen_t clntLen = sizeof(clntAddr);

            int newfd = accept(sources.i2c_listenport, 
                               (struct sockaddr*)&clntAddr, 
                               &clntLen);
            if (newfd < 0) 
                continue;
            
            if (sources.i2c_dataport > 0) {
                close(newfd);
                continue;
            }
            int nodelayflag = 1;
            setsockopt(newfd,            /* socket affected */
                       IPPROTO_TCP,     /* set option at TCP level */
                       TCP_NODELAY,     /* name of option */
                       (char *) &nodelayflag,  /* the cast is historical*/
                       sizeof(int));    /* length of option value */

            sources.i2c_dataport = newfd;
        }
        if (sources.i2c_dataport > 0) {
            if (FD_ISSET(sources.i2c_dataport, &rfds)) {
                //wifi_i2c_incoming(sources.i2c_dataport);
	    	}
	    	if (FD_ISSET(sources.i2c_dataport, &efds)) {
				//ALOGI("error detected in i2c dataport, close\n");
				close(sources.i2c_dataport);
				sources.i2c_dataport = -1;
		    }
        }

            // HDCP2 port handling

		/*
        if (sources.hdcp2_listenport > 0 && sources.hdcp2_dataport <= 0 &&
            FD_ISSET(sources.hdcp2_listenport, &rfds)) {
            struct sockaddr_in clntAddr;
            socklen_t clntLen = sizeof(clntAddr);

            int newfd = accept(sources.hdcp2_listenport, 
                               (struct sockaddr*)&clntAddr, 
                               &clntLen);
            if (newfd < 0) {
                continue;
            }
            ALOGI("[%s:%d:%s]\n",__FILE__,__LINE__,__func__);
            if (sources.hdcp2_dataport > 0) {
				ALOGI("[%s:%d:%s]\n",__FILE__,__LINE__,__func__);
                //close(sources.hdcp2_dataport);
                close(newfd);
                continue;
            }
			ALOGI("[%s:%d:%s]\n",__FILE__,__LINE__,__func__);
            int nodelayflag = 1;
            setsockopt(newfd,
                       IPPROTO_TCP,
                       TCP_NODELAY,
                       (char *) &nodelayflag,
                       sizeof(int));
            sources.hdcp2_dataport = newfd;
        }
        if (sources.hdcp2_dataport > 0 ) {			
			//WiDi_UDHCPC_Enable = false;
            if (FD_ISSET(sources.hdcp2_dataport, &rfds)) {
				ALOGI("[%s:%d:%s]hdcp2_dataport %d\n",__FILE__,__LINE__,__func__, sources.hdcp2_dataport);
                wifi_hdcp2_process_msg(sources.hdcp2_dataport);
            }
		    else if (FD_ISSET(sources.hdcp2_dataport, &efds)) {
				ALOGI("[%s:%d:%s]\n",__FILE__,__LINE__,__func__);
				close(sources.hdcp2_dataport);
				sources.hdcp2_dataport = -1;
		    }
        }
		*/
#if (HDCP_REPEATER == 1)
		/* init state of timeval */
		if ((org30s_tv.tv_sec == 0)&&(org60s_tv.tv_sec == 0)&&(now30s_tv.tv_sec == 0)&&(now60s_tv.tv_sec == 0)) 
		{
			gettimeofday( &org30s_tv, NULL );
			gettimeofday( &org60s_tv, NULL );
			gettimeofday( &now30s_tv, NULL );
			gettimeofday( &now60s_tv, NULL );
			ALOGI("POLICE: Check HDMI encryption status start => \n");
		}
		/* run the hdmi_task function every X seconds */
		if( (now60s_tv.tv_sec - org60s_tv.tv_sec) >= POLICE_LOOP_TIME )
		{
			/* Only police if we're authenticated */
			int hst = cpu_spu_sendmsg(CID_STATE_GET, NULL, 0);
			if( H2_STATE_B4_AUTHENTICATED == hst )
			{
			   ALOGI("POLICE: Checking HDMI encryption status.\n");
			   cpu_spu_sendmsg(CID_STATE_HDMI1X_POLICING_DECT, NULL, 0);
			}
			gettimeofday( &org60s_tv, NULL );
		}
		//for debug ??
		if( (now30s_tv.tv_sec - org30s_tv.tv_sec) >= CHK_HDCP1X_LOOP_TIME )
		{
			   H2bool bMsg;
			   int result = cpu_spu_sendmsg(CID_CHECK_HDCP1X, (unsigned char *)&bMsg, sizeof(bMsg));
			   ALOGI("POLICE: Checking HDMI1X rvc list, exit:%d, result:%d\n", bMsg, result);
			   gettimeofday( &org30s_tv, NULL );
		}
		
		//update 30s/60s timer
 		gettimeofday( &now30s_tv, NULL );
		gettimeofday( &now60s_tv, NULL );
#endif
    }
    NP("tandy: [%s:%d]:%s()\n", __FILE__,__LINE__,__func__);
	irtsp_thread_run = 0;
	if (targ) {
    	NP("tandy: [%s:%d]:%s()\n", __FILE__,__LINE__,__func__);
    	delete targ;
    	targ = NULL;
    	argth=NULL;
	}
	NP("tandy: [%s:%d]:%s()\n", __FILE__,__LINE__,__func__);
    return NULL;
}

bool wifi_rtsp_init(char* ip, int port, void (*callback)(WIFI_SOURCE_STATE, WIFI_SOURCE_STATE)) 
{

    ALOGI("#### RTSP start.... #####\n");
	unlink(MIRACAST_WIFI_SOURCE_FILE);
	unlink(MIRACAST_HDCP2_INFO_FILE);
	ALOGI("[%s:%d:%s].delete %s and %s.\n",__FILE__,__LINE__,__func__,MIRACAST_WIFI_SOURCE_FILE,MIRACAST_HDCP2_INFO_FILE);
	argth = new thread_arg();
    argth->listen_port = -1;
    argth->quit = false;
    argth->method = WIFI_TRIGGER_NONE;
    argth->change_callback = callback;
	argth->rtsp_port = port;
	snprintf(argth->rtsp_ip, 512, "%s", ip);
    pthread_attr_t  attr;
    pthread_t tid;
    pthread_attr_init(&attr);
    pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED);
    if (pthread_create(&tid, &attr, wifi_rtsp_thread, (void*)argth) != 0) {
        ALOGI("unable to launch thread\n");
        return false;
    }
    pthread_attr_destroy(&attr);
    irtsp_thread_run=1;
    return true;
}

bool wifi_rtsp_quit() 
{
    if (argth&&irtsp_thread_run==1) {
    	NP("tandy: [%s:%d:%s], time=%d\n",__FILE__,__LINE__,__func__,time(NULL));
        argth->quit = true;
    }
	int iter = 100;
	do
	{
		if(irtsp_thread_run == 0) break;
		usleep(100000);
	}
	while(--iter);

	argth = NULL;

    ALOGI("Wifi RTSP quit... good bye \n");
    return true;
}

WIFI_SOURCE_STATE wifi_rtsp_state() 
{
    WIFI_SOURCE_STATE ret = sources.state;
    //if (ret == WIFI_SOURCE_TEARDOWN) //move to main_dfb.cpp, main loop
	//	sources.state = WIFI_SOURCE_DISCONNECTED;
    return ret;
}

void set_wifi_rtsp_state(WIFI_SOURCE_STATE iState)
{
	sources.state = iState;
}

bool wifi_rtsp_pause() {
	if (argth!=NULL) {
  argth->method = WIFI_TRIGGER_PAUSE;
	}
  return true;
}

bool wifi_rtsp_play() {
	  if (argth!=NULL) {
  argth->method = WIFI_TRIGGER_PLAY;
}
  return true;
}

bool wifi_rtsp_sendidr() {
	if (argth!=NULL) {
		argth->method = WIFI_TRIGGER_IDR;
	}
  return true;
}

bool wifi_rtsp_teardown() {
  if (argth!=NULL) {
	argth->method = WIFI_TRIGGER_TEARDOWN;
  }
  //pthread_join(tid, NULL);
  return true;
}

bool wifi_rtsp_standby() {
	  if (argth!=NULL) {
   argth->method = WIFI_TRIGGER_STANDBY;
}
   return true;
}

void wifi_uibc_event(unsigned char category, unsigned char * body, unsigned short length) 
{
    bool pad = false;
    int paddedlen = length + 4;
    if (paddedlen & 0x00000001) 
       paddedlen += 1;
    if (sources.uibcfd <= 0) 
        return;
    
    if (wifi_rtsp_state() !=  WIFI_SOURCE_PLAY) {
        return;
    }
	char * padbody = (char*) malloc(paddedlen);
    memset(padbody, 0 ,  paddedlen);

    unsigned short header[2];
    if (category) {
            // HIDC
        char * bytes = (char*) &header[0];
	bytes[0] = 0; bytes[1] = 1;
        //header[0] = 0x1000;
    }
    else 
        header[0] = 0;
    //header[0] = htons(header[0]);
    if (length & 0x00000001) {
	length ++;
        pad = true;
    }

    header[1] = htons(paddedlen);
    memcpy(padbody, &header, sizeof(header)); 
    memcpy(padbody+sizeof(header), body, length);
    //write(sources.uibcfd, (char*)&header, sizeof(header));
    write(sources.uibcfd, padbody, paddedlen);
    free(padbody);
}

static void send_ge_kboard_ie(unsigned char type, unsigned short ascii) 
{
    unsigned char ie[8];
    memset(ie, 0, sizeof(ie));
    
    unsigned short len = htons(5);
    ascii = htons(ascii);
    ie[0] = (type == KEYBOARD_DOWN)?3:4;
    memcpy(ie+1, &len, sizeof(len));
    memcpy(ie+4, &ascii, sizeof(ascii));
    wifi_uibc_event(INPUT_GENERIC, ie, 8);
}

static void send_ge_mouse_ie(unsigned char type, unsigned short x, unsigned short y) 
{
    unsigned short endx, endy;
    unsigned char * ie, *wr;
    long vwidth, vheight, fps;
    bool is3d;
    int mode3d;
    if (type == MOUSE_WHEEL) {
        wr = ie = (unsigned char*) malloc(5);
        *wr = 6;   // type
        wr ++ ;
        short len = htons(2);
        memcpy(wr, &len, sizeof(len));  // len
        wr += 2;

	short step = 0;
	short vertical = (short) x;
	if (vertical > 0) {
	    step =  0x6001;
	} else {
	    step =  0x4001;
	}
        step = (short) htons(step);
        memcpy(wr, &step, sizeof(step));
        wifi_uibc_event(INPUT_GENERIC, ie, 5);
	free(ie);
        return;
    }

    wifi_get_videoinfo(&is3d, &fps, &mode3d, &vwidth, &vheight);
    wr = ie = (unsigned char*) malloc(9);
    
    *wr = type;   // type
    wr ++ ;
    unsigned short len = htons(6);
    memcpy(wr, &len, sizeof(len));  // len
    wr += 2;
    *wr = 1;  // 1 pointer
    wr ++;
    *wr = 0;  // index 0
    wr ++;
    if (vwidth > 0 && vheight > 0) {
        x = x * vwidth / 1280;
        y = y * vheight / 720;
    }
    endx = htons(x);
    endy = htons(y);
    memcpy(wr, &endx, sizeof(endx));
    wr+=2;
    memcpy(wr, &endy, sizeof(endy));
    wifi_uibc_event(INPUT_GENERIC, ie, 9);
    free(ie);
}

static void send_ge_mt_ie(unsigned char type, unsigned short pointer_num, unsigned short* x, unsigned short* y)
{
    unsigned short endx, endy;
    unsigned char * ie, *wr;
    long vwidth, vheight, fps;
    bool is3d;
    int mode3d;
/*
	ALOGI("\n[%s, %d] type = %d\n", __func__, __LINE__, type);
	ALOGI("\n[%s, %d] pointer_num = %d\n", __func__, __LINE__, pointer_num);
	ALOGI("\n[%s, %d] x1 = %d\n", __func__, __LINE__, x[0]);
	ALOGI("\n[%s, %d] y1 = %d\n", __func__, __LINE__, y[0]);
	ALOGI("\n[%s, %d] x2 = %d\n", __func__, __LINE__, x[1]);
	ALOGI("\n[%s, %d] y2 = %d\n", __func__, __LINE__, y[1]);
*/
/*
    if (type == MOUSE_WHEEL) {
        wr = ie = (unsigned char*) malloc(5);
        *wr = 6;   // type
        wr ++ ;
        short len = htons(2);
        memcpy(wr, &len, sizeof(len));  // len
        wr += 2;

    short step = 0;
    short vertical = (short) x;
    if (vertical > 0) {
        step =  0x6001;
    } else {
        step =  0x4001;
    }
        step = (short) htons(step);
        memcpy(wr, &step, sizeof(step));
        wifi_uibc_event(INPUT_GENERIC, ie, 5);
    free(ie);
        return;
    }
*/
    wifi_get_videoinfo(&is3d, &fps, &mode3d, &vwidth, &vheight);
    wr = ie = (unsigned char*) malloc(3 + 1 + 5 * pointer_num);   // wr = ie = (unsigned char*) malloc(9);
	memset(wr, 0x00, (3 + 1 + 5 * pointer_num));

    //*wr = type;   // type
	if(type == MT_DOWN)
		*wr = MOUSE_BUTTON_DOWN;
	else if(type == MT_UP)
		*wr = MOUSE_BUTTON_UP;
	else if(type == MT_MOTION)
		*wr = MOUSE_MOTION;

    wr ++ ;
    unsigned short len = htons(1 + 5 * pointer_num);  // 1 or 2 pointers
    memcpy(wr, &len, sizeof(len));  // len
    wr += 2;
    *wr = (unsigned char) pointer_num; // htons(pointer_num);  // 1 or 2 pointers
    wr ++;

	for(int i = 0; i < pointer_num; i++) {
	    *wr = i;  // index number, 0 or 1 possible for 2 fingers multi touch screen
	    wr ++;
		//ALOGI("\n[%s, %d] *** x(%d) = %d\n", __func__, __LINE__, i, x[i]);
		//ALOGI("\n[%s, %d] *** y(%d) = %d\n", __func__, __LINE__, i, y[i]);
	    if (vwidth > 0 && vheight > 0) {
	        //x[i] = x[i] * vwidth / 1280;
	        //y[i] = y[i] * vheight / 720;
	        x[i] = x[i] * vwidth / 1920;  // This touch screen is 1920*1080 base.
	        y[i] = y[i] * vheight / 1080;
	    }
		//ALOGI("\n[%s, %d] *** x(%d) = %d\n", __func__, __LINE__, i, x[i]);
		//ALOGI("\n[%s, %d] *** y(%d) = %d\n", __func__, __LINE__, i, y[i]);
	    endx = htons(x[i]);
	    endy = htons(y[i]);
	    memcpy(wr, &endx, sizeof(endx));
	    wr += 2;
	    memcpy(wr, &endy, sizeof(endy));
	    wr += 2;
	}

	// dump 
/*
	ALOGI("\nstanely> Start dumping.................\n");
	for(int z = 0; z < (3 + 1 + 5 * pointer_num); z++)
	{
		//ALOGI("%02X ", ntohs(ie[z]));	
		ALOGI("%02X ", ie[z]);	
	}
	ALOGI("\n");
*/
    wifi_uibc_event(INPUT_GENERIC, ie, 3 + 1 + 5 * pointer_num);
    free(ie);
}

static short last_x = -1, last_y = -1;
static unsigned char last_buttons = 0;

void wifi_uibc_init() 
{
    last_x = -1;
    last_y = -1;
    last_buttons = 0;
    //hid_keyboard_init();
}

static void send_hid_mouse(unsigned char type, unsigned short x, unsigned short y) 
{
    char xdiff, ydiff;
    
    if (last_x == -1) 
        last_x = x;
    if (last_y == -1) 
        last_y = y;
    
    xdiff = (char) (x - last_x);
    ydiff = (char) (y - last_y);
    if (type == MOUSE_BUTTON_DOWN) {
        last_buttons |= 0x04;
    }
    else if (type == MOUSE_BUTTON_UP) 
        last_buttons = 0;
    
    char * mbuf = (char*) malloc(8);
    unsigned short len = 3;
    len = htons(len);
    memset(mbuf, 0, 8);
        // hidc mouse report
    mbuf[0] = 1;   // usb
    mbuf[1] = 1;   // mouse
    mbuf[2] = 0;   // contains report
    memcpy(mbuf+3, &len, sizeof(len));
    mbuf[5] = last_buttons;
    mbuf[6] = xdiff;
    mbuf[7] = ydiff;
    wifi_uibc_event(INPUT_HIDC, (unsigned char*) mbuf, 8);
    last_x = x;
    last_y = y;
    free(mbuf);
}


void send_mouse_action(unsigned char type, unsigned short x, unsigned short y) 
{
    if (sources.uibcfd <= 0) 
        return;

    if (IS_INPUT_GENERIC(sources.intflag) && IS_MOUSE_ENABLE(sources.intflag))
        send_ge_mouse_ie(type, x, y);
    else if (IS_INPUT_HIDC(sources.intflag) && IS_MOUSE_ENABLE(sources.intflag))
        send_hid_mouse(type, x, y);
}

void send_keyboard_action(unsigned char type, unsigned short ascii) 
{
    if (sources.uibcfd <= 0) 
        return;

	/*
    if (IS_INPUT_GENERIC(sources.intflag) && IS_KEYBOARD_ENABLE(sources.intflag)) {
	if (DFB_KEY_TYPE(ascii) == DIKT_UNICODE)
            send_ge_kboard_ie(type, ascii);
    }
    else if (IS_INPUT_HIDC(sources.intflag) && IS_KEYBOARD_ENABLE(sources.intflag)) {
        send_hid_keyboard(&sources, type, ascii);
    }
	*/
}

void send_mt_action(unsigned char type, unsigned short pointer_num, unsigned short* x, unsigned short* y)  // Multi Touch
{
    //ALOGI("\n[%s, %d] HERE(%lx)\n", __func__, __LINE__, sources.intflag);

    if (sources.uibcfd <= 0)
        return;

    if (IS_INPUT_GENERIC(sources.intflag) && IS_MT_ENABLE(sources.intflag))   
	{
            send_ge_mt_ie(type, pointer_num, x, y);
	    //ALOGI("\n[%s, %d] HERE2(%lx)\n", __func__, __LINE__, sources.intflag);
	}

    // TODO: NO HIDC SUPPRT FOR MULTI TOUCH DEVICE!!!
}

void wifi_get_videoinfo(bool *is3d, long* fps, int *mode, long * width, long* height) {
    *width = sources.video_w;
    *height = sources.video_h;
    *is3d = sources.is_3d;
    *fps = sources.fps;
    *mode = sources.format_3d;
}

void wifi_get_audioinfo(SOCKET_MEDIA_TYPE *atype, long *chan_num, long *sampleRate, long* bitspersample) 
{
    *atype = sources.audiotype;
    *chan_num = sources.channelnum;
    *sampleRate = sources.sampleRate;
    *bitspersample = sources.bitspersample;
}

int wifi_get_role()
{
	//Role=02=>client
	//Role=03=>GO
	char lbuffer[256]={0};
	char *pPtr=NULL;
	int irtn = 0;
	FILE *pf = NULL;
	sprintf(lbuffer, "iwpriv wlan0 p2p_get role |grep Role= > /tmp/p2prole.txt");
	system(lbuffer);
	//ALOGI("[%s:%d]:%s()- System cmd:%s\n", __FILE__, __LINE__, __func__, lbuffer);
	pf = fopen( "/tmp/p2prole.txt", "r" );
	if (pf!=NULL) {
		fread(lbuffer,1,sizeof(lbuffer),pf);
		ALOGI("[%s:%d]:%s()- Role==%s\n", __FILE__, __LINE__, __func__, lbuffer);
		if (strstr(lbuffer,"Role=02")!=NULL) {
			irtn = 2;
		}
		else if (strstr(lbuffer,"Role=03")!=NULL) {
			irtn = 3;
		}
		fclose(pf);
	}
	return irtn;
}

int wifi_get_status(char *sPattern)
{
	int irtn = 0;
	FILE *pf = NULL;
	char lbuffer[512]={0};
	char *pPtr=NULL;
#if  defined(REALTEK_WFDISPLAY_SIGMA)
	return 1;
#endif
	/*
	//if want to use ETHERNET test,
	return 1;
	*/	
	//1. check role
	int irole = 0;	
	
	ALOGI("[%s:%d]:%s()- \n", __FILE__, __LINE__, __func__);
	irole = wifi_get_role();
	if (irole ==2) {//client
		sprintf(lbuffer, "/usr/local/bin/IMS_Modules/Wfdisplay/scripts/wpa_cli status|grep wpa_state= > /tmp/p2pPeer.txt");
		system(lbuffer);
		pf = fopen( "/tmp/p2pPeer.txt", "r" );
		if (pf!=NULL) {
			memset(lbuffer,0,sizeof(lbuffer));
			fread(lbuffer,1,sizeof(lbuffer),pf);
			ALOGI("[%s:%d]:%s()- wpa status=%s\n", __FILE__, __LINE__, __func__, lbuffer);
			if (strstr(lbuffer,"wpa_state=")!=NULL) {
				pPtr = lbuffer+strlen("wpa_state=");
			//ALOGI("[%s:%d]:%s()- pPtr=%s,sPattern=%s\n", __FILE__, __LINE__, __func__, pPtr,sPattern);
				if (strncmp(pPtr,sPattern,strlen(sPattern))==0) {
				//ALOGI("[%s:%d]:%s()- pPtr=%s\n", __FILE__, __LINE__, __func__, pPtr);
					irtn = 1;
				}
			}
			fclose(pf);
		}
	}
	else if (irole==3) {
		memset(lbuffer,0,sizeof(lbuffer));
		if (1) {
			sprintf(lbuffer, "/usr/local/bin/IMS_Modules/Wfdisplay/scripts/hostapd_cli all_sta |grep dot11RSNAStatsSTAAddress= > /tmp/p2pPeer.txt");
			system(lbuffer);
			NP("tandy:[%s:%d]:%s()- System cmd:%s\n", __FILE__, __LINE__, __func__, lbuffer);
			pf = fopen( "/tmp/p2pPeer.txt", "r" );
			if (pf!=NULL) {
				memset(lbuffer,0,sizeof(lbuffer));
				fread(lbuffer,1,sizeof(lbuffer),pf);
				ALOGI("[%s:%d]:%s()- lbuffer=%s\n", __FILE__, __LINE__, __func__, lbuffer);			
				pPtr = strstr(lbuffer,"dot11RSNAStatsSTAAddress=");
				if (pPtr!=NULL) {
					pPtr += strlen("dot11RSNAStatsSTAAddress=");
					if (*pPtr !=0 && *pPtr!='\n') {
						irtn = 1;				
					}
				}
				fclose(pf);
			}
			else {
				NP("tandy:[%s:%d]:%s()\n", __FILE__, __LINE__, __func__);
			}
		}
	}
	else {//unlnown role
	}
	remove("/tmp/p2pPeer.txt");
	return irtn;
}

int is_rtsp_thread_run()
{
	return irtsp_thread_run;
}

int is_hdcp_thread_run()
{
	return ihdcp_thread_run;
}

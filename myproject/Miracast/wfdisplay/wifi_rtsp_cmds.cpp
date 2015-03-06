#include <stdio.h>
#include <stdlib.h>
#include <stddef.h>

#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/stat.h>
#include <unistd.h>
#include <fcntl.h>
#include <string.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <sys/time.h>
#include <time.h>
#include <pthread.h>

#include <netinet/tcp.h>

#include "wifi_rtsp.h"
#include "wifi_rtsp_cmds.h"
#include "wfd_audio_video_cap.h"
#include "systemCommand.h"
#include "l2sd-ta-msgs.h"
#include "sample_config.h"
#include "deliver_info_to_dvdplayer.h"

//#include "hdcp2_interface.h"
#include "hdcpDebug.h"
#include "HDCP2X_VSN.h"

#define HDMI_EDID_PATH  "/tmp/edid.bin"
#define HDMI_EDIR_SIZE  256

//widi3.5
//read fail default value
#define DEFAULT_CONFIG_PATH         "/usr/local/etc/widiconfig"
#define DEFAULT_LOGO_FILE           "./image/logo.png"
#define DEFAULT_INTERFACE           "wlan0"
#define DEFAULT_FRIENDLY_NAME       "RTDIW_MK2"
#define DEFAULT_MODEL_NAME          "RTDIW_MK2"
#define DEFAULT_PRODUCT_ID          "RTDIW_MK2"
#define DEFAULT_DEVICE_ID           "0000"
#define DEFAULT_DEVICE_URL          "http://intel.com/go/wirelessdisplay"
#define DEFAULT_MANUFACTURER        "Realtek"
#define DEFAULT_SOFTWARE_VERSION    "2.1.0.0"
#define DEFAULT_HARDWARE_VERSION    "2.0.0.0"

#define LOG_TAG "RTSP_CMD"
#include <utils/Log.h>
#if 0
#define NP(args...)	
#else
#define NP(args...)	ALOGI(args);
#endif

static int CSequence = 300;

#define SPEED_UP_NO_STORED_KM 1
#define SYNC_RECEIVER_PRE_COMPUTATION_SUPPORT_WITH_TRANSMITTER 1
#define DOWNGRADE_HDCP22_WHEN_NO_PROTOCOL_DESCRIPTOR 1


static void update_cseq(std::string & cseq) {
	int num = atoi(cseq.c_str());
	if (num > CSequence) 
		CSequence = num + 1;
}

//from widi
char* read_line( FILE* fin )
{
    static char buf[100];
    char * rc;

    rc = fgets( buf, sizeof(buf), fin );
    if ( rc != NULL ) {
        // chomp newline if it's there
        char * c = strrchr( buf, '\n' );
        if ( c ) *c = 0;
    }
    return rc;
}

char* read_field_value( FILE* fin, char** field, char** value )
{
    char* line;
    char* sep;

    line = read_line( fin );
    if ( line != NULL ) {
        sep = strchr( line, '=' );
        if ( sep && ((*(sep+1)) !='\0') ) {
            *field = line;
            *sep = 0;
            *value = sep+1;
        } else {
            *field = NULL;
            *value = NULL;
        }
    }

    return line;
}
int config_parse_adapter( FILE * fin, adapter_config_t * adapter )
{
    char * line;
    char * field;
    char * value;

    memset( adapter, 0, sizeof(adapter_config_t) );

    while ( (line = read_field_value( fin, &field, &value )) != NULL ) {
        if ( strcmp( line, "" ) == 0 ) break; // blank line ends the section
        if ( ! field || ! value ) continue; // skip line without field or value
        if ( strcasestr( field, "default_friendly_name" ) ) {
            snprintf( adapter->default_friendly_name, 
                    sizeof(adapter->default_friendly_name), "%s", value );
        } else if ( strcasestr( field, "friendly_name" ) ) {
            // Friendly name has to come after default_friendly_name
            // otherwise the friendly_name will match on
            // default_friendly_name as well
            snprintf( adapter->friendly_name, sizeof(adapter->friendly_name), 
                    "%s", value );
        } else if ( strcasestr( field, "model_name" ) ) {
            snprintf( adapter->model_name, sizeof(adapter->model_name),
                    "%s", value );
        } else if ( strcasestr( field, "product_id" ) ) {
            snprintf( adapter->product_id, sizeof(adapter->product_id),
                    "%s", value );
        } else if ( strcasestr( field, "manufacturer_name" ) ) {
            snprintf( adapter->manufacturer_name, 
                    sizeof(adapter->manufacturer_name), "%s", value );
        } else if ( strcasestr( field, "logo_file" ) ) {
            snprintf( adapter->logo_file, 
                    sizeof(adapter->logo_file), "%s", value );
        } else if ( strcasestr( field, "software_version" ) ) {
            snprintf( adapter->software_version,
                    sizeof(adapter->software_version), "%s", value );
        } else if ( strcasestr( field, "hardware_version" ) ) {
            snprintf( adapter->hardware_version, 
                    sizeof(adapter->hardware_version), "%s", value );
        } else if ( strcasestr( field, "device_url" ) ) {
            snprintf( adapter->device_url, sizeof(adapter->device_url),
                    "%s", value );
        } else if ( strcasestr( field, "p2p_interface_name" ) ) {
            snprintf( adapter->p2p_interface_name, 
                    sizeof(adapter->p2p_interface_name), "%s", value );
        } else if ( strcasestr( field, "interface_name" ) ) {
            snprintf( adapter->interface_name, 
                    sizeof(adapter->interface_name), "%s", value );
        } else if ( strcasestr( field, "rtsp_sink_port" ) ) {
            adapter->rtsp_sink_port = (uint16_t)atoi( value );
        } else if ( strcasestr( field, "hdcp2_port" ) ) {
            adapter->hdcp2_port = (uint16_t)atoi( value );
        } else if ( strcasestr( field, "overscan_compensation_x" ) ) {
            adapter->overscan_compensation_x = (uint16_t)atoi( value );
        } else if ( strcasestr( field, "overscan_compensation_y" ) ) {
            adapter->overscan_compensation_y = (uint16_t)atoi( value );                                
        } else {
            ALOGI( "skipping unrecognized field [%s] with value [%s]",
                    field, value);
        }
    }

    ALOGI( "parsed adapter section" );

    return 0;
}

// for sigma, do we enable to disable certain capability
static bool wifi_capability_option (const char * option) {
      FILE *fd = fopen(SIGMA_WFDOPTION_FILE, "r");
      if (fd == NULL) {
	  return false;
      }

      char line[1024];
      while (fgets(line, 1024, fd) != NULL) {
	  if (strstr(line, option) != NULL) {
	    fclose(fd);
	    return true;
	  }
      }
      fclose(fd);
      return false;
}

bool wifi_rtsp_cmd_options(struct wifi_source *src, const char* options) 
{
    std::string resp;
    std::string cseq = "0";
    std::string reqheader;
    std::map<std::string, std::string, ltstr> headers;
    time_t now;
    int nfd = src->srcnfd;
    char *timestr;
    
    wifi_rtsp_header_parse(nfd, headers);
    if (headers["cseq"] != "") 
        cseq = headers["cseq"];
    
    if (headers["require"] == "") {
            // must have require key!
        ALOGI("I don't see the require key!\n");
        goto bad_request;
    }
    reqheader = headers["require"];
    if (reqheader != "org.wfa.wfd1.0") {
        ALOGI("require header is incorrect %s\n", reqheader.c_str());
        goto bad_request;
    }
    
    resp.append("RTSP/1.0 200 OK\r\nCSeq: ");
    update_cseq(cseq);
    resp.append(cseq); resp.append("\r\nDate: ");
    now = time(NULL);
    timestr = ctime(&now);
    if (timestr && strlen(timestr)) {
       resp.append(timestr, strlen(timestr) - 1);
    }

    resp.append("\r\nPublic: org.wfa.wfd1.0, SET_PARAMETER, GET_PARAMETER\r\n\r\n");
    //ALOGI(">>>>>>\n%s\n<<<<<<<\n", resp.c_str());
    NP("tandy: [%s:%d]:%s(),resp=\n%s\n", __FILE__,__LINE__,__func__,resp.c_str());

    write(nfd, resp.c_str(), resp.size());
    return wifi_rtsp_send_options(src);
    
  bad_request:
    resp.append("RTSP/1.0 400 Bad Request\r\nCSeq: ");
    update_cseq(cseq);
    resp.append(cseq);
    resp.append("\r\nRequire: org.wfa.wfd1.0\r\n\r\n");

    //ALOGI(">>>>>>\n%s\n<<<<<<<\n", resp.c_str());
    NP("tandy: [%s:%d]:%s(),resp=\n%s\n", __FILE__,__LINE__,__func__,resp.c_str());
    write(nfd, resp.c_str(), resp.size());
    return false;
}

static int wifi_rtsp_parsehex(const char * buf, int len, unsigned long *result) 
{
    *result = 0;
    int index = 0;
    unsigned char upper;
    
    while (index < len) {
        if (buf[index] >= 'a' && buf[index] <= 'f') {
            upper = buf[index] - 'a' + 10;
        }
        else if (buf[index] >= '0' && buf[index] <= '9') {
            upper = buf[index] - '0';
        }
	else {
	    return index;
	}
       
	*result = ((*result) << 4) | upper;
        index ++;
    }
    return index;
}

static bool wifi_rtsp_parse_uibc(struct wifi_source *src, const std::string & uibc) 
{
    const char * buf = uibc.c_str();
	if (buf != NULL) {
		ALOGI("wifi_rtsp_parse_uibc(), uibc set parameter string: %s\n", buf);
	}
    buf += 21; // skip wfd_uibc_capability: 
    if (strncmp(buf, "none", 4) == 0) {
	return true;
    }
    if (strncmp(buf, "input_category_list", 19) != 0){
        ALOGI("UIBC set parameter must be input_category_list = \n");
        return false;
    }
    buf += 19;
    while (*buf != '=') 
	buf ++;
    if (*buf == '=') 
	buf ++;
    else {
	return false;
    }
    while (*buf == ' ') 
	buf++;

    bool generic_set = false;
    SET_INPUT_TYPE_GENERIC(src->intflag); 
    while (*buf != ';') {
	if (strncmp(buf, "GENERIC", 7) == 0) {
	    buf+=7;
	    generic_set = true;
	    SET_INPUT_TYPE_GENERIC(src->intflag);
	    if (*buf == ',') {
	        buf++;
	        if (*buf == ' ')
		    buf++;
	    }
	}
        else if (strncmp(buf, "HIDC", 4) == 0) {
	    if (!generic_set) {
		ALOGI("HIDC set to input \n");
	        SET_INPUT_TYPE_HIDC(src->intflag);
	    }
            buf += 4;
            if (*buf == ',') {
                buf++;
                if (*buf == ' ')
                    buf++;
            }
	}
        else{
	    ALOGI("I don't handle caplist=%s\n", buf);
	    return false;
	}
    } 
    if (IS_INPUT_GENERIC(src->intflag)) {
	buf = strstr(buf, "generic_cap_list");
        if (buf == NULL) {
            ALOGI("UIBC is expecting generic_cap_list\n");
            return false;
        }
        buf += 16;
    }
    else {
	buf = strstr(buf, "hidc_cap_list");
        if (buf == NULL) {
            ALOGI("UIBC is expecting hidc_cap_list\n");
            return false;
        }
        buf += 13;
    }
    while (*buf != '=')
        buf ++;
    if (*buf == '=')
        buf ++;
    else {
        return false;
    }
    while (*buf == ' ')
        buf++;

    while (*buf != ';' && *buf != '\0') {
        if (IS_INPUT_GENERIC(src->intflag) && 
	    strncmp(buf, "Keyboard", 8) == 0) {
	    KEY_BOARD_ENABLE(src->intflag);
	    ALOGI("generic keyboard enable\n");
            buf += 8;
            if (*buf == ',') 
                buf += 2;
            continue;
        }
        if (IS_INPUT_GENERIC(src->intflag) && 
	    strncmp(buf, "Mouse", 5) == 0) {
            buf += 5;
	    ALOGI("generic mouse enable\n");
	    MOUSE_BOARD_ENABLE(src->intflag);
            if (*buf == ',') 
                buf += 2;
            continue;
        }
        if (IS_INPUT_GENERIC(src->intflag) &&
        strncmp(buf, "MultiTouch", 10) == 0) {   // enable multi touch
            buf += 10;
        ALOGI("generic MultiTouch enable\n");
        MT_BOARD_ENABLE(src->intflag);
            if (*buf == ',')
                buf += 2;
            continue;
        }
        if (IS_INPUT_HIDC(src->intflag) &&
            strncmp(buf, "Keyboard/USB", 12) == 0) {
            buf += 12;
	    ALOGI("hidc keyboard enable\n");
	    KEY_BOARD_ENABLE(src->intflag);
            if (*buf == ',')
                buf += 2;
            continue;
        }
        if (IS_INPUT_HIDC(src->intflag) &&
            strncmp(buf, "Mouse/USB", 9) == 0) {
            buf += 9;
	    ALOGI("hidc mouse enable\n");
	    MOUSE_BOARD_ENABLE(src->intflag);
            if (*buf == ',')
                buf += 2;
            continue;
        }

        ALOGI("I'm sorry, I don't handle %s\n", buf);
        buf++;
    }

    const char *port = strstr(buf, "port");
    if (!port) {
        ALOGI("I cannot find port\n");
        return false;
    }
    port += 4;
    while (*port != '=')
        port ++;
    if (*port == '=')
        port ++;
    else {
        return false;
    }
    while (*port == ' ')
        port++;

    unsigned short portnum = atoi(port);

    src->uibcport = portnum;
    ALOGI("we have port num = %d\n", portnum);
    return true;
}

static bool wifi_rtsp_parse_audiocodecs(struct wifi_source *src, const std::string & audio) 
{
    const char * buf = audio.c_str();
    buf += 18;  // skip  "wfd_audio_codecs: "
    unsigned long acodec;
    
    if (strncmp(buf, "LPCM", 4) == 0) {
        src->audiotype = S_MEDIA_TYPE_AUDIO_WFD_LPCM; 
        wifi_rtsp_parsehex(buf + 5, 8, &acodec);
	if (acodec & 0x00000001) {
            src->sampleRate = 44100;
	} else {
	    src->sampleRate = 48000;
	}
        src->bitspersample = 16;
	src->channelnum = 2;
	ALOGI("it's set to LPCM %ld\n", acodec);
        if ((acodec & AUDIO_LPCM) == 0) 
            return false;
        else 
            return true;
    }
    
    if (strncmp(buf, "AAC", 3) == 0) {
        wifi_rtsp_parsehex(buf + 4, 8, &acodec);
        if ((acodec & AUDIO_AAC) == 0) 
            return false;
        else {
            src->audiotype = S_MEDIA_TYPE_AUDIO_HE_AAC;
            src->sampleRate = 48000;
            src->bitspersample = 16; 
            switch(acodec) {
                case 0x00000001:
                    src->channelnum = 2;
                    break;
                case 0x00000002:
                    src->channelnum = 4;
                    break;
                case 0x00000003:
                    src->channelnum = 6;
                    break;
                case 0x00000004:
                    src->channelnum = 8;
                    break;
                default:
                    break;
            }
        }    
        return true;
    }
    
    if (strncmp(buf, "AC3", 3) == 0) {
        wifi_rtsp_parsehex(buf + 4, 8, &acodec);
        if ((acodec & AUDIO_AC3) == 0) 
            return false;
        else {
            src->audiotype = S_MEDIA_TYPE_AUDIO_AC3;
            src->sampleRate = 48000;
            src->bitspersample = 16;
            switch(acodec) {
                case 0x00000001:
                    src->channelnum = 2;
                    break;
                case 0x00000002:
                    src->channelnum = 4;
                    break;
                case 0x00000003:
                    src->channelnum = 6;
                    break;
                default:
                    break;
            }   
            return true;
        }
    }
    
    return false;
}  

static bool wifi_rtsp_parse_videocodecs(struct wifi_source *src, const std::string & video, bool codeconly = false)
{
    int hexlen;
    unsigned long dummy;
    const char * buf = video.c_str();
    src->video_w = 0; src->video_h = 0;
    if (!codeconly) {
      buf += 19; // skip "wfd_video_formats:SP"
      
      hexlen = wifi_rtsp_parsehex(buf, 2, &dummy);
      buf = buf+hexlen+1; // skip "native SP"
      hexlen = wifi_rtsp_parsehex(buf, 2, &dummy);
      buf = buf+hexlen+1; // skip "prefered_display_mode SP"
    }
 
    unsigned long profile;
    hexlen = wifi_rtsp_parsehex(buf, 2, &profile);
    if (profile > H264_PROFILE) {
        ALOGI("incorrect video profile - recvd %02x, support %02x\n", (unsigned int) profile, H264_PROFILE);
        //return false;
    }
    buf += (hexlen+1);  // skip Profile+SP
    unsigned long level;
    hexlen = wifi_rtsp_parsehex(buf, 2, &level);
    if (level > H264_LEVEL) {
        ALOGI("incorrect video level\n");
        return false;
    }
    
    buf += (hexlen + 1);  // skip level+SP

    hexlen = wifi_rtsp_parsehex(buf, 8, &dummy);
    if (dummy != 0 && (dummy & CEA_SUPPORT) == 0) {
	ALOGI("I cannot support CEA format %x\n", (unsigned int)dummy);
	return false;
    }
    // for CEA video width/height
    if (dummy != 0) {
      if (dummy & CEA0) {
	src->video_w = 640; src->video_h = 480;
      }
      else if (dummy & CEA1 || 
	       dummy & CEA2) {
	src->video_w = 720; src->video_h = 480;
      }
      else if (dummy & CEA3 || 
	       dummy & CEA4) {
	src->video_w = 720; src->video_h = 576;
      }
      else if (dummy & CEA5 || 
	       dummy & CEA6 || 
	       dummy & CEA10 ||
	       dummy & CEA11 ||
	       dummy & CEA15) {
	src->video_w = 1280; src->video_h = 720;
      }
      else if (dummy & CEA7 || 
	       dummy & CEA8 || 
	       dummy & CEA9 || 
	       dummy & CEA12 ||
	       dummy & CEA13 ||
	       dummy & CEA14 || 
	       dummy & CEA16) {
	src->video_w = 1920; src->video_h = 1080;
      }
    }
    buf += (hexlen + 1);  // EA
    hexlen = wifi_rtsp_parsehex(buf, 8, &dummy);
    if (dummy !=0 && (dummy & VESA_SUPPORT) == 0) {
	ALOGI("I cannot support VESA format %x\n", (unsigned int)dummy);
	return false;
    }
    if (dummy != 0) {
      if (dummy & VESA0 || 
	  dummy & VESA1) {
	src->video_w = 800; src->video_h = 600;
      }
      else if (dummy & VESA2 || 
	       dummy & VESA3) {
	src->video_w = 1024; src->video_h = 768;
      }
      else if (dummy & VESA4 || 
	       dummy & VESA5) {
	src->video_w = 1152; src->video_h = 864;
      }
      else if (dummy & VESA6 || 
	       dummy & VESA7) {
	src->video_w = 1280; src->video_h = 768;
      }
      else if (dummy & VESA8 || 
	       dummy & VESA9) {
	src->video_w = 1280; src->video_h = 800;
      }
      else if (dummy & VESA10 || 
	       dummy & VESA11) {
	src->video_w = 1360; src->video_h = 768;
      }
      else if (dummy & VESA12 || 
	       dummy & VESA13) {
	src->video_w = 1366; src->video_h = 768;
      }
      else if (dummy & VESA14 || 
	       dummy & VESA15) {
	src->video_w = 1280; src->video_h = 1024;
      }
      else if (dummy & VESA16 || 
	       dummy & VESA17) {
	src->video_w = 1400; src->video_h = 1050;
      }
      else if (dummy & VESA18 || 
	       dummy & VESA19) {
	src->video_w = 1440; src->video_h = 900;
      }
      else if (dummy & VESA20 || 
	       dummy & VESA21) {
	src->video_w = 1600; src->video_h = 900;
      }
      else if (dummy & VESA22 || 
	       dummy & VESA23) {
	src->video_w = 1600; src->video_h = 1200;
      }
      else if (dummy & VESA24 || 
	       dummy & VESA25) {
	src->video_w = 1680; src->video_h = 1024;
      }
      else if (dummy & VESA26 || 
	       dummy & VESA27) {
	src->video_w = 1680; src->video_h = 1050;
      }
      else if (dummy & VESA28 || 
	       dummy & VESA29) {
	src->video_w = 1920; src->video_h = 1200;
      }
    }

    buf += (hexlen + 1);  // CEA
    hexlen = wifi_rtsp_parsehex(buf, 8, &dummy);
    if (dummy != 0 && (dummy & HH_SUPPORT) == 0) {
	ALOGI("I cannot support HH format %x\n", (unsigned int)dummy);
	return false;
    }
    if (dummy != 0) {
      if (dummy & HH0 || 
	  dummy & HH1) {
	src->video_w = 800; src->video_h = 480;
      }      
      else if (dummy & HH2 || 
	       dummy & HH3) {
	src->video_w = 854; src->video_h = 480;
      }
      else if (dummy & HH4 || 
	       dummy & HH5) {
	src->video_w = 864; src->video_h = 480;
      }
      else if (dummy & HH6 || 
	       dummy & HH7) {
	src->video_w = 640; src->video_h = 360;
      }
      else if (dummy & HH8 || 
	       dummy & HH9) {
	src->video_w = 960; src->video_h = 540;
      }
      else if (dummy & HH10 || 
	       dummy & HH11) {
	src->video_w = 848; src->video_h = 480;
      }
    }

    buf += (hexlen + 1);  // HH
    hexlen = wifi_rtsp_parsehex(buf, 2, &dummy);
    buf += (hexlen + 1);  // delay
    hexlen = wifi_rtsp_parsehex(buf, 4, &dummy);
    buf += (hexlen + 1);  // misc
    hexlen = wifi_rtsp_parsehex(buf, 4, &dummy);
    buf += (hexlen + 1);  // misc
    hexlen = wifi_rtsp_parsehex(buf, 2, &dummy);
    buf += (hexlen + 1);  // misc

    unsigned long maxh, maxv;
    if (strncmp(buf, "none", 4) == 0) {
	return true;
    }
    hexlen = wifi_rtsp_parsehex(buf, 4, &maxh);
    buf+= (hexlen + 1);
    if (strncmp(buf, "none", 4) == 0) {
        return true;
    }
    hexlen = wifi_rtsp_parsehex(buf, 4, &maxv);
    buf+=(hexlen+1);
    if (maxh > 1920) {
        ALOGI("max horizontal too high %ld\n", maxh);
        return false;
    }
    if (maxv > 1080) {
        ALOGI("max vertical too high %ld\n", maxv);
        return false;
    }
    src->video_w = maxh;
    src->video_h = maxv;
    return true;
}

static bool wifi_rtsp_parse_preferred(struct wifi_source *src, const std::string & prefer) 
{
    int hexlen;
    unsigned long dummy;
    const char * buf = prefer.c_str();
    buf += 28; // skip "wfd_preferred_display_mode:SP"
  
    if (strncmp(buf, "none", 4) == 0) {
	return true;
    }
 
    hexlen = wifi_rtsp_parsehex(buf, 6, &dummy);
    buf = buf+hexlen+1; // 6 HEX for p-clock

    hexlen = wifi_rtsp_parsehex(buf, 4, &dummy);
    if (dummy > 1920) {
        ALOGI("H is %ld, too large\n", dummy);
        return false;
    }
    buf = buf+hexlen+1; // skip H

    hexlen = wifi_rtsp_parsehex(buf, 4, &dummy);
    buf = buf+hexlen+1; // skip HB
    
    hexlen = wifi_rtsp_parsehex(buf, 4, &dummy);
    buf = buf+hexlen+1; // skip HSPOL-HSOFF

    hexlen = wifi_rtsp_parsehex(buf, 4, &dummy);
    buf = buf+hexlen+1; // skip HSW 

    hexlen = wifi_rtsp_parsehex(buf, 4, &dummy);
    if (dummy > 1080) {
        ALOGI("V is %ld, too large\n", dummy);
        return false;
    }
    buf = buf+hexlen+1; // skip V

    hexlen = wifi_rtsp_parsehex(buf, 4, &dummy);
    buf = buf+hexlen+1; // skip VB 

    hexlen = wifi_rtsp_parsehex(buf, 4, &dummy);
    buf = buf+hexlen+1; // skip VSPOL-VSOFF

    hexlen = wifi_rtsp_parsehex(buf, 4, &dummy);
    buf = buf+hexlen+1; // skip VSW

    hexlen = wifi_rtsp_parsehex(buf, 2, &dummy);
    buf = buf+hexlen+1; // skip VBS3D

    hexlen = wifi_rtsp_parsehex(buf, 2, &dummy);
    if (dummy!= 0) {
        ALOGI("2d-sd mode %ld, we dont support 3D\n", dummy);
        return false;
    }
    buf = buf+hexlen+1; // 2d-s3d-modes

    hexlen = wifi_rtsp_parsehex(buf, 2, &dummy);
    if (dummy > 0) {
        ALOGI("pixel %ld, 24 bits only!\n", dummy);
	return false;
    }  // p-depth
    buf = buf+hexlen+1;
    std::string vcodec;
    vcodec.append(buf);
    return wifi_rtsp_parse_videocodecs(src, vcodec, true);
}

#define WFD_3D_CODECS   0x000060ef
//#define WFD_3D_FORMATS "wfd_3d_video_formats: 38 00 01 08 00000000000060ef 02 0000 00ff 11 0780 0438\r\n"

static bool wifi_rtsp_parse_3dcodecs(struct wifi_source *src, const std::string & video) 
{
    int hexlen;
    unsigned long dummy;
    const char * buf = video.c_str();
    
    buf += 22;  // skip "wfd_3d_video_formats: "

    if (strncmp(buf, "none", 4) == 0) {
	return true;
    }

    hexlen = wifi_rtsp_parsehex(buf, 2, &dummy);
    buf += (hexlen + 1);  // skip NATIVE SP
    hexlen = wifi_rtsp_parsehex(buf, 2, &dummy);
    buf += (hexlen + 1);  // skip prefered_display SP

    unsigned long profile;
    hexlen = wifi_rtsp_parsehex(buf, 2, &profile);
    if (profile > H264_PROFILE) {
        ALOGI("incorrect 3d video profile - recvd %02x, support %02x\n", (unsigned int) profile, H264_PROFILE);
        return false;
    }
    buf += (hexlen + 1);  // skip Profile+SP
    unsigned long level;
    hexlen = wifi_rtsp_parsehex(buf, 2, &level);
    if (level > H264_LEVEL) {
        ALOGI("incorrect 3d video level\n");
        return false;
    }
    buf += (hexlen + 1);  // skip level+SP

    unsigned long long vcodec;
    unsigned long lower, upper;
    
    hexlen = wifi_rtsp_parsehex(buf, 8, &lower);
    buf += hexlen; 
    vcodec = lower; vcodec = vcodec <<32;
    hexlen = wifi_rtsp_parsehex(buf, 8, &upper);
    vcodec = vcodec | upper;
    
    if ((vcodec & VIDEO_3D_SUPPORT) == 0) {
        ALOGI("I cannot support the 3d codec %lld\n", vcodec);
        return false;
    }
    src->is_3d = true;

    if (vcodec & VIDEO_3D0) {
        src->format_3d = TOP_AND_BOTTOM_3DMODE;
        src->video_w = 1920; src->video_h = 1080;
        src->fps = 24;
    }
    else if (vcodec & VIDEO_3D1) {
        src->format_3d = TOP_AND_BOTTOM_3DMODE;
        src->video_w = 1280; src->video_h = 720;
        src->fps = 60;
    }
    else if (vcodec & VIDEO_3D2) {
        src->format_3d = TOP_AND_BOTTOM_3DMODE;
        src->video_w = 1280; src->video_h = 720;
        src->fps = 50;
    }
    else if (vcodec & VIDEO_3D13) {
        src->format_3d = SIDE_BY_SIDE_3DMODE;
        src->video_w = 1920; src->video_h = 1080;
        src->fps = 60;
    }
    else if (vcodec & VIDEO_3D14) {
        src->format_3d = SIDE_BY_SIDE_3DMODE;
        src->video_w = 1920; src->video_h = 1080;        
        src->fps = 50;
    }
    else if (vcodec & VIDEO_3D15) {
        src->format_3d = TOP_AND_BOTTOM_3DMODE;
        src->video_w = 640; src->video_h = 480;
        src->fps = 60;
    }
    else if (vcodec & VIDEO_3D16) {
        src->format_3d = SIDE_BY_SIDE_3DMODE;
        src->video_w = 640; src->video_h = 480;
        src->fps = 60;
    }
    else if (vcodec & VIDEO_3D17) {
        src->format_3d = TOP_AND_BOTTOM_3DMODE;
        src->video_w = 720; src->video_h = 480;
        src->fps = 60;
    }
    else if (vcodec & VIDEO_3D18) {
        src->format_3d = SIDE_BY_SIDE_3DMODE;
        src->video_w = 720; src->video_h = 480;
        src->fps = 60;
    }
    else if (vcodec & VIDEO_3D19) {
        src->format_3d = TOP_AND_BOTTOM_3DMODE;
        src->video_w = 720; src->video_h = 576;
        src->fps = 50;
    }
    else if (vcodec & VIDEO_3D20) {
        src->format_3d = SIDE_BY_SIDE_3DMODE;
        src->video_w = 720; src->video_h = 576;
        src->fps = 50;
    }
    else if (vcodec & VIDEO_3D21) {
        src->format_3d = TOP_AND_BOTTOM_3DMODE;
        src->fps = 24;
        src->video_w = 1280; src->video_h = 720;
    }
    else if (vcodec & VIDEO_3D22) {
        src->format_3d = SIDE_BY_SIDE_3DMODE;
        src->video_w = 1280; src->video_h = 720;
        src->fps = 24;
    }
    else if (vcodec & VIDEO_3D23) {
        src->format_3d = TOP_AND_BOTTOM_3DMODE;
        src->video_w = 1280; src->video_h = 720;
        src->fps = 25;
    }
    else if (vcodec & VIDEO_3D24) {
        src->format_3d = SIDE_BY_SIDE_3DMODE;
        src->video_w = 1280; src->video_h = 720;
        src->fps = 25;
    }
    else if (vcodec & VIDEO_3D25) {
        src->format_3d = TOP_AND_BOTTOM_3DMODE;
        src->video_w = 1280; src->video_h = 720;
        src->fps = 30;
    }
    else if (vcodec & VIDEO_3D26) {
        src->format_3d = SIDE_BY_SIDE_3DMODE;
        src->video_w = 1280; src->video_h = 720;
        src->fps = 30;
    }
    else if (vcodec & VIDEO_3D27) {
        src->format_3d = TOP_AND_BOTTOM_3DMODE;
        src->video_w = 1920; src->video_h = 1080;
        src->fps = 30;
    }
    else if (vcodec & VIDEO_3D28) {
        src->format_3d = TOP_AND_BOTTOM_3DMODE;
        src->video_w = 1920; src->video_h = 1080;
        src->fps = 50;
    }
    else if (vcodec & VIDEO_3D29) {
        src->format_3d = TOP_AND_BOTTOM_3DMODE;
        src->fps = 60;
        src->video_w = 1920; src->video_h = 1080;
    }
    else if (vcodec & VIDEO_3D30) {
        src->format_3d = SIDE_BY_SIDE_3DMODE;
        src->video_w = 1280; src->video_h = 720;        
        src->fps = 50;
    }
    else if (vcodec & VIDEO_3D31) {
        src->format_3d = SIDE_BY_SIDE_3DMODE;
        src->video_w = 1280; src->video_h = 720;
        src->fps = 60;
    }
    else if (vcodec & VIDEO_3D32) {
        src->format_3d = SIDE_BY_SIDE_3DMODE;
        src->video_w = 1920; src->video_h = 1080;
        src->fps = 24;
    }
    else if (vcodec & VIDEO_3D33) {
        src->format_3d = SIDE_BY_SIDE_3DMODE;
        src->video_w = 1920; src->video_h = 1080;
        src->fps = 50;
    }
    else if (vcodec & VIDEO_3D34) {
        src->format_3d = SIDE_BY_SIDE_3DMODE;
        src->video_w = 1920; src->video_h = 1080;
        src->fps = 60;
    }
    else {
        ALOGI("I received a 3D mode that I do not know how to handle %lld\n", vcodec);
        return false;
    }

    buf += (hexlen+1);  // 3d video formats

    hexlen = wifi_rtsp_parsehex(buf, 2, &dummy); // delay
    buf += (hexlen + 1);
    hexlen = wifi_rtsp_parsehex(buf, 4, &dummy);
    buf += (hexlen + 1);
    hexlen = wifi_rtsp_parsehex(buf, 4, &dummy);
    buf += (hexlen + 1);
    hexlen = wifi_rtsp_parsehex(buf, 2, &dummy);
    buf += (hexlen + 1);

    unsigned long maxh, maxv;

    if (strncmp(buf, "none", 4) == 0) 
	return true;
    hexlen = wifi_rtsp_parsehex(buf, 4, &maxh);
    buf+=(hexlen+1);
    if (strncmp(buf, "none", 4) == 0) 
	return true;
    hexlen = wifi_rtsp_parsehex(buf, 4, &maxv);
    buf+=(hexlen+1);
    if (maxh > MAX_H_RESOLUTION) {
        ALOGI(" 3d max horizontal too high\n");
        return false;
    }
    if (maxv > MAX_V_RESOLUTION) {
        ALOGI("3d max vertical too high\n");
        return false;
    }   

    return true;
}

bool wifi_rtsp_cmd_trigger(struct wifi_source *src, WIFI_TRIGGER_METHOD method) 
{
	/*
	return false will trigger TEARDOWN(except method already is TEARDOWN)
	*/
    char body[4096];
    char outbound[4096];
    bool retval = false;
    WIFI_SOURCE_STATE old = src->state;

    const char * gencap = "Keyboard, Mouse, MultiTouch";
    const char * hidcap = "Keyboard/USB, Mouse/USB";
    if (wifi_capability_option("nokeyboard")) {
          gencap = "Mouse";
          hidcap = "Mouse/USB";
	}
	else if (wifi_capability_option("nomouse")) {
          gencap = "Keyboard";
          hidcap = "Keyboard/USB";
    }

    switch(method) {
        case WIFI_TRIGGER_UIBC_HID_REMOTE:
        case WIFI_TRIGGER_UIBC_GENERIC:
	    	retval = true;
			if (wifi_capability_option("nohidc")) {
				snprintf(body, 4096, "wfd_uibc_capability: input_category_list=GENERIC;generic_cap_list=%s;hidc_cap_list=none;port=none\r\n", gencap);
	    	}
	    	else if (wifi_capability_option("nogeneric")) {
				snprintf(body, 4096, "wfd_uibc_capability: input_category_list=HIDC;generic_cap_list=none;hidc_cap_list=%s;port=none\r\n", hidcap);
	    	}
	    	else {
				snprintf(body, 4096, "wfd_uibc_capability: input_category_list=GENERIC, HIDC;generic_cap_list=%s;hidc_cap_list=%s;port=none\r\n", gencap, hidcap);
	    	}
	    	snprintf(outbound, 4096, "SET_PARAMETER rtsp://localhost/wfd1.0 RTSP/1.0\r\nCSeq: %d\r\nContent-Type: text/parameters\r\nContent-Length: %d\r\n\r\n%s\r\n", ++CSequence, strlen(body), body);
	    	break;
		case WIFI_TRIGGER_ROUTEAUDIO:
	    	snprintf(outbound, 4096, "SET_PARAMETER rtsp://localhost/wfd1.0 RTSP/1.0\r\nCSeq: %d\r\nContent-Type: text/parameters\r\nContent-Length: 22\r\n\r\nwfd_route: secondary\r\n", ++CSequence);
	    	break;
        case WIFI_TRIGGER_STANDBY:
	    	snprintf(outbound, 4096, "SET_PARAMETER rtsp://localhost/wfd1.0 RTSP/1.0\r\nCSeq: %d\r\nContent-Type: text/parameters\r\nContent-Length: 13\r\n\r\nwfd_standby\r\n", ++CSequence);
	    	break;
        case WIFI_TRIGGER_IDR:
            snprintf(outbound, 4096, "SET_PARAMETER rtsp://localhost/wfd1.0 RTSP/1.0\r\nCSeq: %d\r\nContent-Type: text/parameters\r\nContent-Length: 17\r\n\r\nwfd_idr_request\r\n", ++CSequence);
	    	retval = true;
            break;
        case WIFI_TRIGGER_SETUP:
            snprintf(outbound, 4096, "SETUP %s RTSP/1.0\r\nCSeq: %d\r\nTransport: RTP/AVP/UDP;unicast;client_port=%d\r\n\r\n", src->presentation_url, ++CSequence, WIFI_RTP_DEFAULT_PORT);
            break;
        case WIFI_TRIGGER_PLAY:
	    	if (src->session_id) 
	            snprintf(outbound, 4096, "PLAY %s RTSP/1.0\r\nCSeq: %d\r\nSession: %s\r\n\r\n", src->presentation_url, ++CSequence, src->session_id);
	    	else 
				snprintf(outbound, 4096, "PLAY %s RTSP/1.0\r\nCSeq: %d\r\n\r\n", src->presentation_url, ++CSequence);
            break;
        case WIFI_TRIGGER_PAUSE:
	    	if (src->session_id)
				snprintf(outbound, 4096, "PAUSE %s RTSP/1.0\r\nCSeq: %d\r\nSession: %s\r\n\r\n", src->presentation_url, ++CSequence, src->session_id);
			else 
                snprintf(outbound, 4096, "PAUSE %s RTSP/1.0\r\nCSeq: %d\r\n\r\n", src->presentation_url, ++CSequence);
            break;
        case WIFI_TRIGGER_TEARDOWN:
	    	if (src->session_id)
				snprintf(outbound, 4096, "TEARDOWN %s RTSP/1.0\r\nCSeq: %d\r\nSession: %s\r\n\r\n", src->presentation_url, ++CSequence, src->session_id);
			else 
               snprintf(outbound, 4096, "TEARDOWN %s RTSP/1.0\r\nCSeq: %d\r\n\r\n", src->presentation_url, ++CSequence);
            break;
        default:
            return false;
    }

    //ALOGI(">>>>>>\n%s\n<<<<<<<\n", outbound);
    NP("tandy: [%s:%d]:%s(),outbound=%s,method=%d\n", __FILE__,__LINE__,__func__,outbound,method);
	if(!src->badSocket)
    write(src->srcnfd, outbound, strlen(outbound));
    
    char buffer[512];

wait_for_reply:
	int itimeout = 10;
	if (method == WIFI_TRIGGER_TEARDOWN) {
		NP("tandy: [%s:%d]:%s()\n", __FILE__,__LINE__,__func__);
		return true;		
	}
    if (!rtsp_parse_for_pattern(src->srcnfd, buffer, 512, "\r\n", 2, itimeout)) {
        ALOGI("Unable to parse response, force teardown\n"); // failed?
		//src->state = WIFI_SOURCE_TEARDOWN;
		//if (src->change_callback) 
		//	src->change_callback(old, WIFI_SOURCE_TEARDOWN);
        return false;
    }
    if (strstr(buffer, "200") == NULL) {
        ALOGI("response header is not 200, check for commands\n");
		if (strncasecmp(buffer, "GET_PARAMETER rtsp://localhost/wfd1.0", 37) == 0) {
			ALOGI("interleaved getparam detected \n");
			if (wifi_rtsp_cmd_getparameter(src, "localhost/wfd1.0"))
				goto wait_for_reply;
		}
		else if (strncasecmp(buffer, "SET_PARAMETER rtsp://localhost/wfd1.0", 30) == 0) {
			ALOGI("interleaved setparam detected \n");
			if (wifi_rtsp_cmd_setparameter(src, "localhost/wfd1.0"))
	    		goto wait_for_reply;
		}
		std::map<std::string, std::string, ltstr> headers;
		wifi_rtsp_header_parse(src->srcnfd, headers);
		if (!retval)
	    	return retval;
    }
    std::map<std::string, std::string, ltstr> headers;
    wifi_rtsp_header_parse(src->srcnfd, headers);
    if (method == WIFI_TRIGGER_SETUP) {
		std::string sess = headers["session"];
		const char * semi = strstr(sess.c_str(), ";");
		if (sess.length() > 0) {
	    	if (semi) 
				src->session_id = strndup(sess.c_str(), semi - sess.c_str() );
	    	else
	        	src->session_id = strdup(sess.c_str());
	    	int sfd = open(WFDSESSION_FILE, O_RDWR|O_TRUNC|O_CREAT, S_IRUSR|S_IWUSR|S_IRGRP|S_IWGRP|S_IROTH|S_IWOTH);
	      	if (sfd > 0) {
                write(sfd, src->session_id, strlen(src->session_id));
		   fchmod(sfd,0666);
				close(sfd);
            }			
	  }
        if (src->change_callback) {
        	NP("tandy: [%s:%d]:%s()\n", __FILE__,__LINE__,__func__);
	    	src->state = WIFI_SOURCE_SETUP;
            src->change_callback(old, WIFI_SOURCE_SETUP);
        }
        src->rtsp_pending_method = WIFI_TRIGGER_PLAY;
		return true;
	//return wifi_rtsp_cmd_trigger(src, WIFI_TRIGGER_PLAY);
    }
    if (method == WIFI_TRIGGER_PLAY) {
    	NP("tandy: [%s:%d]:%s()\n", __FILE__,__LINE__,__func__);
        src->state = WIFI_SOURCE_PLAY;
        if (src->change_callback) {
            src->change_callback(old, WIFI_SOURCE_PLAY);
        }
    }
    else if (method == WIFI_TRIGGER_TEARDOWN) {//we don't need this any more, trigger occure at wifi_rtsp.cpp, we clear source there!
            // a teardown is successful... do clean up here.
        //src->state = WIFI_SOURCE_TEARDOWN;        
        //if (src->change_callback) {
        //   src->change_callback(old, WIFI_SOURCE_TEARDOWN);
        //}
        return true; // for a rtsp session stop
    }
    else if (method == WIFI_TRIGGER_PAUSE) {
        src->state = WIFI_SOURCE_PAUSE;
        if (src->change_callback) {
            src->change_callback(old, WIFI_SOURCE_PAUSE);
        }
    }
    else if (method == WIFI_TRIGGER_STANDBY) {
	src->state = WIFI_SOURCE_STANDBY;
	if (src->change_callback) 
	    src->change_callback(old, WIFI_SOURCE_STANDBY);
    }
    return true;
}


bool wifi_rtsp_send_options(struct wifi_source *src) 
{
        // start M2 message
    std::string sbuf;
    WIFI_SOURCE_STATE old = src->state;
    char charbuf[1024];
    snprintf(charbuf, 1024, "OPTIONS * RTSP/1.0\r\nCSeq: %d\r\nRequire: org.wfa.wfd1.0\r\n\r\n", ++CSequence);
    sbuf.append(charbuf);

    //ALOGI(">>>>>>\n%s\n<<<<<<<\n", sbuf.c_str());
    NP("tandy: [%s:%d]:%s(),sbuf=\n%s\n", __FILE__,__LINE__,__func__,sbuf.c_str());
    write(src->srcnfd, sbuf.c_str(), sbuf.size());
    char buffer[512];
    if (!rtsp_parse_for_pattern(src->srcnfd, buffer, 512, "\r\n", 2, 6)) {
        ALOGI("Unable to parse response\n");
        return false;
    }
    if (strstr(buffer, "200") == NULL) {
        ALOGI("response header is not 200\n");
        return false;
    }
    
    
    std::map<std::string, std::string, ltstr> headers;
    wifi_rtsp_header_parse(src->srcnfd, headers);
    std::string methods = headers["public"];
    if (methods.find("SET_PARAMETER") == std::string::npos || 
        methods.find("GET_PARAMETER") == std::string::npos ||
        methods.find("SETUP") == std::string::npos ||
        methods.find("PLAY") == std::string::npos ||
        methods.find("PAUSE") == std::string::npos ||
        methods.find("TEARDOWN") == std::string::npos) {
        ALOGI("in complete methods list %s\n", methods.c_str());
        return false;
    }

    src->state = WIFI_SOURCE_INIT;
    if (src->change_callback) {
        src->change_callback(old, WIFI_SOURCE_INIT);
    }
    
    return true;
}


bool wifi_rtsp_cmd_setparameter(struct wifi_source *src, const char * options) 
{
    std::map<std::string, std::string, ltstr> headers;
    std::vector<std::string> content;
    std::string reply;
    WIFI_SOURCE_STATE old = src->state;
    int len;
    bool request_ok = true;
    bool trigger = false;
    WIFI_TRIGGER_METHOD tmethod = WIFI_TRIGGER_PLAY;;
    bool close_conn = true;  // close connection in case of error?
    char * err_msg = NULL;
    char body[4096];
    char errb[4096];

    wifi_rtsp_header_parse(src->srcnfd, headers);
    std::string cseq = "0";
    if (headers["cseq"] != "") 
        cseq = headers["cseq"];
    std::string alen = headers["content-length"];
    
    if (alen.length() <= 0)
        goto error_response;
    len = atoi(alen.c_str());
    if (!len) {
        goto error_response;
    }

    rtsp_payload_parse(src->srcnfd, len, content);
    for (unsigned int i=0; i<content.size(); i++) {
        std::string set = content[i];
		ALOGI("rtsp_payload_parse-- %s\n",set.c_str());
        if (strncmp(set.c_str(), "wfd_audio_codecs:", 17) == 0) {
            if (wifi_rtsp_parse_audiocodecs(src, set))
                continue;
            ALOGI("I cannot support audio codec %s\n", set.c_str());            
            goto error_response;
        }
        if (strncmp(set.c_str(), "wfd_video_formats:", 18) == 0) {
            if (wifi_rtsp_parse_videocodecs(src, set))
                continue;
            ALOGI("I cannot support video codec %s\n", set.c_str());
            snprintf(body, 4096, "wfd_video_formats: 415\r\n");
            snprintf(errb, 4096, "RTSP/1.0 303 See Other\r\nCSeq: %s\r\nContent-Type: text/parameters\r\nContent-Length: %d\r\n\r\n%s", cseq.c_str(),strlen(body),body);
            err_msg = errb;
            close_conn = false;
            goto error_response;
        }
        if (strncmp(set.c_str(), "wfd_3d_video_formats:", 21) == 0) {
            if (wifi_rtsp_parse_3dcodecs(src, set))
                continue;
            ALOGI("3d video format is not supported %s\n", set.c_str());
            goto error_response;
        }
        if (strncmp(set.c_str(), "wfd_presentation_URL:", 21) == 0) {
            const char * data = set.c_str();
            data += 22;
            int urllen = 0;
            while (*data != ' ' && *data != '\r' && *data != '\0') {
                urllen ++;
                data++;
            }
            
            std::string subr = set.substr(22, urllen);
            if (src->presentation_url) 
                free(src->presentation_url);
            src->presentation_url = strdup(subr.c_str()); 
            continue;
        }
        if (strncmp(set.c_str(), "wfd_standby", 11) == 0 && 
	        set.length() == 11) {
                // going into standby mode?
            src->state = WIFI_SOURCE_STANDBY;
            if (src->change_callback) {
                src->change_callback(old, WIFI_SOURCE_STANDBY);
            }
        }
        if (strncmp(set.c_str(), "wfd_trigger_method:", 19) == 0) {        	
            const char * data = set.c_str();
            data += 20;
            NP("tandy: [%s:%d:%s] wfd_trigger_method=%s \n",__FILE__,__LINE__,__func__, data);
            if (strncmp(data, "SETUP", 5) == 0) {
                trigger = true;
                tmethod = WIFI_TRIGGER_SETUP;
            }
            else if (strncmp(data, "PLAY", 4) == 0) {
                trigger = true;
                tmethod = WIFI_TRIGGER_PLAY;
            }
            else if (strncmp(data, "PAUSE", 5) == 0) {
                if (src->state == WIFI_SOURCE_STANDBY) {
                    update_cseq(cseq);
                    snprintf(errb, 4096, "RTSP/1.0 406 in-standby-mode\r\nCSeq: %s\r\n\r\n", cseq.c_str());
                    err_msg = errb;
                    close_conn = false;
                    goto error_response;
                }
                trigger = true;
                tmethod = WIFI_TRIGGER_PAUSE;
            }
            else if (strncmp(data, "TEARDOWN", 8) == 0) {
                trigger = true;
                tmethod = WIFI_TRIGGER_TEARDOWN;                
            }
            else 
                goto error_response;
            continue;
        }
		
        if (strncmp(set.c_str(), "wfd_uibc_capability:", 20) == 0) {
            if (wifi_rtsp_parse_uibc(src, set))
                continue;
            goto error_response;
        }
        if (strncmp(set.c_str(), "wfd_av_format_change_timing:", 28) == 0) {
            ALOGI("I don't support av_format_change_timing yet...\n");
            continue;
        }
        if (strncmp(set.c_str(), "wfd_client_rtp_ports:", 20) == 0) {
            ALOGI("I will ignore attempt to set rtp ports\n");
            continue;
        }
        if (strncmp(set.c_str(), "wfd_content_protection:", 22) == 0) {
           ALOGI("I will ignore attempt to set content protection\n");
           continue;
        }
        if (strncmp(set.c_str(), "wfd_coupled_sink:", 16) == 0) {
           ALOGI("I will ignore attempt to set couple sink\n");
           continue;
        }
        if (strncmp(set.c_str(), "wfd_prefered_display_mode:", 26) == 0) {
            if (wifi_rtsp_parse_preferred(src, set))
                continue;
            close_conn = false;  // don't close, let server give us more preferred options
            goto error_response;
        }
        if (strncmp(set.c_str(), "wfd_uibc_setting: ", 18) == 0) {
            if (strstr(set.c_str(), "disable") != NULL) {
                if (src->uibcfd > 0) {
                    close(src->uibcfd);
                    src->uibcfd = -1;
                }
                continue;
	          }

            ALOGI("uibc port is set to %d, ip=%s\n", src->uibcport, inet_ntoa(src->address.sin_addr));
            struct sockaddr_in out;
            int netfd = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
            if (netfd < 0) {	  	
                goto error_response;
            }
	  
            out.sin_port = htons(src->uibcport);
            out.sin_family = AF_INET;
            out.sin_addr.s_addr = src->address.sin_addr.s_addr;
	  
            if (connect (netfd, (struct sockaddr*) &out, sizeof(out)) < 0) {
                ALOGI("I am unable to connect to UIBC port\n");
                close_conn = false;
                goto error_response;
            }
            src->uibcfd = netfd;
            int nodelayflag = 1;
            setsockopt(netfd,            /* socket affected */
              IPPROTO_TCP,     /* set option at TCP level */
              TCP_NODELAY,     /* name of option */
              (char *) &nodelayflag,  /* the cast is historical*/
              sizeof(int));    /* length of option value */

            int dscp = 0xb8;    // dscp expedited forwarding
            setsockopt(netfd, IPPROTO_IP, IP_TOS, &dscp, sizeof(int));
            continue;
       }

		if (strncmp(set.c_str(), "intel_friendly_name:", 20) == 0) 
	 	{
	 		ALOGI(">>>>> Set Friendy name: %s \n", set.c_str() + 21);
			if (set.c_str() + 21 != NULL)
			{
				if (src->friendly_name) 
			    	free(src->friendly_name);
				src->friendly_name = strdup(set.c_str() + 21); 
				char cmd[128];
				sprintf(cmd, "echo %s > /usr/local/etc/wfdconfig/intel_friendly_name", src->friendly_name);
				ALOGI(">>>>>> cmd : %s", cmd);
				system(cmd);
				continue;
			}
			else
				goto error_response;
		
	 	}
	 	if (strncmp(set.c_str(), "intel_overscan_comp:", 20) == 0) 
	 	{
	 		ALOGI(">>>>> Set overscan_comp: %s \n", set.c_str() + 21);
			int x,y;
			sscanf(set.c_str() + 21, "x=%d, y=%d", &x, &y);
			if (x == y && x <= 15 && x >= 0)
			{
				src->overscan_comp = x;
				char cmd[128];
				sprintf(cmd, "echo %d > /tmp/wfd/intel_overscan_comp", src->overscan_comp);
				ALOGI(">>>>>> cmd : %s", cmd);
				system(cmd);
				continue;
			}	
			else
				goto error_response;
	 	}
            // none of the above, I don't handle this req yet
       ALOGI("I don't know how to handle %s\n", set.c_str());
        //request_ok = false;
    }

    if (request_ok) {
           char headers[4096];
           if (trigger && src->presentation_url==NULL) 
               goto error_response;
           update_cseq(cseq);
           snprintf(headers, 4096, "RTSP/1.0 200 OK\r\nCSeq: %s\r\n\r\n", cseq.c_str());
           //ALOGI(">>>>>>\n%s\n<<<<<<<\n", headers);
           NP("tandy: [%s:%d]:%s(),response headers:\n%s\n", __FILE__,__LINE__,__func__,headers);
           write(src->srcnfd, headers, strlen(headers));
    
           if (trigger) {
               src->rtsp_pending_method = tmethod;
            //return wifi_rtsp_cmd_trigger(src, tmethod);
           }
		   write_wifi_source_file(src); 			   
           return true;
    }

  error_response:
    update_cseq(cseq);
    if (err_msg == NULL){
        snprintf(errb, 4096, "RTSP/1.0 403 Forbidden\r\nCSeq: %s\r\n\r\n", cseq.c_str());
        //ALOGI(">>>>>>\n%s\n<<<<<<<\n", errb);
        NP("tandy: [%s:%d]:%s(),errb=%s\n", __FILE__,__LINE__,__func__,errb);
        write(src->srcnfd, errb, strlen(errb));
    }
    else {
        write(src->srcnfd, err_msg, strlen(err_msg));
    }
    
    if (close_conn)
        return false;
    return true;
}

// some well-known fixed system capabilities

#define WFD_AUDIO_CODECS "wfd_audio_codecs: LPCM 00000003 02, AAC 0000000f 02, AC3 00000007 02\r\n"
#define SIGMA_WFD_AUDIO_CODECS "wfd_audio_codecs: LPCM 00000003 02\r\n"


//#define WFD_VIDEO_FORMATS "wfd_video_formats: 38 00 01 08 000000ff 07ffffff 00000fff 02 0000 00ff 11 0780 0438\r\n"
//#define WFD_3D_FORMATS "wfd_3d_video_formats: 38 00 01 08 000060ef 02 0000 00ff 11 0780 0438\r\n"
#define WFD_3D_FORMATS "wfd_3d_video_formats: none\r\n"
#define WFD_PREFERRED_DISPLAY "wfd_preferred_display_mode: 004e20 0780 0000 0000 0000 0438 0000 0000 0000 00 00 00 01 08 0001deff 07ffffff 00000fff 02 0000 00ff 11 0780 0438\r\n"

static std::string wifi_rtsp_calc_audioformats()
{
    std::string reply;
    char line[1024];
    
    if (wifi_capability_option("lpcm")) {
        snprintf(line, 1024, "wfd_audio_codecs: LPCM %08x %02x\r\n",  SIGMA_AUDIO_LPCM, SIGMA_AUDIO_DELAY);
    }
    else {
        snprintf(line, 1024, "wfd_audio_codecs: LPCM %08x %02x, AAC %08x %02x, AC3 %08x %02x\r\n", AUDIO_LPCM, AUDIO_DELAY, AUDIO_AAC, AUDIO_DELAY, AUDIO_AC3, AUDIO_DELAY);
    }
    
    reply.append(WFD_AUDIO_CODECS);
    return reply;
}

static std::string wifi_rtsp_calc_3dformats() 
{
    std::string reply;
    char line[1024];
    unsigned short tvinfo;
    
    if (VIDEO_3D_SUPPORT == 0 || !system_getTVSupport3D(&tvinfo)) {
        snprintf(line, 1024, "wfd_3d_video_formats: none\r\n");
        reply.append(line);
        return reply;
    }
    char preferred = 0x01;
    char frame_control = VIDEO_FRAME_CONTROL;
    char maxres[512];
    
    if (wifi_capability_option("sigmaav")) {
        frame_control = SIGMA_VIDEO_FRAME_CONTROL;
    }
    
    if (wifi_capability_option("nopreferred")) {
        preferred = 0x00;
        snprintf(maxres, 512, "none none");
    }
    snprintf(line, 1024, "wfd_3d_video_formats: %02x %02x %02x %02x %16llx %02x %04x %04x %02x %s\r\n", NATIVE_MODE, preferred, H264_PROFILE, H264_LEVEL, VIDEO_3D_SUPPORT, VIDEO_LATENCY, MIN_SLICE_SIZE, SLICE_ENC_PARAMS, frame_control, maxres);
    reply.append(line);
    return reply;
}

static std::string wifi_rtsp_calc_vidformats() {
    unsigned int CEA=0, VESA=0, HH=0;
    FILE *fd = fopen(SIGMA_VIDEOLIST_FILE, "r");
    std::string reply;
    char line[1024];

    if (fd != NULL ) {
        if (fgets(line, 1024, fd) == NULL) {
	    goto default_video;
	}
	if (strstr(line, "CEA-16")) {
	  CEA |= CEA16;
	}
	if (strstr(line, "CEA-15")) {
	  CEA |= CEA15;
	}
	if (strstr(line, "CEA-14")) {
	  CEA |= CEA14;
	}
	
	if (strstr(line, "CEA-13")) {  // no 1080p50 for jupiter
	  CEA |= CEA13;
	}
	if (strstr(line, "CEA-12")) {
	  CEA |= CEA12;
	}
	if (strstr(line, "CEA-11")) {
	  CEA |= CEA11;
	}
	if (strstr(line, "CEA-10")) {
	  CEA |= CEA10;
	}
	if (strstr(line, "CEA-9")) {
	  CEA |= CEA9;
	}
	if (strstr(line, "CEA-8")) {  // no 1080p60 for jupiter
	  CEA |= CEA8;
	}
	if (strstr(line, "CEA-7")) {
	  CEA |= CEA7;
	}
	if (strstr(line, "CEA-6")) {
	  CEA |= CEA6;
	}
	if (strstr(line, "CEA-5")) {
	  CEA |= CEA5;
	}
	if (strstr(line, "CEA-4")) {
	  CEA |= CEA4;
	}
	if (strstr(line, "CEA-3")) {
	  CEA |= CEA3;
	}
	if (strstr(line, "CEA-2")) {
	  CEA |= CEA2;
	}
	if (strstr(line, "CEA-1 ")) {
	  CEA |= CEA1;
	}
	if (strstr(line, "CEA-0")) {
	  CEA |= CEA0;
	}

	if (strstr(line, "VESA-29")) {
	  VESA |= VESA29;
	}
	if (strstr(line, "VESA-28")) {
	  VESA |= VESA28;
	}
	if (strstr(line, "VESA-27")) {
	  VESA |= VESA27;
	}
	if (strstr(line, "VESA-26")) {
	  VESA |= VESA26;
	}
	if (strstr(line, "VESA-25")) {
	  VESA |= VESA25;
	}
	if (strstr(line, "VESA-24")) {
	  VESA |= VESA24;
	}
	if (strstr(line, "VESA-23")) {
	  VESA |= VESA23;
	}
	if (strstr(line, "VESA-22")) {
	  VESA |= VESA22;
	}
	if (strstr(line, "VESA-21")) {
	  VESA |= VESA21;
	}
	if (strstr(line, "VESA-20")) {
	  VESA |= VESA20;
	}
	if (strstr(line, "VESA-19")) {
	  VESA |= VESA19;
	}
	if (strstr(line, "VESA-18")) {
	  VESA |= VESA18;
	}
	if (strstr(line, "VESA-17")) {
	  VESA |= VESA17;
	}
	if (strstr(line, "VESA-16")) {
	  VESA |= VESA16;
	}
	if (strstr(line, "VESA-15")) {
	  VESA |= VESA15;
	}
	if (strstr(line, "VESA-14")) {
	  VESA |= VESA14;
	}
	if (strstr(line, "VESA-13")) {
	  VESA |= VESA13;
	}
	if (strstr(line, "VESA-12")) {
	  VESA |= VESA12;
	}
	if (strstr(line, "VESA-11")) {
	  VESA |= VESA11;
	}
	if (strstr(line, "VESA-10")) {
	  VESA |= VESA10;
	}
	if (strstr(line, "VESA-9")) {
	  VESA |= VESA9;
	}
	if (strstr(line, "VESA-8")) {
	  VESA |= VESA8;
	}
	if (strstr(line, "VESA-7")) {
	  VESA |= VESA7;
	}
	if (strstr(line, "VESA-6")) {
	  VESA |= VESA6;
	}
	if (strstr(line, "VESA-5")) {
	  VESA |= VESA5;
	}
	if (strstr(line, "VESA-4")) {
	  VESA |= VESA4;
	}
	if (strstr(line, "VESA-3")) {
	  VESA |= VESA3;
	}
	if (strstr(line, "VESA-2 ")) {
	  VESA |= VESA2;
	}
	if (strstr(line, "VESA-1 ")) {
	  VESA |= VESA1;
	}
	if (strstr(line, "VESA-0")) {
	  VESA |= VESA0;
	}
    
	if (strstr(line, "HH-9")) {
	  HH |= HH9;
	}
	if (strstr(line, "HH-8")) {
	  HH |= HH8;
	}
	if (strstr(line, "HH-7")) {
	  HH |= HH7;
	}
	if (strstr(line, "HH-6")) {
	  HH |= HH6;
	}
	if (strstr(line, "HH-5")) {
	  HH |= HH5;
	}
	if (strstr(line, "HH-4")) {
	  HH |= HH4;
	}
	if (strstr(line, "HH-3")) {
	  HH |= HH3;
	}
	if (strstr(line, "HH-2")) {
	  HH |= HH2;
	}
	if (strstr(line, "HH-1")) {
	  HH |= HH1;
	}
	if (strstr(line, "HH-0")) {
	  HH |= HH0;
	}
	fclose(fd);

        char preferred = 0x01;
        char frame_control = VIDEO_FRAME_CONTROL;
        char maxres[512];
        
        if (wifi_capability_option("sigmaav")) {
            frame_control = SIGMA_VIDEO_FRAME_CONTROL;
        }

        if (wifi_capability_option("nopreferred")) {
            preferred = 0x00;
            snprintf(maxres, 512, "none none");
        }
        else {
            snprintf(maxres, 512, "%04x %04x", MAX_H_RESOLUTION, MAX_V_RESOLUTION);
        }
        
        
        if (wifi_capability_option("nofskip")) {
            frame_control = frame_control & ~(FRAME_SKIP_SUPPORT);
            frame_control = frame_control & ~(MAX_SKIP_MASK);
        }
        
        if (wifi_capability_option("noavch")) {
            frame_control = frame_control & ~(FRAME_RATE_SUPPORT);
        }
        
        snprintf(line, 1024, "wfd_video_formats: %02x %02x %02x %02x %08x %08x %08x %02x %04x %04x %02x %s\r\n", NATIVE_MODE, preferred, H264_PROFILE, H264_LEVEL, CEA, VESA, HH, VIDEO_LATENCY, MIN_SLICE_SIZE, SLICE_ENC_PARAMS, frame_control, maxres);
        reply.append(line);
	return reply;
    }

 default_video:
    char preferred = 0x01;
    char frame_control = VIDEO_FRAME_CONTROL;
    char maxres[512];
    CEA=CEA_SUPPORT; VESA=VESA_SUPPORT; HH=HH_SUPPORT;

    if (wifi_capability_option("sigmaav")) {
        frame_control = SIGMA_VIDEO_FRAME_CONTROL;
    }

    if (wifi_capability_option("nopreferred")) {
        preferred = 0x00;
        snprintf(maxres, 512, "none none");
    }
    else {
        snprintf(maxres, 512, "%04x %04x", MAX_H_RESOLUTION, MAX_V_RESOLUTION);
    }
    
    
    if (wifi_capability_option("nofskip")) {
        frame_control = frame_control & ~(FRAME_SKIP_SUPPORT);
        frame_control = frame_control & ~(MAX_SKIP_MASK);
    }
    
    if (wifi_capability_option("noavch")) {
        frame_control = frame_control & ~(FRAME_RATE_SUPPORT);
    }
     
    if (wifi_capability_option("manvideo")) {
        CEA = SIGMA_MANDATORY_CEA;
        VESA = SIGMA_MANDATORY_VESA;
        HH = SIGMA_MANDATORY_HH;
    }
    
    snprintf(line, 1024, "wfd_video_formats: %02x %02x %02x %02x %08x %08x %08x %02x %04x %04x %02x %s\r\n", NATIVE_MODE, preferred, H264_PROFILE, H264_LEVEL, CEA, VESA, HH, VIDEO_LATENCY, MIN_SLICE_SIZE, SLICE_ENC_PARAMS, frame_control, maxres);
    reply.append(line);

    return reply;
}


#ifdef __cplusplus
extern "C" {
#endif
int hdcp22_GetProtocolDescriptorBit();
#ifdef __cplusplus
}
#endif

bool wifi_rtsp_cmd_getparameter(struct wifi_source * src, const char * options)
{
    std::map<std::string, std::string, ltstr> headers;
    std::vector<std::string> content;
    std::string reply;
    int len;
    static bool bReadWiDiConfig=false;
    static adapter_config_t aconfig;
    FILE *fin=NULL;

    if (bReadWiDiConfig == false)
    {
		memset(&aconfig, 0x00, sizeof(adapter_config_t));
    	fin = fopen( DEFAULT_CONFIG_PATH, "r" );
    	if ( fin == NULL ) {
        	ALOGI( "unable to open config file %s", DEFAULT_CONFIG_PATH);
        	bReadWiDiConfig = false;
    	}
	else
	{
		ALOGI("open widi config file : %s\n",DEFAULT_CONFIG_PATH);
		config_parse_adapter(fin, &aconfig);
		if((strlen(aconfig.manufacturer_name)==0)||(strlen(aconfig.software_version)==0)||
		  (strlen(aconfig.hardware_version)==0)||(strlen(aconfig.model_name)==0))
		{
			bReadWiDiConfig = false;
		}
		else
			bReadWiDiConfig = true;
		fclose(fin);
    	}
    }
    
    wifi_rtsp_header_parse(src->srcnfd, headers);
    std::string cseq = "0";
    if (headers["cseq"] != "") 
        cseq = headers["cseq"];
    std::string alen = headers["content-length"];
    if (alen.length() <= 0){

        char headers[4096];
        update_cseq(cseq);
        snprintf(headers, 4096, "RTSP/1.0 200 OK\r\nCSeq: %s\r\n\r\n", cseq.c_str());

        //ALOGI(">>>>>>\n%s\n<<<<<<<\n", headers);
        NP("tandy: [%s:%d]:%s(),headers=%s\n", __FILE__,__LINE__,__func__,headers);
        write(src->srcnfd, headers, strlen(headers));
        return true;
    }
    len = atoi(alen.c_str());
    if (len <= 0) {
        char headers[4096];
        update_cseq(cseq);
        snprintf(headers, 4096, "RTSP/1.0 200 OK\r\nCSeq: %s\r\n\r\n", cseq.c_str());

        //ALOGI(">>>>>>\n%s\n<<<<<<<\n", headers);
        NP("tandy: [%s:%d]:%s(),headers=%s\n", __FILE__,__LINE__,__func__,headers);
        write(src->srcnfd, headers, strlen(headers));
        return true;
    }
    ALOGI("we are in get_parameter, %d\n", len);
    rtsp_payload_parse(src->srcnfd, len, content);
    for (unsigned int i=0; i<content.size(); i++) {
        std::string ask = content[i];
        if (ask == "wfd_audio_codecs") {
            reply.append(wifi_rtsp_calc_audioformats());
            continue;
        }
        if (ask == "wfd_video_formats") {
	  reply.append(wifi_rtsp_calc_vidformats());
            continue;
        }
	if (ask == "wfd_preferred_display_mode") {
	    reply.append(WFD_PREFERRED_DISPLAY);
	}
	if (ask == "wfd_standby_resume_capability") {
	    if (!wifi_capability_option("nostandby"))
	      reply.append("wfd_standby_resume_capability: supported\r\n");
	    else
	      reply.append("wfd_standby_resume_capability: none\r\n");
        }
        if (ask == "wfd_3d_video_formats") {
            reply.append(wifi_rtsp_calc_3dformats());
            continue;
        }
        if (ask == "wfd_presentation_URL") {
            char buf[512];
            snprintf(buf, 512, "rtsp://%s/wfd1.0/streamid=0", inet_ntoa(src->address.sin_addr));
            reply.append(buf);
            continue;
        }
        if (ask == "wfd_display_edid") {
            FILE * edidfile;
            char edir_buf[HDMI_EDIR_SIZE];
	    if (!wifi_capability_option("noedid")) {
	        edidfile = fopen ( HDMI_EDID_PATH , "r" );
		if (edidfile == NULL) {
		    reply.append("wfd_display_edid: 0000 none\r\n");
		    continue;
		}

		int retSize = fread (edir_buf, 1, HDMI_EDIR_SIZE, edidfile);
		if (retSize != 128 && retSize != 256) {
		    ALOGI("reject strange EDID size %d...\n", retSize);
		    reply.append("wfd_display_edid: 0000 none\r\n");
		    fclose(edidfile);
		    continue;
		}
            
		if (retSize == 128)
		    reply.append("wfd_display_edid: 0001 ");
		else
		    reply.append("wfd_display_edid: 0002 ");

		for (int k=0; k<retSize; k++) {
		  char hexbuf[4];
		  snprintf(hexbuf, 4, "%02x", (unsigned char) edir_buf[k]);
		  reply.append(hexbuf);
		}
		reply.append("\r\n");
		fclose(edidfile);
            }
	    else {
	        reply.append("wfd_display_edid: none\r\n");
	    }
            continue;
        }
	if (ask == "wfd_content_protection") {
	    if (src->hdcp2_listenport > 0) {
	        if (!wifi_capability_option("nohdcp"))
	        {
				
			    #if (HDCP_2X_VSN == 22)

	        	char hdcp_vsn[8];
				memset(hdcp_vsn,0x0,8);
				snprintf(hdcp_vsn,8,"2.1");
#ifdef DOWNGRADE_HDCP22_WHEN_NO_PROTOCOL_DESCRIPTOR

				if(hdcp22_GetProtocolDescriptorBit()!=0x01)
				{
						DPRINT(("\033[0;31;31m[DHDCP] down-grade HDCP version to 2.1 cause no protocol descriptor bit is found [%s %d].\033[m\n",
										__FILE__,
										__LINE__ ));
						memset(hdcp_vsn,0x0,8);
						snprintf(hdcp_vsn,8,"2.1");
				}
#endif
                char msgBuf[128];
                memset(msgBuf,0x0,128);
                snprintf(msgBuf,128,"wfd_content_protection: HDCP%s port=25030\r\n",hdcp_vsn);
                reply.append(msgBuf);
                ALOGI("(%d) wfd_content_protection: HDCP%s port=25030!\n",__LINE__,hdcp_vsn);
		        #elif (HDCP_2X_VSN == 21)
			    ALOGI("(%d) hdcp wfd_content_protection: HDCP2.1 port=25030!\n",__LINE__);
		        reply.append("wfd_content_protection: HDCP2.1 port=25030\r\n");
			    #else
                ALOGI("hdcp wfd_content_protection: HDCP2.0 port=25030!\n");
		        reply.append("wfd_content_protection: HDCP2.0 port=25030\r\n");
			    #endif
				
				 
				
		    }
		    else
		    {
		        ALOGI("wfd_content_protection: non!\n");
                reply.append("wfd_content_protection: none\r\n"); // for sigma
            }
	  }else
	    reply.append("wfd_content_protection: none\r\n");
	    
	    continue;
	}
        if (ask == "wfd_coupled_sink") {
                // no coupled capability yet...
            reply.append("wfd_coupled_sink: none\r\n");
            continue;
        }
        if (ask == "wfd_I2C") {
	    char temp[256];
	    if (wifi_capability_option("noi2c") || src->i2c_listenport == -1)
	      snprintf(temp, 256, "wfd_I2C: none\r\n");
	    else 
	      snprintf(temp, 256, "wfd_I2C: %d\r\n", WIFI_I2C_DEFAULT_PORT);
            reply.append(temp);
            continue;
        }
        if (ask == "wfd_client_rtp_ports") {
            reply.append("wfd_client_rtp_ports: RTP/AVP/UDP;unicast 24030 0 mode=play\r\n");
        }
	//widi3.5
        if (ask == "intel_sink_manufacturer_name") {
		    char buf[512];
		    memset(buf,0x00,512);
            snprintf(buf, 512, "intel_sink_manufacturer_name: %s\r\n",/*bReadWiDiConfig?aconfig.manufacturer_name:*/DEFAULT_MANUFACTURER);
            reply.append(buf);
        }
        if (ask == "intel_sink_model_name") {
		    char buf[512];
		    memset(buf,0x00,512);
            snprintf(buf, 512, "intel_sink_model_name: %s\r\n",bReadWiDiConfig?aconfig.model_name:DEFAULT_MODEL_NAME);
            reply.append(buf);
        }
        if (ask == "intel_sink_version") {
		    char buf[2048];
		    memset(buf,0x00,2048);
            snprintf(buf, 2048, "intel_sink_version: product_ID=%s, hw_version=%s, sw_version=%s\r\n",bReadWiDiConfig?aconfig.product_id:DEFAULT_PRODUCT_ID,bReadWiDiConfig?aconfig.hardware_version:DEFAULT_HARDWARE_VERSION,bReadWiDiConfig?aconfig.software_version:DEFAULT_SOFTWARE_VERSION);
            reply.append(buf);
        }
        if (ask == "wfd_uibc_capability") {
	    if (wifi_capability_option("nouibc")) {
		reply.append("wfd_uibc_capability: none\r\n");
	    }
	    else {
		char uibcrep[1024];
		const char * gencap = "Keyboard, Mouse, MultiTouch";
		const char * hidcap = "Keyboard/USB, Mouse/USB";
		if (wifi_capability_option("nokeyboard")) {
		   gencap = "Mouse";
		   hidcap = "Mouse/USB";
		}
		else if (wifi_capability_option("nomouse")) {
		   gencap = "Keyboard";
		   hidcap = "Keyboard/USB";
		}
		
	        if (wifi_capability_option("nohidc")) {
		    snprintf(uibcrep, 1024, "wfd_uibc_capability: input_category_list=GENERIC;generic_cap_list=%s;hidc_cap_list=none;port=none\r\n", gencap);
		}
		else if (wifi_capability_option("nogeneric")) {
		    snprintf(uibcrep, 1024, "wfd_uibc_capability: input_category_list=HIDC;generic_cap_list=none;hidc_cap_list=%s;port=none\r\n", hidcap);
		}
		else {
		    snprintf(uibcrep, 1024, "wfd_uibc_capability: input_category_list=GENERIC, HIDC;generic_cap_list=%s;hidc_cap_list=%s;port=none\r\n", gencap, hidcap);
		}
		reply.append(uibcrep);
	    }
        }
        if (ask == "wfd_connector_type") {
            reply.append("wfd_connector_type: 07\r\n");
        }
    }
    if (reply.length() > 0) {
        char headers[4096];

        update_cseq(cseq);
        snprintf(headers, 4096, "RTSP/1.0 200 OK\r\nCSeq: %s\r\nContent-Type: text/parameters\r\nContent-Length: %d\r\n\r\n%s", cseq.c_str(), reply.length(), reply.c_str());
	
	//ALOGI(">>>>>>\n%s\n<<<<<<<\n", headers);
	NP("tandy: [%s:%d]:%s(),response headers:\n%s\n", __FILE__,__LINE__,__func__,headers);
	//ALOGI(">>>>>>\n%s\n<<<<<<<\n", reply.c_str());
        write(src->srcnfd, headers, strlen(headers));
        //write(src->srcnfd, reply.c_str(), reply.length());
        return true;
    }
    else {
        char headers[4096];
        update_cseq(cseq);
        snprintf(headers, 4096, "RTSP/1.0 200 OK\r\nCSeq: %s\r\n\r\n", cseq.c_str());

        //ALOGI(">>>>>>\n%s\n<<<<<<<\n", headers);
        NP("tandy: [%s:%d]:%s(),response headers:\n%s\n", __FILE__,__LINE__,__func__,headers);
        //ALOGI(">>>>>>\n%s\n<<<<<<<\n", reply.c_str());
        write(src->srcnfd, headers, strlen(headers));
        //write(src->srcnfd, reply.c_str(), reply.length());
        return true;
    }
}

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/ioctl.h>
#include <sys/time.h>
#include <fcntl.h>
#include <assert.h>

#include "rcp_api.h"
#include "hdcp2_interface.h"
#include "hdcp2_messages.h"
#include "wifi_rtsp.h"
#include "wifi_rtsp_cmds.h"
#include "wifi_hdcp2.h"
#include "deliver_info_to_dvdplayer.h"

#define LOG_TAG "WIFI_HDCP"
#include <utils/Log.h>
#if 0
#define NP(args...)	
#else
#define NP(args...)	ALOGI(args);
#endif

/**
 * HDCP2 message sizes, used to verify that a full message has been received
 */
#define AKE_INIT_MSG_SIZE (64/8 + 1)
#define AKE_NO_STORED_EKM_SIZE (1024/8 + 1)
#define AKE_STORED_EKM_SIZE (128/8+128/8 + 1)
#define AKE_TRANSMITTER_INFO_SIZE	(5 + 1)
#define AKE_RECEIVER_INFO_SIZE	(5 + 1)
#define LC_INIT_SIZE (64/8 + 1)
#define SKE_SEND_EKS_SIZE (128/8 + 64/8 + 1)
#define REP_AUTH_SEND_ACK_SIZE (128/8 + 1)
#define REP_REP_AUTH_READY (256/8 + 1)
#define REP_REP_AUTH_STREAM_MANAGE ( 5 + 1)
#define AKE_SEND_CERT_SIZE (4176/8 + 2 )
#define AKE_SEND_RRX_SIZE (64/8 + 1 )
#define AKE_SEND_H_PRIME_SIZE ( 256/8 + 1 )
#define AKE_SEND_PAIRING_INFO_SIZE ( 128/8 + 1 )
#define AKE_SEND_L_PRIME_SIZE ( 256/8 + 1 )
#define AKE_SEND_MOST_L_PRIME_SIZE ( 256/8/2 + 1 )
#define RTT_READY_SIZE ( 1 )
#define RTT_CHALLENGE_SIZE ( 128/8 + 1 )
#define REP_RECEIVER_AUTH_STATUS (3 + 1)

#define HDCP_NEGO_MAX_TIME               3
static time_t hdcp_init_time  = 0;

struct timeval tv_Start_200MS;
struct timeval tv_End_200MS;
/* 
 * initialize the hdcp2 component
 */
int wifi_hdcp2_init(void) 
{
    int servSock;                    /* Socket descriptor for server */
    struct sockaddr_in echoServAddr; /* Local address */
    unsigned short echoServPort;     /* Server port */

	ALOGI("before cpu_spu_sendmsg\n");
    if (cpu_spu_sendmsg(CID_INIT, NULL, 0) != 0) {
	ALOGI("SPU INIT FAIL!!!!!\n");
        return -1;
    }
	ALOGI("after cpu_spu_sendmsg\n");
    hdcp_init_time = 0;
    echoServPort = WIFI_HDCP2_DEFAULT_PORT;

    /* Create socket for incoming connections */
    if ((servSock = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP)) < 0)
        return -1;
      
    /* Construct local address structure */
    memset(&echoServAddr, 0, sizeof(echoServAddr));   /* Zero out structure */
    echoServAddr.sin_family = AF_INET;                /* Internet address family */
    echoServAddr.sin_addr.s_addr = htonl(INADDR_ANY); /* Any incoming interface */
    echoServAddr.sin_port = htons(echoServPort);      /* Local port */

    int optval = 1;
    setsockopt(servSock, SOL_SOCKET, SO_REUSEADDR,
               (const void *)&optval , sizeof(int));


    /* Bind to the local address */
    if (bind(servSock, (struct sockaddr *) &echoServAddr, sizeof(echoServAddr)) < 0){
        close(servSock);
        return -1;
    }

    /* Mark the socket so it will listen for incoming connections */
    if (listen(servSock, 3) < 0) {
    	NP("tandylo: [%s:%d:%s]\n",__FILE__,__LINE__,__func__);
        close(servSock);
        return -1;
    }
    return servSock;
}

void SaveStringInFile(const char *filename, unsigned char* str, int len)
{
#ifndef PKT_DEBUG
		return;
#endif
	FILE *fdf=NULL;
	fdf = fopen(filename, "w+");
	if (fdf == 0)
			ALOGI("[LY] open file %s error\n", filename);
	fwrite(str, 1, len, fdf);
	fclose(fdf);
}

static void wifi_hdcp2_sendmsg(int nfd, unsigned char *msg) 
{
    unsigned char msgid = msg[0];
	int bytesToSend = 0;
    struct timeval tv;
    int i;
    gettimeofday(&tv, NULL);
    switch( msgid )
    {
      case AKE_SEND_CERT:
         ALOGI("#send ake_send_certification at (%ld, %ld)\n", tv.tv_sec, tv.tv_usec);
         bytesToSend = AKE_SEND_CERT_SIZE;
         break;
      case AKE_SEND_RRX:
         ALOGI("#send ake_send_rrx at (%ld, %ld)\n", tv.tv_sec, tv.tv_usec);
         bytesToSend = AKE_SEND_RRX_SIZE;
         break;
      case AKE_SEND_H_PRIME:
         ALOGI("#send ake_send_h' at (%ld, %ld)\n", tv.tv_sec, tv.tv_usec);
         bytesToSend = AKE_SEND_H_PRIME_SIZE;
         break;
      case AKE_SEND_PAIRING_INFO:
         ALOGI("#send ake_pairing_info at (%ld, %ld)\n", tv.tv_sec, tv.tv_usec);
         bytesToSend = AKE_SEND_PAIRING_INFO_SIZE;
         break;
      case LC_SEND_L_PRIME:
      {  
        
            if (((getPreComputation()&0x01) == H2_LC_INIT_PRECOMPUTATION) && (IsCapHDCP2Plus()))
		    {
	         	ALOGI("#send most 128bits of lc_send_l' at (%ld, %ld)\n", tv.tv_sec, tv.tv_usec);
	         	bytesToSend = AKE_SEND_MOST_L_PRIME_SIZE;
		  	 }
			 	 else  //not pre-computation
			   {
				  	ALOGI("#send lc_send_l' at (%ld, %ld)\n", tv.tv_sec, tv.tv_usec);
						bytesToSend = AKE_SEND_L_PRIME_SIZE;
			 	 }         
       }
       break;
	    case RTT_READY:
				 ALOGI("#send RTT Ready at (%ld, %ld)\n", tv.tv_sec, tv.tv_usec);
			   bytesToSend = RTT_READY_SIZE;
		  	 break;
		  case AKE_RECEIVER_INFO:
			 	 ALOGI("#send receiver info' at (%ld, %ld)\n", tv.tv_sec, tv.tv_usec);
			 	 bytesToSend = AKE_RECEIVER_INFO_SIZE;
			 	 break;
	  case RECEIVER_AUTH_STATUS:
				 ALOGI("#send receiver auth status' at (%ld, %ld)\n", tv.tv_sec, tv.tv_usec);
			 	 bytesToSend = REP_RECEIVER_AUTH_STATUS;
			 	 break;
	  case REP_AUTH_STREAM_READY:
		  ALOGI("#send repeater auth stream ready' at (%ld, %ld)\n", tv.tv_sec, tv.tv_usec);
		  bytesToSend = REP_REP_AUTH_READY;
		  SaveStringInFile("/tmp/_Rep_Auth_Stream_Ready", msg, bytesToSend);
		  break;	  		
      case REP_AUTH_SEND_RXID_LIST:
         ALOGI("->REP_AUTH_SEND_RXID_LIST(%ld, %ld)\n", tv.tv_sec, tv.tv_usec);
         /**
          * REP_AUTH_SEND_RXID_LIST Contains:
          * msgID
          * Max Devs Exceeded ( 1 byte )
          * Max Cascade Exceeded ( 1 byte )
          * if ( max devs exceeded = 0 and max cascade = 0 )
          * Device Count (1 byte )
          * Depth        (1 byte )
          * Vprime       ( V_SIZE = 32 bytes )
          * ReceiverIds, ( 5 bytes each ) * DEVICECOUNT
          *
          * This is equal to V_SIZE + 5 + 5*DEVICECOUNT bytes total.
          */
         if ( ( msg[1] == 1 )  ||  /* Max devs exceeded */
               ( msg[2] == 1 ))     /* Max cascade exceeded */
         {
            bytesToSend = 3;
         }
         else
         {
         	if (IsCapHDCP2Plus())
         	{
            	bytesToSend = V_SIZE/2+3+7+msg[3]*5;
				SaveStringInFile("/tmp/_ReceiverIdListPack2", msg, bytesToSend);
         	}
			else
            	bytesToSend = V_SIZE+5 + msg[3]*5; 
         }
		 ALOGI("[LY] rxid_list out:%d\n", bytesToSend);
         break;

      default:
         ALOGI("->Unknown message from spu! [msgid = %d]\n", msgid);
         return;
    }   
    write(nfd, msg, bytesToSend);
}

int wifi_hdcp2_status(SOCKET_CW_OFFSET* aeskey, SOCKET_CW_OFFSET * aesiv) {
    time_t now;
    time(&now);
    if (hdcp_init_time == 0) 
	return HDCP2_NEGO_STATUS_NONE;

    if (spu_nego_status(aeskey, aesiv))
	return HDCP2_NEGO_STATUS_DONE;

    if ((now - hdcp_init_time) < HDCP_NEGO_MAX_TIME)
	return HDCP2_NEGO_STATUS_STARTED;

    return HDCP2_NEGO_STATUS_TIMEDOUT;
}

bool wifi_hdcp2_process_msg (int nfd) 
{
    static unsigned char msg[MAX_PACKED_MSG_SIZE];
    int bytes = read(nfd, msg, 1);
    ALOGI("[hdcp] kelly  read byes: (%ld)\n", bytes);
    int msgSize = 0;
    struct timeval tv;
	//cancel error handling at hdcp2.1 repeater
	#if 0
	if ((IsCapHDCP2PlusWithRepeater())&&(RXID_LIST_200MS_TIMEOUT_STATE == RXID_200MS_TIMEOUT_INIT))
	{
		int sec=0, usec=0, msec=0;
		gettimeofday(&tv_End_200MS, NULL);
		if (tv_End_200MS.tv_usec < tv_Start_200MS.tv_usec)
		{
			if (tv_Start_200MS.tv_sec >= tv_End_200MS.tv_sec)
				ALOGI("[LY] error sec time\n");
			usec = (1000 - (tv_Start_200MS.tv_usec/1000)+(tv_End_200MS.tv_usec/1000))*1000;
			sec = tv_End_200MS.tv_sec - tv_Start_200MS.tv_sec -1;
		}
		else
		{
			sec = tv_End_200MS.tv_sec - tv_Start_200MS.tv_sec;
			usec = tv_End_200MS.tv_usec - tv_Start_200MS.tv_usec;
		}
		ALOGI("[LY] rxid 200ms dect (%ld:%ld)\n", sec, usec);

		if (((sec * 1000) + (usec / 1000)) > 1000) //1sec timeout
		{
			//send Receiver_AuthStatus to transimitter
			msg[0] = RECEIVER_AUTH_STATUS;
			msg[1] = 0x00;
			msg[2] = 0x04;
			msg[3] = 0x01;  //tell transmitter that we don't see ack
			wifi_hdcp2_sendmsg(nfd, msg);
			RXID_LIST_200MS_TIMEOUT_STATE = RXID_200MS_TIMEOUT_UNDEF;
			ALOGI("[LY] rxid 200ms timeout !!(%ld:%ld),(%ld,%ld)\n",tv_Start_200MS.tv_sec, tv_End_200MS.tv_sec,tv_Start_200MS.tv_usec, tv_End_200MS.tv_usec);
		}
	}
	#endif
	
    if (bytes < 1) 
        return false; //kelly pf2 update
	
    gettimeofday(&tv, NULL);
    switch( msg[0] )
      {
         case AKE_INIT:
		 	//hdcp2_init();
		 	init_rtk_hdcp(); //setHDCPCAP(CAP_HDCP2_0); //gHDCP = CAP_HDCP2_0; //init state
            
			ALOGI("rcv ake_init at (%ld, %ld)\n", tv.tv_sec, tv.tv_usec);
	    if (hdcp_init_time == 0) 
		time(&hdcp_init_time);
            msgSize = AKE_INIT_MSG_SIZE;
            break;
	 case AKE_TRANSMITTER_INFO:
	    ALOGI("rcv ake_transmitter_info at (%ld, %ld)\n", tv.tv_sec, tv.tv_usec);
	    msgSize = AKE_TRANSMITTER_INFO_SIZE;
	    break;
         case AKE_NO_STORED_EKM:
            ALOGI("rcv ake_no_stored_ekm at (%ld, %ld)\n", tv.tv_sec, tv.tv_usec);
            msgSize = AKE_NO_STORED_EKM_SIZE;
            break;
         case AKE_STORED_EKM:
            ALOGI("rcv ake_stored_ekm at (%ld, %ld)\n", tv.tv_sec, tv.tv_usec);
            msgSize = AKE_STORED_EKM_SIZE;
            break;
         case LC_INIT:
            ALOGI("rcv lc_init (%ld, %ld)\n", tv.tv_sec, tv.tv_usec);
            msgSize = LC_INIT_SIZE;
            break;    
			   case RTT_CHALLENGE:
			    	ALOGI("rcv rtt_challenge (%ld, %ld)\n", tv.tv_sec, tv.tv_usec);
			    	msgSize = RTT_CHALLENGE_SIZE;
			    	break;            
         case SKE_SEND_EKS:
            ALOGI("rcv ske_send_eks at (%ld, %ld)\n", tv.tv_sec, tv.tv_usec);
            msgSize = SKE_SEND_EKS_SIZE;
            break;
		 case REP_AUTH_SEND_ACK:
		 	ALOGI("rcv rep_auth_send_ack at (%ld, %ld)\n", tv.tv_sec, tv.tv_usec);
            msgSize = REP_AUTH_SEND_ACK_SIZE;
		 	break;
		 case REP_AUTH_STREAM_MANAGE:
		    ALOGI("rcv rep_auth_stream_manage at (%ld, %ld)\n", tv.tv_sec, tv.tv_usec);
		    msgSize = REP_REP_AUTH_STREAM_MANAGE;
		    break;	
         default:
            ALOGI("<-Invalid msg %d received. Skipping\n", msg[0]);
            msgSize = 0;
            return true;
      }

	if (msg[0] == REP_AUTH_STREAM_MANAGE)
	{	//read k , 2bytes
		int k=0;
		bytes = read(nfd, msg+1, 5);
		k = B4TL(0x00, 0x00, msg[4], msg[5]);
		msgSize = msgSize+(k*7);
		bytes = read(nfd, msg+1+5, k*7);
		ALOGI("[LY] total stream manage read length:%d\n", msgSize);
	}
	else
		bytes = read(nfd, msg+1, msgSize-1);

    int result = cpu_spu_sendmsg(CID_MESSAGE_SEND, msg, sizeof(msg));

    if (result == STATUS_OK_PENDING_MSG ) {
        wifi_hdcp2_sendmsg(nfd, msg);

	//cancel error handling at hdcp2.1 repeater
	#if 0
	if ((IsCapHDCP2PlusWithRepeater())&&(msg[0] == REP_AUTH_SEND_RXID_LIST) && (RXID_LIST_200MS_TIMEOUT_STATE == RXID_200MS_TIMEOUT_UNDEF))
	{
		gettimeofday(&tv_Start_200MS, NULL);
		gettimeofday(&tv_End_200MS, NULL);
		RXID_LIST_200MS_TIMEOUT_STATE = RXID_200MS_TIMEOUT_INIT;
		ALOGI("[LY] detect rxid 200ms start(%ld:%ld),(%ld,%ld)\n",tv_Start_200MS.tv_sec, tv_End_200MS.tv_sec,tv_Start_200MS.tv_usec, tv_End_200MS.tv_usec);
	}
	#endif
         /**
          * If MSGID is AKE_SEND_RRX, poll for the possible followup messages:
          * AKE_SEND_HPRIME and AKE_SEND_PAIRING_INFO
          */
        if ( msg[0] == AKE_SEND_RRX )
        {
            while( 1 )
            {
               ALOGI("Polling for message to send\n");
               int size = sizeof(msg);
               result = cpu_spu_rcvmsg(msg, &size);
               
               if ( result == STATUS_OK_PENDING_MSG )
               {
                   wifi_hdcp2_sendmsg(nfd, msg);
               }
               else
               {
                  ALOGI("No message to send\n");
                  break;
               }
            }
         }
    }
    if (spu_nego_status(NULL, NULL)) {
        ALOGI("HDCP2 NEGO SUCCESS, SET KEY\n");
		write_hdcp2_info_file();
    }   

    return true;
}

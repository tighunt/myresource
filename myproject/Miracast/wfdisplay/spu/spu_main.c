
/**
 * This file contains the methods for loading serialization data
 * from flash, receiving messages from the application, and sending
 * the appropriate responses.
 *
 */

/**
 * Includes
 */
#include "hdcp2_hal.h"
#include "wifi_hdcp2.h"
#include "crypto.h"
#include "hmac.h"
#include "hdcp2_session.h"
#include "hdcp2_messages.h"
#include "hdcp2_interface.h"
#if (HDCP_REPEATER == 1)
#include "hdmi.h"
#endif
//#include <OSAL.h>
#include <pthread.h>
// to enable or disable the debug messages of this source file, put 1 or 0 below
#define LOG_TAG "HDCP2_SPU_MAIN"
#include <utils/Log.h>

/*
 * Prototypes
 */
pthread_mutex_t* p_thread_mutex;

void spu_set_thread_mutex(pthread_mutex_t* pMutex)
{
		p_thread_mutex = pMutex;
}

bool spu_nego_status(SOCKET_CW_OFFSET * aeskey, SOCKET_CW_OFFSET* aesiv) 
{
    if (cpu_spu_sendmsg(CID_STATE_GET, NULL, 0) == H2_STATE_B4_AUTHENTICATED) {
	if (aeskey)
	{
			aeskey->evenKeyOffset = hdcp2_GetKSXorLc128_CWOffset();
			aeskey->oddKeyOffset = hdcp2_GetKSXorLc128_CWOffset();
	}
	if (aesiv)
	{
			aesiv->evenKeyOffset = hdcp2_GetRiv_CWOffset();
			aesiv->oddKeyOffset = hdcp2_GetRiv_CWOffset();
	}
        return true;
    }
    return false;
}

int cpu_spu_sendmsg(CID_t cmd, unsigned char* indata, int insize) 
{
    int failCode;
    //extern pthread_t thread_mutex; 
	//ALOGI("cpu_spu_sendmsg before lock");
    pthread_mutex_lock(p_thread_mutex);
	//ALOGI("cpu_spu_sendmsg after lock");

    switch( cmd )
    {
        case CID_INIT:
            h2Init();       /** Zero out session values */
#if (HDCP_REPEATER == 1)
            hdmi_init( );
#endif			
            failCode = 0;
            break;
         case CID_MESSAGE_SEND:
            failCode = h2MessageParse(indata, insize);
            break;
         case CID_MESSAGE_POLL:
            failCode = h2MessagePoll(indata, insize);
            break;
         case CID_STATE_GET:
            pthread_mutex_unlock(p_thread_mutex);
            return h2StateGet();
		 case CID_SEND_KSVS:
		    ALOGI("CID_SEND_KSVS received\n");
		    failCode = hdcp2_SetKsvs( indata, insize );
		    break;
#if (HDCP_REPEATER == 1)
         case CID_CHECK_HDCP1X:
            failCode = hdmi_CheckHdcp1x( (H2bool*)&indata );
            break;
	 case CID_STATE_HDMI1X_POLICING_DECT:
            hdmi_task();
	    failCode = 0;
	    break;
#endif
         default:
            ALOGI( "Unknown command!\n");
            failCode = -1;
            break;
      }

      pthread_mutex_unlock(p_thread_mutex);
      ALOGI(" Failcode: %d\r\n", failCode );
      return failCode;
}

int cpu_spu_rcvmsg(unsigned char *outdata, int *outsize) 
{
    return cpu_spu_sendmsg(CID_MESSAGE_POLL, outdata, *outsize);
}

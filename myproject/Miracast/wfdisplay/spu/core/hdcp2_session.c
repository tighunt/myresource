#include "hdcp2_hal.h"
#include <sys/time.h>

#include "hdcp2_session.h"
#include "hdcp2_messages.h"
#include "crypto.h"
#include "hdcp2_interface.h"
#include "rcp_api.h"

#define LOG_TAG "HDCP2_SESSION"
#include <utils/Log.h>
#include "HDCP2X_VSN.h"
#include "hdcpDebug.h"

static h2State gState = H2_STATE_INIT;

static H2bool gbNoStoredEkm = 0;
extern int gPairingState;

static int gKmExistence;
static unsigned char tmpKpubkmBuf[EKPUBKM_SIZE]={0};

#define SPEED_UP_NO_STORED_KM 1
#define SYNC_RECEIVER_PRE_COMPUTATION_SUPPORT_WITH_TRANSMITTER 1
//#define DOWNGRADE_HDCP22_WHEN_NO_PROTOCOL_DESCRIPTOR 1 // it cause the problem, if the source is 2.2 and communicate in 2.1

#define DECRYPT_NO_INTERRUPT 1

/**
 * Functions to process incoming messages
 */

/**
 * Return code:
 * <0 = error
 *  0 = success
 *  1 = success, outgoing message available in *message.
 *
 *  Error codes:
 *  -1 = generic error
 *  -2 = Not enough room in *message for the outgoing message.
 *  -3 = Message received, but gState is not correct for this message.
 *  -4 = Decryption failed.
 */

#define STATUS_ERROR_DECRYPTION_FAILED -4
#define STATUS_ERROR_BAD_STATE -3
#define STATUS_ERROR_JUDGE_LPRIME -5
#define STATUS_ERROR_JUDGE_VPRIME -6
#define STATUS_ERROR_OUTGOING_MSG_TOO_LARGE -2
#define STATUS_OK 0

#if (HDCP_2X_VSN==21)
#define H2_TRANS_RECV_INFO_VERSION 0x01
#elif (HDCP_2X_VSN==22)
#define H2_TRANS_RECV_INFO_VERSION 0x02
#else
#define H2_TRANS_RECV_INFO_VERSION 0x00	// un-used, but give it a initial value
#endif

#define H2_TRANS_RECV_INFO_VERSION_21	0x01


/**
 * A message is waiting to be sent as a reply
 */
#define STATUS_OK_PENDING_MSG 1


static void changeSelfThreadParam(int policy,int priority)
{
	struct sched_param param;
	param.sched_priority=priority;
	/* scheduling parameters of target thread */
	int s=pthread_setschedparam(pthread_self(), policy, &param);

#ifdef CHECK_RST
	if (s != 0)
	   handle_error_en(s, "pthread_setschedparam");

	s=pthread_getschedparam(pthread_self(), &policy, &param);
	if (s != 0)
	   handle_error_en(s, "pthread_getschedparam");
	if(policy==SCHED_FIFO)
		printf("[Bruce] SCHED_FIFO\n");
	else if(policy==SCHED_OTHER)
		printf("[Bruce] SCHED_OTHER\n");
#endif
}

static void getCurrentThreadPriority(int *p_policy, int *p_priority)
{
	struct sched_param param;
	//int policy;
	int s=pthread_getschedparam(pthread_self(), p_policy, &param);
	*p_priority=param.sched_priority;

#ifdef CHECK_RST
	if (s != 0)
		handle_error_en(s, "pthread_getschedparam");
	else
		printf("[Bruce] policy:%s priority:%d\n",(*p_policy==SCHED_OTHER)?"SCHED_OTHER":"SCHED_FIFO",*p_priority);
#endif

}


/**
 * On Process AKE Init:
 * Set state to B1 ( Received AKE_INIT )
 * Copy RTX
 * Send AKE_SendCert message
 *
 * Any time AKE Init is received, the state machine is reset, so it
 * can be called from any state.
 *
 */
 static j=0;
 static checkTmp[1024]={0};
 
static int processAkeInit( unsigned char *message, unsigned int length )
{
   static H2_AKEInitPayLoad payload;
   static H2_AKESendCertPayLoad sendPayload;
   
   memset(&payload,0x00,sizeof(H2_AKEInitPayLoad));
   memset(&sendPayload,0x00,sizeof(H2_AKESendCertPayLoad));
   
   int rc=-1;

   /** Zero out session values */
   hdcp2_reset();

   if (!message) {
       return rc;
   }
   
   if (length < (RTX_SIZE +1))
       return rc;
   
   memcpy( payload.rTx, message+1, RTX_SIZE );

   /** Copy RTX value */
   hdcp2_Setrtx( payload.rTx );

   /** Make sure there's room for the outgoing message */
   if ( length < MAX_PACKED_MSG_SIZE )
   {
       printf("No room for outgoing AKE Send Cert message!\r\n");
       rc = STATUS_ERROR_OUTGOING_MSG_TOO_LARGE;
       return rc;
   }

   /** HDCP2 -> HDCP1 repeater */
   sendPayload.repeater = HDCP_REPEATER;

   /** Copy Cert RX into the outgoing message */
   hdcp2_GetCertRx( sendPayload.Cert_rx, sizeof(sendPayload.Cert_rx));

   /** Pack the message for transmission */
   message[0] = AKE_SEND_CERT;  /** Set msgId = AKE_SEND_CERT */
   message[1] = sendPayload.repeater;       /** Set repeater */
   memcpy(message+2, sendPayload.Cert_rx, CERT_RX_SIZE ); 
   length = CERT_RX_SIZE + 2;
   
   //hdcp2.1 repeater
   extern int g_REP_seq_num;
   g_REP_seq_num++;

   /** Everything went okay, so update state */
   gState = H2_STATE_B1_AKE_INIT;

   return STATUS_OK_PENDING_MSG;
}

static void saveNoStoredKmPayload(unsigned char *message)
{
	memcpy( tmpKpubkmBuf, message, EKPUBKM_SIZE );
}

/**
 * low level API to decrypt Ekpubrx(Km)
 */
static H2status decryptEKpubKm(unsigned char *message)
{
	H2status stat = H2_OK;
	static H2_AKENoStoredEkmPayLoad no_stored_km_payload;
	
	memset(&no_stored_km_payload,0x00,sizeof(H2_AKENoStoredEkmPayLoad));
	
    ALOGI("hdcp decryptEKpubKm start\n");
    
    #ifdef DECRYPT_NO_INTERRUPT
	int orgThreadPriority=0;
	int orgThreadPolicy=0;

	// - adjust thread policy and priority
	getCurrentThreadPriority(&orgThreadPolicy,&orgThreadPriority);
	changeSelfThreadParam(SCHED_FIFO,16);
    #endif

	memcpy( no_stored_km_payload.EKpub_Km, message, EKPUBKM_SIZE );    
	stat = hdcp2_SetEKpubKm( no_stored_km_payload.EKpub_Km );
	
	#ifdef DECRYPT_NO_INTERRUPT
	// - restore thread policy and priority
	changeSelfThreadParam(orgThreadPolicy,orgThreadPriority);
    #endif

	if ( stat != H2_OK )
    {
	   ALOGI("hdcp decryptEKpubKm 1mDecryption of EKpubKm failed!\n");
	}
	// km is existence
	gKmExistence=1;
	
	
	ALOGI("hdcp decryptEKpubKm 1mDecryption of EKpubKm success!\n");
	return stat;
}

/**
 * Process Ake No Stored Ekm message.
 *
 * This message is only valid in state B1.
 */

static int processAkeNoStoredEkm( unsigned char *message, unsigned int length )
{
   //H2status stat = H2_OK;
   ALOGI("hdcp AkeNoStoredEkm start\n");
   static H2_AKENoStoredEkmPayLoad payload;
   static H2_AKESendrrxPayLoad sendPayload;
   
   memset(&payload,0x00,sizeof(H2_AKENoStoredEkmPayLoad));
   memset(&sendPayload,0x00,sizeof(H2_AKESendrrxPayLoad));

   struct timeval tv;

   gbNoStoredEkm = 1;        /* Send Pairing info */

   // set to none-existance
   gKmExistence=0;

   /** Check state! */
   if ( gState != H2_STATE_B1_AKE_INIT )
   {
      gState = H2_STATE_INIT;      /* Reset state on error */
      ALOGI("hdcp AkeNoStoredEkm: no ake init yet\n");
      return STATUS_ERROR_BAD_STATE;
	 }

   if ( length < (EKPUBKM_SIZE + 1 )) {
         ALOGI ("hdcp AkeNoStoredEkm msg length is invalid!\r\n");
         return -1;
   }

#ifdef SPEED_UP_NO_STORED_KM
    saveNoStoredKmPayload(message+1);   
#else
   DPRINT(("\033[0;31;31m[DHDCP] decrypt Ekpubrx(km) in AKE_No_Stored_km\033[m\n"));
	/* decrypt Ekpubrx(km)
	 * note that in original implementation,
	 * this function would not only decrypt km,
	 * but also compute kd and H'
	 *
	 * Louis has already move the computation of kd and H' to next block.
	 * however, we still want send_rrx react faster.
	 * so we will move decrypt km to next block also...
	 */
	H2status stat = decryptEKpubKm(message+1);

   if ( stat != H2_OK )
   {
       ALOGI("hdcp processAkeNoStoredEkm Decryption of EKpubKm failed!\r\n");
       return STATUS_ERROR_DECRYPTION_FAILED;        /** No pending message. Timeout will notify TX of an error */
   }
#endif

   hdcp2_GenrRx(); 
   hdcp2_GetrRx( sendPayload.rrx, sizeof( sendPayload.rrx) );
   gettimeofday(&tv, NULL);
   ALOGI(" <=- dec ekpubKm ake_no_stored_ekm at (%ld, %ld)\n", tv.tv_sec, tv.tv_usec);
   
   message[0] = AKE_SEND_RRX;
   memcpy( message+1, sendPayload.rrx, RRX_SIZE ); /** Copy rrx */

   length = RRX_SIZE + 1;
   
   /** Set pairing state */
   gPairingState = PAIRING_SEND_HPRIME;
   
   
   ALOGI("hdcp processAkeNoStoredEkm STATUS_OK_PENDING_MSG! PAIRING_SEND_HPRIME\r\n");
   return STATUS_OK_PENDING_MSG;
}
/**
 * Process Ake Stored Ekm message.
 *
 * This message is only valid in state B1.
 */
					 
static int processAkeStoredEkm( unsigned char *message, unsigned int length )
{
   static H2_AKEStoredEkmPayLoad payload;
   static H2_AKESendrrxPayLoad sendPayload;
   
   memset(&payload,0x00,sizeof(H2_AKEStoredEkmPayLoad));
   memset(&sendPayload,0x00,sizeof(H2_AKESendrrxPayLoad));

   
   ALOGI("hdcp: processAkeStoredEkm start \n");

   /** Check state! */
   if ( gState != H2_STATE_B1_AKE_INIT )
   {
        ALOGI("Error: StoredEkm: Bad state\r\n");
        gState = H2_STATE_INIT;      /* Reset state on error */
        return STATUS_ERROR_BAD_STATE;
   }

    gbNoStoredEkm = 0;        /* Don't send pairing info */
    // set to none-existance
    gKmExistence=0;

    if ( length < (EKHKM_SIZE + M_SIZE + 1 ))
    {
       ALOGI("AkeStored msg length is invalid!\r\n");
       return -1;
     }

   memcpy( payload.EKh_Km, message+1, EKHKM_SIZE );
   memcpy( payload.m, message+1+EKHKM_SIZE, M_SIZE );

   /** Copy EKhKm / m value */
   hdcp2_SetEKhKm( payload.EKh_Km, payload.m );
   gKmExistence=1;

   /** Generate rRx */
   hdcp2_GenrRx();
   hdcp2_GetrRx( sendPayload.rrx, sizeof(sendPayload.rrx));

   // put rrx message into buffer
   message[0] = AKE_SEND_RRX;
   memcpy(message+1, sendPayload.rrx, RRX_SIZE ); 

   length = RRX_SIZE + 1;

   /** Set pairing state */
   gPairingState = PAIRING_SEND_HPRIME;
   
   ALOGI("hdcp: processAkeStoredEkm STATUS_OK_PENDING_MSG PAIRING_SEND_HPRIME\n");

   return STATUS_OK_PENDING_MSG;
}

static int processLcInitWithoutPreComputation( unsigned char *message, unsigned int length )
{
    static H2_LCInitPayLoad payload;
    static H2_LCSendLprimePayLoad sendPayload;
    
    memset(&payload,0x00,sizeof(H2_LCInitPayLoad));
    memset(&sendPayload,0x00,sizeof(H2_LCSendLprimePayLoad));

    ALOGI("processLcInitWithoutPreComputation: [%d]",__LINE__);


    /** Check state! */
    if (( gState != H2_STATE_B1_AKE_INIT ) && ( gState != H2_STATE_B2_LC_INIT ))
    {
        ALOGI("Error: LcInit: Bad state %d (should be %d)\r\n", gState, H2_STATE_B1_AKE_INIT);
        gState = H2_STATE_INIT;      /* Reset state on error */
        return STATUS_ERROR_BAD_STATE;
    }
   
    if ( length < ( RN_SIZE + 1 ))
    {
        ALOGI("LCINIT Length is invalid!\r\n" );
        return -1;
    }
     
    memcpy( payload.rn, message+1, RN_SIZE );
    
    /** Copy rn value */
    hdcp2_Setrn( payload.rn );
    
    /** Copy Lprime into the outgoing message */
    hdcp2_GetlPrime( sendPayload.Lprime, sizeof(sendPayload.Lprime)); 
    
    /* put message into buffer for transmission */
    message[0] = LC_SEND_L_PRIME;
    memcpy(message+1, sendPayload.Lprime, L_SIZE );
    length = L_SIZE + 1;
    
    /** State is B2 */
    gState = H2_STATE_B2_LC_INIT;
    
    return STATUS_OK_PENDING_MSG;
}

static int processLcInitWithPreComputation( unsigned char *message, unsigned int length )
{
    static H2_LCInitPayLoad payload;
    static H2_LCSendLprimePayLoad sendPayload;
    
    memset(&payload,0x00,sizeof(H2_LCInitPayLoad));
    memset(&sendPayload,0x00,sizeof(H2_LCSendLprimePayLoad));
    
    ALOGI("processLcInitWithPreComputation: [%d]",__LINE__);
    
    if (( gState != H2_STATE_B1_AKE_INIT ) && ( gState != H2_STATE_B2_LC_INIT ))
    {
        ALOGI("Error: LcInit: Bad state %d (should be %d)\r\n", gState, H2_STATE_B1_AKE_INIT );
        gState = H2_STATE_INIT;	   /* Reset state on error */
        return STATUS_ERROR_BAD_STATE;
    }	   
    
    if ( length < ( RN_SIZE + 1 ))
    {
        ALOGI("LC_INIT msg length is invalid!\r\n");
        return -1;
    }
   
    memcpy( payload.rn, message+1, RN_SIZE );
	 
	 /** Copy rn value */
	 hdcp2_Setrn( payload.rn );
	 
    /** Make sure there's room for the outgoing message */
    if ( length < AKE_MSG_SEND_LPRIME_SIZE )
    {
        ALOGI("No room for outgoing SKE Send Lprime message!\r\n");
        return STATUS_ERROR_OUTGOING_MSG_TOO_LARGE;  
    }
	 
    /** Copy Lprime into the outgoing message & backup it */
    hdcp2_GetlPrime( sendPayload.Lprime, L_SIZE); //kelly ?
    
    /** save it */
    hdcp2_SetLPrime(sendPayload.Lprime);
    
    message[0] = RTT_READY;  /** Set msgId = RTT_READY */
    length = 1;

    return STATUS_OK_PENDING_MSG;
}

static int processAkeTransmitterInfo( unsigned char *message, unsigned int length )
{
   //extern unsigned char gPreComputation;
   static H2_AkeTransmitterInfoPayLoad payLoad;
   static H2_AkeReceiverInfoPayLoad sendpayLoad={0};   
   static unsigned int outgoingLength = 0;
   
   ALOGI("hdcp processAkeTransmitterInfo: state \r\n");

   //check valid state
    if ( gState >= H2_STATE_B2_LC_INIT )
    {
        ALOGI("processAkeTransmitterInfo: Bad state \r\n");
        gState = H2_STATE_INIT;	   /* Reset state on error */
        return STATUS_ERROR_BAD_STATE;
    }

    if ( length < (AKE_TRANSMITTER_LENGTH_SIZE+AKE_TRANSMITTER_VERSION_SIZE+AKE_TRANSMITTER_CAPABILITY_MASK_SIZE + 1 ))   /* Skip msgId */
    {
        ALOGI("transmit info msg length is invalid!\r\n");
        return -1;
    }

    /** Copy data */
    memcpy( payLoad.LENGTH, message+1, 2 );
    payLoad.VERSION = *(message+1+2);
    memcpy( payLoad.TRANSMITTER_CAPABILITY_MASK, message+1+2+1, 2 );

    ALOGI("[LY] TRANSMITTER_CAPABILITY_MASK : %d, %d\n",
    		payLoad.TRANSMITTER_CAPABILITY_MASK[0],
    		payLoad.TRANSMITTER_CAPABILITY_MASK[1]);
   
     // Big Endian: low byte, high address
     setPreComputation(payLoad.TRANSMITTER_CAPABILITY_MASK[1] & 0x1);	// <--- bit 0
     setContentCategorySupport((payLoad.TRANSMITTER_CAPABILITY_MASK[1]>>1) & 0x1);	// <--- bit 1     
     
     DPRINT(("\033[0;31;31m[DHDCP] processAkeTransmitterInfo PreComputation: %d\033[m\n",getPreComputation()));
     // Bruce add after HDCP 2.2, save Transmitter info.
	 getRTKHDCPSTR()->transmitterVersion=payLoad.VERSION;
	 // also save Transmitter capability mask,
	 // we treat incoming payload as raw data and would not do endian conversion.
	 getRTKHDCPSTR()->transmitterCapMask[0]=payLoad.TRANSMITTER_CAPABILITY_MASK[0];
	 getRTKHDCPSTR()->transmitterCapMask[1]=payLoad.TRANSMITTER_CAPABILITY_MASK[1];


	 ALOGI("processAkeTransmitterInfo transmitterVersion :[%d] \r\n",getRTKHDCPSTR()->transmitterVersion);
     ALOGI("processAkeTransmitterInfo transmitterCapMask[0]:[%d] [1]:[%d]\r\n",getRTKHDCPSTR()->transmitterCapMask[0],getRTKHDCPSTR()->transmitterCapMask[1]);

	 //pack the out-going packet
	 //version
	 sendpayLoad.VERSION = H2_TRANS_RECV_INFO_VERSION;



	 DPRINT(("\033[0;31;31m[DHDCP] AKE_RECEIVER_INFO VERSION: %d\033[m\n",sendpayLoad.VERSION));


	
	 if(IsCapHDCP2Plus())
	 {
		sendpayLoad.RECEIVER_CAPABILITY_MASK[1] = H2_LC_INIT_PRECOMPUTATION;
		setSelfPreComputation(H2_LC_INIT_PRECOMPUTATION);

 #ifdef SYNC_RECEIVER_PRE_COMPUTATION_SUPPORT_WITH_TRANSMITTER
		/**
		  * more like a work-around:
		  * finally, if the transmitter doesn't have the capability to do the pre-computation
		  * we won't enable our pre-computation mechanism either.
		  */
		if ((getPreComputation()&0x01) == H2_LC_INIT_NO_PRECOMPUTATION)
		{
		    //kelly check.
			DPRINT(("\033[0;31;31m[DHDCP] sync Receiver pre-computation support bit with transmitter's [%s %d]\033[m\n",
					__FILE__,
					__LINE__));

			sendpayLoad.RECEIVER_CAPABILITY_MASK[1] = H2_LC_INIT_NO_PRECOMPUTATION;
			setSelfPreComputation(H2_LC_INIT_NO_PRECOMPUTATION);
		}
 #endif
	 }
	 else
	 {
		sendpayLoad.RECEIVER_CAPABILITY_MASK[1] = H2_LC_INIT_NO_PRECOMPUTATION;
		setSelfPreComputation(H2_LC_INIT_NO_PRECOMPUTATION);
	 }
	sendpayLoad.RECEIVER_CAPABILITY_MASK[0] = 0x00; //reserved

	 // Bruce add after HDCP 2.2, save Receiver Info.
	 getRTKHDCPSTR()->receiverVersion=sendpayLoad.VERSION;
	 getRTKHDCPSTR()->receiverCapMask[0]=sendpayLoad.RECEIVER_CAPABILITY_MASK[0];
	 getRTKHDCPSTR()->receiverCapMask[1]=sendpayLoad.RECEIVER_CAPABILITY_MASK[1];
	 
    ALOGI("receiverVersion :[%d] \r\n",getRTKHDCPSTR()->receiverVersion);

	 //length
	 sendpayLoad.LENGTH[0] = 0x00;
	 sendpayLoad.LENGTH[1] = AKE_RECEIVER_INFO_PAYLOAD_SIZE+1;/* add one for msg_id field */
	 ALOGI("[LY] RECEIVER CAPABILITY MASK: %d, %d\n",
			 sendpayLoad.RECEIVER_CAPABILITY_MASK[0],
			 sendpayLoad.RECEIVER_CAPABILITY_MASK[1]);
		
     message[0] = AKE_RECEIVER_INFO;  /** Set msgId = AKE_RECEIVER_IFNO */
	 memcpy(message+1, &sendpayLoad, AKE_RECEIVER_INFO_PAYLOAD_SIZE);
   /* pack the LENGTH, VERSION, RECEIVER_CAPABILITY_MASK outside the caller function  */
   length = AKE_RECEIVER_INFO_PAYLOAD_SIZE + 1;
   
    DPRINT(("\033[0;31;31m[DHDCP] (%d)processAkeTransmitterInfo PreC: %d\033[m\n",__LINE__,getPreComputation()));
    DPRINT(("\033[0;31;31m[DHDCP] (%d)processAkeTransmitterInfo SelfPreC: %d\033[m\n",__LINE__,getSelfPreComputation()));
   		

    /** Signal that a message is waiting */
    //following the hdcp2.0 spec. we don't reply for this pkt
    if(IsCapHDCP2Plus())
	{
	    ALOGI("[processAkeTransmitterInfo (%d)] return STATUS_OK_PENDING_MSG",__LINE__);
   		return STATUS_OK_PENDING_MSG;
   	}
	else
   	{	
        ALOGI("[processAkeTransmitterInfo (%d)]  return -1",__LINE__);
		return -1;
	}	
}

static int processRTTChallenge( unsigned char *message, unsigned int length )
{
	   H2status ret;
	
	 ALOGI( " hdcp processRTTChallenge start!\r\n" );
	   /**
	 	 * These two are static to keep them off the stack.
		 */
	   static H2_AkeRTTChallengePayLoad payload;
	   static H2_LCSendMostLprimePayLoad sendPayload;
     static H2_LCSendMostLprimePayLoad MostLPrime;
	
		 /** Check state! */	
		 if (( gState != H2_STATE_B1_AKE_INIT ) && ( gState != H2_STATE_B2_LC_INIT ))
		 {
		   ALOGI("Error: LcInit: Bad state %d (should be %d)\r\n", gState, H2_STATE_B1_AKE_INIT );
			 gState = H2_STATE_INIT;	  /* Reset state on error */
			 return STATUS_ERROR_BAD_STATE;
		 }
		 
     if ( length < ( (L_SIZE/2) + 1 ))
     {
         ALOGI( "RTTChallenge pkt length is invalid!\r\n" );
         return -1;
     }

     /** Copy data */
     memcpy( payload.L, message+1, (L_SIZE/2) );

		 /** judge the least 128bits of L */
		 ret = hdcp2_JudgeLeastLPrime( &payload, sizeof(payload.L));
		 if (ret == H2_ERROR)
		 {
			  ALOGI( "Error: JudgeLeastLPrime fail\r\n" );
			  gState = H2_STATE_INIT;	   /* Reset state on error */
			  return STATUS_ERROR_JUDGE_LPRIME;
		 }
	
		 /** Make sure there's room for the outgoing message */
		 if ( length < AKE_MSG_SEND_MOST_LPRIME_SIZE )
		 {
			 ALOGI( "No room for outgoing most 128bits of LC_Send_L_Prime message!\r\n");
			 return STATUS_ERROR_OUTGOING_MSG_TOO_LARGE;
		 }
	
		 /** Copy Lprime into the outgoing message */		  
		 hdcp2_GetMostlPrime( sendPayload.MostLprime, sizeof(sendPayload.MostLprime));
	
     message[0] = LC_SEND_L_PRIME;  /** Set msgId = LC_SEND_L_PRIME */
     memcpy( message+1, sendPayload.MostLprime, L_SIZE/2 ); /** Copy Lprime */
     length = (L_SIZE/2) + 1;
	
		 /** State is B2 */
		 gState = H2_STATE_B2_LC_INIT;
		 
	    ALOGI( " hdcp processRTTChallenge STATUS_OK_PENDING_MSG H2_STATE_B2_LC_INIT!\r\n" );
	
	   return STATUS_OK_PENDING_MSG;
}

static int processRepAuthStreamManage ( unsigned char *message, unsigned int length )
{
    int i;
    H2status stat = H2_OK;
    
    ALOGI( " hdcp processRepAuthStreamManage start!\r\n" );
    
    static H2_RepeaterAuthStreamManage_PayLoad payload={0};
    static H2_RepeaterAuthReady_PayLoad sendPayload={0};
    
    /* judge V and V' */
    //Gen. M Prime
    hdcp2_GetMPrime(&sendPayload, message, length);
	
    message[0] = REP_AUTH_STREAM_READY;  /** Set msgId = RECEIVER_AUTH_STATUS */
    memcpy(message+1, sendPayload.MPrime, MPRIME_SIZE);
    length = MPRIME_SIZE + 1;
    
    ALOGI( " hdcp processRepAuthStreamManage STATUS_OK_PENDING_MSG REP_AUTH_STREAM_READY!\r\n" );
	
    return STATUS_OK_PENDING_MSG;
}

static int processRepAuthSendAck ( unsigned char *message, unsigned int length )
{
	int i;
	H2status stat = H2_OK;
	
	static H2_RepeaterAuthSendACK_PayLoad payload;
	static H2_ReceiverAuthStatus_PayLoad sendPayload={0};
	
	ALOGI( " hdcp processRepAuthSendAck start!\r\n" );   

	
	/** Unpack RepAuthSendAck message */
	#if 0
	ALOGI("[LY] Rep Auth Send ACK \n:");
	for (i=0;i<17;i++)
		ALOGI("%02x ", message[i]);
	ALOGI("\n");
	#endif

	/** Check length */
	if ( length < ( (V_SIZE/2) + 1 ))
	{
	  ALOGI( "RepAuthSendAck pkt Length is invalid!\r\n" );
	  return -1;
	}
	
	/** Copy data */
	memcpy( payload.V, message+1, (V_SIZE/2) );

	//1. judge validation V & V'
	if (hdcp2_JudgeLeastVPrime(payload.V, V_SIZE/2) != 0)
	{
	 	ALOGI( "Error unpacking message!\r\n");
	 	return STATUS_ERROR_JUDGE_VPRIME;
	}
	//TODO: 
	//2. receiving RepeaterAuth_Send_Ack within 200MS or not
	
	// if fail, REAUTH_REQ = true, 0x01
	sendPayload.LENGTH[0]=0x00;
	sendPayload.LENGTH[1]=RECEIVERAUTHSTATUS_SIZE+1; //following spec.	   
	sendPayload.REAUTH_REQ = 0x00; //false = pass 
	
    message[0] = RECEIVER_AUTH_STATUS;  /** Set msgId = RECEIVER_AUTH_STATUS */
	memcpy(message+1, sendPayload.LENGTH, 2);
	message[3] = sendPayload.REAUTH_REQ;
    length = RECEIVERAUTHSTATUS_SIZE + 1;

    ALOGI( " hdcp processRepAuthSendAck STATUS_OK_PENDING_MSG RECEIVER_AUTH_STATUS!\r\n" );  
	return STATUS_OK_PENDING_MSG;
}

static int processSkeSendEks( unsigned char *message, unsigned int length )
{
   static H2_SKESendEksPayLoad payload;
   static H2_RepeaterAuthSendRxIdList_PayLoad sendPayload = {0};
   static H2uint8 ksvs[RECEIVERID_SIZE*MAX_DEVICECOUNT];
   static unsigned int outgoingLength = 0;
   int ret;
   
   
   ALOGI( "hdcp processSkeSendEks start\r\n");

   if ( gState != H2_STATE_B2_LC_INIT )
   {
      ALOGI("hdcp SkeSendEks: Bad State\r\n");
      gState = H2_STATE_INIT;      /* Reset state on error */
      return STATUS_ERROR_BAD_STATE;
   }

   if ( 0 != h2MsgRepeaterAuthSendReceiverIdListPack( NULL, &outgoingLength, &sendPayload, ksvs ))
   {
      ALOGI( "hdcp processSkeSendEks Error determing length of RX ID Message\r\n");
      return -1;
   }
   if ( length < outgoingLength )
   {
      ALOGI( "hdcp processSkeSendEks No room for outgoing RX ID list message!\r\n");
      return STATUS_ERROR_OUTGOING_MSG_TOO_LARGE;
   }

   /** Check length */
   if ( length < ( EDKEYKS_SIZE + RIV_SIZE + 1 ))
   {
      ALOGI( "hdcp processSkeSendEks Length is invalid!\r\n" );
      return -1;
   }

   /** Copy data */
   memcpy( payload.Edkey_Ks, message+1, EDKEYKS_SIZE );
   memcpy( payload.riv, message+EDKEYKS_SIZE+1, RIV_SIZE );

   /** Transition to state B3 */
   gState = H2_STATE_B3_SKE_SEND_EKS;

   /** Copy EdKeyKs value */
   hdcp2_SetEdKeyKs( payload.Edkey_Ks );

   /** Copy riv */
   hdcp2_SetRiv( payload.riv );

	 /*
	  * Don't send KSV if we're not a repeater.
	  */
#if (HDCP_REPEATER == 1)
	  H2uint8 DeviceCount;
	  H2uint8 Depth;
	  H2uint8 DepthExceeded;
	  H2uint8 DevicesExceeded;
			
	  hdcp2_GetKsvInfo( &DeviceCount, &Depth, &DevicesExceeded, &DepthExceeded, ksvs );
	  	  
	  if ( DeviceCount || DepthExceeded || DevicesExceeded )
	  {
	  	hdcp2_GetKsvInfo( &sendPayload.deviceCount, &sendPayload.depth, 
  	     &sendPayload.maxDevsExceeded, &sendPayload.maxCascadeExceeded , 
		      (H2uint8 *)ksvs );
	  
			/** Copy Vprime into the outgoing message */
			hdcp2_GetvPrime( sendPayload.VPrime, sizeof( sendPayload.VPrime ) );

			hdcp2_SaveStringInFile("/tmp/_vprime2", sendPayload.VPrime, sizeof( sendPayload.VPrime ));
	  
			if ( 0 != h2MsgRepeaterAuthSendReceiverIdListPack( message, &length, &sendPayload, ksvs ))
			{
		  	ALOGI( "hdcp processSkeSendEks Error packing Rx Id list\r\n");
		  	return -1;
			}

  		hdcp2_SaveStringInFile("/tmp/_ReceiverIdListPack1", message, length);
	    
			/** Success, Send KSVs */
			ret = STATUS_OK_PENDING_MSG;
	  }
	  else
	  {
			ret = STATUS_OK;
	  }
#endif

    /** State is B4 */
    gState = H2_STATE_B4_AUTHENTICATED;
    if(getHDCPCAP()==CAP_HDCP2_0)
		ALOGI("processSkeSendEks HDCP2.0 nego success!\n");
	else if(getHDCPCAP()==CAP_HDCP2_1)
		ALOGI("processSkeSendEks HDCP2.1 nego success!\n");
	else if(getHDCPCAP()==CAP_HDCP2_2)
		ALOGI("processSkeSendEks HDCP2.2 nego success!\n");

   return ret;
}


h2State h2StateGet( void )
{
   return gState;
}

int h2MessagePoll( unsigned char *message, unsigned int length )
{
    int rc = 0;
    
    switch ( gPairingState )
    {
        case PAIRING_INIT:
        break;
        case PAIRING_SEND_HPRIME:
        {                 
            DPRINT(("\033[0;31;31m[DHDCP] PAIRING_SEND_HPRIME start\033[m\n")); 
        
            #ifdef SPEED_UP_NO_STORED_KM
            if(gKmExistence==0)
            {
                DPRINT(("\033[0;31;31m[DHDCP] decrypt Ekpubrx(km) before AKE_Send_H_prime\033[m\n"));
                H2status stat = decryptEKpubKm(tmpKpubkmBuf);
            
                if ( stat != H2_OK )
                {
                    printf("Decryption of EKpubKm failed!\r\n");
                    return STATUS_ERROR_DECRYPTION_FAILED;        /** No pending message. Timeout will notify TX of an error */
                }
            }
            #endif
        
            H2uint8 Kd_tmp[KD_SIZE]={0};		 	
            static H2_AKESendHprimePayLoad sendPayload;
            
            memset(&sendPayload,0x00,sizeof(sendPayload));
            /** Make sure there's room for the outgoing message */
            if ( length < AKE_MSG_SEND_HPRIME_SIZE )
            {
                ALOGI("buffer too small for AKE Send Hprime message!\r\n");
                rc = STATUS_ERROR_OUTGOING_MSG_TOO_LARGE;
                
                DPRINT(("\033[0;31;31m[DHDCP] PAIRING_SEND_HPRIME end STATUS_ERROR_OUTGOING_MSG_TOO_LARGE\033[m\n")); 
                break;
            }
            
            int modeHDCP22=0;
		
	        #ifdef DECRYPT_NO_INTERRUPT                                                      		
	        int orgThreadPriority=0;                                               		
	        int orgThreadPolicy=0;                                                 		
                                                                                 		
	        // - adjust thread policy and priority                                 		
	        getCurrentThreadPriority(&orgThreadPolicy,&orgThreadPriority);         		
	        changeSelfThreadParam(SCHED_FIFO,16);                                  		
            #endif                                                               		
				
            if(getHDCPCAP()==CAP_HDCP2_2 && getRTKHDCPSTR()->transmitterVersion==0x2)
            modeHDCP22=1;
          	
            // Verbose debug print during QA phase
            DPRINT(("\033[0;31;31m[DHDCP] Process compute H' HDCPCap=%d TransmitterVersion=%d protocalBit=%d modeHDCP22=%d [%s %d]\033[m\n",
          		        getHDCPCAP(),
          		        getRTKHDCPSTR()->transmitterVersion,
          		        hdcp22_GetProtocolDescriptorBit(),
          		        modeHDCP22,
          		        __FILE__,
          		        __LINE__ ));
            /* Gen Kd = dkey0 || dkey1 */
            RCP_HDCP2_GenKd(SRAM_KM_ENTRY, SessionSecrets.rtx, SessionSecrets.rRx,SessionSecrets.rn, SRAM_KD_ENTRY,modeHDCP22);
            spu_GetKD(Kd_tmp);
            
            /** Compute HPrime from Kd, RTX, and Repeater */
            /** Copy Hprime into the outgoing message */
            if(getHDCPCAP()==CAP_HDCP2_2 && getRTKHDCPSTR()->transmitterVersion==0x2 && hdcp22_GetProtocolDescriptorBit()==0x1)
            {
                ALOGI( "h2MessagePoll[%d] 2.2 case!\r\n",__LINE__);
                DPRINT(("\033[0;31;31m[DHDCP] do Compute_Hprime_22\033[m\n"));
                Compute_Hprime_22(Kd_tmp,SessionSecrets.rtx,HDCP_REPEATER,
                		getRTKHDCPSTR()->receiverVersion,getRTKHDCPSTR()->receiverCapMask,
                		getRTKHDCPSTR()->transmitterVersion,getRTKHDCPSTR()->transmitterCapMask,
                		SessionSecrets.hPrime);
            }
            else
            {
            	ALOGI( "h2MessagePoll[%d] 2.0 2.1 case!\r\n",__LINE__);
            	DPRINT(("\033[0;31;31m[DHDCP] do Compute_Hprime\033[m\n"));
            	Compute_Hprime(Kd_tmp, SessionSecrets.rtx, HDCP_REPEATER, SessionSecrets.hPrime);
            }
            memset(Kd_tmp, 0x00, KD_SIZE);
          
            #ifdef DECRYPT_NO_INTERRUPT                                 
	        // - restore thread policy and priority                       
	        changeSelfThreadParam(orgThreadPolicy,orgThreadPriority);     
            #endif     
            /** Copy Hprime into the outgoing message */
            hdcp2_GethPrime( sendPayload.Hprime, sizeof(sendPayload.Hprime));
            
            /** Pack the message for transmission */
                 message[0] = AKE_SEND_H_PRIME;
          	 memcpy( message+1, sendPayload.Hprime, H_SIZE );
            
            /** Signal that a message is waiting */
            rc = STATUS_OK_PENDING_MSG;
            gPairingState = (gbNoStoredEkm ? PAIRING_SEND_PAIRING_INFO : PAIRING_INIT );
                
            DPRINT(("\033[0;31;31m[DHDCP] PAIRING_SEND_HPRIME end  ok\033[m\n"));    
        }
        break;
        case PAIRING_SEND_PAIRING_INFO:
        {
           
              ALOGI( " hdcp processRepAuthSendAck PAIRING_SEND_PAIRING_INFOS!\r\n" ); 
              static H2_AKESendPairingInfoPayLoad sendPayload;
        
			  #ifdef DECRYPT_NO_INTERRUPT                                    
			  int orgThreadPriority=0;                                         
              int orgThreadPolicy=0;                                           
                                                                 
              // - adjust thread policy and priority                           
              getCurrentThreadPriority(&orgThreadPolicy,&orgThreadPriority);   
              changeSelfThreadParam(SCHED_FIFO,16);                            
              #endif               
              hdcp2_GetEKhKm( sendPayload.Ekh_Km, sizeof( sendPayload.Ekh_Km ));
        
              #ifdef DECRYPT_NO_INTERRUPT                                    
	          // - restore thread policy and priority                          
	          changeSelfThreadParam(orgThreadPolicy,orgThreadPriority);        
              #endif         
              message[0] = AKE_SEND_PAIRING_INFO;
              memcpy( message+1, sendPayload.Ekh_Km, EKHKM_SIZE ); /** Copy rrx */
              length = EKHKM_SIZE + 1;
        
              gPairingState = PAIRING_INIT;
              rc = STATUS_OK_PENDING_MSG;
              DPRINT(("\033[0;31;31m[DHDCP] PAIRING_SEND_PAIRING_INFO end  ok\033[m\n")); 
        }
        break;
        default:
           gPairingState = PAIRING_INIT;
        break;
    }
    return rc;
}

void h2Init( void )
{
   DPRINT(("\033[0;31;31m[DHDCP] h2Init start\033[m\n")); 
   extern int g_REP_seq_num;
   extern int RXID_LIST_200MS_TIMEOUT_STATE;
   gState = H2_STATE_INIT;

   gbNoStoredEkm = 0;

   gPairingState = PAIRING_INIT;
   
   g_REP_seq_num = -1;

   /* set default pre-computation to false */
   setPreComputation(H2_LC_INIT_NO_PRECOMPUTATION);

   RXID_LIST_200MS_TIMEOUT_STATE = RXID_200MS_TIMEOUT_UNDEF;
   
   return;
}


int h2MessageParse( unsigned char *message, unsigned int length )
{
   int rc = 0;
   do
   {

      if ( NULL == message )
      {
         break;
      }
      unsigned char msgId = *message;
      
      switch( msgId )
      {
         /**
          * TX -> RX messages
          */
         case AKE_INIT:
            rc = processAkeInit( message, length );
            break;
         case AKE_NO_STORED_EKM:
         {
#ifdef DECRYPT_NO_INTERRUPT
	        int orgThreadPriority=0;
	        int orgThreadPolicy=0;

	        // - adjust thread policy and priority
	        getCurrentThreadPriority(&orgThreadPolicy,&orgThreadPriority);
	        changeSelfThreadParam(SCHED_FIFO,16);
            #endif
            rc = processAkeNoStoredEkm( message, length );
            /** Start pairing process */
            gPairingState = PAIRING_SEND_HPRIME;
#ifdef DECRYPT_NO_INTERRUPT
	        // - restore thread policy and priority
	        changeSelfThreadParam(orgThreadPolicy,orgThreadPriority);
            #endif
}
            break;
         case AKE_STORED_EKM:
            rc = processAkeStoredEkm( message, length );
            gPairingState = PAIRING_SEND_HPRIME;
            break;
         case LC_INIT:
         {   
        	 // 1. Transmitter supports pre-computation && is HDCP 2.1 or HDCP 2.2
        	 // 2. Receiver supports pre-computation && is HDCP 2.1 or HDCP 2.2
			if (((getPreComputation()&0x01) == H2_LC_INIT_PRECOMPUTATION) && ((getSelfPreComputation()&0x01) == H2_LC_INIT_PRECOMPUTATION) && IsCapHDCP2Plus())
			{
				DPRINT(("\033[0;31;31m[DHDCP] process LC_Init, PreCompute mode [%s %d]\033[m\n",
						__FILE__,
						__LINE__ ));
				rc = processLcInitWithPreComputation( message, length );
			}
			else
			{
				DPRINT(("\033[0;31;31m[DHDCP] process LC_Init, None-preCompute mode [%s %d]\033[m\n",
						__FILE__,
						__LINE__ ));
				rc = processLcInitWithoutPreComputation( message, length );
			}
		}	
        break;
        case RTT_CHALLENGE:
            rc = processRTTChallenge( message, length );	
        break;   
        case AKE_TRANSMITTER_INFO: //for hdcp2.1
            rc = processAkeTransmitterInfo( message, length );
        break;
        case SKE_SEND_EKS:
            rc = processSkeSendEks( message, length );
        break;
        case REP_AUTH_SEND_ACK:
		 	rc = processRepAuthSendAck(message, length);
        break;
        case REP_AUTH_STREAM_MANAGE:		 	
		 	rc = processRepAuthStreamManage(message, length);
        break;
            /**
             * RX -> TX messages. We should not receive these!
             */
        case AKE_SEND_CERT:
        case AKE_SEND_RRX:
        case AKE_SEND_H_PRIME:
        case AKE_SEND_PAIRING_INFO:
        case LC_SEND_L_PRIME:
        case REP_AUTH_SEND_RXID_LIST:
           ALOGI("TX received on RX!\n");
           /* TODO: Error handling? */
           rc = -1;
        break;
        default:
            ALOGI("HDCP received unknown message\n");
            rc = -2;
        break;
      }
   } while( 0 );

   return rc;
}

int h2MsgRepeaterAuthSendReceiverIdListPack( unsigned char *pMsg, unsigned int *pLength, const H2_RepeaterAuthSendRxIdList_PayLoad *payload, const H2uint8 *rcvrIds )
{
   int rc = 0;
   unsigned int ulSize = 0;
   unsigned int ii;
   int i;
   unsigned char num_V[4]={0};
   int rxidResult_size=0;

   if (IsCapHDCP2Plus())
   	rxidResult_size = REPEATERAUTHLIST_BASE_SIZE_H21;
   else
   	rxidResult_size = REPEATERAUTHLIST_BASE_SIZE_H20;

   do
   {
      /** If pMsg is NULL, return how many bytes are needed for the
       * message in *pLength
       */
      if ( NULL != payload )
      {
         if (( payload->maxDevsExceeded ) || ( payload->maxCascadeExceeded ))
         {
            ulSize = 3;
         }
         else
         {
            ulSize = rxidResult_size +
               payload->deviceCount*RECEIVERID_SIZE;
         }
      }

      if ( NULL == payload )
      {
         /** Need payload to determine message size */
         rc = -1;
         break;
      }

      if ( NULL == pMsg )
      {
         if ( NULL != pLength )
         {
            *pLength = ulSize;

            rc = 0;
            break;
         }
         else
         {
            /** Need pLength or pMsg */
            rc = -1;
            break;
         }
      }

      if ( NULL == rcvrIds )
      {
         if (( 0 == payload->maxDevsExceeded ) &&
            ( 0 == payload->maxCascadeExceeded ))
         {
            /** Receiver ID list cant be null
             * if cascade or devs are not exceeded.
             */
            rc = -1;
            break;
         }
      }
      if (( pLength ) && ( *pLength < ulSize))
      {
         ALOGI( "Error: Message is not large enough\n" );
         *pLength = ulSize;
         rc = 1;
         break;
      }

      pMsg[0] = REP_AUTH_SEND_RXID_LIST;
      pMsg[1] = payload->maxDevsExceeded;
      pMsg[2] = payload->maxCascadeExceeded;
      if (( payload->maxDevsExceeded ) || ( payload->maxCascadeExceeded ))
      {
         ulSize = 3;
         rc=0;
         break;
      }
      pMsg[3] = payload->deviceCount;
      pMsg[4] = payload->depth;
	  if (IsCapHDCP2Plus())
	  {
		  extern int g_REP_seq_num;
		  extern H2_gKsvInfo gKsvInfo;
		  LTB4(num_V[0], num_V[1], num_V[2], num_V[3], g_REP_seq_num);
	      pMsg[5] = gKsvInfo.HDCP20RepeaterDownStream;//gKsvInfo.HDCP20RepeaterDownStream;
	      pMsg[6] = gKsvInfo.HDCP1DeviceDownStream;//gKsvInfo.HDCP1DeviceDownStream;
		  pMsg[7] = num_V[1];
		  pMsg[8] = num_V[2];
		  pMsg[9] = num_V[3];
	      memcpy( pMsg+10, payload->VPrime, V_SIZE/2 ); /** Copy most 128 bits of Lprime */
	  }
	  else
	      memcpy( pMsg+5, payload->VPrime, V_SIZE ); /** Copy VPrime */

      for( ii=0; ii<payload->deviceCount;ii++)
      {
      	if (IsCapHDCP2Plus())
        	memcpy( pMsg+10+V_SIZE/2+ii*RECEIVERID_SIZE , rcvrIds+ii*RECEIVERID_SIZE, RECEIVERID_SIZE );
		else
	        memcpy( pMsg+5+V_SIZE+ii*RECEIVERID_SIZE , rcvrIds+ii*RECEIVERID_SIZE, RECEIVERID_SIZE );
      }
	  
	  if (IsCapHDCP2Plus())
	  {
		  hdcp2_SetLeastVPrime(payload->VPrime);
		  hdcp2_SaveStringInFile("/tmp/_vprime1", payload->VPrime, V_SIZE);
	  }

      if ( pLength )
      {
         *pLength = ulSize;
      }

      rc = 0;
      break;
   } while( 0 );

   return rc;
}


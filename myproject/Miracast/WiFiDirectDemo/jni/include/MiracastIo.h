#ifndef __MIRACAST_IO_H__
#define __MIRACAST_IO_H__
#include "SocketAVData.h"
#ifdef __cplusplus
namespace android {
// ref-counted object for callbacks
class EventListener: virtual public RefBase
{
public:
	virtual void notify(int msg, int ext1, int ext2, const Parcel *obj) = 0;
};

};
extern "C" {
#endif

typedef struct
{
	SOCKET_TS_DESC_CONFIG	config;
	SOCKET_CW_OFFSET		key;
	SOCKET_CW_OFFSET		iv;
} deliver_hdcp2_info;


typedef enum {


    NAVBUF_NONE,


    NAVBUF_DATA,


    NAVBUF_WAIT,


    NAVBUF_SHORT_WAIT,


    NAVBUF_STOP,


    NAVBUF_FLUSH,


    NAVBUF_NEW_SEGMENT,


    NAVBUF_END_SEGMENT,


    NAVBUF_NEW_MEDIA_TYPE,


    NAVBUF_NEW_AVSYNC_MODE,


    NAVBUF_ADJUST_CLOCK,


    NAVBUF_EVENT,


    NAVBUF_NEW_VIDEO_TYPE,

    NAVBUF_PREFETCH,


	NAVBUF_NEW_AVSYNC_MODE_EXT,


    NAVBUF_VIDEO_STOP,


    NAVBUF_DATA_EXT,


    NAVBUF_LONG_WAIT,

} NAV_BUF_ID;

typedef struct _tagNAVBUF {


    NAV_BUF_ID type;

    unsigned int bDoNotInterruptWithUserCmd;

        struct {



            unsigned char* payloadData;
            unsigned int   payloadSize;
            //int64_t        pts;
            //unsigned int   dataOffsetForPTS;
            unsigned char  rtp_header_marker_bit;
            unsigned short rtp_header_seq_number;
            unsigned int   infoId;
            unsigned char* infoData;
            unsigned int   infoSize;
        } data;
} NAVBUF;
/*
typedef struct
{
	long              videoAttrSize;	//number of bytes immediately following this field and up to audioAttrSize.
										//0 means no video attribute
	//SOCKET_MEDIA_TYPE videoType;
	long              videoPid;			// -1 means unknown
	long              isDIVX4;			// -1 means unknown
	long              ptsIncrement;		// -1 means unknown
	long              profile;			// -1 means unknown
	long              pictureWidth;		// -1 means unknown
	long              pictureHeight;	// -1 means unknown


	long              audioAttrSize;	//number of bytes immediately following this field and up to spicAttrSize.
										//0 means no audio attribute
	//SOCKET_MEDIA_TYPE audioType;
	long              channelNum;		// -1 means unknown
	long              sampleRate;		// -1 means unknown
	long              bitsPerSample;	// -1 means unknown
	long              audioPid;			// -1 means unknown
	long              sbrFlag;			// if audioType is S_MEDIA_TYPE_AUDIO_RAW_AAC, 1 means HE AAC
	long              codecID;			// WAVE form wFormatTag IDs. ref: http://wiki.multimedia.cx/index.php?title=TwoCC
	long              blockAlignSize;   // in bytes

	long              spicAttrSize;		//number of bytes immediately following this field.
										//0 means no spic attribute
	//SOCKET_MEDIA_TYPE spicType;
	long              spicPid;			// -1 means unknown
	unsigned int      spicLangCode;		//ISO-639 language code

	long              contentAttrSize;
	long              packetSize;		//packet size for ts container
	//SOCKET_CONTAINER_TYPE containerType;
	//SOCKET_APP_TYPE	  appType;
	long              bMonitorPAT;
	long			  pcrPid;
	long			  syncWordOffset;	// 0x47 position of 192 packet size TS file.
										// 0: 4Bytes TT at beginning of each packet, 4: 4BytesTT at the end of each packet.

} SOCKET_MEDIA_ATTRIBUTE;*/

typedef long HRESULT;
#define S_OK		(0)
#define S_FALSE		(-1)


typedef enum {

    /* generic io error, (-1) matches error code returned by read() or lseek64() */
    IOPLUGIN_ERROR_GENERIC     = -1,

    /* End-of-File error */
    IOPLUGIN_ERROR_EOF         = -2,

    /* Busy, used in asynchronous io modes or network streaming io */
    IOPLUGIN_ERROR_BUSY        = -3,

    /* error after device removal */
    IOPLUGIN_ERROR_NO_DEVICE   = -4,

    /* error in file-system */
    IOPLUGIN_ERROR_FILE_SYSTEM = -5,

    /* the requested file position already gets overwritten in a circular file */
    IOPLUGIN_ERROR_OVERWRITTEN = -6,

    /* playback catch record */
    IOPLUGIN_ERROR_CATCH_RECORD = -7,

    /* Erroneous I/O due to hardware error */
    IOPLUGIN_ERROR_EIO = -8,

    IOPLUGIN_ERROR_RESET = -9,

/* waiting for in-coming data but timeout */
    IOPLUGIN_ERROR_TIMEOUT = -10,

} IOPLUGIN_ERROR;


typedef enum {

	/* to indicate there is no private info  0 */
	PRIVATEINFO_NONE,

	/* sending PTS information (int64_t)  1*/
	PRIVATEINFO_PTS,

	/* sending payload information (PAYLOADINFO)  2*/
	PRIVATEINFO_PAYLOAD_INFO,

} PRIVATEINFO_IDENTIFIER;

//#define MIRACAST_WIFI_SOURCE_FILE		"/tmp/MyTmp/miracast_wifi_source"
//#define MIRACAST_HDCP2_INFO_FILE		"/tmp/MyTmp/miracast_hdcp2_info"


#define TS_PACKET_SIZE			188
#define UNRETURN_BUFFER_SIZE	(188*7*500)
#define MIRACAST_MTU_SIZE		(1500)

typedef struct {
	unsigned int version:2;	/* protocol version */
	unsigned int p:1;		/* padding flag */
	unsigned int x:1;		/* header extension flag */
	unsigned int cc:4;		/* CSRC count */
	unsigned int m:1;		/* marker bit */
	unsigned int pt:7;		/* payload type */
	unsigned short seq;		/* sequence number */
	unsigned int ts;		/* timestamp */
	unsigned int ssrc;		/* synchronization source */
} rtp_hdr_t;

typedef enum {
	RETURN_HDCP2_NONE,
	RETURN_HDCP2_CONFIG,
	RETURN_HDCP2_IV,
	RETURN_HDCP2_KEY
} RETURN_HDCP2_STATE;

typedef struct _tagIOPlugin_Miracast
{
	int					socketId;
	SOCKET_DATA_HEADER	header;
	int				bHasReturnMediaAttributes;
	int				bHasReturnHDCP2Info;
	RETURN_HDCP2_STATE	returnHDCP2State;
	deliver_hdcp2_info	hdcp2Info;
	unsigned char		unReturnBuffer[UNRETURN_BUFFER_SIZE];
	unsigned int		unReturnBytes;
} IOPlugin_Miracast;

void* Miracast_IO_open();
int Miracast_IO_close(void* pInstance);
int Miracast_IO_read(void* pInstance, unsigned char* pBuffer, int size, NAVBUF* pEventReceived);
#ifdef __cplusplus
}
#endif

#endif

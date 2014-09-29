/*
 * input plugin for http network streams
 *
 */
//#include "HttpIOPlugin.h"
#include "hresult.h"
#include <stdio.h>
#include <stdlib.h>


typedef enum {
    MEDIATYPE_None,                     // 0, not available

//  Major Type:                         Description
    MEDIATYPE_AnalogAudio,              // 1, Analog audio.
    MEDIATYPE_AnalogVideo,              // 2, Analog video.
    MEDIATYPE_Audio,                    // 3, Audio.
    MEDIATYPE_Video,                    // 4, Video.
    MEDIATYPE_AUXLine21Data,            // 5, Line 21 data. Used by closed captions.
    MEDIATYPE_DVD_ENCRYPTED_PACK,       // 6, Encrypted DVD data
    MEDIATYPE_MPEG2_PES,                // 7, MPEG-2 PES packets.
    MEDIATYPE_ScriptCommand,            // 8, Data is a script command, used by closed captions.
    MEDIATYPE_Stream,                   // 9, Byte stream with no time stamps.
    MEDIATYPE_Text,                     // 10, Text.
    MEDIATYPE_Image,					// 11, Image
    MEDIATYPE_Directory,				// 12, Direcotory 
    MEDIATYPE_NEPTUNE_TP,				// 13

//  Audio Subtype:                      Description
    MEDIASUBTYPE_PCM,                   // 14, PCM audio.(big endian)
    MEDIASUBTYPE_PCM_LITTLE_ENDIAN,     // 15, PCM audio.(little endian)
    MEDIASUBTYPE_MPEG1Packet,           // 16, MPEG1 Audio packet.
    MEDIASUBTYPE_MPEG1ES,               // 17, MPEG1 Audio Payload (Elementary Stream).
    MEDIASUBTYPE_MPEG2_AUDIO,           // 18, MPEG-2 audio data
    MEDIASUBTYPE_DVD_LPCM_AUDIO,        // 19, DVD audio data
    MEDIASUBTYPE_MLP_AUDIO,				// 20, trueHD
    MEDIASUBTYPE_DOLBY_AC3,             // 21, Dolby data
    MEDIASUBTYPE_DOLBY_AC3_SPDIF,       // 22, Dolby AC3 over SPDIF.
    MEDIASUBTYPE_MPEG_AUDIO,            // 23, general MPEG audio
    MEDIASUBTYPE_DTS,                   // 24, DTS audio
    MEDIASUBTYPE_DTS_HD,				// 25, DTS-HD audio
    MEDIASUBTYPE_DTS_HD_EXT,			// 26, DTS-HD extension sub stream
    MEDIASUBTYPE_DTS_HD_CORE,			// 27, DTS_HD core sub stream
    MEDIASUBTYPE_DDP,					// 28, Dolby Digital Plus audio 
    MEDIASUBTYPE_SDDS,                  // 29, SDDS audio
    MEDIASUBTYPE_DV,                  	// 30, DV audio
	MEDIASUBTYPE_AAC,					// 31, AAC(Advanced Audio Coding)
	MEDIASUBTYPE_RAW_AAC,				// 32, AAC without header		
    MEDIASUBTYPE_OGG_AUDIO,             // 33, OGG vorbis
    MEDIASUBTYPE_WMA,                   // 34, WMA audio
    MEDIASUBTYPE_WMAPRO,                // 35, WMAPRO audio
    MEDIASUBTYPE_MP3,                   // 36, MP3 file
    MEDIASUBTYPE_MP4,                   // 37, MP4 aac file
    MEDIASUBTYPE_LATM_AAC,              // 38, LATM/LOAS AAC file
	MEDIASUBTYPE_WAVE,                  // 39, WAVE audio
    MEDIASUBTYPE_AIFF,                  // 40, AIFF audio
    MEDIASUBTYPE_RTP,                   // 41, RTP
    MEDIASUBTYPE_APE,                   // 42, APE, pure audio
   #ifdef USE_CAMERA
            MEDIASUBTYPE_ILBC_AUDIO,            // ILBC audio
    #endif
    MEDIASUBTYPE_DVD_SUB_PICTURE,       	// 43, DVD-Video subpicture
	MEDIASUBTYPE_DIVX_SUB_PICTURE,			// 44, DivX Subtitles
	MEDIASUBTYPE_DVB_SUBTITLE_SUB_PICTURE,  // 45, DVB Subtitles
	MEDIASUBTYPE_DVB_TT_SUB_PICTURE,    	// 46, DVB Teletext
	MEDIASUBTYPE_ATSC_CC_SUB_PICTURE,   	// 47, ATSC close caption
	MEDIASUBTYPE_BLURAY_PGS_SUB_PICTURE,	// 48, Blu-ray PGS

// Video Subtype:						Description
	MEDIASUBTYPE_DIVX3_VIDEO,			// 49, DivX 3.11 Video
	MEDIASUBTYPE_DIVX_VIDEO,			// 50, DivX 4/5 Video
	MEDIASUBTYPE_MPEG4_VIDEO,			// 51, MPEG-4 Video
	MEDIASUBTYPE_MJPEG_VIDEO,			// 52, Motion JPEG
	MEDIASUBTYPE_DVSD_VIDEO,			// 53, DV(Standard Definition)Video
	MEDIASUBTYPE_H264_VIDEO,			// 54
	MEDIASUBTYPE_H263_VIDEO,			// 55
	MEDIASUBTYPE_VP6_VIDEO,				// 56
	MEDIASUBTYPE_VC1_VIDEO,				// 57
	MEDIASUBTYPE_VC1_ADVANCED_VIDEO,	// 58
	MEDIASUBTYPE_VC1_WINDOWSMEDIA_VIDEO,// 59
	MEDIASUBTYPE_AVS_VIDEO,				// 60

//  DVD type/Subtype:                        Description
    MEDIATYPE_DVD,						// 61
    MEDIATYPE_SVCD,						// 62
    MEDIATYPE_VCD,						// 63
    MEDIATYPE_CDDA,						// 64
    MEDIATYPE_FILE,						// 65
    MEDIATYPE_PLAYLIST,					// 66
    MEDIATYPE_NET,						// 67
    MEDIATYPE_MMS,						// 68 
    MEDIATYPE_TRANSCODE,				// 69
    MEDIATYPE_DV,						// 70
    MEDIATYPE_DV_FILE,					// 71
    MEDIATYPE_STB,                      // 72
    MEDIATYPE_FILESET,					// 73
    MEDIATYPE_FILELINK,					// 74
    MEDIATYPE_RTSP,						// 75
    MEDIATYPE_RTP,                      // 76, hongjian_shen
    MEDIATYPE_PPS,						// 77, 
    MEDIATYPE_BESTV,					// 78,
    MEDIATYPE_VOOLETV,					// 79,
    MEDIATYPE_THUNDER_REMOTE,			// 80, 
    MEDIATYPE_THUNDER,					// 81, 
    MEDIATYPE_CUSTOM_1,					// 82, 
    MEDIATYPE_CUSTOM_2,					// 83
    MEDIATYPE_CUSTOM_3,					// 84

    MEDIASUBTYPE_MPEG2_VIDEO,           // 85, MPEG-2 video
    MEDIASUBTYPE_DVD_SUBPICTURE,        // 86, DVD Subpicture
    MEDIASUBTYPE_DVD_RTRW_PLUS,         // 87, DVD +RW 
    MEDIASUBTYPE_DVD_RTRW_MINUS,        // 88, DVD -VR
	MEDIASUBTYPE_DVD_RTRW_MINUS_HD,		// 89, DVD -VR on HDD (as special case)
    MEDIASUBTYPE_DVD_VIDEO,             // 90, Recordble DVD -VIDEO
    MEDIASUBTYPE_DVD_VIDEO_ROM,         // 91, DVD ROM
    MEDIASUBTYPE_DVD_RTRW_STILL,        // 92, DVD -VR Still Picture
    MEDIASUBTYPE_DVD_MENU,              // 93, DVD Menu VOB
    MEDIASUBTYPE_SVCD,                  // 94, Recordable SVCD
    MEDIASUBTYPE_SVCD_ROM,              // 95, SVCD Read Only
    MEDIASUBTYPE_VCD,                   // 96, VCD Recordable
    MEDIASUBTYPE_VCD_ROM,               // 97, VCD Read Only
    MEDIASUBTYPE_CDDA,                  // 98, Recordable CDDA
    MEDIASUBTYPE_CDDA_ROM,              // 99, CDDA Read Only
    MEDIASUBTYPE_AVCHD,                 // 100, AVCHD 
    MEDIASUBTYPE_AVCHD_ROM,             // 101, AVCHD Read Only
    MEDIASUBTYPE_HTTP,					// 102, 
    MEDIASUBTYPE_MMS,					// 103,
    MEDIASUBTYPE_TRANSCODE,				// 104, 
    MEDIASUBTYPE_MPEG_PROGRAM,			// 105,
    MEDIASUBTYPE_STB_1394,              // 106, Richard
    MEDIASUBTYPE_FILESET,				// 107,
    MEDIASUBTYPE_FILELINK,				// 108

//  Line 21 Subtype:                    Description
    MEDIASUBTYPE_Line21_BytePair,       // 109, Line 21 data as byte pairs
    MEDIASUBTYPE_Line21_GOPPacket,      // 110, Line 21 data in DVD GOP Packet
    MEDIASUBTYPE_Line21_VBIRawData,     // 111, Line 21 data in raw VBI format

//  MPEG-1 Subtypes:                    Description
    MEDIASUBTYPE_MPEG1Audio,            // 112, MPEG audio
    MEDIASUBTYPE_MPEG1System,           // 113, MPEG system
    MEDIASUBTYPE_MPEG1SystemStream,     // 114, Obsolete. Do not use.
    MEDIASUBTYPE_MPEG1Video,            // 115, MPEG video
    MEDIASUBTYPE_MPEG1VideoCD,          // 116, MPEG video CD
    MEDIASUBTYPE_MPEG1SuperVCD,         // 117, MPEG SVCD

//  MPEG-2 Subtypes:                    Description
    MEDIASUBTYPE_MPEG2_PROGRAM,         // 118, Program stream
    MEDIASUBTYPE_MPEG2_TRANSPORT,       // 119, Transport stream (TS), with 188-byte packets

//  Subpicture Subtypes:                Description
    MEDIASUBTYPE_AI44,                  // 120, For subpicture and text overlays
    MEDIASUBTYPE_IAK2,                  // 121, For subpicture and text overlays,
                                        // 	   2 bits indexed, constant alpha and single color key
    MEDIASUBTYPE_IAK4,                  // 122, For subpicture and text overlays,
                                        //     4 bits indexed, constant alpha and single color key
    MEDIASUBTYPE_IAK8,                  // 123, For subpicture and text overlays,
                                        //     8 bits indexed, constant alpha and single color key.
                                        //     16 programmable palete.
//  External Subtitles
    MEDIASUBTYPE_SRT,					// 124, 
    MEDIASUBTYPE_SSA,					// 125,
    MEDIASUBTYPE_ASS,					// 126,
    MEDIASUBTYPE_TXT,					// 127

//  Analog Video Subtypes:              Description
    MEDIASUBTYPE_AnalogVideo_NTSC,      // 128, NTSC
    MEDIASUBTYPE_AnalogVideo_PAL,       // 129, PAL (will be extended)

//  Image Subtypes
    MEDIASUBTYPE_IMAGE_JPEG,            // 130, JPEG Images 
    MEDIASUBTYPE_IMAGE_BITMAP,          // 131, BMP Images 
    MEDIASUBTYPE_IMAGE_GIF,             // 132, GIF Images 
    MEDIASUBTYPE_IMAGE_TIFF,            // 133, TIFF Images 
    MEDIASUBTYPE_IMAGE_PNG,             // 134, PNG Images 
    MEDIASUBTYPE_IMAGE_DNG,             // 135, DNG Images 
  


    MEDIASUBTYPE_MPEG2_AUDIO_WITH_EXTENSION,	// 136
    MEDIASUBTYPE_MPEG2_AUDIO_WITHOUT_EXTENSION,	//137


	MEDIASUBTYPE_PPSRM,			// 138, PPS RM
	MEDIASUBTYPE_PPSWMA,        // 139, PPS WMA
	
	MEDIASUBTYPE_BESTVRM,		// 140, PPS RM
	MEDIASUBTYPE_BESTVWMA,      // 141, PPS WMA
	
	// Real Media
	MEDIASUBTYPE_RM,			// 142
	MEDIASUBTYPE_RV,			// 143, real video
	MEDIASUBTYPE_RA_COOK,		// 144, RealAudio 8 Low Bit Rate(cook)
	MEDIASUBTYPE_RA_ATRC,		// 145, RealAudio 8 Hight Bit Rate(atrc)
	MEDIASUBTYPE_RA_RAAC,		// 146, AAC(raac)
	MEDIASUBTYPE_RA_SIPR,		// 147, RealAudio Voice(sipr)
	MEDIASUBTYPE_RA_LSD,		// 148, RealAudio Lossless

    MEDIASUBTYPE_ADPCM,         // 149, ADPCM audio
    MEDIASUBTYPE_FLAC,			// 150
    MEDIASUBTYPE_ULAW,			// 151
    MEDIASUBTYPE_ALAW,			// 152

// AVI Container subtype (using DivX specification)
	MEDIASUBTYPE_DMF_0,			// 153, Traditional AVI 1.0
	MEDIASUBTYPE_DMF_1,			// 154, DivX Ultra
	MEDIASUBTYPE_DMF_2,			// 155, OpenDML 2.0	
	
    MEDIASUBTYPE_MP4_VIDEO,     // 156, mp4 video file

//  File Container Type
    MEDIATYPE_Interleaved,      // 157, Interleaved audio and video, aka AVI (Type1 DV AVI will use MEDIATYPE_DV)
    MEDIATYPE_FLASHVIDEO,       // 158, Macromedia Flash Video
    MEDIATYPE_MATROSKA,         // 159, Matroska Container (MKV)
    MEDIASUBTYPE_MATROSKA_DIVX_ENHANCED,	// 160,
    MEDIATYPE_ASF,              // 161, ASF/WMV/WMA file
    
// HDMV lpcm audio type
	MEDIASUBTYPE_PCM_HDMV, 		// 162

	//used for blue-ray	
	MEDIASUBTYPE_HDMV_MLP_AUDIO,	// 163, trueHD audio
	MEDIASUBTYPE_HDMV_DOLBY_AC3,	// 164
	MEDIASUBTYPE_HDMV_DDP,			// 165
	MEDIASUBTYPE_HDMV_DTS,			// 166
	MEDIASUBTYPE_HDMV_DTS_HD,		// 167
	
	MEDIATYPE_FLASH,				// 168

	MEDIATYPE_PHOTO_ALBUM,			// 169

	MEDIATYPE_NRD, 					// 170
	MEDIASUBTYPE_NRD_2XX,			// 171

	MEDIASUBTYPE_BD,				// 172
	MEDIASUBTYPE_BD_ROM,			// 173

	MEDIASUBTYPE_VP8_VIDEO,			// 174
	
    MEDIATYPE_DIRECT_CONNECT,		// 175

    MEDIATYPE_HLS, 					// 176, HTTP Live Streaming
    
    MEDIATYPE_CONFERENCE,			// 177, 
    
    MEDIASUBTYPE_AMRWB_AUDIO,		// 178
    MEDIASUBTYPE_AMRNB_AUDIO,		// 179
    
    //Used for Skype
    MEDIASUBTYPE_SILK_AUDIO,		// 180
    MEDIASUBTYPE_G729_AUDIO,		// 181
    
    MEDIASUBTYPE_MVC_DEPENDENT_VIDEO, //182
     
    //used for camera display
    MEDIATYPE_CAMERA,				// 183
    MEDIASUBTYPE_YUV_VIDEO,			// 184
    
    MEDIASUBTYPE_APE_AUDIO,			// 185
    
    MEDIASUBTYPE_WMV7_VIDEO,		// 186
    MEDIASUBTYPE_WMV8_VIDEO,		// 187
    
    MEDIATYPE_SOCKET,			// 188
    MEDIATYPE_SNDA,             // 189
    
    MEDIASUBTYPE_IMAGE_JPS,     //190 3d photo        
    MEDIASUBTYPE_IMAGE_MPO,     //191 3d photo
    
    MEDIASUBTYPE_NRD_3XX,		//192
    MEDIATYPE_OMADRMFILE,		//193 omadrm file
    MEDIASUBTYPE_OMADRMMP4,		//194 omadrm mp4
    MEDIASUBTYPE_OMADRMASF,		//195 omadrm asf
    MEDIASUBTYPE_SNDAOGG,		//196 snda raw ogg
    MEDIASUBTYPE_SNDASSOGG,		//197 snda standard ogg
    MEDIASUBTYPE_SNDAWAVE,      //198 snda WAVE audio

	MEDIASUBTYPE_FJPEG_VIDEO,		//199 awind
} ENUM_MEDIA_TYPE;

typedef struct _tagNAVMEDIAINFOEX {
        ENUM_MEDIA_TYPE mediaType; 
        ENUM_MEDIA_TYPE videoType;               /* for Codec Type */
        ENUM_MEDIA_TYPE audioType;               /* for Codec Type */
        ENUM_MEDIA_TYPE spicType;                /* for Codec Type */

        void*           audioPrivateData;        
        unsigned int    audioPrivateDataSize;    /* AUDIOCODECINFO */

        void*           videoPrivateData;
        unsigned int    videoPrivateDataSize;    /* VIDEOCODECINFO */

        void*           spicPrivateData;
        unsigned int    spicPrivateDataSize;

    } NAVMEDIAINFOEX;


typedef struct _tagNAVIOINFO {

        int64_t         totalBytes;               /* -1 means unknown */
        int             totalSeconds;             /* -1 means unknown */
        int             averageBytesPerSecond;    /* -1 means unknown */
        int             seekAlignmentInBytes;     /* -1 means no restriction */
        int             readSizeAlignmentInBytes; /* -1 means no restriction */
        bool            bSeekable;                /* true: support seeking within media */
        bool            bSlowSeek;                /* true: seeking is slow */
        bool            bStreaming;               /* true: media can grow bigger */
        bool            bDirectIOCapable;         /* true: support direct IO */
		bool			bOpenMany;                /* true: this stream can support openN */
        ENUM_MEDIA_TYPE ioType;
        void*           ioInternalHandle;
        ENUM_MEDIA_TYPE	preDeterminedType;        /* for RTSP w/RTP cases */
        NAVMEDIAINFOEX  mediaInfo;
        bool            bSeekByTime;              /* true: Calling IOPLUGIN.seekByTime() to do the seeking jobs */
    	
    	bool			bPrepareBuf;			  /* true : io plugin will prepare buffer and input plguin gets read pointer by getReadPtr()*/
    	bool			bUseDDRCopy;			  /* It is meaningless if bPrepareBuf = false. true : buffer prepared by IO plugin is contiguous and be able to use DDRCopy */
    } NAVIOINFO;


typedef enum {

        /* to indicate there is nothing in Nav Buffer */
        NAVBUF_NONE,

        /* to send data (bit-stream or private-info), use fields under "data" */
        NAVBUF_DATA,

        /* to ask nav thread to wait, use fields under "wait" */
        NAVBUF_WAIT,

        /* to ask nav thread to wait (fullness pending is kept), use fields under "wait" */
        NAVBUF_SHORT_WAIT,

        /* to ask for streaming stop, use fields under "stop" */
        NAVBUF_STOP,

        /* to flush everything buffered downstream, no arguments */
        NAVBUF_FLUSH,

        /* to begin a new bit-stream segment, use fields under "segment" */
        NAVBUF_NEW_SEGMENT,

        /* to end the current bit-stream segment, use fields under "segment" */
        NAVBUF_END_SEGMENT,

        /* to set new media type for demux, use fields under "media" */
        NAVBUF_NEW_MEDIA_TYPE,

        /* to set new av synchronization mode, use fields under "avsync" */
        NAVBUF_NEW_AVSYNC_MODE,

        /* to adjust clock in system-master av sync mode, use fields under "clock" */
        NAVBUF_ADJUST_CLOCK,

        /* to fire event, use fields under "event" */
        NAVBUF_EVENT,
        
        /* to set new video type to change new video decoder, use fields under "media" */
        NAVBUF_NEW_VIDEO_TYPE,
        
        NAVBUF_PREFETCH,

		/*to set new av synchronization mode, use fields under "avsync" 
		  and field 'reason' is valid. */
		NAVBUF_NEW_AVSYNC_MODE_EXT,
		
		/* to ask video pin to stop streaming, use fields under "stop" */
        NAVBUF_VIDEO_STOP,
        
    } NAV_BUF_ID;



	typedef enum {
	
			NAV_AVSYNC_AUDIO_MASTER_AUTO,	   /* the new auto no skip mode */
			NAV_AVSYNC_AUDIO_MASTER_AUTO_SKIP, /* the old auto mode */
			NAV_AVSYNC_AUDIO_MASTER_AUTO_AF,   /* auto no skip mode, but use audio master first at the start */
			NAV_AVSYNC_AUDIO_MASTER,
			NAV_AVSYNC_SYSTEM_MASTER,
			NAV_AVSYNC_AUDIO_ONLY,
			NAV_AVSYNC_VIDEO_ONLY,
			NAV_AVSYNC_VIDEO_ONLY_SLIDESHOW,
			NAV_AVSYNC_SLIDESHOW
	
		} NAV_AVSYNC_MODE;


typedef enum {
         	CAUSED_BY_AUDIO_TRACK_CHANGE = 0,
    } AVSYNC_REASON;


typedef struct _tagNAVBUF {

        /* identify nav buffer type */
        NAV_BUF_ID type;

        /* non-zero (true) means recepient should read another block without checking user commands */
        unsigned int bDoNotInterruptWithUserCmd;

        union {

            /* if type == NAVBUF_DATA */
            struct {
                unsigned int pinIndex; /* index (0~) of the pin to deliver data */
                                       /* NOTE: pinIndex is decided by demux */

                unsigned int   channelIndex;     /* index (0~) of the channel used */
                unsigned char* payloadData;      /* bit-stream buffer (NULL means no bit-stream) */
                unsigned int   payloadSize;      /* size of payloadData in bytes (0 means no data) */
                int64_t        pts;              /* presentation time (-1 means N/A) */
                unsigned int   dataOffsetForPTS; /* offset of the bit-stream byte associated with pts */
                unsigned int   infoId;           /* private-info id (can be PRIVATEINFO_NONE) */
                unsigned char* infoData;         /* private-info data buffer */
                unsigned int   infoSize;         /* private-info data size in bytes */

                unsigned long startAddress; /* start address where the input block was read */
                                            /* NOTE: for parser application only, use 0xFFFFFFFF otherwise */

/* ------------ the following lines are to be deleted after new interface adopted --------------*/
                unsigned long  ArrayAddress;  /* for DV App*/
                unsigned char  Array_num;     /* for DV App*/
                unsigned long  Array_size;    /* for DV Video */
                unsigned long  dv_frame_size; /* for DV Video */
/* ---------------------------------------------------------------------------------------------*/
            } data;

            /* if type == NAVBUF_WAIT */
            struct {
                long waitTime; /* in milliseconds */
            } wait;

            /* if type == NAVBUF_STOP or type == NAVBUF_ONEPIN_STOP*/
            struct {
                unsigned int lastChannelIndex; /* index (0~) of the last channel used before stop */
            } stop;

            /* if type == NAVBUF_NEW_SEGMENT or NAVBUF_END_SEGMENT */
            struct {
                unsigned int channelIndex; /* index (0~) of the channel used */
            } segment;

            /* if type == NAVBUF_NEW_MEDIA_TYPE or NAVBUF_NEW_VIDEO_TYPE*/
            struct {
                unsigned int    channelIndex; /* index (0~) of the channel used */
                ENUM_MEDIA_TYPE mediaType;    /* new media type for demux */
            } media;

            /* if type == NAVBUF_NEW_AVSYNC_MODE or
            			NAVBUF_NEW_AVSYNC_MODE_EXT */
            struct {
                NAV_AVSYNC_MODE mode;
                AVSYNC_REASON reason;		//can be accessed only when nav type is NAVBUF_NEW_AVSYNC_MODE_EXT
            } avsync;

            /* if type == NAVBUF_ADJUST_CLOCK */
            struct {
                int __TO_BE_DEFINED__;
            } clock;

            /* if type == NAVBUF_EVENT */
            struct {
                unsigned int   eventId;   /* EVCode */
                unsigned char* eventData; /* event data buffer */
                unsigned int   eventSize; /* event data size in bytes */
            } event;
            
             /* if type == NAVBUF_PREFETCH */
            struct {
                unsigned char*  buff;
                unsigned int    size;
            } prefetch;
        };

    } NAVBUF;

typedef bool (*fpGetAuthInfo)(char* pAccountBuf, int iAccountBufSize, 
                                  char* pPwdBuf, int iPwdBufSize,
                                  int iRertyCounts);

    typedef struct _tagNAVIOCLIPINFO 
    {
        int64_t clipSize;        //in bytes
        int64_t clipDuration;    //in milli seconds
    } NAVIOCLIPINFO;

    typedef struct _tagNAVSUBTITLE
    {
        char* pLanguage;
        char* pURL;
    } NAVSUBTITLE;

    typedef struct _tagNAVSUBTITLEINFO 
    {
        unsigned int iCounts;    // The counts of subtitle information
        NAVSUBTITLE* pSubtitle;  // An array of NAVSUBTITLE.
    } NAVSUBTITLEINFO;

    typedef struct _tagNAVTHUMBNAILINFO 
    {
        unsigned int iCounts;    // The counts of thumbnail
        unsigned int iInterval;  // Thumbnail's interval
        char* pURL;
    } NAVTHUMBNAILINFO;

	typedef enum
	{
		IOPROP_SET_KEEP_READING_REFERENCE = 0,	//[IN] long - an address to the reference. 
												// if value in the address is 0 means stop reading.
												
														
	} IOPLUGIN_PROPERTY_ID;
	

    typedef struct _tagIOPLUGIN {

        /* IO plugin has no need to have any instance.
         * Suppose we should remove this field.
         * It is reserved for any possibility. */
        long pInstance;

        /* open file */
        void* (*open)(char* path, int b_direct_io);

        /* get IO info */
        void (*getIOInfo)(void* pInstance, NAVIOINFO *info);

        /* set blocking/non-blocking mode and time-out (-1 means no time-out and wait forever) */
        void (*setBlockingMode)(void* pInstance, int bBlocking, int timeout);

        /* keep reading */
        int (*read)(void* pInstance, unsigned char* pBuffer, int size, NAVBUF* pEventReceived);

        /* seek to a new position */
        long long (*seek)(void* pInstance, long long offset, int origin);

        /*seek to a new positon by time, for rtsp*/
        long long (*seek1)(void* pInstance, long long t_offset);
        
        /* close file */
        int (*close)(void* pInstance);

        /* dispose this plugin */
        int (*dispose)(void* pInstance);
        
#ifndef DISABLE_IOFULLNESS	// for IO plugin buffer fullness requirement
		// fullness: buffer data size (in byte)
		// return value: 1 -> success, 0 -> fail
		int (*getBufferFullness)(void* pInstance, int* fullness, int* pFlag);		
#endif
        
            /*
             * open another sub stream within an existing IOPlugin,
             * params:
             *      pInstance: the existing IOPlugin, must not be NULL
             *      path: if NULL, the new stream will be the same url with
             *            the original IOPlugin. Otherwise, the new stream
             *            will be based on new path.
             *      b_direct_io: 1 to open as directo IO, 0 otherwise
             *      offset: initial offset of the new open stream.
             * return:
             *      integer >= 0, to be used as fd for getIOInfoN/readN
             *      negative int, indicates error
             */
        int (*openN) (void *pIstance, char * path, int b_direct_io, long long offset);

            /*
             * retrieve the substream information (info seekable etc). 
             * params:
             *       pInstance: the IOPlugin. cannot be NULL
             *       fd: int returned from openN, denoding of the substream.
             */
        void (*getIOInfoN)(void *pIstance, int fd, NAVIOINFO *info);

            /*
             * Seek to an offset in the substream. 
             * params:
             *       pInstance: the IOPlugin. cannot be NULL
             *       fd: int returned from openN, descriptor of the substream.
             *       offset: which offset to seek to
             *       origin: SEEK_END, SEEK_SET etc.
             * return:
             *       the offset of the substream after seek.
             *       negative number means error
             */
        long long (*seekN)(void *pIstance, int fd, long long offset, int origin);
            /*
             * Read from a substream. 
             * params:
             *       pInstance: the IOPlugin. cannot be NULL
             *       fd: int returned from openN, descriptor of the substream.
             *       pBuffer: buffer to store the read
             *       size: length of buffer.
             *       pEventReceived: event received
             * return:
             *       positive number of bytes read
             *       negative number for error
             */
        int (*readN)(void *pIstance, int fd, unsigned char* pBuffer, int size, NAVBUF* pEventReceived);
            /*
             * Close a specific substream.
             * params:
             *       pInstance: the IOPlugin. cannot be NULL
             *       fd: int returned from openN, descriptor of the substream.
             * return:
             *
             * note: calling close on the original IOPlugin will close all
             * sub-streams on the IOPlugin.
             */      
        int (*closeN)(void *pIstance, int fd);

        // Seek to a new position by time.
        //
        //  Parameters: 
        //      1. pInstance: The handle of the IO Plugin.
        //      2. timeOffset: Time offset, time unit is milli-second
        //
        //  Return:
        //      The new position in time, time unit is milli-second
        //
        long long (*seekByTime)(void* pInstance, long long timeOffset);

        // Get specific clip info
        //
        //  Parameters:
        //      1. pInstance: The handle of the IO Plugin.
        //      2. iTimeStamp: An absolute timestamp in milli seconds to indicate which clip I want. 
        //      3. pInfo: Contain clip info when function returned
        //
        //  Return:
        //      true --> if clip is found and info is available.
        //
        bool (*getClipInfo)(void* pInstance, int iTimeStamp, NAVIOCLIPINFO* pClipInfo);
        
        // Get subtitle info
        //
        //  Parameters:
        //      1. pInstance: The handle of the IO Plugin.
        //
        //  Return:
        //      Subtitle information. NOTE: NAVSUBTITLEINFO.pSubtitle is an array of NAVSUBTITLE.
        //      NULL --> Failed.
        //
        NAVSUBTITLEINFO* (*getSubtitleInfo)(void* pInstance);
        
        // Get thumbnail info
        //
        //  Parameters:
        //      1. pInstance: The handle of the IO Plugin.
        //
        //  Return:
        //      Thumbnail information.
        //      NULL --> Failed.
        //
        NAVTHUMBNAILINFO* (*getThumbnailInfo)(void* pInstance);

        // Set the callback function for IO Plug-in to get the authentication information.
        //
        //  Parameters:
        //      1. GetAuthInfo: Function pointer to the callback function.
        //
        // NOTE: YOU SHOULD CALL THIS FUNCTION BEFORE OPENING CONNECTION!!
        // NOTE: When you dose not use this callback(GetAuthInfo) anymore, 
        //       please reset it by calling setAuthCallback(NULL).
        //       IO Plug-in will not reset this callback(GetAuthInfo) for you.
        //
        void (*setAuthCallback)(fpGetAuthInfo GetAuthInfo);
        
        int (*getReadPtr)(void* pInstance, unsigned char** ppBuffer, int size, NAVBUF* pEventReceived);
        
        // Set/Get property to/from io plugin.
        // 
        // Parameters :
        //	1. id - property id
        //	2. pInData - a pointer to an input data buffer
        //	3. inDataSize - how many data in bytes in the input data buffer
        //	4. pOutData - a pointer to an output data buffer
        //	5. outDataSize - total size of output data buffer
        //	6. pReturnSize - used to set the number of data in bytes in output data buffer
        int (*setGetProperty)(void* pInstance, IOPLUGIN_PROPERTY_ID id, void* pInData, int inDataSize, void* pOutData, int outDataSize, int* pReturnSize);
        
    } IOPLUGIN;



static void* http_IOPlugin_open( char* path, int b_direct_io)
{
	return 0;
}

static void http_IOPlugin_getIOInfo(void* pInstance, NAVIOINFO *info)
{
}

static int http_IOPlugin_close(void* pInstance)
{
	return 0;
}


static int http_IOPlugin_read(void* pInstance, unsigned char* pBuffer, int size, NAVBUF* pEventReceived)
{
	return 0;
}

static void  http_IOPlugin_setBlockingMode(void *pInstance, int bBlocking, int timeout)
{
}

static long long http_IOPlugin_seek(void* pInstance, long long offset, int origin)
{
	return 0;
}


static int http_IOPlugin_dispose(void* pInstance)
{
    return 1;
}

static int http_IOPlugin_getBufferFullness(void* pInstance, int* fullness, int* pFlag)
{
	return 0;
}

static int http_IOPlugin_openN(void *pInstance, char* path, int b_direct_io, long long offset) 
{
	return 0;
}

static void http_IOPlugin_getIOInfoN(void *pInstance, int fd, NAVIOINFO *info) 
{
}

static int http_IOPlugin_readN(void* pInstance, int fd, unsigned char* pBuffer, int size, NAVBUF* pEventReceived) 
{
	return 0;
}

static long long http_IOPlugin_seekN(void* pInstance, int fd, long long offset, int origin) 
{
	return 0;
}

static int http_IOPlugin_closeN(void* pInstance, int fd) 
{
	return 0;
}

static void http_IOPlugin_SetAuthCallback(fpGetAuthInfo getAuthInfo)
{
}

const char* http_IOPlugin_Get_Content_Type(void* pInstance)
{
	return 0;
}

unsigned int http_IOPlugin_Get_Stream_Rate(void* pInstance)
{
	return 0;
}

const char* http_IOPlugin_Get_User_Agent(void* pInstance)
{
	return 0;
}

const char* http_IOPlugin_Get_Real_Path(void* pInstance)
{
	return 0;
}


long openIOPlugin_Http(IOPLUGIN* pIOPlugin)
{
    pIOPlugin->pInstance            = 1;
    
    pIOPlugin->open                 = http_IOPlugin_open;
    pIOPlugin->getIOInfo            = http_IOPlugin_getIOInfo;
    pIOPlugin->setBlockingMode      = http_IOPlugin_setBlockingMode;
    pIOPlugin->read                 = http_IOPlugin_read;
    pIOPlugin->seek                 = http_IOPlugin_seek;
    pIOPlugin->close                = http_IOPlugin_close;
    pIOPlugin->dispose              = http_IOPlugin_dispose;

#ifndef DISABLE_IOFULLNESS
    pIOPlugin->getBufferFullness    = http_IOPlugin_getBufferFullness;
#endif

    pIOPlugin->openN                = http_IOPlugin_openN;
    pIOPlugin->getIOInfoN           = http_IOPlugin_getIOInfoN;
    pIOPlugin->readN                = http_IOPlugin_readN;
    pIOPlugin->seekN                = http_IOPlugin_seekN;
    pIOPlugin->closeN               = http_IOPlugin_closeN;

    pIOPlugin->setAuthCallback      = http_IOPlugin_SetAuthCallback;
    return S_OK;
}
	

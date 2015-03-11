#ifndef __WIFI_RTSP_DEFINES_H__
#define __WIFI_RTSP_DEFINES_H__

#include <sys/socket.h>
#include <netinet/in.h>
#ifdef NOT_INCLUDE_TVSERVER
#include "SocketAVDataLocal.h"
#else
#include "SocketAVData.h"
#endif
#include <pthread.h>

#define MIRACAST_WIFI_SOURCE_FILE		"/tmp/RealtekTmp/miracast_wifi_source"
#define MIRACAST_HDCP2_INFO_FILE		"/tmp/RealtekTmp/miracast_hdcp2_info"
#define SIGMA_TRIGGER_FILE				"/tmp/RealtekTmp/sigmatrigger"
#define SIGMA_WFDOPTION_FILE			"/tmp/RealtekTmp/sigma_wfdoption"
#define SIGMA_VIDEOLIST_FILE			"/tmp/RealtekTmp/sigma_videolist"
#define WFDSESSION_FILE					"/tmp/RealtekTmp/wfdsessionid"

// display the current status
enum WIFI_DISPLAY_STATE {
    WIFI_NONE_STATE,
    WIFI_INIT,         // initialized
    WIFI_NEGO,         // nego started in wifi direct
    WIFI_PROVE,        // prove
    WIFI_NEGO_FAIL,    // wifi nego failed
    WIFI_NEGO_SUCCESS, // wifi nego succeeded;
    WIFI_P2P_IPOK,// sink is waiting to Source via RTSP
    WIFI_RTSP_CONNECT, // sink is connected to Source via RTSP
    WIFI_RTSP_STANDBY, // sink in standby
    WIFI_PINCODE, // sink shows pincode
    WIFI_NOCHANGE,//no change 
};

// struct about the source we're in communication with
enum WIFI_SOURCE_STATE {
    WIFI_SOURCE_DISCONNECTED = 0,
    WIFI_SOURCE_IPOK,
    WIFI_SOURCE_CONNECTED,
    WIFI_SOURCE_INIT,
    WIFI_SOURCE_SETUP,
    WIFI_SOURCE_PLAY,
    WIFI_SOURCE_PAUSE,
    WIFI_SOURCE_STANDBY,
    WIFI_SOURCE_TEARDOWN
};

enum WIFI_TRIGGER_METHOD {
	WIFI_TRIGGER_NONE,
	WIFI_TRIGGER_SETUP,
	WIFI_TRIGGER_PLAY,
	WIFI_TRIGGER_PAUSE,
	WIFI_TRIGGER_IDR,
	WIFI_TRIGGER_TEARDOWN,
	WIFI_TRIGGER_STANDBY,
	WIFI_TRIGGER_ROUTEAUDIO,
	WIFI_TRIGGER_UIBC_HID_REMOTE,
	WIFI_TRIGGER_UIBC_GENERIC,
};

struct wifi_source 
{
    enum WIFI_SOURCE_STATE state;
    void (*change_callback)(WIFI_SOURCE_STATE, WIFI_SOURCE_STATE); // when state change
    
    int srcnfd;  // network descriptor, source -> sink
    int uibcfd;  // in case UIBC is supported
    unsigned long intflag;  // what type is used for input (generic or hidc)
    unsigned short uibcport; // UIBC port from source
    struct sockaddr_in address; // source IP addr
        // more parameter about this session after connected with src
    char * presentation_url;
    char * session_id;

	// about the video codec
    bool is_3d;
    long video_w;
    long video_h;
    long fps;
    int  format_3d;   // side by side, or top & bottom

	// about the audio codec
    SOCKET_MEDIA_TYPE audiotype;
    long channelnum;
    long sampleRate;
    long bitspersample;

	// i2c related.
    int i2c_listenport;
    int i2c_dataport;

        // HDCP 2.0
	pthread_t hdcp2_thread_tid;
    int hdcp2_listenport;
    int hdcp2_dataport;

	// RTSP pending method to be initiated
    enum WIFI_TRIGGER_METHOD rtsp_pending_method;

	//Intel WiDi 3.5 only
    char* friendly_name;
    int overscan_comp;
	bool badSocket;
};

#endif

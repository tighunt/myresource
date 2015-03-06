#ifndef __WIFI_RTSP_RTK_H
#define __WIFI_RTSP_RTK_H

#include <iostream>
#include <string>
#include <map>
#include <vector>
#ifdef NOT_INCLUDE_TVSERVER
#include "SocketAVDataLocal.h"
#else
#include "SocketAVData.h"
#endif
#include "wifi_rtsp_defines.h"

//enum for hdcp thread state
enum HDCP_THREAD_STATE {
    EM_HDCP_THREAD_STATE_START,
    EM_HDCP_THREAD_STATE_PENDING,
    EM_HDCP_THREAD_STATE_STOP
};

// functions to be used by extern program
bool wifi_rtsp_init(char* ip, int port, void (*callback)(WIFI_SOURCE_STATE, WIFI_SOURCE_STATE) = NULL);
bool wifi_rtsp_quit();
bool wifi_rtsp_pause();     // send pause from sink, must be in play state
bool wifi_rtsp_play();
bool wifi_rtsp_sendidr();   // send idr, for video recovery
bool wifi_rtsp_teardown();  // 
bool wifi_rtsp_standby(); // standby request


WIFI_SOURCE_STATE wifi_rtsp_state();
void set_wifi_rtsp_state(WIFI_SOURCE_STATE iState);
int is_rtsp_thread_run();
int is_hdcp_thread_run();

#define MOUSE_BUTTON_DOWN              0
#define MOUSE_BUTTON_UP                1
#define MOUSE_MOTION                   2
#define MOUSE_WHEEL		       3

#define MT_DOWN					       4  // MT = Multi Touch Screen
#define MT_UP					       5	
#define MT_MOTION				       6	

#define KEYBOARD_DOWN                  2
#define KEYBOARD_UP                    3
#define INPUT_GENERIC                  0
#define INPUT_HIDC                     1
#define WFD_TIMEOUT                 150
#define POLICE_LOOP_TIME  			   60 // seconds
#define CHK_HDCP1X_LOOP_TIME  		   30 // seconds
void send_keyboard_action(unsigned char type, unsigned short ascii);
void send_mouse_action(unsigned char type, unsigned short x, unsigned short y);
void send_mt_action(unsigned char type, unsigned short pointer_num, unsigned short* x, unsigned short* y);  // Multi Touch
void wifi_uibc_init(void);


void wifi_get_audioinfo(SOCKET_MEDIA_TYPE *atype, long *chan_num, long *sampleRate, long* bitspersample);
void wifi_get_videoinfo(bool *is_3d, long *fps, int *mode3d, long * width, long* height);
#endif

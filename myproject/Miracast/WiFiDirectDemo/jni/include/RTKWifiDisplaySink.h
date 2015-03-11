#ifndef RTK_WIFI_DISPLAY_SINK_H_
#define RTK_WIFI_DISPLAY_SINK_H_

#include "wifi_rtsp.h"

struct RTKWifiDisplaySinkCB {
    RTKWifiDisplaySinkCB();
    virtual ~RTKWifiDisplaySinkCB();
	virtual void OnStateChange(WIFI_SOURCE_STATE oldstate, WIFI_SOURCE_STATE state);
};

struct RTKWifiDisplaySink {
    RTKWifiDisplaySink();
    virtual ~RTKWifiDisplaySink();

	static RTKWifiDisplaySink* GetInstance();
	static void ReleaseInstance();

	bool Init(char* path, int port, RTKWifiDisplaySinkCB* callback);

    bool Start();
	bool Stop();
	bool Pause();
	bool Play();
	
	bool SendIdr();
	int IsRunning();
	WIFI_SOURCE_STATE GetState();
public:
	pthread_mutex_t m_thread_mutex;
	RTKWifiDisplaySinkCB* cb;
	char m_path[512];
	int m_port;
	int m_isRunning;
};

#endif  // RTK_WIFI_DISPLAY_SINK_H_

#include <stdio.h>
#include <pthread.h>
#include "wifi_hdcp2.h"
#include "systemCommand.h"
#include "RTKWifiDisplaySink.h"

#define LOG_TAG "RTKWifiDisplaySink"
#include <utils/Log.h>
#if 0
#define MY_DEBUG(args...)	
#else
#define MY_DEBUG(args...)	ALOGI(args);
//#define MY_DEBUG(args...)	printf(args);
#endif

static RTKWifiDisplaySink* theSink = NULL;

extern "C" void call_back_fcn (WIFI_SOURCE_STATE oldstate, WIFI_SOURCE_STATE state) {
		MY_DEBUG("[baili][%s]\n", __func__);
		if(!theSink || !theSink->cb)
				return;
		theSink->cb->OnStateChange(oldstate, state);
}
//pthread_mutex_t thread_mutex = PTHREAD_MUTEX_INITIALIZER;

/***********************************************************************************/
/*RTKWifiDisplaySinkCB                                                             */
/***********************************************************************************/

RTKWifiDisplaySinkCB::RTKWifiDisplaySinkCB() {
		MY_DEBUG("[baili][%s]\n", __func__);
}

RTKWifiDisplaySinkCB::~RTKWifiDisplaySinkCB() {
		MY_DEBUG("[baili][%s]\n", __func__);
}

void RTKWifiDisplaySinkCB::OnStateChange(WIFI_SOURCE_STATE oldstate, WIFI_SOURCE_STATE state) {
		MY_DEBUG("[baili][%s] [%d => %d]\n", __func__, oldstate, state);
}

/***********************************************************************************/
/*RTKWifiDisplaySink                                                               */
/***********************************************************************************/

RTKWifiDisplaySink::RTKWifiDisplaySink() {
		MY_DEBUG("[baili][%s]\n", __func__);
		m_isRunning = false;
		cb = NULL;
		m_thread_mutex = PTHREAD_MUTEX_INITIALIZER;
		spu_set_thread_mutex(&m_thread_mutex);
		system_init();
}

RTKWifiDisplaySink::~RTKWifiDisplaySink() {
		MY_DEBUG("[baili][%s]\n", __func__);
		system_close();
		m_isRunning = false;
		pthread_mutex_destroy(&m_thread_mutex);
}

RTKWifiDisplaySink* RTKWifiDisplaySink::GetInstance() {
		MY_DEBUG("[baili][%s]\n", __func__);
		if(!theSink) {
				theSink = new RTKWifiDisplaySink();
		}
		return theSink;
}

void RTKWifiDisplaySink::ReleaseInstance() {
		MY_DEBUG("[baili][%s]\n", __func__);
		if(theSink) {
				theSink->Stop();
				while(is_rtsp_thread_run() || is_hdcp_thread_run())
				{
						MY_DEBUG("[baili][%s]waitting rtsp and hdcp thread return.\n", __func__);
						sleep(1);
				}
				delete theSink;
				theSink = NULL;
		}
}

bool RTKWifiDisplaySink::Init(char* path, int port, RTKWifiDisplaySinkCB* callback) {
		MY_DEBUG("[baili][%s]\n", __func__);
		if(cb) {
				return false;
		}
		snprintf(m_path, 512, "%s", path);
		m_port = port;
		cb = callback;
		return true;
}

bool RTKWifiDisplaySink::Start() {
		MY_DEBUG("[baili][%s]\n", __func__);
		if(m_isRunning)
				return true;
		if(wifi_rtsp_init(m_path, m_port, call_back_fcn))
				m_isRunning = true;
		return m_isRunning;
}

bool RTKWifiDisplaySink::Stop() {
		MY_DEBUG("[baili][%s]\n", __func__);
		return wifi_rtsp_quit();
}

bool RTKWifiDisplaySink::Pause(){
		return wifi_rtsp_pause();
}

bool RTKWifiDisplaySink::Play(){
		return wifi_rtsp_play();
}
	
bool RTKWifiDisplaySink::SendIdr() {
		MY_DEBUG("[baili][%s]\n", __func__);
		return wifi_rtsp_sendidr();
}

int RTKWifiDisplaySink::IsRunning() {
		MY_DEBUG("[baili][%s]\n", __func__);
		return is_rtsp_thread_run();
}

WIFI_SOURCE_STATE RTKWifiDisplaySink::GetState() {
		MY_DEBUG("[baili][%s]\n", __func__);
		return wifi_rtsp_state();
}

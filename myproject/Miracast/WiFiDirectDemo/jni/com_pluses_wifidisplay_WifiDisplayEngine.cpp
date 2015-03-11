#include <assert.h>
#include "JNIHelp.h"
#include <jni.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <android/log.h>
#include <android_runtime/AndroidRuntime.h>
#include "JNIHelp.h"
#include "android_os_Parcel.h"
#include "android_util_Binder.h"
#include <binder/Parcel.h>

#define TAG "NativeWifiDisplayEngine"
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
//#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))

// for native media
#include <OMXAL/OpenMAXAL.h>
#include <OMXAL/OpenMAXAL_Android.h>

// for native window JNI
#include <android/native_window_jni.h>

#include "include/MiracastIo.h"
#include "include/RTKWifiDisplaySink.h"
#include "include/WifiDisplayEngine.h"


struct fields_t {
	jfieldID	 context; 
	jmethodID   post_event;	
	JavaVM* gs_jvm;
	};
static fields_t fields;

// engine interfaces
static XAObjectItf engineObject = NULL;
static XAEngineItf engineEngine = NULL;

// output mix interfaces
static XAObjectItf outputMixObject = NULL;

// streaming media player interfaces
static XAObjectItf             playerObj = NULL;
static XAPlayItf               playerPlayItf = NULL;
static XAAndroidBufferQueueItf playerBQItf = NULL;
static XAStreamInformationItf  playerStreamInfoItf = NULL;
static XAVolumeItf             playerVolItf = NULL;

// number of required interfaces for the MediaPlayer creation
#define NB_MAXAL_INTERFACES 3 // XAAndroidBufferQueueItf, XAStreamInformationItf and XAPlayItf

// video sink for the player
static ANativeWindow* theNativeWindow;

// number of buffers in our buffer queue, an arbitrary number
#define NB_BUFFERS 8

// we're streaming MPEG-2 transport stream data, operate on transport stream block size
#define MPEG2_TS_PACKET_SIZE 188

// number of MPEG-2 transport stream blocks per buffer, an arbitrary number
#define PACKETS_PER_BUFFER 10

// determines how much memory we're dedicating to memory caching
#define BUFFER_SIZE (PACKETS_PER_BUFFER*MPEG2_TS_PACKET_SIZE)

// where we cache in memory the data to play
// note this memory is re-used by the buffer queue callback
static char dataCache[BUFFER_SIZE * NB_BUFFERS];

// handle of the file to play
static FILE *file;

// has the app reached the end of the file
static jboolean reachedEof = JNI_FALSE;

// constant to identify a buffer context which is the end of the stream to decode
static const int kEosBufferCntxt = 1980; // a magic value we can compare against

// For mutual exclusion between callback thread and application thread(s).
// The mutex protects reachedEof, discontinuity,
// The condition is signalled when a discontinuity is acknowledged.

static pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;
static pthread_cond_t cond = PTHREAD_COND_INITIALIZER;

// whether a discontinuity is in progress
static jboolean discontinuity = JNI_FALSE;
static int times =0;
static jboolean enqueueInitialBuffers(jboolean discontinuity);
void initPlayer();
void restart();


////
static IOPlugin_Miracast* pIOPlugin;
static int allDataSize = 0;
pthread_t pid;


// ----------------------------------------------------------------------------

// ----------------------------------------------------------------------------

using namespace android;

// ----------------------------------------------------------------------------
// ref-counted object for callbacks
class JNIEventListener: public EventListener
{
public:
    JNIEventListener(JNIEnv* env, jobject thiz, jobject weak_thiz);
    ~JNIEventListener();
    virtual void notify(int msg, int ext1, int ext2, const Parcel *obj = NULL);
private:
    JNIEventListener();
    jclass      mClass;
    jobject     mObject;
};

JNIEventListener::JNIEventListener(JNIEnv* env, jobject thiz, jobject weak_thiz)
{

	jclass clazz = env->GetObjectClass(thiz);	
	if (clazz == NULL)	
	{		
		LOGE("Can't find com/pluses/wifidisplay/WifiDisplayEngine");		
		jniThrowException(env, "java/lang/Exception", NULL);		
		return; 
	}	
	mClass = (jclass)env->NewGlobalRef(clazz);	

	mObject  = env->NewGlobalRef(weak_thiz);
}

JNIEventListener::~JNIEventListener()
{
    // remove global references
    JNIEnv *env = AndroidRuntime::getJNIEnv();
    env->DeleteGlobalRef(mObject);
    env->DeleteGlobalRef(mClass);
}

void JNIEventListener::notify(int msg, int ext1, int ext2, const Parcel *obj)
{
	JNIEnv *env ;
    fields.gs_jvm->AttachCurrentThread(&env, NULL);
	if (obj && obj->dataSize() > 0) 	
	{	
		jobject jParcel = createJavaParcelObject(env);
		if (jParcel != NULL)
		{		
			Parcel* nativeParcel = parcelForJavaObject(env, jParcel);
			nativeParcel->setData(obj->data(), obj->dataSize());
			env->CallStaticVoidMethod(mClass, fields.post_event, mObject,msg, ext1, ext2, jParcel);
			env->DeleteLocalRef(jParcel);
		}
	} else	
	{	
		env->CallStaticVoidMethod(mClass, fields.post_event, mObject,msg, ext1, ext2, NULL);
	}	
	if (env->ExceptionCheck())	
	{	
		LOGE("An exception occurred while notifying an event\n");
		env->ExceptionClear();	
	}
	fields.gs_jvm->DetachCurrentThread();
}

// ----------------------------------------------------------------------------
////
sp<JNIEventListener> listener = NULL;
// ----------------------------------------------------------------------------

using namespace android;

// ----------------------------------------------------------------------------

struct MyCB : public RTKWifiDisplaySinkCB {
		MyCB();
		virtual ~MyCB();
		virtual void OnStateChange(WIFI_SOURCE_STATE oldstate, WIFI_SOURCE_STATE state);
		//private:
		//RTMediaPlayer* m_player;
};

MyCB::MyCB()
{
    LOGE("[baili][%s][%s][%d]\n", __FILE__, __func__, __LINE__);
    //m_player = player;
}

MyCB::~MyCB()
{
    LOGV("[baili][%s][%s][%d]\n", __FILE__, __func__, __LINE__);
    //m_player=NULL;
}

void MyCB::OnStateChange(WIFI_SOURCE_STATE oldstate, WIFI_SOURCE_STATE state)
{
    LOGV("[baili][%s] [%d => %d]\n", __func__, oldstate, state);
    if (state == 8)
    {
        //sleep(20);
        //m_player->sendEvent(MEDIA_PLAYBACK_COMPLETE);
        LOGV("[baili] player send MEDIA_PLAYBACK_COMPLETE");
    }
}

void PlayEventCallback (
	XAPlayItf caller,
	void * pContext,
	XAuint32 playevent)
{
/* Callback code goes here */
	LOGE("PlayEventCallback event = %d\n",playevent);


    if (listener != 0) {
        listener->notify(playevent, 0, 0);
    }
    if(playevent == 1)
		restart();
}

void *thread_get_data(void *arg){
	LOGE("thread_get_data start\n");
	
	while(1)
	{
		int bytesRead;
    	bytesRead = Miracast_IO_read(pIOPlugin, (unsigned char*)dataCache, BUFFER_SIZE, NULL);
		usleep(1000);
	}
	LOGE("thread_get_data end\n");
	return ((void *)0); 
}


void restart()
{
	/*LOGE("restart playback\n");

	 // clear the buffer queue
    (*playerBQItf)->Clear(playerBQItf);
    (*playerPlayItf)->SetPlayState(playerPlayItf,XA_PLAYSTATE_STOPPED);
    if (playerObj != NULL) {
		LOGE("playerObj  destory start \n");
        (*playerObj)->Destroy(playerObj);
		LOGE("playerObj  destory end \n");
        playerObj = NULL;
        playerPlayItf = NULL;
        playerBQItf = NULL;
        playerStreamInfoItf = NULL;
        playerVolItf = NULL;
    }
	LOGE("reinit player\n");
	initPlayer();*/

	if (pthread_create(&pid,NULL,thread_get_data,NULL) != 0){
        LOGE("pthread_create fail\n");
    }

}

// AndroidBufferQueueItf callback to supply MPEG-2 TS packets to the media player
static XAresult AndroidBufferQueueCallback(
        XAAndroidBufferQueueItf caller,
        void *pCallbackContext,        /* input */
        void *pBufferContext,          /* input */
        void *pBufferData,             /* input */
        XAuint32 dataSize,             /* input */
        XAuint32 dataUsed,             /* input */
        const XAAndroidBufferItem *pItems,/* input */
        XAuint32 itemsLength           /* input */)
{
    XAresult res;
    int ok;

    // pCallbackContext was specified as NULL at RegisterCallback and is unused here
    assert(NULL == pCallbackContext);

    // note there is never any contention on this mutex unless a discontinuity request is active
    ok = pthread_mutex_lock(&mutex);
    assert(0 == ok);
    // was a discontinuity requested?
    if (discontinuity) {
        // Note: can't rewind after EOS, which we send when reaching EOF
        // (don't send EOS if you plan to play more content through the same player)
        if (!reachedEof) {
            // clear the buffer queue
            res = (*playerBQItf)->Clear(playerBQItf);
            assert(XA_RESULT_SUCCESS == res);
            // rewind the data source so we are guaranteed to be at an appropriate point
            rewind(file);
            // Enqueue the initial buffers, with a discontinuity indicator on first buffer
            (void) enqueueInitialBuffers(JNI_TRUE);
        }
        // acknowledge the discontinuity request
        discontinuity = JNI_FALSE;
        ok = pthread_cond_signal(&cond);
        assert(0 == ok);
        goto exit;
    }

    if ((pBufferData == NULL) && (pBufferContext != NULL)) {
        const int processedCommand = *(int *)pBufferContext;
        if (kEosBufferCntxt == processedCommand) {
            LOGV("EOS was processed\n");
            // our buffer with the EOS message has been consumed
            assert(0 == dataSize);
            goto exit;
        }
    }

    // pBufferData is a pointer to a buffer that we previously Enqueued
    assert((dataSize > 0) && ((dataSize % MPEG2_TS_PACKET_SIZE) == 0));
    assert(dataCache <= (char *) pBufferData && (char *) pBufferData <
            &dataCache[BUFFER_SIZE * NB_BUFFERS]);
    assert(0 == (((char *) pBufferData - dataCache) % BUFFER_SIZE));

    // don't bother trying to read more data once we've hit EOF
    if (reachedEof) {
        goto exit;
    }

    //size_t nbRead;
    // note we do call fread from multiple threads, but never concurrently
    //size_t bytesRead;
    //bytesRead = fread(pBufferData, 1, BUFFER_SIZE, file);
    int bytesRead;
    bytesRead = Miracast_IO_read(pIOPlugin, (unsigned char*)pBufferData, BUFFER_SIZE, NULL);
    		//fread(dataCache, 1, BUFFER_SIZE * NB_BUFFERS, file);
    while(bytesRead<0){
    	LOGE(" AndroidBufferQueueCallback bytesRead %d bytes", bytesRead);
    	//usleep(1000*10);//10ms
    	bytesRead = Miracast_IO_read(pIOPlugin, (unsigned char*)pBufferData, BUFFER_SIZE, NULL);
    }
    LOGE("AndroidBufferQueueCallback bytesRead %d bytes", bytesRead);
    if (bytesRead > 0) {
    	allDataSize += bytesRead;
        if ((bytesRead % MPEG2_TS_PACKET_SIZE) != 0) {
            LOGV("Dropping last packet because it is not whole");
        }
        size_t packetsRead = bytesRead / MPEG2_TS_PACKET_SIZE;
        size_t bufferSize = packetsRead * MPEG2_TS_PACKET_SIZE;
        res = (*caller)->Enqueue(caller, NULL /*pBufferContext*/,
                pBufferData /*pData*/,
                bufferSize /*dataLength*/,
                NULL /*pMsg*/,
                0 /*msgLength*/);
        assert(XA_RESULT_SUCCESS == res);
    } else {
    	LOGE("get all data with size = %d",allDataSize);
        // EOF or I/O error, signal EOS
        XAAndroidBufferItem msgEos[1];
        msgEos[0].itemKey = XA_ANDROID_ITEMKEY_EOS;
        msgEos[0].itemSize = 0;
        // EOS message has no parameters, so the total size of the message is the size of the key
        //   plus the size if itemSize, both XAuint32
        res = (*caller)->Enqueue(caller, (void *)&kEosBufferCntxt /*pBufferContext*/,
                NULL /*pData*/, 0 /*dataLength*/,
                msgEos /*pMsg*/,
                sizeof(XAuint32)*2 /*msgLength*/);
        assert(XA_RESULT_SUCCESS == res);
        reachedEof = JNI_TRUE;
    }

exit:
    ok = pthread_mutex_unlock(&mutex);
    assert(0 == ok);
    return XA_RESULT_SUCCESS;
}


// callback invoked whenever there is new or changed stream information
static void StreamChangeCallback(XAStreamInformationItf caller,
        XAuint32 eventId,
        XAuint32 streamIndex,
        void * pEventData,
        void * pContext )
{
    LOGV("StreamChangeCallback called for stream %u", streamIndex);
    // pContext was specified as NULL at RegisterStreamChangeCallback and is unused here
    assert(NULL == pContext);
    switch (eventId) {
      case XA_STREAMCBEVENT_PROPERTYCHANGE: {
        /** From spec 1.0.1:
            "This event indicates that stream property change has occurred.
            The streamIndex parameter identifies the stream with the property change.
            The pEventData parameter for this event is not used and shall be ignored."
         */

        XAresult res;
        XAuint32 domain;
        res = (*caller)->QueryStreamType(caller, streamIndex, &domain);
        assert(XA_RESULT_SUCCESS == res);
        switch (domain) {
          case XA_DOMAINTYPE_VIDEO: {
            XAVideoStreamInformation videoInfo;
            res = (*caller)->QueryStreamInformation(caller, streamIndex, &videoInfo);
            assert(XA_RESULT_SUCCESS == res);
            LOGV("Found video size %u x %u, codec ID=%u, frameRate=%u, bitRate=%u, duration=%u ms",
                        videoInfo.width, videoInfo.height, videoInfo.codecId, videoInfo.frameRate,
                        videoInfo.bitRate, videoInfo.duration);
          } break;
          default:
            fprintf(stderr, "Unexpected domain %u\n", domain);
            break;
        }
      } break;
      default:
        fprintf(stderr, "Unexpected stream event ID %u\n", eventId);
        break;
    }
}

// Enqueue the initial buffers, and optionally signal a discontinuity in the first buffer
static jboolean enqueueInitialBuffers(jboolean discontinuity)
{

    /* Fill our cache.
     * We want to read whole packets (integral multiples of MPEG2_TS_PACKET_SIZE).
     * fread returns units of "elements" not bytes, so we ask for 1-byte elements
     * and then check that the number of elements is a multiple of the packet size.
     */
    int bytesRead;
    int times = 0;
    bytesRead = Miracast_IO_read(pIOPlugin, (unsigned char*)dataCache, BUFFER_SIZE*NB_BUFFERS, NULL);
    		//fread(dataCache, 1, BUFFER_SIZE * NB_BUFFERS, file);
    if(bytesRead>0){
    	times ++;
    }
    while(bytesRead<0 && times<10){
    	LOGE("bytesRead %d bytes", bytesRead);
    	usleep(1000*10);
    	bytesRead = Miracast_IO_read(pIOPlugin, (unsigned char*)dataCache, BUFFER_SIZE*NB_BUFFERS, NULL);
        if(bytesRead>0){
        	times ++;
        }
    }
    LOGE("bytesRead %d bytes", bytesRead);
    allDataSize = bytesRead;
    if (bytesRead <= 0) {
        // could be premature EOF or I/O error
        return JNI_FALSE;
    }

    if ((bytesRead % MPEG2_TS_PACKET_SIZE) != 0) {
        LOGV("Dropping last packet because it is not whole");
    }
    size_t packetsRead = bytesRead / MPEG2_TS_PACKET_SIZE;
    LOGV("Initially queueing %u packets", packetsRead);

    /* Enqueue the content of our cache before starting to play,
       we don't want to starve the player */
    size_t i;
    for (i = 0; i < NB_BUFFERS && packetsRead > 0; i++) {
        // compute size of this buffer
        size_t packetsThisBuffer = packetsRead;
        if (packetsThisBuffer > PACKETS_PER_BUFFER) {
            packetsThisBuffer = PACKETS_PER_BUFFER;
        }
        size_t bufferSize = packetsThisBuffer * MPEG2_TS_PACKET_SIZE;
        XAresult res;
        if (discontinuity) {
            // signal discontinuity
            XAAndroidBufferItem items[1];
            items[0].itemKey = XA_ANDROID_ITEMKEY_DISCONTINUITY;
            items[0].itemSize = 0;
            // DISCONTINUITY message has no parameters,
            //   so the total size of the message is the size of the key
            //   plus the size if itemSize, both XAuint32
            res = (*playerBQItf)->Enqueue(playerBQItf, NULL /*pBufferContext*/,
                    dataCache + i*BUFFER_SIZE, bufferSize, items /*pMsg*/,
                    sizeof(XAuint32)*2 /*msgLength*/);
            discontinuity = JNI_FALSE;
        } else {
            res = (*playerBQItf)->Enqueue(playerBQItf, NULL /*pBufferContext*/,
                    dataCache + i*BUFFER_SIZE, bufferSize, NULL, 0);
        }
        assert(XA_RESULT_SUCCESS == res);
        packetsRead -= packetsThisBuffer;
    }

    return JNI_TRUE;
}

// create the engine and output mix objects
static void com_pluses_wifidisplay_WifiDisplayEngine_nativeInit(JNIEnv* env, jclass clazz)
{
    XAresult res;
    // create engine
    res = xaCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
    assert(XA_RESULT_SUCCESS == res);

    // realize the engine
    res = (*engineObject)->Realize(engineObject, XA_BOOLEAN_FALSE);
    assert(XA_RESULT_SUCCESS == res);

    // get the engine interface, which is needed in order to create other objects
    res = (*engineObject)->GetInterface(engineObject, XA_IID_ENGINE, &engineEngine);
    assert(XA_RESULT_SUCCESS == res);

    // create output mix
    res = (*engineEngine)->CreateOutputMix(engineEngine, &outputMixObject, 0, NULL, NULL);
    assert(XA_RESULT_SUCCESS == res);

    // realize the output mix
    res = (*outputMixObject)->Realize(outputMixObject, XA_BOOLEAN_FALSE);
    assert(XA_RESULT_SUCCESS == res);
}

//
static void com_pluses_wifidisplay_WifiDisplayEngine_nativeDeinit(JNIEnv* env, jclass clazz)
{
    // destroy streaming media player object, and invalidate all associated interfaces
    if (playerObj != NULL) {
        (*playerObj)->Destroy(playerObj);
        playerObj = NULL;
        playerPlayItf = NULL;
        playerBQItf = NULL;
        playerStreamInfoItf = NULL;
        playerVolItf = NULL;
    }

    // destroy output mix object, and invalidate all associated interfaces
    if (outputMixObject != NULL) {
        (*outputMixObject)->Destroy(outputMixObject);
        outputMixObject = NULL;
    }

    // destroy engine object, and invalidate all associated interfaces
    if (engineObject != NULL) {
        (*engineObject)->Destroy(engineObject);
        engineObject = NULL;
        engineEngine = NULL;
    }

    // close the file
    if (file != NULL) {
        fclose(file);
        file = NULL;
    }
    Miracast_IO_close(pIOPlugin);
    // make sure we don't leak native windows
    if (theNativeWindow != NULL) {
        ANativeWindow_release(theNativeWindow);
        theNativeWindow = NULL;
    }
}

void initPlayer()
{
	XAresult res;

	LOGV("Test palyer 3\n");
	// configure data source
	XADataLocator_AndroidBufferQueue loc_abq = { XA_DATALOCATOR_ANDROIDBUFFERQUEUE, NB_BUFFERS };
	XADataFormat_MIME format_mime = {
			XA_DATAFORMAT_MIME, XA_ANDROID_MIME_MP2TS, XA_CONTAINERTYPE_MPEG_TS };
	XADataSource dataSrc = {&loc_abq, &format_mime};
	
	// configure audio sink
	XADataLocator_OutputMix loc_outmix = { XA_DATALOCATOR_OUTPUTMIX, outputMixObject };
	XADataSink audioSnk = { &loc_outmix, NULL };
	LOGV("Test palyer 4\n");
	// configure image video sink
	XADataLocator_NativeDisplay loc_nd = {
			XA_DATALOCATOR_NATIVEDISPLAY,		 // locatorType
			// the video sink must be an ANativeWindow created from a Surface or SurfaceTexture
			(void*)theNativeWindow, 			 // hWindow
			// must be NULL
			NULL								 // hDisplay
	};
	XADataSink imageVideoSink = {&loc_nd, NULL};
	
	// declare interfaces to use
	XAboolean	  required[NB_MAXAL_INTERFACES]
						   = {XA_BOOLEAN_TRUE, XA_BOOLEAN_TRUE, 		  XA_BOOLEAN_TRUE};
	XAInterfaceID iidArray[NB_MAXAL_INTERFACES]
						   = {XA_IID_PLAY,	   XA_IID_ANDROIDBUFFERQUEUESOURCE,
											   XA_IID_STREAMINFORMATION};
	LOGV("Test palyer 5\n");
	// create media player
	res = (*engineEngine)->CreateMediaPlayer(engineEngine, &playerObj, &dataSrc,
			NULL, &audioSnk, &imageVideoSink, NULL, NULL,
			NB_MAXAL_INTERFACES /*XAuint32 numInterfaces*/,
			iidArray /*const XAInterfaceID *pInterfaceIds*/,
			required /*const XAboolean *pInterfaceRequired*/);
	assert(XA_RESULT_SUCCESS == res);
	
	// realize the player
	res = (*playerObj)->Realize(playerObj, XA_BOOLEAN_FALSE);
	assert(XA_RESULT_SUCCESS == res);
	
	// get the play interface
	res = (*playerObj)->GetInterface(playerObj, XA_IID_PLAY, &playerPlayItf);
	assert(XA_RESULT_SUCCESS == res);
	LOGV("Test palyer 6");
	// get the stream information interface (for video size)
	res = (*playerObj)->GetInterface(playerObj, XA_IID_STREAMINFORMATION, &playerStreamInfoItf);
	assert(XA_RESULT_SUCCESS == res);
	
	// get the volume interface
	res = (*playerObj)->GetInterface(playerObj, XA_IID_VOLUME, &playerVolItf);
	assert(XA_RESULT_SUCCESS == res);
	
	// get the Android buffer queue interface
	res = (*playerObj)->GetInterface(playerObj, XA_IID_ANDROIDBUFFERQUEUESOURCE, &playerBQItf);
	assert(XA_RESULT_SUCCESS == res);
	
	// specify which events we want to be notified of
	res = (*playerBQItf)->SetCallbackEventsMask(playerBQItf, XA_ANDROIDBUFFERQUEUEEVENT_PROCESSED);
	assert(XA_RESULT_SUCCESS == res);
	
	// register the callback from which OpenMAX AL can retrieve the data to play
	res = (*playerBQItf)->RegisterCallback(playerBQItf, AndroidBufferQueueCallback, NULL);
	assert(XA_RESULT_SUCCESS == res);
	
	// we want to be notified of the video size once it's found, so we register a callback for that
	res = (*playerStreamInfoItf)->RegisterStreamChangeCallback(playerStreamInfoItf,
			StreamChangeCallback, NULL);
	assert(XA_RESULT_SUCCESS == res);
	LOGV("Test palyer 7\n");
	
	// enqueue the initial buffers
	if (!enqueueInitialBuffers(JNI_FALSE)) {
		LOGE("enqueueInitialBuffers fail \n");
		return ;
	}
	
	// prepare the player
	res = (*playerPlayItf)->SetPlayState(playerPlayItf, XA_PLAYSTATE_PAUSED);
	assert(XA_RESULT_SUCCESS == res);
	
	/* Setup to receive position event callbacks */
	res = (*playerPlayItf)->RegisterCallback(playerPlayItf,
	PlayEventCallback, NULL); 
	assert(XA_RESULT_SUCCESS == res);
	res = (*playerPlayItf)->SetPositionUpdatePeriod(playerPlayItf,
	1000);//1000ms
	res = (*playerPlayItf)->SetCallbackEventsMask(playerPlayItf,
	XA_PLAYEVENT_HEADATEND|XA_PLAYEVENT_HEADATMARKER|XA_PLAYEVENT_HEADATNEWPOS|XA_PLAYEVENT_HEADMOVING|XA_PLAYEVENT_HEADSTALLED);
	assert(XA_RESULT_SUCCESS == res);
	
	
	// set the volume
	res = (*playerVolItf)->SetVolumeLevel(playerVolItf, 0);
	assert(XA_RESULT_SUCCESS == res);
	LOGV("Test palyer 8\n");
	// start the playback
	res = (*playerPlayItf)->SetPlayState(playerPlayItf, XA_PLAYSTATE_PLAYING);
		assert(XA_RESULT_SUCCESS == res);

}
// create streaming media player
static jboolean com_pluses_wifidisplay_WifiDisplayEngine_nativeInitPlayer(JNIEnv* env,
        jclass clazz, jstring filename)
{
    XAresult res;

    // convert Java string to UTF-8
    //const char *utf8 = (*env)->GetStringUTFChars(env, filename, NULL);
    char const * utf8 = (env)->GetStringUTFChars(filename, NULL);
    assert(NULL != utf8);

    // open the file to play
    //file = fopen(utf8, "rb");
    //if (file == NULL) {
       // return JNI_FALSE;
    //}
    LOGV("Test palyer 1");
    char tmp[512];
    int port;
    char* p = strstr(utf8+11, ":");
    if (p)
    {
        snprintf(tmp, p-utf8-11+1, "%s", utf8+11);
        port = atoi(p+1);
        LOGV("[baili][miracast]path: %s port: %d\n", tmp, port);
        MyCB* cb = new MyCB();
        RTKWifiDisplaySink* sink = RTKWifiDisplaySink::GetInstance();
        sink->Init(tmp, port, cb);
        sink->Start();
        //m_wfdCB = cb;
        //snprintf(path, 1024, "%s", "miracast://tmp/MediaData?container=ts");
    }

////
    LOGV("Test palyer 2");
    pIOPlugin = (IOPlugin_Miracast*)Miracast_IO_open();
    if(pIOPlugin == NULL)
    	return JNI_FALSE;
////
    LOGV("Test palyer 3");
    // configure data source
    XADataLocator_AndroidBufferQueue loc_abq = { XA_DATALOCATOR_ANDROIDBUFFERQUEUE, NB_BUFFERS };
    XADataFormat_MIME format_mime = {
            XA_DATAFORMAT_MIME, XA_ANDROID_MIME_MP2TS, XA_CONTAINERTYPE_MPEG_TS };
    XADataSource dataSrc = {&loc_abq, &format_mime};

    // configure audio sink
    XADataLocator_OutputMix loc_outmix = { XA_DATALOCATOR_OUTPUTMIX, outputMixObject };
    XADataSink audioSnk = { &loc_outmix, NULL };
    LOGV("Test palyer 4");
    // configure image video sink
    XADataLocator_NativeDisplay loc_nd = {
            XA_DATALOCATOR_NATIVEDISPLAY,        // locatorType
            // the video sink must be an ANativeWindow created from a Surface or SurfaceTexture
            (void*)theNativeWindow,              // hWindow
            // must be NULL
            NULL                                 // hDisplay
    };
    XADataSink imageVideoSink = {&loc_nd, NULL};

    // declare interfaces to use
    XAboolean     required[NB_MAXAL_INTERFACES]
                           = {XA_BOOLEAN_TRUE, XA_BOOLEAN_TRUE,           XA_BOOLEAN_TRUE};
    XAInterfaceID iidArray[NB_MAXAL_INTERFACES]
                           = {XA_IID_PLAY,     XA_IID_ANDROIDBUFFERQUEUESOURCE,
                                               XA_IID_STREAMINFORMATION};
    LOGV("Test palyer 5");
    // create media player
    res = (*engineEngine)->CreateMediaPlayer(engineEngine, &playerObj, &dataSrc,
            NULL, &audioSnk, &imageVideoSink, NULL, NULL,
            NB_MAXAL_INTERFACES /*XAuint32 numInterfaces*/,
            iidArray /*const XAInterfaceID *pInterfaceIds*/,
            required /*const XAboolean *pInterfaceRequired*/);
    assert(XA_RESULT_SUCCESS == res);

    // release the Java string and UTF-8
    //(*env)->ReleaseStringUTFChars(env, filename, utf8);
    env->ReleaseStringUTFChars(filename, utf8);
    // realize the player
    res = (*playerObj)->Realize(playerObj, XA_BOOLEAN_FALSE);
    assert(XA_RESULT_SUCCESS == res);

    // get the play interface
    res = (*playerObj)->GetInterface(playerObj, XA_IID_PLAY, &playerPlayItf);
    assert(XA_RESULT_SUCCESS == res);
    LOGV("Test palyer 6");
    // get the stream information interface (for video size)
    res = (*playerObj)->GetInterface(playerObj, XA_IID_STREAMINFORMATION, &playerStreamInfoItf);
    assert(XA_RESULT_SUCCESS == res);

    // get the volume interface
    res = (*playerObj)->GetInterface(playerObj, XA_IID_VOLUME, &playerVolItf);
    assert(XA_RESULT_SUCCESS == res);

    // get the Android buffer queue interface
    res = (*playerObj)->GetInterface(playerObj, XA_IID_ANDROIDBUFFERQUEUESOURCE, &playerBQItf);
    assert(XA_RESULT_SUCCESS == res);

    // specify which events we want to be notified of
    res = (*playerBQItf)->SetCallbackEventsMask(playerBQItf, XA_ANDROIDBUFFERQUEUEEVENT_PROCESSED);
    assert(XA_RESULT_SUCCESS == res);

    // register the callback from which OpenMAX AL can retrieve the data to play
    res = (*playerBQItf)->RegisterCallback(playerBQItf, AndroidBufferQueueCallback, NULL);
    assert(XA_RESULT_SUCCESS == res);

    // we want to be notified of the video size once it's found, so we register a callback for that
    res = (*playerStreamInfoItf)->RegisterStreamChangeCallback(playerStreamInfoItf,
            StreamChangeCallback, NULL);
    assert(XA_RESULT_SUCCESS == res);
    LOGV("Test palyer 7");

    // enqueue the initial buffers
    if (!enqueueInitialBuffers(JNI_FALSE)) {
        return JNI_FALSE;
    }

    // prepare the player
    res = (*playerPlayItf)->SetPlayState(playerPlayItf, XA_PLAYSTATE_PAUSED);
    assert(XA_RESULT_SUCCESS == res);

	/* Setup to receive position event callbacks */
	res = (*playerPlayItf)->RegisterCallback(playerPlayItf,
	PlayEventCallback, NULL); 
	assert(XA_RESULT_SUCCESS == res);
	res = (*playerPlayItf)->SetPositionUpdatePeriod(playerPlayItf,
	1000);//1000ms
	res = (*playerPlayItf)->SetCallbackEventsMask(playerPlayItf,
	XA_PLAYEVENT_HEADATEND|XA_PLAYEVENT_HEADATMARKER|XA_PLAYEVENT_HEADATNEWPOS|XA_PLAYEVENT_HEADMOVING|XA_PLAYEVENT_HEADSTALLED);
	assert(XA_RESULT_SUCCESS == res);
	

    // set the volume
    res = (*playerVolItf)->SetVolumeLevel(playerVolItf, 0);
    assert(XA_RESULT_SUCCESS == res);
    LOGV("Test palyer 8");
    // start the playback
    res = (*playerPlayItf)->SetPlayState(playerPlayItf, XA_PLAYSTATE_PLAYING);
        assert(XA_RESULT_SUCCESS == res);
    return JNI_TRUE;
}

//
static void com_pluses_wifidisplay_WifiDisplayEngine_nativeStart(JNIEnv* env, jclass clazz)
{
    LOGV("nativeStart start");
    XAresult res;
    res = (*playerPlayItf)->SetPlayState(playerPlayItf, XA_PLAYSTATE_PLAYING);
    assert(XA_RESULT_SUCCESS == res);
    LOGV("nativeStart end");
}

static void com_pluses_wifidisplay_WifiDisplayEngine_nativePause(JNIEnv* env, jclass clazz)
{
    XAresult res;
    res = (*playerPlayItf)->SetPlayState(playerPlayItf, XA_PLAYSTATE_PAUSED);
    assert(XA_RESULT_SUCCESS == res);
}

static void com_pluses_wifidisplay_WifiDisplayEngine_nativeStop(JNIEnv* env, jclass clazz)
{
	com_pluses_wifidisplay_WifiDisplayEngine_nativeDeinit(env, clazz);
}

// set the surface
static void com_pluses_wifidisplay_WifiDisplayEngine_nativeSetSurface(JNIEnv *env, jclass clazz, jobject surface)
{
    // obtain a native window from a Java surface
    theNativeWindow = ANativeWindow_fromSurface(env, surface);
}

static void
com_pluses_wifidisplay_WifiDisplayEngine_native_init(JNIEnv *jenv)
{
    jclass clazz;

    clazz = jenv->FindClass("com/pluses/wifidisplay/WifiDisplayEngine");
    if (clazz == NULL) {
        return;
    }
	
	fields.context = jenv->GetFieldID(clazz, "mNativeContext", "J"); 
	if (fields.context == NULL) 	
	{	
		LOGE("fields.context = null\n");
		return; 
	}		
	fields.post_event = jenv->GetStaticMethodID(clazz, "postEventFromNative",											  
												"(Ljava/lang/Object;IIILjava/lang/Object;)V");	 
	if (fields.post_event == NULL) 	
	{		
		LOGE("fields.post_even = null\n");
		return;	
	}

	// Set the virtual machine.
    jenv->GetJavaVM(&(fields.gs_jvm));

}

static void
com_pluses_wifidisplay_WifiDisplayEngine_native_setup(JNIEnv *env, jobject thiz, jobject weak_this)
{
      listener = new JNIEventListener(env, thiz, weak_this);
}

static JNINativeMethod gMethods[] = {
		{"nativeInit",         "()V",                              (void *)com_pluses_wifidisplay_WifiDisplayEngine_nativeInit},
		{"nativeDeinit",         "()V",                              (void *)com_pluses_wifidisplay_WifiDisplayEngine_nativeDeinit},
		{"nativeStart",         "()V",                              (void *)com_pluses_wifidisplay_WifiDisplayEngine_nativeStart},
		{"nativeStop",         "()V",                              (void *)com_pluses_wifidisplay_WifiDisplayEngine_nativeStop},
		{"nativePause",         "()V",                              (void *)com_pluses_wifidisplay_WifiDisplayEngine_nativePause},
		{"nativeInitPlayer",  "(Ljava/lang/String;)Z",            (void *)com_pluses_wifidisplay_WifiDisplayEngine_nativeInitPlayer},
		{"nativeSetSurface",    "(Landroid/view/Surface;)V",        (void *)com_pluses_wifidisplay_WifiDisplayEngine_nativeSetSurface},
                {"native_init",         "()V",                              (void *)com_pluses_wifidisplay_WifiDisplayEngine_native_init},
                {"native_setup",        "(Ljava/lang/Object;)V",            (void *)com_pluses_wifidisplay_WifiDisplayEngine_native_setup},
};

static const char* const kClassPathName = "com/pluses/wifidisplay/WifiDisplayEngin";

static int RegisterNativeMethods(JNIEnv* env, const char* className,
JNINativeMethod* gMethods,int numMethods)
{
    jclass clazz;
    clazz = env->FindClass(className);

    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0)
    {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}
// This function only registers the native methods
static int register_com_pluses_wifidisplay_WifiDisplayEngine(JNIEnv *env)
{
    return AndroidRuntime::registerNativeMethods(env,
                "com/pluses/wifidisplay/WifiDisplayEngine", gMethods, NELEM(gMethods));
}

jint JNI_OnLoad(JavaVM* vm, void* /* reserved */)
{
    JNIEnv* env = NULL;
    jint result = -1;

    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        LOGE("ERROR: GetEnv failed\n");
        goto bail;
    }
    assert(env != NULL);



    if (register_com_pluses_wifidisplay_WifiDisplayEngine(env) < 0) {
        LOGE("ERROR: WifiDisplayEngine native registration failed\n");
        goto bail;
    }

    /* success -- return valid version number */
    result = JNI_VERSION_1_4;

bail:
    return result;
}

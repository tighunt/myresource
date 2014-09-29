/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* This is a JNI example where we use native methods to play video
 * using OpenMAX AL. See the corresponding Java source file located at:
 *
 *   src/com/example/nativemedia/NativeMedia/NativeMedia.java
 *
 * In this example we use assert() for "impossible" error conditions,
 * and explicit handling and recovery for more likely error conditions.
 */

#include <assert.h>
#include <jni.h>
#include <pthread.h>
#include <stdio.h>
#include <string.h>

#include "curl/curl.h"
#include "curl/easy.h"

// for __android_log_print(ANDROID_LOG_INFO, "YourApp", "formatted message");
#include <android/log.h>
#define TAG "NativeMedia"
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

// for native media
#include <OMXAL/OpenMAXAL.h>
#include <OMXAL/OpenMAXAL_Android.h>

// for native window JNI
#include <android/native_window_jni.h>

///remove static
// engine interfaces
XAObjectItf engineObject = NULL;
XAEngineItf engineEngine = NULL;

// output mix interfaces
XAObjectItf outputMixObject = NULL;

// streaming media player interfaces
XAObjectItf playerObj = NULL;
XAPlayItf playerPlayItf = NULL;
XAAndroidBufferQueueItf playerBQItf = NULL;
XAStreamInformationItf playerStreamInfoItf = NULL;
XAVolumeItf playerVolItf = NULL;
JNIEnv* env = NULL;

JavaVM* m_pJVM;
jclass m_ClassQueryMedia;

/////end remove

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

char* files[100];
int fileIndex = 0;
// has the app reached the end of the file
static jboolean reachedEof = JNI_FALSE;

static int filenum = 0;

// constant to identify a buffer context which is the end of the stream to decode
static const int kEosBufferCntxt = 1980; // a magic value we can compare against

// For mutual exclusion between callback thread and application thread(s).
// The mutex protects reachedEof, discontinuity,
// The condition is signalled when a discontinuity is acknowledged.

static pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;
static pthread_cond_t cond = PTHREAD_COND_INITIALIZER;

static pthread_mutex_t mutex_rw = PTHREAD_MUTEX_INITIALIZER;

pthread_t httpThd = 0;

// whether a discontinuity is in progress
static jboolean discontinuity = JNI_FALSE;

jboolean canread = JNI_FALSE;

static jboolean enqueueInitialBuffers(jboolean discontinuity);

/*****************************curl test*********************************/

typedef struct pageInfo_t {
	char *data;
	int len;
} pageInfo_t;
static size_t HTTPData(void *buffer, size_t size, size_t nmemb, void *userData) {
	int len = size * nmemb;
	pageInfo_t *page = (pageInfo_t *) userData;
	int lenth = 0;
	LOGE("buffer:0x%x", buffer);
	if (buffer) {
		//memcpy(&page->data[page->len], buffer, len);
		//page->len += len;
		while (space() < len) {
			LOGE("space is full ,waitting");
			sleep(2);
		}
		lenth = write(buffer, (unsigned int) len);
	}
	LOGE("lenth1=%d , lenth2= %d", len, lenth);
	return len;
}

void curltest() {
	LOGE("curltest() funs");
	pageInfo_t page;
	CURL *curl;
	CURLcode res;

	page.data = (char *) malloc(1000 * 188);
	page.len = 0;
	if (page.data)
		memset(page.data, 0, 1000 * 188);

	curl = curl_easy_init();
	if (curl) {
		while (1) {
			if (fileIndex >= filenum) {
				int ok;
				ok = pthread_mutex_lock(&mutex_rw);
				assert(0 == ok);
				canread == JNI_FALSE;
				ok = pthread_mutex_unlock(&mutex);
				assert(0 == ok);
				break;
			}
			LOGE("file http %s", files[fileIndex]);
			curl_easy_setopt(curl, CURLOPT_URL, files[fileIndex]);
			curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, HTTPData);
			curl_easy_setopt(curl, CURLOPT_WRITEDATA, &page);

			res = curl_easy_perform(curl);
			/* always cleanup */
			//curl_easy_cleanup(curl);
			LOGE("Result %d", res);
			fileIndex ++;
		}
		curl_easy_cleanup(curl);
	} else {
		LOGE("Unable to init cURL");
	}

}

/***********************************curl test end************************/
void test() {
	LOGE("this is test");
}
char *jbyteArrayToByte(JNIEnv* env, jbyteArray data, int *outLen) {
	jbyte * olddata = (*env)->GetByteArrayElements(env, data, JNI_FALSE);
	jsize oldsize = (*env)->GetArrayLength(env, data);
	char* bytearr = (char*) olddata;
	*outLen = (int) oldsize;

	return bytearr;
}
jboolean getFile(jint n) {
	LOGE("getfilestart");
	long status;
	jclass cls;
	jmethodID mid;
	jboolean not;

	LOGE("getfilestart1");
	LOGE("getfilestart2");
	cls = (*env)->FindClass(env, "com/example/nativemedia/NativeMedia");
	LOGE("dddd");
	if (cls != 0) {

		mid = (*env)->GetStaticMethodID(env, cls, "getFile", "(I)V");

		if (mid != 0) {

			(*env)->CallStaticVoidMethod(env, cls, mid, n);
			return JNI_TRUE;
		} else
			return JNI_FALSE;
	} else
		return JNI_FALSE;
}

jboolean testCallback() {
	LOGE("testCallback");
	long status;
	jclass cls;
	jmethodID mid;
	jboolean not;
    JNIEnv* pJniEnv = 0;
    if ((*m_pJVM)->AttachCurrentThread(m_pJVM,&pJniEnv, 0) < 0)
    {
        LOGE("CHttpMediaThumbnailExecutor::GetMediaThumbnailFromJava(): "\
               "???????? JVM attach thread failed!\n");
        return JNI_FALSE;
    }
	LOGE("testCallback1");
	LOGE("testCallback2");
	cls = (*pJniEnv)->FindClass(pJniEnv, "com/example/nativemedia/NativeMedia");
	//mid  = (*pJniEnv)->GetStaticMethodID(pJniEnv, m_ClassQueryMedia, "getFile", "(I)V");
    if (mid == 0)
    {
        LOGE("CHttpMediaThumbnailExecutor::GetMediaThumbnailFromJava(): ?????? "\
               "Cannot find method %s\n");
        (*m_pJVM)->DetachCurrentThread(m_pJVM);
        return JNI_FALSE;
    }
	LOGE("testCallback3");
	(*m_pJVM)->DetachCurrentThread(m_pJVM);
}

// AndroidBufferQueueItf callback to supply MPEG-2 TS packets to the media player
static XAresult AndroidBufferQueueCallback(XAAndroidBufferQueueItf caller,
		void *pCallbackContext, /* input */
		void *pBufferContext, /* input */
		void *pBufferData, /* input */
		XAuint32 dataSize, /* input */
		XAuint32 dataUsed, /* input */
		const XAAndroidBufferItem *pItems,/* input */
		XAuint32 itemsLength /* input */) {
	XAresult res;
	int ok;
	///
	testCallback();
	/////
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
		const int processedCommand = *(int *) pBufferContext;
		if (kEosBufferCntxt == processedCommand) {
			LOGV("EOS was processed\n");
			// our buffer with the EOS message has been consumed
			assert(0 == dataSize);
			goto exit;
		}
	}

	// pBufferData is a pointer to a buffer that we previously Enqueued
	assert((dataSize > 0) && ((dataSize % MPEG2_TS_PACKET_SIZE) == 0));
	assert(
			dataCache <= (char *) pBufferData
					&& (char *) pBufferData
							< &dataCache[BUFFER_SIZE * NB_BUFFERS]);
	assert(0 == (((char *) pBufferData - dataCache) % BUFFER_SIZE));

	// don't bother trying to read more data once we've hit EOF
	if (reachedEof) {
		goto exit;
	}

	size_t nbRead;

	size_t bytesRead;
	bytesRead = BUFFER_SIZE;
	int status = 0;
	//bytesRead = fread(pBufferData, 1, BUFFER_SIZE, file);
	status = read(pBufferData, &bytesRead, 188, 0);
	while (status == 0 || bytesRead <= 0) {
		int ok;
		ok = pthread_mutex_lock(&mutex_rw);
		assert(0 == ok);
		if (canread == JNI_FALSE) {
			ok = pthread_mutex_unlock(&mutex);
			assert(0 == ok);
			break;
		} else {
			ok = pthread_mutex_unlock(&mutex);
			assert(0 == ok);
		}

		//LOGE("s =%d , b =%d ", status, bytesRead);
		//LOGE("read sleep");
		sleep(1);
		bytesRead = BUFFER_SIZE;
		status = read(pBufferData, &bytesRead, 188, 0);

	}
	//LOGE("read sucess");
	if (bytesRead > 0) {
		if ((bytesRead % MPEG2_TS_PACKET_SIZE) != 0) {
			LOGV("Dropping last packet because it is not whole");
		}
		size_t packetsRead = bytesRead / MPEG2_TS_PACKET_SIZE;
		size_t bufferSize = packetsRead * MPEG2_TS_PACKET_SIZE;
		res = (*caller)->Enqueue(caller, NULL /*pBufferContext*/,
				pBufferData /*pData*/, bufferSize /*dataLength*/, NULL /*pMsg*/,
				0 /*msgLength*/);
		assert(XA_RESULT_SUCCESS == res);
	} else {
		// EOF or I/O error, signal EOS
		XAAndroidBufferItem msgEos[1];
		msgEos[0].itemKey = XA_ANDROID_ITEMKEY_EOS;
		msgEos[0].itemSize = 0;
		// EOS message has no parameters, so the total size of the message is the size of the key
		//   plus the size if itemSize, both XAuint32
		res = (*caller)->Enqueue(caller,
				(void *) &kEosBufferCntxt /*pBufferContext*/, NULL /*pData*/,
				0 /*dataLength*/, msgEos /*pMsg*/,
				sizeof(XAuint32) * 2 /*msgLength*/);
		assert(XA_RESULT_SUCCESS == res);
		reachedEof = JNI_TRUE;
	}

	exit: ok = pthread_mutex_unlock(&mutex);
	assert(0 == ok);
	return XA_RESULT_SUCCESS;
}

// callback invoked whenever there is new or changed stream information
static void StreamChangeCallback(XAStreamInformationItf caller,
		XAuint32 eventId, XAuint32 streamIndex, void * pEventData,
		void * pContext) {
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
			res = (*caller)->QueryStreamInformation(caller, streamIndex,
					&videoInfo);
			assert(XA_RESULT_SUCCESS == res);
			LOGV(
					"Found video size %u x %u, codec ID=%u, frameRate=%u, bitRate=%u, duration=%u ms", videoInfo.width, videoInfo.height, videoInfo.codecId, videoInfo.frameRate, videoInfo.bitRate, videoInfo.duration);
		}
			break;
		default:
			fprintf(stderr, "Unexpected domain %u\n", domain);
			break;
		}
	}
		break;
	default:
		fprintf(stderr, "Unexpected stream event ID %u\n", eventId);
		break;
	}
}

// reset player to play another source
void Java_com_example_nativemedia_NativeMedia_reset(JNIEnv* env,
		jclass clazz) {

}

// create the engine and output mix objects
void Java_com_example_nativemedia_NativeMedia_createEngine(JNIEnv* env,
		jclass clazz) {
	XAresult res;

	// create engine
	res = xaCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
	assert(XA_RESULT_SUCCESS == res);

	// realize the engine
	res = (*engineObject)->Realize(engineObject, XA_BOOLEAN_FALSE);
	assert(XA_RESULT_SUCCESS == res);

	// get the engine interface, which is needed in order to create other objects
	res = (*engineObject)->GetInterface(engineObject, XA_IID_ENGINE,
			&engineEngine);
	assert(XA_RESULT_SUCCESS == res);

	// create output mix
	res = (*engineEngine)->CreateOutputMix(engineEngine, &outputMixObject, 0,
			NULL, NULL);
	assert(XA_RESULT_SUCCESS == res);

	// realize the output mix
	res = (*outputMixObject)->Realize(outputMixObject, XA_BOOLEAN_FALSE);
	assert(XA_RESULT_SUCCESS == res);

}

// Enqueue the initial buffers, and optionally signal a discontinuity in the first buffer
static jboolean enqueueInitialBuffers(jboolean discontinuity) {

	/* Fill our cache.
	 * We want to read whole packets (integral multiples of MPEG2_TS_PACKET_SIZE).
	 * fread returns units of "elements" not bytes, so we ask for 1-byte elements
	 * and then check that the number of elements is a multiple of the packet size.
	 */
	size_t bytesRead;
	//bytesRead = fread(dataCache, 1, BUFFER_SIZE * NB_BUFFERS, file);

	bytesRead = BUFFER_SIZE * NB_BUFFERS;
	int status = 0;
	//bytesRead = fread(pBufferData, 1, BUFFER_SIZE, file);
	status = read(dataCache, &bytesRead, 188, 1);
	while (status == 0 || bytesRead <= 0) {
		//LOGE("enqueueInitialBuffers s =%d , b =%d ", status, bytesRead);
		//LOGE("read sleep");
		/*int ok;
		ok = pthread_mutex_lock(&mutex_rw);
		assert(0 == ok);
		if (canread == JNI_FALSE) {
			ok = pthread_mutex_unlock(&mutex);
			assert(0 == ok);
			break;
		} else {
			ok = pthread_mutex_unlock(&mutex);
			assert(0 == ok);
		}*/
		sleep(1);
		//LOGE("enqueueInitialBuffers retry");
		bytesRead = BUFFER_SIZE * NB_BUFFERS;
		status = read(dataCache, &bytesRead, 188, 1);

	}

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
					dataCache + i * BUFFER_SIZE, bufferSize, items /*pMsg*/,
					sizeof(XAuint32) * 2 /*msgLength*/);
			discontinuity = JNI_FALSE;
		} else {
			res = (*playerBQItf)->Enqueue(playerBQItf, NULL /*pBufferContext*/,
					dataCache + i * BUFFER_SIZE, bufferSize, NULL, 0);
		}
		assert(XA_RESULT_SUCCESS == res);
		packetsRead -= packetsThisBuffer;
	}

	return JNI_TRUE;
}

jboolean Java_com_example_nativemedia_NativeMedia_setFile(JNIEnv* env,
		jclass clazz, jobject list) {
	//LOGE("setfile");

	 jclass clazzs = (*env)->GetObjectClass(env, list);
	 jmethodID getMethodID = (*env)->GetMethodID(env, clazzs, "get",
	 "(I)Ljava/lang/Object;");
	 jmethodID sizeMethodID = (*env)->GetMethodID(env, clazzs, "size", "()I");
	 int size = (*env)->CallIntMethod(env, list, sizeMethodID);
	 filenum = size;
	 //LOGE("arrayList's size is : %d", size);
	 int i = 0;
	 while (size) {
	 //for (int i = 0; i < size; i++)
	 //{
	 jstring str = (jstring)(*env)->CallObjectMethod(env, list, getMethodID,
	 i);
	 jboolean isCpopy;
	 char * p = (*env)->GetStringUTFChars(env, str, &isCpopy);
	 //LOGE(p);
	 files[i] = p;
	 //LOGE(p);
	 //}
	 size--;
	 i++;
	 }

	 // convert Java string to UTF-8
	 //const char *utf8 = (*env)->GetStringUTFChars(env,filename,NULL);
	 //assert(NULL != utf8);

	 // open the file to play
	 fileIndex = 0;
		int ok;
		ok = pthread_mutex_lock(&mutex_rw);
		assert(0 == ok);
		canread = JNI_TRUE;
		ok = pthread_mutex_unlock(&mutex);
		assert(0 == ok);

	 /*file = fopen(files[fileIndex], "rb");
	 LOGE(files[fileIndex]);
	 if (file == NULL) {
	 return JNI_FALSE;
	 }
	 // release the Java string and UTF-8
	 //(*env)->ReleaseStringUTFChars(env, filename, utf8);
	 fileIndex++;
	 */
	return JNI_TRUE;
}
// create streaming media player
jboolean Java_com_example_nativemedia_NativeMedia_createStreamingMediaPlayer(
		JNIEnv* env, jclass clazz, jint filenums) {
	filenum = filenums;
	getFile(0);
	//LOGE("curltest");
	int ret;
	ret = pthread_create(&httpThd, NULL, (void *) curltest, NULL);
	if (ret != 0) {
		//LOGE("Create pthread error!");
		return JNI_FALSE;
	}
	//LOGE("curltest end");
	XAresult res;
	//getFile(0);
	//assert(NULL != filename);
	//LOGE("afetr");
	//LOGE("afetr");
	// convert Java string to UTF-8
	//const char *utf8 = (*env)->GetStringUTFChars(filename);
	//assert(NULL != utf8);

	// open the file to play
	//file = fopen(utf8, "rb");
	//if (file == NULL) {
	//return JNI_FALSE;
	//}
	//LOGE("afetr2");
	// configure data source
	XADataLocator_AndroidBufferQueue loc_abq = {
			XA_DATALOCATOR_ANDROIDBUFFERQUEUE, NB_BUFFERS };
	XADataFormat_MIME format_mime = { XA_DATAFORMAT_MIME, XA_ANDROID_MIME_MP2TS,
			XA_CONTAINERTYPE_MPEG_TS };
	XADataSource dataSrc = { &loc_abq, &format_mime };

	// configure audio sink
	XADataLocator_OutputMix loc_outmix = { XA_DATALOCATOR_OUTPUTMIX,
			outputMixObject };
	XADataSink audioSnk = { &loc_outmix, NULL };

	// configure image video sink
	XADataLocator_NativeDisplay loc_nd = { XA_DATALOCATOR_NATIVEDISPLAY, // locatorType
			// the video sink must be an ANativeWindow created from a Surface or SurfaceTexture
			(void*) theNativeWindow, // hWindow
			// must be NULL
			NULL // hDisplay
			};
	XADataSink imageVideoSink = { &loc_nd, NULL };

	// declare interfaces to use
	XAboolean required[NB_MAXAL_INTERFACES] = { XA_BOOLEAN_TRUE,
			XA_BOOLEAN_TRUE, XA_BOOLEAN_TRUE };
	XAInterfaceID iidArray[NB_MAXAL_INTERFACES] = { XA_IID_PLAY,
			XA_IID_ANDROIDBUFFERQUEUESOURCE, XA_IID_STREAMINFORMATION };

	// create media player
	res = (*engineEngine)->CreateMediaPlayer(engineEngine, &playerObj, &dataSrc,
			NULL, &audioSnk, &imageVideoSink, NULL, NULL,
			NB_MAXAL_INTERFACES /*XAuint32 numInterfaces*/,
			iidArray /*const XAInterfaceID *pInterfaceIds*/,
			required /*const XAboolean *pInterfaceRequired*/);
	assert(XA_RESULT_SUCCESS == res);

	// release the Java string and UTF-8
	//(*env)->ReleaseStringUTFChars(env, filename, utf8);

	// realize the player
	res = (*playerObj)->Realize(playerObj, XA_BOOLEAN_FALSE);
	assert(XA_RESULT_SUCCESS == res);

	// get the play interface
	res = (*playerObj)->GetInterface(playerObj, XA_IID_PLAY, &playerPlayItf);
	assert(XA_RESULT_SUCCESS == res);

	// get the stream information interface (for video size)
	res = (*playerObj)->GetInterface(playerObj, XA_IID_STREAMINFORMATION,
			&playerStreamInfoItf);
	assert(XA_RESULT_SUCCESS == res);

	// get the volume interface
	res = (*playerObj)->GetInterface(playerObj, XA_IID_VOLUME, &playerVolItf);
	assert(XA_RESULT_SUCCESS == res);

	// get the Android buffer queue interface
	res = (*playerObj)->GetInterface(playerObj, XA_IID_ANDROIDBUFFERQUEUESOURCE,
			&playerBQItf);
	assert(XA_RESULT_SUCCESS == res);

	// specify which events we want to be notified of
	res = (*playerBQItf)->SetCallbackEventsMask(playerBQItf,
			XA_ANDROIDBUFFERQUEUEEVENT_PROCESSED);
	assert(XA_RESULT_SUCCESS == res);

	// register the callback from which OpenMAX AL can retrieve the data to play
	res = (*playerBQItf)->RegisterCallback(playerBQItf,
			AndroidBufferQueueCallback, NULL);
	assert(XA_RESULT_SUCCESS == res);

	// we want to be notified of the video size once it's found, so we register a callback for that
	res = (*playerStreamInfoItf)->RegisterStreamChangeCallback(
			playerStreamInfoItf, StreamChangeCallback, NULL);
	assert(XA_RESULT_SUCCESS == res);

	// enqueue the initial buffers
	if (!enqueueInitialBuffers(JNI_FALSE)) {
		return JNI_FALSE;
	}

	// prepare the player
	res = (*playerPlayItf)->SetPlayState(playerPlayItf, XA_PLAYSTATE_PAUSED);
	assert(XA_RESULT_SUCCESS == res);

	// set the volume
	res = (*playerVolItf)->SetVolumeLevel(playerVolItf, 0);
	assert(XA_RESULT_SUCCESS == res);

	// start the playback
	res = (*playerPlayItf)->SetPlayState(playerPlayItf, XA_PLAYSTATE_PLAYING);
	assert(XA_RESULT_SUCCESS == res);

	return JNI_TRUE;
}

// set the playing state for the streaming media player
void Java_com_example_nativemedia_NativeMedia_setPlayingStreamingMediaPlayer(
		JNIEnv* env, jclass clazz, jboolean isPlaying) {
	XAresult res;

	// make sure the streaming media player was created
	if (NULL != playerPlayItf) {

		// set the player's state
		res = (*playerPlayItf)->SetPlayState(playerPlayItf,
				isPlaying ? XA_PLAYSTATE_PLAYING : XA_PLAYSTATE_PAUSED);
		assert(XA_RESULT_SUCCESS == res);

	}

}

// shut down the native media system
void Java_com_example_nativemedia_NativeMedia_shutdown(JNIEnv* env,
		jclass clazz) {
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

	// make sure we don't leak native windows
	if (theNativeWindow != NULL) {
		ANativeWindow_release(theNativeWindow);
		theNativeWindow = NULL;
	}
}

// set the surface
void Java_com_example_nativemedia_NativeMedia_setSurface(JNIEnv *env,
		jclass clazz, jobject surface) {
	// obtain a native window from a Java surface
	theNativeWindow = ANativeWindow_fromSurface(env, surface);
}

// rewind the streaming media player
void Java_com_example_nativemedia_NativeMedia_rewindStreamingMediaPlayer(
		JNIEnv *env, jclass clazz) {
	//m_ClassQueryMedia = clazz;
	XAresult res;

	// make sure the streaming media player was created
	if (NULL != playerBQItf && NULL != file) {
		// first wait for buffers currently in queue to be drained
		int ok;
		ok = pthread_mutex_lock(&mutex);
		assert(0 == ok);
		discontinuity = JNI_TRUE;
		// wait for discontinuity request to be observed by buffer queue callback
		// Note: can't rewind after EOS, which we send when reaching EOF
		// (don't send EOS if you plan to play more content through the same player)
		while (discontinuity && !reachedEof) {
			ok = pthread_cond_wait(&cond, &mutex);
			assert(0 == ok);
		}
		ok = pthread_mutex_unlock(&mutex);
		assert(0 == ok);
	}

}

/*
 * Set some test stuff up.
 *
 * Returns the JNI version on success, -1 on failure.
 */
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
	LOGE("start load");
	jint result = -1;
	m_pJVM = vm;
	if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_4) != JNI_OK) {
		return -1;
	}
	assert(env != NULL);
	m_ClassQueryMedia = (*env)->FindClass(env, "com/example/nativemedia/NativeMedia");
	LOGE(" load m_ClassQueryMedia");
	/* success -- return valid version number */
	result = JNI_VERSION_1_4;

	return result;
}


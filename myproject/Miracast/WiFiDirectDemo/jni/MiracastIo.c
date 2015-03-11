#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <sys/time.h>
#include <assert.h>
#include "include/MiracastIo.h"
#include "include/RingBuffer.h"
#include <sys/stat.h> 
#include <fcntl.h>
#include <android/log.h>
#define TAG "MiracastIO"
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

#define DUMP_TS_FILE	"/data/data/com.rtk.android.miracast/wfd.ts"
static int gDumpTsFd = (-1);
static int gHasDumpTs = 0;
static FILE *file;
#define MAX_DUMP_TS_FILE_SIZE	(20*1024*1024)
static int gDumpTsPkts = 0;
void openDumpTsFile(void)
{
    // open the file to play
    file = fopen(DUMP_TS_FILE, "w");
    if (file == NULL) {
      LOGV("[%s:%d:%s].open %s fail\n",__FILE__,__LINE__,__func__,DUMP_TS_FILE);
    }
}

void closeDumpTsFile(void)
{
    if (file != NULL) {
        fclose(file);
        file = NULL;
    }
}

void writeTsPktsToFile(unsigned char* pTsPkts, unsigned int tsPktsBytes)
{
   if(file !=NULL)
     {
          if(fwrite(pTsPkts, 1, tsPktsBytes, file) != 1)
               LOGV("write data %d bytes",tsPktsBytes);
     }
}



void* Miracast_IO_open()
{
	unsigned short port = 24030;
	int s = 0;
	struct sockaddr_in si_me;
	int optval = 1;
	int buf_siz = 2*1048576;
	IOPlugin_Miracast* pIOPlugin = NULL;
	printf("[%s:%d:%s].enter.\n",__FILE__,__LINE__,__func__);
	buffer_clear();
	if ((s = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)) == (-1)) {
		printf("[%s:%d:%s].open UDP socket fail.\n",__FILE__,__LINE__,__func__);
		close(s);
                return NULL;
	}
	memset((char *)&si_me, 0, sizeof(si_me));
	si_me.sin_family = AF_INET;
	si_me.sin_port = htons(port);
	si_me.sin_addr.s_addr = htonl(INADDR_ANY);

	setsockopt(s, SOL_SOCKET, SO_REUSEADDR, (const void *)&optval , sizeof(int));
	setsockopt(s, SOL_SOCKET, SO_RCVBUF, (char*)&buf_siz, sizeof(buf_siz));

	if (bind(s, (struct sockaddr *)&si_me, sizeof(si_me)) == (-1)) {
		printf("[%s:%d:%s].bind() on port=%d fail.\n",__FILE__,__LINE__,__func__,port);
		close(s);
		return NULL;
	}
	pIOPlugin = calloc(sizeof(IOPlugin_Miracast), 1);
	if (pIOPlugin == NULL) {
		printf("[%s:%d:%s].calloc() fail.\n",__FILE__,__LINE__,__func__);
                close(s);
                return NULL;
	}
	pIOPlugin->socketId = s;
	printf("[%s:%d:%s].success.socketId=%d.port=%d.\n",__FILE__,__LINE__,__func__,pIOPlugin->socketId,port);
	//ts_parse_init();
        LOGE("IO open success");
        openDumpTsFile();
	return pIOPlugin;
}

int Miracast_IO_close(void* pInstance)
{
	printf("[%s:%d:%s].enter.\n",__FILE__,__LINE__,__func__);
	IOPlugin_Miracast* pIOPlugin = (IOPlugin_Miracast*)pInstance;
	free(pIOPlugin);
	buffer_clear();
        closeDumpTsFile();
	return 1;
}

int Miracast_IO_read(void* pInstance, unsigned char* pBuffer, int size, NAVBUF* pEventReceived)
{
	fd_set rfds;
	int retval=0, readval=0;
	struct timeval tv;
	tv.tv_sec = 0;
	tv.tv_usec = 200000;
	unsigned char rptPktBuf[188*100 +12];
	unsigned char* pCurReadTSPkts = NULL;
	unsigned int curReadTSBytes = 0;
	SOCKET_MEDIA_ATTRIBUTE mediaAttr;
	HRESULT ret = S_OK;
	unsigned int copySize = 0;
	unsigned int useSize = 0;
	unsigned int saveSize = 0;
	unsigned char *pHdr = NULL;
	unsigned char marker = 0;
	unsigned char payloadType = 0;
	unsigned short seq = 0;

	IOPlugin_Miracast* pIOPlugin = (IOPlugin_Miracast*)pInstance;
	if (pIOPlugin->socketId == -1) {
		printf("[%s:%d:%s].socketId is (-1).\n",__FILE__,__LINE__,__func__);
		return IOPLUGIN_ERROR_NO_DEVICE;
	}
	memset(&pIOPlugin->header, 0,  sizeof(SOCKET_DATA_HEADER));
	FD_ZERO(&rfds);
	FD_SET(pIOPlugin->socketId, &rfds);
	copySize = size;
	buffer_read(pBuffer, &copySize ,188,0);
	LOGE("read from buffer = %d",copySize);
	useSize = size - copySize;
	retval = select(pIOPlugin->socketId+1, &rfds, NULL, NULL, &tv);
	if (retval > 0) {
		readval = read(pIOPlugin->socketId, rptPktBuf, 188*100 +12);
		if (readval > 0) {
			LOGE("read all readval = %d",readval);
			pHdr = rptPktBuf;
			marker = (pHdr[1] & 0x80) >> 7;
			payloadType = pHdr[1] & 0x7F;
			seq = (pHdr[2]<<8) | (pHdr[3]);
			pCurReadTSPkts = rptPktBuf + sizeof(rtp_hdr_t);
			curReadTSBytes = readval - sizeof(rtp_hdr_t);
			if(curReadTSBytes > useSize)
				saveSize = curReadTSBytes - useSize;
			else
				useSize = curReadTSBytes;
			if(useSize>0){
				memcpy((pBuffer+copySize), pCurReadTSPkts, useSize);
			}
			if(saveSize>0){
				buffer_write((pCurReadTSPkts+useSize), saveSize);
				LOGE("save to buffer = %d",saveSize);
			}
                        writeTsPktsToFile(pCurReadTSPkts, curReadTSBytes);
			return (copySize+useSize);
		}
		else {
			LOGE("[%s:%d:%s].read() fail.return IOPLUGIN_ERROR_BUSY.\n",__FILE__,__LINE__,__func__);
			if(copySize>0)
				return copySize ;
			return IOPLUGIN_ERROR_BUSY;
		}
	}
	else if (retval == (-1)) {
		LOGE("[%s:%d:%s].select() fail.retval==(-1).\n",__FILE__,__LINE__,__func__);
		if(copySize>0)
			return copySize ;
		return IOPLUGIN_ERROR_EOF;
	}
	if(copySize>0)
		return copySize ;
        LOGE("read buffer timeout");
	return IOPLUGIN_ERROR_TIMEOUT;
}



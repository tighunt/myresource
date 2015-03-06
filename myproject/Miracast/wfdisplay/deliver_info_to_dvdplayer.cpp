#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stddef.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include "deliver_info_to_dvdplayer.h"
#ifdef NOT_INCLUDE_TVSERVER
#include "SocketAVDataLocal.h"
#else
#include "SocketAVData.h"
#endif
#include "wifi_rtsp_cmds.h"

#define LOG_TAG "DELIVER_INFO"
#include <utils/Log.h>

extern int wifi_hdcp2_status(SOCKET_CW_OFFSET* aeskey, SOCKET_CW_OFFSET * aesiv);

bool write_wifi_source_file(struct wifi_source *src)
{
	if (src == NULL) {
		ALOGI("[%s:%d:%s].src is NULL.\n",__FILE__,__LINE__,__func__);
		return false;
	}
	FILE *fd = fopen(MIRACAST_WIFI_SOURCE_FILE, "wb");
	if (fd == NULL) {
		ALOGI("[%s:%d:%s].open %s fail.\n",__FILE__,__LINE__,__func__,MIRACAST_WIFI_SOURCE_FILE);
		return false;
	}
	int ret = fwrite(src, 1, sizeof(struct wifi_source), fd);
	if (ret <= 0) {
		ALOGI("[%s:%d:%s].write to %s fail.ret=%d.\n",__FILE__,__LINE__,__func__,MIRACAST_WIFI_SOURCE_FILE,ret);
		return false;
	}

	//ALOGI("[%s:%d:%s].audiotype=%d.channelnum=%ld.sampleRate=%ld.bitspersample=%ld.\n",__FILE__,__LINE__,__func__,src->audiotype,src->channelnum,src->sampleRate,src->bitspersample);

	fclose(fd);
	fd = NULL;
	ALOGI("[%s:%d:%s].write struct wifi_source to %s OK.ret=%d.sizeof(struct wifi_source)=%d.\n",__FILE__,__LINE__,__func__,MIRACAST_WIFI_SOURCE_FILE,ret,sizeof(struct wifi_source));
	return true;
}

bool write_hdcp2_info_file(void)
{
	deliver_hdcp2_info deliverHDCP2Info;
	SOCKET_CW_OFFSET key;
	SOCKET_CW_OFFSET iv;

	memset(&key, 0x00, sizeof(SOCKET_CW_OFFSET));
	memset(&iv, 0x00, sizeof(SOCKET_CW_OFFSET));
	
	if (wifi_hdcp2_status((SOCKET_CW_OFFSET*)&key, (SOCKET_CW_OFFSET*)&iv) != HDCP2_NEGO_STATUS_DONE) {
		return false;
	}
	
	deliverHDCP2Info.config.mode = S_PES_LEVEL_DESCRAMBLED;
	deliverHDCP2Info.config.algorithm = STDA_HDCP2_AES_CTR;
	deliverHDCP2Info.config.round = 0;
	deliverHDCP2Info.key = key;
	deliverHDCP2Info.iv = iv;

	ALOGI("[%s:%d:%s].conifg.mode=%d.algorithm=%d.round=%d.\n",__FILE__,__LINE__,__func__,deliverHDCP2Info.config.mode,deliverHDCP2Info.config.algorithm,deliverHDCP2Info.config.round);
	ALOGI("[%s:%d:%s].iv.evenKeyOffset=0x%X.oddKeyOffset=0x%X.\n",__FILE__,__LINE__,__func__,deliverHDCP2Info.iv.evenKeyOffset,deliverHDCP2Info.iv.oddKeyOffset);
	ALOGI("[%s:%d:%s].key.evenKeyOffset=0x%X.oddKeyOffset=0x%X.\n",__FILE__,__LINE__,__func__,deliverHDCP2Info.key.evenKeyOffset,deliverHDCP2Info.key.oddKeyOffset);

	FILE *fd = fopen(MIRACAST_HDCP2_INFO_FILE, "wb");
	if (fd == NULL) {
		ALOGI("[%s:%d:%s].open %s fail.\n",__FILE__,__LINE__,__func__,MIRACAST_HDCP2_INFO_FILE);
		return false;
	}
	int ret = fwrite(&deliverHDCP2Info, 1, sizeof(deliver_hdcp2_info), fd);
	if (ret <= 0) {
		ALOGI("[%s:%d:%s].write to %s fail.ret=%d.\n",__FILE__,__LINE__,__func__,MIRACAST_HDCP2_INFO_FILE,ret);
		return false;
	}

	fclose(fd);
	fd = NULL;
	ALOGI("[%s:%d:%s].write struct deliver_hdcp2_info to %s OK.ret=%d.sizeof(deliver_hdcp2_info)=%d.\n",__FILE__,__LINE__,__func__,MIRACAST_HDCP2_INFO_FILE,ret,sizeof(deliver_hdcp2_info));
	return true;
}

#include <stdio.h>
#include <stdlib.h>
#include <stddef.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/stat.h>
#include <unistd.h>
#include <fcntl.h>
#include <string.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <sys/time.h>
#include <sys/resource.h>
#include "RTMediaPlayer.h"
#include "RTKWifiDisplaySink.h"

using namespace android;

/***********************************************************************************/
/*MyCB                                                                             */
/***********************************************************************************/

struct MyCB : public RTKWifiDisplaySinkCB {
    MyCB(RTMediaPlayer* player);
    virtual ~MyCB();
	virtual void OnStateChange(WIFI_SOURCE_STATE oldstate, WIFI_SOURCE_STATE state);
private:
	RTMediaPlayer* m_player;
};

MyCB::MyCB(RTMediaPlayer* player)
{
    printf("[baili][%s][%s][%d]\n", __FILE__, __func__, __LINE__);
	m_player = player;
}

MyCB::~MyCB()
{
    printf("[baili][%s][%s][%d]\n", __FILE__, __func__, __LINE__);
}

void MyCB::OnStateChange(WIFI_SOURCE_STATE oldstate, WIFI_SOURCE_STATE state)
{
	printf("[baili][%s] [%d => %d]\n", __func__, oldstate, state);
	if(state == 8)
	{
			sleep(20);
			m_player->stop();
	}
}

/***********************************************************************************/
/*main                                                                             */
/***********************************************************************************/
int main(int argc, char **argv) 
{
	printf("usage: rtk_wfd miracast://172.29.51.162:8554\n");
    printf("[baili][%s][%s][%d],time=%d\n", __FILE__, __func__, __LINE__,time(NULL));

	if(argc < 1)
	{
			printf("too few args.\n");
			return 0;
	}
	char path[512];
	int port;
	char* p = NULL;
	//char* url = "miracast://172.29.51.162:8554";
	char* url = argv[1];
	printf("url %s\n", url);
	if (strncasecmp("miracast://", url, 11)) {
			printf("wrong protocal\n");
			return 0;
	}
	p = strstr(url+11, ":");
	if(!p)
	{
			printf("missing port\n");
			return 0;
	}
	snprintf(path, p-url-11+1, "%s", url+11);
	port = atoi(p+1);
	printf("path: %s port: %d\n", path, port);

	RTMediaPlayer* player = new RTMediaPlayer();
	//player->setDataSource("/sdcard/1.ogm");
	player->setDataSource(url);
	//player->setDataSource("miracast://tmp/MediaData?container=ts");
	player->prepare();
	//player->initCheck();
	player->start();
	sleep(30);
	player->stop();
	delete player;
	return 1;

	MyCB* cb = new MyCB(player);
	//RTKWifiDisplaySinkCB* cb = new RTKWifiDisplaySinkCB();
	RTKWifiDisplaySink* sink = RTKWifiDisplaySink::GetInstance();
	sink->Init(path, port, cb);
	sink->Start();

	while(1){
			if(sink->IsRunning())
			{
					printf("OOOO\n");
					usleep(500000);
			}
			else
			{
					printf("XXXX\n");
					break;
			}
	}

	//player->stop();
	delete player;
	player = NULL;

	RTKWifiDisplaySink::ReleaseInstance();
	delete cb;
	cb = NULL;

    printf("[baili][%s][%s][%d],time=%d\n", __FILE__, __func__, __LINE__,time(NULL));
    return 1;
}

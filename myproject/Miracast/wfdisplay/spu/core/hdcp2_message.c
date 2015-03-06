/*
 * hdcp2_message.c
 *
 *  Created on: 2013/6/10
 *      Author: bruce_huang
 */
#include "../include/hdcp2_messages.h"
#include "../include/hdcpDebug.h"

static RTK_HDCP_STRUCT hdcp_str;

// APIs
void setHDCPCAP(unsigned char val)
{

	printf("[HDCP] setHDCPCap= %d\n",val);

	hdcp_str.gHDCP = val;
}

void setPreComputation(unsigned char val)
{
	hdcp_str.gPreComputation=val;
}

void setSelfPreComputation(unsigned char val)
{
	hdcp_str.gSelfPreComputation=val;
}

void setContentCategorySupport(unsigned char val)
{
	hdcp_str.gContentCategorySupport=val;
}

unsigned char getHDCPCAP()

{
	return hdcp_str.gHDCP;
}

unsigned char getPreComputation()
{
	return hdcp_str.gPreComputation;
}

unsigned char getSelfPreComputation()
{
	return hdcp_str.gSelfPreComputation;
}

/*
unsigned char getTransmitterVersion()
{
	return hdcp_str.transmitterVersion;
}

void setTransmitterVersion(unsigned char val)
{
	hdcp_str.transmitterVersion = val;
}
*/

RTK_HDCP_STRUCT *getRTKHDCPSTR()
{
	return &hdcp_str;
}

void init_rtk_hdcp()
{
	DPRINT(("[DHDCP] init_rtk_hdcp\n"));
	memset(&hdcp_str,0x0,sizeof(hdcp_str));

	#if (HDCP_2X_VSN == 21)
	ALOGI("[DHDCP] init_rtk_hdcp 2.1\n");

	
	setHDCPCAP(CAP_HDCP2_1);		// gHDCP2_1 = CAP_HDCP2_1;
	#elif (HDCP_2X_VSN == 22)
	ALOGI("[DHDCP] init_rtk_hdcp 2.2\n");
	setHDCPCAP(CAP_HDCP2_2);		// Bruce add for HDCP2.2
	#endif
}

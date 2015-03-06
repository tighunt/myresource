#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <string.h>
#include <assert.h>
#include <errno.h>
#include <pthread.h>

#include "systemCommand.h"
//#include "RTMediaConfig.h"
#include <RT_MediaConfig.h>

#define MAX_RESULT_LEN              512

using namespace android;

static   RT_MediaConfig *system_ipc = NULL;

bool system_init(void) 
{
		return true;
	if (system_ipc==NULL)
    	system_ipc = new RT_MediaConfig();
    if (system_ipc->initCheck() != NO_ERROR) {
        printf("sys config failed on init\n");
        assert(0);
    }
    return true;
}

void system_close(void) 
{
		return;
    if (system_ipc) 
        delete system_ipc;
    system_ipc = NULL;
}

bool system_analogop(bool on) 
{
		return false;
    char * retStr = (char*) malloc(MAX_RESULT_LEN);
    char *pargv[1];
    int retlen = MAX_RESULT_LEN;
    const char * cmd = "CommandString";
    
    if (on) {
        pargv[0] = "AnalogOutOn";
    }
    else {
        pargv[0] = "AnalogOutOff";
    }
    
    if (system_ipc->execFunction(cmd, (const char **) pargv, 1, retlen, retStr, true) != NO_ERROR) {
        free(retStr);
        return false;
    }
    free(retStr);
    return true;
}

bool system_getTVSupport3D(unsigned short* p3D_TV_Info) 
{
		return false;
    char * retStr = (char*) malloc(MAX_RESULT_LEN);
    char *pargv[0];
    int retlen = MAX_RESULT_LEN;
    const char * cmd = "getTVSupport3D";

    if (system_ipc->execFunction(cmd, (const char **) pargv, 0, retlen, retStr) != NO_ERROR)
        return false;

    if (retlen > 0) {
        printf("[%s] return %s\n", __func__, retStr);
        *p3D_TV_Info = atoi(retStr);
        free(retStr);
        return true;
    }
    free(retStr);
    return false;
}

bool system_set3DDisplay(bool on, int mode, int fps) 
{
		return false;
    char * retStr = (char*) malloc(MAX_RESULT_LEN);
    char *pargv[3];
    int retlen = MAX_RESULT_LEN;
    const char * cmd = "SetFormat3d";
    char afps[512];
    
    if (on) {
        if (mode == SIDE_BY_SIDE_3DMODE) {
            pargv[0] = "FORMAT_3D_SIDE_BY_SIDE";
        }
        else {
            pargv[0] = "FORMAT_3D_TOP_AND_BOTTOM";
        }
        sprintf(afps, "%d", fps);
        pargv[1] = afps;
    }
    else {
        pargv[0] = "FORMAT_3D_NULL";
        pargv[1] = "0";
    }    
    
    if (system_ipc->execFunction(cmd, (const char **) pargv, 2, retlen, retStr) != NO_ERROR) {
        free(retStr);
        return false;
    }
    free(retStr);
    return true;
}


/*
 * Copyright (C) 2007 The Android Open Source Project
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


package android.app;

import android.app.tv.*;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
//import android.os.ServiceManager;
import android.util.Log;
import android.graphics.SurfaceTexture;

/**
 * This class provides access to the system TV services.  These allow you
 * to schedule your application to be run at some point in the future.  When
 * an alarm goes off, the {@link Intent} that had been registered for it
 * is broadcast by the system, automatically starting the target application
 * if it is not already running.  Registered alarms are retained while the
 * device is asleep (and can optionally wake the device up if they go off
 * during that time), but will be cleared if it is turned off and rebooted.
 * 
 * <p>The Alarm Manager holds a CPU wake lock as long as the alarm receiver's
 * onReceive() method is executing. This guarantees that the phone will not sleep
 * until you have finished handling the broadcast. Once onReceive() returns, the
 * Alarm Manager releases this wake lock. This means that the phone will in some
 * cases sleep as soon as your onReceive() method completes.  If your alarm receiver
 * called {@link android.content.Context#startService Context.startService()}, it
 * is possible that the phone will sleep before the requested service is launched.
 * To prevent this, your BroadcastReceiver and Service will need to implement a
 * separate wake lock policy to ensure that the phone continues running until the
 * service becomes available.
 *
 * <p><b>Note: The Alarm Manager is intended for cases where you want to have
 * your application code run at a specific time, even if your application is
 * not currently running.  For normal timing operations (ticks, timeouts,
 * etc) it is easier and much more efficient to use
 * {@link android.os.Handler}.</b>
 *
 * <p>You do not
 * instantiate this class directly; instead, retrieve it through
 * {@link android.content.Context#getSystemService
 * Context.getSystemService(Context.TV_SERVICE)}.
 */
/** @hide */
public class TvManager
{
    //TV_MEDIA_MSG
    public final static int TV_MEDIA_MSG_SCAN_FREQ_UPDATE = 1309;
	public final static int TV_MEDIA_MSG_SCAN_AUTO_COMPLETE = 1310;
	public final static int TV_MEDIA_MSG_SCAN_MANUAL_COMPLETE = 1311;
	public final static int TV_MEDIA_MSG_SCAN_SEEK_COMPLETE = 1312;
    
    // ENUM_TV_TYPE
    public final static int TV_TYPE_NONE = 0;
    public final static int TV_TYPE_ATV = 1;
    public final static int TV_TYPE_DTV = 2;
    public final static int TV_TYPE_IDTV = 3;

    // TV_SCAN_INFO
    public final static int SCAN_INFO_STATE = 0;
    public final static int SCAN_INFO_PROGRESS = 1;
    public final static int SCAN_INFO_CHANNEL_COUNT = 2;
    public final static int SCAN_INFO_CHANNEL_STRENGTH = 3;
    public final static int SCAN_INFO_CHANNEL_QUALITY = 4;
    public final static int SCAN_INFO_CHANNEL_FREQUENCY = 5;
    public final static int SCAN_INFO_CHANNEL_BANDWIDTH = 6;
    public final static int SCAN_INFO_CURRENT_CHANNEL_NUMBER = 7;
    public final static int SCAN_INFO_SET_SCANINFO_FREQUENCY = 8;

    // RT_ATV_SOUND_SYSTEM
    public final static int RT_ATV_SOUND_SYSTEM_UNKNOWN = 0;
    public final static int RT_ATV_SOUND_SYSTEM_DK = 1;
    public final static int RT_ATV_SOUND_SYSTEM_I = 2;
    public final static int RT_ATV_SOUND_SYSTEM_BG = 3;
    public final static int RT_ATV_SOUND_SYSTEM_MN = 4;
    public final static int RT_ATV_SOUND_SYSTEM_L = 5;
    public final static int RT_ATV_SOUND_SYSTEM_LA = 6;
    public final static int RT_ATV_SOUND_SYSTEM_AUTO = 7;

    // ENUM_NET_IP_TYPE
    public final static int IP_TYPE_IP = 0;
    public final static int IP_TYPE_SUBMASK = 1;
    public final static int IP_TYPE_GATEWAY = 2;
    public final static int IP_TYPE_DNS = 3;

	//ENUM_NET_INTERFACE
    public final static int ETH0 = 0;
    public final static int WLAN0 = 1;
    public final static int WLAN1 = 2;
    public final static int PPP0 = 3;
    public final static int BR0 = 4;
    public final static int NET_INTERFACE_NUM = 5;

    // ENUM_WIRELESS_SECURITY
    public final static int WL_SECURITY_OPEN = 0;
    public final static int WL_SECURITY_WEP = 1;
    public final static int WL_SECURITY_WPA = 2;
    public final static int WL_SECURITY_WEP_SHAREKEY = 3;
    public final static int WL_SECURITY_UNKNOWN = 4;

    // ENUM_WIRELESS_MODE
    public final static int WL_MODE_INFRASTRUCTURE = 0;
    public final static int WL_MODE_P2P = 1;
    public final static int WL_MODE_WPS = 2;
    public final static int WL_MODE_WCN = 3;

    // ENUM_WPS_MODE
    public final static int WPS_MODE_NONE  = 0;
    public final static int WPS_MODE_PIN  = 1;
    public final static int WPS_MODE_PBC  = 2;

    // NET_WIRELESS_STATE
    public final static int NET_WL_NONE = 0;
    public final static int NET_WL_START = 1;
    public final static int NET_WL_WAIT = 2;
    public final static int NET_WL_PING = 3;
    public final static int NET_WL_OK = 4;
    public final static int NET_WL_ERROR = 5;

    //NET_WIRED_DHCP_STATE
    public final static int NET_DHCP_NONE = 0;
    public final static int NET_DHCP_START = 1;
    public final static int NET_DHCP_WAIT = 2;
    public final static int NET_DHCP_OK = 3;
    public final static int NET_DHCP_ERROR = 4;

	//DNR_MODE
	public final static int DNR_OFF = 0;
	public final static int DNR_LOW = 1;
	public final static int DNR_MEDIUM = 2;
	public final static int DNR_HIGH = 3;
	public final static int DNR_AUTO = 4;

	//PICTURE_MODE
	public final static int PICTURE_MODE_USER = 0;
	public final static int PICTURE_MODE_VIVID = 1;
	public final static int PICTURE_MODE_STD = 2;
	public final static int PICTURE_MODE_GENTLE = 3;
	public final static int PICTURE_MODE_MOVIE = 4;
	public final static int PICTURE_MODE_SPORT = 5;
	public final static int PICTURE_MODE_GAME = 6;
	public final static int PICTURE_MODE_MAX = 7;

	//ENUM_ASPECT_RATIO
	public final static int PS_4_3 = 0;
	public final static int LB_4_3 = 1;
	public final static int Wide_16_9 = 2;
	public final static int Wide_16_10 = 3;
	public final static int SCALER_RATIO_AUTO = 4;
	public final static int SCALER_RATIO_4_3 = 5;
	public final static int SCALER_RATIO_16_9 = 6;
	public final static int SCALER_RATIO_14_9 = 7;
	public final static int SCALER_RATIO_LETTERBOX = 8;
	public final static int SCALER_RATIO_PANORAMA = 9;
	public final static int SCALER_RATIO_FIT = 10;
	public final static int SCALER_RATIO_POINTTOPOINT = 11;
	public final static int SCALER_RATIO_BBY_AUTO = 12;
	public final static int SCALER_RATIO_BBY_NORMAL = 13;
	public final static int SCALER_RATIO_BBY_ZOOM = 14;
	public final static int SCALER_RATIO_BBY_WIDE_1 = 15;
	public final static int SCALER_RATIO_BBY_WIDE_2 = 16;
	public final static int SCALER_RATIO_BBY_CINEMA = 17;
	public final static int SCALER_RATIO_CUSTOM = 18;
	public final static int SCALER_RATIO_PERSON = 19;
	public final static int SCALER_RATIO_CAPTION = 20;
	public final static int SCALER_RATIO_MOVIE = 21;
	public final static int SCALER_RATIO_100 = 22;
	public final static int SCALER_RATIO_SOURCE = 23;
	public final static int SCALER_RATIO_ZOOM_14_9 = 24;
	public final static int SCALER_RATIO_DISABLE = 25;
	public final static int ASPECT_RATIO_MAX = 26;

	//SourceOption
	public final static int SOURCE_OSD = 0;
	public final static int SOURCE_ATV1 = 1;
	public final static int SOURCE_ATV2 = 2;
	public final static int SOURCE_DTV1 = 3;
	public final static int SOURCE_DTV2 = 4;
	public final static int SOURCE_AV1 = 5;
	public final static int SOURCE_AV2 = 6;
	public final static int SOURCE_AV3 = 7;
	public final static int SOURCE_SV1 = 8;
	public final static int SOURCE_SV2 = 9;
	public final static int SOURCE_YPP1 = 10;
	public final static int SOURCE_YPP2 = 11;
	public final static int SOURCE_YPP3 = 12;
	public final static int SOURCE_YPP4 = 13;
	public final static int SOURCE_VGA1 = 14;
	public final static int SOURCE_VGA2 = 15;
	public final static int SOURCE_HDMI1 = 16;
	public final static int SOURCE_HDMI2 = 17;
	public final static int SOURCE_HDMI3 = 18;
	public final static int SOURCE_HDMI4 = 19;
	public final static int SOURCE_HDMI5 = 20;
	public final static int SOURCE_HDMI6 = 21;
	public final static int SOURCE_SCART1 = 22;
	public final static int SOURCE_SCART2 = 23;
	public final static int SOURCE_PLAYBACK = 24;
	public final static int SOURCE_MIC = 25;
	public final static int SOURCE_AUTO = 26; //SOURCE_AUTO is not an actual source, it is only used for boot up source in setup menu
	public final static int SOURCE_IDTV1 = 27; // This is a conceptual source.
	public final static int SOURCE_BROWSER = 28; // This is a fake source.
	public final static int SOURCE_NULL = 29;
	public final static int SOURCE_MAX_NUM = 30;

	// RT_COLOR_STD definitin is consistent with  system.mac/branch_src_sharedMemory_integration/Include/Application/AppClass/MediaControl/Types/RtMediaTypes.h
	public final static int RT_COLOR_STD_UNKNOWN = 0;
	public final static int RT_COLOR_STD_AUTO = 1;
	public final static int RT_COLOR_STD_NTSC = 2; 
	public final static int RT_COLOR_STD_NTSC_50 = 3;
	public final static int RT_COLOR_STD_NTSC_443 = 4;
	public final static int RT_COLOR_STD_PAL_I = 5;
	public final static int RT_COLOR_STD_PAL_M = 6;
	public final static int RT_COLOR_STD_PAL_N = 7;
	public final static int RT_COLOR_STD_PAL_60 = 8;
	public final static int RT_COLOR_STD_SECAM = 9;
	public final static int RT_COLOR_STD_SECAML = 10;
	public final static int RT_COLOR_STD_SECAMLA = 11;

	//The definition is consistent with system.mac/branch_src_sharedMemory_integration/Include/Platform_Lib/TVScalerControl_Darwin/scaler/scalerLib.h
	public final static int SLR_COLORTEMP_USER = 0;
	public final static int SLR_COLORTEMP_NORMAL = 1;   //std
	public final static int SLR_COLORTEMP_WARMER = 2;   //6500K
	public final static int SLR_COLORTEMP_WARM = 3;     //7300K
	public final static int SLR_COLORTEMP_COOL = 4;     //8200K
	public final static int SLR_COLORTEMP_COOLER = 5;   //9300K
	public final static int SLR_COLORTEMP_MAX_NUM = 6;

	//AUDIO_EQUALIZER_TYPE
	public final static int AUDIO_EQUALIZER_STANDARD = 0;
	public final static int AUDIO_EQUALIZER_MUSIC = 1;
	public final static int AUDIO_EQUALIZER_NEWS = 2;
	public final static int AUDIO_EQUALIZER_FILM = 3;
	public final static int AUDIO_EQUALIZER_USER = 4;
	public final static int AUDIO_EQUALIZER_NUM = 5;

	//ENUM_EQUALIZER_MODE
	public final static int ENUM_EQUALIZER_RESERVED = 0;
	public final static int ENUM_EQUALIZER_MODE_POP = 1;
	public final static int ENUM_EQUALIZER_MODE_LIVE = 2;
	public final static int ENUM_EQUALIZER_MODE_CLUB = 3;
	public final static int ENUM_EQUALIZER_MODE_ROCK = 4;
	public final static int ENUM_EQUALIZER_MODE_BASS = 5;
	public final static int ENUM_EQUALIZER_MODE_TREBLE = 6;
	public final static int ENUM_EQUALIZER_MODE_VOCAL = 7;
	public final static int ENUM_EQUALIZER_MODE_POWERFUL = 8;
	public final static int ENUM_EQUALIZER_MODE_DANCE = 9;
	public final static int ENUM_EQUALIZER_MODE_SOFT = 10;
	public final static int ENUM_EQUALIZER_MODE_PARTY = 11;
	public final static int ENUM_EQUALIZER_MODE_CLASSICAL = 12;

	//ENUM_MAGIC_PICTURE
	public final static int MAGIC_PICTURE_OFF = 0;
	public final static int MAGIC_PICTURE_STILLDEMO = 1;
	public final static int MAGIC_PICTURE_STILLDEMO_INVERSE = 2;
    public final static int MAGIC_PICTURE_DYNAMICDEMO = 3;
    public final static int MAGIC_PICTURE_MOVE = 4;
    public final static int MAGIC_PICTURE_MOVE_INVERSE = 5;
    public final static int MAGIC_PICTURE_ZOOM = 6;
    public final static int MAGIC_PICTURE_OPTIMIZE = 7;
    public final static int MAGIC_PICTURE_ENHANCE = 8;

    //ENUM_DREAM_PANEL
    public final static int DREAM_PANEL_OFF = 0;
    public final static int DREAM_PANEL_ENVIRONMENT = 1;
    public final static int DREAM_PANEL_IMAGE = 2;
    public final static int DREAM_PANEL_MULTIPLE = 3;
    public final static int DREAM_PANEL_DEMO = 4;
    public final static int DREAM_PANEL_MAX = 5;

	//ENUM_MENU_LANGUAGE  =>  Include/Application/AppClass/setupdef.h
	public final static int MENU_LANG_ENGLISH  = 0;
	public final static int MENU_LANG_CHINESE  = 1;
    public final static int MENU_LANG_JAPANESE = 2;
    public final static int MENU_LANG_SPANISH  = 3;
    public final static int MENU_LANG_FRENCH   = 4;
    public final static int MENU_LANG_GERMAN   = 5;
    public final static int MENU_LANG_ITALIAN  = 6;
    public final static int MENU_LANG_KOREAN   = 7;
    public final static int MENU_LANG_DUTCH    = 8;
    public final static int MENU_LANG_RUSSIAN  = 9;
    public final static int MENU_LANG_SCHINESE = 10;
    public final static int MENU_LANG_MAX_NUM  = 11;

     //SLR_3D_MODE;
    public final static int SLR_3DMODE_2D = 0;
    public final static int SLR_3DMODE_3D_AUTO = 1;
    public final static int SLR_3DMODE_3D_SBS = 2;
    public final static int SLR_3DMODE_3D_TB = 3;
    public final static int SLR_3DMODE_3D_FP = 4;
    public final static int SLR_3DMODE_2D_CVT_3D = 5;
    // --- new 3D format ---
    public final static int SLR_3DMODE_3D_LBL = 6;
    public final static int SLR_3DMODE_3D_VSTRIP = 7;
    public final static int SLR_3DMODE_3D_CKB = 8;
    public final static int SLR_3DMODE_3D_REALID = 9;
    public final static int SLR_3DMODE_3D_SENSIO = 10;
    // -------------------
    public final static int SLR_3DMODE_3D_AUTO_CVT_2D = 11;
    public final static int SLR_3DMODE_3D_SBS_CVT_2D = 12;
    public final static int SLR_3DMODE_3D_TB_CVT_2D = 13;
    public final static int SLR_3DMODE_3D_FP_CVT_2D = 14;
    // --- new 3D format ---
    public final static int SLR_3DMODE_3D_LBL_CVT_2D = 15;
    public final static int SLR_3DMODE_3D_VSTRIP_CVT_2D = 16;
    public final static int SLR_3DMODE_3D_CKB_CVT_2D = 17;
    public final static int SLR_3DMODE_3D_REALID_CVT_2D = 18;
    public final static int SLR_3DMODE_3D_SENSIO_CVT_2D = 19;
    // -------------------
    public final static int SLR_3DMODE_NUM = 20;
    // disable
    public final static int SLR_3DMODE_DISABLE = 0xff;

	//CHMGR_SORT_POLICY
	public final static int CHMGR_SORT_BY_CHNUM = 0;
	public final static int CHMGR_SORT_BY_FREQUENCY = 1;
	public final static int CHMGR_SORT_BY_USER = 2;

	//AUDIO_AO_CHANNEL_OUT_SWAP
	public final static int AUDIO_AO_CHANNEL_OUT_STEREO = 0x0;
	public final static int AUDIO_AO_CHANNEL_OUT_L_TO_R = 0x1;
	public final static int AUDIO_AO_CHANNEL_OUT_R_TO_L = 0x2;
	public final static int AUDIO_AO_CHANNEL_OUT_LR_SWAP = 0x3;

	//VIDEO_TRANSITION_TYPE
	public final static int VIDEO_TRANSITION_COPY = 0;
	public final static int VIDEO_TRANSITION_CROSSFADE = 1;
	public final static int VIDEO_TRANSITION_LEFT_TO_RIGHT = 2;
	public final static int VIDEO_TRANSITION_TOP_TO_BOTTOM = 3;
	public final static int VIDEO_TRANSITION_WATERFALL = 4;
	public final static int VIDEO_TRANSITION_SNAKE = 5;
	public final static int VIDEO_TRANSITION_RANDOM_BOX = 6;
	public final static int VIDEO_TRANSITION_DIAGONAL = 7;
	public final static int VIDEO_TRANSITION_FADEIN_FADEOUT = 8;
	public final static int VIDEO_TRANSITION_MOVE = 9;
	public final static int VIDEO_TRANSITION_CROSSFADE_KENBURNS = 10;
	public final static int VIDEO_TRANSITION_WINDOW = 11;
	public final static int VIDEO_TRANSITION_EXTEND = 12;
	public final static int VIDEO_TRANSITION_EXPAND = 13;
	public final static int VIDEO_TRANSITION_STEP_ALPHA = 14;
	public final static int VIDEO_TRANSITION_FLYING_RECTANGLE = 15;
	public final static int VIDEO_TRANSITION_VENETIAN_BLINDS = 16;
	public final static int VIDEO_TRANSITION_BLUR = 17;
	public final static int VIDEO_TRANSITION_CIRCLE = 18;
	public final static int VIDEO_TRANSITION_RIGHT_TO_LEFT = 19;
	public final static int VIDEO_TRANSITION_BOTTOM_TO_TOP = 20;
	public final static int VIDEO_TRANSITION_UNKNOWN = 21;

	// SLR_RATIO_TYPE
	public final static int SLR_RATIO_AUTO = 0;
	public final static int SLR_RATIO_4_3 = 1;
	public final static int SLR_RATIO_16_9 = 2;
	public final static int SLR_RATIO_14_9 = 3;
	public final static int SLR_RATIO_LETTERBOX = 4;
	public final static int SLR_RATIO_PANORAMA = 5;
	public final static int SLR_RATIO_FIT = 6;
	public final static int SLR_RATIO_POINTTOPOINT = 7;
	public final static int SLR_RATIO_BBY_AUTO = 8;
	public final static int SLR_RATIO_BBY_NORMAL = 9;
	public final static int SLR_RATIO_BBY_ZOOM = 10;
	public final static int SLR_RATIO_BBY_WIDE_1 = 11;
	public final static int SLR_RATIO_BBY_WIDE_2 = 12;
	public final static int SLR_RATIO_BBY_CINEMA = 13;
	public final static int SLR_RATIO_CUSTOM = 14;
	public final static int SLR_ASPECT_RATIO_PERSON = 15;
	public final static int SLR_ASPECT_RATIO_CAPTION = 16;
	public final static int SLR_ASPECT_RATIO_MOVIE = 17;
	public final static int SLR_ASPECT_RATIO_100 = 18;
	public final static int SLR_ASPECT_RATIO_SOURCE = 19;
	public final static int SLR_RATIO_ZOOM_14_9 = 20;
	public final static int SLR_RATIO_DISABLE = 0xff;//disable 

	//Get decode image result
    public final static long DEC_IMG_DECODING = 1;
    public final static long DEC_IMG_SUCCESS = 0;
    public final static long DEC_IMG_FAIL = -1;
	public final static long DEC_IMG_RESOURCE_LOCKED = -2;
	public final static long DEC_IMG_HANDLE_INVALID  = -3;
	public final static long DEC_IMG_PARAM_INVALID   = -4;
    public final static long DEC_IMG_NOT_WORKING = -5;

    // GLDC OSD SHOW mode
    public final static int GLDC_OSD_SHOW = 1;
    public final static int GLDC_OSD_HIDE = 0;

	//QUADFHD_SETTING_MODE_TYPE 
	public final static int QUADFHD_NATIVE_MODE = 0;         
	public final static int QUADFHD_PHOTO_MODE = 1;       
	public final static int QUADFHD_VIDEO_MODE = 2;
	public final static int QUADFHD_FS_THREED_MODE = 3;
	public final static int QUADFHD_SBS_BYPASS_MODE = 4;
	public final static int QUADFHD_TAB_BYPASS_MODE = 5;
	public final static int QUADFHD_QUAD_OSD_MODE = 6;

    private final ITvManager mService;


    /**
     * package private on purpose
     */
    TvManager(ITvManager service) {
        mService = service;
    }

	//SystemCommandExecutor
    public void stopRpcServer() {
        try {
            mService.stopRpcServer();
        } catch (RemoteException ex) {
        }
    }

    public void dumpPliMemoryUsage() {
        try {
            mService.dumpPliMemoryUsage();
        } catch (RemoteException ex) {
        }
    }

    public void suspendRpcServer() {
        try {
            mService.suspendRpcServer();
        } catch (RemoteException ex) {
        }
    }

    public void resumeRpcServer() {
        try {
            mService.resumeRpcServer();
        } catch (RemoteException ex) {
        }
    }

    public void restoreSysDefSetting() {
        try {
            mService.restoreSysDefSetting();
        } catch (RemoteException ex) {
        }
    }

	public void setScreenSaverTiming(int itiming){
		try{
			mService.setScreenSaverTiming(itiming);
		}catch(RemoteException ex){
		}
	}

	public void setMenuLanguage(int iLanguage){
		try{
			mService.setMenuLanguage(iLanguage);
		}catch(RemoteException ex){
		}
	}

	public void setSerialCode(String strSerCode){
		try{
			mService.setSerialCode(strSerCode);
		}catch(RemoteException ex){
		}
	}

	public int getScreenSaverTiming(){
		try{
			return	mService.getScreenSaverTiming();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int getMenuLanguage(){
		try{
			return	mService.getMenuLanguage();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public String getSerialCode(){
		try{
			return mService.getSerialCode();
		}catch(RemoteException ex){
		}
		return null;
	}

    public void startPerformanceLog(){
        try {
            mService.startPerformanceLog();
        } catch (RemoteException ex) {
        }
    }

	public void stopPerformanceLog(String strFilePath){
		try{
			mService.stopPerformanceLog(strFilePath);
		}catch(RemoteException ex){
		}

	} 

    public void clearEeprom(){
        try {
            mService.clearEeprom();
        } catch (RemoteException ex) {
        }
    }

	public boolean readEepToFile(){
		boolean result = false;
		
		try{
			result = mService.readEepToFile();
		} catch(RemoteException ex){
		}

		return result;
	}

	public boolean writeFileToEep(){
		boolean result = false;
		
		try{
			result = mService.writeFileToEep();
		} catch(RemoteException ex){
		}

		return result;
	}

    public void upgradeSystem(){
        try {
            mService.upgradeSystem();
        } catch (RemoteException ex) {
        }
    }

	public String getCurAPK(){
		try{
			return mService.getCurAPK();
		} catch (RemoteException ex) {
		}
		return null;
	}

    //TvChannelApiExecutor
	public boolean tvAutoScanStart(int tvType, boolean updateScan) {
        try {
            return mService.tvAutoScanStart(tvType, updateScan);
        } catch (RemoteException ex) {
			return false;
        }
    }

    public void tvAutoScanStop() {
        try {
            mService.tvAutoScanStop();
        } catch (RemoteException ex) {
        }
    }

    public void tvAutoScanComplete() {
        try {
            mService.tvAutoScanComplete();
        } catch (RemoteException ex) {
        }
    }

	public boolean tvSeekScanStart(boolean bSeekForward ){
		boolean result = false;
		
		try{
			result = mService.tvSeekScanStart(bSeekForward);
		} catch(RemoteException ex){
		}

		return result;
	}

	public void tvSeekScanStop(){
		try{
			mService.tvSeekScanStop();
		} catch(RemoteException ex){
		}
	}
	
	public boolean tvScanManualStart(int iFreq, int iBandWidth, int iPhyChNum){
		boolean result = false;

		try{
			result = mService.tvScanManualStart(iFreq, iBandWidth, iPhyChNum);
		}catch(RemoteException ex){
		}

		return result;
	}

	public void tvScanManualStop(){
		try{
			mService.tvScanManualStop();
		}catch(RemoteException ex){
		}
	}

	public void tvScanManualComplete(){
		try{
			mService.tvScanManualComplete();
		}catch(RemoteException ex){
		}
	}

	public int tvScanInfo(int infoId){
		try{
			return	mService.tvScanInfo(infoId);
		}catch(RemoteException ex){
		}
		return 0;
	}

	public boolean isTvScanning(){
		try{
			return	mService.isTvScanning();
		}catch(RemoteException ex){
		}
		return false;
	}

	public int getAtvSeqScanStartFreq(){
		try{
			return	mService.getAtvSeqScanStartFreq();
		}catch(RemoteException ex){
		}
		return 0;
	}
	
	public int getAtvSeqScanEndFreq(){
		try{
			return	mService.getAtvSeqScanEndFreq();
		}catch(RemoteException ex){
		}
		return 0;
	}
	
	public void saveChannel(){
		try{
			mService.saveChannel();
		}catch(RemoteException ex){
		}
	}

	public void playNextChannel(){
		try{
			mService.playNextChannel();
		}catch(RemoteException ex){
		}
	}

	public void playPrevChannel(){
		try{
			mService.playPrevChannel();
		}catch(RemoteException ex){
		}
	}

	public void playFirstChannel(){
		try{
			mService.playFirstChannel();
		}catch(RemoteException ex){
		}
	}

	public void playHistoryChannel(){
		try{
			mService.playHistoryChannel();
		}catch(RemoteException ex){
		}
	}

    public void updateTvChannelList(ChannelFilter filter) {
        try {
            mService.updateTvChannelList(filter);
        } catch (RemoteException ex) {
        }
    }

    public ChannelInfo getCurChannel() {
        try {
            return mService.getCurChannel();
        } catch (RemoteException ex) {
            return null;
        }
    }

    public ChannelInfo getChannelInfoByIndex(int iIndex) {
        try {
            return mService.getChannelInfoByIndex(iIndex);
        } catch (RemoteException ex) {
            return null;
        }
    }

    public int getChannelCount() {
        try {
            return mService.getChannelCount();
        } catch (RemoteException ex) {
            return 0;
        }
    }
	
	public boolean sortChannel(int policy){
		//CHMGR_SORT_POLICY
		try{
			return mService.sortChannel(policy);
		}catch(RemoteException ex){
		}
		return false;
	}

	public void playChannelByIndex(int iIndex){
		try{
			mService.playChannelByIndex(iIndex);
		}catch(RemoteException ex){
		}
	}

	public void playChannelByNum(int iNum){
		try{
			mService.playChannelByNum(iNum);
		}catch(RemoteException ex){
		}
	}

	public boolean playChannel(int iIndex){
		boolean result = false;

		try{
			result = mService.playChannel(iIndex);
		}catch(RemoteException ex){
		}

		return result;
	}
	
	public boolean playChannelByLCN(int iLcnNum){
		boolean result = false;

		try{
			result = mService.playChannelByLCN(iLcnNum);
		}catch(RemoteException ex){
		}

		return result;
	}
	
	public boolean playFirstChannelInFreq(int iFreq){
		boolean result = false;

		try{
			result = mService.playFirstChannelInFreq(iFreq);
		}catch(RemoteException ex){
		}

		return result;
	}
	
	public boolean playChannelByChnumFreq(int iSysChNum, int iFreq, String tvSystem){
		boolean result = false;

		try{
			result = mService.playChannelByChnumFreq(iSysChNum, iFreq, tvSystem);
		}catch(RemoteException ex){
		}

		return result;
	}
	
	public boolean playNumberChannel(int majorNum, int minorNum, boolean isAudioFocus){
		boolean result = false;

		try{
			result = mService.playNumberChannel(majorNum, minorNum, isAudioFocus);
		}catch(RemoteException ex){
		}

		return result;
	}		

	public void swapChannelByIdxEx(int iChIdx, boolean bSwapChNum, boolean bPlayAfterSwap){
		try{
			mService.swapChannelByIdxEx(iChIdx, bSwapChNum, bPlayAfterSwap);
		}catch(RemoteException ex){
		}
	}

	public void swapChannelByNumEx(int iChNum, boolean bSwapChNum, boolean bPlayAfterSwap){
		try{
			mService.swapChannelByNumEx(iChNum, bSwapChNum, bPlayAfterSwap);
		}catch(RemoteException ex){
		}
	}


	public void ReloadLastPlayedSource(){
		try{
			mService.reloadLastPlayedSource();
		}catch(RemoteException ex){
		}
	}

	public void setCurChannelSkipped(boolean bSkip){
		try{
			mService.setCurChannelSkipped(bSkip);
		}catch(RemoteException ex){
		}
	}

	public void setCurAtvSoundStd(int soundSystemId){
		try{
			mService.setCurAtvSoundStd(soundSystemId);
		}catch(RemoteException ex){
		}
	}

	public void fineTuneCurFrequency(int iOffset, boolean bPerminant){
		try{
			mService.fineTuneCurFrequency(iOffset, bPerminant);
		}catch(RemoteException ex){
		}
	}

	public void setCurChAudioCompensation(int iValue, boolean bApply){
		try{
			mService.setCurChAudioCompensation(iValue, bApply);
		}catch(RemoteException ex){
		}
	}

	public boolean setSource(int iIndex){
		try{
			return mService.setSource(iIndex);
		}catch(RemoteException ex){
		}

		return false;
	}

	public void setBootSource(int sourceOpt){
		try{
			mService.setBootSource(sourceOpt);
		}catch(RemoteException ex){
		}
	}

	public boolean setSourceAndDisplayWindow(int src, int x, int y, int width, int height){
		boolean result = false;

		try{
			result = mService.setSourceAndDisplayWindow(src, x, y, width, height);
		}catch(RemoteException ex){
		}
		return result;
	}

	public boolean getCurChannelSkipped(){
		boolean result = false;
		
		try{
			result = mService.getCurChannelSkipped();
		} catch(RemoteException ex){
		}
		return result;
	}

	public int getCurAtvSoundStd(){
		//RT_ATV_SOUND_SYSTEM
		try{
			return	mService.getCurAtvSoundStd();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int getCurChAudioCompensation(){
		try{
			return	mService.getCurChAudioCompensation();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public String getSourceList(){
		try{
			return	mService.getSourceList();
		}catch(RemoteException ex){
		}
		return null;
	}

	public int getSourceListCnt(boolean bWithoutPlayback){
		try{
			return	mService.getSourceListCnt(bWithoutPlayback);
		}catch(RemoteException ex){
		}
		return 0;
	}
	
	public long getCurSourceType(){
		try{
			return	mService.getCurSourceType();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int getBootSource(){
		try{
			return	mService.getBootSource();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int getCurTvType(){
		try{
			return	mService.getCurTvType();
		}catch(RemoteException ex){
		}
		return 0;
	}		
	
	public int getCurLiveSource(){
		try{
			return mService.getCurLiveSource();
		}catch(RemoteException ex){
		}
		return 0;
	}


    //TvDisplaySetupApiExecutor
	public void setBrightness(int iValue){
		try{
			mService.setBrightness(iValue);
		}catch(RemoteException ex){
		}
	}

	public void setContrast(int iValue){
		try{
			mService.setContrast(iValue);
		}catch(RemoteException ex){
		}
	}

	public void setSaturation(int iValue){
		try{
			mService.setSaturation(iValue);
		}catch(RemoteException ex){
		}
	}

	public void setHue(int iValue){
		try{
			mService.setHue(iValue);
		}catch(RemoteException ex){
		}
	}

	public void setSharpness(boolean iApply, int iValue){
		try{
			mService.setSharpness(iApply, iValue);
		}catch(RemoteException ex){
		}
	}

	public void setColorTempMode(int level){
		try{
			mService.setColorTempMode(level);
		}catch(RemoteException ex){
		}
	}

	public void setDNR(int mode){
		try{
			mService.setDNR(mode);
		}catch(RemoteException ex){
		}
	}

	public void setAspectRatio(int ratio){
		try{
			mService.setAspectRatio(ratio);
		}catch(RemoteException ex){
		}
	}

	public void saveAspectRatio(int ratio){
		try{
			mService.saveAspectRatio(ratio);
		}catch(RemoteException ex){
		}
	}

	public void setPictureMode(int mode){
		try{
			mService.setPictureMode(mode);
		}catch(RemoteException ex){
		}
	}

	public void setCurAtvColorStd(int colorStd){
		try{
			mService.setCurAtvColorStd(colorStd);
		}catch(RemoteException ex){
		}
	}

	public void setAvColorStd(int colorStd){
		try{
			mService.setAvColorStd(colorStd);
		}catch(RemoteException ex){
		}
	}

	public boolean setVgaAutoAdjust(){
		boolean result = false;

		try{
			result = mService.setVgaAutoAdjust();
		}catch(RemoteException ex){
		}

		return result;
	}

	public boolean setVgaHPosition(char ucPosition){
		boolean result = false;

		try{
			result = mService.setVgaHPosition(ucPosition);
		}catch(RemoteException ex){
		}

		return result;
	}

	public boolean setVgaVPosition(char ucPosition){
		boolean result = false;

		try{
			result = mService.setVgaVPosition(ucPosition);
		}catch(RemoteException ex){
		}

		return result;
	}

	public boolean setVgaPhase(char ucValue){
		boolean result = false;

		try{
			result = mService.setVgaPhase(ucValue);
		}catch(RemoteException ex){
		}

		return result;
	}

	public boolean setVgaClock(char ucValue){
		boolean result = false;

		try{
			result = mService.setVgaClock(ucValue);
		}catch(RemoteException ex){
		}

		return result;
	}

	public void setMagicPicture(int magicPic){
		try{
			mService.setMagicPicture(magicPic);
		}catch(RemoteException ex){
		}
	}

	public void setDCR(int iDCR){
		try{
			mService.setDCR(iDCR);
		}catch(RemoteException ex){
		}
	}

	public void setDCC(boolean iDccOn, boolean iIsApply){
		try{
			mService.setDCC(iDccOn, iIsApply);
		}catch(RemoteException ex){
		}
	}

	public void setBacklight(int iValue){
		try{
			mService.setBacklight(iValue);
		}catch(RemoteException ex){
		}
	}

	public void set3dMode(int imode){
		try{
			mService.set3dMode(imode);
		}catch(RemoteException ex){
		}
	}

	public void set3dDeep(int imode){
		try{
			mService.set3dDeep(imode);
		}catch(RemoteException ex){
		}
	}

    public void set3dLRSwap(boolean bOn) {
        try {
            mService.set3dLRSwap(bOn);
        } catch (RemoteException ex) {
        }
    }

    public boolean set3dCvrt2D(boolean bOn, int iFrameFlag) {
		boolean result = false;
        
		try {
            result = mService.set3dCvrt2D(bOn, iFrameFlag);
        } catch (RemoteException ex) {
        }

		return result;
    }

	public void set3dStrength(int iStrength){
		try{
			mService.set3dStrength(iStrength);
		}catch(RemoteException ex){
		}
	}

	public boolean set3dModeAndChangeRatio(int iMode, boolean bMute, int iType){
		boolean result = false;

		try{
			result = mService.set3dModeAndChangeRatio(iMode, bMute, iType);
		}catch(RemoteException ex){
		}
		return result;
	}

	public boolean getIs3d(){
		boolean result = false;

		try{
			result = mService.getIs3d();
		} catch(RemoteException ex){
		}
		return result;
	}

	public void setColorTempRGain(int iValue){
		try{
			mService.setColorTempRGain(iValue);
		}catch(RemoteException ex){
		}
	}

	public void setColorTempGGain(int iValue){
		try{
			mService.setColorTempGGain(iValue);
		}catch(RemoteException ex){
		}
	}

	public void setColorTempBGain(int iValue){
		try{
			mService.setColorTempBGain(iValue);
		}catch(RemoteException ex){
		}
	}

	public void setColorTempROffset(int iValue){
		try{
			mService.setColorTempROffset(iValue);
		}catch(RemoteException ex){
		}
	}

	public void setColorTempGOffset(int iValue){
		try{
			mService.setColorTempGOffset(iValue);
		}catch(RemoteException ex){
		}
	}

	public void setColorTempBOffset(int iValue){
		try{
			mService.setColorTempBOffset(iValue);
		}catch(RemoteException ex){
		}
	}

	public void setDisplayWindow(int iX, int iY, int iWidth, int iHeight){
		try{
			mService.setDisplayWindow(iX, iY, iWidth, iHeight);
		}catch(RemoteException ex){
		}
	}

	public void setPanelOn(boolean bOn){
		try{
			mService.setPanelOn(bOn);
		}catch(RemoteException ex){
		}
	}

	public void scaler_ForceBg(boolean bOn){
		try{
			mService.scaler_ForceBg(bOn);
		}catch(RemoteException ex){
		}
	}

	public void scaler_4k2kOSD_ForceBg(boolean bOn){
		try{
			mService.scaler_4k2kOSD_ForceBg(bOn);
		}catch(RemoteException ex){
		}
	}

	public void setOverScan(int h_start, int h_width, int v_start, int v_length){
		try{
			mService.setOverScan(h_start, h_width, v_start, v_length);
		}catch(RemoteException ex){
		}
	}

	public int getBrightness(){
		try{
			return	mService.getBrightness();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int getContrast(){
		try{
			return	mService.getContrast();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int getSaturation(){
		try{
			return	mService.getSaturation();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int getHue(){
		try{
			return	mService.getHue();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int getSharpness(){
		try{
			return	mService.getSharpness();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int getColorTempLevel(){
		try{
			return	mService.getColorTempLevel();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int getDNR(){
		try{//DNR_MODE 
			return	mService.getDNR();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int getPictureMode(){
		try{//PICTURE_MODE 
			return	mService.getPictureMode();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int getCurAtvColorStd(){
		try{//RT_COLOR_STD 
			return	mService.getCurAtvColorStd();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int getCurAtvColorStdNoAuto(){
		try{//RT_COLOR_STD 
			return	mService.getCurAtvColorStdNoAuto();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int getAvColorStd(){
		try{//RT_COLOR_STD 
			return	mService.getAvColorStd();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int getAvColorStdNoAuto(){
		try{//RT_COLOR_STD 
			return	mService.getAvColorStdNoAuto();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public char getVgaHPosition(){
		try{ 
			return	mService.getVgaHPosition();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public char getVgaVPosition(){
		try{ 
			return	mService.getVgaVPosition();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public char getVgaPhase(){
		try{ 
			return	mService.getVgaPhase();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public char getVgaClock(){
		try{ 
			return	mService.getVgaClock();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int getAspectRatio(int iSourceOption){
		try{//ENUM_ASPECT_RATIO --(int iSourceOption)
			return	mService.getAspectRatio(iSourceOption);
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int getMagicPicture(){
		try{//ENUM_MAGIC_PICTURE --()
			return mService.getMagicPicture();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int getDCR(){
		try{//ENUM_DREAM_PANEL --()
			return mService.getDCR();
		}catch(RemoteException ex){
		}
		return 0;
	}
	public String getDCRDemoDate(){
		try{
			return	mService.getDCRDemoDate();
		}catch(RemoteException ex){
		}
		return null;
	}

	public int getBacklight(){
		try{
			return mService.getBacklight();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int get3dMode(){
		try{
			return mService.get3dMode();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int get3dDeep(){
		try{
			return mService.get3dDeep();
		}catch(RemoteException ex){
		}
		return 0;
	}
	
	public boolean get3dLRSwap(){
		boolean result = false;
		
		try{
			result = mService.get3dLRSwap();
		} catch(RemoteException ex){
		}
		return result;
	}

	public boolean get3dCvrt2D(){
		boolean result = false;
		
		try{
			result = mService.get3dCvrt2D();
		} catch(RemoteException ex){
		}
		return result;
	}
	
	public boolean getPanelOn(){
		boolean result = false;
		
		try{
			result = mService.getPanelOn();
		} catch(RemoteException ex){
		}
		return result;
	}

	public String getResolutionInfo(){
		try{
			return	mService.getResolutionInfo();
		}catch(RemoteException ex){
		}
		return null;
	}

	public boolean checkDviMode(){
		boolean result = false;
		
		try{
			result = mService.checkDviMode();
		} catch(RemoteException ex){
		}
		return result;
	}

	public String getOverScan(){
		try{
			return	mService.getOverScan();
		}catch(RemoteException ex){
		}
		return null;
	}

	//TvFactoryApiExecutor
	public void setScalerAutoColorRGain(int iValue){
		try{
			mService.setScalerAutoColorRGain(iValue);
		}catch(RemoteException ex){
		}
	}

	public void setScalerAutoColorGGain(int iValue){
		try{
			mService.setScalerAutoColorGGain(iValue);
		}catch(RemoteException ex){
		}
	}

	public void setScalerAutoColorBGain(int iValue){
		try{
			mService.setScalerAutoColorBGain(iValue);
		}catch(RemoteException ex){
		}
	}

	public void setScalerAutoColorROffset(int iValue){
		try{
			mService.setScalerAutoColorROffset(iValue);
		}catch(RemoteException ex){
		}
	}

	public void setScalerAutoColorGOffset(int iValue){
		try{
			mService.setScalerAutoColorGOffset(iValue);
		}catch(RemoteException ex){
		}
	}

	public void setScalerAutoColorBOffset(int iValue){
		try{
			mService.setScalerAutoColorBOffset(iValue);
		}catch(RemoteException ex){
		}
	}

	//TvSoundSetupApiExecutor
	public void setVolume(int iValue){
		try{
			mService.setVolume(iValue);
		}catch(RemoteException ex){
		}
	}

    public void setMute(boolean mute) {
        try {
            mService.setMute(mute);
        } catch (RemoteException ex) {
        }
    }

	public void setBalance(int iValue){
		try{
			mService.setBalance(iValue);
		}catch(RemoteException ex){
		}
	}

	public void setBass(int iValue){
		try{
			mService.setBass(iValue);
		}catch(RemoteException ex){
		}
	}

	public void setTreble(int iValue){
		try{
			mService.setTreble(iValue);
		}catch(RemoteException ex){
		}
	}

    public void setTrueSurround(boolean bEnable) {
        try {
            mService.setTrueSurround(bEnable);
        } catch (RemoteException ex) {
        }
    }

    public void setClarity(boolean bEnable) {
        try {
            mService.setClarity(bEnable);
        } catch (RemoteException ex) {
        }
    }

    public void setTrueBass(boolean bEnable) {
        try {
            mService.setTrueBass(bEnable);
        } catch (RemoteException ex) {
        }
    }

    public void setSubWoof(boolean bEnable) {
        try {
            mService.setSubWoof(bEnable);
        } catch (RemoteException ex) {
        }
    }

	public void setSubWoofVolume(int iValue){
		try{
			mService.setSubWoofVolume(iValue);
		}catch(RemoteException ex){
		}
	}

	public void setEqualizerMode(int iMode){
		try{
			mService.setEqualizerMode(iMode);
		}catch(RemoteException ex){
		}
	}

	public void setEqualizer(int iFreq, int iValue){
		try{
			mService.setEqualizer(iFreq, iValue);
		}catch(RemoteException ex){
		}
	}

    public void setOnOffMusic(boolean bOn) {
        try {
            mService.setOnOffMusic(bOn);
        } catch (RemoteException ex) {
        }
    }

	public void setAudioHdmiOutput(int mode){
		try{
			mService.setAudioHdmiOutput(mode);
		}catch(RemoteException ex){
		}
	}

	public void setSurroundSound(int mode){
		try{
			mService.setSurroundSound(mode);
		}catch(RemoteException ex){
		}
	}

	public void setAudioSpdifOutput(int mode){
		try{
			mService.setAudioSpdifOutput(mode);
		}catch(RemoteException ex){
		}
	}

	public void setWallEffect(boolean bEnable) {
        try {
            mService.setWallEffect(bEnable);
        } catch (RemoteException ex) {
        }
    }

	public void setAudioMode(int mode){
		try{
			mService.setAudioMode(mode);
		}catch(RemoteException ex){
		}
	}

	public void setAudioEffect(int audioEffect, int param){
		try{
			mService.setAudioEffect(audioEffect, param);
		}catch(RemoteException ex){
		}
	}	

	public void setKeyToneVolume(int iVol){
		try{
			mService.setKeyToneVolume(iVol);
		}catch(RemoteException ex){
		}
	}

	/**
	 * TvServer's SetSource() will restore audio swapped channel to default value.
	 * MediaPlayServer will restore audio swapped channel to default value when user stops to play media.
	 */
	public void setAudioChannelSwap(int sel){
		//AUDIO_AO_CHANNEL_OUT_SWAP
		try{
			mService.setAudioChannelSwap(sel);
		}catch(RemoteException ex){
		}
	}

	public int getVolume(){
		try{
			return mService.getVolume();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public boolean getMute(){
		boolean result = false;
		
		try{
			result = mService.getMute();
		} catch(RemoteException ex){
		}
		return result;
	}

	public int getBalance(){
		try{ 
			return	mService.getBalance();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int getBass(){
		try{ 
			return	mService.getBass();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int getTreble(){
		try{ 
			return	mService.getTreble();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int getEqualizerMode(){
		try{
			return	mService.getEqualizerMode();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int getEqualizer(int iFreq){
		try{
			return	mService.getEqualizer(iFreq);
		}catch(RemoteException ex){
		}
		return 0;
	}

	public boolean getTrueSurround(){
		boolean result = false;
		
		try{
			result = mService.getTrueSurround();
		} catch(RemoteException ex){
		}

		return result;
	}

	public boolean getClarity(){
		boolean result = false;
		
		try{
			result = mService.getClarity();
		} catch(RemoteException ex){
		}

		return result;
	}

	public boolean getTrueBass(){
		boolean result = false;
		
		try{
			result = mService.getTrueBass();
		} catch(RemoteException ex){
		}

		return result;
	}

	public boolean getSubWoof(){
		boolean result = false;
		
		try{
			result = mService.getSubWoof();
		} catch(RemoteException ex){
		}

		return result;
	}

	public int getSubWoofVolume(){
		try{ 
			return	mService.getSubWoofVolume();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public boolean getOnOffMusic(){
		boolean result = false;
		
		try{
			result = mService.getOnOffMusic();
		} catch(RemoteException ex){
		}

		return result;
	}

	public boolean getWallEffect(){
		boolean result = false;
		
		try{
			result = mService.getWallEffect();
		} catch(RemoteException ex){
		}

		return result;
	}

	public int getAudioMode(){
		try{ 
			return	mService.getAudioMode();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public boolean getAudioVolume(){
		boolean result = false;
		
		try{
			result = mService.getAudioVolume();
		} catch(RemoteException ex){
		}

		return result;
	}

	public int getKeyToneVolume(){
		try{ 
			return	mService.getKeyToneVolume();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int getAudioChannelSwap(){
		try{ 
			return	mService.getAudioChannelSwap();
		}catch(RemoteException ex){
		}
		return 0;
	}

	//Invoke WLanSetupApiExecutor functions
	void setWLanTmpProfileName(String pStrName){
		try{
			mService.setWLanTmpProfileName(pStrName);
		}catch(RemoteException ex){
		}
	}

	void setWLanTmpProfileSSID(String pStrSSID){
		try{
			mService.setWLanTmpProfileSSID(pStrSSID);
		}catch(RemoteException ex){
		}
	}

	public void setWLanTmpProfileWifiMode(int mode){
		//ENUM_WIRELESS_MODE
		try{
			mService.setWLanTmpProfileWifiMode(mode);
		}catch(RemoteException ex){
		}
	}

	public void setWLanTmpProfileWifiSecurity(int security, String pStrKey){
		try{//ENUM_WIRELESS_SECURITY
			mService.setWLanTmpProfileWifiSecurity(security, pStrKey);
		}catch(RemoteException ex){
		}
	}

	public void setWLanTmpProfileDhcpHostIp(char ip1, char ip2, char ip3, char ip4){
		try{
			mService.setWLanTmpProfileDhcpHostIp(ip1, ip2, ip3, ip4);
		}catch(RemoteException ex){
		}
	}

	public void setWLanTmpProfileDhcpStartIp(char ip1, char ip2, char ip3, char ip4){
		try{
			mService.setWLanTmpProfileDhcpStartIp(ip1, ip2, ip3, ip4);
		}catch(RemoteException ex){
		}
	}

	public void setWLanTmpProfileDhcpEndIp(char ip1, char ip2, char ip3, char ip4){
		try{
			mService.setWLanTmpProfileDhcpEndIp(ip1, ip2, ip3, ip4);
		}catch(RemoteException ex){
		}
	}

	public void setWLanTmpProfileWepIndex(int iIndex){
		try{
			mService.setWLanTmpProfileWepIndex(iIndex);
		}catch(RemoteException ex){
		}
	}

	public void setWLanProfileCopyToTmp(int iProfileIndex){
		try{
			mService.setWLanProfileCopyToTmp(iProfileIndex);
		}catch(RemoteException ex){
		}
	}

	public void setWLanProfileCopyFromTmp(int iProfileIndex){
		try{
			mService.setWLanProfileCopyFromTmp(iProfileIndex);
		}catch(RemoteException ex){
		}
	}

	public void setWLanIpAddr(int netType, char ip1, char ip2, char ip3, char ip4){
		try{//ENUM_NET_IP_TYPE
			mService.setWLanIpAddr(netType, ip1, ip2, ip3, ip4);
		}catch(RemoteException ex){
		}
	}

	public void setWLanProfileActiveIndex(int iProfileIndex){
		try{
			mService.setWLanProfileActiveIndex(iProfileIndex);
		}catch(RemoteException ex){
		}
	}

	public String getWLanProfileName(int iProfileIndex){
		try{
			return	mService.getWLanProfileName(iProfileIndex);
		}catch(RemoteException ex){
		}
		return null;
	}

	public String getWLanProfileSSID(int iProfileIndex){
		try{
			return	mService.getWLanProfileSSID(iProfileIndex);
		}catch(RemoteException ex){
		}
		return null;
	}

	public int getWLanProfileWifiMode(int iProfileIndex){
		try{//ENUM_WIRELESS_MODE --(int iProfileIndex)
			return	mService.getWLanProfileWifiMode(iProfileIndex);
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int getWLanProfileWifiSecurity(int iProfileIndex){
		try{//ENUM_WIRELESS_SECURITY --(int iProfileIndex)
			return	mService.getWLanProfileWifiSecurity(iProfileIndex);
		}catch(RemoteException ex){
		}
		return 0;
	}

	public String getWLanProfileDhcpHostIp(int iProfileIndex){
		try{
			return	mService.getWLanProfileDhcpHostIp(iProfileIndex);
		}catch(RemoteException ex){
		}
		return null;
	}

	public String getWLanProfileDhcpStartIp(int iProfileIndex){
		try{
			return	mService.getWLanProfileDhcpStartIp(iProfileIndex);
		}catch(RemoteException ex){
		}
		return null;
	}

	public String getWLanProfileDhcpEndIp(int iProfileIndex){
		try{
			return	mService.getWLanProfileDhcpEndIp(iProfileIndex);
		}catch(RemoteException ex){
		}
		return null;
	}

	public int getWLanProfileWepIndex(int iProfileIndex){
		try{
			return	mService.getWLanProfileWepIndex(iProfileIndex);
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int getWLanProfileActiveIndex(){
		try{
			return	mService.getWLanProfileActiveIndex();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int getWLanProfileTotalNumber(){
		try{
			return	mService.getWLanProfileTotalNumber();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public String getWLanIpAddr(int netType){
		try{//ENUM_NET_IP_TYPE
			return	mService.getWLanIpAddr(netType);
		}catch(RemoteException ex){
		}
		return null;
	}

	public boolean getWLanDHCPState(){
		try{
			return	mService.getWLanDHCPState();
		}catch(RemoteException ex){
		}
		return false;
	}

	public int getWLanApListSize(){
		try{
			return	mService.getWLanApListSize();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public String getWLanApName(int iApIndex){
		try{
			return	mService.getWLanApName(iApIndex);
		}catch(RemoteException ex){
		}
		return null;
	}

	public int getWLanApSecurity(int iApIndex){
		try{//ENUM_WIRELESS_SECURITY
			return	mService.getWLanApSecurity(iApIndex);
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int getWLanApStrength(int iApIndex){
		try{
			return	mService.getWLanApStrength(iApIndex);
		}catch(RemoteException ex){
		}
		return 0;
	}

	public void setWLanWpsMode(int mode){
		try{//ENUM_WPS_MODE
			mService.setWLanWpsMode(mode);
		}catch(RemoteException ex){
		}
	}

	public int getWLanWpsMode(){
		try{//ENUM_WPS_MODE --()
			return	mService.getWLanWpsMode();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public String getWLanPinCode(){
		try{
			return	mService.getWLanPinCode();
		}catch(RemoteException ex){
		}
		return null;
	}

	public void wLanConnectStart(int iProfileIndex){
		try{
			mService.wLanConnectStart(iProfileIndex);
		}catch(RemoteException ex){
		}
	}

	public void wLanConnectStop(boolean bForce){
		try{
			mService.wLanConnectStop(bForce);
		}catch(RemoteException ex){
		}
	}

	public int wLanConnectQueryState(){
		try{//NET_WIRELESS_STATE --()
			return	mService.wLanConnectQueryState();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public int wLan0ActivateState(){
		try{//NET_WIRELESS_STATE --()
			return	mService.wLan0ActivateState();
		}catch(RemoteException ex){
		}
		return 0;
	}

	//LanSetupApiExecutor
	public boolean wiredLanDHCPStart(){
		try{
			return	mService.wiredLanDHCPStart();
		}catch(RemoteException ex){
		}
		return false;
	}

	public void wiredLanDhcpStop(boolean bForceStop){
		try{
			mService.wiredLanDhcpStop(bForceStop);
		}catch(RemoteException ex){
		}
	}

	public int wiredLanDhcpQueryState(){
		try{//NET_WIRED_DHCP_STATE --()
			return	mService.wiredLanDhcpQueryState();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public boolean getWiredLanDhcpEnable(){
		try{
			return	mService.getWiredLanDhcpEnable();
		}catch(RemoteException ex){
		}
		return false;
	}

	public String getWiredLanIpAddr(int netType, boolean bFromDatabase){
		//String --(ENUM_NET_IP_TYPE, boolean)	
		try{
			return	mService.getWiredLanIpAddr(netType, bFromDatabase);
		}catch(RemoteException ex){
		}
		return null;
	}

	public String getMacAddressInfo(int iNetInterface){
		//String --(ENUM_NET_INTERFACE)	
		try{
			return	mService.getMacAddressInfo(iNetInterface);
		}catch(RemoteException ex){
		}
		return null;
	}

	public String getMacAddress(){
		try{
			return	mService.getMacAddress();
		}catch(RemoteException ex){
		}
		return null;
	}

	public String getSystemVersion(){
		try{
			return	mService.getSystemVersion();
		}catch(RemoteException ex){
		}
		return null;
	}

	public String getBootcodeVersion(){
		try{
			return	mService.getBootcodeVersion();
		}catch(RemoteException ex){
		}
		return null;
	}		

	public String getEepVersion(){
		try{
			return	mService.getEepVersion();
		}catch(RemoteException ex){
		}
		return null;
	}	

	public String getCpuVersion(){
		try{
			return	mService.getCpuVersion();
		}catch(RemoteException ex){
		}
		return null;
	}	
	
	public String getReleaseDate(){
		try{
			return	mService.getReleaseDate();
		}catch(RemoteException ex){
		}
		return null;
	}

	public String getPanelName(){
		try{
			return	mService.getPanelName();
		}catch(RemoteException ex){
		}
		return null;
	}	

	public int getUartMode(){
		try{ 
			return	mService.getUartMode();
		}catch(RemoteException ex){
		}
		return 0;
	}
	
	public int getEepPage(){
		try{ 
			return	mService.getEepPage();
		}catch(RemoteException ex){
		}
		return 0;
	}
	
	public int getEepOffset(){
		try{ 
			return	mService.getEepOffset();
		}catch(RemoteException ex){
		}
		return 0;
	}
	
	public int getEepData(){
		try{ 
			return	mService.getEepData();
		}catch(RemoteException ex){
		}
		return 0;
	}

	public void setUartMode(int iValue){
		try{
			mService.setUartMode(iValue);
		}catch(RemoteException ex){
		}
	}

	public void setEepPage(int iValue){
		try{
			mService.setEepPage(iValue);
		}catch(RemoteException ex){
		}
	}

	public void setEepOffset(int iValue){
		try{
			mService.setEepOffset(iValue);
		}catch(RemoteException ex){
		}
	}

	public void setEepData(int iValue){
		try{
			mService.setEepData(iValue);
		}catch(RemoteException ex){
		}
	}

	public void startWifi(){
		try{
			mService.startWifi();
		}catch(RemoteException ex){
		}
	}

	public void stopWifi(){
		try{
			mService.stopWifi();
		}catch(RemoteException ex){
		}
	}

	public boolean getWifiState(){
		try{
			return	mService.getWifiState();
		}catch(RemoteException ex){
		}
		return false;
	}
	
	public int getNsta(int type){
		try{
			return	mService.getNsta(type);
		}catch(RemoteException ex){
		}
		return 0;
	}
	
	public void setNsta(int type,int iValue){
		try{
			mService.setNsta(type,iValue);
		}catch(RemoteException ex){
		}
	}

	public int getPattern(){
		try{
			return	mService.getPattern();
		}catch(RemoteException ex){
		}
		return 0;
	}
	
	public void setPattern(int iValue){
		try{
			mService.setPattern(iValue);
		}catch(RemoteException ex){
		}
	}

	public void rebootSystem(){
		try{
			mService.rebootSystem();
		}catch(RemoteException ex){
		}
	}

	public void setFacSingleKey(boolean mode){
		try{
			mService.setFacSingleKey(mode);
		}catch(RemoteException ex){
		}
	}

	public boolean getFacSingleKey(){
		try{
			return	mService.getFacSingleKey();
		}catch(RemoteException ex){
		}
		return false;
	}

	public void setWarmMode(boolean mode){
		try{
			mService.setWarmMode(mode);
		}catch(RemoteException ex){
		}
	}

	public void exitSkyworthFactorySet(){
		try{
			mService.exitSkyworthFactorySet();
		}catch(RemoteException ex){
		}
	}

	public void setBusoffMode(boolean mode){
		try{
			mService.setBusoffMode(mode);
		}catch(RemoteException ex){
		}
	}

	public boolean getFacAutoScanGuide(){
		try{
			return	mService.getFacAutoScanGuide();
		}catch(RemoteException ex){
		}
		return false;
	}

	public void setFacAutoScanGuide(boolean mode){
		try{
			mService.setFacAutoScanGuide(mode);
		}catch(RemoteException ex){
		}
	}

	public boolean getFacWarmMode(){
		try{
			return	mService.getFacWarmMode();
		}catch(RemoteException ex){
		}
		return false;
	}

	public void setFacWarmMode(boolean mode){
		try{
			mService.setFacWarmMode(mode);
		}catch(RemoteException ex){
		}
	}

	public boolean getDDREnable(){
		try{
			return	mService.getDDREnable();
		}catch(RemoteException ex){
		}
		return false;
	}

	public void setDDREnable(boolean mode){
		try{
			mService.setDDREnable(mode);
		}catch(RemoteException ex){
		}
	}

	public boolean getDDRPhaseShift(){
		try{
			return	mService.getDDRPhaseShift();
		}catch(RemoteException ex){
		}
		return false;
	}

	public void setDDRPhaseShift(boolean mode){
		try{
			mService.setDDRPhaseShift(mode);
		}catch(RemoteException ex){
		}
	}

	public int getDDRStep(){
		try{
			return	mService.getDDRStep();
		}catch(RemoteException ex){
		}
		return 0;
	}
	
	public void setDDRStep(int iValue){
		try{
			mService.setDDRStep(iValue);
		}catch(RemoteException ex){
		}
	}

	public int getDDRPeriod(){
		try{
			return	mService.getDDRPeriod();
		}catch(RemoteException ex){
		}
		return 0;
	}
	
	public void setDDRPeriod(int iValue){
		try{
			mService.setDDRPeriod(iValue);
		}catch(RemoteException ex){
		}
	}

	public int getDDROffset(){
		try{
			return	mService.getDDROffset();
		}catch(RemoteException ex){
		}
		return 0;
	}
	
	public void setDDROffset(int iValue){
		try{
			mService.setDDROffset(iValue);
		}catch(RemoteException ex){
		}
	}

	public boolean getLVDSEnable(){
		try{
			return	mService.getLVDSEnable();
		}catch(RemoteException ex){
		}
		return false;
	}

	public void setLVDSEnable(boolean mode){
		try{
			mService.setLVDSEnable(mode);
		}catch(RemoteException ex){
		}
	}

	public int getLVDSDclkRange(){
		try{
			return	mService.getLVDSDclkRange();
		}catch(RemoteException ex){
		}
		return 0;
	}
	
	public void setLVDSDclkRange(int iValue){
		try{
			mService.setLVDSDclkRange(iValue);
		}catch(RemoteException ex){
		}
	}

	public int getLVDSDclkFMDIV(){
		try{
			return	mService.getLVDSDclkFMDIV();
		}catch(RemoteException ex){
		}
		return 0;
	}
	
	public void setLVDSDclkFMDIV(int iValue){
		try{
			mService.setLVDSDclkFMDIV(iValue);
		}catch(RemoteException ex){
		}
	}

	public boolean getLVDSNewMode(){
		try{
			return	mService.getLVDSNewMode();
		}catch(RemoteException ex){
		}
		return false;
	}

	public void setLVDSNewMode(boolean mode){
		try{
			mService.setLVDSNewMode(mode);
		}catch(RemoteException ex){
		}
	}

	public int getLVDSPLLOffset(){
		try{
			return	mService.getLVDSPLLOffset();
		}catch(RemoteException ex){
		}
		return 0;
	}
	
	public void setLVDSPLLOffset(int iValue){
		try{
			mService.setLVDSPLLOffset(iValue);
		}catch(RemoteException ex){
		}
	}

	public boolean getLVDSOnlyEvenOdd(){
		try{
			return	mService.getLVDSOnlyEvenOdd();
		}catch(RemoteException ex){
		}
		return false;
	}

	public void setLVDSOnlyEvenOdd(boolean mode){
		try{
			mService.setLVDSOnlyEvenOdd(mode);
		}catch(RemoteException ex){
		}
	}

	public boolean getLVDSEvenOrOdd(){
		try{
			return	mService.getLVDSEvenOrOdd();
		}catch(RemoteException ex){
		}
		return false;
	}

	public void setLVDSEvenOrOdd(boolean mode){
		try{
			mService.setLVDSEvenOrOdd(mode);
		}catch(RemoteException ex){
		}
	}

	public int getLVDSDrivingCurrent(){
		try{
			return	mService.getLVDSDrivingCurrent();
		}catch(RemoteException ex){
		}
		return 0;
	}
	
	public void setLVDSDrivingCurrent(int iValue){
		try{
			mService.setLVDSDrivingCurrent(iValue);
		}catch(RemoteException ex){
		}
	}

	public String getBARCODE1(){
		try{
			return	mService.getBARCODE1();
		}catch(RemoteException ex){
		}
		return null;
	}	

	public String getBARCODE2(){
		try{
			return	mService.getBARCODE2();
		}catch(RemoteException ex){
		}
		return null;
	}	

	public String getBARCODE3(){
		try{
			return	mService.getBARCODE3();
		}catch(RemoteException ex){
		}
		return null;
	}	

	public String getBARCODE4(){
		try{
			return	mService.getBARCODE4();
		}catch(RemoteException ex){
		}
		return null;
	}	

	public void setBARCODE1(char bar1, char bar2, char bar3, char bar4, char bar5, char bar6, char bar7, char bar8){
		try{
			mService.setBARCODE1(bar1, bar2, bar3, bar4, bar5, bar6, bar7, bar8);
		}catch(RemoteException ex){
		}
	}

	public void setBARCODE2(char bar1, char bar2, char bar3, char bar4, char bar5, char bar6, char bar7, char bar8){
		try{
			mService.setBARCODE2(bar1, bar2, bar3, bar4, bar5, bar6, bar7, bar8);
		}catch(RemoteException ex){
		}
	}

	public void setBARCODE3(char bar1, char bar2, char bar3, char bar4, char bar5, char bar6, char bar7, char bar8){
		try{
			mService.setBARCODE3(bar1, bar2, bar3, bar4, bar5, bar6, bar7, bar8);
		}catch(RemoteException ex){
		}
	}

	public void setBARCODE4(char bar1, char bar2, char bar3, char bar4, char bar5, char bar6, char bar7, char bar8){
		try{
			mService.setBARCODE4(bar1, bar2, bar3, bar4, bar5, bar6, bar7, bar8);
		}catch(RemoteException ex){
		}
	}

	public int getColorTempData(int index){
		try{
			return	mService.getColorTempData(index);
		}catch(RemoteException ex){
		}
		return 0;
	}
	
	public void setColorTempData(int index,int iValue){
		try{
			mService.setColorTempData(index,iValue);
		}catch(RemoteException ex){
		}
	}
	
	public boolean setScalerAutoColor(){
		boolean result = false;
		try{
			result = mService.setScalerAutoColor();
		}catch(RemoteException ex){
		}
		return result;
	}
	
	public void resetColorTemp(){
		try{
			mService.resetColorTemp();
		}catch(RemoteException ex){
		}
	}
	

	public void setWiredLanManualInit(){
		try{
			mService.setWiredLanManualInit();
		}catch(RemoteException ex){
		}
	}

	public void setWiredLanManualIp(){
		try{
			mService.setWiredLanManualIp();
		}catch(RemoteException ex){
		}
	}

	public void setWiredLanIpAddr(int netType, char ip1, char ip2, char ip3, char ip4){
		try{//ENUM_NET_IP_TYPE
			mService.setWiredLanIpAddr(netType, ip1, ip2, ip3, ip4);
		}catch(RemoteException ex){
		}
	}

	public void setMacAddress(char mac1, char mac2, char mac3, char mac4, char mac5, char mac6){
		try{//ENUM_NET_IP_TYPE
			mService.setMacAddress(mac1, mac2, mac3, mac4, mac5, mac6);
		}catch(RemoteException ex){
		}
	}


	//ImageDecoderApiExecutor
	public long startDecodeImage(boolean bBackupHttpFileSource){
		try{
			return mService.startDecodeImage(bBackupHttpFileSource);
		}catch(RemoteException ex){
		}
		return 0;
	}

	public void decodeImage(String pFilePath, int transitionType){
		//VIDEO_TRANSITION_TYPE
		try{
			mService.decodeImage(pFilePath, transitionType);
		}catch(RemoteException ex){
		}
	}

	public long getDecodeImageResult(){
		try{
			return	mService.getDecodeImageResult();
		}catch(RemoteException ex){
		}
		return 0;
	}
	
	public void stopDecodeImage(){
		try{
			mService.stopDecodeImage();
		}catch(RemoteException ex){
		}
	}

	public void zoomIn(){
		try{
			mService.zoomIn();
		}catch(RemoteException ex){
		}
	}

	public void zoomOut(){
		try{
			mService.zoomOut();
		}catch(RemoteException ex){
		}
	}

	public void leftRotate(){
		try{
			mService.leftRotate();
		}catch(RemoteException ex){
		}
	}

	public void rightRotate(){
		try{
			mService.rightRotate();
		}catch(RemoteException ex){
		}
	}

	public void upRotate(){
		try{
			mService.upRotate();
		}catch(RemoteException ex){
		}
	}

	public void downRotate(){
		try{
			mService.downRotate();
		}catch(RemoteException ex){
		}
	}


	public void enableQFHD(){
		try{
			mService.enableQFHD();	
		}catch(RemoteException ex){
		}
	}

	public void disableQFHD(){
		try{
			mService.disableQFHD();	
		}catch(RemoteException ex){
		}

	}

    	public void setSuperResolutionMode(boolean enable){
            try{
                mService.setSuperResolutionMode(enable); 
            }catch(RemoteException ex){
            }
	}

	public int getCurQuadFHDMode(){
		try{
			return  mService.getCurQuadFHDMode();
		}catch(RemoteException ex){
		}
		return 0;
	}
        
	public boolean setCurQuadFHDMode(int mode){
		//QUADFHD_SETTING_MODE_TYPE 
	    boolean result = false;

        try {
            result = mService.setCurQuadFHDMode(mode);
        } catch (RemoteException ex) {
        }
        return result;
	}	
	
	//NoSignal DisplayReady
	public boolean getNoSignalDisplayReady(){
		try{
			/**
			 * NoSignal = true
			 * DisplayReady = false
			 */
			return mService.getNoSignalDisplayReady();
		}catch( RemoteException ex ){
		}
		return true;
	}

	public void setEnableBroadcast(boolean enable){
		try{
			mService.setEnableBroadcast(enable);
		}catch(RemoteException ex){
		}
	}

	public void setVideoAreaOn(int x, int y, int w, int h){
		try{
			mService.setVideoAreaOn(x, y, w, h);
		}catch(RemoteException ex){
		}
	
	}

	public void setVideoAreaOff(){
		try{
			mService.setVideoAreaOff();
		}catch(RemoteException ex){
		}
	
	}

    public boolean gldcOsdShow(int mode) {
        boolean result = false;

        try {
            result = mService.gldcOsdShow(mode);
        } catch (RemoteException ex) {
        }

        return result;
    }
	
	public boolean isKeyDown(int key) {
        boolean result = false;

        try {
            result = mService.isKeyDown(key);
        } catch (RemoteException ex) {
        }

        return result;
    }

	public void setInitialFlag(boolean bInitial) {
		try {
			mService.setInitialFlag(bInitial);
		} catch (RemoteException ex) {
		}
	}

	public boolean getInitialFlag() {
		boolean result = false;
        
		try {
			result = mService.getInitialFlag();
		} catch (RemoteException ex) {
		}

		return result;
	}
	
    public boolean setRTKIRMouse(boolean setting) {
        boolean result = false;

        try {
            result = mService.setRTKIRMouse(setting);
        } catch (RemoteException ex) {
        }

        return result;
    }

	public String getChannelNameList(int iStartIdx, int iContentLen, boolean bFilter) {
		try{
			return	mService.getChannelNameList(iStartIdx, iContentLen, bFilter);
		}catch(RemoteException ex){
		}

		return null;			
	}

	public String getCurrentProgramInfo() {
		try{
			return	mService.getCurrentProgramInfo();
		}catch(RemoteException ex){
		}

		return null;			
	}	

	public String getCurrentProgramDescription() {
		try{
			return	mService.getCurrentProgramDescription();
		}catch(RemoteException ex){
		}

		return null;			
	}	
		
	public String getCurrentProgramRating() {
		try{
			return	mService.getCurrentProgramRating();
		}catch(RemoteException ex){
		}

		return null;			
	}
	
	public boolean hasCurrentProgramWithSubtitle() {
		boolean result = false;
        
		try {
			result = mService.hasCurrentProgramWithSubtitle();
		} catch (RemoteException ex) {
		}

		return result;
	}	

	public String getCurAtvSoundSelect(){
		try{
			return	mService.getCurAtvSoundSelect();
		}catch(RemoteException ex){
		}
		return null;
	}
			
	public String getCurrentAudioLang() {
		try{
			return	mService.getCurrentAudioLang();
		}catch(RemoteException ex){
		}

		return null;			
	}
	
	public String getCurInputInfo() {
		try{
			return	mService.getCurInputInfo();
		}catch(RemoteException ex){
		}

		return null;			
	}	
	
	public String getCurrentSetting_tv(String tvStr) {
		try{
			return	mService.getCurrentSetting_tv(tvStr);
		}catch(RemoteException ex){
		}

		return null;			
	}

   	public void setFunctionParser(int paramcounter, String param){
   		Log.w("TVMANAGER~~~~~~~~~~~~~~~~~~~~~~~", "setFunctionParser~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		try{
			mService.setFunctionParser(paramcounter, param);
		}catch(RemoteException ex){
		}
	}
   	
   	public String getFunctionParser(int paramcounter, String param){
   		Log.w("TVMANAGER~~~~~~~~~~~~~~~~~~~~~~~", "getFunctionParser~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		try{
			return mService.getFunctionParser(paramcounter, param);
		}catch(RemoteException ex){
		}
		return null;
	}

	public int getChannelFreqCount(){
		try{
			return	mService.getChannelFreqCount();
		}catch(RemoteException ex){
		}
		return 0;
	}			
	
	public int getChannelFreqByTableIndex(int index){
		try{
			return	mService.getChannelFreqByTableIndex(index);
		}catch(RemoteException ex){
		}
		return 0;
	}		
	
	public String getChannelchannelNumByTableIndex(int index) {
		try{
			return	mService.getChannelchannelNumByTableIndex(index);
		}catch(RemoteException ex){
		}

		return null;			
	}
	
	public int getChannelCountByFreq(int freq){
		try{
			return	mService.getChannelCountByFreq(freq);
		}catch(RemoteException ex){
		}
		return 0;
	}			
	
	public int getCurChannelIndex(){
		try{
			return	mService.getCurChannelIndex();
		}catch(RemoteException ex){
		}
		return 0;
	}		
	
	public int getChannelListChannelCount(){
		try{
			return	mService.getChannelListChannelCount();
		}catch(RemoteException ex){
		}
		return 0;
	}			

	public String getChannelDataList(int iStartIdx, int iContentLen) {
		try{
			return	mService.getChannelDataList(iStartIdx, iContentLen);
		}catch(RemoteException ex){
		}

		return null;			
	}	

	public boolean recoverVideoSize() {
		boolean result = false;
        
		try {
			result = mService.recoverVideoSize();
		} catch (RemoteException ex) {
		}

		return result;		
	}

	public String getVideoSize() {
		try{
			return	mService.getVideoSize();
		}catch(RemoteException ex){
		}

		return null;			
	}
	
	public void setVideoSize(int iX, int iY, int iWidth, int iHeight) {
		try {
			mService.setVideoSize(iX, iY, iWidth, iHeight);
		} catch (RemoteException ex) {
		}				
	}

	public String getCurDtvSoundSelectList() {
		try{
			return	mService.getCurDtvSoundSelectList();
		}catch(RemoteException ex){
		}

		return null;			
	}	
	
	public int getCurDtvSoundSelectCount(){
		try{
			return	mService.getCurDtvSoundSelectCount();
		}catch(RemoteException ex){
		}
		return 0;
	}	
		
	public String getCurAtvSoundSelectList() {
		try{
			return	mService.getCurAtvSoundSelectList();
		}catch(RemoteException ex){
		}

		return null;			
	}
		
	public int getCurAtvSoundSelectCount(){
		try{
			return	mService.getCurAtvSoundSelectCount();
		}catch(RemoteException ex){
		}
		return 0;
	}	
	
	public boolean setDisplayFreeze(boolean enable) {
		boolean result = false;
        
		try {
			result = mService.setDisplayFreeze(enable);
		} catch (RemoteException ex) {
		}

		return result;		
	}		

	public void setCaptionMode(int mode) {
		try {
			mService.setCaptionMode(mode);
		} catch (RemoteException ex) {
		}				
	}
	
	public void setAnalogCaption(int type) {
		try {
			mService.setAnalogCaption(type);
		} catch (RemoteException ex) {
		}				
	}
	
	public void setDigitalCaption(int type) {
		try {
			mService.setDigitalCaption(type);
		} catch (RemoteException ex) {
		}				
	}	

	public boolean getDCC() {
		boolean result = false;
        
		try {
			result = mService.getDCC();
		} catch (RemoteException ex) {
		}

		return result;		
	}	

	public void setChannelFav(int index, boolean enable) {
		try {
			mService.setChannelFav(index, enable);
		} catch (RemoteException ex) {
		}				
	}

	public void setChannelSkip(int index, boolean enable) {
		try {
			mService.setChannelSkip(index, enable);
		} catch (RemoteException ex) {
		}				
	}

	public void setChannelBlock(int index, boolean enable) {
		try {
			mService.setChannelBlock(index, enable);
		} catch (RemoteException ex) {
		}				
	}
	
	public void setChannelDel(int index, boolean enable) {
		try {
			mService.setChannelDel(index, enable);
		} catch (RemoteException ex) {
		}				
	}

	public boolean getChannelFav(int index) {
		boolean result = false;
        
		try {
			result = mService.getChannelFav(index);
		} catch (RemoteException ex) {
		}

		return result;		
	}		
	
	public boolean getChannelSkip(int index) {
		boolean result = false;
        
		try {
			result = mService.getChannelSkip(index);
		} catch (RemoteException ex) {
		}

		return result;		
	}	

	public boolean getChannelBlock(int index) {
		boolean result = false;
        
		try {
			result = mService.getChannelBlock(index);
		} catch (RemoteException ex) {
		}

		return result;		
	}			
		
	public boolean queryTvStatus(int type) {
		boolean result = false;
        
		try {
			result = mService.queryTvStatus(type);
		} catch (RemoteException ex) {
		}

		return result;		
	}			
		
	public void setHdmiAudioSource(int index) {
		try {
			mService.setHdmiAudioSource(index);
		} catch (RemoteException ex) {
		}
	}	
	
	public void setSourceLockEnable(boolean enable) {
		try {
			mService.setSourceLockEnable(enable);
		} catch (RemoteException ex) {
		}
	}	

	public boolean getSourceLockStatus(int source) {
		boolean result = false;
        
		try {
			result = mService.getSourceLockStatus(source);
		} catch (RemoteException ex) {
		}

		return result;		
	}

	public void setSourceLockStatus(int source, boolean lock) {
		try {
			mService.setSourceLockStatus(source, lock);
		} catch (RemoteException ex) {
		}
	}
	
	public boolean getSourceLockStatusByIndex(int srcIndex) {
		boolean result = false;
        
		try {
			result = mService.getSourceLockStatusByIndex(srcIndex);
		} catch (RemoteException ex) {
		}

		return result;		
	}

	public void setSourceLockStatusByIndex(int srcIndex, boolean lock) {
		try {
			mService.setSourceLockStatusByIndex(srcIndex, lock);
		} catch (RemoteException ex) {
		}
	}	

	public int startRecordTs(String filePath, boolean bWithPreview) {
		try {
			return mService.startRecordTs(filePath, bWithPreview);
		} catch (RemoteException ex) {
		}
		
		return 0;
	}
	
	public boolean stopRecordTs() {
		boolean result = false;
				
		try {
			result = mService.stopRecordTs();
		} catch (RemoteException ex) {
		}
		
		return result;
	}	
		
	/*
	 * Transcode Control - for TVAnywhere
	 */
	public void transcodeControlStart(){
		try{
			mService.transcodeControlStart();
		}
		catch(RemoteException ex){
		}
	}

	public void transcodeControlStop(){
		try{
			mService.transcodeControlStop();
		}
		catch(RemoteException ex){
		}
	}

	public void transcodeControlPause(){
		try{
			mService.transcodeControlPause();
		}
		catch(RemoteException ex){
		}
	}

	public void transcodeControlResume(){
		try{
			mService.transcodeControlResume();
		}
		catch(RemoteException ex){
		}
	}

	public void setSurfaceTexture(SurfaceTexture surfaceTexture){
		setJniSurfaceTexture(surfaceTexture);	
	}
	private native void setJniSurfaceTexture(SurfaceTexture surfaceTexture);
}

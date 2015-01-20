package com.rtk.tv.utils;


public class TvBroadcastDefs {
	public static final String ACTION_TV_NO_SIGNAL ="com.realtek.TVSignalAlarm.nosignal";
	public static final String ACTION_TV_DISPLAY_READY = "com.realtek.TVSignalAlarm.displayready";	
	public static final String ACTION_TV_SOURCE_AUTO_SWITCH = "com.realtek.TVSignalAlarm.sourceautoswitch";
	public static final String ACTION_TV_MEDIA_MESSAGE = "com.broadcast.tv.media.message";
	public static final String EXTRA_TV_MEDIA_MESSAGE = "PDU_BCT_TV_MEDIA_MESSAGE";
    public static final String CMD_BCT_TV_SIGNAL_STATUS = "com.broadcast.tv.signalstatus";
    public static final String CMD_BCT_TV_SOURCE_SWITCH_STATUS = "com.broadcast.tv.sourceswitch.status";
    public static final String CMD_BCT_TV_VGA_ADJUST_STATUS = "com.broadcast.tv.vgaadjust.status";
    public static final String CMD_BCT_TV_CHANNEL_SELECT_CHANGING = "com.broadcast.tv.channelselect.changing";
    public static final String CMD_BCT_TV_CHANNEL_SELECT_FINISHED = "com.broadcast.tv.channelselect.finished";
    public static final String CMD_BCT_TV_3D_STATUS = "com.broadcast.tv.3dstatus";
    public static final String CMD_BCT_TV_MUTE_CHANGE = "com.realtek.TVSignalAlarm.muteChange";
    public static final String CMD_BCT_TV_VOLUME_CHANGE = "com.realtek.TVSignalAlarm.volumeChange";
    public static final String CMD_BCT_TV_EXT1_SIGNAL_CHANGE = "com.broadcast.tv.ext1SigChange";
    public static final String PDU_BCT_TV_SIGNAL_STATUS = "PDU_BCT_TV_SIGNAL_STATUS";
    public static final String CC_TV_SIG_STATUS_NULL = "0"; // no signal
    public static final String CC_TV_SIG_STATUS_NOSIG = "1"; // no signal
    public static final String CC_TV_SIG_STATUS_UNSTABLE = "2"; // unstable
    public static final String CC_TV_SIG_STATUS_NOTSUP = "3"; // not support
    public static final String CC_TV_SIG_STATUS_STABLE = "4"; // stable
    public static final String CC_TV_SIG_STATUS_DISPLAYREADY = "5";
    public static final String PDU_BCT_TV_SIGNAL_FORMAT = "PDU_BCT_TV_SIGNAL_FORMAT";
    public static final String PDU_BCT_TV_SOURCE_SWITCH_STATUS = "PDU_BCT_TV_SOURCE_SWITCH_STATUS";
    public static final String CC_TV_SOURCE_SWITCH_DONE = "0";
    public static final String CC_TV_SOURCE_SWITCH_START = "1";
    public static final String CC_TV_SOURCE_SWITCH_SAME_SOURCE = "2";
    public static final String PDU_BCT_TV_VGA_ADJUST_STATUS = "PDU_BCT_TV_VGA_ADJUST_STATUS";
    public static final int    CC_TV_VGA_ADJUST_FAILED =  0;
    public static final int    CC_TV_VGA_ADJUST_SUCCESS = 1;
    public static final int    CC_TV_VGA_ADJUST_DOING =   2;	
    public static final String PDU_BCT_TV_3D_STATUS = "PDU_BCT_TV_3D_STATUS"; 
    public static final String CC_TV_STATUS3D_HDMI_DVI = "HDMI_DVI";
    public static final String PDU_BCT_DMT_ITV_VOICEINPUT = "inputstatus";
    public static final String CC_DMT_ITV_VOICEINPUT_CATCH = "catch";
    public static final String CC_DMT_ITV_VOICEINPUT_LOST = "lost";
    public static final String RTK_BCT_SEND_HOTKEYS = "com.broadcast.tv.SendHotKey";
    public static final String PDU_BCT_TV_HOT_KEY = "specialKey";

}

package com.rtk.tv;

import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.tv.TvContract;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.media.tv.TvView;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import com.rtk.tv.data.ChannelInfo;
import com.rtk.tv.data.ProgramInfo;
import com.rtk.tv.data.TimeshiftStatus;
import com.rtk.tv.service.ServiceManager;
import com.rtk.tv.utils.Constants;

public class TvManagerHelper {

	private static final String TAG = "com.rtk.tv-TvManagerHelper";
	private static final boolean VERBOSE = true;
	private static final boolean BENCHMARK = true;
	private static final boolean GINGA = true;
	
	public static final boolean DEBUG = false;
	
	//ATSC
	//public static final boolean isATSC = true;  //FIXME: need API
	//ATSC input
    public static final int ATSC_CABLE = 1;
    public static final int ATSC_AIR = 2;
    private static int atsc_input = ATSC_AIR;
    //ATSC cable system
    public static final int ATSC_AUTO = 0;
    public static final int ATSC_STD = 1;
    public static final int ATSC_IRC = 2;
    public static final int ATSC_HRC = 3;    
    private static int atsc_cable_system = ATSC_STD;
    //ATSC CC Mode
    public static final int CC_Mode_off = 0;
    public static final int CC_Mode_on = 1;
    public static final int CC_on_Mute = 2;
    //ATSC CC Basic Selection
    public static final int CC_basic_CC1 = 64;  //TODO: the number maybe changed
    public static final int CC_basic_CC2 = 65;
    public static final int CC_basic_CC3 = 66;
    public static final int CC_basic_CC4 = 67;
    public static final int CC_basic_Text1 = 68;
    public static final int CC_basic_Text2 = 69;
    public static final int CC_basic_Text3 = 70;
    public static final int CC_basic_Text4 = 71;
    //ATSC CC Advanced Selection
    public static final int CC_SERVICE_TOTAL = 64; //service1-63, maybe changed to 6 or 16...
    public static final int CC_ad_Service1 = 0;
    public static final int CC_ad_Service2 = 1;
    public static final int CC_ad_Service3 = 2;
    public static final int CC_ad_Service4 = 3;
    public static final int CC_ad_Service5 = 4;
    public static final int CC_ad_Service6 = 5;
    //ATSC CC Option mode  
    public static final int CC_option_Default = 0; //off
    public static final int CC_option_Custom = 1;  //on
    //ATSC CC Font Style
    public static final int CC_char_Font0 = 0;
    public static final int CC_char_Font1 = 1;
    public static final int CC_char_Font2 = 2;
    public static final int CC_char_Font3 = 3;
    public static final int CC_char_Font4 = 4;
    public static final int CC_char_Font5 = 5;
    public static final int CC_char_Font6 = 6;
    public static final int CC_char_Font7 = 7;
    public static final int CC_char_Font_Default = 0xFF;
    //ATSC CC Font Size
    public static final int CC_char_Size_Small = 0;
    public static final int CC_char_Size_Std = 1;
    public static final int CC_char_Size_Large = 2;
    public static final int CC_char_Size_Default = 0xFF;
    //ATSC CC Font Edge Style
    public static final int CC_charEdge_None = 0;
    public static final int CC_charEdge_Raised = 1;
    public static final int CC_charEdge_Depressed = 2;
    public static final int CC_charEdge_Uniform = 3;
    public static final int CC_charEdge_LeftDropShadow = 4;
    public static final int CC_charEdge_RightDropShadow = 5;
    public static final int CC_charEdge_Default = 0xFF;
    //ATSC CC color(font&bg)
    public static final int CC_color_Black = 0x00;
    public static final int CC_color_White = 0x3F;
    public static final int CC_color_Red = 0x30;
    public static final int CC_color_Green = 0x0C;
    public static final int CC_color_Blue = 0x03;
    public static final int CC_color_Yellow = 0x3C;
    public static final int CC_color_Manenta = 0x33;
    public static final int CC_color_Cyan = 0x0F;
    public static final int CC_color_Default = 0xFF;
    //ATSC Opacity(text&bg)
    public static final int CC_opacity_Solid = 0;
    public static final int CC_opacity_Flashing = 1;
    public static final int CC_opacity_Translucent = 2;
    public static final int CC_opacity_Transparent = 3;
    public static final int CC_opacity_low = 4;
    public static final int CC_opacity_high = 5;
    public static final int CC_opacity_Default = 0xFF;
    
	public static final int RESOLUTION_UNKNOWN = -1;
	public static final int RESOLUTION_HD = 0;
	public static final int RESOLUTION_SD = 1;
	
	// Aspect Ratio
	public final static int ASPECT_RATIO_UNKNOWN = 0;
	public final static int ASPECT_RATIO_PS_4_3 = 1;
	public final static int ASPECT_RATIO_LB_4_3 = 2;
	//public final static int ASPECT_RATIO_16_9 =  TvManager.Wide_16_9;
	//public final static int ASPECT_RATIO_16_10 = TvManager.Wide_16_10;
	public final static int SCALER_RATIO_AUTO = 5;
	public final static int SCALER_RATIO_4_3 = 6;
	public final static int SCALER_RATIO_16_9 = 7;
	public final static int SCALER_RATIO_14_9 = 8;
	public final static int SCALER_RATIO_LETTERBOX = 9;
	public final static int SCALER_RATIO_PANORAMA = 10;
	public final static int SCALER_RATIO_FIT = 11;
	public final static int SCALER_RATIO_POINTTOPOINT = 12;
	public final static int SCALER_RATIO_BBY_AUTO = 13;
	public final static int SCALER_RATIO_BBY_NORMAL = 14;
	public final static int SCALER_RATIO_BBY_ZOOM = 15;
	public final static int SCALER_RATIO_BBY_WIDE_1 = 16;
	public final static int SCALER_RATIO_BBY_WIDE_2 = 17;
	public final static int SCALER_RATIO_BBY_CINEMA = 18;
	public final static int SCALER_RATIO_CUSTOM = 19;
	public final static int SCALER_RATIO_PERSON = 20;
	public final static int SCALER_RATIO_CAPTION = 21;
	public final static int SCALER_RATIO_MOVIE = 22;
	public final static int SCALER_RATIO_ZOOM = 23;
	public final static int SCALER_RATIO_100 = 24;
	public final static int SCALER_RATIO_SOURCE = 25;
	public final static int SCALER_RATIO_ZOOM_14_9 = 26;
	public final static int SCALER_RATIO_NATIVE = 27;
	public final static int SCALER_RATIO_DISABLE = 28;
	public final static int ASPECT_RATIO_MAX = 29;
	
	public static final int PARENT_RATING_UNKNOWN = -1;
	
	public static final int GNERE_UNKNOWN = -1;
	
	// Sound Settings
	public static final int AUDIO_BALANCING_MAX = 50;
	public static final int AUDIO_BALANCING_MIN = -50;
	
	public static final int TV_MOUNTING_STAND = 0;
	public static final int TV_MOUNTING_WALL = 1;
	
	public static final int AUDIO_VOLUME_OFFSET_MAX = 50;
	public static final int AUDIO_VOLUME_OFFSET_MIN = -50;
	
	// Menu -> Preferences -> Teletext
	public static final int TELETEXT_AUTO = 0;
	public static final int TELETEXT_LIST = 1;
	
	// Menu -> PVR -> Time Shift Size
	public static final int VGA_HPOSITION_MIN = -50;
	public static final int VGA_HPOSITION_MAX = 50;

	public static final int VGA_VPOSITION_MIN = -50;
	public static final int VGA_VPOSITION_MAX = 50;

	public static final int VGA_CLOCK_MIN = -50;
	public static final int VGA_CLOCK_MAX = 50;

	public static final int VGA_PHASE_MIN = 0;
	public static final int VGA_PHASE_MAX = 100;


	
	// TODO: Audio Format
	public static final int AUDIO_FORMAT_HE_AAC = 0;
	public static final int AUDIO_FORMAT_DD = 1;
	public static final int AUDIO_FORMAT_DD_PLUS = 2;
	
	public static class AudioInformation{
		public int format;// DD, DD+, HE-ACC
		public int track;// Mono, Dual, Stereo
		public String language;
		public boolean hardOfHearing;
		public boolean ad;// Audio Description?
	}
	
	public static class VideoInformation{
		public int resolution = RESOLUTION_UNKNOWN;
		public int aspectRatio = ASPECT_RATIO_UNKNOWN;
		public String reso;
	}
	
	public static Date translateTvTime(long time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time * 1000);
		return c.getTime();
	}
	
	/////////////////////////////////////
	private static final String KEY_PREF_3DMODE = "3d_mode";
	private static final String KEY_PREF_TIMESHIFT_SIZE = "timeshift_size";
	private static TvManagerHelper sInstance;
	
	public static TvManagerHelper getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new TvManagerHelper(context);
		}
		return sInstance;
	}
	
	public final TvInputManager mTvManager;
	private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
	private final Handler mMainHandler = new Handler(Looper.getMainLooper());
	private final SharedPreferences mPreference;
	private final Context mContext;
	
	private TvManagerHelper(Context context) {
		mContext = context.getApplicationContext();
		mTvManager 	 = (TvInputManager) mContext.getSystemService(Context.TV_INPUT_SERVICE);
		mPreference = context.getSharedPreferences("tv_manager", Context.MODE_PRIVATE);
	}
	
	// System API
	public long currentTvTimeMillis() {
		long now = 100/*mTvManager.getDtvTimeMillis()*/;
		// Use system time if TV time is not available.
		if (now < 0) {
			now = System.currentTimeMillis();
		}
		return now;
	}
	
	public void setInputSource(final TvInputInfo src) {
		/*if (isRecording()) {
			Toast.makeText(mContext, R.string.msg_cannot_switch_source_during_recording, Toast.LENGTH_SHORT).show();
			return;
		} else if (mTvManager.isTimeshifting()) {
			Toast.makeText(mContext, R.string.msg_cannot_switch_source_during_timeshifting, Toast.LENGTH_SHORT).show();
			return;
		}
		// Note: This is supposed to be non-blocking call.
		mTvManager.setSource(src);*/
	}
	
	/**common API of Tv Input Framework start**/
	private ServiceManager mServiceManager;
	public void setServiceManager(ServiceManager sm){
		mServiceManager = sm;
	}
	private TvView tvInstance;
	public void setTvView(TvView tv){
		tvInstance = tv;
	}
	private SoftReference<List<Integer>> mCacheSourceList;
	public List<TvInputInfo> getInputSourceList() {
		return mTvManager.getTvInputList();
	}
	
	public TvInputInfo getInputSourceById(String inputId) {
		return mTvManager.getTvInputInfo(inputId);
	}
	
	public TvInputInfo getCurInputSource() {
		return mTvManager.getTvInputInfo(mServiceManager.getCurInputId());
	}
	
	public long[] getAllChannels(Context context) {
		String[] projection = { TvContract.Channels._ID };
		Cursor cursor = context.getContentResolver().query(
				TvContract.Channels.CONTENT_URI, projection, null, null, null);
		if (cursor == null)
			return null;
		long[] channels = new long[cursor.getCount()];
		int index = 0;
		while (cursor.moveToNext()) {
			channels[index] = cursor.getLong(0);
		}
		return channels;
	}
	
	//ChannelInfo[] channelList;
	public ChannelInfo[] getInputChannelsByInput(String inputId,Context context) {
		Cursor cursor = context.getContentResolver().query(
				TvContract.buildChannelsUriForInput(inputId), null, null,
				null, null);
		ChannelInfo[] channelList = ChannelInfo.buildChannels(cursor);
		if(channelList!=null){
			for(ChannelInfo info: channelList){
				info.setPrograms(getProgramsByChannel(info.getChannelId(),context));
			}
		}
		mServiceManager.setChannels(channelList);
		return channelList;
	}
	
	public ChannelInfo[] getChannels(){
		return mServiceManager.getChannels();
	}
	
	public ProgramInfo[] getProgramsByChannel(long channelId,Context context) {
		Cursor cursor = context.getContentResolver().query(
				TvContract.buildProgramsUriForChannel(channelId), null, null,
				null, null);
		return ProgramInfo.buildPrograms(cursor);
	}
	
	//private ChannelInfo curChannelInfo ;
	public ChannelInfo getCurrentChannelInfo() {
		return mServiceManager.getCurrentChannelInfo();
	}
	
	public void tune(String inputId,long channelId){
		mServiceManager.setCurInputId(inputId);
		tvInstance.tune(inputId, TvContract.buildChannelUri(channelId));
	}
	
	public void tune(String inputId){
		mServiceManager.setCurInputId(inputId);
		tvInstance.tune(inputId, TvContract.buildChannelUriForPassthroughInput(inputId));
	}
	
	//String curInputId;
	public String getCurInputId() {
		return mServiceManager.getCurInputId();
	}
	
	public void reset(){
		mServiceManager.reset();
	}
	
	public void retune(Context context){
		String curInputId = mServiceManager.getCurInputId();
		ChannelInfo  curChannelInfo = mServiceManager.getCurrentChannelInfo();
		if(curInputId == null)
		{
				List<TvInputInfo> infoList = getInputSourceList();
				TvInputInfo inputInfo;
				if(infoList!=null && infoList.size()>0){
					inputInfo = infoList.get(0); 
					curInputId = inputInfo.getId();
					mServiceManager.setCurInputId(curInputId);
				}else{
					Debug.e(TAG, "input is null");
					return;
				}
				if (!inputInfo.isPassthroughInput()) {
					ChannelInfo[] channels = getInputChannelsByInput(
							curInputId, context);
					if (channels != null && channels.length > 0) {
						curChannelInfo = channels[0];
						mServiceManager.setCurrentChannelInfo(curChannelInfo);
					} else {
						Debug.e(TAG, "channel is null");
						return;
					}
				}
		}else if(curChannelInfo == null){
			if (!getInputSourceById(curInputId).isPassthroughInput()) {
				ChannelInfo[] channels = getInputChannelsByInput(
						curInputId, context);
				if (channels != null && channels.length > 0) {
					curChannelInfo = channels[0];
					mServiceManager.setCurrentChannelInfo(curChannelInfo);
				} else {
					Debug.e(TAG, "channel is null");
					return;
				}
			}
		}
		if(getInputSourceById(curInputId).isPassthroughInput()){
			tune(curInputId);
		}else
			tune(curInputId,curChannelInfo.getChannelId());
	}
	
	public void switchToNextChannel() {
		String curInputId = mServiceManager.getCurInputId();
		ChannelInfo curChannelInfo = mServiceManager.getCurrentChannelInfo();
		ChannelInfo[] channelList = mServiceManager.getChannels();
		if(curInputId ==null || isRecording())
			return;
		int next = curChannelInfo.getIndex() + 1;
		if(next<channelList.length){
			curChannelInfo = channelList[next];
			tune(curInputId,curChannelInfo.getChannelId());
		}
	}

	public void switchToPrevChannel() {
		String curInputId = mServiceManager.getCurInputId();
		ChannelInfo curChannelInfo = mServiceManager.getCurrentChannelInfo();
		ChannelInfo[] channelList = mServiceManager.getChannels();
		if(curInputId ==null || isRecording())
			return;
		int last = curChannelInfo.getIndex() - 1;
		if(last>=0){
			curChannelInfo = channelList[last];
			tune(curInputId,curChannelInfo.getChannelId());
		}
	}
	
	public void setChannel(ChannelInfo channelInfo){
		String curInputId = mServiceManager.getCurInputId();
		ChannelInfo curChannelInfo = mServiceManager.getCurrentChannelInfo();
		if(curInputId ==null || isRecording())
			return;
		curChannelInfo = channelInfo;
		tune(curInputId,curChannelInfo.getChannelId());
	}
	
	public void setChannelByIndex(int index){
		String curInputId = mServiceManager.getCurInputId();
		ChannelInfo curChannelInfo = mServiceManager.getCurrentChannelInfo();
		ChannelInfo[] channelList = mServiceManager.getChannels();
		if(curInputId ==null || isRecording() || channelList == null || index >= channelList.length)
			return;
		curChannelInfo = channelList[index];
		tune(curInputId,curChannelInfo.getChannelId());
	}
	
	public void setChannel(long channelId){
		String curInputId = mServiceManager.getCurInputId();
		ChannelInfo[] channelList = mServiceManager.getChannels();
		if(curInputId ==null || isRecording())
			return;
		ChannelInfo tmp = null;
		for(ChannelInfo info : channelList ){
			if(info.getChannelId() == channelId){
				tmp = info;
				break;
			}
		}
		if(tmp==null){
			Log.d(TAG, "no channel found");
			return;
		}
		mServiceManager.setCurrentChannelInfo(tmp);
		tune(curInputId,channelId);
	}
	
	public boolean hasProgramInformation() {
		ChannelInfo curChannelInfo = mServiceManager.getCurrentChannelInfo();
		if(curChannelInfo == null)
			return false;
		ProgramInfo[] programs = curChannelInfo.getPrograms();
		if(programs!=null && programs.length>0){
			return true;
		}else
			return false;
	}
	
	public boolean isPvrSupported() {
		String curInputId = mServiceManager.getCurInputId();
		if(curInputId == null)
			return false;
		TvInputInfo input = getCurInputSource();
		if(input.getType() == TvInputInfo.TYPE_DVI)//suppose this is DTV
			return true;
		return false;
	}
	/***common API of Tv Input Framework end***/
	
	public void switchToNextSoruce() {
		String currentSource = getCurInputId();
		List<TvInputInfo> list = getInputSourceList();
		if (list.size() <= 0) {
			return;
		}
		
		for(int idx =0;idx<list.size();idx++){
			if(list.get(idx).getId().equals(currentSource)){
				idx++;
				if (idx >= list.size()) {
					idx = 0;
				}
				setInputSource(list.get(idx));
				break;
			}
		}
	}

	public void switchToPrevSource() {
		String currentSource = getCurInputId();
		List<TvInputInfo> list = getInputSourceList();
		if (list.size() <= 0) {
			return;
		}
		for(int idx =0;idx<list.size();idx++){
			if(list.get(idx).getId().equals(currentSource)){
				idx--;
				if (idx < 0) {
					idx = list.size() - 1;
				}
				setInputSource(list.get(idx));
				break;
			}
		}
	}

	public static boolean hasChannel(int source) {
		switch(source) {
		/*case TvManager.SOURCE_ATV1:
		case TvManager.SOURCE_ATV2:
		case TvManager.SOURCE_DTV1:
		case TvManager.SOURCE_DTV2:
		case TvManager.SOURCE_IDTV1:
			return true;*/
		default:
			return false;
		}
	}
	
	public static boolean showInputSourceLabel(String inputId) {
		/*switch(source) {
		case TvManager.SOURCE_ATV1:
		case TvManager.SOURCE_ATV2:
		case TvManager.SOURCE_DTV1:
		case TvManager.SOURCE_DTV2:
		case TvManager.SOURCE_IDTV1:
			return false;
		default:
			return true;
		}*/
		return true;
	}
	
	// ===================== Picture Settings =============================
	public void set3dColorManagement(boolean enable) {
		//mTvManager.set3dColourManagement(enable);
	}
	
	public boolean is3dColorManagementEnabled() {
		//return mTvManager.get3dColourManagement();
		return false;
	}

	public void setBacklightControl(boolean active) {
		//mTvManager.setBacklight(active ? 1 : 0);
	}
	
	public boolean isBacklightActive() {
		//return mTvManager.getBacklight() == 1;
		return false;
	}

	// ===================== Sound Settings ====================================
	public void setAudioBalancing(int value) {
		//mTvManager.setBalance(value);
	}

	public int getAudioBalancing() {
		//return mTvManager.getBalance();
		return 0;
	}
	
	public void setAudioDistortionControl(boolean b) {
		//mTvManager.setAudioDistortionControl(b);
	}

	public boolean isAudioDistortionControlEnabled() {
		//return mTvManager.getAudioDistortionControl();
		return false;
	}
	
	public void setTvMounting(int value) {
		//mTvManager.SetTvMountSetting(value);
	}
	
	public int getTvMounting() {
		//return mTvManager.GetTvMountSetting();
		return 0;
	}

	public boolean isDynamicRangeControlEnabled() {
		//return mTvManager.getDynamicRangeControl();
		return false;
	}

	public void setDynamicRangeControlEnabled(boolean enable) {
		//mTvManager.setDynamicRangeControl(enable);
	}

	public int getAudioLevelOffset() {
		//return mTvManager.getAudioVolumeOffset();
		return 0;
	}
	
	public void setAudioLevelOffset(int offset) {
		//mTvManager.setAudioVolumeOffset(offset);
	}
	
	// Preferences
	public int getTeletextMode() {
		//return mTvManager.getTeleTextMode();
		return 0;
	}

	public void setTeletextMode(int value) {
		//mTvManager.setTeleTextMode(value);
	}

	public void setTeletextLanguage(int lang) {
		//mTvManager.setTeleTextLanguage(lang);
	}

	public int getTeletextLanguage() {
		//return mTvManager.getTeleTextLanguage();
		return 0;
	}

	public boolean isBlueScreenEnabled() {
		return false;
	}

	public void setBlueScreenEnable(boolean b) {
		
	}



	public int getVgaHPosition()
	{
		//return (mTvManager.getVgaHPosition() - 50);
		return 0;
	}

	public void setVgaHPosition(char value)
	{
		//mTvManager.setVgaHPosition(value);
	}

	public int getVgaVPosition()
	{
		//return (mTvManager.getVgaVPosition() - 50);
		return 0;
	}

	public void setVgaVPosition(char value)
	{
		//mTvManager.setVgaVPosition(value);
	}

	public void setVgaAutoAdjust()
	{
		//mTvManager.setVgaAutoAdjust();
	}

	public int getVgaClock()
	{
		//return (mTvManager.getVgaClock() - 50);
		return 0;
	}

	public void setVgaClock(char value)
	{
		//mTvManager.setVgaClock(value);
	}

	public int getVgaPhase()
	{
		//return mTvManager.getVgaPhase();
		return 0;
	}

	public void setVgaPhase(char value)
	{
		//mTvManager.setVgaPhase(value);
	}

	public long getCurSourceType()
	{
		//return mTvManager.getCurSourceType();
		return 0;
	}

	// Setup
	public void setCountry(int value) {
		//mTvManager.setCountryCode(value);
	}

	public int getTvLocation() {
		//return mTvManager.getTVLocation();
		return 0;
	}

	public void setTvLocation(int location) {
		//mTvManager.setTVLocation(location);
	}

	public void setPvrTimeShiftSize(int value) {
		mPreference.edit().putInt(KEY_PREF_TIMESHIFT_SIZE, value).commit();
	}
	
	/**
	 * 
	 * @return size in MB.
	 */
	public int getPvrTimeShiftSize() {
		return mPreference.getInt(KEY_PREF_TIMESHIFT_SIZE, Constants.PVR_TIME_SHIFT_SIZE_512M);
	}
	
	public boolean setPvrTimeShiftEnable(boolean enable) {
		return setPvrTimeShiftEnable(enable, true);
	}
	
	public boolean setPvrTimeShiftEnable(boolean enable, boolean play) {
		/*boolean cur = mTvManager.isTimeshifting();
		boolean result = false; 
		if (enable && !cur) {
			File f = getPvrStorage();
			if (f != null) {
				result = mTvManager.startTimeshift(f.getAbsolutePath(), getPvrTimeShiftSize());
				if (result && play) {
					mTvManager.playTimeshift();
				}
			}
		} else if (!enable && cur) {
			result = mTvManager.stopTimeshift();
		} else {
			result = true;
		}
		return result;*/
		return false;
	}
	
	public boolean isPvrTimeShiftEnabled() {
		//return mTvManager.isTimeshifting();
		return false;
	}

	// Subtitle
	public List<String> getCurrentSubtitles() {
		List<String> list = new ArrayList<String>();
		/*int count = mTvManager.getDtvSubtitleIndexListCount();
		if (count > 0) {
			String str = mTvManager.getDtvSubtitleIndexList();
			String[] subs = str.split("\n");
			for (int i = 0; i < subs.length; i++) {
				list.add(subs[i]);
			}
		}*/
		return list;
	}
	
	public boolean isSubtitleEnabled() {
		//return mTvManager.getSubtitleEnable();
		return false;
	}
	
	public void setSubtitleEnable(boolean enable) {
		//mTvManager.setSubtitleEnable(enable);
	}
	
	public int getCurrentSubtitleIndex() {
		//return mTvManager.getCurDtvSubtitleIndex();
		return 0;
	}
	
	public int setCurrentSubtitle(int index) {
		/*if (mTvManager.setDtvSubtitleByIndex(index)) {
			return index;
		}
		return getCurrentSubtitleIndex();*/
		return 0;
	}

	// -------- ATV Manual Tuning --------
	
	// Atv Sound Std
	public int getCurrentAtvSoundStd() {
		//return mTvManager.getCurAtvSoundStd();
		return 0;
	}
	
	public void setCurrentAtvSoundStd(int atvSoundStd) {
		//mTvManager.setCurAtvSoundStd(atvSoundStd);
	}
	
	// Atv Color Std
	Future<?> mSetAtvColorTask;
	public void setCurrentAtvColorStd(final int colorStd) {
		// Do it asynchronously.
		if (mSetAtvColorTask != null) {
			mSetAtvColorTask.cancel(false);
		}
		Runnable r = new Runnable() {

			@Override
			public void run() {
				// This takes about 1.5 seconds.
				//mTvManager.setCurAtvColorStd(colorStd);
			}

		};
		mSetAtvColorTask = mExecutor.submit(r);
	}
	
	public int getCurrentAtvColorStd() {
		//return mTvManager.getCurAtvColorStd();
		return 0;
	}
	
	// Fine Tuning
	Future<?> mSetCurrentFrequencyOffset;
	public void setCurrentAtvFrequencyOffset(final int offset, final boolean perminant) {
		if (mSetCurrentFrequencyOffset != null) {
			mSetCurrentFrequencyOffset.cancel(false);
		}
		Runnable r = new Runnable() {

			@Override
			public void run() {
				//mTvManager.fineTuneCurFrequency(offset, perminant);
			}

		};
		mSetCurrentFrequencyOffset = mExecutor.submit(r);
	}

	public int getCurrentAtvFrequencyOffset() {
		//return mTvManager.getTvCurChFreqOffset();
		return 0;
	}
	
	// Skipped
	
	public void setCurrentAtvChannelSkipped(boolean skipped) {
		//mTvManager.setCurChannelSkipped(skipped);
	}
	

	// Channel Booster
	public void setAtvChannelBooster(int channelIndex, boolean enabled) {
		//mTvManager.setChannelbooster(channelIndex, enabled);
	}

	public boolean isAtvChannelBoosterEnabled(int channelIndex) {
		//return mTvManager.getChannelbooster(channelIndex) == 1;
		return false;
	}

	// ATV Frequency Table
	public static final int ATV_TYPE_CABLE = 0;
	public static final int ATV_TYPE_SATELLITE = 1;
	
	public int getAtvFrequencyTableSize() {
		//return mTvManager.getChannelFreqCount();
		return 0;
	}
	
	public int getAtvFrequencyAt(int tableIndex) {
		//return mTvManager.getChannelFreqByTableIndex(tableIndex);
		return 0;
	}

	private int getAtvBandwidthAt(int tableIndex) {
		//return mTvManager.getChannelBandwidth(tableIndex);
		return 0;
	}
	
	public int getAtvScanEntryCount() {
		/*if (isAtvTableScanEnabled()) {
			return getAtvFrequencyTableSize();
		}
		return getChannelCount();*/
		return 0;
	}
	
	public boolean isAtvTableScanEnabled() {
		//return mTvManager.isAtvTableScan();
		return false;
	}
	
	public void setAtvTableScanEnabled(boolean b) {
		//mTvManager.setAtvTableScan(b);
	}
	
	// ATV Scanning
	public void startAtvTableScanning(int tableIdx) {
		int frequenct = getAtvFrequencyAt(tableIdx);
		int bandwidth = getAtvBandwidthAt(tableIdx);
		//mTvManager.tvSeekScanStop();
		//mTvManager.tvScanManualStart(frequenct, bandwidth, 0);
	}

	public void startAtvSeekScanning(boolean seekForward) {
		//mTvManager.tvSeekScanStop();
		//mTvManager.tvSeekScanStart(seekForward);
	}
	
	public void stopAtvScanning() {
		//mTvManager.tvSeekScanStop();
	}

	// DTV Frequency table
	public int getDtvFrequencyTableSize() {
		//return mTvManager.getChannelFreqCount();
		return 0;
	}

	public int getDtvFrequency(int index) {
		//return mTvManager.getChannelFreqByTableIndex(index);
		return 0;
	}
	

	public int getDtvBandwidth(int index) {
		//return mTvManager.getChannelBandwidth(index);
		return 0;
	}
	
	public void stopDtvScanning() {
		//mTvManager.tvScanManualStop();
	}

	public void startDtvScanning(int frequency, int bandwidth, int index, int source) {
		//mTvManager.tvScanManualStart(frequency, bandwidth, index);
	}

	
	// Picture Setting
	public void setPictureMode(int value) {
		//mTvManager.setPictureMode(value);
	}

	public void refresh3dModeAspectRatio() {
		mExecutor.execute(mRefresh3dAspectRatio);
	}
	
	private final Runnable mRefresh3dAspectRatio = new Runnable() {
		
		@Override
		public void run() {
			/*int aspect = mTvManager.getAspectRatio(getInputSource());
			int mode = mTvManager.get3dMode();
			int lastMode = mPreference.getInt(KEY_PREF_3DMODE, mode);
			
			// Restore 3D mode if it's been changed by others.
			if (lastMode != mode) {
				mTvManager.set3dModeAndChangeRatio(lastMode, false, aspect);
			} else {
				mTvManager.setAspectRatio(aspect);
			}*/
		}
	};

	public boolean isSignalDisplayReady() {
		//return !mTvManager.getNoSignalDisplayReady();
		return false;
	}

	// Ginga
	public boolean isGingaExisted() {
		if (GINGA) {
			//return mTvManager.getIsGingaExist();
			return false;
		}
		return false;
	}

	public boolean isForwardKeyToGinga() {
		if (GINGA) {
			//return mTvManager.getIsForwadKeyToGinga();
			return false;
		}
		return false;
	}

	public void forwardKeyToGinga(int keyCode) {
		if (GINGA) {
			//mTvManager.AndroidDfbKeyPress(keyCode);
		}
	}

	public void closeGinga() {
		if (GINGA) {
			//mTvManager.closeGingaApp();
		}
	}
    //mhep5
    public int mheg5KeyPress(int keyCode) {
		//return mTvManager.Mheg5_keyPress(keyCode);
    	return 0;
	}

	public boolean mheg5IsCIPLUS() {
		/*if(mTvManager.Mheg5_isCIPLUS()==1){
               return true;
		}*/
		return false;
	}
	public boolean mheg5IsStarted() {
		/*if(mTvManager.Mheg5_isStarted()>0){
               return true;
		}*/
		return false;
	}

	public boolean mheg5IsQuietlyTune() {
		/*if(mTvManager.Mheg5_isQuietlyTune()>0){
               return true;
		}*/
		return false;
	}
	public void setChannelListFragIndex(int inputIndex) {
		//mTvManager.SetCurOPModeIndex(inputIndex);
	}
	public int getChannelListFragIndex() {
		//return mTvManager.GetCurOPModeIndex();
		return 0;
	}
	// 3D Settings
	public static final int MODE_3D_AUTO = -1;
	public static final int MODE_3D_DISABLED = 0;
	public static final int MODE_3D_LEFT_RIGHT = 1;
	public static final int MODE_3D_UP_DOWN = 2;
	public static final int MODE_3D_2D_TO_3D = 3;
	
	public static final int MAX_3D_DEPTH = 100;
	
	public boolean isSupport3D() {
		//return mTvManager.isSupport3D();
		return false;
	}
	
	public void set3DMode(int mode) {
		assert mode != MODE_3D_AUTO;
		switch(mode) {
			/*case MODE_3D_DISABLED:
				mTvManager.set3dMode(TvManager.SLR_3DMODE_2D);
				break;
			case MODE_3D_LEFT_RIGHT:
				mTvManager.set3dMode(TvManager.SLR_3DMODE_3D_SBS);
				break;
			case MODE_3D_UP_DOWN:
				mTvManager.set3dMode(TvManager.SLR_3DMODE_3D_TB);
				break;
			case MODE_3D_2D_TO_3D:
				mTvManager.set3dMode(TvManager.SLR_3DMODE_2D_CVT_3D);
				break;*/
			default:
				break;
		}
		
		//mPreference.edit().putInt(KEY_PREF_3DMODE, mTvManager.get3dMode()).commit();
	}
	
	public int get3DMode() {
		/*int mode = mTvManager.get3dMode();
		switch(mode) {
			case TvManager.SLR_3DMODE_2D:
				return MODE_3D_DISABLED;
			case TvManager.SLR_3DMODE_2D_CVT_3D:
				return MODE_3D_2D_TO_3D;
			case TvManager.SLR_3DMODE_3D_SBS:
			case TvManager.SLR_3DMODE_3D_SBS_CVT_2D:
				return MODE_3D_LEFT_RIGHT;
			case TvManager.SLR_3DMODE_3D_TB:
			case TvManager.SLR_3DMODE_3D_TB_CVT_2D:
				return MODE_3D_UP_DOWN;
			default:
			case TvManager.SLR_3DMODE_3D_AUTO:
			case TvManager.SLR_3DMODE_3D_AUTO_CVT_2D:
				return MODE_3D_AUTO;
		}*/
		return 0;
	}
	
	public static boolean is3DModeOptionEnabled(int mode) {
	    return mode != MODE_3D_AUTO;
    }
	
	public boolean is3Dto2DEnabled() {
	    //return mTvManager.get3dCvrt2D();
		return false;
    }
	
	public void set3Dto2DEnabled(boolean enabled) {
		//mTvManager.set3dCvrt2D(enabled, enabled ? 1 : -1);	
		//mPreference.edit().putInt(KEY_PREF_3DMODE, mTvManager.get3dMode()).commit();
	}

	public static boolean is3Dto2DOptionEnabled(int mode) {
		return mode == MODE_3D_AUTO || mode == MODE_3D_LEFT_RIGHT || mode == MODE_3D_UP_DOWN;
    }
	
	public boolean is3DLeftRightSwapped() {
	    //return mTvManager.get3dLRSwap();
		return false;
    }
	
	public void set3DLeftRightSwapped(boolean enabled) {
		//mTvManager.set3dLRSwap(enabled);		
		//mPreference.edit().putInt(KEY_PREF_3DMODE, mTvManager.get3dMode()).commit();
	}
	
	public static boolean is3DLeftRightSwappedOptionEnabled(int mode) {
		return mode == MODE_3D_AUTO || mode == MODE_3D_LEFT_RIGHT || mode == MODE_3D_UP_DOWN;
    }
	
	public int get3DDepth() {
		//return mTvManager.get3dDeep();
		return 0;
	}
	
	public void set3DDepth(int deep) {
		//mTvManager.set3dDeep(deep);
	}
	
	public static boolean is3DDepthOptionEnabled(int mode) {
		return mode == MODE_3D_LEFT_RIGHT || mode == MODE_3D_UP_DOWN;
	}

	public boolean is4K2KMode() {
		//return mTvManager.is4K2KMode();
		return false;
	}

	// PVR Control
	public int getTimeshiftState() {
		/*int stat = TvManager.STAT_TIMESHIFT_DISABLED;
		if (isPvrTimeShiftEnabled()) {
			stat = mTvManager.getTimeshiftState();
		}
		return stat;*/
		return 0;
	}
	
	public boolean getTimeshiftStatus(TimeshiftStatus status) {
		//return mTvManager.getTimeshiftStatus(status);
		return false;
	}

	public boolean resumeTimeshift() {
		//return mTvManager.playTimeshift();
		return false;
	}

	public boolean pauseTimeshift() {
		//return mTvManager.pauseTimeshift();
		return false;
	}

	public void forwardTimeshift() {
		//mTvManager.fastForwardTimeshift();
	}

	public void rewindTimeshift() {
		//mTvManager.fastRewindTimeshift();
	}

	public boolean startRecord() {
		boolean result = false;
		/*File f = getPvrStorage();
		// Disable timeshift first
		if (isPvrTimeShiftEnabled()) {
			setPvrTimeShiftEnable(false);
		}
		if (f != null) {
			result = mTvManager.startRecord(f.getAbsolutePath());
		}*/
		return result;
	}

	public boolean isRecording() {
		//return mTvManager.isRecording();
		return false;
	}

	public boolean stopRecord() {
		//boolean result = mTvManager.stopRecord();
		//return result;
		return false;
	}

	public File getPvrStorage() {
		// Saved preferences
		/*File pref = null;
		String path = mPreference.getString("pvr_storage", null);
		if (path != null) {
			pref = new File(path);
		}
		
		// Available disk
		StorageManager sm = StorageManager.from(mContext);
		StorageVolume[] volumes = sm.getVolumeList();
		
		// 1. Filter out unusable storage volumes.
		// 2. Check if prefered storage existed.
		// 3. Get first available volume
		StorageVolume first = null;
		for (int i = 0; i < volumes.length; i++) {
			if (!isUsablePvrStorageVolume(volumes[i])) {
				volumes[i] = null;
			} else if (volumes[i].getPath().equals(path)) {
				return pref;
			} else if (first == null) {
				first = volumes[i];
			}
		}
		
		// Use first available storage
		if (first != null) {
			setPvrStorage(first.getPath());
			return first.getPathFile();
		}*/

		return null;
	}
	
	/*public static final boolean isUsablePvrStorageVolume(StorageVolume v) {
		return Environment.MEDIA_MOUNTED.equals(v.getState()) && !v.isEmulated() && !v.isPrimary();
	}*/

	public void setPvrStorage(String path) {
		mPreference.edit().putString("pvr_storage", path).commit();
	}

	/**
	 * Blocking call.
	 * @param folderName
	 * @return
	 */
	public boolean startFormat(String folderName) {
		//return mTvManager.startFormat(folderName);
		return false;
	}

	public void syncDtvTime() {
		// TODO: Move this to TvManagerService
		// Use DTV stream to update time.
		// See NetworkTimeUpdateService.java
		Settings.Global.putInt(mContext.getContentResolver(), Settings.Global.AUTO_TIME, 2);
		
		long dtv = 100/*mTvManager.getDtvTimeMillis()*/;
		long now = System.currentTimeMillis();
		AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		if (dtv != now) {
			am.setTime(dtv);
			String s1 = new Date(now).toString();
			String s2 = new Date(dtv).toString();
			Log.i(TAG, "System time updates from " + s1 + " to " + s2);
		}
	}

	public boolean isDtvTimeSyhcnronized() {
		long dtv = 100/*mTvManager.getDtvTimeMillis()*/;
		long now = System.currentTimeMillis();
		return Math.abs(dtv - now) <= 5000;//MAX_ERROR = 5 second;
	}

	public int getAspectRatio(int iSourceOption){
		//return mTvManager.getAspectRatio(iSourceOption);
		return 0;
	}

	public void setAspectRatio(int ratio){
		//mTvManager.setAspectRatio(ratio);
	}

	public String playbackGetAudioType() {
		//return mTvManager.playbackGetAudioType();
		return "";
	}

	public String playbackGetVideoType() {
		//return mTvManager.playbackGetVideoType();
		return "";
	}

	public void setTvChannelLockStatus(int ch_index, boolean lock) {
		//mTvManager.setTvChannelLockStatus(ch_index, lock);
	}

	public boolean getTvChannelLockStatus(int ch_index) {
		//return mTvManager.getTvChannelLockStatus(ch_index);
		return false;
	}

	public void setSourceLockEnable(boolean enable) {
		//mTvManager.setSourceLockEnable(enable);
	}

	public void setSourceLockStatus(int source, boolean lock) {
		//mTvManager.setSourceLockStatus(source, lock);
	}

	public void setChannelLockEnable(boolean enable) {
		//mTvManager.setChannelLockEnable(enable);
	}

	public long getDtvCurFrequency() {
		//return mTvManager.getDtvCurFrequency();
		return 0;
	}

	public boolean manualDetectStart(int iFreq, int iBandWidth, int iPhyChNum,
			int tvtype, int tvsubtype) {
		//return mTvManager.manualDetectStart(iFreq, iBandWidth, iPhyChNum,
				//tvtype, tvsubtype);
		return false;
	}

	public boolean isManualDetectRunning() {
		//return mTvManager.isManualDetectRunning();
		return false;
	}

	public boolean manualDetectUpdate(int iFreq, int iBandWidth) {
		//return mTvManager.manualDetectUpdate(iFreq, iBandWidth);
		return false;
	}

	public boolean manualDetectStop() {
		//return mTvManager.manualDetectStop();
		return false;
	}

//for ATSC begin		
	public void setAtscInput(int inputId)
	{
	    //mTvManager.setTvInput(inputId);
	}
	public int getAtscInput()
	{
	    String tmp ="" /*mTvManager.getCurrentSetting_tv("TV_INPUT")*/;
	    if(tmp.equals("CABLE"))
	        atsc_input = ATSC_CABLE;
	    else
	        atsc_input = ATSC_AIR;
	    return atsc_input;
	}
	public void setAtscCableSystem(int cableTypeId)
	{
	    //mTvManager.setTvCableType(cableTypeId);	    
	}
	public int getAtscCableSystem()  //not use temporarily
	{
	    //return mTvManager.getTvCableType();
		return 0;
	}
	
	public void setAtscCCMode(int ccModeId)
	{
	    //mTvManager.setCaptionMode(ccModeId);
	}
	public int getAtscCCMode()
	{
	    //return mTvManager.getCaptionMode();
		return 0;
	}
	public void setAtscCCBasicSelection(int ccBasicSelectionId)
    {
	    //mTvManager.setAnalogCaption(ccBasicSelectionId);
    }
    public int getAtscCCBasicSelection()
    {
        //return mTvManager.getAnalogCaption();
    	return 0;
    }	
    public void setAtscCCAdvancedSelection(int ccAdvancedSelectionId)
    {
        //mTvManager.setDigitalCaption(ccAdvancedSelectionId);
    }
    public int getAtscCCAdvancedSelection()
    {
        //return mTvManager.CC_getDigitalCaption();
    	return 0;
    }
    public String getAtscCCServiceList()
    {
        //return mTvManager.CC_getSetting("serviceList");
    	return "";
    }
    public void enter_captionPreview()
    {
        //mTvManager.enter_captionPreview();
    }    
    public void exit_captionPreview()
    {
        //mTvManager.exit_captionPreview();
    }
    public void setAtscCCOptionMode(int ccOptionModeId)
    {
        //mTvManager.CC_setAttributeMode(ccOptionModeId);
    }
    public int getAtscCCOptionMode()
    {
        int ret = CC_option_Default;
        String tmp = ""/*mTvManager.CC_getSetting("option_mode")*/;
        if(tmp.equals("DEFAULT"))
            ret = CC_option_Default;
        else if(tmp.equals("CUSTOM"))
            ret = CC_option_Custom;
        return ret;
    }
    public void setAtscCCOptionFont(int ccOptionFontId)
    {
        //mTvManager.CC_setFont(ccOptionFontId);
    }
    public int getAtscCCOptionFont()
    {
        int ret = CC_char_Font_Default;
        String tmp = ""/*mTvManager.CC_getSetting("font")*/;
        if(tmp.equals("DEFAULT"))
            ret = CC_char_Font_Default;
        else if(tmp.equals("FONT0"))
            ret = CC_char_Font0;
        else if(tmp.equals("FONT1"))
            ret = CC_char_Font1;
        else if(tmp.equals("FONT2"))
            ret = CC_char_Font2;
        else if(tmp.equals("FONT3"))
            ret = CC_char_Font3;
        else if(tmp.equals("FONT4"))
            ret = CC_char_Font4;
        else if(tmp.equals("FONT5"))
            ret = CC_char_Font5;
        else if(tmp.equals("FONT6"))
            ret = CC_char_Font6;
        else if(tmp.equals("FONT7"))
            ret = CC_char_Font7;
        else if(tmp.equals("FONT8"))
            ret = CC_char_Font0;
        return ret;        
    }
    public void setAtscCCOptionFontSize(int ccOptionFontSizeId)
    {
        //mTvManager.CC_setFont(ccOptionFontSizeId);
    }
    public int getAtscCCOptionFontSize()
    {
        int ret = CC_char_Size_Default;
        String tmp = ""/*mTvManager.CC_getSetting("size")*/;
        if(tmp.equals("SMALL"))
            ret = CC_char_Size_Small;
        else if(tmp.equals("STANDARD"))
            ret = CC_char_Size_Std;
        else if(tmp.equals("LARGE"))
            ret = CC_char_Size_Large;
        else if(tmp.equals( "DEFAULT"))
            ret = CC_char_Size_Default;
        return ret;        
    }
    public void setAtscCCOptionEdgeStyle(int ccOptionEdgeStyleId)
    {
        //mTvManager.CC_setEdgeStyle(ccOptionEdgeStyleId);
    }
    public int getAtscCCOptionEdgeStyle()
    {
        int ret = CC_charEdge_None;
        String tmp = ""/*mTvManager.CC_getSetting("edgeStyle")*/;
        if(tmp.equals("NONE"))
            ret = CC_charEdge_None;
        else if(tmp.equals("RAISED"))
            ret = CC_charEdge_Raised;
        else if(tmp.equals("DEPRESSED"))
            ret = CC_charEdge_Depressed;
        else if(tmp.equals("UNIFORM"))
            ret = CC_charEdge_Uniform;
        else if(tmp.equals("LEFT_SHADOW"))
            ret = CC_charEdge_LeftDropShadow;
        else if(tmp.equals("RIGHT_SHADOW"))
            ret = CC_charEdge_RightDropShadow;
        else if(tmp.equals("DEFAULT"))
            ret = CC_charEdge_Default;
        return ret;
    } 
    public void setAtscCCOptionEdgeColor(int ccOptionEdgeColorId)
    {
        //mTvManager.CC_setEdgeColor(ccOptionEdgeColorId);
    }
    public int getAtscCCOptionEdgeColor()
    {
        int ret = CC_color_Default;
        String tmp = ""/*mTvManager.CC_getSetting("edgeColor")*/;
        if(tmp.equals("BLACK"))
            ret = CC_color_Black;
        else if(tmp.equals("WHITE"))
            ret = CC_color_White;
        else if(tmp.equals("RED"))
            ret = CC_color_Red;
        else if(tmp.equals("GREEN"))
            ret = CC_color_Green;
        else if(tmp.equals("BLUE"))
            ret = CC_color_Blue;
        else if(tmp.equals("YELLOW"))
            ret = CC_color_Yellow;
        else if(tmp.equals("MAGENTA"))
            ret = CC_color_Manenta;
        else if(tmp.equals("CYAN"))
            ret = CC_color_Cyan;
        else
            ret = CC_color_Default;
        return ret;
    } 
    public void setAtscCCOptionTextColor(int ccOptionTextColorId)
    {
        //mTvManager.CC_setFgColor(ccOptionTextColorId);
    }
    public int getAtscCCOptionTextColor()
    {
        int ret = CC_color_Default;
        String tmp = ""/*mTvManager.CC_getSetting("FgColor")*/;
        if(tmp.equals("BLACK"))
            ret = CC_color_Black;
        else if(tmp.equals("WHITE"))
            ret = CC_color_White;
        else if(tmp.equals("RED"))
            ret = CC_color_Red;
        else if(tmp.equals("GREEN"))
            ret = CC_color_Green;
        else if(tmp.equals("BLUE"))
            ret = CC_color_Blue;
        else if(tmp.equals("YELLOW"))
            ret = CC_color_Yellow;
        else if(tmp.equals("MAGENTA"))
            ret = CC_color_Manenta;
        else if(tmp.equals("CYAN"))
            ret = CC_color_Cyan;
        else
            ret = CC_color_Default;
        return ret;
    }
    public void setAtscCCOptionBgColor(int ccOptionBgColorId)
    {
        //mTvManager.CC_setBgColor(ccOptionBgColorId);
    }
    public int getAtscCCOptionBgColor()
    {
        int ret = CC_color_Default;
        String tmp = ""/*mTvManager.CC_getSetting("BgColor")*/;
        if(tmp.equals("BLACK"))
            ret = CC_color_Black;
        else if(tmp.equals("WHITE"))
            ret = CC_color_White;
        else if(tmp.equals("RED"))
            ret = CC_color_Red;
        else if(tmp.equals("GREEN"))
            ret = CC_color_Green;
        else if(tmp.equals("BLUE"))
            ret = CC_color_Blue;
        else if(tmp.equals("YELLOW"))
            ret = CC_color_Yellow;
        else if(tmp.equals("MAGENTA"))
            ret = CC_color_Manenta;
        else if(tmp.equals("CYAN"))
            ret = CC_color_Cyan;
        else
            ret = CC_color_Default;
        return ret;
    }
    public void setAtscCCOptionTextOpacity(int ccOptionTextOpacityId)
    {
        //mTvManager.CC_setFgAlpha(ccOptionTextOpacityId);
    }
    public int getAtscCCOptionTextOpacity()
    {
        int ret = CC_opacity_Default;
        String tmp = ""/*mTvManager.CC_getSetting("FgAlpha")*/;
        if(tmp.equals("SOLID"))
            ret = CC_opacity_Solid;
        else if(tmp.equals("FLASHING"))
            ret = CC_opacity_Flashing;
        else if(tmp.equals("TRANSLUCENT"))
            ret = CC_opacity_Translucent;
        else if(tmp.equals("TRANSPARENT"))
            ret = CC_opacity_Transparent;
        else if(tmp.equals("LOW"))
            ret = CC_opacity_low;
        else if(tmp.equals("HIGH"))
            ret = CC_opacity_high;
        else
            ret = CC_opacity_Default;
        return ret;
    }
    public void setAtscCCOptionBgOpacity(int ccOptionBgOpacityId)
    {
        //mTvManager.CC_setBgAlpha(ccOptionBgOpacityId);
    }
    public int getAtscCCOptionBgOpacity()
    {
        int ret = CC_opacity_Default;
        String tmp = ""/*mTvManager.CC_getSetting("BgAlpha")*/;
        if(tmp.equals("SOLID"))
            ret = CC_opacity_Solid;
        else if(tmp.equals("FLASHING"))
            ret = CC_opacity_Flashing;
        else if(tmp.equals("TRANSLUCENT"))
            ret = CC_opacity_Translucent;
        else if(tmp.equals("TRANSPARENT"))
            ret = CC_opacity_Transparent;
        else if(tmp.equals("LOW"))
            ret = CC_opacity_low;
        else if(tmp.equals("HIGH"))
            ret = CC_opacity_high;
        else
            ret = CC_opacity_Default;
        return ret;
    }
//for ATSC end    
    
    //for parent control
    //lockType: 0:channel lock; 1:parental lock; 2:source lock; 3:all
    public void setPasswordVerified(int lockType)
    {
        //mTvManager.setPasswordVerified(lockType);
    }
    public int getPLPassword()
    {
        //return mTvManager.getParentalLockPasswd();
    	return 0;
    }
    
    public void setPLPassword(int value)
    {
        //mTvManager.setParentalLockPasswd(value);
    }
    
    public int getPLEnable()
    {
        //return mTvManager.getParentalLockEnable() ? 1 : 0;
    	return 0;
    }
    
    public void setPLEnable(boolean value)
    {
        //mTvManager.setParentalLockEnable(value);
    }
    
    /*public VChipRatingConfig getVChipRatingConfig()
    {
        return mTvManager.getVChipRatingConfig();
    }
    
    public void setVChipRatingConfig(VChipRatingConfig config)
    {
        mTvManager.setVChipRatingConfig(config);
    }*/
    
    public int getPLUSAMPAA()
    {
        //return mTvManager.getPLUSAMPAA();
    	return 0;
    }
    
    public int setPLUSAMPAA(int value)
    {
        //return mTvManager.setPLUSAMPAA(value);
    	return 0;
    }
    
    public int getPLCanadaEnglish()
    {
        //return mTvManager.getPLCanadaEnglish();
    	return 0;
    }
    
    public int setPLCanadaEnglish(int value)
    {
        //return mTvManager.setPLCanadaEnglish(value);
    	return 0;
    }
    
    public int getPLCanadaFrench()
    {
        //return mTvManager.getPLCanadaFrench();
    	return 0;
    }
    
    public int setPLCanadaFrench(int value)
    {
        //return mTvManager.setPLCanadaFrench(value);
    	return 0;
    }
    
    public int getPLRRTDimensionOrder(int index)
    {
        //return mTvManager.getPLRRTDimensionOrder(index);
    	return 0;
    }
    
    public ArrayList<String> getPLRRTDimensions()
    {
        //return mTvManager.getPLRRTDimensions();
    	return null;
    }
    
    public ArrayList<String> getPLRRTDimensionItems(int index)
    {
        //return mTvManager.getPLRRTDimensionItems(index);
    	return null;
    }
    
    public int getPLRRTDimensionItemValue(int dimenstion, int item)
    {
        //return mTvManager.getPLRRTDimensionItemValue(dimenstion, item);
    	return 0;
    }
    
    public int setPLRRTDimensionItemValue(int dimenstion, int item, int value)
    {
        //return mTvManager.setPLRRTDimensionItemValue(dimenstion, item, value);
    	return 0;
    }
    
    public void resetPLRRT()
    {
        //mTvManager.resetPLRRT();
    }
 
    public void clearPLLock()
    {
        //mTvManager.clearPLLock();
    }
    
    public void setAudioSpdifOutput(int value){
    	//mTvManager.setAudioSpdifOutput(value);
    }
    
    public void setAudioChannelSwap(int value){
    	//mTvManager.setAudioChannelSwap(value);
    }
    
    public int getAudioChannelSwap(){
    	//return mTvManager.getAudioChannelSwap();
    	return 0;
    }
    
    public int getSystemVersion(){
    	//return mTvManager.getSystemVersion();
    	return 0;
    }
    
    public void resetTvSettings(){
    	//mTvManager.resetTvSettings();
    }
}

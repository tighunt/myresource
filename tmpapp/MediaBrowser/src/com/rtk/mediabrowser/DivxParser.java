package com.rtk.mediabrowser;


import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.PopupWindow.OnDismissListener;

public class DivxParser {
	
	private final static String TAG = "DivxParser";
	
	public MediaPlayer mPlayer = null;
	public Handler handler = null;
	public BookMark mBookMark = null;
	public ArrayList<String> fileTitleArray = null;
	public int currIndex = 0;
	public int resume_index = -1;
	
	public Activity mContext = null;
	
	public int drmStatus = NAV_DIVX_DRM_NONE;
	
	public int m_titleNum = 0;
	public int m_chapterNum = 0;
	public int m_current_title_num = 1;
	public int m_current_chapter_num = 1;
	public int melapseTime_seconds = 0;
	public int mcurrChapterStartTime = 0;
	public int mFrameIndex = 0;
	public int chaptertype = 0;
	
	private int textEncoding = 0;
	private int textColor = 0;
	private int fontSize = 20;
	public int SPU_ENABLE = 0;
	
	public boolean HasEditionTitle = false;
	
	public byte[] metaData = new byte[]{0,0,0,0};
	public byte[] rating = new byte[]{0,0,0,0};
	public byte[] inputArray = new byte[]{0,0,0,0};
	public byte[] outputArray = new byte[]{0,0,0,0};
	
	public int m_divx_metadata_type = DIVX_METADATA_TITLE;
	
	public boolean m_firstPrev = false;
	public boolean m_TPT_on = false;
	public boolean m_isMetadataShow = false;
	public int m_showtime = 0;
	
	private ConfirmMessage msg = null;
	private ConfirmMessage short_msg = null;
	
	private ComfirmDailog dailog = null;
	
	private Timer timer = null;
	private TimerTask task_time_out = null;
	
	public boolean isConfirmMsgDismissByBackKey = false;
	
	public boolean isRight2Left = false;
	
	public BookMark mVideoBookMark = null;
	
	public DivxParser(VideoPlayerActivity context) {
		// TODO Auto-generated constructor stub
		mContext = context;
		handler = context.handler;
		mPlayer = context.mPlayer;
		mBookMark = context.mVideoBookMark;
		fileTitleArray = context.fileTitleArray;
		currIndex = context.currIndex;
		resume_index = context.resume_index;
		isRight2Left = context.isRight2Left;
		drmStatus = NAV_DIVX_DRM_NONE;
		isConfirmMsgDismissByBackKey = false;
		msg = new ConfirmMessage(context);
		short_msg = new ConfirmMessage(context,678,226);
		dailog = new ComfirmDailog(context);
		timer = new Timer(true);
	    mVideoBookMark = context.mVideoBookMark; //new BookMark(fileName);
	}

	public static final int NAVPROP_DIVX_DRM_QUERY = 0;
	public static final int NAVPROP_DIVX_DRM_APPROVE_RENTAL = 1;
	public static final int NAVPROP_DIVX_EDITIONNUM_QUERY = 2;
	public static final int NAVPROP_DIVX_EDITIONNAME_QUERY = 3;
	public static final int NAVPROP_DIVX_EDITIONLAWRATE_QUERY = 4;
	public static final int NAVPROP_DIVX_METADATA_QUERY = 5;
	public static final int NAVPROP_DIVX_LAWRATE_QUERY = 6;
	public static final int NAVPROP_DIVX_TITLENUM_QUERY = 7;
	public static final int NAVPROP_DIVX_CHAPTERTYPE_QUERY = 8;
	public static final int NAVPROP_DIVX_LASTPLAYPATH = 9;
	public static final int	NAVPROP_INPUT_GET_PLAYBACK_STATUS = 10;
	public static final int NAVPROP_INPUT_GET_NAV_STATE = 11;
	public static final int NAVPROP_INPUT_SET_NAV_STATE = 12;
	public static final int NAVPROP_INPUT_SET_NAV_STATE_FORCED = 13;
	public static final int NAVPROP_DIVX_VOD_QUERY = 14;
	public static final int NAVPROP_DIVX_TPT_QUERY = 15;
	public static final int NAVPROP_INPUT_GET_VIDEO_STATUS = 16;
	
	public static final int NAV_DIVX_DRM_NONE = 0;
	public static final int NAV_DIVX_DRM_NOT_AUTH_USER = 1;
	public static final int NAV_DIVX_DRM_RENTAL_EXPIRED = 2;
	public static final int NAV_DIVX_DRM_REQUEST_RENTAL = 3;
	public static final int NAV_DIVX_DRM_AUTHORIZED = 4;
	public static final int NAV_DIVX_DRM_RENTAL_AUTHORIZED = 5;
	
	public static final int NAV_DIVX_DRM_REQUEST_RENTAL_NOT_ACCEPT = 6;
	
	public static final int DIVX_METADATA_TITLE = 0;
	public static final int DIVX_METADATA_AUDIO = 1;
	public static final int DIVX_METADATA_SUBTITLE = 2;
	public static final int DIVX_METADATA_CHAPTER = 3;

	//long time1, time2;
	
	public void execSetGetNavProperty(int propertyID)
	{
		switch(propertyID)
		{
			case NAVPROP_DIVX_DRM_QUERY:
			{
				//time1 = System.currentTimeMillis();
				
				byte[] output_DRM = mPlayer.execSetGetNavProperty(NAVPROP_DIVX_DRM_QUERY, inputArray);
				
				/*time2 = System.currentTimeMillis();
	     		Log.e(TAG, "Divx Step1: time = " + (time2 - time1));
		        time1 = time2;*/
				
				if(output_DRM == null)
				{
					if(resume_index >= 0)
					{
						ResumePlay(resume_index);
						resume_index = -1;
					}
					else 
					{
						int index = mVideoBookMark.findBookMark(fileTitleArray.get(currIndex));
						if(index < 0)
							PlayFile();
						else
							ResumePlay(currIndex);
					}
				}
				else
				{
					drmStatus = output_DRM[0] 
								| ((output_DRM[1] & 0xFF) << 8)
								| ((output_DRM[2] & 0xFF) << 16) 
								| ((output_DRM[3] & 0xFF) << 24);
					switch(drmStatus)
					{
						case NAV_DIVX_DRM_NOT_AUTH_USER:
						{
							DRM_Not_Authorization();
						}
						break;
						case NAV_DIVX_DRM_RENTAL_EXPIRED:
						{
							DRM_Rental_Expired(output_DRM);
						}
						break;
						case NAV_DIVX_DRM_REQUEST_RENTAL:
						{
							DRM_Request_Rental(output_DRM);
						}
						break;
						case NAV_DIVX_DRM_NONE:
						case NAV_DIVX_DRM_AUTHORIZED:
						case NAV_DIVX_DRM_RENTAL_AUTHORIZED:
							if(resume_index >= 0)
							{
								ResumePlay(resume_index);
								resume_index = -1;
							}else
							{
								int index = mVideoBookMark.findBookMark(fileTitleArray.get(currIndex));
								if(index < 0)
									PlayFile();
								else
									ResumePlay(currIndex);
							}
						break;
					}
				}
			}
			break;
			case NAVPROP_DIVX_DRM_APPROVE_RENTAL:
			break;
			case NAVPROP_DIVX_EDITIONNUM_QUERY:
			break;
			case NAVPROP_DIVX_EDITIONNAME_QUERY:
			break;
			case NAVPROP_DIVX_EDITIONLAWRATE_QUERY:
			break;
			case NAVPROP_DIVX_METADATA_QUERY:
			{
				Locale locale = mContext.getResources().getConfiguration().locale;
				String language = locale.getISO3Language();
				language = Utility.language_code_map(language);
				
				if(language != null)
				{
					byte[] lang = new byte[language.length()];
					Log.e(TAG,"#####language = "+language);
					lang = language.getBytes();
					
					switch(m_divx_metadata_type)
					{
					case DIVX_METADATA_TITLE:
						//inputArray = new byte[] {DIVX_METADATA_TITLE, 0, 0, 0, 'c', 'h', 'i', '\0'};
						inputArray = new byte[5+lang.length];
						inputArray[0] = DIVX_METADATA_TITLE;
						inputArray[1] = 0;
						inputArray[2] = 0;
						inputArray[3] = 0;
						System.arraycopy(lang, 0, inputArray, 4, lang.length);
						inputArray[7] = '\0';
					break;
					case DIVX_METADATA_AUDIO:
						//inputArray = new byte[] {DIVX_METADATA_AUDIO, 0, 0, 0, 'e', 'n', 'g', '\0'};
						inputArray = new byte[5+lang.length];
						inputArray[0] = DIVX_METADATA_AUDIO;
						inputArray[1] = 0;
						inputArray[2] = 0;
						inputArray[3] = 0;
						System.arraycopy(lang, 0, inputArray, 4, lang.length);
						inputArray[7] = '\0';
					break;
					case DIVX_METADATA_SUBTITLE:
						//inputArray = new byte[] {DIVX_METADATA_SUBTITLE, 0, 0, 0, 'e', 'n', 'g', '\0'};
						inputArray = new byte[5+lang.length];
						inputArray[0] = DIVX_METADATA_SUBTITLE;
						inputArray[1] = 0;
						inputArray[2] = 0;
						inputArray[3] = 0;
						System.arraycopy(lang, 0, inputArray, 4, lang.length);
						inputArray[7] = '\0';
					break;
					case DIVX_METADATA_CHAPTER:
						//inputArray = new byte[] {DIVX_METADATA_CHAPTER, 0, 0, 0, 'e', 'n', 'g', '\0'};
						inputArray = new byte[5+lang.length];
						inputArray[0] = DIVX_METADATA_CHAPTER;
						inputArray[1] = 0;
						inputArray[2] = 0;
						inputArray[3] = 0;
						System.arraycopy(lang, 0, inputArray, 4, lang.length);
						inputArray[7] = '\0';
					break;
					}
					metaData = mPlayer.execSetGetNavProperty(NAVPROP_DIVX_METADATA_QUERY, inputArray);
					//for(int i= 0; i<metaData.length;i++)
						//Log.e(TAG, "metdata["+i+"] = "+metaData[i]);
					//Log.e(TAG, "##########metaData="+metaData);
					if (metaData == null)
					{
						Log.e(TAG, "execSetGetNavProperty(NAVPROP_DIVX_METADATA_QUERY) return null!");
					}
				}else
				{
					Log.e(TAG, "Language "+locale.getISO3Language()+" should be added to Utility.java!");
				}
			}
			break;
			case NAVPROP_DIVX_LAWRATE_QUERY:
				rating = mPlayer.execSetGetNavProperty(NAVPROP_DIVX_LAWRATE_QUERY, inputArray);
				if(rating == null)
				{
					Log.e(TAG, "execSetGetNavProperty(NAVPROP_DIVX_LAWRATE_QUERY) return null!");
				}
			break;
			case NAVPROP_DIVX_TITLENUM_QUERY:
			{
				byte[] output_title = mPlayer.execSetGetNavProperty(NAVPROP_DIVX_TITLENUM_QUERY, inputArray);
				if(output_title != null)
				{
					int titlenum = output_title[0] 
							| ((output_title[1] & 0xFF) << 8)
							| ((output_title[2] & 0xFF) << 16) 
							| ((output_title[3] & 0xFF) << 24);
					
					if(titlenum > 1)
					{
						m_titleNum = titlenum;
					}else if(titlenum == 1){
						output_title = mPlayer.execSetGetNavProperty(NAVPROP_DIVX_EDITIONNUM_QUERY, inputArray);
						if(output_title != null)
						{
							int editionnum = output_title[0] 
									| ((output_title[1] & 0xFF) << 8)
									| ((output_title[2] & 0xFF) << 16) 
									| ((output_title[3] & 0xFF) << 24);
							if(editionnum > 1)
							{
								m_titleNum = editionnum;
								HasEditionTitle = true;
							}else
							{
								m_titleNum = titlenum;
							}
						}else
						{
							m_titleNum = titlenum;
						}
					}
				}else
				{
					Log.e(TAG, "execSetGetNavProperty(NAVPROP_DIVX_TITLENUM_QUERY) return null!");
				}
			}
			break;
			case NAVPROP_DIVX_CHAPTERTYPE_QUERY:
			{
				byte[] output_chapter = mPlayer.execSetGetNavProperty(NAVPROP_DIVX_CHAPTERTYPE_QUERY, inputArray);
				if(output_chapter != null)
				{
					chaptertype = output_chapter[0] 
							| ((output_chapter[1] & 0xFF) << 8)
							| ((output_chapter[2] & 0xFF) << 16) 
							| ((output_chapter[3] & 0xFF) << 24);
				}else
				{
					Log.e(TAG, "execSetGetNavProperty(NAVPROP_DIVX_CHAPTERTYPE_QUERY) return null!");
				}
			}
			break;
			case NAVPROP_DIVX_LASTPLAYPATH:
			break;
			case NAVPROP_INPUT_GET_PLAYBACK_STATUS:
			{	
				byte[] outputArray = mPlayer.execSetGetNavProperty(NAVPROP_INPUT_GET_PLAYBACK_STATUS, inputArray);
				
				if(outputArray != null)
				{
					m_titleNum = (outputArray[4] & 0xFF) 
							| ((outputArray[5] & 0xFF) << 8)
							| ((outputArray[6] & 0xFF) << 16) 
							| ((outputArray[7] & 0xFF) << 24);
					m_current_title_num = (outputArray[8] & 0xFF)
							| ((outputArray[9] & 0xFF) << 8)
							| ((outputArray[10] & 0xFF) << 16) 
							| ((outputArray[11] & 0xFF) << 24);
					m_chapterNum = (outputArray[12] & 0xFF) 
							| ((outputArray[13] & 0xFF) << 8)
							| ((outputArray[14] & 0xFF) << 16)
							| ((outputArray[15] & 0xFF) << 24);
					m_current_chapter_num =  (outputArray[16] & 0xFF)
							| ((outputArray[17] & 0xFF) << 8)
							| ((outputArray[18] & 0xFF) << 16) 
							| ((outputArray[19] & 0xFF) << 24);
					melapseTime_seconds = (outputArray[32] & 0xFF)
							| ((outputArray[33] & 0xFF) << 8)
							| ((outputArray[34] & 0xFF) << 16) 
							| ((outputArray[35] & 0xFF) << 24);
					mcurrChapterStartTime =  (outputArray[48] & 0xFF)  
							| ((outputArray[49] & 0xFF) << 8)
							| ((outputArray[50] & 0xFF) << 16) 
							| ((outputArray[51] & 0xFF) << 24);
					mFrameIndex = (outputArray[52] & 0xFF)  
							| ((outputArray[53] & 0xFF) << 8)
							| ((outputArray[54] & 0xFF) << 16) 
							| ((outputArray[55] & 0xFF) << 24);
					
				}else
				{
					Log.e(TAG, "execSetGetNavProperty(NAVPROP_INPUT_GET_PLAYBACK_STATUS) return null!");
				}
			}
			break;
			case NAVPROP_INPUT_GET_NAV_STATE:
			{
				outputArray = mPlayer.execSetGetNavProperty(NAVPROP_INPUT_GET_NAV_STATE, inputArray);
				if(outputArray == null)
				{
					Log.e(TAG, "execSetGetNavProperty(NAVPROP_INPUT_GET_NAV_STATE) return null!");
				}
			}
			break;
			case NAVPROP_INPUT_SET_NAV_STATE:
			{
				//elapsedtime = 0;
				mPlayer.execSetGetNavProperty(NAVPROP_INPUT_SET_NAV_STATE, inputArray);
			}
			break;
			case NAVPROP_INPUT_SET_NAV_STATE_FORCED:
			{
				mPlayer.execSetGetNavProperty(NAVPROP_INPUT_SET_NAV_STATE_FORCED, inputArray);
			}
			break;
			default:
			break;
		}		
	}
	
	public Boolean ArrayElementNonzeroJudge(byte[] array)
	{
		if(array == null)
			return false;
		
		for(int i = 0; i < array.length; i++)
		{
			if(array[i] != 0)
				return true;
		}
		return false;
	}
	
	public void PlayFile()
	{	
		//final int index = mBookMark.findBookMark(fileTitleArray.get(currIndex));
		
		//if (index < 0)
		//{
			mPlayer.start();
     		
			handler.sendEmptyMessage(HandlerControlerVariable.GET_DURATION);
			
     		execSetGetNavProperty(NAVPROP_DIVX_TITLENUM_QUERY);
     		if(m_titleNum > 1)
			{	
				mPlayer.execPlayTitle(m_current_title_num);
			}
	     	execSetGetNavProperty(NAVPROP_DIVX_CHAPTERTYPE_QUERY);// for M08 STEP 3
	     	
	     	execSetGetNavProperty(NAVPROP_INPUT_GET_PLAYBACK_STATUS);
	     	
	     	m_divx_metadata_type = DIVX_METADATA_TITLE;
			execSetGetNavProperty(NAVPROP_DIVX_METADATA_QUERY);
			execSetGetNavProperty(NAVPROP_DIVX_LAWRATE_QUERY);
			handler.sendEmptyMessage(HandlerControlerVariable.SET_DIVX_METADATA_LAWRATE_INFO);
		
			SPU_ENABLE = 0;
			mBookMark.cleanBookMark();
		//}
		/*else
		{	
			short_msg.setTitle(mContext.getResources().getString(R.string.msg_divx));
			short_msg.setMessage(mContext.getResources().getString(R.string.msg_resumePlay));
			short_msg.setButtonText(mContext.getResources().getString(R.string.msg_yes));
			short_msg.setOnDismissListener(new OnDismissListener(){
				public void onDismiss() {
					isConfirmMsgDismissByBackKey = true;
					mContext.finish();
				}
			});
			short_msg.setKeyListener(true);
			short_msg.confirm_bt.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					if(short_msg.confirm_text.getText().toString().compareTo
					(mContext.getResources().getString(R.string.msg_yes))==0)
					{
						mPlayer.start();
						
						inputArray = mBookMark.getNavBuffer(index);
						execSetGetNavProperty(NAVPROP_INPUT_SET_NAV_STATE);
						
						handler.sendEmptyMessage(HandlerControlerVariable.GET_DURATION);
						
						execSetGetNavProperty(NAVPROP_DIVX_CHAPTERTYPE_QUERY);// for M08 STEP 3
							
						execSetGetNavProperty(NAVPROP_DIVX_TITLENUM_QUERY); // for HasEditionTitle query
						
						execSetGetNavProperty(NAVPROP_INPUT_GET_PLAYBACK_STATUS);
							
						m_divx_metadata_type = DIVX_METADATA_TITLE;
						execSetGetNavProperty(NAVPROP_DIVX_METADATA_QUERY);
						execSetGetNavProperty(NAVPROP_DIVX_LAWRATE_QUERY);
						handler.sendEmptyMessage(HandlerControlerVariable.SET_DIVX_METADATA_LAWRATE_INFO);
						
						int subtitleTrack = mBookMark.getSubtitleTrack(index);
						int subtitleOn = mBookMark.isSubtitleOn(index);
						SPU_ENABLE = subtitleOn;
						mPlayer.getSubtitleInfo();
						mPlayer.setSubtitleInfo(subtitleTrack, subtitleOn, textEncoding, textColor, fontSize);
						
						int audioTrack = mBookMark.getAudioTrack(index);
						mPlayer.setAudioTrackInfo(audioTrack);
						mBookMark.removeBookMark(index);
					}
					else
					{
						mPlayer.start();
						handler.sendEmptyMessage(HandlerControlerVariable.GET_DURATION);
						
						execSetGetNavProperty(NAVPROP_DIVX_TITLENUM_QUERY);
						if(m_titleNum > 1)
						{	
							mPlayer.execPlayTitle(m_current_title_num);
						}
				     	execSetGetNavProperty(NAVPROP_DIVX_CHAPTERTYPE_QUERY);// for M08 STEP 3
				     	
				     	execSetGetNavProperty(NAVPROP_INPUT_GET_PLAYBACK_STATUS);
				     	
						m_divx_metadata_type = DIVX_METADATA_TITLE;
						execSetGetNavProperty(NAVPROP_DIVX_METADATA_QUERY);
						execSetGetNavProperty(NAVPROP_DIVX_LAWRATE_QUERY);
						handler.sendEmptyMessage(HandlerControlerVariable.SET_DIVX_METADATA_LAWRATE_INFO);
						
						mBookMark.removeBookMark(index);
						
						SPU_ENABLE = 0;
					}
					short_msg.setOnDismissListener(null);
					short_msg.dismiss();
					isConfirmMsgDismissByBackKey = false;
				}	
			});
			short_msg.show();
		}*/
	}
	
	
	private void DRM_Not_Authorization()
	{
		message_time_out(TimerDelay.delay_6s);
		
		msg.setMessage(mContext.getResources().getString(R.string.msg_drm_not_auth_user));
		msg.setButtonText(mContext.getResources().getString(R.string.guide_ok));
		msg.left.setVisibility(View.INVISIBLE);
		msg.right.setVisibility(View.INVISIBLE);
		msg.setOnDismissListener(new OnDismissListener(){
			public void onDismiss() {
				
				if(task_time_out != null)
				{
					task_time_out.cancel();
					task_time_out = null;
				}
				
				isConfirmMsgDismissByBackKey = true;
				mBookMark.removeBookMark(resume_index);
				mBookMark.writeBookMark();
				mContext.finish();
			}
		});
		msg.confirm_bt.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				
				if(task_time_out != null)
				{
					task_time_out.cancel();
					task_time_out = null;
				}
				
				msg.setOnDismissListener(null);
				msg.dismiss();
				isConfirmMsgDismissByBackKey = false;
				mBookMark.removeBookMark(resume_index);
				mBookMark.writeBookMark();
				mContext.finish();
			}
		});
		
		if(isRight2Left)
			msg.setMessageRight();
		else
			msg.setMessageLeft();
		msg.show();
	}
	
	private void DRM_Rental_Expired(byte [] output_DRM)
	{
		if(output_DRM == null)
			return;
		
		int useLimit = output_DRM[4];
		int useCount = output_DRM[5];
		
		message_time_out(TimerDelay.delay_6s);
		
		Locale locale = mContext.getResources().getConfiguration().locale;
		String language = locale.getISO3Language();
		
		if(language.compareTo("zho") == 0)
		{
			msg.setMessage(mContext.getResources().getString(R.string.msg_drm_request_rental1)
			+ " " + useLimit + " " + mContext.getResources().getString(R.string.msg_drm_request_rental2)
			+ " " + useCount + " " + mContext.getResources().getString(R.string.msg_drm_rental_expired2));
		}else
		{
			msg.setMessage(mContext.getResources().getString(R.string.msg_drm_request_rental1)
			+ " " + useCount + " " + mContext.getResources().getString(R.string.msg_drm_request_rental2)
			+ " " + useLimit + " " + mContext.getResources().getString(R.string.msg_drm_rental_expired2));
		}
		
		msg.setButtonText(mContext.getResources().getString(R.string.guide_ok));
		msg.left.setVisibility(View.INVISIBLE);
		msg.right.setVisibility(View.INVISIBLE);
		msg.setOnDismissListener(new OnDismissListener(){
			public void onDismiss() {
				if(task_time_out != null)
				{
					task_time_out.cancel();
					task_time_out = null;
				}
				
				isConfirmMsgDismissByBackKey = true;
				mBookMark.removeBookMark(resume_index);
				mBookMark.writeBookMark();
				mContext.finish();
			}
		});
		msg.setKeyListener(true);
		msg.confirm_bt.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				if(task_time_out != null)
				{
					task_time_out.cancel();
					task_time_out = null;
				}
				
				msg.setOnDismissListener(null);
				msg.dismiss();
				isConfirmMsgDismissByBackKey = false;
				mBookMark.removeBookMark(resume_index);
				mBookMark.writeBookMark();
				mContext.finish();
			}
		});
		if(isRight2Left)
			msg.setMessageRight();
		else
			msg.setMessageLeft();
		msg.show();
	}
	
	private void DRM_Request_Rental(byte [] output_DRM)
	{
		int useLimit = output_DRM[4];
		int useCount = output_DRM[5];
		
		message_time_out(TimerDelay.delay_60s);
		
		if(useCount < useLimit)
		{
			Locale locale = mContext.getResources().getConfiguration().locale;
			String language = locale.getISO3Language();
			
			if(language.compareTo("zho") == 0)
			{
				dailog.setMessage(mContext.getResources().getString(R.string.msg_drm_request_rental1)
				+" "+ useLimit + " " + mContext.getResources().getString(R.string.msg_drm_request_rental2)
				+ " " + useCount + " " + mContext.getResources().getString(R.string.msg_drm_request_rental3));
			}else
			{
				dailog.setMessage(mContext.getResources().getString(R.string.msg_drm_request_rental1)
				+" "+ useCount + " " + mContext.getResources().getString(R.string.msg_drm_request_rental2)
				+ " " + useLimit + " " + mContext.getResources().getString(R.string.msg_drm_request_rental3));
			}
			
			dailog.setOnDismissListener(new OnDismissListener(){
				public void onDismiss() {
					
					if(task_time_out != null)
					{
						task_time_out.cancel();
						task_time_out = null;
					}
					
					isConfirmMsgDismissByBackKey = true;
					mBookMark.removeBookMark(resume_index);
					mBookMark.writeBookMark();
					mContext.finish();
				}
			});
			
			dailog.confirm_yes.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					if(task_time_out != null)
					{
						task_time_out.cancel();
						task_time_out = null;
					}
					
					dailog.setOnDismissListener(null);
					dailog.dismiss();
					isConfirmMsgDismissByBackKey = false;
					
					byte[] output = mPlayer.execSetGetNavProperty(NAVPROP_DIVX_DRM_APPROVE_RENTAL, inputArray);
					int ret =  output[0] 
							| ((output[1] & 0xFF) << 8)
							| ((output[2] & 0xFF) << 16) 
							| ((output[3] & 0xFF) << 24);
					
					if(ret == 0) // NAVPROP_DIVX_DRM_APPROVE_RENTAL fail
					{
						byte[] drm = mPlayer.execSetGetNavProperty(NAVPROP_DIVX_DRM_QUERY, inputArray);
						if(drm != null)
						{
							drmStatus = drm[0] 
									| ((drm[1] & 0xFF) << 8)
									| ((drm[2] & 0xFF) << 16) 
									| ((drm[3] & 0xFF) << 24);
							
							if(drmStatus == NAV_DIVX_DRM_NOT_AUTH_USER)
								DRM_Not_Authorization();
							else if(drmStatus == NAV_DIVX_DRM_RENTAL_EXPIRED)
								DRM_Rental_Expired(drm);
						}else
						{
							isConfirmMsgDismissByBackKey = true;
        					mBookMark.removeBookMark(resume_index);
							mBookMark.writeBookMark();
        					mContext.finish();
						}
					}else
					{
						drmStatus = NAV_DIVX_DRM_RENTAL_AUTHORIZED;
						if(resume_index >= 0)
						{
							ResumePlay(resume_index);
							resume_index = -1;
						}else
						{
							int index = mVideoBookMark.findBookMark(fileTitleArray.get(currIndex));
							if(index < 0)
								PlayFile();
							else
								ResumePlay(currIndex);
						}
					}
				}
				
			});
			
			dailog.confirm_no.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					if(task_time_out != null)
					{
						task_time_out.cancel();
						task_time_out = null;
					}
					
					dailog.setOnDismissListener(null);
					dailog.dismiss();
					isConfirmMsgDismissByBackKey = false;
					
					drmStatus = NAV_DIVX_DRM_REQUEST_RENTAL_NOT_ACCEPT;
					mBookMark.removeBookMark(resume_index);
					mBookMark.writeBookMark();
					mContext.finish();
				}
				
			});
			
			if(isRight2Left)
				dailog.setMessageRight();
			else
				dailog.setMessageLeft();
			dailog.show();
		}
	}
	
	public void ResumePlay(int index)
	{
		if(index >= 0 && index < mBookMark.bookMarkLength())
		{	
			int audioTrack = mBookMark.getAudioTrack(index);
			mPlayer.setAudioTrackInfo(audioTrack);
			
			inputArray = mBookMark.getNavBuffer(index);
			execSetGetNavProperty(NAVPROP_INPUT_SET_NAV_STATE);
			
			mPlayer.start();
			
			handler.sendEmptyMessage(HandlerControlerVariable.GET_DURATION);
			
			execSetGetNavProperty(NAVPROP_DIVX_CHAPTERTYPE_QUERY);// for M08 STEP 3
				
			execSetGetNavProperty(NAVPROP_DIVX_TITLENUM_QUERY); // for HasEditionTitle query
			
			execSetGetNavProperty(NAVPROP_INPUT_GET_PLAYBACK_STATUS);
				
			m_divx_metadata_type = DIVX_METADATA_TITLE;
			execSetGetNavProperty(NAVPROP_DIVX_METADATA_QUERY);
			execSetGetNavProperty(NAVPROP_DIVX_LAWRATE_QUERY);
			handler.sendEmptyMessage(HandlerControlerVariable.SET_DIVX_METADATA_LAWRATE_INFO);
			
			int subtitleTrack = mBookMark.getSubtitleTrack(index);
			int subtitleOn = mBookMark.isSubtitleOn(index);
			SPU_ENABLE = subtitleOn;
			mPlayer.getSubtitleInfo();
			mPlayer.setSubtitleInfo(subtitleTrack, subtitleOn, textEncoding, textColor, fontSize);
			
			/*int audioTrack = mBookMark.getAudioTrack(index);
			mPlayer.setAudioTrackInfo(audioTrack);*/
			mBookMark.removeBookMark(index);
			mBookMark.writeBookMark();
		}
	}
	
	public void message_time_out(int delay)
	{
		if(task_time_out != null)
		{
			task_time_out.cancel();
			task_time_out = null;
		}
		
		task_time_out = new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				isConfirmMsgDismissByBackKey = true;
				mBookMark.removeBookMark(resume_index);
				mBookMark.writeBookMark();
				mContext.finish();
			}
			
		};
		timer.schedule(task_time_out, delay);
	}
}
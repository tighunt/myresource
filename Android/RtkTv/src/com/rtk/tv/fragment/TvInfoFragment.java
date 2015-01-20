package com.rtk.tv.fragment;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.rtk.tv.R;
import com.rtk.tv.RtkTvView;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.data.ChannelInfo;
import com.rtk.tv.data.ProgramInfo;
import com.rtk.tv.utils.TvUtil;

public class TvInfoFragment extends BaseFragment {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm", Locale.US);

	private static final long DELAY_DISMISS = 8000;
	
	// View for source label
	private TextView mTextSource;
	
	// Views for service informations
	private View mContainerChannel;
	private TextView mTextChannelName;
	private TextView mTextChannelNum;
	private ImageView mImageSkipped;
	private ImageView mImageLocked;
//	private ImageView mImageSignal;
	
	// View for audio informations
	private View mContainerAudio;
	private ImageView mImageAudioFormat;
	private ImageView mImageAudioTrack;
	private ImageView mImageHardHear;
	private TextView mTextAudioAd;
	private TextView mTextAudioLang;
	
	// Views for program informations
	private View mContainerProgram;
	private TextView mTextNow;
	private TextView mTextProgramName;
	private TextView mTextProgramTime;
	private TextView mTextProgramDescription;
	
	// Icons of the program details
	private View mContainerVideo;
	private ImageView mImageVideoResolution;
	private ImageView mImageVideoAspect;
	private ImageView mImageTeletext;
	private ImageView mImageSubtitle;
	private ImageView mImageMultiAudioTrack;
	private ImageView mImageAudioDescrption;
	private ImageView mImageParentRating;
	private ImageView mImageEncrypted;
	private ImageView mImageGenre;

	// View of next program
	private TextView mTextNext;
	private TextView mTextNextProgramName;
	private TextView mTextNextProgramTime;

	private BroadcastReceiver mBroadcastReceiver;
	private IntentFilter mIntentFilter;

	private Handler mHandler;
	
	// Data
	//private ChannelSchedule mSchedule;
	private int mCurrentPosition;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new Handler();
		// Update content on channel change.
		mBroadcastReceiver = new BroadcastReceiver() {			
			@Override
			public void onReceive(Context context, Intent intent) {
				updateContent();
			}
		};
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction("ACTION_TV_CHANNEL_CHANGED");//no usable now
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_fragment_tv_info, container, false);
		// Source
		mTextSource = (TextView) view.findViewById(R.id.text_source_label);
		
		// Channel
		mContainerChannel = view.findViewById(R.id.container_system_info);
		mTextChannelName = (TextView) view.findViewById(R.id.text_service_name);
		mTextChannelNum = (TextView) view.findViewById(R.id.text_service_num);
		mImageSkipped = (ImageView) view.findViewById(R.id.ic_stat_skip);
		mImageLocked = (ImageView) view.findViewById(R.id.ic_stat_lock);
//		mImageSignal = (ImageView) view.findViewById(R.id.ic_stat_signal);
		
		// Audio
		mContainerAudio = view.findViewById(R.id.container_audio_info);
		mImageAudioFormat = (ImageView) view.findViewById(R.id.image_audio_format);
		mImageAudioTrack = (ImageView) view.findViewById(R.id.image_audio_track);
		mTextAudioLang = (TextView) view.findViewById(R.id.text_audio_language);
		mTextAudioAd = (TextView) view.findViewById(R.id.text_audio_ad);
		mImageHardHear = (ImageView) view.findViewById(R.id.image_hard_of_hear);
		
		// Program -> Video
		mContainerVideo = view.findViewById(R.id.container_video_info);
		mImageVideoResolution = (ImageView) view.findViewById(R.id.ic_info_video_resolution_hd);
		mImageVideoAspect = (ImageView) view.findViewById(R.id.ic_info_video_aspect_ratio);
		// Program -> Detail
		mImageTeletext = (ImageView) view.findViewById(R.id.ic_info_teletext);
		mImageSubtitle = (ImageView) view.findViewById(R.id.ic_info_subtitle);
		mImageMultiAudioTrack = (ImageView) view.findViewById(R.id.ic_info_multi_audio_track);
		mImageAudioDescrption = (ImageView) view.findViewById(R.id.ic_info_audio_description);
		mImageParentRating = (ImageView) view.findViewById(R.id.ic_info_parental_ranking);
		mImageEncrypted = (ImageView) view.findViewById(R.id.ic_info_encrypted);
		mImageGenre = (ImageView) view.findViewById(R.id.ic_info_grene);
		
		// Program -> Now
		mContainerProgram = view.findViewById(R.id.container_program_info);
		mTextNow = (TextView) view.findViewById(R.id.text_now);
		mTextProgramName = (TextView) view.findViewById(R.id.text_program_name);
		mTextProgramTime = (TextView) view.findViewById(R.id.text_program_time);
		mTextProgramDescription = (TextView) view.findViewById(R.id.text_program_description);
		// Program -> Next
		mTextNext = (TextView) view.findViewById(R.id.text_next);
		mTextNextProgramName = (TextView) view.findViewById(R.id.text_program_name_next);
		mTextNextProgramTime = (TextView) view.findViewById(R.id.text_program_time_next);
		getActivity().registerReceiver(mBroadcastReceiver, mIntentFilter);
		return view;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getActivity().unregisterReceiver(mBroadcastReceiver);
	}

	@Override
	public void onResume() {
		super.onResume();
		updateContent();// Supposed to be put in the callbacks of TvManager!?
		mHandler.postDelayed(mRunDismiss, DELAY_DISMISS);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mHandler.removeCallbacks(mRunDismiss);
	}
	
	private void resetDismissTimer() {
		mHandler.removeCallbacks(mRunDismiss);
		mHandler.postDelayed(mRunDismiss, DELAY_DISMISS);
	}
	
	private Runnable mRunDismiss = new Runnable() {
		
		@Override
		public void run() {
			FragmentManager fm = getFragmentManager();
			if (fm != null) {
			    fm.popBackStack(RtkTvView.STACK_LITE, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			}
		}
	};

	private void updateContent() {
		TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
		String curInputId = tm.getCurInputId();		
		// Setup source info
		if (TvManagerHelper.showInputSourceLabel(curInputId)) {
			mTextSource.setText(tm.getInputSourceById(curInputId).loadLabel(getActivity()));
			mTextSource.setVisibility(View.VISIBLE);
		} else {
			mTextSource.setVisibility(View.GONE);
		}		
		// Setup channel informations
		updateChannelInformation(tm);		
		// Setup audio informations
		updateAuiodInformation(tm, curInputId);

	}
	
	private void updateChannelInformation(TvManagerHelper tm) {
		ChannelInfo channelInfo = tm.getCurrentChannelInfo();
		if (channelInfo!=null) {			
			// Update Channel info
			mTextChannelName.setText(channelInfo.getDisplayName());
			mTextChannelNum.setText(channelInfo.getDisplayNumber());
			//setupSkipIcon(mImageSkipped, channelInfo.skipped);
			//setupLockiIcon(mImageLocked, channelInfo.locked);
			mContainerChannel.setVisibility(View.VISIBLE);
			ProgramInfo[] programs = channelInfo.getPrograms();
			// Right-Bottom Pane
			if (programs !=null && programs.length>0) {
				// Update video informations
				//Todo
				/*VideoInformation vi = tm.getCurrentVideoInformations();
				mImageVideoResolution.setImageResource(
						TvUtil.getVideoResolutionIcon(vi.resolution)
				);
				mImageVideoAspect.setImageResource(
						TvUtil.getVideoAspectIcon(vi.aspectRatio)
				);*/
				
				// Update program information
				//mSchedule = (ChannelSchedule) channelInfo;
				//mCurrentPosition = mSchedule.findLatestProgramPosition(tm.currentTvTimeMillis());
				ProgramInfo program = null;
				ProgramInfo nextProgram = null;
				program = channelInfo.getProgramAt(mCurrentPosition);
				nextProgram = channelInfo.getProgramAt(mCurrentPosition + 1);
				updateProgramInfoViews(program, nextProgram);			
				mContainerProgram.setVisibility(View.VISIBLE);
			} else {
				mContainerProgram.setVisibility(View.GONE);
			}
		} else {
			mContainerChannel.setVisibility(View.GONE);
			mContainerProgram.setVisibility(View.GONE);
		}
	}
	
	private void updateAuiodInformation(TvManagerHelper tm, String source) {
		//Todo
		/*if (TvManagerHelper.hasAudioInformation(source)) {
			AudioInformation audioInfo = tm.getCurrentAudioInformations();
			mImageAudioFormat.setImageResource(
					TvResource.getAudioFormatIcon(audioInfo.format));
			mImageAudioTrack.setImageResource(
					TvResource.getAudioTrackIcon(audioInfo.track));
			mTextAudioLang.setText(
					TvResource.getLanguageName(getActivity(), audioInfo.language));
			mTextAudioAd.setVisibility(audioInfo.ad?
					View.VISIBLE:View.GONE);
			mImageHardHear.setVisibility(audioInfo.hardOfHearing ?
					View.VISIBLE : View.GONE);
			mContainerAudio.setVisibility(View.VISIBLE);
		} else {
			mContainerAudio.setVisibility(View.GONE);
		}*/
	}
	
	private void updateProgramInfoViews(ProgramInfo p1, ProgramInfo p2) {
		TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
		long now = tm.currentTvTimeMillis();
		
		// Setup current program
		if (p1 != null) {
			mTextProgramName.setText(p1.getTitle());
			mTextProgramTime.setText(
					getTimeString(TvUtil.translateTvTime(p1.getStartTimeUtcMillis()),
							TvUtil.translateTvTime(p1.getEndTimeUtcMillis())));
			mTextProgramDescription.setText(p1.getLongDescription());
			// Start of setting icons
			//Todo
			/*mImageSubtitle.setVisibility(p1.hasSubtitle?
				View.VISIBLE:View.GONE);
			mImageTeletext.setVisibility(p1.hasTeletext?
				View.VISIBLE:View.GONE);
			mImageMultiAudioTrack.setImageResource(
				TvResource.getMultiAudioTrackIcon(p1.audioCount)
			);
			mImageAudioDescrption.setVisibility(p1.hasAudioDescription()?
				View.VISIBLE:View.GONE);*/
			mImageParentRating.setImageResource(
					TvUtil.getParentRatingIcon(p1.getContentRating()));
			//mImageEncrypted.setVisibility(p1.encrypted?View.VISIBLE:View.GONE);
			mImageGenre.setImageResource(
				TvUtil.getGreneIcon(p1.getCanonicalGenre()));
			// End of setting icons
			
			if (p1.isExpired(now)) {
				setupTextPrev(getActivity(), mTextNow);
			} else if (p1.isPlaying(now)) {
				setupTextNow(getActivity(), mTextNow);
			} else {
				setupTextNext(getActivity(), mTextNow);
			}
		} else {
			setupTextNow(getActivity(), mTextNow);
			mTextNextProgramName.setText("");
			mTextProgramTime.setText("");
			mTextProgramDescription.setText("");
			mImageSubtitle.setVisibility(View.GONE);
			mImageTeletext.setVisibility(View.GONE);
			mImageMultiAudioTrack.setImageDrawable(null);
			mImageAudioDescrption.setVisibility(View.GONE);
			mImageParentRating.setImageDrawable(null);
			mImageEncrypted.setVisibility(View.GONE);
			mImageGenre.setImageDrawable(null);
		}
		
		// Setup next program
		if (p2 != null) {
			mTextNextProgramName.setText(p2.getTitle());
			mTextNextProgramTime.setText(
					getTimeString(TvUtil.translateTvTime(p2.getStartTimeUtcMillis()),
							TvUtil.translateTvTime(p2.getEndTimeUtcMillis())));
			
			if (p2.isExpired(now)) {
				setupTextPrev(getActivity(), mTextNext);
			} else if (p2.isPlaying(now)) {
				setupTextNow(getActivity(), mTextNext);
			} else {
				setupTextNext(getActivity(), mTextNext);
			}
		} else {
			mTextNextProgramName.setText("");
			mTextNextProgramTime.setText("");
			setupTextNext(getActivity(), mTextNext);
		}
	}
	
	private void setCurrentProgramPosition(int position) {
		/*if (mSchedule == null || mCurrentPosition == position || position < 0
				|| position >= mSchedule.getProgramCount()) {
			return;
		}
		
		mCurrentPosition = position;
		updateProgramInfoViews(
				mSchedule.optProgramAt(mCurrentPosition),
				mSchedule.optProgramAt(mCurrentPosition + 1));*/
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		resetDismissTimer();
		switch (keyCode) {
		case KeyEvent.KEYCODE_CHANNEL_UP:
		case KeyEvent.KEYCODE_CHANNEL_DOWN:
			return false;
		case KeyEvent.KEYCODE_INFO:
		case KeyEvent.KEYCODE_ESCAPE:
		    getFragmentManager().popBackStack(RtkTvView.STACK_LITE, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			return true;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			setCurrentProgramPosition(mCurrentPosition - 1);
			return true;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			setCurrentProgramPosition(mCurrentPosition + 1);
			return true;
		case KeyEvent.KEYCODE_PROG_BLUE:
			//Todo
			/*if (mSchedule != null) {
				long now = TvManagerHelper.getInstance(getActivity()).currentTvTimeMillis();
				setCurrentProgramPosition(mSchedule.findLatestProgramPosition(now));
			}*/
			return true;
		default:
			return false;//super.onKeyDown(keyCode, event);
		}
	}
	
	private static String getTimeString(Date start, Date end) {
		return String.format("%s-%s", 
				DATE_FORMAT.format(start),
				DATE_FORMAT.format(end));
	}

	private static void setupTextNow(Context context, TextView tv) {
		tv.setText(R.string.STRING_NOW);
		tv.setTextAppearance(context, R.style.TextAppearance_Widget_TextView_TvInfoTime_Now);
		tv.setBackgroundResource(R.color.background_tvinfo_now);
	}
	
	private static void setupTextNext(Context context, TextView tv) {
		tv.setText(R.string.STRING_NEXT);
		tv.setTextAppearance(context, R.style.TextAppearance_Widget_TextView_TvInfoTime_Next);
		tv.setBackgroundResource(R.color.background_tvinfo_next);
	}

	private static void setupTextPrev(Context context, TextView tv) {
		tv.setText(R.string.STRING_PREVIOUS);
		tv.setTextAppearance(context, R.style.TextAppearance_Widget_TextView_TvInfoTime_Prev);
		tv.setBackgroundResource(R.color.background_tvinfo_prev);
	}
	
	private static void setupSkipIcon(ImageView iv, boolean skipped) {
		if (skipped) {
			iv.setVisibility(View.VISIBLE);
		} else {
			iv.setVisibility(View.GONE);
		}
	}
	
	private static void setupLockiIcon(ImageView iv, boolean locked) {
		iv.setVisibility(locked ? View.VISIBLE : View.GONE);
	}
}

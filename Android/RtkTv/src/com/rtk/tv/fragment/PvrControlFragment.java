
package com.rtk.tv.fragment;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.rtk.tv.R;
import com.rtk.tv.RtkTvView;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.data.ChannelInfo;
import com.rtk.tv.data.ProgramInfo;
import com.rtk.tv.data.TimeshiftStatus;
import com.rtk.tv.utils.Constants;
import com.rtk.tv.utils.TvUtil;
import com.rtk.tv.widget.PvrProgressBar;

public class PvrControlFragment extends BaseFragment implements OnClickListener, OnItemClickListener, OnDismissListener {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("hh:mm", Locale.US);

	private static final boolean VERBOSE = true;
	private static final String TAG = "PVR";

	private static final boolean EPG_TIMESHIFT_BAR = false;

	private static final int DISMISS_TIMEOUT = 10000;
	private static final int MSG_REFRESH = 0;
	private static final int MSG_DISMISS = 1;

	private static final int[] MENU_IDS = {
			R.string.recorded_videos,
			R.string.schedule_recording,
			R.string.recording_schedules
	};

	private static class MenuItem {
		public final String title;

		public MenuItem(Context context, int id) {
			title = context.getString(id);
		}

		@Override
		public String toString() {
			return title;
		}

	}

	private final TimeshiftStatus mStatus = new TimeshiftStatus();
	private final Handler mHandler = new PvrHandler();
	private TvManagerHelper mTvManager;
	private MenuItem[] mMenuItems;
	private ChannelInfo mChannel;
	private TextView mTextMessage;
	private TextView mTextStart;
	private TextView mTextEnd;
	private PvrProgressBar mProgress;

	private ImageView mImageRate;
	private ImageButton mButtonPlayPause;
	private View mButtonRew;
	private View mButtonFast;
	private View mButtonStop;
	private View mButtonRecord;
	private View mButtonMore;
	private ListPopupWindow mListPopupWindow;

	@SuppressLint("HandlerLeak")
	private class PvrHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case MSG_REFRESH:
					if (isResumed()) {
						refreshStatus();
						sendEmptyMessageDelayed(MSG_REFRESH, 1000);
					}
					break;
				case MSG_DISMISS:
					if (!mListPopupWindow.isShowing()) {
						dismiss();
					}
					break;
				default:
					break;
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTvManager = TvManagerHelper.getInstance(getActivity());

		Context context = getActivity();
		mMenuItems = new MenuItem[MENU_IDS.length];
		for (int i = 0; i < MENU_IDS.length; i++) {
			mMenuItems[i] = new MenuItem(context, MENU_IDS[i]);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Context context = inflater.getContext();
		View root = inflater.inflate(R.layout.layout_fragment_pvr_control, container, false);

		// Progress
		mProgress = (PvrProgressBar) root.findViewById(R.id.progress);
		mTextMessage = (TextView) root.findViewById(R.id.text_message);
		mTextStart = (TextView) root.findViewById(R.id.text_starttime);
		mTextEnd = (TextView) root.findViewById(R.id.text_endtime);

		// Playback
		mImageRate = (ImageView) root.findViewById(R.id.image_pvr_play);
		mButtonRew = root.findViewById(R.id.button_rew);
		mButtonFast = root.findViewById(R.id.button_ff);
		mButtonPlayPause = (ImageButton) root.findViewById(R.id.button_pause_play);
		mButtonStop = root.findViewById(R.id.button_stop);
		mButtonRew.setOnClickListener(this);
		mButtonFast.setOnClickListener(this);
		mButtonPlayPause.setOnClickListener(this);
		mButtonStop.setOnClickListener(this);

		// Record
		mButtonRecord = root.findViewById(R.id.button_record);
		mButtonRecord.setOnClickListener(this);

		// Menu
		ArrayAdapter<MenuItem> adapter = new ArrayAdapter<MenuItem>(context, R.layout.item_text_popup);
		adapter.addAll(mMenuItems);

		mButtonMore = root.findViewById(R.id.button_more);
		mButtonMore.setOnClickListener(this);

		Resources r = getResources();
		int w = r.getDimensionPixelSize(R.dimen.dropdown_pvr_width);
		int vo = r.getDimensionPixelOffset(R.dimen.dropdown_pvr_vertical_offset);
		int ho = r.getDimensionPixelOffset(R.dimen.dropdown_pvr_horizontal_offset);
		mListPopupWindow = new ListPopupWindow(inflater.getContext());
		mListPopupWindow.setAnchorView(mButtonMore);
		mListPopupWindow.setWidth(w);
		mListPopupWindow.setOnItemClickListener(this);
		mListPopupWindow.setOnDismissListener(this);
		mListPopupWindow.setAdapter(adapter);
		mListPopupWindow.setModal(true);
		mListPopupWindow.setVerticalOffset(vo);
		mListPopupWindow.setHorizontalOffset(ho);

		return root;
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshStatus();
		mHandler.sendEmptyMessageDelayed(MSG_REFRESH, 1000);
		mHandler.sendEmptyMessageDelayed(MSG_DISMISS, DISMISS_TIMEOUT);
	}

	@Override
	public void onPause() {
		super.onPause();
		mHandler.removeMessages(MSG_REFRESH);
		mHandler.removeMessages(MSG_DISMISS);
	}

	private void refreshStatus() {
		mTvManager.getTimeshiftStatus(mStatus);
		mChannel = TvManagerHelper.getInstance(getActivity()).getCurrentChannelInfo();
		if (VERBOSE) {
			Log.v(TAG, mStatus.toString());
		}
		updateViews();
	}

	private void updateViews() {
		mTextMessage.setVisibility(View.INVISIBLE);
		updatePlaybackButtons();
		updateProgress();
	}

	private void updatePlaybackButtons() {
		int stat = mStatus.stat;
		int rate = (int) mStatus.rate;
		switch (rate) {
			case 2:
				mImageRate.setImageResource(R.drawable.ic_pvr_play_2x);
				mImageRate.setVisibility(View.VISIBLE);
				break;
			case 4:
				mImageRate.setImageResource(R.drawable.ic_pvr_play_4x);
				mImageRate.setVisibility(View.VISIBLE);
				break;
			case 16:
				mImageRate.setImageResource(R.drawable.ic_pvr_play_16x);
				mImageRate.setVisibility(View.VISIBLE);
				break;
			case 32:
				mImageRate.setImageResource(R.drawable.ic_pvr_play_32x);
				mImageRate.setVisibility(View.VISIBLE);
				break;
			default:
				mImageRate.setVisibility(View.INVISIBLE);
				break;
		}
		
		// ButtonPlayPause
		switch (stat) {
			default:
			case Constants.STAT_TIMESHIFT_UNKNOWN:
			case Constants.STAT_TIMESHIFT_DISABLED:
			case Constants.STAT_TIMESHIFT_PLAYBACK:
				mButtonPlayPause.setImageResource(R.drawable.ic_pvr_pause);
				mButtonPlayPause.setId(R.drawable.ic_pvr_pause);
				break;
			case Constants.STAT_TIMESHIFT_PAUSE:
			case Constants.STAT_TIMESHIFT_FFWD:
			case Constants.STAT_TIMESHIFT_SFWD:
			case Constants.STAT_TIMESHIFT_FRWD:
			case Constants.STAT_TIMESHIFT_SRWD:
			case Constants.STAT_TIMESHIFT_STOP:
				mButtonPlayPause.setImageResource(R.drawable.ic_pvr_play);
				mButtonPlayPause.setId(R.drawable.ic_pvr_play);
				break;
		}
		
		// Stop Button
		if (mTvManager.isPvrTimeShiftEnabled()) {
			mButtonStop.setVisibility(View.VISIBLE);
		} else {
			mButtonStop.setVisibility(View.INVISIBLE);
		}
	}

	@SuppressWarnings("unused")
	private void updateProgress() {
		TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
		final long now = tm.currentTvTimeMillis();
		int nowSec = (int) (now / 1000L);
		
		ProgramInfo program = null;
		if (EPG_TIMESHIFT_BAR) {
			if (mChannel != null) {
				program = mChannel.findLatestProgram(now);
			}
		}	
		// Display only recording times in timeshift bar.
		// No EPG available
		if (program == null) {
			// No EPG + Timeshift
			if (mStatus.isTimeshifting()) {
				int duration = mStatus.getRecordTimeSeconds() - mStatus.getStartTimeSeconds();
				int play = mStatus.getPlayTimeSeconds() - mStatus.getStartTimeSeconds();
				
				mProgress.setMax(duration);
				mProgress.setProgress(duration);
				mProgress.setRecordDuration(duration);
				mProgress.setIndicatorProgress(play);
				
				mTextStart.setText(String.format("%02d:%02d:%02d", mStatus.startHour,
						mStatus.startMinute, mStatus.startSecond));
				mTextEnd.setText(String.format("%02d:%02d:%02d", mStatus.recordHour,
						mStatus.recordMinute, mStatus.recordSecond));
				// No EPG + Not Timeshift
			} else {
				mProgress.setProgress(0);
				mProgress.setIndicatorProgress(-1);
				mProgress.setRecordDuration(-1);
				mTextStart.setText(null);
				mTextEnd.setText(null);
			}

		// Has EPG
		// Display EPG information in the timeshift progress bar.
		} else {
			// program
			int startSec = (int) (program.getStartTimeUtcMillis() / 1000L);
			int endSec = (int) (program.getEndTimeUtcMillis() / 1000L);
			int duration = endSec - startSec;
			mProgress.setMax(duration);
			mProgress.setProgress(nowSec - startSec);

			// EPG + Timeshift
			if (mStatus.isTimeshifting()) {
				int recordDuration = mStatus.getRecordTimeSeconds() - mStatus.getStartTimeSeconds();
				int playOffset = mStatus.getRecordTimeSeconds() - mStatus.getPlayTimeSeconds();
				mProgress.setIndicatorProgress(nowSec - startSec - playOffset);
				mProgress.setRecordDuration(recordDuration);

			// EPG + No Timeshift
			} else {
				mProgress.setIndicatorProgress(-1);
				mProgress.setRecordDuration(-1);
			}
			mTextStart.setText(DATE_FORMAT.format(TvUtil.translateTvTime(program.getStartTimeUtcMillis())));
			mTextEnd.setText(DATE_FORMAT.format(TvUtil.translateTvTime(program.getEndTimeUtcMillis())));
		}

	}

	@Override
	public void onClick(View v) {
		// Reset dismiss message
		mHandler.removeMessages(MSG_DISMISS);
		mHandler.sendEmptyMessageDelayed(MSG_DISMISS, DISMISS_TIMEOUT);

		switch (v.getId()) {
			// Playback
			case R.id.button_rew:
				mTvManager.rewindTimeshift();
				refreshStatus();
				break;
			case R.id.button_ff:
				mTvManager.forwardTimeshift();
				refreshStatus();
				break;
			case R.drawable.ic_pvr_play: {
				boolean b = mTvManager.resumeTimeshift();
				if (!b) {
					Toast.makeText(getActivity(), R.string.msg_failed_to_resume_timeshift, Toast.LENGTH_SHORT).show();
				}
				refreshStatus();
			}
				break;
			case R.drawable.ic_pvr_pause:
				// Timeshift is disabled
				if (mStatus.stat == Constants.STAT_TIMESHIFT_DISABLED ||
					mStatus.stat == Constants.STAT_TIMESHIFT_UNKNOWN) {
					// Start timeshift and pause
					if (!mTvManager.setPvrTimeShiftEnable(true, false)) {
						Toast.makeText(getActivity(), R.string.msg_failed_to_start_timeshift, Toast.LENGTH_SHORT).show();
					}
				} else {
					// Pause timeshift
					if (!mTvManager.pauseTimeshift()) {
						Toast.makeText(getActivity(), R.string.msg_failed_to_pause_timeshift, Toast.LENGTH_SHORT).show();
					}
				}
				refreshStatus();
				break;
			// Record
			case R.id.button_record:
				if (!mTvManager.isPvrTimeShiftEnabled()) {
					toggleRecord();
				} else {
					Toast.makeText(getActivity(), R.string.msg_disable_timeshift_before_record, Toast.LENGTH_SHORT).show();
				}
				break;
			// Stop
			case R.id.button_stop:
				mTvManager.setPvrTimeShiftEnable(false);
				break;
			// More
			case R.id.button_more:
				mListPopupWindow.show();
				break;
			default:
				break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Reset dismiss message
		mHandler.removeMessages(MSG_DISMISS);
		mHandler.sendEmptyMessageDelayed(MSG_DISMISS, DISMISS_TIMEOUT);

		switch (keyCode) {
			case KeyEvent.KEYCODE_MENU:
				mButtonMore.requestFocus();
				mListPopupWindow.show();
				return true;
			case KeyEvent.KEYCODE_CHANNEL_DOWN:
			case KeyEvent.KEYCODE_CHANNEL_UP:
				return false;
			case KeyEvent.KEYCODE_PROG_BLUE:
				dismiss();
				return true;
			default:
				return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int strId = MENU_IDS[position];
		/*switch (strId) {
			case R.string.recorded_videos:
				Intent intent = new Intent(getActivity(), MediaListActivity.class);
				getActivity().startActivity(intent);
				break;
			case R.string.schedule_recording:
				intent = new Intent(getActivity(), RecordScheduleSetupActivity.class);
				getActivity().startActivity(intent);
				break;
			case R.string.recording_schedules:
				intent = new Intent(getActivity(), RecordScheduleListActivity.class);
				getActivity().startActivity(intent);
				break;
			default:
				break;
		}*/
		mListPopupWindow.dismiss();
	}

	/**
	 * On menu pop-up dismiss
	 */
	@Override
	public void onDismiss() {
		mHandler.removeMessages(MSG_DISMISS);
		mHandler.sendEmptyMessageDelayed(MSG_DISMISS, DISMISS_TIMEOUT);
	}

	private void toggleRecord() {
		if (mTvManager.isRecording()) {
			boolean b = mTvManager.stopRecord();
			if (!b) {
				Toast.makeText(getActivity(), R.string.msg_failed_to_stop_record, Toast.LENGTH_SHORT).show();
			}
		} else {
			boolean b = mTvManager.startRecord();
			if (!b) {
				Toast.makeText(getActivity(), R.string.msg_failed_to_start_record, Toast.LENGTH_SHORT).show();
			}
		}
		refreshStatus();
	}

	private void dismiss() {
		mListPopupWindow.dismiss();
		FragmentManager fm = getFragmentManager();
		if (fm != null) {
		    fm.popBackStack(RtkTvView.STACK_LITE, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
	}

}

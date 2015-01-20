package com.rtk.tv.fragment;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.rtk.tv.R;
import com.rtk.tv.RtkTvView;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.data.ChannelInfo;
import com.rtk.tv.data.ProgramInfo;
import com.rtk.tv.utils.TvUtil;
import com.rtk.tv.widget.CallbackHorizontalScrollView;

public class EpgFragment extends BaseFragment {
	
	private static final SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat(
			"EE, dd", Locale.US);

	private static final SimpleDateFormat DATE_FORMAT_FULL = new SimpleDateFormat(
			"HH:mm EE, dd MMM yyyy", Locale.US);
	
	private static final int MIN_DAY_OFFSET = 0;
	private static final int MAX_DAY_OFFSET = 7;
	private static final int DEFAULT_DAY_OFFSET = 0;

	public static final String TAG = "EpgFragment";
	
	private final Handler mHandler = new Handler(Looper.getMainLooper());

	// Data
	private int mDayOffset = DEFAULT_DAY_OFFSET;
	private ChannelInfo[] mData;
	
	// User status
	private int mFocusedChannelIdx;
	
	//// Views
	private View mButtonPrev;
	private View mButtonNext;
	
	private TextView mTextMode;

	private TextView mTextDate;
	private TextView mTextCurrentTime;
	
	// List
//	private ListView mListView;
	private ViewGroup mContainerChannelItem;
	private MyAdapter mListAdapter;
	
	// Time Bar
	private CallbackHorizontalScrollView mScrollTime;
	private TimeBarController mTimeBarControl;
	private ViewGroup mContainerTimeBar;
	
	// Epg items Area
	private ViewGroup mContainerEpg;
	private EpgBoardController mEpgControl;
	
	private ScrollView mScroll;//The vertical one
	private CallbackHorizontalScrollView mScrollEpg;
	private View mProgress;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    Log.d(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.layout_fragment_epg, container, false);
		// Mode
		mTextMode = (TextView) view.findViewById(R.id.text_channel_mode);

		// Day controls
		mTextCurrentTime = (TextView) view.findViewById(R.id.text_full_time);
		mTextDate = (TextView) view.findViewById(R.id.text_date);
		mButtonPrev = view.findViewById(R.id.button_prev);
		mButtonPrev.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setDayOffset(mDayOffset - 1);
			}
		});
		mButtonNext = view.findViewById(R.id.button_next);
		mButtonNext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setDayOffset(mDayOffset + 1);
			}
		});

		// Channel List
//		mListView = (ListView) view.findViewById(R.id.list);
		mContainerChannelItem = (ViewGroup) view.findViewById(R.id.container_epg_channel);
		mListAdapter = new MyAdapter();
		mListAdapter.setContainer(mContainerChannelItem);
//		mListView.setAdapter(mListAdapter);
		
		// Time Bar
		mContainerTimeBar = (ViewGroup) view.findViewById(R.id.container_time_bar);
		mScrollTime = (CallbackHorizontalScrollView) view.findViewById(R.id.scroll_epg_time);
		mTimeBarControl = new TimeBarController(mContainerTimeBar);
		mTimeBarControl.initializeLayout();
		
		// EPG Board
		mContainerEpg = (ViewGroup) view.findViewById(R.id.container_epg_item);
		mEpgControl = new EpgBoardController(mContainerEpg);
		
		// Content/Progress
		mScrollEpg = (CallbackHorizontalScrollView) view.findViewById(R.id.scroll_epg_item);
		mScrollEpg.setFocusable(false);
		mProgress = view.findViewById(R.id.progress_epg);
		
		// Bind ScrollView
		CallbackHorizontalScrollView.bindScrollView(mScrollEpg, mScrollTime);
		
		mScroll = (ScrollView) view.findViewById(R.id.scroll);
		mScroll.setFocusable(false);
		
		updateMode();
		updateContent(null);
		updateDateSettingViews();
		mScrollTime.requestFocus();
		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
		ChannelInfo info = tm.getCurrentChannelInfo();
		if(info!=null)
			mFocusedChannelIdx = info.getIndex();
	}

	@Override
	public void onResume() {
		super.onResume();
		mUpdateTime.run();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mHandler.removeCallbacks(mUpdateTime);
	}
	
	private void setDayOffset(int dayOffset) {
		if (dayOffset < MIN_DAY_OFFSET || dayOffset > MAX_DAY_OFFSET || mDayOffset == dayOffset) {
			return;
		}		
		mDayOffset = dayOffset;
		updateDateSettingViews();		
		// reload content
		updateContent(null);
	}
	
	private void focusOnCurrentProgram() {
		View focus = mEpgControl.findCurrentProgram(mFocusedChannelIdx);
		if (focus != null) {
			// focus.requestFocus(); This doesn't work in touch mode.
			focus.requestFocusFromTouch();
		}
	}
	
	private Runnable mUpdateTime = new Runnable() {
		
		@Override
		public void run() {
			TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
			Date time = new Date(tm.currentTvTimeMillis());
			mTextCurrentTime.setText(DATE_FORMAT_FULL.format(time));
			
			mHandler.postDelayed(this, 1000);
		}
	};
	
	private void updateMode() {
		TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
		mTextMode.setText(tm.getCurInputSource().loadLabel(getActivity()));
	}
	
	private void updateDateSettingViews() {
		Calendar c = Calendar.getInstance();
		TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
		c.setTimeInMillis(tm.currentTvTimeMillis());
		c.add(Calendar.DATE, mDayOffset);
		Date d = c.getTime();
		
		mTextDate.setText(DATE_FORMAT_DATE.format(d));
		
		mButtonNext.setEnabled(mDayOffset < MAX_DAY_OFFSET);
		mButtonPrev.setEnabled(mDayOffset > MIN_DAY_OFFSET);
		
		// Not focusable when disabled
		mButtonNext.setFocusable(mDayOffset < MAX_DAY_OFFSET);
		mButtonPrev.setFocusable(mDayOffset > MIN_DAY_OFFSET);
	}

	private void updateContent(ChannelInfo[] data) {
		mData = data;
		
		// Hide content and show spinner if it's loading
		if (mData == null) {
			mScrollEpg.setVisibility(View.INVISIBLE);
			mProgress.setVisibility(View.VISIBLE);
			return;
		}
		
		mScrollEpg.setVisibility(View.VISIBLE);
		mProgress.setVisibility(View.GONE);
		
		// Setup views
		mListAdapter.updateData(mData);
		mEpgControl.updateData(mData);
		
		// Setup focus linking
		int channelCount = data.length;
		for (int i = 0; i < channelCount; i++) {
			View cv = mContainerChannelItem.getChildAt(i);
			cv.setId(View.generateViewId());
			
			ViewGroup epg = (ViewGroup) mContainerEpg.getChildAt(i);
			int epgCount = epg.getChildCount();
			for (int j = 0; j < epgCount; j++) {
				View p = epg.getChildAt(j);
				if (p.isFocusable()) {
					p.setId(View.generateViewId());
					p.setNextFocusLeftId(cv.getId());
					cv.setNextFocusRightId(p.getId());
					break;
				}
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		// Forward/Rewind: Horizontal scroll 
		case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
			mScrollEpg.arrowScroll(View.FOCUS_RIGHT);
			return true;
		case KeyEvent.KEYCODE_MEDIA_REWIND:
			mScrollEpg.arrowScroll(View.FOCUS_LEFT);
			return true;
		// Volume Up/Down: Horizontal page scroll
		// This is not functional for the events are eaten by the system volume control.
		case KeyEvent.KEYCODE_VOLUME_UP:
			mScrollEpg.pageScroll(View.FOCUS_RIGHT);
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			mScrollEpg.pageScroll(View.FOCUS_LEFT);
			return true;
		// Media Previous/Next: Switch date
		case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
			mButtonPrev.performClick();
			return true;
		case KeyEvent.KEYCODE_MEDIA_NEXT:
			mButtonNext.performClick();
			return true;
		// Channel Up/Down: Vertical scroll
		case KeyEvent.KEYCODE_CHANNEL_UP:
			mScroll.arrowScroll(View.FOCUS_UP);
			return true;
		case KeyEvent.KEYCODE_CHANNEL_DOWN:
			mScroll.arrowScroll(View.FOCUS_DOWN);
			return true;
		// Blue button: Reset date
		case KeyEvent.KEYCODE_PROG_BLUE:
			setDayOffset(0);
			focusOnCurrentProgram();
			return true;
		// Exit
		case KeyEvent.KEYCODE_ESCAPE:
		case KeyEvent.KEYCODE_PROG_GREEN:
		//case KeyEvent.KEYCODE_BACK: //move handle back key to Tv_strategy
		case 254:
			//getFragmentManager().beginTransaction().remove(this).commit();
		    getFragmentManager().popBackStack(RtkTvView.STACK_LITE, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			return true;
		default:
			return super.onKeyDown(keyCode, event);
		}
	}
	

	/**
	 * The list adapter for the channel entries on the left pane.
	 * @author Jason
	 *
	 */
	private class MyAdapter extends BaseAdapter implements OnFocusChangeListener, OnClickListener {

		private  ChannelInfo[] mData;
		private ViewGroup mContainer;
		
		public MyAdapter() {
		}
		
		public void setContainer(ViewGroup viewGroup) {
			mContainer = viewGroup;
			updateView();
		}

		@Override
		public int getCount() {
			if(mData == null)
				return 0;
			return mData.length;
		}

		@Override
		public Object getItem(int position) {
			if(mData == null)
				return null;
			return mData[position];
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Context context = parent.getContext();
			if (convertView == null) {
				int height = context.getResources().getDimensionPixelSize(R.dimen.epg_item_height);
				convertView = LayoutInflater.from(context).inflate(
						R.layout.item_epg_channel, parent, false);				
				// Fix view hegith width it margin
				MarginLayoutParams params = (MarginLayoutParams) convertView.getLayoutParams();
				params.height = height - params.topMargin - params.bottomMargin;
				convertView.setLayoutParams(params);
			}
			TextView textNum = (TextView) convertView.findViewById(R.id.text_channel_num);
			TextView textName = (TextView) convertView.findViewById(R.id.text_channel_name);
			ChannelInfo info = mData[position];
			textNum.setText(info.getDisplayNumber());
			textName.setText(info.getDisplayName());			
			convertView.setClickable(true);
			convertView.setFocusable(true);
			convertView.setOnFocusChangeListener(this);
			convertView.setOnClickListener(this);
			convertView.setTag(info);
			return convertView;
		}
		
		public void updateData( ChannelInfo[] channels) {
			mData = channels;
			notifyDataSetChanged();
			updateView();
		}

		private void updateView() {
			if (mContainer != null) {
				final int childCount = mContainer.getChildCount();
				final int count = getCount();
				for (int i = 0; i < count; i++) {
					View convertView = null;
					if (i < childCount) {
						convertView = mContainer.getChildAt(i);
					}
					View v = getView(i, convertView, mContainer);
					if (convertView == null) {
						mContainer.addView(v);
					}
				}
				if (childCount > count) {
					mContainer.removeViews(count, childCount);
				}
			}
		}

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
				mFocusedChannelIdx = mContainer.indexOfChild(v);
			}
		}

		@Override
		public void onClick(View v) {
			ChannelInfo info = (ChannelInfo) v.getTag();
			if (info != null) {
				TvManagerHelper tm = TvManagerHelper.getInstance(v.getContext());
				tm.setChannel(info.getChannelId());
				getFragmentManager().popBackStack(RtkTvView.STACK_LITE, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			}
		}
	}
	
	/**
	 * The view adapter which initializes the views of time bar control.
	 * @author Jason
	 *
	 */
	private static class TimeBarController {
		
		private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm", Locale.US);
//		private static final String FORMAT_TIME = "%s ~ %s";
		private static final int TIME_INTERNAL = 30;//30 minutes
		
		private ViewGroup mContainer;
		
		public TimeBarController(ViewGroup container) {
			mContainer = container;
		}

		public void initializeLayout() {
			Context context = mContainer.getContext();
			mContainer.removeAllViews();

			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			
			String time;//, nextTime;
			final int width = context.getResources().getDimensionPixelSize(R.dimen.epg_time_interval_with);
			for(int i = 0; i < 24 * 60 / TIME_INTERNAL; i++) {
				TextView tv = (TextView) LayoutInflater.from(context)
						.inflate(R.layout.item_epg_time, mContainer, false);
				// Fix the view width with its margin
				MarginLayoutParams params = (MarginLayoutParams) tv.getLayoutParams();
				params.width = width - params.leftMargin - params.rightMargin;
				tv.setLayoutParams(params);
				
				time = DATE_FORMAT.format(c.getTime());
				c.add(Calendar.MINUTE, TIME_INTERNAL);
//				nextTime = DATE_FORMAT.format(c.getTime());
//				tv.setText(String.format(FORMAT_TIME, time, nextTime));
				tv.setText(time);
				mContainer.addView(tv);
			}
		}
		
	}
	
	/**
	 * An adapter which controls the views of program items. 
	 * @author Jason Lin
	 *
	 */
	private class EpgBoardController implements OnFocusChangeListener, OnClickListener, OnKeyListener {
		
		private static final String TAG_FRAGMENT_INFO = "program_info";
		private static final int TIME_INTERNAL = 30;//30 minutes
		
		private ViewGroup mContainer;
		
		private ChannelInfo[] mData;
		
		public EpgBoardController(ViewGroup container) {
			mContainer = container;
		}

		public View findCurrentProgram(int channelIdx) {
			if (mData == null || mContainer == null ||
				channelIdx < 0 || channelIdx >= mData.length) {
				return null;
			}			
			// Get current program
			Context context = mContainer.getContext();
			TvManagerHelper tm = TvManagerHelper.getInstance(context);
			
			ChannelInfo info = mData[channelIdx];
			ProgramInfo p = info.findLatestProgram(tm.currentTvTimeMillis());
			if (p == null) {
				return null;
			}
			
			// Find the view item of the program
			ViewGroup container = (ViewGroup) mContainer.getChildAt(channelIdx);
			int childCount = container.getChildCount();
			for (int i = 0; i < childCount; i++) {
				View v = container.getChildAt(i); 
				if (v.getTag() == p) {
					return v;
				}
			}
			return null;
		}

		public void updateData(ChannelInfo[] channels) {
		    Log.d(TAG, "EpgBoardController: updateData");
			mData = channels;
			Context context = mContainer.getContext();
			mContainer.removeAllViews();
			TvManagerHelper tm = TvManagerHelper.getInstance(context);
			long now = tm.currentTvTimeMillis();
			Calendar c = Calendar.getInstance();		
			// for each channel
			for (int i = 0; i < channels.length; i++) {
				LinearLayout layout = new LinearLayout(context);
				layout.setOrientation(LinearLayout.HORIZONTAL);				
				ChannelInfo info = channels[i];
				ProgramInfo[] programs = info.getPrograms();
				Date baseTime = info.getBaseTime();
				layout.setTag(info);
				int lastTimeStamp = 0;
				// for each program
				for (int j = 0; j < programs.length; j++) {
					final ProgramInfo p = programs[j];
					c.setTime(TvUtil.translateTvTime(p.getStartTimeUtcMillis()));
					
					int minStart =
							(int) ((p.getStartTimeUtcMillis() - baseTime.getTime())/60000L);
					int minEnd =
							(int) ((p.getEndTimeUtcMillis() - baseTime.getTime())/60000L);
					
					// Check/Revise program time
					if (minStart < 0 && minEnd < 0 ||
						minStart > minEnd) {
						Log.e(TAG, String.format("Invalid program: %s", p.toString()));
						continue;
					}
					
					if (minStart < 0) {
						minStart = 0;
					}					
					if (minEnd > 1440) {//1440 minutes = 1 day
						minEnd = 1440;
					}					
					// 
					int duration = minEnd - minStart; 					
					// Put empty view in the time slot
					int empty = minStart - lastTimeStamp;
					if (empty > 0 ) {
						View v = createEmptyView(context, empty);
						layout.addView(v);
					}				
					// Add program item into layout
					if (duration > 0) {
						TextView tv = createProgramView(layout, duration);
						tv.setText(p.getTitle());
						tv.setTag(p);
						tv.setOnClickListener(this);
						tv.setOnFocusChangeListener(this);
						tv.setOnKeyListener(this);
						
						boolean expired = p.isExpired(now);
						tv.setEnabled(!expired);
						tv.setFocusable(!expired);
						layout.addView(tv);
						lastTimeStamp = minEnd;
					}
					
				}
				// Put empty view at the end of first row.
				// Make sure the layout's width is equal to the time bar.
				if (i == 0 && lastTimeStamp < 1440) {
					int w = 1440 - lastTimeStamp;
					View v = createEmptyView(context, w);
					layout.addView(v);
				}
				
				int height = context.getResources().getDimensionPixelSize(R.dimen.epg_item_height);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						height);
				mContainer.addView(layout, params);
			}
		}
		
		private TextView createProgramView(LinearLayout parent, int minutes) {
			Context context = parent.getContext();
			float width = context.getResources().getDimension(R.dimen.epg_time_interval_with);
			width *= (float) minutes / (float) TIME_INTERNAL;
			TextView tv = (TextView) LayoutInflater.from(context).inflate(R.layout.item_epg_item, parent, false);
			LinearLayout.LayoutParams params = (LayoutParams) tv.getLayoutParams();
			width -= params.leftMargin;
			width -= params.rightMargin;
			params.width = (int) width;
			tv.setLayoutParams(params);
			return tv;
		}
		
		private View createEmptyView(Context context, int minutes) {
			float width = context.getResources().getDimension(R.dimen.epg_time_interval_with);
			width *= (float) minutes / (float) TIME_INTERNAL;
			TextView tv = new TextView(context);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					(int) width,
					LinearLayout.LayoutParams.MATCH_PARENT);
			tv.setLayoutParams(params);
			return tv;
		}
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
				ChannelInfo info = (ChannelInfo) ((View) v.getParent()).getTag();
				EpgFragment.this.mFocusedChannelIdx = info.getIndex();
			}
		}

		@Override
		public void onClick(View v) {
			FragmentManager fm = getFragmentManager();
			ProgramInfoFragment f = (ProgramInfoFragment) fm.findFragmentByTag(TAG_FRAGMENT_INFO);
			if (f != null) {
				f.dismiss();
			} else {
				ProgramInfo p = (ProgramInfo) v.getTag();
				f = ProgramInfoFragment.createInstance(p);
				f.show(fm, TAG_FRAGMENT_INFO);
			}
		}

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// For scroll views take over the focus control of its descendants,
			// the first EPG item won't return focus back to the channel entry by pressing left key.
			// Request focus manually by setting up an OnKeyListener.
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				int id;
				View next = null;
				switch(keyCode) {
				case KeyEvent.KEYCODE_DPAD_LEFT:
					id = v.getNextFocusLeftId();
					next = v.getRootView().findViewById(id);
					break;
				case KeyEvent.KEYCODE_DPAD_RIGHT:
				case KeyEvent.KEYCODE_DPAD_UP:
				case KeyEvent.KEYCODE_DPAD_DOWN:
				default:
					return false;
				}
				if (next != null) {
					next.requestFocus();
					return true;
				}
				
			// Key handles for pressing EPG items
			} else if (event.getAction() == KeyEvent.ACTION_UP) {
				switch(keyCode) {
					// Record
					case KeyEvent.KEYCODE_PROG_RED:
						/*ViewGroup parent = (ViewGroup) v.getParent();
						MediaManager mm = MediaManager.getInstance(getActivity());
						DialogFragment f = mm.enterScheduleByEpg(
								(ChannelInformation) parent.getTag(), (TvProgram) v.getTag(),
								TvManagerHelper.getInstance(getActivity()).getInputSource());
						f.show(getFragmentManager(), "dialog");*/
						return true;
					default:
						return false;
				}
			}
			return false;
		}
	}
	
}

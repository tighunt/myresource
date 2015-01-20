package com.rtk.tv.fragment.tvsetup;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rtk.tv.R;
import com.rtk.tv.TvManagerHelper;

public class DtvManualTuningResultFragment extends Fragment implements OnItemClickListener, OnClickListener/*, OnItemMoveListener*/ {
	
	private static final boolean DEBUG = true;
	private static final String TAG = "DtvManualTuningResult";	
	public static Bundle buildArguments(int index, int frequency, int bandwidth, boolean advanced, int modulation, int symbolRate) {
		Bundle args = new Bundle();
		args.putInt(ARG_INDEX, index);
		args.putInt(ARG_FREQ, frequency);
		args.putInt(ARG_BAND, bandwidth);
		args.putBoolean(ARG_ADVANCE, advanced);
		args.putInt(ARG_MODL, modulation);
		args.putInt(ARG_SYMB, symbolRate);
		return args;
	}	
	private static final String ARG_INDEX = "index";
	private static final String ARG_FREQ = "frequency";
	private static final String ARG_BAND = "bandwidth";
	private static final String ARG_ADVANCE = "advanced";
	private static final String ARG_MODL = "modulation";
	private static final String ARG_SYMB = "symbol";
	private static final int REQUEST_SCAN = 1;
	private static final int REQUEST_SORT = 2;
	private static final int[] SORT_POLICIES = {
		0,1,2,3
		/*
		TvManager.SORT_CHANNEL_NUMBER,
		TvManager.SORT_FREQUENCY,
		TvManager.SORT_DVB_SERVICE_ID,
		TvManager.SORT_DVB_LCN,*/
	};

	private ListView mListView;
	private ProgressBar mProgress;
	//private MyAdapter mAdapter;
	private Drawable mListSelector;
	private View mButtonScan;

	//private List<ChannelInformation> mData = new ArrayList<TvManagerHelper.ChannelInformation>();
	private boolean mIsScanning = false;
	private TvManagerHelper tm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tm = TvManagerHelper.getInstance(getActivity());
		
		loadContent();
		
		// Register receiver
		//IntentFilter filter = new IntentFilter(TvBroadcastDefs.ACTION_TV_MEDIA_MESSAGE);
		//getActivity().registerReceiver(mReceiver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(mReceiver);
	}

	private void loadContent() {
		//mData.clear();
		//mData.addAll(tm.getDtvChannelList());
	}
	
	private void startDtvScanning(int index, int frequency, int bandwidth, boolean advanced, int modulation, int symbolRate) {
		// Start scan
		tm.stopDtvScanning();
		if (advanced) {
			//tm.mTvManager.setDtvScanFrontend(symbolRate, modulation);
		}
		//tm.startDtvScanning(frequency, bandwidth , index, TvManager.SOURCE_DTV1);
		mIsScanning = true;
		updateViewContent();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		// Scan
		if (requestCode == REQUEST_SCAN && resultCode == Activity.RESULT_OK) {
			// Get arguments
			Bundle args = data.getExtras();
			startDtvScanning(
					args.getInt(ARG_INDEX), args.getInt(ARG_FREQ), args.getInt(ARG_BAND),
					args.getBoolean(ARG_ADVANCE, false),
					args.getInt(ARG_MODL), args.getInt(ARG_SYMB));
			
		// Sort
		} else if (requestCode == REQUEST_SORT && resultCode == Activity.RESULT_OK) {
			/*int selection = data.getIntExtra(SimpleListDialog.EXTRA_SELECTION, 0);
			int policy = SORT_POLICIES[selection];
			Log.d(TAG, "Sort channel: policy=" + policy);
			if (tm.mTvManager.sortChannel(policy)) {
				loadContent();
				updateViewContent();
			} else {
				Toast.makeText(getActivity(), R.string.msg_failed_to_sort_channel, Toast.LENGTH_SHORT).show();
			}*/
		}
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			/*int messageId = intent.getIntExtra(TvBroadcastDefs.EXTRA_TV_MEDIA_MESSAGE, -1);
			if (DEBUG) {
				Log.d(TAG, String.format("Receive message: action = %s, id = %d", intent.getAction(), messageId));
			}
			if (messageId == TvManager.TV_MEDIA_MSG_SCAN_FREQ_UPDATE) {
				// (Only update after finished.)
				
			} else if (messageId == TvManager.TV_MEDIA_MSG_SCAN_MANUAL_COMPLETE) {
				tm.mTvManager.tvScanManualComplete();
				mIsScanning = false;
				loadContent();
				updateViewContent();
			}*/
		}
		
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_fragment_dtv_manual_tuning_result, container, false);
		mListView = (ListView) view.findViewById(R.id.list);
		//mAdapter = new MyAdapter(inflater.getContext(), R.layout.item_dtv_manual_tuning, mData);
		//mAdapter.setOnItemMoveListener(this);
		//mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);

		mListView.requestFocus();
		mListView.setOnKeyListener(mOnListKey);
		
		mProgress = (ProgressBar) view.findViewById(R.id.progress);
		
		mButtonScan = view.findViewById(R.id.button_manual_tuning);
		mButtonScan.setOnClickListener(this);
		mButtonScan.setOnKeyListener(mOnButtonKey);
		
		View buttonSort = view.findViewById(R.id.button_sort);
		buttonSort.setOnClickListener(this);
		buttonSort.setOnKeyListener(mOnButtonKey);
		
		updateViewContent();
		view.requestFocus();
		return view;
	}
	
	private void updateViewContent() {
		if (mIsScanning) {
			mProgress.setVisibility(View.VISIBLE);
		} else {
			mProgress.setVisibility(View.INVISIBLE);
		}
		//mAdapter.notifyDataSetChanged();
	}
	
	private final OnKeyListener mOnButtonKey = new OnKeyListener() {
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_DPAD_UP:
					// Circular child focusing
					mListView.setSelection(mListView.getCount() - 1);
					mListView.requestFocus();
					return true;
				case KeyEvent.KEYCODE_DPAD_DOWN:
					mListView.setSelection(0);
					mListView.requestFocus();
					return true;
				default:
					break;
				}
			}
			return false;
		}
	};
	
	private final OnKeyListener mOnListKey = new OnKeyListener() {
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			/*if (event.getAction() == KeyEvent.ACTION_DOWN) {
				switch (keyCode) {
				// Up
				case KeyEvent.KEYCODE_DPAD_UP:
					int up = mAdapter.moveUp();
					if (up >= 0) {
						mListView.setSelection(up);
						return true;
						
					// Circular child focusing
					} else if (mListView.getSelectedItemPosition() == 0) {
						mButtonScan.requestFocus();
						return true;
					}
					break;
				// Down
				case KeyEvent.KEYCODE_DPAD_DOWN:
					int down = mAdapter.moveDown();
					if (down >= 0) {
						int y = mListView.getSelectedView().getTop();
						mListView.setSelectionFromTop(down, y);
						return true;
					}
					break;
				// Move
				case KeyEvent.KEYCODE_PROG_GREEN:
					int position = mListView.getSelectedItemPosition();
					if (position >= 0) {
						mAdapter.startStopMoving(position);
					}
					return true;
				// OK
				case KeyEvent.KEYCODE_DPAD_CENTER:
					if (mAdapter.stopMoving()) {
						return true;
					}
					break;
				// Delete
				case KeyEvent.KEYCODE_PROG_RED: {
					ChannelInformation ci = (ChannelInformation) mListView.getSelectedItem();
					if (ci != null) {
						TvManager tm = (TvManager) getActivity().getSystemService(Context.TV_SERVICE);
						tm.setChannelDel(ci.channelIndex, true);
						loadContent();
						updateViewContent();
					}
				}
					return true;
				// Favorite
				case KeyEvent.KEYCODE_PROG_YELLOW: {
					ChannelInformation ci = (ChannelInformation) mListView.getSelectedItem();
					if (ci != null) {
						TvManager tm = (TvManager) getActivity().getSystemService(Context.TV_SERVICE);
						tm.setChannelFav(ci.channelIndex, !ci.isFavorite);
						ci.isFavorite = !ci.isFavorite;
						updateViewContent();
					}
				}
					return true;
				// Lock/Unlock
				case KeyEvent.KEYCODE_PROG_BLUE: {
					ChannelInformation ci = (ChannelInformation) mListView.getSelectedItem();
					if (ci != null) {
						TvManager tm = (TvManager) getActivity().getSystemService(Context.TV_SERVICE);
						tm.setChannelBlock(ci.channelIndex, !ci.isBlocked);
						ci.isBlocked = !ci.isBlocked;
						updateViewContent();
					}
				}
					return true;
				default:
					break;
				}
			}*/
			return false;
		}
	};

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//ChannelInformation channel = mData.get(position);
		//tm.setCurrentChannelByIndex(channel.channelIndex, null, Looper.getMainLooper());
		//FragmentUtils.showSubFragment(this, DtvSignalInfoFragment.class);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.button_manual_tuning: {
				Fragment f = new DtvManualTuningFragment();
				f.setTargetFragment(this, REQUEST_SCAN);
				getFragmentManager().beginTransaction()
				.detach(this)
				.add(getId(), f)
				.addToBackStack(f.getClass().getName())
				.commit();
			}
				break;
			// Sort policy selection dialog
			case R.id.button_sort: {
				String title = getString(R.string.STRING_SORT);
				String[] options = getResources().getStringArray(R.array.sort_dtv);
				//SimpleListDialog ld = SimpleListDialog.newInstance(title, options);
				//ld.setTargetFragment(this, REQUEST_SORT);
				//ld.show(getFragmentManager(), "sort");
			}
				break;
			default:
				break;
		}
	}

	private static class ViewHolder {
		View statNew;
		View statFavorite;
		View statLock;
		
		TextView textPosition;
		TextView textName;
		TextView textNumber;
		TextView textServiceId;
		TextView textFrequency;
	}

	/*private static class MyAdapter extends MovableArrayAdapter<ChannelInformation> {

		public MyAdapter(Context context, int resource, List<ChannelInformation> objects) {
			super(context, resource, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.item_dtv_manual_tuning, parent, false);
				holder = new ViewHolder();
				holder.statNew = convertView.findViewById(R.id.ic_stat_new);
				holder.statFavorite = convertView.findViewById(R.id.ic_stat_favorite);
				holder.statLock = convertView.findViewById(R.id.ic_stat_lock);
				holder.textPosition = (TextView) convertView.findViewById(R.id.text_position);
				holder.textName = (TextView) convertView.findViewById(R.id.text_name);
				holder.textNumber = (TextView) convertView.findViewById(R.id.text_number);
				holder.textServiceId = (TextView) convertView.findViewById(R.id.text_service_id);
				holder.textFrequency = (TextView) convertView.findViewById(R.id.text_frequency);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag(); 
			}
			ChannelInformation c = getItem(position);
			
			holder.statNew.setVisibility(c.isNew ? View.VISIBLE : View.INVISIBLE);
			holder.statFavorite.setVisibility(c.isFavorite ? View.VISIBLE : View.INVISIBLE);
			holder.statLock.setVisibility(c.isBlocked ? View.VISIBLE : View.INVISIBLE);
			
			holder.textPosition.setText(String.valueOf(c.channelIndex));
			holder.textName.setText(c.name);
			holder.textNumber.setText(c.formatNumber("%02d", "%d-%d"));
			holder.textServiceId.setText(String.valueOf(c.serviceId));
			holder.textFrequency.setText(String.valueOf(c.frequency));
			
			//
			if (isMoving()) {
				if (getMovingItemPosition() == position) {
					convertView.setAlpha(1F);
					
				} else {
					convertView.setAlpha(0.5F);
				}
			} else {
				convertView.setAlpha(1F);
			}
			
			return convertView;
		}
		
	}

	@Override
	public void onItemMoved(Object item, int oldPosition, int position) {
		// Restore selector
		mListView.setSelector(mListSelector);
		
		if (oldPosition == position) {
			return;
		}
		
		// Move channel
		ChannelInformation ci = (ChannelInformation) item;
		TvManager tm = (TvManager) getActivity().getSystemService(Context.TV_SERVICE);
		Log.d("Test", "Move channel " + ci.channelIndex + " to " + position);
		if(!tm.moveChannelByIndex(ci.channelIndex, position)) {
			Toast.makeText(getActivity(), R.string.msg_failed_to_move_channel, Toast.LENGTH_SHORT).show();
		}
		
		// Refresh
		loadContent();
		updateViewContent();
	}

	@Override
	public void onItemMoving(Object item, int position) {
		mListSelector = mListView.getSelector();
		mListView.setSelector(android.R.color.transparent);
	}*/
}

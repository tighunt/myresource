package com.rtk.tv.fragment.tvsetup;

import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;

import com.rtk.tv.R;
import com.rtk.tv.TvManagerHelper;

public class AtvManualTuningFragment extends Fragment implements OnItemClickListener, OnItemSelectedListener {
	
	private ListView mListView;
	/*private ArrayAdapter<ChannelInformation> mAdapter;
	private List<ChannelInformation> mData;*/
	private TvManagerHelper tm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tm = TvManagerHelper.getInstance(getActivity());
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Get data
		//mData = tm.getAtvChannelList();

		// Setup data and the list view.
		View view = inflater.inflate(R.layout.layout_fragment_atv_manual_tuning, container, false);
		/*mListView = (ListView) view.findViewById(R.id.list);
		mListView.setOnItemClickListener(this);
		mListView.setOnKeyListener(mOnListKey);
		mAdapter = new MyAdapter(inflater.getContext(), R.layout.item_dtv_manual_tuning);
		mListView.setAdapter(mAdapter);*/
		updateViewContent();
		view.requestFocus();
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mListView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
	            mListView.setOnItemSelectedListener(AtvManualTuningFragment.this);
            }
			@Override
            public void onNothingSelected(AdapterView<?> parent) {
	            
            }
		});
		
		// Select current channel
		mListView.requestFocus();
		/*int idx = tm.getCurrentChannelIndex();
		if (idx >= 0 && idx < mData.size()) {
			mListView.setSelection(idx);
		} else {
			mListView.setOnItemSelectedListener(this);
		}*/
		
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	
	private void updateViewContent() {
		/*mAdapter.clear();
		mAdapter.addAll(mData);
		mAdapter.notifyDataSetChanged();*/
	}
	
	private final OnKeyListener mOnListKey = new OnKeyListener() {
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_DPAD_UP:
					if (mListView.getSelectedItemPosition() == 0) {
						mListView.setSelection(mListView.getCount() - 1);
						return true;
					}
					break;
				case KeyEvent.KEYCODE_DPAD_DOWN:
					if (mListView.getSelectedItemPosition() == mListView.getCount() - 1) {
						mListView.setSelection(0);
						return true;
					}
					break;
				default:
					break;
				}
			}
			return false;
		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    /*ChannelInformation info = mData.get(position);
		tm.setCurrentChannelByIndex(info.channelIndex, null, null);
		
		getFragmentManager().beginTransaction()
		.detach(this)
		.add(getId(), new AtvManualTuningSettingFragment(), Tv_strategy.STACK_MENU)
		.addToBackStack(Tv_strategy.STACK_MENU)
		.commit();*/
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
	    //ChannelInformation info = mData.get(position);
		//tm.setCurrentChannelByIndex(info.channelIndex, null, null);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}
/*	
	private static class MyAdapter extends ArrayAdapter<ChannelInformation> {

		public MyAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.item_atv_manual_tuning, parent, false);
			}
			ChannelInformation c = getItem(position);
			TextView textPosition = (TextView) convertView.findViewById(R.id.text_position);
			TextView textLabel = (TextView) convertView.findViewById(R.id.text_label);
			View imageSkip = convertView.findViewById(R.id.image_skip);
			
			textPosition.setText(c.formatNumber("%03d", "%d-%d"));
			textLabel.setText(c.name);
			imageSkip.setVisibility(c.skipped ? View.VISIBLE : View.INVISIBLE);
			
			return convertView;
		}		
	}*/
}

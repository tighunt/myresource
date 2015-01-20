package com.rtk.tv.fragment;

import java.util.List;

import com.rtk.tv.R;
import com.rtk.tv.RtkTvView;
import com.rtk.tv.TvManagerHelper;
import android.app.FragmentManager;
import android.content.Context;
import android.media.tv.TvInputInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class InputSourceFragment extends BaseFragment implements OnItemClickListener {

	private static final long SELECTION_DELAY = 3000;
	private final Handler mHandler = new Handler(Looper.getMainLooper());
	
	private ListView mListView;
	private ArrayAdapter<Source> mAdapter;
	
	private static class Source {
		public final String name;
		public final String id;
		
		public Source(String id, String name) {
			this.name = name;
			this.id = id;
		}	
		@Override
		public String toString() {
			return name;
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		resetTimeout();
	}

	@Override
	public void onPause() {
		super.onPause();
		mHandler.removeCallbacks(mConfirmSelection);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_fragment_input_selector, container, false);
		mListView = (ListView) view.findViewById(R.id.list);
		mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mAdapter = new ArrayAdapter<InputSourceFragment.Source>(inflater.getContext(), R.layout.item_text);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mListView.setOnKeyListener(mOnListKey);
		
		updateListData(inflater.getContext());
		mListView.requestFocus();
		return view;
	}
	
	/**
	 * Circular child focusing
	 */
	private final OnKeyListener mOnListKey = new OnKeyListener() {
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				switch (keyCode) {
				/*case KeyEvent.KEYCODE_DPAD_RIGHT:
				     if((mListView.getSelectedItemPosition() == 5)&&(cecenable_list()==true)&&(Runtime.hdmi_dector(1)>0)){
					    Log.d("tiny_li","KeyEvent.KEYCODE_DPAD_RIGHT:");
						mHandler.removeCallbacks(mConfirmSelection);
					 }
				     if((mListView.getSelectedItemPosition() == 6)&&(cecenable_list()==true)&&(Runtime.hdmi_dector(2)>0)){
					    Log.d("tiny_li","KeyEvent.KEYCODE_DPAD_RIGHT:");
						mHandler.removeCallbacks(mConfirmSelection);
					 }
				     if((mListView.getSelectedItemPosition() == 7)&&(cecenable_list()==true)&&(Runtime.hdmi_dector(3)>0)){
					    Log.d("tiny_li","KeyEvent.KEYCODE_DPAD_RIGHT:");
						mHandler.removeCallbacks(mConfirmSelection);
					    Activity a = getActivity();
					 }					 
					 break;*/
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
				case KeyEvent.KEYCODE_TV_INPUT:
					int pos = mListView.getSelectedItemPosition() + 1;
					if (pos >= mListView.getCount()) {
						pos = 0;
					}
					mListView.setSelection(pos);
					return true;
				default:
					break;
				}
			}
			return false;
		}
	};
	
	private void updateListData(Context context) {
		TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
		String currentInputId = tm.getCurInputId();
		List<TvInputInfo> list = tm.getInputSourceList();
		if(list == null)
			return;
		// Setup list view
		mAdapter.clear();
		for (int i = 0; i < list.size(); i++) {
			TvInputInfo info = list.get(i);
			mAdapter.add(new Source(info.getId(), info.loadLabel(context).toString()));
			// Check current selection
			if (info.getId().equals(currentInputId)) {
				mListView.setItemChecked(i, true);
				mListView.setSelection(i);
			}
		}		
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Make sure ListView is focused
		if (!mListView.hasFocus()) {
			mListView.requestFocus();
		}	
		// Reset timeout
		resetTimeout();	
		switch (keyCode) {
		// Cancel
		case KeyEvent.KEYCODE_ESCAPE:
		case KeyEvent.KEYCODE_DPAD_LEFT:
			mHandler.removeCallbacks(mConfirmSelection);
			getFragmentManager().popBackStack(RtkTvView.STACK_LITE, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			return true;
		// Pass to list view directly
		case KeyEvent.KEYCODE_TV_INPUT:
			return mListView.dispatchKeyEvent(event);
		default:
			return super.onKeyDown(keyCode, event);
		}
	}
	
	private void resetTimeout() {
		mHandler.removeCallbacks(mConfirmSelection);
		mHandler.postDelayed(mConfirmSelection, SELECTION_DELAY);
	}
	
	private Runnable mConfirmSelection = new Runnable() {
		
		@Override
		public void run() {
			int index = mListView.getSelectedItemPosition();
			if (index >= 0) {
				mListView.performItemClick(mListView.getChildAt(index), index, mAdapter.getItemId(index));
			} else {
				//getFragmentManager().beginTransaction().remove(SourceSelectorFragment.this).commit();
			    getFragmentManager().popBackStack(RtkTvView.STACK_LITE, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			}
		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Source source = mAdapter.getItem(position);
		TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());		       
	       if (!tm.getCurInputId().equals(source.id))
	       {
	    	   /*tm.setInputSource(source.id);
	    	   FragmentManager fm = getFragmentManager();
	    	   Fragment dfb = fm.findFragmentByTag("DFB");
	               if (source.id != 3)
	               {
	                       if (dfb != null)
	                               getFragmentManager().beginTransaction().remove(dfb).commit();
	               }
	               else 
	               {
	                       if (dfb == null && (tm.isGingaExisted()||tm.mheg5IsCIPLUS())) {
	                               dfb = new DfbFragment();
	                               getFragmentManager().beginTransaction()
	                           .add(android.R.id.content, dfb, "DFB").commit();
	                       }
	               }*/
	      }
	       getFragmentManager().popBackStack(RtkTvView.STACK_LITE, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		// Show TV info
		/*Activity a = getActivity();
		if (a instanceof RtkTvView) {
			if((source.id == 3 ||source.id == 4)&&(tm.mheg5IsQuietlyTune())){
                    Log.d("SourceSelectorFragment","onItemClick ### can not show info bar=====");
			}else{
				((RtkTvView) a).showLiteFragment(TvInfoFragment.class);
			}
		}*/
	}
	private boolean cecenable_list()
	{
	  /*Activity a = getActivity();
	  try{
			if(Settings.Global.getInt(((RtkTvView) a).getContentResolver(), Settings.Global.CEC_HDMI_CEC)==1)
			{
             return true;
	        }
			else
			return false;
		 }catch (SettingNotFoundException e) {}	*/ 
		 return false;
	}

}

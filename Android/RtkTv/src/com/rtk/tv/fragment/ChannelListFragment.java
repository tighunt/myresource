
package com.rtk.tv.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import com.rtk.tv.R;
import com.rtk.tv.RtkTvView;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.data.ChannelInfo;

import android.util.Log;

public class ChannelListFragment extends BaseFragment implements OnClickListener {

	public static final int PAGE_ALL = 0;
	public static final int PAGE_FAVORITE = 1;

	public static final int KEYCODE = KeyEvent.KEYCODE_PROG_YELLOW;
	private static final long DISMISS_TIMEOUT = 6000;
    
	private static abstract class Category {

		abstract public SubListFragment onCreateFragment();
		abstract public String getTitle(Context context);

		private Reference<SubListFragment> mFragment = null;
		
		public SubListFragment getFragment() {
			SubListFragment f = null;
			if (mFragment != null) {
				f = mFragment.get();
			}
			if (f == null) {
				f = onCreateFragment();
				mFragment = new WeakReference<SubListFragment>(f);
			}
			return f;
		}
	}
	
	private Handler mHandler = new Handler();
	
	// Page
	private final List<Category> mCategories = new ArrayList<Category>();
	private int mCurrentPage = 0;
	
	// View
	private TextView mTextTitle;
	private FrameLayout mContainer;
	private int opCount =0;
	private int isLeftKey = 0;
    private int curIndex=0;
	private TvManagerHelper tm;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCategories.clear();
		mCategories.add(ALL_CHANNEL);
		mCategories.add(FAVORITE_CHANNEL);
		tm = TvManagerHelper.getInstance(getActivity());
		curIndex=tm.getChannelListFragIndex();
		Log.d("#####", "curIndex="+curIndex);
		for(int i=0;i<opCount;i++)
	    {
          mCategories.add(ALL_CHANNEL);
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_fragment_channel_list, container, false);
		mContainer = (FrameLayout) view.findViewById(R.id.container_content);
		mTextTitle = (TextView) view.findViewById(R.id.text_title);
		view.findViewById(R.id.button_left).setOnClickListener(this);
		view.findViewById(R.id.button_right).setOnClickListener(this);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		if(opCount < 1){
			setupPages(PAGE_ALL, false);
		}else{
		   if(curIndex> 1){
				isLeftKey = 3;
				setupPages(curIndex, false);
		   }else{
                setupPages(0, false);
		   }
          
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		setupPages(-1, false);
		mContainer = null;
	}
	
	protected void setupPages(final int page, boolean adjustIndex) {
		FragmentManager fm = getChildFragmentManager();
		int id = mContainer.getId();
		Fragment old = fm.findFragmentById(id);
		
		if (adjustIndex && page >= mCategories.size()) {
			mCurrentPage = 0;
		} else if (adjustIndex && page < 0) {
			mCurrentPage = mCategories.size() - 1;
		} else {
			mCurrentPage = page;
		}
		
		// Check empty
		if (mCurrentPage < 0 || mCurrentPage >= mCategories.size()) {
			if (old != null) {
				fm.beginTransaction().remove(old).commit();
			}
			mTextTitle.setText(null);
			return;
		}

        int opModeIndex=-1;
		if(isLeftKey ==1 &&opCount >0){
		  if( mCurrentPage == mCategories.size() - 1){
		  	  opModeIndex=opCount-1;
              enterOPMode(opModeIndex);
		  }else if(mCurrentPage ==0){
              Log.d("#####", "page="+page);
		  }else if(mCurrentPage ==1){
		      opModeIndex=0;
              exitOPMode(opModeIndex);
		  }else if(mCurrentPage >1){
		      opModeIndex=mCurrentPage -1;
              exitOPMode(opModeIndex);
			  opModeIndex = opModeIndex-1;
			  enterOPMode(opModeIndex);
		  }
		}
		else if(isLeftKey ==2 && opCount >0){
           if( mCurrentPage ==2){
		   	  opModeIndex=0;
              enterOPMode(opModeIndex);
		  }else if(mCurrentPage ==0){
		      opModeIndex=opCount-1;
              exitOPMode(opModeIndex);
		  }else if(mCurrentPage >2){
		      opModeIndex=mCurrentPage -3;
              exitOPMode(opModeIndex);
			  opModeIndex = opModeIndex+1;
			  enterOPMode(opModeIndex);
		  }
		}else if(isLeftKey ==3)
		{
			opModeIndex=curIndex-2;
			enterOPMode(opModeIndex);
		}
		// Add
		tm.setChannelListFragIndex(mCurrentPage);
		Category c = mCategories.get(mCurrentPage);
		mTextTitle.setText(c.getTitle(getActivity()));
		if(mCurrentPage >1){
          //mTextTitle.setText(mCiManager.getOPName(opModeIndex));
		}
		
			Fragment f = c.getFragment();
			  
			if (old == null) {
				fm.beginTransaction().add(id, f).commit();
			}else {
			     if(mCurrentPage!=1 && opCount >0)
			  		 c.getFragment().updateListData(c.getFragment().mContext);
				fm.beginTransaction().replace(id, f).commit();
			}
		

		isLeftKey = 0;
	}
	private void enterOPMode(int curPage) {
		//Todo
	}
	private void exitOPMode(int curPage) {
		//Todo
	}
	@Override
	public void onClick(View v) {
		// Reset dismiss callback
		mHandler.removeCallbacks(mRunDismiss);
		mHandler.postDelayed(mRunDismiss, DISMISS_TIMEOUT);		
		switch(v.getId()) {
			case R.id.button_left:
		        isLeftKey = 1;
				setupPages(mCurrentPage - 1, true);
				break;
			case R.id.button_right:
				isLeftKey = 2;
				setupPages(mCurrentPage + 1, true);
				break;
			default:
				break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mHandler.postDelayed(mRunDismiss, DISMISS_TIMEOUT);
	}

	@Override
	public void onPause() {
		super.onPause();
		mHandler.removeCallbacks(mRunDismiss);
	}
	
	private final Runnable mRunDismiss = new Runnable() {	
		@Override
		public void run() {
			mHandler.removeCallbacks(this);
			FragmentManager fm = getFragmentManager();
			if (fm != null) {
			    getFragmentManager().popBackStack(RtkTvView.STACK_LITE, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Reset dismiss callback
		mHandler.removeCallbacks(mRunDismiss);
		mHandler.postDelayed(mRunDismiss, DISMISS_TIMEOUT);

		switch (keyCode) {
		// Pass to default list view implementation
			case KeyEvent.KEYCODE_SPACE:
			case KeyEvent.KEYCODE_PAGE_UP:
			case KeyEvent.KEYCODE_PAGE_DOWN:
				return false;
				// Left/Right Switch
			case KeyEvent.KEYCODE_DPAD_LEFT:
				isLeftKey = 1;
				setupPages(mCurrentPage - 1, true);
				return true;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				isLeftKey = 2;
				setupPages(mCurrentPage + 1, true);
				return true;
				// Dismiss
			case KEYCODE:
			case KeyEvent.KEYCODE_ESCAPE:
			    getFragmentManager().popBackStack(RtkTvView.STACK_LITE, FragmentManager.POP_BACK_STACK_INCLUSIVE);
				return true;
			default:
				return false;
		}
	}
	
	private Category ALL_CHANNEL = new Category() {

		@Override
		public SubListFragment onCreateFragment() {
			SubListFragment f = new SubListFragment();
			return f;
		}

		@Override
		public String getTitle(Context context) {
			return context.getString(R.string.channel);
		}

	};

	private Category FAVORITE_CHANNEL = new Category() {

		@Override
		public SubListFragment onCreateFragment() {
			SubListFragment f = new SubListFragment();
			Bundle args = new Bundle();
			args.putString("filter", "favorite");
			f.setArguments(args);
			return f;
		}

		@Override
		public String getTitle(Context context) {
			return context.getString(R.string.favorite);
		}
	};
	
	private static class SubListFragment extends Fragment implements OnItemClickListener {

		private ListView mListView;
		private ArrayAdapter<ChannelInfo> mAdapter;
		private TvManagerHelper mTvManager;
		private Context mContext;
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mTvManager = TvManagerHelper.getInstance(getActivity());
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			mListView = new ListView(inflater.getContext());
			mListView.setVerticalFadingEdgeEnabled(true);
			mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			mAdapter = new ArrayAdapter<ChannelInfo>(inflater.getContext(), R.layout.item_text);
			mListView.setAdapter(mAdapter);
			mListView.setOnItemClickListener(this);
            mContext = inflater.getContext();
			updateListData(inflater.getContext());
			mListView.requestFocus();
			mListView.setOnKeyListener(mOnListKey);
			return mListView;
		}

		/**
		 * Circular child focusing
		 */
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

		private void updateListData(Context context) {					
			ChannelInfo[] list = mTvManager.getChannels();
			ChannelInfo info = mTvManager.getCurrentChannelInfo();
			if(list == null || list.length == 0)
				return;
			int currentPosition =0;
			if(info!=null)
				currentPosition = info.getIndex();
			// Setup list view
			mAdapter.clear();
			mAdapter.addAll(list);
			mAdapter.notifyDataSetChanged();		
			if (currentPosition >= 0) {
				mListView.setSelection(currentPosition);
				mListView.setItemChecked(currentPosition, true);
			} else {
				mListView.setSelection(0);
			}
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			ChannelInfo info = mAdapter.getItem(position);
			mTvManager.setChannel(info);
			Fragment pf = getParentFragment();
			pf.getFragmentManager().beginTransaction().remove(pf).commit();
			
		}
	}
	

}

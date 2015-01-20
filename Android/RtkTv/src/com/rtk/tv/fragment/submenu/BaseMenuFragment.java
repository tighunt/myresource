package com.rtk.tv.fragment.submenu;

import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.rtk.tv.R;
import com.rtk.tv.fragment.submenu.item.MenuItem;

public abstract class BaseMenuFragment extends Fragment implements OnItemClickListener, OnKeyListener {
	
	public static final String ARG_PARENT_ID = "parent_id";
	public static final String ARG_LAYOUT_ID = "layout_id";

	private static class MenuAdapter extends BaseAdapter {
		
		private List<MenuItem> mItems;

		public MenuAdapter(List<MenuItem> items) {
			super();
			mItems = items;
		}

		@Override
		public int getCount() {
			return mItems.size();
		}

		@Override
		public Object getItem(int position) {
			return mItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = mItems.get(position).getView(convertView, parent);
			v.setEnabled(isEnabled(position));
			return v;
		}

		@Override
		public boolean isEnabled(int position) {
			return mItems.get(position).isEnable();
		}
		
		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}
		
	}
	
	abstract public void onCreateMenuItems(List<MenuItem> items);
	abstract public int getTitle();
	public String getTitleString(){return "";}
	
	private final List<MenuItem> mItems = new ArrayList<MenuItem>();
	
	private ListView mListView;
	private MenuAdapter mAdapter;
	
	private int mLastSelection = -1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	protected View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		int layout = R.layout.layout_fragment_base_menu;
		Bundle bundle = getArguments();
		if (bundle != null) {
			layout = bundle.getInt(ARG_LAYOUT_ID, layout);
		}
		return inflater.inflate(layout, container, false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = onInflateLayout(inflater, container, savedInstanceState);
		mItems.clear();
		onCreateMenuItems(mItems);
		
		// Setup Title
		TextView textTitle = (TextView) layout.findViewById(R.id.text_title);
		if(getTitle()>0)
		    textTitle.setText(getTitle());
		else
		    textTitle.setText(getTitleString());
		
		// Setup ListView
		mListView = (ListView) layout.findViewById(R.id.list);
		mAdapter = new MenuAdapter(mItems);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mListView.setOnKeyListener(this);
		
		Bundle args = getArguments();
		if (args != null) {
			int parentId = args.getInt(ARG_PARENT_ID);
			mListView.setNextFocusDownId(parentId);
			mListView.setNextFocusUpId(parentId);
		}
		
		return layout;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (mListView.isFocused()) {
			mLastSelection = mListView.getSelectedItemPosition();
		} else {
			mLastSelection = -1;
		}
		mItems.clear();
	}
	
	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		if (savedInstanceState != null) {
			mLastSelection = savedInstanceState.getInt("last_list_selection", -1);
		}
		if (mLastSelection >= 0) {
			mListView.requestFocus();
			mListView.setSelection(mLastSelection);
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("last_list_selection", mLastSelection);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
		parent.requestFocusFromTouch();
		parent.setSelection(position);
		MenuItem item = mItems.get(position);
		item.performClick();
	}
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		View itemView = mListView.getSelectedView();
		
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (itemView != null && event.getAction() == KeyEvent.ACTION_DOWN) {
				MenuItem item = (MenuItem) itemView.getTag();
				item.selectPrev();
				mListView.invalidateViews();
			}
			return true;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (itemView != null && event.getAction() == KeyEvent.ACTION_DOWN) {
				MenuItem item = (MenuItem) itemView.getTag();
				item.selectNext();
				mListView.invalidateViews();
			}
			return true;
		default:
			if (itemView != null) {
				MenuItem item = (MenuItem) itemView.getTag();
				return event.dispatch(item, null, null);
			}
			return false;
		}
	}
	
	protected ListView getListView() {
		return mListView;
	}
	
	public Fragment getBaseFragment() {
		Fragment f = getParentFragment();
		if (f == null) {
			f = this;
		}
		return f;
	}
	
	public void notifyDataSetChanged() {
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	protected static int indexOf(int[] array, int value) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	}
	
}

package com.android.seting;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


@TargetApi(11)
public class DetailsFragment extends Fragment{
	
	@TargetApi(11)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	public static DetailsFragment newInstance(int index) { 
		DetailsFragment details = new DetailsFragment();
		Bundle args = new Bundle();
		args.putInt("index", index);
		details.setArguments(args);
		return details;
	}

	public int getShownIndex() {
		return getArguments().getInt("index", 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (container == null)
			return null;

		/*ScrollView scroller = new ScrollView(getActivity());
		TextView text = new TextView(getActivity());

		int padding = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 4, getActivity()
						.getResources().getDisplayMetrics());
		text.setPadding(padding, padding, padding, padding);
		scroller.addView(text);

		text.setText(array[getShownIndex()]);
		return scroller;*/
		
		switch(getShownIndex())
		{
		case 0:
			return inflater.inflate(R.layout.picture_setting, container, false);
		case 1:
			return inflater.inflate(R.layout.sound_setting, container, false);
		case 2:
			return inflater.inflate(R.layout.net_setting, container, false);
		case 3:
			return inflater.inflate(R.layout.advance_setting, container, false);
		case 4:
			return inflater.inflate(R.layout.user_setting, container, false);
		case 5:
			return inflater.inflate(R.layout.local_info, container, false);
		default:
			return inflater.inflate(R.layout.picture_setting, container, false);
		}		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
		 menu.add("Menu 1a").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            menu.add("Menu 1b").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		Toast.makeText(getActivity(), "index is"+getShownIndex()+" && menu text is "+item.getTitle(), 1000).show();
		return super.onOptionsItemSelected(item);
	}
}

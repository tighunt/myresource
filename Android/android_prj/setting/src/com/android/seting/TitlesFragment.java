package com.android.seting;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.android.seting.DetailsFragment;
import android.view.View.OnClickListener;

@TargetApi(11)
public class TitlesFragment extends ListFragment{
	boolean mDualPane;
	int mCurCheckPosition = 0;
	
	public static String[] array = { "图像设置", "声音设置", "网络设置", "高级设置",
		"用户设置", "本机设置"};
	SeekBar sb;
	@TargetApi(11)
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState); 
		sb = (SeekBar)getActivity().findViewById(R.id.seek_brightness);
		sb.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //Do something
                }
            });
		System.out.println("Fragment-->onCreate");
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		System.out.println("Fragment-->onCreateView");
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		System.out.println("Fragment-->onPause");
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();			
		System.out.println("Fragment-->onStop");
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		System.out.println("Fragment-->onAttach");
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		System.out.println("Fragment-->onStart");
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.out.println("Fragment-->onResume");
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.out.println("Fragment-->onDestroy");
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		System.out.println("Fragment-->onActivityCreated");
		setListAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, array));

		View detailsFrame = getActivity().findViewById(R.id.details);

		mDualPane = detailsFrame != null
				&& detailsFrame.getVisibility() == View.VISIBLE;

		if (savedInstanceState != null) {
			mCurCheckPosition = savedInstanceState.getInt("curChoice", 0); 		
			}

		if (mDualPane) {
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

			showDetails(mCurCheckPosition);
		}
		
		//final TextView tv = (TextView)getActivity().findViewById(R.id.text_brightness);
		//final SeekBar sb = (SeekBar)getActivity().findViewById(R.id.seek_brightness);
		
		/*OnSeekBarChangeListener sbLis=new OnSeekBarChangeListener(){
			 
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				//进度改变时触发
				//tv.setText(String.valueOf(sb.getProgress()));
	 
			}
	 
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// 开始拖动时触发，与onProgressChanged区别在于onStartTrackingTouch在停止拖动前只触发一次
				//而onProgressChanged只要在拖动，就会重复触发
			}
	 
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				//结束拖动时触发
	 
			}			
	    };
	    
	    sb.setOnSeekBarChangeListener(sbLis);*/
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);

		outState.putInt("curChoice", mCurCheckPosition);		
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		showDetails(position);
	}
	
	

	void showDetails(int index) {
		mCurCheckPosition = index; 
		if (mDualPane) {
			getListView().setItemChecked(index, true);
			DetailsFragment details = (DetailsFragment) getFragmentManager()
					.findFragmentById(R.id.details); 
			if (details == null || details.getShownIndex() != index) {
				details = DetailsFragment.newInstance(mCurCheckPosition); 

				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				ft.replace(R.id.details, details);			
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.commit();
			}
		} else {
			new AlertDialog.Builder(getActivity()).setTitle(
					android.R.string.dialog_alert_title).setMessage(
					array[index]).setPositiveButton(android.R.string.ok,
					null).show();
		}
	}
}

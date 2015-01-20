package com.rtk.tv.fragment.submenu;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import com.rtk.tv.R;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.fragment.BaseFragment;
import com.rtk.tv.utils.FragmentUtils;
import com.rtk.tv.utils.TvBroadcastDefs;

public class AutoAdjustMenuFragment extends BaseFragment implements OnClickListener {
	private IntentFilter mIntentFilter = new IntentFilter(TvBroadcastDefs.CMD_BCT_TV_VGA_ADJUST_STATUS); 
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() 
	{
        @Override
        public void onReceive(Context context, Intent intent) 
        {
            String action = intent.getAction();
            if (TvBroadcastDefs.CMD_BCT_TV_VGA_ADJUST_STATUS.equals(action)) 
			{
                int msg = intent.getIntExtra(TvBroadcastDefs.PDU_BCT_TV_VGA_ADJUST_STATUS, 1);
				if (msg == TvBroadcastDefs.CC_TV_VGA_ADJUST_SUCCESS)
				{
					getFragmentManager().popBackStack();
				}
            }
        }
    };	

	public static void show(Fragment parent, Class<? extends Fragment> targetFragment, Bundle targetArgs, int title, int message) {
		Bundle arg = new Bundle();
		arg.putInt("title", title);
		arg.putInt("message", message);
		arg.putString("targetClass", targetFragment.getName());
		arg.putBundle("targetArgs", targetArgs);
		FragmentUtils.showSubFragment(parent, AutoAdjustMenuFragment.class, arg);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Bundle args = getArguments();
		int title = args.getInt("title");
		int message = args.getInt("message");

		View v = inflater.inflate(R.layout.dialog_alert_generic, container, false);
		v.findViewById(R.id.button_neutral).setVisibility(View.GONE);
		v.findViewById(R.id.button_cancel).setOnClickListener(this);
		TextView textTitle = (TextView) v.findViewById(R.id.text_title);
		TextView textMessage = (TextView) v.findViewById(R.id.text_message);
        final TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
		textTitle.setText(title);
		textMessage.setText(message);
		getActivity().registerReceiver(mBroadcastReceiver, mIntentFilter);
		tm.setVgaAutoAdjust(); 		
		return v;
	}
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getActivity().unregisterReceiver(mBroadcastReceiver);
	} 

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button_cancel:
				FragmentUtils.popBackSubFragment(this);
				break;
			case R.id.button_ok:
				FragmentUtils.popBackSubFragment(this);
				break;
			default:
				break;
		}
	}

}


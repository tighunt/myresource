
package com.rtk.tv.fragment.tvsetup;

import com.rtk.tv.R;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.fragment.BaseFragment;
import com.rtk.tv.utils.FragmentUtils;
import android.app.ActivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class ResetTvFragment extends BaseFragment implements OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_fragment_reset_tv, container, false);
		v.findViewById(R.id.button_ok).setOnClickListener(this);
		v.findViewById(R.id.button_cancel).setOnClickListener(this);
		return v;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button_ok:
				reset();
				break;
			case R.id.button_cancel:
				break;
			default:
				break;
		}
		FragmentUtils.popBackSubFragment(this);
	}

	private void reset() {
		// Block monkey from reset TV.
		if (!ActivityManager.isUserAMonkey()) {
			TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
			tm.resetTvSettings();
		}
	}

}

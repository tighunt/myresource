
package com.rtk.tv.fragment.tvsetup;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.rtk.tv.R;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.fragment.BaseFragment;

public class SystemInfoFragment extends BaseFragment {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy", Locale.US);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_fragment_system_info, container, false);
		TextView textMessage = (TextView) v.findViewById(R.id.text_message);
		TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());

		StringBuilder sb = new StringBuilder();
		// Manufacturer / Year
		sb.append(Build.MANUFACTURER);
		sb.append('\t');
		sb.append(DATE_FORMAT.format(new Date(Build.TIME)));
		sb.append('\n');		
		sb.append("Board: ");
		sb.append(Build.BOARD);
		sb.append('\n');	
		sb.append("Brand: ");
		sb.append(Build.BRAND);
		sb.append('\n');	
		sb.append("Model: ");
		sb.append(Build.MODEL);
		sb.append('\n');
		sb.append("Build: " + Build.VERSION.INCREMENTAL);
		sb.append('\n');		
		sb.append("TV Application version: ");
		sb.append(tm.getSystemVersion());
		textMessage.setText(sb.toString());
		return v;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_CENTER:
			case KeyEvent.KEYCODE_ENTER:
				getFragmentManager().popBackStack();
				return true;
			default:
				return super.onKeyDown(keyCode, event);
		}
	}

}

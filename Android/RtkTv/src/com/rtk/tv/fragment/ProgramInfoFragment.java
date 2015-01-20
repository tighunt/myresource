package com.rtk.tv.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.rtk.tv.R;
import com.rtk.tv.data.ProgramInfo;

public class ProgramInfoFragment extends DialogFragment {
	
	public static ProgramInfoFragment createInstance(ProgramInfo program) {
		ProgramInfoFragment f = new ProgramInfoFragment();
		Bundle args = new Bundle();
		args.putString("title", program.getTitle());
		args.putLong("start_time", program.getStartTimeUtcMillis());
		args.putLong("end_time", program.getEndTimeUtcMillis());
		args.putString("description", program.getLongDescription());
		args.putString("summary", program.getShortDescription());
		f.setArguments(args);
		return f;
	}
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH:mm", Locale.US);

	private TextView mTextProgramName;
	private TextView mTextProgramTime;
	private TextView mTextProgramDescription;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_TITLE, 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_fragment_program_info, container, false);
		mTextProgramName = (TextView) view.findViewById(R.id.text_program_name);
		mTextProgramTime = (TextView) view.findViewById(R.id.text_program_time);
		mTextProgramDescription = (TextView) view.findViewById(R.id.text_program_description);
		updateContent();
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	private void updateContent() {
		// Get program informations
		Bundle args = getArguments();

		// Setup views
		mTextProgramName.setText(args.getString("title"));
		mTextProgramTime.setText(
				getTimeString(args.getLong("start_time"), args.getLong("end_time")));
		
		String description = args.getString("description");
		String summary = args.getString("summary");
		if (!TextUtils.isEmpty(description)) {
			mTextProgramDescription.setText(description);
		} else {
			mTextProgramDescription.setText(summary);
		}
	}

	private static String getTimeString(long start, long end) {
		return String.format("%s ~ %s", 
				DATE_FORMAT.format(new Date(start)),
				DATE_FORMAT.format(new Date(end)));
	}

}

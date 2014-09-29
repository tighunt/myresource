package com.bamutian.navigation;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.xijiebamutian.R;

public class Temp2 extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.temp2);
	}
	
	@Override
	protected void onResume() {
		ConvertActivityGroup.UpdateToolbar();
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		ConvertActivityGroup.ActivityBack();
	}
}

package com.genius.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Button btnn = new Button(this);
		btnn.setText("asdflashdfkhsadlfhasldf");
		setContentView(btnn);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			SwitchViewDemoActivity.MY_ACTIVITY.finish();
		}

		return super.onKeyDown(keyCode, event);
	}

}

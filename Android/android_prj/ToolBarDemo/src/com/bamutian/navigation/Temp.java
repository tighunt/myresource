package com.bamutian.navigation;

import com.bamutian.util.ApplicationData;
import com.bamutian.util.ExitDialog;
import com.xijiebamutian.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class Temp extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.temp);
	}
	
	@Override
	protected void onResume() {
		ConvertActivityGroup.UpdateToolbar();
		super.onResume();
	}
	
	@Override
	public void onBackPressed() {
		if (ApplicationData.originalStack.size() == 1) {
			ExitDialog hintDialog = new ExitDialog(ConvertActivityGroup.group);
			hintDialog.show();
		} else {
			ConvertActivityGroup.ActivityBack();
		}
	}
}

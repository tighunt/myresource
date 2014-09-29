package com.bamutian.navigation;

import com.xijiebamutian.R;
import android.app.TabActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

/**
 * This class mainly initializes some UI widgets in the open menu and add
 * listeners on these widgets if necessary.At the same time, it works as the
 * parent class of StartActivity and provides some variables which maybe used in
 * its child class. To define the available functions of the open menu as you
 * like.
 * 
 * @author XijieChen
 */
public class OpenMenuHelper extends TabActivity {

	protected View menuCommonused, menuSetting, menuTool;
	private LayoutInflater mInflater;
	protected PopupWindow popup;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mInflater = getLayoutInflater();
		menuCommonused = mInflater.inflate(R.layout.openmenugrid_commonused,
				null);
		menuSetting = mInflater.inflate(R.layout.openmenugrid_setting, null);
		menuTool = mInflater.inflate(R.layout.openmenugrid_tool, null);

	}

	public void Menu_commonused_btn1(View view) {
		popup.dismiss();
	}

	public void Menu_commonused_btn2(View view) {
		popup.dismiss();
	}

	public void Menu_commonused_btn3(View view) {
		popup.dismiss();
	}

	public void Menu_commonused_btn4(View view) {
		popup.dismiss();
		System.exit(0);
	}

	public void Menu_setting_btn1(View view) {
		popup.dismiss();
	}

	public void Menu_setting_btn2(View view) {
		popup.dismiss();
	}

	public void Menu_setting_btn3(View view) {
		popup.dismiss();
	}

	public void Menu_setting_btn4(View view) {
		popup.dismiss();
	}

	public void Menu_tool_btn1(View view) {
		popup.dismiss();
	}

	public void Menu_tool_btn2(View view) {
		popup.dismiss();
	}

	public void Menu_tool_btn3(View view) {
		popup.dismiss();
	}

	public void Menu_tool_btn4(View view) {
		popup.dismiss();
	}
}

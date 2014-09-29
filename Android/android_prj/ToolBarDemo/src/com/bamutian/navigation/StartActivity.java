package com.bamutian.navigation;

import java.util.Stack;

import com.bamutian.adapter.MenuTitleAdapter;
import com.bamutian.util.ApplicationData;
import com.xijiebamutian.R;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TabHost.TabSpec;

/**
 * This activity is a frame with a framelayout which will exchange activities by
 * CovertActivityGroup and the toolbar on the bottom of this Activity.
 * 
 * @author XijieChen
 */
public class StartActivity extends OpenMenuHelper {

	private int titleIndex;
	private ViewFlipper mViewFlipper;
	private LinearLayout mLayout;
	private TextView title1, title2, title3;
	private GridView mTitleGridView;

	public static StartActivity start;

	
	Resources res;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_start);
		
		ApplicationData.originalStack = new Stack<String>();
		ApplicationData.forwardStack = new Stack<String>();

		TabHost mTabHost = this.getTabHost();

		start = this;
		res = getResources();
		Intent intent = new Intent();
		intent.setClass(this, ConvertActivityGroup.class);
		TabSpec spec = mTabHost.newTabSpec("").setContent(intent).setIndicator(
				"");
		// Set the ActivityGroup into a tabspec.
		mTabHost.addTab(spec);// Add a tab into the tabHost
		mTabHost.setCurrentTab(0);

		initPopupMenu(); 
	}

	/**
	 * This method will be called when users click the Home button on the bottom
	 * of the StartActivity.
	 */
	public void Toolbar_HomeBtn(View temp) {
		Intent intent = new Intent();
		intent.setClass(ConvertActivityGroup.group.getCurrentActivity(),
				Temp.class);
		ConvertActivityGroup.ActivityConvert(intent, "Temp");
	}

	/**
	 * This method will be called when users click the Back button on the bottom
	 * of the StartActivity.
	 */
	public void Toolbar_BackBtn(View temp) {
		ConvertActivityGroup.ActivityBack();
	}

	/**
	 * This method will be called when users click the Menu button on the bottom
	 * of the StartActivity.
	 */
	public void Toolbar_MenuBtn(View temp) {
		OpenMenu();
	}

	/**
	 * This method will be called when users click the Forward button on the
	 * bottom of the StartActivity.
	 */
	public void Toolbar_ForwardBtn(View temp) {
		ConvertActivityGroup.ActivityForward();
	}

	/**
	 * This method will be called when users click the Personal Center button on
	 * the bottom of the StartActivity.
	 */
	public void Toolbar_PersonalCenterBtn(View temp) {
		Intent intent = new Intent();
		intent.setClass(ConvertActivityGroup.group.getCurrentActivity(),
				Temp2.class);
		ConvertActivityGroup.ActivityConvert(intent, "Temp2");
	}

	private void initPopupMenu() {
		// 创建动画
		mViewFlipper = new ViewFlipper(this);
		mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.menu_in));
		mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.menu_out));
		mLayout = new LinearLayout(StartActivity.this);
		mLayout.setOrientation(LinearLayout.VERTICAL);
		// 标题选项栏
		mTitleGridView = new GridView(StartActivity.this);
		mTitleGridView.setLayoutParams(new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		mTitleGridView.setSelector(R.color.alpha_00);
		mTitleGridView.setNumColumns(3);
		mTitleGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		mTitleGridView.setVerticalSpacing(1);
		mTitleGridView.setHorizontalSpacing(1);
		mTitleGridView.setGravity(Gravity.CENTER);
		MenuTitleAdapter mta = new MenuTitleAdapter(this, res
				.getStringArray(R.array.TOOLBAR_MENUTITLE), 16, 0xFFFFFFFF);
		mTitleGridView.setAdapter(mta);
		mTitleGridView.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				onChangeItem(arg1, arg2);
			}

			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		mTitleGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				onChangeItem(arg1, arg2);
			}
		});

		mLayout.addView(mTitleGridView);
		mLayout.addView(menuCommonused);
		mViewFlipper.addView(mLayout);
		mViewFlipper.setFlipInterval(120000);
		// 创建Popup
		popup = new PopupWindow(mViewFlipper, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		popup.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.popupmenu_background));// 设置menu菜单背景
		popup.setFocusable(true);// menu菜单获得焦点 如果没有获得焦点menu菜单中的控件事件无法响应
		popup.update();
		// 设置默认项
		title1 = (TextView) mTitleGridView.getItemAtPosition(0);
		title1.setBackgroundColor(0x00);
	}

	private void onChangeItem(View arg1, int arg2) {
		titleIndex = arg2;
		switch (titleIndex) {
		case 0:
			title1 = (TextView) arg1;
			title1.setBackgroundColor(0x00);
			if (title2 != null)
				title2.setBackgroundResource(R.drawable.toolbar_menu_release);
			if (title3 != null)
				title3.setBackgroundResource(R.drawable.toolbar_menu_release);
			mLayout.removeViewAt(1);
			mLayout.addView(menuCommonused);
			break;
		case 1:
			title2 = (TextView) arg1;
			title2.setBackgroundColor(0x00);
			if (title1 != null)
				title1.setBackgroundResource(R.drawable.toolbar_menu_release);
			if (title3 != null)
				title3.setBackgroundResource(R.drawable.toolbar_menu_release);
			mLayout.removeViewAt(1);
			mLayout.addView(menuSetting);
			break;
		case 2:
			title3 = (TextView) arg1;
			title3.setBackgroundColor(0x00);
			if (title2 != null)
				title2.setBackgroundResource(R.drawable.toolbar_menu_release);
			if (title1 != null)
				title1.setBackgroundResource(R.drawable.toolbar_menu_release);
			mLayout.removeViewAt(1);
			mLayout.addView(menuTool);
			break;
		}
	}

	// 菜单被显示之前的事件
	public boolean onPrepareOptionsMenu(Menu menu) {
		OpenMenu();
		return true;
	}

	public void OpenMenu() {
		if (popup != null) {
			if (popup.isShowing())
				popup.dismiss();
			else {
				ConvertActivityGroup.UpdateOpenMenu(StartActivity.this,
						menuCommonused, menuSetting, menuTool);
				popup.showAtLocation(findViewById(R.id.toolbar_linearlayout),
						Gravity.BOTTOM, 0, 60);
				mViewFlipper.startFlipping();// 播放动画
			}
		}
	}
}

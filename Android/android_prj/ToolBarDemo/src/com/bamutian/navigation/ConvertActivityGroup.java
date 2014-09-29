package com.bamutian.navigation;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.bamutian.util.ApplicationData;
import com.xijiebamutian.R;

/**
 * This class is used to remove the layout and start another activity by intent.
 * What's more, provides some methods to change the usable state of the toolbar.
 * 
 * @author XijieChen
 */
public class ConvertActivityGroup extends ActivityGroup {

	public static LinearLayout container = null;
	public static ActivityGroup group;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		group = this;

		setContentView(R.layout.activity_group);
		container = (LinearLayout) findViewById(R.id.container);

		Intent intent = new Intent(this, Temp.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		if (ApplicationData.forwardStack.size() != 0)
			ApplicationData.forwardStack.clear();
		ApplicationData.originalStack.push("Temp");
		// Convert an activity to a view
		Window w = group.getLocalActivityManager().startActivity(
				"Temp", intent);
		View view = w.getDecorView();
		container.addView(view);
		LinearLayout.LayoutParams params = (LayoutParams) view
				.getLayoutParams();
		params.width = LayoutParams.FILL_PARENT;
		params.height = LayoutParams.FILL_PARENT;
		view.setLayoutParams(params);
		ConvertActivityGroup.UpdateToolbar();
	}

	/**
	 * When users click the back button on the bottom toolbar, push the current
	 * Activity to forwardStack and pop the previous Activity from the
	 * originalStack to show in the screen.Finally, push the current Activity to
	 * originalStack to make sure that it always on the top of the stack.
	 */
	public static void ActivityBack() {
		if (ApplicationData.originalStack.size() > 1) {
			String activityString = ApplicationData.originalStack.pop();
			ApplicationData.forwardStack.push(activityString);
			activityString = ApplicationData.originalStack.pop();
			ApplicationData.originalStack.push(activityString);
			Activity tempActivity = ConvertActivityGroup.group
					.getLocalActivityManager().getActivity(activityString);
			Intent intent = new Intent();
			intent.setClass(ConvertActivityGroup.group.getCurrentActivity(),
					tempActivity.getClass());
			ConvertActivityGroup.container.removeAllViews();
			Window w = ConvertActivityGroup.group.getLocalActivityManager()
					.startActivity(activityString, intent);
			View view = w.getDecorView();
			ConvertActivityGroup.container.addView(view);
		}
	}

	/**
	 * When users click the forward button on the bottom toolbar, pop the
	 * forward Activity from the forwardStack and push it to the originalStack.
	 * At the same time, show it to the screen.
	 */
	public static void ActivityForward() {
		if (ApplicationData.forwardStack.size() > 0) {
			String activityString = ApplicationData.forwardStack.pop();
			ApplicationData.originalStack.push(activityString);
			Activity tempActivity = ConvertActivityGroup.group
					.getLocalActivityManager().getActivity(activityString);
			Intent intent = new Intent();
			intent.setClass(ConvertActivityGroup.group.getCurrentActivity(),
					tempActivity.getClass());
			ConvertActivityGroup.container.removeAllViews();
			Window w = ConvertActivityGroup.group.getLocalActivityManager()
					.startActivity(activityString, intent);
			View view = w.getDecorView();
			ConvertActivityGroup.container.addView(view);
		}
	}

	/**
	 * Whenever enter a certain activity, update the bottom toolbar. Called this
	 * method when you start a new activity or restart a previous activity in
	 * order to change the usable state of imagebuttons.
	 */
	public static void UpdateToolbar() {
		ImageButton homeButton = (ImageButton) StartActivity.start
				.findViewById(R.id.Toolbar_HomeBtn);
		ImageButton backButton = (ImageButton) StartActivity.start
				.findViewById(R.id.Toolbar_BackBtn);
		ImageButton forwardButton = (ImageButton) StartActivity.start
				.findViewById(R.id.Toolbar_ForwardBtn);
		ImageButton personalButton = (ImageButton) StartActivity.start
				.findViewById(R.id.Toolbar_PersonalCenterBtn);
		if (ApplicationData.originalStack.size() <= 1) {
			backButton.setImageResource(R.drawable.controlbar_backward_disable);
			backButton.setEnabled(false);
		} else {
			backButton.setImageResource(R.drawable.controlbar_backward_enable);
			backButton.setEnabled(true);
		}
		if (ApplicationData.forwardStack.size() == 0) {
			forwardButton
					.setImageResource(R.drawable.controlbar_forward_disable);
			forwardButton.setEnabled(false);
		} else {
			forwardButton
					.setImageResource(R.drawable.controlbar_forward_enable);
			forwardButton.setEnabled(true);
		}
		if (ApplicationData.originalStack.size() != 0) {
			String activityString = ApplicationData.originalStack.pop();
			ApplicationData.originalStack.push(activityString);
			if (activityString.equals("Temp")) {
				homeButton
						.setImageResource(R.drawable.controlbar_homepage_disable);
				homeButton.setEnabled(false);
			} else {
				homeButton
						.setImageResource(R.drawable.controlbar_homepage_enable);
				homeButton.setEnabled(true);
			}
			if (activityString.equals("Temp2")) {
				personalButton
						.setImageResource(R.drawable.controlbar_personalcenter_disable);
				personalButton.setEnabled(false);
			} else {
				personalButton
						.setImageResource(R.drawable.controlbar_personalcenter_enable);
				personalButton.setEnabled(true);
			}
		}
	}

	/**
	 * Whenever press the menu button to show the open menu, call this method to
	 * update the usable status of the menu depends on the current Activity.
	 * 
	 * @param menuCommonused
	 * @param menuSetting
	 * @param menuTool
	 */
	public static void UpdateOpenMenu(Context mContext, View menuCommonused,
			View menuSetting, View menuTool) {
		ImageButton menu_commonused_image2 = (ImageButton) menuCommonused
				.findViewById(R.id.menu_commonused_image2);
		TextView menu_commonused_title2 = (TextView) menuCommonused
				.findViewById(R.id.menu_commonused_title2);
		Resources res = mContext.getResources();
		String activityString = ApplicationData.originalStack.pop();
		ApplicationData.originalStack.push(activityString);
		if (activityString.equals("Temp2")) {
			menu_commonused_image2.setEnabled(true);
			menu_commonused_image2.setImageResource(R.drawable.menu_bookmark);
			menu_commonused_title2.setTextColor(mContext.getResources()
					.getColor(R.color.white));
			menu_commonused_title2.setText(res.getText(R.string.commonused2));
		} else {
			menu_commonused_image2.setEnabled(false);
			menu_commonused_image2
					.setImageResource(R.drawable.menu_bookmark_disable);
			menu_commonused_title2.setTextColor(mContext.getResources()
					.getColor(R.color.darkgray));
		}
	}

	/**
	 * When you want to convert activities from ConvertGroup, call this method
	 * and deliver the two parameters in need.
	 * 
	 * @param intent
	 *            define the target Activity which you want to jump to.
	 * @param tag
	 *            the name of the target Activity which is a flag in the
	 *            originalStack.
	 */
	public static void ActivityConvert(Intent intent, String tag) {
		ConvertActivityGroup.container.removeAllViews();
		if (ApplicationData.forwardStack.size() != 0)
			ApplicationData.forwardStack.clear();
		ApplicationData.originalStack.add(tag);
		Window w = ConvertActivityGroup.group.getLocalActivityManager()
				.startActivity(tag, intent);
		View view = w.getDecorView();
		ConvertActivityGroup.container.addView(view);
	}

	@Override
	public void onBackPressed() {
		System.out.println("GroupµÄOnBack");
		group.getLocalActivityManager().getCurrentActivity().onBackPressed();
	}
}

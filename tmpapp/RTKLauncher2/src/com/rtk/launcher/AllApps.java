package com.rtk.launcher;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.TvManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AllApps extends Activity {
	private ArrayList<ApplicationInfo> mApplications;
	private GridView mGrid;
	private TextView txt_time;
	private TextView txt_am;
	private TextView txt_date;
	private TextView txt_week;
	private final int SETTIME = 2000;
	private final int RESETAPP = 2001;
	private Timer timer;
	private Context mContext;
	ApplicationsAdapter adp;
	private final BroadcastReceiver mApplicationsReceiver = new ApplicationsIntentReceiver();
	private TvManager mTv; 
	IntentFilter filter;
	/*
	 * String[] pkgs={"com.rtk.launcher", "com.realtek.rtkatv",
	 * "com.android.seting", "com.realsil.market", "com.rtk.photoplayback",
	 * "com.rtk.netplayer", "com.rtk.videoplayer"};
	 */

	String[] pkgs = { "com.rtk.launcher" };
	private boolean is4k2k = false;
	HomeApplication  application = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (HomeApplication) getApplication();
		mContext = this;
		is4k2k = application.isIs4k2k();
			if (application.getDensityDpi() == 160)
				setContentView(R.layout.activity_apps_low);
			else
				setContentView(R.layout.activity_apps);
		txt_time = (TextView) findViewById(R.id.txt_time2);
		txt_am = (TextView) findViewById(R.id.txt_am2);
		txt_date = (TextView) findViewById(R.id.txt_date2);
		txt_week = (TextView) findViewById(R.id.txt_week2);
		if (application.isAppchanged() || mApplications == null
				|| mApplications.size() == 0) {
			application.setAppchanged(false);
			loadApplications();
		}

		if (mGrid == null) {
			mGrid = (GridView) findViewById(R.id.all_myapps);
		}
		if (mApplications == null || mApplications.size() == 0)
			return;
		adp = new ApplicationsAdapter(this, mApplications);
		mGrid.setAdapter(adp);
		mGrid.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// ImageAdapter zonesImageAdapter = (ImageAdapter)
				// zonesView.getAdapter();
				adp.notifyDataSetChanged(arg2);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		if (mApplications.size() > 0) {
			mGrid.setSelection(0);
			adp.notifyDataSetChanged(0);
		}
		mGrid.setOnItemClickListener(new ApplicationLauncher());
		timer = new Timer(true);
		registerIntentReceivers();
		// mGrid.getChildAt(0).requestFocus();
	}

	/**
	 * GridView adapter to show the list of all installed applications.
	 */
	public class ApplicationsAdapter extends ArrayAdapter<ApplicationInfo> {
		private ArrayList<ApplicationInfo> theApplications;

		public ApplicationsAdapter(Context context,
				ArrayList<ApplicationInfo> apps) {
			super(context, 0, apps);
			theApplications = apps;
		}

		private int selected = -1;

		public void notifyDataSetChanged(int id) {
			selected = id;
			super.notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ApplicationInfo info = theApplications.get(position);

			boolean isSelected = false;
			// AppsViewItemViewInfo itemViewInfo = null;
			MyView tag;
			if (selected == position) {
				// the special one.Scale Large
				isSelected = true;
			}
			if (null == convertView) {
				final LayoutInflater inflater = getLayoutInflater();
				tag = new MyView();
					if (application.getDensityDpi() == 160)
						convertView = inflater.inflate(R.layout.appsviewcell_low, null);
					else
						convertView = inflater.inflate(R.layout.appsviewcell, null);
				tag.imageView = (ImageView) convertView
						.findViewById(R.id.appsviewcell_image);
				tag.imageViewBg = (LinearLayout) convertView
						.findViewById(R.id.appsviewcell_bg);
				tag.textView = (TextView) convertView
						.findViewById(R.id.appsviewcell_text);
				convertView.setTag(tag);
			} else {
				tag = (MyView) convertView.getTag();
			}

			tag.imageView.setImageDrawable(info.icon);
			tag.textView.setText(info.title);
			if(is4k2k)
			{
			if (isSelected) {
				tag.imageViewBg.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.apps_foucs_x));
			} else {
				tag.imageViewBg.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.apps_unfoucs_x));
			}
			}else
			{
				if (isSelected) {
					tag.imageViewBg.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.apps_foucs));
				} else {
					tag.imageViewBg.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.apps_unfoucs));
				}
			}
			return convertView;
		}
	}

	/**
	 * Starts the selected activity/application in the grid view.
	 */
	private class ApplicationLauncher implements
			AdapterView.OnItemClickListener {
		public void onItemClick(AdapterView parent, View v, int position,
				long id) {
			ApplicationInfo app = (ApplicationInfo) parent
					.getItemAtPosition(position);
			startActivity(app.intent);
		}
	}

	private boolean isIn(String pkg) {
		for (int i = 0; i < 1; i++) {
			if (pkg.equals(pkgs[i]))
				return true;
		}
		return false;
	}

	class MyView {
		ImageView imageView;
		LinearLayout imageViewBg;
		TextView textView;
	}

	public void onResume() {
		super.onResume();
		if (task == null) {
			task = new TimerTask() {
				public void run() {
					Message message = new Message();
					message.what = SETTIME;
					handler.sendMessage(message);
				}
			};
			timer.schedule(task, 0, 1000);
		}

		mTv = (TvManager)getSystemService(Context.TV_SERVICE);
		if (mTv != null) {
			mTv.setMute(true);
		}
	}

	public void onDestory() {
		super.onDestroy();
		unregisterIntentReceivers();
		android.os.Process.killProcess(android.os.Process.myPid()); 
	}
	
	public void onStop() {
		super.onStop();
		task.cancel();
		task = null;
		if (mTv != null) {
			mTv.setMute(false);
			mTv = null;
		}
	}

	TimerTask task = null;

	String getWeekStr(int week) {
		switch (week) {
		case Calendar.SUNDAY:
			return (this.getResources().getString(R.string.sun));
		case Calendar.MONDAY:
			return (this.getResources().getString(R.string.mon));
		case Calendar.TUESDAY:
			return (this.getResources().getString(R.string.tue));
		case Calendar.WEDNESDAY:
			return (this.getResources().getString(R.string.wed));
		case Calendar.THURSDAY:
			return (this.getResources().getString(R.string.thu));
		case Calendar.FRIDAY:
			return (this.getResources().getString(R.string.fri));
		case Calendar.SATURDAY:
			return (this.getResources().getString(R.string.sat));
		}
		return "";
	}

	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			// TODO Auto-generated method stub
			switch (msg.what) {
			case SETTIME:

				Calendar calendar = Calendar.getInstance();
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH);
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				int hour = calendar.get(Calendar.HOUR);
				int minute = calendar.get(Calendar.MINUTE);
				int week = calendar.get(Calendar.DAY_OF_WEEK);
				String str_am = ((calendar.get(Calendar.AM_PM) == Calendar.PM) ? "pm"
						: "am");
				if (hour == 0) {
					if (str_am.equals("pm")) {
						hour = 12;
					}
				}
				txt_time.setText(String.format("%02d:%02d", hour, minute));
				txt_am.setText(str_am);
				txt_date.setText(String
						.format("%d/%02d/%02d", year, month+1, day));
				txt_week.setText(getWeekStr(week));
				break;

			case RESETAPP:
				loadApplications();
				application.setAppchanged(false);
				if (mGrid == null) {
					mGrid = (GridView) findViewById(R.id.all_myapps);
				}
				if (mApplications == null || mApplications.size() == 0)
					return;
				int position  = mGrid.getSelectedItemPosition();
				/*mGrid.setAdapter(new ApplicationsAdapter(mContext,
						mApplications));*/
				adp.notifyDataSetChanged();
				mGrid.requestFocus();
				if(mApplications.size()<=position)
				{
					if(mApplications.size()>0)
					{
						mGrid.setSelection(0);
						adp.notifyDataSetChanged(0);
					}
				}
				else
				{
					mGrid.setSelection(position);
					adp.notifyDataSetChanged(position);
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	public void loadApplications() {
		PackageManager manager = getPackageManager();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		final List<ResolveInfo> apps = manager.queryIntentActivities(
				mainIntent, 0);

		if (apps != null) {
			int count = apps.size();

			if (mApplications == null) {
				mApplications = new ArrayList<ApplicationInfo>();
			}
			mApplications.clear();
			for (int i = 0; i < count; i++) {
				ApplicationInfo application = new ApplicationInfo();
				ResolveInfo info = apps.get(i);// info.

				application.title = info.loadLabel(manager);
				application.setActivity(new ComponentName(
						info.activityInfo.applicationInfo.packageName,
						info.activityInfo.name), Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				application.icon = info.activityInfo.loadIcon(manager);
				if (!isIn(application.intent.getComponent().getPackageName()))
					mApplications.add(application);
			}
		}
	}

	private class ApplicationsIntentReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// loadApplications(false);
			application.setAppchanged(true);
			handler.sendEmptyMessage(RESETAPP);
		}
	}

	/**
	 * Registers various intent receivers. The current implementation registers
	 * only a wallpaper intent receiver to let other applications change the
	 * wallpaper.
	 */
	private void registerIntentReceivers() {
		filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
		filter.addDataScheme("package");
		registerReceiver(mApplicationsReceiver, filter);
	}
	private void unregisterIntentReceivers() {
		unregisterReceiver(mApplicationsReceiver);
	}
}

package com.android.settings.datetime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.xmlpull.v1.XmlPullParserException;

import android.app.AlarmManager;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.graphics.Color;

import com.android.settings.DateTimeSettings;
import com.android.settings.R;

/**
 * 自定义Dialog,用于对时区的选择
 * 
 * @author 杜聪甲(ducj@biaoqi.com.cn)
 * @date 2011-11-9 下午05:11:10
 * @since 1.0
 */
public class ZoneListSelectDialog extends Dialog {

	private static final String TAG = "ZoneList";

	private static final String KEY_ID = "id";

	private static final String KEY_DISPLAYNAME = "name";

	private static final String KEY_GMT = "gmt";

	private static final String KEY_OFFSET = "offset";

	private static final String XMLTAG_TIMEZONE = "timezone";

	private static final int HOURS_1 = 60 * 60000;

	private static final int UPDATE_TIMEZONE = 1;

	// 每页显示的个数
	private static final int COUNT_SIZE = 5;

	private DateTimeSettings mDateTimeSettingActivity;

	private SimpleAdapter mTimezoneAdapter;

	private Handler mUpdateHandler;

	// 时区选中列表
	private ListView mZoneList;

	// 总的页数
	private TextView mTotalPage;

	// 当前的页数
	private TextView mCurrentPage;

	// 总的页数
	private int mTotalPageCount;

	// 当前的页数
	private int mCurrentPageCount;

	// 记录当前选中的项
	private int mDefault = 0;

	// 总数量
	private int mTotalCount = 0;

	public ZoneListSelectDialog(DateTimeSettings dateTimeSettingActivity, Handler mHandler) {
		super(dateTimeSettingActivity);
		this.mDateTimeSettingActivity = dateTimeSettingActivity;
		this.mUpdateHandler = mHandler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.time_zone_list);

		// 实例化新的窗口
		Window w = getWindow();

		// 获取默认显示数据
		Display display = w.getWindowManager().getDefaultDisplay();

		// 获取窗口的背景图片
		Resources resources = mDateTimeSettingActivity.getResources();
		Drawable drawable = resources.getDrawable(R.drawable.dialog_bg);
		// // 设置窗口的背景图片
		w.setBackgroundDrawable(drawable);

		// 窗口的标题为空
		w.setTitle("                                                   "
				+ mDateTimeSettingActivity.getResources().getString(R.string.timezone_select));
		w.setTitleColor(Color.argb(204, 204, 204, 204));

		// 定义窗口的宽和高
		int width = (int) (display.getWidth() * 0.5);
		int height = (int) (display.getHeight() * 0.6);// [2011-1-13 yanhd] change widows width

		// 设置窗口的大小
		w.setLayout(width, height);

		// 设置窗口的显示位置
		w.setGravity(Gravity.CENTER);

		// 设置窗口的属性
		WindowManager.LayoutParams wl = w.getAttributes();
		w.setAttributes(wl);

		findViews();

		registerListeners();
	}

	private void findViews() {
		mTotalPage = (TextView) findViewById(R.id.page_total_count);
		mCurrentPage = (TextView) findViewById(R.id.page_current_count);
		mZoneList = (ListView) findViewById(R.id.timezone_select);

		mZoneList.setDividerHeight(0);
		mZoneList.setVerticalScrollBarEnabled(false);

		String[] from = new String[] { KEY_DISPLAYNAME, KEY_GMT };
		int[] to = new int[] { R.id.aboutItem, R.id.content };

		List<HashMap> timezoneSortedList = getZones();

		System.out.println("mDefault  " + mDefault);

		mTotalCount = timezoneSortedList.size();

		mCurrentPageCount = mDefault / COUNT_SIZE + 1;
		mTotalPageCount = timezoneSortedList.size() / COUNT_SIZE + 1;

		mTimezoneAdapter = new SimpleAdapter(mDateTimeSettingActivity, (List) timezoneSortedList,
				R.layout.about_item_text, from, to);

		mZoneList.setAdapter(mTimezoneAdapter);

		mCurrentPage.setText("" + mCurrentPageCount);
		mTotalPage.setText("/" + mTotalPageCount);

		mZoneList.setSelectionFromTop(mDefault, 2);
	}

	private void registerListeners() {

		mZoneList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Map map = (Map) mZoneList.getItemAtPosition(position);
				// 更新系统时区的数值
				AlarmManager alarm = (AlarmManager) mDateTimeSettingActivity.getSystemService(Context.ALARM_SERVICE);
				alarm.setTimeZone((String) map.get(KEY_ID));
				mUpdateHandler.sendEmptyMessage(UPDATE_TIMEZONE);
				dismiss();
			}
		});

		mZoneList.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mDefault = position;
				mCurrentPageCount = mDefault / COUNT_SIZE + 1;
				mCurrentPage.setText("" + mCurrentPageCount);
				System.out.println("onItemSelected mDefault" + position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			int restCount = mTotalCount - mDefault;
			if (COUNT_SIZE < restCount) {
				mZoneList.setSelection(mDefault + COUNT_SIZE);
			} else {
				mZoneList.setSelection(mDefault + restCount);

			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			int restCount = mTotalCount - mDefault;
			if (COUNT_SIZE < restCount) {
				mZoneList.setSelection(mDefault - COUNT_SIZE);
			} else {
				mZoneList.setSelection(mDefault - restCount);
			}
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			dismiss();
		}
		return super.onKeyUp(keyCode, event);
	}

	/**
	 * 获取XML中的所有数据,解析出所有的时区
	 * 
	 * @return
	 */
	private List<HashMap> getZones() {
		List<HashMap> myData = new ArrayList<HashMap>();
		long date = Calendar.getInstance().getTimeInMillis();
		try {
			XmlResourceParser xrp = mDateTimeSettingActivity.getResources().getXml(R.xml.timezones);
			while (xrp.next() != XmlResourceParser.START_TAG)
				continue;
			xrp.next();
			while (xrp.getEventType() != XmlResourceParser.END_TAG) {
				while (xrp.getEventType() != XmlResourceParser.START_TAG) {
					if (xrp.getEventType() == XmlResourceParser.END_DOCUMENT) {
						return myData;
					}
					xrp.next();
				}
				if (xrp.getName().equals(XMLTAG_TIMEZONE)) {
					String id = xrp.getAttributeValue(0);
					String displayName = xrp.nextText();
					addItem(myData, id, displayName, date);
				}
				while (xrp.getEventType() != XmlResourceParser.END_TAG) {
					xrp.next();
				}
				xrp.next();
			}
			xrp.close();
		} catch (XmlPullParserException xppe) {
			Log.e(TAG, "Ill-formatted timezones.xml file");
		} catch (java.io.IOException ioe) {
			Log.e(TAG, "Unable to read timezones.xml file");
		}

		return myData;
	}

	/**
	 * 增加从XML解析出的时区
	 * 
	 * @param myData
	 * @param id
	 * @param displayName
	 * @param date
	 */
	protected void addItem(List<HashMap> myData, String id, String displayName, long date) {
		HashMap map = new HashMap();
		map.put(KEY_ID, id);
		map.put(KEY_DISPLAYNAME, displayName);
		TimeZone tz = TimeZone.getTimeZone(id);
		int offset = tz.getOffset(date);
		int p = Math.abs(offset);
		StringBuilder name = new StringBuilder();
		name.append("GMT");

		if (offset < 0) {
			name.append('-');
		} else {
			name.append('+');
		}

		name.append(p / (HOURS_1));
		name.append(':');

		int min = p / 60000;
		min %= 60;

		if (min < 10) {
			name.append('0');
		}
		name.append(min);

		map.put(KEY_GMT, name.toString());
		map.put(KEY_OFFSET, offset);

		if (id.equals(TimeZone.getDefault().getID())) {
			mDefault = myData.size();
		}

		myData.add(map);
	}
}

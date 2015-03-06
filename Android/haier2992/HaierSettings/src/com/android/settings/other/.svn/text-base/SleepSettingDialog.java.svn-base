package com.android.settings.other;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.app.TvManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.TvManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.android.settings.R;
/*import com.mstar.tv.service.aidl.Constants;
import com.mstar.tv.service.aidl.EN_SLEEP_TIME_STATE;
import com.mstar.tv.service.skin.TimerSkin;*/
import com.android.settings.other.EN_SLEEP_TIME_STATE;
import com.android.settings.other.TimerSkin;
/**
 * 自定义Dialog用于睡眠模式的选择
 * 
 * @author 曹美娟
 * @date 2012-1-5 下午03:30:25
 * @since 1.0
 */
public class SleepSettingDialog extends Dialog {
	private TimerSkin timerSkin;
	// 用于睡眠时间模式显示的LIstView
	private ListView mSleepmodeListView;
	// 睡眠时间目前选择项
	private int balanceIndex = 0;

	private List<Map<String, Object>> sleepMode = new ArrayList<Map<String, Object>>();

	private SimpleAdapter sleepAdapter;

	private String[] locales = { "mode1", "mode2", "mode3", "mode4", "mode5", "mode6", "mode7", "mode8", "mode9",
			"mode10", "mode11" };
	// 用于标记当前的选中项
	private TvManager mTvManager;
	private int select;
	private OtherSettings mOtherSetting;
	private SharedPreferences sf;
	private String[] items; // 睡眠时间模式数组:关、5分钟、10分钟、15分钟、30分钟、45分钟、60分钟、90分钟、120分钟、180分钟、240分钟
	private int sleeptimerMode = SLEEP_OFF;
	private Handler handler = null;
	private static final int SLEEP_OFF = 0; // 睡眠时间为关
	private static final int SLEEP_5MIN = 1; // 睡眠时间为5分钟
	private static final int SLEEP_10MIN = 2; // 睡眠时间为10分钟
	private static final int SLEEP_15MIN = 3; // 睡眠时间为15分钟
	private static final int SLEEP_30MIN = 4; // 睡眠时间为30分钟
	private static final int SLEEP_45MIN = 5; // 睡眠时间为45分钟
	private static final int SLEEP_60MIN = 6; // 睡眠时间为60分钟
	private static final int SLEEP_90MIN = 7; // 睡眠时间为90分钟
	private static final int SLEEP_120MIN = 8; // 睡眠时间为120分钟
	private static final int SLEEP_180MIN = 9; // 睡眠时间为180分钟
	private static final int SLEEP_240MIN = 10; // 睡眠时间为240分钟
	private static final String SLEEPTIMER = "sleeptimer"; // 睡眠时间preferences
	private static final String SLEEPTIMER_MODE = "sleeptimer_mode"; // 睡眠时间模式

	public SleepSettingDialog(OtherSettings otherSettings,Handler mHandler) {
		super(otherSettings);
		this.mOtherSetting = otherSettings;
		handler = mHandler;
		
	}

	// 2-18 cbl
	/*protected Handler handler = new Handler() {
		public void handleMessage(Message msg) {//20130511 modify by cw
			super.handleMessage(msg);
			if (msg.what == Constants.CONNECTION_OK) {
				Bundle bundle = msg.getData();
				int index = bundle.getInt("Index");
				switch (index) {
				}
			}
		
		    Log.d("***********Leewokan********", "----------------Handler----------");
		    super.handleMessage(msg); 
		    if (msg.what == SLEEP_OFF) 
            {
		        
            }
            else if(msg.what == SLEEP_5MIN)
            {
                Log.d("***********Leewokan********", "----------------SLEEP_5MIN----------");
                downTimer.schedule(goToPowerOff(), 5* 60 * 1000);
            }  
            else if(msg.what == SLEEP_10MIN)
            {
                downTimer.schedule(goToPowerOff(), 10* 60 * 1000);
            }
            else if(msg.what == SLEEP_15MIN)
            {
                downTimer.schedule(goToPowerOff(), 15* 60 * 1000);
            }
            else if(msg.what == SLEEP_30MIN)
            {
                downTimer.schedule(goToPowerOff(), 30* 60 * 1000);
            }
            else if(msg.what == SLEEP_45MIN)
            {
                downTimer.schedule(goToPowerOff(), 45* 60 * 1000);
            }
            else if(msg.what == SLEEP_60MIN)
            {
                downTimer.schedule(goToPowerOff(), 60* 60 * 1000);
            }
            else if(msg.what == SLEEP_90MIN)
            {
                downTimer.schedule(goToPowerOff(), 90* 60 * 1000);
            }
            else if(msg.what == SLEEP_120MIN)
            {
                downTimer.schedule(goToPowerOff(), 120* 60 * 1000);
            }
            else if(msg.what == SLEEP_180MIN)
            {
                downTimer.schedule(goToPowerOff(), 180* 60 * 1000);
            }
            else if(msg.what == SLEEP_240MIN)
            {
                downTimer.schedule(goToPowerOff(), 240* 60 * 1000);
            }
		};
	};*/
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sleep_mode);
		Window w = getWindow();
		Display display = w.getWindowManager().getDefaultDisplay();
		WindowManager.LayoutParams layoutParams = w.getAttributes();
		w.setBackgroundDrawableResource(R.drawable.set_bg);
		layoutParams.width = display.getWidth() * 3 / 10;
		layoutParams.height = (int) (display.getHeight() * 0.45);
		w.setAttributes(layoutParams);
		w.setTitle(null);
		w.setGravity(Gravity.CENTER);
		/*timerSkin = new TimerSkin(mOtherSetting);
		timerSkin.connect(handler);// 2-18 cbl
*/			
		mTvManager = (TvManager) mOtherSetting.getSystemService("tv");
		findViews();
		registerListeners();
	}

	/**
	 * init compontent
	 */
	private void findViews() {
		mSleepmodeListView = (ListView) findViewById(R.id.sleep_mode_list);
		mSleepmodeListView.setDivider(null);
		CountDownTimerActivity.SetSleepSettingDialog(this);
		items = mOtherSetting.getResources().getStringArray(R.array.sleep_mode);
	//	sf = mOtherSetting.getSharedPreferences(SLEEPTIMER, Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
	//	int eyeCurrentMode = sf.getInt(SLEEPTIMER_MODE, 0);
		int eyeCurrentMode = mTvManager.getTimerAutoSleep();
		switch (eyeCurrentMode) {
		case SLEEP_OFF:
			select = 0; // 关
			break;
		case SLEEP_5MIN:
			select = 1; // 5分钟
			break;
		case SLEEP_10MIN:
			select = 2; // 10分钟
			break;
		case SLEEP_15MIN:
			select = 3; // 15分钟
			break;
		case SLEEP_30MIN:
			select = 4; // 30分钟
			break;
		case SLEEP_45MIN:
			select = 5; // 45分钟
			break;
		case SLEEP_60MIN:
			select = 6; // 60分钟
			break;
		case SLEEP_90MIN:
			select = 7; // 90分钟
			break;
		case SLEEP_120MIN:
			select = 8; // 120分钟
			break;
		case SLEEP_180MIN:
			select = 9; // 180分钟
			break;
		case SLEEP_240MIN:
			select = 10; // 240分钟
			break;

		default:
			select = 0;
		}
		balanceIndex = select;
		for (int i = 0; i < items.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("nameItem", items[i]);
			if (select == i) {
				map.put("imgItem", R.drawable.selected);
			} else {
				map.put("imgItem", R.drawable.unselected);
			}
			sleepMode.add(map);
		}

		sleepAdapter = new SimpleAdapter(mOtherSetting, sleepMode, R.layout.date_format_item_list, new String[] {
				"nameItem", "imgItem" }, new int[] { R.id.date_format_item, R.id.date_format_item_iv });

		mSleepmodeListView.setAdapter(sleepAdapter);
		System.out.println("select " + select);
		mSleepmodeListView.setSelectionFromTop(select, 2);
	}

	private void registerListeners() {
		mSleepmodeListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				changeRadioImg(balanceIndex, false);
				changeRadioImg(position, true);
				balanceIndex = position;
				setSleeptimerMode(position);
				dismiss();
			}
		});
	}

	/**
	 * 实时改变单选项
	 * 
	 * @param selectedItem
	 *            当前选中项
	 * @param b
	 *            是否点击此项
	 */
	@SuppressWarnings("unchecked")
	private void changeRadioImg(int selectedItem, boolean b) {
		HashMap<String, Object> map = (HashMap<String, Object>) sleepAdapter.getItem(selectedItem);
		if (b) {
			map.put("imgItem", R.drawable.selected);
		} else {
			map.put("imgItem", R.drawable.unselected);
		}
		sleepAdapter.notifyDataSetChanged();
	}

	/**
	 * 设置睡眠时间的模式：关、5分钟、10分钟、15分钟、30分钟、45分钟、60分钟、90分钟、120分钟、180分钟、240分钟
	 * 
	 * @param position
	 */
	public void setSleeptimerMode(int position) {
		String mode = locales[position];
		
		try {
			if ("mode1".equals(mode) && position == 0) {
				sleeptimerMode = SLEEP_OFF;
				handler.sendEmptyMessage(SLEEP_OFF);
				//timerSkin.setSleepMode(EN_SLEEP_TIME_STATE.STATE_SLEEP_OFF);
			} else if ("mode2".equals(mode)) {
				sleeptimerMode = SLEEP_5MIN;
				handler.sendEmptyMessage(SLEEP_5MIN);
				//timerSkin.setSleepMode(EN_SLEEP_TIME_STATE.STATE_SLEEP_5MIN);
			} else if ("mode3".equals(mode)) {
				sleeptimerMode = SLEEP_10MIN;
				handler.sendEmptyMessage(SLEEP_10MIN);
				//timerSkin.setSleepMode(EN_SLEEP_TIME_STATE.STATE_SLEEP_10MIN);
			} else if ("mode4".equals(mode)) {
				sleeptimerMode = SLEEP_15MIN;
				handler.sendEmptyMessage(SLEEP_15MIN);
				//timerSkin.setSleepMode(EN_SLEEP_TIME_STATE.STATE_SLEEP_15MIN);
			} else if ("mode5".equals(mode)) {
				sleeptimerMode = SLEEP_30MIN;
				handler.sendEmptyMessage(SLEEP_30MIN);
				//timerSkin.setSleepMode(EN_SLEEP_TIME_STATE.STATE_SLEEP_30MIN);
			} else if ("mode6".equals(mode)) {
				sleeptimerMode = SLEEP_45MIN;
				handler.sendEmptyMessage(SLEEP_45MIN);
				//timerSkin.setSleepMode(EN_SLEEP_TIME_STATE.STATE_SLEEP_45MIN);
			} else if ("mode7".equals(mode)) {
				sleeptimerMode = SLEEP_60MIN;
				handler.sendEmptyMessage(SLEEP_60MIN);
				//timerSkin.setSleepMode(EN_SLEEP_TIME_STATE.STATE_SLEEP_60MIN);
			} else if ("mode8".equals(mode)) {
				sleeptimerMode = SLEEP_90MIN;
				handler.sendEmptyMessage(SLEEP_90MIN);
				//timerSkin.setSleepMode(EN_SLEEP_TIME_STATE.STATE_SLEEP_90MIN);
			} else if ("mode9".equals(mode)) {
				sleeptimerMode = SLEEP_120MIN;
				handler.sendEmptyMessage(SLEEP_120MIN);
				//timerSkin.setSleepMode(EN_SLEEP_TIME_STATE.STATE_SLEEP_120MIN);
			} else if ("mode10".equals(mode)) {
				sleeptimerMode = SLEEP_180MIN;
				handler.sendEmptyMessage(SLEEP_180MIN);
				//timerSkin.setSleepMode(EN_SLEEP_TIME_STATE.STATE_SLEEP_180MIN);
			} else if ("mode11".equals(mode)) {
				sleeptimerMode = SLEEP_240MIN;
				handler.sendEmptyMessage(SLEEP_240MIN);
				//timerSkin.setSleepMode(EN_SLEEP_TIME_STATE.STATE_SLEEP_240MIN);
			}

			// 保存睡眠时间的模式
			sf = mOtherSetting.getSharedPreferences(SLEEPTIMER, 0);
			sf.edit().putInt(SLEEPTIMER_MODE, sleeptimerMode).commit();
			mOtherSetting.setSleepTime(sleeptimerMode);
			mOtherSetting.setSleepMode(sleeptimerMode);// 2011-2-20 cbl show
														// result
		} catch (Exception e) {
			// 如果发生异常，睡眠时间模式为关
		   // Log.i("-----Leewokan-------", "*************this is Exception***********");
		//	sf = mOtherSetting.getSharedPreferences(SLEEPTIMER, 0);
		//	sf.edit().putInt(SLEEPTIMER_MODE, SLEEP_OFF).commit();
		    mOtherSetting.setSleepTime(sleeptimerMode);
			mOtherSetting.setSleepMode(sleeptimerMode);// 2011-2-20 cbl show
														// result
			e.printStackTrace();
		}
	}

}

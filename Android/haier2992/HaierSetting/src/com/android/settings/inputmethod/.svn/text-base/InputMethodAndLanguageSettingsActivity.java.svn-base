package com.android.settings.inputmethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.settings.R;

/**
 * set language and input method
 * 
 * @author ducj
 * @date 2011-11-5 下午04:34:10
 * @since 1.0
 */
public class InputMethodAndLanguageSettingsActivity extends Activity {

	private final static int LANGUAGE_SETTING = 0;

	private final static int KEYBOARD_SETTING = 1;

	private ListView mListView;

	private ListView mLanguage_setting_ls;

	// get all input method
	List<InputMethodInfo> mInputMethodProperties;
	// the default input method
	String mLastInputMethodId;

	private List<String> inputMethodID = new ArrayList<String>();

	// private PictureSkin pictureSkin;

	private List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> inputMethodSetting = new ArrayList<Map<String, Object>>();
	List<Map<String, Object>> inputMethodLists = new ArrayList<Map<String, Object>>();
	private SimpleAdapter languageAdapter;
	private SimpleAdapter inputMethodSettingAdapter;
	private int count = 0;

	int select = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.language_input_setting);

		findView();

		registerListener();

		// Intent intent = new Intent();
		// intent.setAction("com.android.settings.languageOrWallpaperSetting.action");
		// sendBroadcast(intent);
		//
		// Log.d("zjf", "【系统设置里】发送设置语言的广播。。。。。。");

	}

	/**
	 * init component
	 */
	private void findView() {

		mListView = (ListView) findViewById(R.id.language_imput_ls);
		mListView.setDivider(null);

		mLanguage_setting_ls = (ListView) findViewById(R.id.language_setting_ls);
		mLanguage_setting_ls.setDivider(null);
		// add the first data
		HashMap<String, Object> map1 = new HashMap<String, Object>();
		map1.put("nameItem", getString(R.string.language_setting));
		map1.put("imgItem", R.drawable.set_drop);
		items.add(map1);

		// add the second data
		HashMap<String, Object> map2 = new HashMap<String, Object>();
		map2.put("nameItem", getString(R.string.keyboard_setting));
		map2.put("imgItem", R.drawable.set_drop);
		items.add(map2);

		languageAdapter = new SimpleAdapter(this, items, R.layout.date_format_item_list, new String[] { "nameItem",
				"imgItem" }, new int[] { R.id.date_format_item, R.id.date_format_item_iv });
		mListView.setAdapter(languageAdapter);

		onCreateIMM();
		inputMethodSettingAdapter = new SimpleAdapter(this, inputMethodSetting, R.layout.date_format_item_list,
				new String[] { "nameItem", "imgItem" }, new int[] { R.id.date_format_item, R.id.date_format_item_iv });
		mLanguage_setting_ls.setAdapter(inputMethodSettingAdapter);
	}

	/**
	 * register listener
	 */
	private void registerListener() {
		// select language and modify the default input method
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case LANGUAGE_SETTING:
					LanguageSelectDialog selectDialog = new LanguageSelectDialog(
							InputMethodAndLanguageSettingsActivity.this);
					selectDialog.show();

					break;
				case KEYBOARD_SETTING:
					KeyboadrSelectDialog keyboadrSelectDialog = new KeyboadrSelectDialog(
							InputMethodAndLanguageSettingsActivity.this);
					keyboadrSelectDialog.show();
					break;
				}
			}
		});

		// modify input methods' setting
		mLanguage_setting_ls.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String activityName = inputMethodID.get(position);
				String packageName = activityName.substring(0, activityName.lastIndexOf("."));

				Log.d("InputMethod", "activityName:" + activityName);
				Log.d("InputMethod", "packageName:" + packageName);

				int slash = activityName.indexOf("/");
				if (slash > 0) {
					packageName = activityName.substring(0, slash);
					activityName = activityName.substring(slash + 1);
				}
				if (activityName.length() > 0) {
					Intent i = new Intent(Intent.ACTION_MAIN);
					i.setClassName(packageName, activityName);
					startActivity(i);
				}
			}
		});
	}
	/**
	 * get the default input method and all input methods
	 */
	private void onCreateIMM() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		mInputMethodProperties = imm.getInputMethodList();

		// the id of default input method
		mLastInputMethodId = Settings.Secure.getString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
		count = (mInputMethodProperties == null ? 0 : mInputMethodProperties.size());
		for (int i = 0; i < count; ++i) {
			InputMethodInfo property = mInputMethodProperties.get(i);
			// the id of input method
			String id = property.getId();
			if (id.equals("com.android.inputmethod.latin/.LatinIME")) {
				continue;
			}
			// the package of input method
			// String packageName = property.getPackageName();
			// the name of input method
			CharSequence label = property.loadLabel(getPackageManager());
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("nameItem", label + " " + getString(R.string.setting));
			map.put("imgItem", R.drawable.set_drop);
			HashMap<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("nameItem", label + getString(R.string.setting));
			boolean isDefault = isDefaultInputMethod(mLastInputMethodId, id);
			if (isDefault) {
				inputMap.put("imgItem", R.drawable.selected);
				select = i;
			} else {
				inputMap.put("imgItem", R.drawable.unselected);
			}

			// If setting activity is available, add a setting.
			if (null != property.getSettingsActivity()) {
				String settingsActivity = property.getSettingsActivity();
				Log.d("InputMethod", "1111111_settingsActivity:" + settingsActivity);

				if (settingsActivity.lastIndexOf("/") < 0) {
					settingsActivity = property.getPackageName() + "/" + settingsActivity;
				}
				
				Log.d("InputMethod", "2222222_settingsActivity:" + settingsActivity);

				if (("com.android.inputmethod.pinyin").equals(property.getPackageName())) {
					continue;
				}
				
				inputMethodID.add(settingsActivity);
			}
			inputMethodLists.add(inputMap);

			// add by cbl 2012-5-15 15:09:30 filter Google Pinyin IME
			if (!property.getPackageName().equals("com.android.inputmethod.pinyin"))
				inputMethodSetting.add(map);
		}

	}

	/**
	 * whether is default input method
	 * 
	 * @return
	 */
	private boolean isDefaultInputMethod(String default_method, String current_method) {

		if (default_method.equals(current_method)) {
			return true;
		} else {
			return false;
		}

	}

}

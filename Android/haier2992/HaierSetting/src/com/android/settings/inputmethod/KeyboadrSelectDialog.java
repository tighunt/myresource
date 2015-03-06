package com.android.settings.inputmethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.settings.R;

/**
 * select input method
 * 
 * @author ducj(ducj@biaoqi.com.cn)
 * @date 2011-12-5 上午09:51:36
 * @since 1.0
 */
public class KeyboadrSelectDialog extends Dialog {

	// show all input method
	private ListView mKeyboardListView;
	// the current using input method
	private int balanceIndex = 0;
	// get all input method
	private List<InputMethodInfo> mInputMethodProperties;
	private List<InputMethodInfo> mInputMethodInfos = new ArrayList<InputMethodInfo>();
	// the default input method
	private String mLastInputMethodId;

	private InputMethodAndLanguageSettingsActivity mLanguageSettingActivity;

	private List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();

	private SimpleAdapter inputMethodAdapter;

	private int select;

	private int count = -1;

	public KeyboadrSelectDialog(InputMethodAndLanguageSettingsActivity languageSettingActivity) {
		super(languageSettingActivity);
		this.mLanguageSettingActivity = languageSettingActivity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.input_setting);

		// 实例化新的窗口
		Window w = getWindow();
		// 获取默认显示数据
		Display display = w.getWindowManager().getDefaultDisplay();
		// 设置窗口的背景图片
		w.setBackgroundDrawableResource(R.drawable.set_bg);

		// 窗口的标题为空
		w.setTitle(null);
		// 定义窗口的宽和高
		int width = (int) (display.getWidth() * 0.3);
		int height = (int) (display.getHeight() * 0.45);

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

	/**
	 * init compontent
	 */
	private void findViews() {

		mKeyboardListView = (ListView) findViewById(R.id.input_select_list);
		mKeyboardListView.setDivider(null);

		onCreateIMM();

		inputMethodAdapter = new SimpleAdapter(mLanguageSettingActivity, items, R.layout.date_format_item_list,
				new String[] { "nameItem", "imgItem" }, new int[] { R.id.date_format_item, R.id.date_format_item_iv });
		mKeyboardListView.setAdapter(inputMethodAdapter);
		mKeyboardListView.setSelection(select);

	}

	private void registerListeners() {
		mKeyboardListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				changeRadioImg(balanceIndex, false);
				changeRadioImg(position, true);
				balanceIndex = position;
				settingInputMethod(position);
				dismiss();
			}
		});
	}

	/**
	 * setting input method
	 * 
	 * @param position
	 */
	private void settingInputMethod(int position) {

		InputMethodInfo property = mInputMethodInfos.get(position);
		String id = property.getId();

		Settings.Secure.putString(mLanguageSettingActivity.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD,
				id != null ? id : mLastInputMethodId);
	}

	/**
	 * get the default input method and all input methods
	 */
	private void onCreateIMM() {
		InputMethodManager imm = (InputMethodManager) mLanguageSettingActivity
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		mInputMethodProperties = imm.getInputMethodList();

		// the id of default input method
		mLastInputMethodId = Settings.Secure.getString(mLanguageSettingActivity.getContentResolver(),
				Settings.Secure.DEFAULT_INPUT_METHOD);
		Log.d("mLastInputMethodId", "mLastInputMethodId--Key="+mLastInputMethodId);
		for (InputMethodInfo property : mInputMethodProperties) {
			// the id of input method
			String id = property.getId();
			if (id.equals("com.android.inputmethod.latin/.LatinIME")) {
				continue;
			}
			mInputMethodInfos.add(property);
			count++;
			// the package of input method
			// String packageName = property.getPackageName();
			// the name of input method
			CharSequence label = property.loadLabel(mLanguageSettingActivity.getPackageManager());
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("nameItem", label);
			boolean isDefault = isDefaultInputMethod(mLastInputMethodId, id);
			if (isDefault) {
				map.put("imgItem", R.drawable.selected);
				select = count < 0 ? 0 : count;
			} else {
				map.put("imgItem", R.drawable.unselected);
			}
			items.add(map);
		}
		balanceIndex = select;

	}

	/**
	 * change the background of radio button
	 * 
	 * @param selectedItem
	 *            current selected item
	 * @param b
	 *            whether clicked
	 */
	@SuppressWarnings("unchecked")
	private void changeRadioImg(int selectedItem, boolean b) {
		SimpleAdapter la = inputMethodAdapter;
		HashMap<String, Object> map = (HashMap<String, Object>) la.getItem(selectedItem);
		if (b) {
			map.put("imgItem", R.drawable.selected);
		} else {
			map.put("imgItem", R.drawable.unselected);
		}
		la.notifyDataSetChanged();
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

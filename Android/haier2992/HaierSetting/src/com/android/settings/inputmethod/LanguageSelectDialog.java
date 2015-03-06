package com.android.settings.inputmethod;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.ActivityManagerNative;
import android.app.Dialog;
import android.app.IActivityManager;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
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
import com.android.settings.city.CityHelp;

/**
 * select language
 * 
 * @author ducj(ducj@biaoqi.com.cn)
 * @date 2011-11-5 上午09:51:36
 * @since 1.0
 */
public class LanguageSelectDialog extends Dialog {

	// 用于语言显示的LIstView
	private ListView mLanguageListView;
	// 语言目前选择项
	private int balanceIndex = 0;

	private List<Map<String, Object>> languageNames = new ArrayList<Map<String, Object>>();

	private SimpleAdapter languageAdapter;

	private String[] locales = { "zh_CN", "en_US" };
	// 用于判断当前的语言
	private boolean isSelected;
	// 用于标记当前的选中项
	private int select;

	private Locale mLocale;

	private InputMethodAndLanguageSettingsActivity mLanguageSettingActivity;
	
	public LanguageSelectDialog(InputMethodAndLanguageSettingsActivity languageSettingActivity) {
		super(languageSettingActivity);
		this.mLanguageSettingActivity = languageSettingActivity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.language_setting);

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
		int height = (int) (display.getHeight() * 0.4);

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
		Configuration conf = mLanguageSettingActivity.getResources()
				.getConfiguration();
		String locale = conf.locale.getDisplayName(conf.locale);
		if (locale != null && locale.length() > 1) {
			locale = Character.toUpperCase(locale.charAt(0))
					+ locale.substring(1);
			System.out.println("locale ++++++++++++" + locale);
		}

		if (!locale.equals("English (United States)")) {
			isSelected = true;
			select = 0;
		} else {
			isSelected = false;
			select = 1;
		}

		mLanguageListView = (ListView) findViewById(R.id.language_select_list);
		mLanguageListView.setDivider(null);

		String items[] = mLanguageSettingActivity.getResources()
				.getStringArray(R.array.language_names);

		// add the first data
		HashMap<String, Object> map1 = new HashMap<String, Object>();
		map1.put("nameItem", items[0]);
		if (isSelected) {
			map1.put("imgItem", R.drawable.selected);
		} else {
			map1.put("imgItem", R.drawable.unselected);
		}
		languageNames.add(map1);

		// add the second data
		HashMap<String, Object> map2 = new HashMap<String, Object>();
		map2.put("nameItem", items[1]);
		if (isSelected) {
			map2.put("imgItem", R.drawable.unselected);
		} else {
			map2.put("imgItem", R.drawable.selected);
		}
		languageNames.add(map2);

		languageAdapter = new SimpleAdapter(mLanguageSettingActivity,
				languageNames, R.layout.date_format_item_list, new String[] {
						"nameItem", "imgItem" }, new int[] {
						R.id.date_format_item, R.id.date_format_item_iv });

		mLanguageListView.setAdapter(languageAdapter);
		System.out.println("select " + select);
		mLanguageListView.setSelection(select);
	}

	private void registerListeners() {
		mLanguageListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				changeRadioImg(balanceIndex, false);
				changeRadioImg(position, true);
				balanceIndex = position;

				mLocale = getLocale(locales[position]);

				try {
					IActivityManager am = ActivityManagerNative.getDefault();
					final Configuration config = am.getConfiguration();

					config.locale = mLocale;

					// indicate this isn't some passing default - the user wants
					// this remembered
					config.userSetLocale = true;

					am.updateConfiguration(config);
					// Trigger the dirty bit for the Settings Provider.
					BackupManager.dataChanged("com.android.providers.settings");
					
					//refreshCityName(config);
					
					new Thread(new Runnable(){
						@Override
						public void run() {
							refreshCityName(config);
						}
					}).start();
					
				} catch (RemoteException e) {
					// Intentionally left blank
					e.printStackTrace();
				}
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
		SimpleAdapter la = languageAdapter;
		HashMap<String, Object> map = (HashMap<String, Object>) la
				.getItem(selectedItem);
		if (b) {
			map.put("imgItem", R.drawable.selected);
		} else {
			map.put("imgItem", R.drawable.unselected);
		}
		la.notifyDataSetChanged();
	}

	/**
	 * 获取当前的Locale
	 * 
	 * @param locale
	 * @return
	 */
	private Locale getLocale(String locale) {
		mLocale = new Locale(locale.substring(0, 2), locale.substring(3, 5));
		return mLocale;
	}
	
	/**
	 * refresh city xml
	 * 
	 * @param config
	 */
	public void refreshCityName(Configuration config) {
		String locale = config.locale.getDisplayName(config.locale);
		if (null == locale || locale.length() < 1)
			return;
		locale = Character.toUpperCase(locale.charAt(0)) + locale.substring(1);
		String fileName = locale.equals("English (United States)") ? "citys_en.xml" : "citys.xml";
		String cityId = CityHelp.getCityId(mLanguageSettingActivity);
		String cityName = getNewCityName(fileName, cityId);
		CityHelp.setCitySettings(mLanguageSettingActivity, cityId, cityName);
	}

	public String getNewCityName(String fileName, String id) {
		if ("".equals(id))
			return "";
		String cityName = "";
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			InputStream inputStream = mLanguageSettingActivity.getAssets().open(fileName);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inputStream);

			NodeList cityNodes = document.getElementsByTagName("city");
			for (int i = 0; i < cityNodes.getLength(); i++) {
				Element cityNode = (Element) cityNodes.item(i);
				String cityId = cityNode.getAttributes().item(0).getNodeValue();
				if (cityId.equals(id)) {
					cityName = cityNode.getAttributes().item(1).getNodeValue();
					return cityName;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cityName;
	}
}

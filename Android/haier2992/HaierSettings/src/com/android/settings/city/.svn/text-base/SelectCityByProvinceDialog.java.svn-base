package com.android.settings.city;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.settings.R;

public class SelectCityByProvinceDialog extends Dialog implements OnItemClickListener {

	private Context mContext;
	private GridView mGridView;
	private TextView mProvinceName;
	private String provinceName;
	private List<Map<String, String>> cityList;
	private Map<String, String> cityMap;
	private String selectedCityId;
	private String selectedCityName;

	private ProvinceSelectSettingActivity act;
	public String getSelectedCityName() {
		return selectedCityName;
	}

	public SelectCityByProvinceDialog(Context context, String provinceName) {

		this(context);
		this.mContext = context;
		this.act = (ProvinceSelectSettingActivity) context;
		this.provinceName = provinceName;
	}

	public SelectCityByProvinceDialog(Context context) {
		super(context);
		this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.city_citysselect);

		mProvinceName = (TextView) findViewById(R.id.citys_list);
		mGridView = (GridView) findViewById(R.id.cities_gridview);
		Window window = getWindow();
//		WindowManager.LayoutParams lp = window.getAttributes();
//		lp.gravity = Gravity.CENTER;
//		lp.x = 0;
//		lp.y = 0;
//		window.setAttributes(lp);
		window.setGravity(Gravity.CENTER);
		init();
	}

	private void init() {
		if (provinceName != null) {
			mProvinceName.setText(provinceName);
		}
		// mGridView.setSelector(new ColorDrawable(new Color().alpha(0)));
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		cityList = new ArrayList<Map<String, String>>();
		try {
			String fileName = isChinese() ? "citys.xml" : "citys_en.xml";
			InputStream inputStream = mContext.getAssets().open(fileName);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inputStream);
			NodeList nodes_province = document.getElementsByTagName("province");
			for (int i = 0; i < nodes_province.getLength(); i++) {
				Element node_province = (Element) nodes_province.item(i);
				String province_Name = node_province.getAttributes().item(1).getNodeValue();
				if (province_Name.equals(provinceName)) {
					NodeList nodes_city = node_province.getElementsByTagName("city");
					for (int j = 0; j < nodes_city.getLength(); j++) {
						cityMap = new HashMap<String, String>();
						CityEntity cityEntity = new CityEntity();
						Element node_city = (Element) nodes_city.item(j);
						String locid = node_city.getAttributes().item(0).getNodeValue();
						cityEntity.setLocid(locid);
						String name = node_city.getAttributes().item(1).getNodeValue();
						cityEntity.setName(name);
						cityMap.put("locid", locid);
						cityMap.put("name", name);
						cityList.add(cityMap);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("tag", "解析xml文档出现问题");
		}

		SimpleAdapter adapter = new SimpleAdapter(mContext, cityList, R.layout.city_gridview_item,
				new String[] { "name" }, new int[] { R.id.city_item_test });
		mGridView.setAdapter(adapter);
		mGridView.setOnItemClickListener(this);
	}

	@SuppressWarnings("unchecked")
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		Map<String, String> item = (Map<String, String>) adapter.getItemAtPosition(position);
		selectedCityId = item.get("locid");
		selectedCityName = item.get("name");
		
		CityHelp.setCitySettings(mContext, selectedCityId, selectedCityName);
		Intent intent = new Intent();
		intent.putExtra("locid", selectedCityId);
		intent.putExtra("city", selectedCityName);
		intent.setClass(mContext, CitySettings.class);
		act.setResult(Activity.RESULT_OK, intent);
		act.finish();
		this.dismiss();
	}

	@Override
	public void show() {
		super.show();
	}

	/**
	 * 判断系统当前的语言环境是否中文 add by zhanghs at 2012-03-05
	 * 
	 * @return 中文：true 英文：false
	 */
	public boolean isChinese() {
		Configuration conf = mContext.getResources().getConfiguration();
		String locale = conf.locale.getDisplayName(conf.locale);
		if (locale != null && locale.length() > 1) {
			locale = Character.toUpperCase(locale.charAt(0)) + locale.substring(1);
		}
		return locale.equals("English (United States)") ? false : true;
	}
}

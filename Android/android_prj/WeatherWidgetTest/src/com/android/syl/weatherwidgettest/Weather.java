package com.android.syl.weatherwidgettest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

public class Weather extends AppWidgetProvider {
	private static final String TAG = "Weather_syl";
	public static final String KEY_WEATHER = "weatherInfo";
	private static SharedPreferences sharePre = null;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.e(TAG, "onUpdate");
		// 更新界面
		updateAppWidget(context, appWidgetManager, appWidgetIds);

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
		String action = intent.getAction();
		Log.v(TAG, "onReceive: " + action);
		
		if (action.equals("android..action.syl.update")) {
			String value = intent.getStringExtra("value");
			Log.v(TAG, "value: " + value);
//			RemoteViews appWidgetView = new RemoteViews(
//					context.getPackageName(), R.layout.main);
//			appWidgetView.setTextViewText(R.id.weather_update, value);
//			AppWidgetManager.getInstance(context).updateAppWidget(
//					new ComponentName(context, Weather.class), appWidgetView);
		}
	}

	private static void updateAppWidget(Context context,
			AppWidgetManager appWidgetManager, int[] appWidgetIds) {

		Log.v(TAG, "updataAppWidget");
		// 获取解析数据
		String[] currentWeather = new String[6];
		String strUrl = "http://www.google.com/ig/api?weather=wuhan&hl=zh-cn";
		try {

			URL url = new URL(strUrl);
			// 解析XML文件
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader xmlreader = parser.getXMLReader();

			WeatherHandler handler = new WeatherHandler();
			xmlreader.setContentHandler(handler);
			// 网络获取接口返回的XML文件
			InputStreamReader isr = new InputStreamReader(url.openStream(),
					"GBK");

			// BufferedReader in = new BufferedReader(isr);
			// String s = null;
			// Log.e(TAG, "get xml file:");
			// while((s = in.readLine()) != null) {
			// Log.d(TAG, s);
			// }

			InputSource is = new InputSource(isr);
			xmlreader.parse(is);

			currentWeather = handler.getCurrentWeather();

		} catch (Exception e) {
			e.printStackTrace();
		}

		// 处理获取的数据
		String currentIcon = currentWeather[4];
		String currentInfo = currentWeather[0] + "\n" + currentWeather[2]
				+ "摄氏度    " + "\n" + currentWeather[3] + "\n"
				+ currentWeather[5];

		// 把数据显示上widget
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.main);
		// 图片
		try {
			URL imgURL = new URL("http://www.google.com/" + currentIcon);
			URLConnection conn = imgURL.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			Bitmap bm = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();
			views.setImageViewBitmap(R.id.image, bm);
		} catch (Exception e) {
		}
		// 文字
		views.setTextViewText(R.id.weather_text, currentInfo);

		sharePre = PreferenceManager.getDefaultSharedPreferences(context);
		sharePre.edit().putString(KEY_WEATHER, currentInfo).commit();

		// 添加响应事件
		Intent intent = new Intent(context, WeatherWidgetTestActivity.class);
		// intent.putExtra("weatherInfo", currentInfo);
		PendingIntent Pintent = PendingIntent
				.getActivity(context, 0, intent, 0);
		views.setOnClickPendingIntent(R.id.widget, Pintent);

		// 最后更新
		appWidgetManager.updateAppWidget(appWidgetIds, views);
	}
}

package com.android.syl.weatherwidgettest;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherWidgetTestActivity extends Activity {
	private final static String TAG = "WeatherWidgetTestActivity_syl";
	private Button button = null;
	private TextView editWeather = null;
	private EditText editNumber = null;
	private BroadcastReceiver mBR = null;

	private static int UPDATE_TIME = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity);

		Log.d(TAG, "onCreate");

		setTitle("·¢ËÍÎÂÇé");

		findViews();

		// »ñÈ¡ÌìÆøÊý¾Ý
		// String weatherInfo =
		// getIntent().getStringExtra("weatherInfo").toString();

		SharedPreferences sharedPres = PreferenceManager.getDefaultSharedPreferences(this);
		String weatherInfo = sharedPres.getString(Weather.KEY_WEATHER, "not get data");

		editWeather.setText(weatherInfo);
		editNumber.requestFocus();

		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String number = editNumber.getText().toString();
				String weather = editWeather.getText().toString();
				Log.d("test1", weather);

				if (number.equals("")) {
					Toast.makeText(WeatherWidgetTestActivity.this, "ÇëÊäÈë·¢ËÍºÅÂë£¡",
							Toast.LENGTH_LONG).show();
					return;
				} else {
					// sendMessage(number, weather);
					Intent intent = new Intent("android..action.syl.update");
					intent.putExtra("value", UPDATE_TIME++);
					sendBroadcast(intent);
					Toast.makeText(WeatherWidgetTestActivity.this,
							"android.syl.update: " + UPDATE_TIME,
							Toast.LENGTH_LONG).show();
					RemoteViews appWidgetView = new RemoteViews(
							getPackageName(), R.layout.main);
					appWidgetView.setTextViewText(R.id.weather_update, String.valueOf(UPDATE_TIME));
					AppWidgetManager.getInstance(WeatherWidgetTestActivity.this).updateAppWidget(
							new ComponentName(WeatherWidgetTestActivity.this, Weather.class), appWidgetView);
				}
			}
		});

	}

	public void sendMessage(String number, String weather) {
		String SENT = "SMS_SENT";

		PendingIntent pi = PendingIntent.getBroadcast(this, 0,
				new Intent(SENT), 0);

		// ---when the SMS has been sent---
		mBR = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "¶ÌÐÅ·¢ËÍ³É¹¦£¡:)",
							Toast.LENGTH_LONG).show();
					editNumber.setText("");
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(getBaseContext(), "¶ÌÐÅ·¢ËÍÊ§°Ü£¡1:(",
							Toast.LENGTH_LONG).show();
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(getBaseContext(), "¶ÌÐÅ·¢ËÍÊ§°Ü£¡2:(",
							Toast.LENGTH_LONG).show();
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(getBaseContext(), "¶ÌÐÅ·¢ËÍÊ§°Ü£¡3:(",
							Toast.LENGTH_LONG).show();
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(getBaseContext(), "¶ÌÐÅ·¢ËÍÊ§°Ü£¡:(",
							Toast.LENGTH_LONG).show();
					break;
				}
			}
		};

		registerReceiver(mBR, new IntentFilter(SENT));

		Log.d(TAG, weather);
		SmsManager sm = SmsManager.getDefault();
		sm.sendTextMessage(number, null, weather, pi, null);
	}

	public void findViews() {
		button = (Button) findViewById(R.id.button);
		editWeather = (TextView) findViewById(R.id.messageedit);
		editNumber = (EditText) findViewById(R.id.numberedit);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// unregisterReceiver(mBR);
		super.onDestroy();
	}
}
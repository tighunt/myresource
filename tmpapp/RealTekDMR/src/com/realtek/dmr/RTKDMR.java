package com.realtek.dmr;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RTKDMR extends Activity{

	private final String TAG = "RTKDMR";
	private Button startService = null;
	private Button stopService = null;
        private ActivityManager am = null;
        private boolean isMonkeyRunning = false;
	
	@Override  
	public void onCreate(Bundle savedInstanceState) {  
		super.onCreate(savedInstanceState);
		System.out.println("xxxxxxxxxxxxx onCreate");
		setContentView(R.layout.dmr);
		startService = (Button)findViewById(R.id.start_service);
                am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                if (am != null) isMonkeyRunning = am.isUserAMonkey();

                // check monkey running or not
                if (!isMonkeyRunning) {
		startService.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("xxxxxxxxxx startService");
				
				startService(new Intent("com.android.server.Service_DMR"));
			}
		});
                }
		
		stopService = (Button)findViewById(R.id.stop_service);

                // check monkey running or not
                if (!isMonkeyRunning) {
		stopService.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("xxxxxxxxxx stopService");
				stopService(new Intent("com.android.server.Service_DMR"));
			}
		});
                }
		
		System.out.println("xxxxxxxxxx startService end");
	}
}

package com.android.settings.other;

import java.util.Timer;
import java.util.TimerTask;

import com.android.settings.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;


public class CountDownTimerActivity extends Activity
{
    private TextView leftTimeText;
    private LinearLayout leftTimeLinear;
    private LinearLayout chooseLinear;
    private LinearLayout chooseLinear_yes;
    private LinearLayout chooseLinear_no;

    private LeftTimeBroadcastRev leftTimeBroadcastRev;
    private int leftTimeValue = 0;
    private static SleepSettingDialog mSleepSettingDialog;
    
    static void SetSleepSettingDialog(SleepSettingDialog sleepSettingDialog){
    	mSleepSettingDialog = sleepSettingDialog;
    }
    
    
    
    
    public   Handler handler = new Handler() {
        public void handleMessage(Message msg){
        	super.handleMessage(msg); 
        	if( (msg.what > 0)  &&  (msg.what <= 60) ){
        		leftTimeText.setText(msg.what  + "");
        		leftTimeText.invalidate();
        	}
        	else{
        		finish();
        	}
        }	        
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	Log.d("CountDownTimerActivity", "onCreate");
    	try{
	    	if(getIntent().getIntExtra("lefttime", -1) == 0){
	    		finish();
	    	}
    	}catch (Exception e) {
			Log.d("CountDownTimerActivity", e.toString());
		}
        super.onCreate(savedInstanceState);
        setContentView(R.layout.countdowntimer);
        findViews();
//        leftTimeBroadcastRev = new LeftTimeBroadcastRev();
//        IntentFilter filter = new IntentFilter("epgLeftTime");
//		   registerReceiver(leftTimeBroadcastRev, filter);
        chooseLinear_yes.requestFocus(); 
        setOnClickListenter();
        
        Intent intent =getIntent();
        leftTimeValue = intent.getIntExtra("LeftTime", 0);
        if(leftTimeValue == 60){
        	leftTimeText.setText(leftTimeValue  + "");
            leftTimeText.invalidate();        	        
            new Timer().schedule(new TimerTask(){
            	public void run(){        		
            		handler.sendEmptyMessage(leftTimeValue);
            		leftTimeValue--;
            	}
            },  0,  1000);
        }      
    }
	
    
    private void findViews()
    {
        leftTimeText = (TextView) findViewById(R.id.textview_left_time_val);
        leftTimeLinear = (LinearLayout) findViewById(R.id.linearlayout_left_time);
        chooseLinear = (LinearLayout) findViewById(R.id.linearlayout_left_time_choosen);
        chooseLinear_yes = (LinearLayout) findViewById(R.id.linearlayout_left_time_choosen_yes);
//        chooseLinear_no = (LinearLayout) findViewById(R.id.linearlayout_left_time_choosen_no);
//        chooseLinear_no.requestFocus();
        leftTimeLinear.setFocusable(false);
    }

    private void setOnClickListenter()
    {
        OnClickListener listener = new OnClickListener() {

            @Override
            public void onClick(View view)
            {
                int currentid = getCurrentFocus().getId();
                // TODO Auto-generated method stub
                switch (currentid)
                {
                    case R.id.linearlayout_left_time_choosen_yes:
                    {
//                        TVRootApp app = (TVRootApp) getApplication();
//                        TvDeskProvider serviceProvider = (TvDeskProvider) app.getTvDeskProvider();
//                        EpgDesk ed = serviceProvider.getEpgManagerInstance();
//                        System.out.println("\n>>>execEpgTimerAction");
//                        try
//                        {
//                            ed.execEpgTimerAction();
//                            ed.delEpgEvent(0);
//                        }
//                        catch (TvCommonException e)
//                        {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                        startActivity(new Intent(CountDownTimerActivity.this,RootActivity.class));
                    	mSleepSettingDialog.setSleeptimerMode(0);
                        finish();
                    }
                        break;
 /*                   case R.id.linearlayout_left_time_choosen_no:
                    {
//                        TVRootApp app = (TVRootApp) getApplication();
//                        TvDeskProvider serviceProvider = (TvDeskProvider) app.getTvDeskProvider();
//                        EpgDesk ed = serviceProvider.getEpgManagerInstance();
//                        try
//                        {
//                            ed.cancelEpgTimerEvent( ed.getEpgTimerEventByIndex(0).startTime, false);
////                            ed.delEpgEvent(0);
//                        }
//                        catch (TvCommonException e)
//                        {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                        finish();
                    }
                        break;*/
                    default:
                        break;
                }
            }
        };
        chooseLinear_yes.setOnClickListener(listener);///////////////////////////////////
//        chooseLinear_no.setOnClickListener(listener);
    }

    @Override
    public boolean onKeyDown(int arg0, KeyEvent arg1) {
    	if(arg1.getKeyCode() == KeyEvent.KEYCODE_BACK){
    		return true;
    	}
    	return super.onKeyDown(arg0, arg1);
    }
    
    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
    	///overridePendingTransition(0,0);
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
 //   	unregisterReceiver(leftTimeBroadcastRev);
        super.onStop();
    }
    class LeftTimeBroadcastRev extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			 if(intent != null)
             {
                 leftTimeValue = intent.getIntExtra("LeftTime", 0);
             }
             System.out.println("\n###########leftTimeValue " + leftTimeValue);
             if(leftTimeValue > 0)
             {
                 leftTimeText.setText(leftTimeValue  + "");
                 leftTimeText.invalidate();
             }
             else
             {
/*
                 TVRootApp app = (TVRootApp) getApplication();
                 TvDeskProvider serviceProvider = (TvDeskProvider) app.getTvDeskProvider();
                 EpgDesk ed = serviceProvider.getEpgManagerInstance();
                 try
                 {
                     ed.execEpgTimerAction();
                     ed.delEpgEvent(0);
                 }
                 catch (TvCommonException e)
                 {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                 }
*/
//                 startActivity(new Intent(CountDownTimerActivity.this,RootActivity.class));
                 finish();
             }
		}
    	
    }

}



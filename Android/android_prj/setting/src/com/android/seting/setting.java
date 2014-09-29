package com.android.seting;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.view.View.OnClickListener;


public class setting extends Activity {

	public static String[] array = { "图像设置", "声音设置", "网络设置", "高级设置",
		"用户设置", "本机设置"};	
	//SeekBar sb = (SeekBar)findViewById(R.id.seek_brightness);
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting); 
        /*sb.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //Do something
                }
            });*/
    }
	
	/*public OnSeekBarChangeListener sbLis=new OnSeekBarChangeListener(){
		 
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			//进度改变时触发
			//tv.setText(String.valueOf(sb.getProgress()));
 
		}
 
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// 开始拖动时触发，与onProgressChanged区别在于onStartTrackingTouch在停止拖动前只触发一次
			//而onProgressChanged只要在拖动，就会重复触发
		}
 
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			//结束拖动时触发
 
		}			
    };*/
	public void clickhandler(View v)
	{
		switch(v.getId())
		{
			case R.id.seek_brightness:
				System.out.println("Enter clickhandler");
				final TextView tv = (TextView)findViewById(R.id.text_brightness);
				final SeekBar sb = (SeekBar)findViewById(R.id.seek_brightness);
				
				 OnSeekBarChangeListener sbLis=new OnSeekBarChangeListener(){
					 
						@Override
						public void onProgressChanged(SeekBar seekBar, int progress,
								boolean fromUser) {
							//进度改变时触发
							tv.setText(String.valueOf(sb.getProgress()));
				 
						}
				 
						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
							// 开始拖动时触发，与onProgressChanged区别在于onStartTrackingTouch在停止拖动前只触发一次
							//而onProgressChanged只要在拖动，就会重复触发
						}
				 
						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
							//结束拖动时触发
				 
						}			
				    };
				    
				    //sb.setOnSeekBarChangeListener(sbLis);
				break;
			case R.id.seek_contrast:
				break;
			case R.id.seek_color:
				break;
			case R.id.seek_hue:
				break;
			case R.id.seek_sharpness:
				break;
			case R.id.bt_p1:
				System.out.println("click p1");
		}
	}

}

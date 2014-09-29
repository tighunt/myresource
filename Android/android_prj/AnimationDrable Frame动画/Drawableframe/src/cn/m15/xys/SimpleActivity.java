package cn.m15.xys;




import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SimpleActivity extends Activity {

    /**播放动画按钮**/
    Button button0 = null;
  
    /**停止动画按钮**/
    Button button1 = null;
    
    /**设置动画循环选择框**/
    RadioButton radioButton0= null;
    RadioButton radioButton1= null;
    RadioGroup  radioGroup = null;
  
    /**拖动图片修改Alpha值**/
    SeekBar seekbar = null;
  
    /**绘制动画View**/
    ImageView imageView = null;
   
    /**绘制动画对象**/
    AnimationDrawable animationDrawable = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.simple);

	/**拿到ImageView对象**/
	imageView = (ImageView)findViewById(R.id.imageView);
	/**通过ImageView对象拿到背景显示的AnimationDrawable**/
	animationDrawable = (AnimationDrawable) imageView.getBackground();
	
	
	/**开始播放动画**/
	button0 = (Button)findViewById(R.id.button0);
	button0.setOnClickListener(new OnClickListener() {
	    
	    @Override
	    public void onClick(View arg0) {
		/**播放动画**/
		if(!animationDrawable.isRunning()) {
		    animationDrawable.start();
		}
	    }
	});
	
	/**停止播放动画**/
	button1 = (Button)findViewById(R.id.button1);
	button1.setOnClickListener(new OnClickListener() {
	    
	    @Override
	    public void onClick(View arg0) {
		/**停止动画**/
		if(animationDrawable.isRunning()) {
		    animationDrawable.stop();
		}
	    }
	});
	/**单次播放**/
	radioButton0 = (RadioButton)findViewById(R.id.checkbox0);
	/**循环播放**/
	radioButton1 = (RadioButton)findViewById(R.id.checkbox1);
	/**单选列表组**/
	radioGroup = (RadioGroup)findViewById(R.id.radiogroup);
	radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
	    
	    @Override
	    public void onCheckedChanged(RadioGroup radioGroup, int checkID) {
		if(checkID == radioButton0.getId()) {
		    //设置单次播放
		    animationDrawable.setOneShot(true);
		}else if (checkID == radioButton1.getId()) {
		    //设置循环播放
		    animationDrawable.setOneShot(false);
		}
		
		//发生改变后让动画重新播放
		animationDrawable.stop();
		animationDrawable.start();
	    }
	});
	
	/**监听的进度条修改透明度**/
	seekbar = (SeekBar)findViewById(R.id.seekBar);
	seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
	    @Override
	    public void onStopTrackingTouch(SeekBar seekBar) {
		
	    }
	    @Override
	    public void onStartTrackingTouch(SeekBar seekBar) {
		
	    }
	    @Override
	    public void onProgressChanged(SeekBar seekBar, int progress, boolean frameTouch) {
		/**设置动画Alpha值**/
		animationDrawable.setAlpha(progress);
		/**通知imageView 刷新屏幕**/
		imageView.postInvalidate();
	    }
	});
	
    }
}
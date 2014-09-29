package cn.m15.xys;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class ScaleActivity extends Activity {

    /**缩小动画按钮**/
    Button mButton0 = null;
  
    /**放大动画按钮**/
    Button mButton1 = null;
  
    /**显示动画的ImageView**/
    ImageView mImageView = null;
  
    /**缩小动画**/
    Animation mLitteAnimation = null;
    
    /**放大动画**/
    Animation mBigAnimation = null; 
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.scale);

	/**拿到ImageView对象**/
	mImageView = (ImageView)findViewById(R.id.imageView);

	/**加载缩小与放大动画**/
	mLitteAnimation = AnimationUtils.loadAnimation(this, R.anim.scalelitte);
	mBigAnimation = AnimationUtils.loadAnimation(this, R.anim.scalebig);
	
	mButton0 = (Button)findViewById(R.id.button0);
	mButton0.setOnClickListener(new OnClickListener() {
	    
	    @Override
	    public void onClick(View arg0) {
	    
		/**播放缩小动画**/
		mImageView.startAnimation(mLitteAnimation);
	    
	    }
	});
	
	mButton1 = (Button)findViewById(R.id.button1);
	mButton1.setOnClickListener(new OnClickListener() {
	    
	    @Override
	    public void onClick(View arg0) {
		/**播放放大动画**/
		mImageView.startAnimation(mBigAnimation);
	    }
	});
    }
}
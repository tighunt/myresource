package cn.m15.xys;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class RotateActivity extends Activity {

    /**向左旋转动画按钮**/
    Button mButton0 = null;
  
    /**向右旋转动画按钮**/
    Button mButton1 = null;
  
    /**显示动画的ImageView**/
    ImageView mImageView = null;
  
    /**向左旋转动画**/
    Animation mLeftAnimation = null;
    
    /**向右旋转动画**/
    Animation mRightAnimation = null; 
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.retate);

	/**拿到ImageView对象**/
	mImageView = (ImageView)findViewById(R.id.imageView);

	/**加载向左与向右旋转动画**/
	mLeftAnimation = AnimationUtils.loadAnimation(this, R.anim.retateleft);
	mRightAnimation = AnimationUtils.loadAnimation(this, R.anim.retateright);
	
	mButton0 = (Button)findViewById(R.id.button0);
	mButton0.setOnClickListener(new OnClickListener() {
	    
	    @Override
	    public void onClick(View arg0) {
	    
		/**播放向左旋转动画**/
		mImageView.startAnimation(mLeftAnimation);
	    
	    }
	});
	
	mButton1 = (Button)findViewById(R.id.button1);
	mButton1.setOnClickListener(new OnClickListener() {
	    
	    @Override
	    public void onClick(View arg0) {
		/**播放向右旋转动画**/
		mImageView.startAnimation(mRightAnimation);
	    }
	});
    }
}
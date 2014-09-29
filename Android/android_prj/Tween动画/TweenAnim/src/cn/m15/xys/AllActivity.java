package cn.m15.xys;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class AllActivity extends Activity {
    /**显示动画的ImageView**/
    ImageView mImageView = null;
  
    /**综合动画**/
    Animation mAnimation = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.translate);

	/**拿到ImageView对象**/
	mImageView = (ImageView)findViewById(R.id.imageView);

	/**加载综合动画**/
	mAnimation = AnimationUtils.loadAnimation(this, R.anim.all);
	
	/**播放综合动画**/
	mImageView.startAnimation(mAnimation);
    }
}
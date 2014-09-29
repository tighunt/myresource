package org.ourunix.android.animationtest;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class AnimationTest extends Activity {
	private Button mButton;
	private ImageView mImageView;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        initView();
        
        mButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startAnimation();
			}
		});
    }
    
	private void initView() {
		mButton = (Button)findViewById(R.id.button_b);
		mImageView = (ImageView)findViewById(R.id.animation_iv);
	}
    
	protected void startAnimation() {
		mImageView.setBackgroundResource(R.drawable.frame_animation);
		AnimationDrawable frameAnimation = (AnimationDrawable)mImageView.getBackground();
		if (frameAnimation.isRunning()){
			frameAnimation.stop();
		}else{
			frameAnimation.stop();
			frameAnimation.start();
		}
	}
}
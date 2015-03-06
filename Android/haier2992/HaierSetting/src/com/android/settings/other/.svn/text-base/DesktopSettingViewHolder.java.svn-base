package com.android.settings.other;

import com.android.settings.R;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class DesktopSettingViewHolder {
	
	private DesktopSettingActivity mWallPaperActivity;
	
	private RelativeLayout mWallPaperStaticLayout;
	private Button mWallPaperStaticBtn;
	
	private RelativeLayout mWallPaperLiveLayout;
	private Button mWallPaperLiveBtn;
	
	//private RelativeLayout mWallPaperGalleryLayout;
	//private Button mWallPaperGalleryBtn;
	
	public DesktopSettingViewHolder(DesktopSettingActivity activity){
		mWallPaperActivity = activity;
		
		findViews();
	}
	
	public void findViews(){
		mWallPaperStaticLayout = (RelativeLayout)mWallPaperActivity.findViewById(R.id.other_wallpaper_static);
		mWallPaperStaticBtn = (Button)mWallPaperActivity.findViewById(R.id.wallpaper_static_btn);
		
		mWallPaperLiveLayout = (RelativeLayout)mWallPaperActivity.findViewById(R.id.other_wallpaper_live);
		mWallPaperLiveBtn = (Button)mWallPaperActivity.findViewById(R.id.wallpaper_live_btn);
		
		//mWallPaperGalleryLayout = (RelativeLayout)mWallPaperActivity.findViewById(R.id.other_wallpaper_gallery);
		//mWallPaperGalleryBtn = (Button)mWallPaperActivity.findViewById(R.id.wallpaper_gallery_btn);
		
		mWallPaperStaticBtn.setFocusable(true);
		mWallPaperStaticBtn.setFocusableInTouchMode(true);
		mWallPaperStaticBtn.requestFocus();
		mWallPaperStaticLayout.setBackgroundResource(R.drawable.set_button);
	}
	
	public void setBtnClickListener(View.OnClickListener listener){
		mWallPaperStaticBtn.setOnClickListener(listener);
		mWallPaperLiveBtn.setOnClickListener(listener);
		//mWallPaperGalleryBtn.setOnClickListener(listener);
		// 2012-04-17 Zhanghs
		mWallPaperStaticLayout.setOnClickListener(listener);
		mWallPaperLiveLayout.setOnClickListener(listener);
		
	}
	
	public void setFocusChangeListener(View.OnFocusChangeListener listener){
		mWallPaperStaticBtn.setOnFocusChangeListener(listener);
		mWallPaperLiveBtn.setOnFocusChangeListener(listener);
		//mWallPaperGalleryBtn.setOnFocusChangeListener(listener);
	}
	
	public void processFocusChange(View v, boolean hasFocus){
		int id = v.getId();
		int resId = 0;
		
		switch(id){
		case R.id.wallpaper_static_btn:
			// 壁纸
			resId = hasFocus ? R.drawable.set_button : R.drawable.one_px;
			mWallPaperStaticLayout.setBackgroundResource(resId);
			break;
			
		case R.id.wallpaper_live_btn:
			// 动态壁纸
			resId = hasFocus ? R.drawable.set_button : R.drawable.one_px;
			mWallPaperLiveLayout.setBackgroundResource(resId);
			break;
			
		/*case R.id.wallpaper_gallery_btn:
			// 图库
			resId = hasFocus ? R.drawable.set_button : R.drawable.one_px;
			mWallPaperGalleryLayout.setBackgroundResource(resId);	
			break;*/
			
		default:
			break;
		}
	}

}

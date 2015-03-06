package com.android.settings.other;

import com.android.settings.R;

import android.content.ComponentName;
import android.content.Intent;
import android.view.View;

public class DesktopSettingListener {
	
	private final static int IDX_WALLPAPER_STATIC = 0;
	private final static int IDX_WALLPAPER_LIVE = 1;
	private final static int IDX_WALLPAPER_GALLERY = 2;
	
	private static final String PACKAGE_NAME[] = {
		"com.haier.launcher", //"com.android.launcher"
		"com.android.wallpaper.livepicker",
		//"com.android.gallery"
		"com.android.gallery3d"
	};
	
	private static final String ACTIVITY_NAME[] = {
		"com.haier.launcher2.WallpaperChooser", //"com.android.launcher2.WallpaperChooser",
		"com.android.wallpaper.livepicker.LiveWallpaperActivity",
		//"com.android.camera.PickWallpaper"
		"com.android.gallery3d.app.Wallpaper"
	};
	
	
	private DesktopSettingActivity mActivity;
	private DesktopSettingViewHolder mHolder;
	
	public DesktopSettingListener(DesktopSettingActivity activity, DesktopSettingViewHolder holder){
		mActivity = activity;
		mHolder = holder;
		
		mHolder.setBtnClickListener(new BtnClickListener());
		mHolder.setFocusChangeListener(new BtnFocusChangeListener());
	}
	
	public void chooseWallPaperAPK(int id){
		if (id < IDX_WALLPAPER_STATIC || id > IDX_WALLPAPER_GALLERY) {
			return;
		}
		
		ComponentName comp = new ComponentName(PACKAGE_NAME[id], ACTIVITY_NAME[id]);
		Intent intent = new Intent();
		intent.setComponent(comp);
		intent.setAction("android.intent.action.SET_WALLPAPER");
		mActivity.startActivity(intent);
	}
	
	
	public class BtnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			
			switch(id){
			case R.id.wallpaper_static_btn:
			case R.id.other_wallpaper_static: // 2012-04-17 Zhanghs
				// 壁纸
				chooseWallPaperAPK(IDX_WALLPAPER_STATIC);
				break;
				
			case R.id.wallpaper_live_btn:
			case R.id.other_wallpaper_live:
				// 动态壁纸
				chooseWallPaperAPK(IDX_WALLPAPER_LIVE);
				break;
				
			/*case R.id.wallpaper_gallery_btn:
				// 图库
				chooseWallPaperAPK(IDX_WALLPAPER_GALLERY);
				break;*/
				
			default:
				break;
			}
		}
		
	}
	
	public class BtnFocusChangeListener implements View.OnFocusChangeListener {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			mHolder.processFocusChange(v, hasFocus);
		}
		
	}

}

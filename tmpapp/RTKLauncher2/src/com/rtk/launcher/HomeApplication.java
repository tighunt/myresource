/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rtk.launcher;

import android.app.Application;
import android.util.DisplayMetrics;
import android.util.Log;

public class HomeApplication extends Application {
private int screenWidth;// px
private int screenHeight;// px
private int densityDpi;// dpi
private float scale;//  densityDpi/160
private boolean Appchanged = false;
private boolean is4k2k = false;	
private boolean isx4k2k = false;
    @Override
    public void onCreate() {
        super.onCreate();
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        //this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        densityDpi = dm.densityDpi;
        Log.e("", "densityDpi=" +densityDpi);
        scale = dm.density;
        Log.e("","scale =" + scale);
        if(screenWidth>=3840 || screenHeight >= 3840)
        	is4k2k = true;
        if(scale >=3)
        	isx4k2k = true;
    }

    /**
     * There's no guarantee that this function is ever called.
     */
    @Override
    public void onTerminate() {
        super.onTerminate();

    }

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public int getDensityDpi() {
		return densityDpi;
	}

	public void setDensityDpi(int densityDpi) {
		this.densityDpi = densityDpi;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public boolean isAppchanged() {
		return Appchanged;
	}

	public void setAppchanged(boolean appchanged) {
		Appchanged = appchanged;
	}

	public boolean isIs4k2k() {
		return is4k2k;
	}

	public void setIs4k2k(boolean is4k2k) {
		this.is4k2k = is4k2k;
	}

	public boolean isIsx4k2k() {
		return isx4k2k;
	}

	public void setIsx4k2k(boolean isx4k2k) {
		this.isx4k2k = isx4k2k;
	}
}

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

package com.tsb.launcher;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.HashMap;

/**
 * Cache of application icons.  Icons can be made from any thread.
 */
public class IconCache {
    @SuppressWarnings("unused")
    public static final String TAG = "Launcher.IconCache";
    public static final String audioClass = "com.rtk.mediabrowser.AudioBrowser";
    public static final String photoClass = "com.rtk.mediabrowser.GridViewActivity";
    public static final String videoClass = "com.rtk.mediabrowser.VideoBrowser";
    public static final String galleryClass = "com.android.gallery3d.app.Gallery";
    public static final String calendarClass = "com.android.calendar.AllInOneActivity";
    private static final String browserClass = "com.android.browser.BrowserActivity";
    public static final String storeClass = "com.hiapk.markettv.MarketMainFrame";
    public static final String miracastClass = "com.rtk.android.miracast.MiracastActivity";
    public static final String settingClass = "com.android.settings.Settings";
    public static final String infoClass = "com.realtek.information.MainActivity";
    private static final int INITIAL_ICON_CACHE_CAPACITY = 50;

    private static class CacheEntry {
        public Bitmap icon;
        public String title;
    }

    private final Bitmap mDefaultIcon;
    private final LauncherApplication mContext;
    private final PackageManager mPackageManager;
    private final HashMap<ComponentName, CacheEntry> mCache =
            new HashMap<ComponentName, CacheEntry>(INITIAL_ICON_CACHE_CAPACITY);
    private int mIconDpi;

    public IconCache(LauncherApplication context) {
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        mContext = context;
        mPackageManager = context.getPackageManager();
        mIconDpi = activityManager.getLauncherLargeIconDensity();

        // need to set mIconDpi before getting default icon
        mDefaultIcon = makeDefaultIcon();
    }

    public Drawable getFullResDefaultActivityIcon() {
        return getFullResIcon(Resources.getSystem(),
                android.R.mipmap.sym_def_app_icon);
    }

    public Drawable getFullResIcon(Resources resources, int iconId) {
        Drawable d;
        try {
            d = resources.getDrawableForDensity(iconId, mIconDpi);
        } catch (Resources.NotFoundException e) {
            d = null;
        }

        return (d != null) ? d : getFullResDefaultActivityIcon();
    }

    public Drawable getFullResIcon(String packageName, int iconId) {
        Resources resources;
        try {
            resources = mPackageManager.getResourcesForApplication(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }
        if (resources != null) {
            if (iconId != 0) {
                return getFullResIcon(resources, iconId);
            }
        }
        return getFullResDefaultActivityIcon();
    }

    public Drawable getFullResIcon(ResolveInfo info) {
        return getFullResIcon(info.activityInfo);
    }

    public Drawable getFullResIcon(ActivityInfo info) {

        Resources resources;
        try {
            resources = mPackageManager.getResourcesForApplication(
                    info.applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }
        if (resources != null) {
            int iconId = info.getIconResource();
            if (iconId != 0) {
                return getFullResIcon(resources, iconId);
            }
        }
        return getFullResDefaultActivityIcon();
    }

    private Bitmap makeDefaultIcon() {
        Drawable d = getFullResDefaultActivityIcon();
        Bitmap b = Bitmap.createBitmap(Math.max(d.getIntrinsicWidth(), 1),
                Math.max(d.getIntrinsicHeight(), 1),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        d.setBounds(0, 0, b.getWidth(), b.getHeight());
        d.draw(c);
        c.setBitmap(null);
        return b;
    }

    /**
     * Remove any records for the supplied ComponentName.
     */
    public void remove(ComponentName componentName) {
        synchronized (mCache) {
            mCache.remove(componentName);
        }
    }

    /**
     * Empty out the cache.
     */
    public void flush() {
        synchronized (mCache) {
            mCache.clear();
        }
    }

    /**
     * Fill in "application" with the icon and label for "info."
     */
    public void getTitleAndIcon(ApplicationInfo application, ResolveInfo info,
            HashMap<Object, CharSequence> labelCache) {
        synchronized (mCache) {

            CacheEntry entry = cacheLocked2(application.componentName, info, labelCache);
            application.title = entry.title;
            application.iconBitmap = entry.icon;
        }
    }

    public Bitmap getIcon(Intent intent) {
        synchronized (mCache) {
            final ResolveInfo resolveInfo = mPackageManager.resolveActivity(intent, 0);
            ComponentName component = intent.getComponent();
            
            if (resolveInfo == null || component == null) {
                return mDefaultIcon;
            }
            CacheEntry entry = cacheLocked(component, resolveInfo, null);
            return entry.icon;
        }
    }

    public Bitmap getIcon(ComponentName component, ResolveInfo resolveInfo,
            HashMap<Object, CharSequence> labelCache) {
        synchronized (mCache) {
            if (resolveInfo == null || component == null) {
                return null;
            }
            CacheEntry entry = cacheLocked(component, resolveInfo, labelCache);
            return entry.icon;
        }
    }

    public boolean isDefaultIcon(Bitmap icon) {
        return mDefaultIcon == icon;
    }

    private CacheEntry cacheLocked(ComponentName componentName, ResolveInfo info,
            HashMap<Object, CharSequence> labelCache) {
        CacheEntry entry = mCache.get(componentName);
        if (entry == null) {
            entry = new CacheEntry();

            mCache.put(componentName, entry);
            ComponentName key = LauncherModel.getComponentNameFromResolveInfo(info);
            if(getFixedInfo(componentName,entry))
            {
            	  if (labelCache != null) {
                    labelCache.put(key, entry.title);
                }
            	return entry;
            }

            if (labelCache != null && labelCache.containsKey(key)) {
                entry.title = labelCache.get(key).toString();
            } else {
                entry.title = info.loadLabel(mPackageManager).toString();
                if (labelCache != null) {
                    labelCache.put(key, entry.title);
                }
            }
            if (entry.title == null) {
                entry.title = info.activityInfo.name;
            }
            if(entry.icon == null)
            entry.icon = Utilities.createIconBitmap(
                    getFullResIcon(info), mContext);
        }
        
        else
        {
        	if(entry.icon.getWidth() < Utilities.getsIconWidth() ||
        			entry.icon.getHeight() < Utilities.getsIconHeight())
        	{
        		//Log.e(TAG, "remove icon");
        		mCache.remove(componentName);
                entry = new CacheEntry();

                mCache.put(componentName, entry);
                if(getFixedInfo(componentName,entry))
                {
                	return entry;
                }
                ComponentName key = LauncherModel.getComponentNameFromResolveInfo(info);
                if (labelCache != null && labelCache.containsKey(key)) {
                    entry.title = labelCache.get(key).toString();
                } else {
                    entry.title = info.loadLabel(mPackageManager).toString();
                    if (labelCache != null) {
                        labelCache.put(key, entry.title);
                    }
                }
                if (entry.title == null) {
                    entry.title = info.activityInfo.name;
                }
                if(entry.icon == null)
                entry.icon = Utilities.createIconBitmap(
                        getFullResIcon(info), mContext);
        	}
        }
        return entry;
    }
    
    private boolean getFixedInfo(ComponentName componentName,CacheEntry entry )
    {
    	if(componentName == null)
    		return false;
    	String className = componentName.getClassName();
    	Log.e(TAG, className);
    	if(className == null)
    		return false;
    	if(className.equals(audioClass)){
    		entry.icon = Utilities.createIconBitmap(mContext.getResources().getDrawable(R.drawable.icon_music),mContext);
    		entry.title = mContext.getResources().getString(R.string.music);
    		return true;
    	}
    	if(className.equals(photoClass)){
    		entry.icon = Utilities.createIconBitmap(mContext.getResources().getDrawable(R.drawable.icon_photos),mContext);
    		entry.title = mContext.getResources().getString(R.string.photo);
    		return true;
    	}
    	if(className.equals(videoClass)){
    		entry.icon = Utilities.createIconBitmap(mContext.getResources().getDrawable(R.drawable.icon_videos),mContext);
    		entry.title = mContext.getResources().getString(R.string.video);
    		return true;
    	}
    	if(className.equals(galleryClass)){
    		entry.icon = Utilities.createIconBitmap(mContext.getResources().getDrawable(R.drawable.icon_gallery),mContext);
    		//entry.title = mContext.getResources().getString(R.string.youtube);

    		return false;
    	}
    	if(className.equals(calendarClass)){
    		entry.icon = Utilities.createIconBitmap(mContext.getResources().getDrawable(R.drawable.icon_calendar),mContext);
    		//entry.title = mContext.getResources().getString(R.string.gmusic);
    		return false;
    	}
    	if(className.equals(browserClass)){
    		entry.icon = Utilities.createIconBitmap(mContext.getResources().getDrawable(R.drawable.icon_chrome),mContext);
    		//entry.title = mContext.getResources().getString(R.string.chrome);
    		return false;
    	}
    	if(className.equals(storeClass)){
    		entry.icon = Utilities.createIconBitmap(mContext.getResources().getDrawable(R.drawable.icon_play_store),mContext);
    		//entry.title = mContext.getResources().getString(R.string.store);

    		return false;
    	}
    	if(className.equals(miracastClass)){
    		entry.icon = Utilities.createIconBitmap(mContext.getResources().getDrawable(R.drawable.icon_play_miracast),mContext);
    		entry.title = mContext.getResources().getString(R.string.miracast);
    		return true;
    	}
    	if(className.equals(settingClass)){
    		entry.icon = Utilities.createIconBitmap(mContext.getResources().getDrawable(R.drawable.icon_setting),mContext);
    		entry.title = mContext.getResources().getString(R.string.setting);
    		return true;
    	}
    	if(className.equals(infoClass)){
    		entry.icon = Utilities.createIconBitmap(mContext.getResources().getDrawable(R.drawable.icon_info),mContext);
    		entry.title = mContext.getResources().getString(R.string.info);
    		return true;
    	}
    	return false;
    }
    
    private boolean getFixedInfo2(ComponentName componentName,CacheEntry entry )
    {
    	if(componentName == null)
    		return false;
    	String className = componentName.getClassName();
    	if(className == null)
    		return false;
    	if(className.equals(audioClass)){
    		entry.icon = Utilities.createIconBitmap2(mContext.getResources().getDrawable(R.drawable.icon_music),mContext);
    		entry.title = mContext.getResources().getString(R.string.music);
    		return true;
    	}
    	if(className.equals(photoClass)){
    		entry.icon = Utilities.createIconBitmap2(mContext.getResources().getDrawable(R.drawable.icon_photos),mContext);
    		entry.title = mContext.getResources().getString(R.string.photo);
    		return true;
    	}
    	if(className.equals(videoClass)){
    		entry.icon = Utilities.createIconBitmap2(mContext.getResources().getDrawable(R.drawable.icon_videos),mContext);
    		entry.title = mContext.getResources().getString(R.string.video);
    		return true;
    	}
    	if(className.equals(galleryClass)){
    		entry.icon = Utilities.createIconBitmap2(mContext.getResources().getDrawable(R.drawable.icon_gallery),mContext);
    		//entry.title = mContext.getResources().getString(R.string.youtube);

    		return false;
    	}
    	if(className.equals(calendarClass)){
    		entry.icon = Utilities.createIconBitmap2(mContext.getResources().getDrawable(R.drawable.icon_calendar),mContext);
    		//entry.title = mContext.getResources().getString(R.string.gmusic);
    		return false;
    	}
    	if(className.equals(browserClass)){
    		entry.icon = Utilities.createIconBitmap2(mContext.getResources().getDrawable(R.drawable.icon_chrome),mContext);
    		//entry.title = mContext.getResources().getString(R.string.chrome);
    		return false;
    	}
    	if(className.equals(storeClass)){
    		entry.icon = Utilities.createIconBitmap2(mContext.getResources().getDrawable(R.drawable.icon_play_store),mContext);
    		entry.title = mContext.getResources().getString(R.string.store);

    		return false;
    	}
    	if(className.equals(miracastClass)){
    		entry.icon = Utilities.createIconBitmap2(mContext.getResources().getDrawable(R.drawable.icon_play_miracast),mContext);
    		entry.title = mContext.getResources().getString(R.string.miracast);
    		return true;
    	}
    	if(className.equals(settingClass)){
    		entry.icon = Utilities.createIconBitmap2(mContext.getResources().getDrawable(R.drawable.icon_setting),mContext);
    		entry.title = mContext.getResources().getString(R.string.setting);
    		return true;
    	}
    	if(className.equals(infoClass)){
    		entry.icon = Utilities.createIconBitmap(mContext.getResources().getDrawable(R.drawable.icon_info),mContext);
    		entry.title = mContext.getResources().getString(R.string.info);
    		return true;
    	}
    	return false;
    }
    private CacheEntry cacheLocked2(ComponentName componentName, ResolveInfo info,
            HashMap<Object, CharSequence> labelCache) {
        CacheEntry entry = mCache.get(componentName);
        if (entry == null) {
            entry = new CacheEntry();

            mCache.put(componentName, entry);
			      if(getFixedInfo2(componentName,entry))
            {
            	return entry;
            }
            ComponentName key = LauncherModel.getComponentNameFromResolveInfo(info);
            if (labelCache != null && labelCache.containsKey(key)) {
                entry.title = labelCache.get(key).toString();
            } else {
                entry.title = info.loadLabel(mPackageManager).toString();
                if (labelCache != null) {
                    labelCache.put(key, entry.title);
                }
            }
            if (entry.title == null) {
                entry.title = info.activityInfo.name;
            }
            if(entry.icon == null)
            entry.icon = Utilities.createIconBitmap2(
                    getFullResIcon(info), mContext);
        }
        else
        {
        	if(entry.icon.getWidth() > Utilities.getsIconWidth2())
        	{
        		//Log.e(TAG, "remove icon");
        		mCache.remove(componentName);
                entry = new CacheEntry();

                mCache.put(componentName, entry);
            if(getFixedInfo2(componentName,entry))
            {
            	return entry;
            }
                ComponentName key = LauncherModel.getComponentNameFromResolveInfo(info);
                if (labelCache != null && labelCache.containsKey(key) && !componentName.getClassName().equals(infoClass)) {
                    entry.title = labelCache.get(key).toString();
                } else {
                    entry.title = info.loadLabel(mPackageManager).toString();
                    if (labelCache != null && !componentName.getClassName().equals(infoClass)) {
                        labelCache.put(key, entry.title);
                    }
                }
                if (entry.title == null) {
                    entry.title = info.activityInfo.name;
                }
                if(entry.icon == null)
                entry.icon = Utilities.createIconBitmap2(
                        getFullResIcon(info), mContext);
        	}
        }
        return entry;
    }

    public HashMap<ComponentName,Bitmap> getAllIcons() {
        synchronized (mCache) {
            HashMap<ComponentName,Bitmap> set = new HashMap<ComponentName,Bitmap>();
            for (ComponentName cn : mCache.keySet()) {
                final CacheEntry e = mCache.get(cn);
                set.put(cn, e.icon);
            }
            return set;
        }
    }
}

/*
 * Copyright (C) 2009 The Android Open Source Project
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

package com.haier.launcher2;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;

import com.haier.launcher.R;

/**
 * Specific {@link AppWidgetHost} that creates our {@link LauncherAppWidgetHostView} which correctly captures all
 * long-press events. This ensures that users can always pick up and move widgets.
 */
public class LauncherAppWidgetHost extends AppWidgetHost {
	private Launcher mLauncher;

	public LauncherAppWidgetHost(Context context, int hostId) {
		super(context, hostId);
		mLauncher = (Launcher) context;
	}
	
	@Override
	protected AppWidgetHostView onCreateView(Context context, int appWidgetId, AppWidgetProviderInfo appWidget) {
		// return new LauncherAppWidgetHostView(context);
		// emirguo add for widget get focus and onclick,20120301

		AppWidgetHostView appWidgetHostView = null;
		try {
			appWidgetHostView = new LauncherAppWidgetHostView(context);
			AppWidgetManager widgets = AppWidgetManager.getInstance(context);
			AppWidgetProviderInfo provider = widgets.getAppWidgetInfo(appWidgetId);

			final String packageName = provider.provider.getPackageName();
			final String className = provider.provider.getClassName();

			System.out.println("??????_LauncherAppWidgetHost.onCreateView.packageName:" + packageName);
			System.out.println("??????_LauncherAppWidgetHost.onCreateView.className:" + className);

			// 天气widget不需要获取焦点、焦点背景框
//			if (className.equals("com.haier.widgets.news.WeatherWidgetProvider")
//					&& packageName.equals("com.haier.widgets.news")) {
//				appWidgetHostView.setFocusable(false);
//				appWidgetHostView.setFocusableInTouchMode(false);
//				appWidgetHostView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
//			} else {
				setOnFocusChangeListener(appWidgetHostView);
				setOnClickListener(appWidgetHostView, packageName, className);

				if (className.equals("com.haier.showallapps.HaierTvEmptyWidget")
						&& packageName.equals("com.haier.showallapps")) {
					appWidgetHostView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
					appWidgetHostView.setFocusable(true);
					if (Launcher.isInWorkSpace()) {
						appWidgetHostView.requestFocus();
					}
					// appWidgetHostView.setBackgroundResource(R.drawable.focused_bg);
				}
//			}
		} catch (Exception e) {
			System.out.println("??????_LauncherAppWidgetHost.onCreateView.e.getLocalizedMessage()):" + e.getLocalizedMessage());
		}

		return appWidgetHostView;
	}

	private void setOnClickListener(AppWidgetHostView appWidgetHostView, final String packageName,
			final String className) {

		appWidgetHostView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				Log.i("Widget","??????_LauncherAppWidgetHost.setOnClickListener.packageName:" + packageName);
				Log.i("Widget","??????_LauncherAppWidgetHost.setOnClickListener.className:" + className);

				if (className.equals("com.haier.showallapps.HaierTvEmptyWidget")
						&& packageName.equals("com.haier.showallapps")) {
					if (Launcher.m_tvIsSmallWindows){
						mLauncher.GotoTVServer();
					}
				} else {
					for (ApplicationInfo app : AppsCustomizePagedView.mApps) {

						if (app.intent.getComponent().getPackageName().equals(packageName)) {

							Launcher.mstarTvChangeSource(packageName);
							view.getContext().startActivity(app.intent);
							break;
						}
						/* 新闻 */
						else if ("com.haier.widgets.news".equals(packageName)
								&& "com.haier.widgets.news.NewsWidgetProvider".equals(className)) {

							ComponentName comp = new ComponentName("com.haier.news.activity",
									"com.haier.news.activity.NewsList");
							Intent intent = new Intent();
							intent.setComponent(comp);
							intent.setAction("android.intent.action.MAIN");
							Launcher.mstarTvChangeSource(packageName);
							view.getContext().startActivity(intent);
							break;
						}
						/* 百视 */
						else if ("com.haier.widgets.news".equals(packageName)
								&& "com.haier.widgets.news.BestvWidgetProvider".equals(className)) {

							ComponentName comp = new ComponentName("com.bestv.ctv", "com.bestv.ctv.MainActivity");
							Intent intent = new Intent();
							intent.setComponent(comp);
							intent.setAction("android.intent.action.MAIN");
							Launcher.mstarTvChangeSource(packageName);
							view.getContext().startActivity(intent);
							break;
						}
						/* 云服务 */
						else if ("com.haiertv.homewgt.cloudservice".equals(packageName)
								&& "com.haiertv.homewgt.cloudservice.AppWidget".equals(className)) {

							ComponentName comp = new ComponentName("com.haierubic.tv.cloud",
									"com.haierubic.tv.cloud.HaierTVCloudLifeActivity");
							Intent intent = new Intent();
							intent.setComponent(comp);
							intent.setAction("android.intent.action.MAIN");
							Launcher.mstarTvChangeSource(packageName);
							view.getContext().startActivity(intent);
							break;
						}
						/* 应用推荐 */
						else if ("com.haier.homecloudlive".equals(packageName)
								&& "com.haier.homecloudlive.RecommendAPPProvider".equals(className)) {
							
							ComponentName comp = new ComponentName("cn.haier.haier.tv7520.vstoresubclient",
									"cn.haier.haier.tv7520.vstoresubclient.MainActivity");
							Intent intent = new Intent();
							intent.setComponent(comp);
							intent.setAction("android.intent.action.MAIN");
							Launcher.mstarTvChangeSource(packageName);
							view.getContext().startActivity(intent);
							break;
						}
						/*天气weather*/
						else if ("com.haier.widgets.news".equals(packageName)
								&& "com.haier.widgets.news.WeatherWidgetProvider".equals(className)){
							ComponentName comp = new ComponentName("com.haier.weather.activity",
									"com.haier.weather.activity.LoadingActivity");
							Intent intent = new Intent();
							intent.setComponent(comp);
							intent.setAction("android.intent.action.MAIN");
							Launcher.mstarTvChangeSource(packageName);
							view.getContext().startActivity(intent);
							break;
						}
							
					}
				}
			}
		});
	}

	private void setOnFocusChangeListener(AppWidgetHostView appWidgetHostView) {
//		appWidgetHostView.setFocusable(true);
//		appWidgetHostView.setFocusableInTouchMode(true);
		appWidgetHostView.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean isFocus) {

				if (isFocus) {
					view.setBackgroundResource(R.drawable.focused_bg);
				} else {
					view.setBackgroundColor(Color.TRANSPARENT);
					LauncherAppWidgetInfo info = (LauncherAppWidgetInfo)view.getTag();
					if ((info.screen == 1) && (info.cellX == 0) && (info.cellY == 1)){ // tv
						view.setFocusable(true);
					} else {
						view.setFocusable(false);
					}
				}
			}
		});
	}

	@Override
	public void stopListening() {
		super.stopListening();
		clearViews();
	}
}

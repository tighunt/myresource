/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package org.apache.cordova.example;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceView;
import android.widget.AbsoluteLayout;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;

import org.apache.cordova.*;

public class cordovaExample extends DroidGap
{
	public static SurfaceView suf;
	public int top =300;
	public int left = 300;
	RelativeLayout rt;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.loadUrl("file:///android_asset/www/index.html");
        rt = new RelativeLayout(this);
        LayoutParams ll = new LayoutParams(800,650);
        this.addContentView(rt,ll);
        suf =new SurfaceView(this);
        rt.addView(suf, 100, 100);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)suf.getLayoutParams();
        params.leftMargin =left;
        params.topMargin =top;
        params.height =100;
        suf.setLayoutParams(params);
       //RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)suf.getLayoutParams();
       //lp.addRule(RelativeLayout.ALIGN_LEFT,10);
       //lp.leftMargin =10;
       //lp.height =100;
        //suf.setLayoutParams(lp);
    }
}


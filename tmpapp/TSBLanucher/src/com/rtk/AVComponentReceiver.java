/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.rtk;

import com.rtk.rtkTv;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * Takes care of setting initial wallpaper for a user, by selecting the
 * first wallpaper that is not in use by another user.
 */
public class AVComponentReceiver extends BroadcastReceiver {
    
    private final static String TAG = "AVComponentReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();       
        //Log.d(TAG, "onReceive --> " + action); 
        if (action.equals("com.broadcast.tv.ext1SigChange")) { 
            rtkTv.getInstance().syncAVYpp();
            Log.d(TAG, "onReceive --> " + action); 
        }
    }   
}

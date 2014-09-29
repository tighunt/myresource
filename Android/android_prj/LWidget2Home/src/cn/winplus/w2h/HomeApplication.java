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

package cn.winplus.w2h;

import android.app.Application;
import android.app.ProgressDialog;

import android.content.ComponentName;

import android.content.Intent;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import android.os.Handler;
import android.os.Message;
import android.content.res.Configuration;

import java.util.ArrayList;
import java.util.List;


public class HomeApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

    }

    /**
     * There's no guarantee that this function is ever called.
     */
    @Override
    public void onTerminate() {
        super.onTerminate();

    }

}

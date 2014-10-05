/*
 * Copyright (C) 2011 The Android Open Source Project
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

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.tsb.launcher.R;

public class Hotseat extends FrameLayout {
    @SuppressWarnings("unused")
    private static final String TAG = "Hotseat";

    private Launcher mLauncher;
    private CellLayout mContent;

    private int mCellCountX;
    private int mCellCountY;
    private int mAllAppsButtonRank;

    private boolean mTransposeLayoutWithOrientation;
    private boolean mIsLandscape;

    public Hotseat(Context context) {
        this(context, null);
    }

    public Hotseat(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Hotseat(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.Hotseat, defStyle, 0);
        Resources r = context.getResources();
        mCellCountX = a.getInt(R.styleable.Hotseat_cellCountX, -1);
        mCellCountY = a.getInt(R.styleable.Hotseat_cellCountY, -1);
        mAllAppsButtonRank = r.getInteger(R.integer.hotseat_all_apps_index);
        mTransposeLayoutWithOrientation = 
                r.getBoolean(R.bool.hotseat_transpose_layout_with_orientation);
        mIsLandscape = context.getResources().getConfiguration().orientation ==
            Configuration.ORIENTATION_LANDSCAPE;
    }

    public void setup(Launcher launcher) {
        mLauncher = launcher;
        setOnKeyListener(new HotseatIconKeyEventListener());
    }

    CellLayout getLayout() {
        return mContent;
    }
  
    private boolean hasVerticalHotseat() {
        return (mIsLandscape && mTransposeLayoutWithOrientation);
    }

    /* Get the orientation invariant order of the item in the hotseat for persistence. */
    int getOrderInHotseat(int x, int y) {
        return hasVerticalHotseat() ? (mContent.getCountY() - y - 1) : x;
    }
    /* Get the orientation specific coordinates given an invariant order in the hotseat. */
    int getCellXFromOrder(int rank) {
        return hasVerticalHotseat() ? 0 : rank;
    }
    int getCellYFromOrder(int rank) {
        return hasVerticalHotseat() ? (mContent.getCountY() - (rank + 1)) : 0;
    }
    public boolean isAllAppsButtonRank(int rank) {
        return rank == mAllAppsButtonRank;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mCellCountX < 0) mCellCountX = LauncherModel.getCellCountX();
        if (mCellCountY < 0) mCellCountY = LauncherModel.getCellCountY();
        mContent = (CellLayout) findViewById(R.id.layout);
        mContent.setGridSize(mCellCountX, mCellCountY);
        mContent.setIsHotseat(true);

        resetLayout();
    }

    void resetLayout() {
        mContent.removeAllViewsInLayout();

        // Add the Apps button
        Context context = getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        BubbleTextView allAppsButton = (BubbleTextView)
                inflater.inflate(R.layout.hotseat_application, mContent, false);
        //allAppsButton.setCompoundDrawablesWithIntrinsicBounds(null,
                //context.getResources().getDrawable(R.drawable.all_apps_button_icon), null, null);
        allAppsButton.setBackground(context.getResources().getDrawable(R.drawable.all_apps_button_icon));
        allAppsButton.setContentDescription(context.getString(R.string.all_apps_button_label));
        allAppsButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mLauncher != null &&
                    (event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                    mLauncher.onTouchDownAllAppsButton(v);
                }
                return false;
            }
        });

        allAppsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (mLauncher != null) {
                    mLauncher.onClickAllAppsButton(v);
                }
            }
        });

        // Note: We do this to ensure that the hotseat is always laid out in the orientation of
        // the hotseat in order regardless of which orientation they were added
        int x = getCellXFromOrder(mAllAppsButtonRank);
        int y = getCellYFromOrder(mAllAppsButtonRank);
        CellLayout.LayoutParams lp = new CellLayout.LayoutParams(x,y,1,1);
        lp.canReorder = false;
        mContent.addViewToCellLayout(allAppsButton, -1, 0, lp, true);
        
        // Add the tvSetting button

        BubbleTextView tvSetting = (BubbleTextView)
                inflater.inflate(R.layout.hotseat_application, mContent, false);
        tvSetting.setBackground(
                context.getResources().getDrawable(R.drawable.tv_setting));
        tvSetting.setContentDescription(context.getString(R.string.all_apps_button_label));
        tvSetting.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mLauncher != null &&
                    (event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                    mLauncher.onClickTvSetting(v);
                }
                return false;
            }
        });

        tvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (mLauncher != null) {
                    mLauncher.onClickTvSetting(v);
                }
            }
        });

        // Note: We do this to ensure that the hotseat is always laid out in the orientation of
        // the hotseat in order regardless of which orientation they were added
         x = getCellXFromOrder(0);
         y = getCellYFromOrder(0);
         lp = new CellLayout.LayoutParams(x,y,1,1);
        lp.canReorder = false;
        mContent.addViewToCellLayout(tvSetting, -1, 0, lp, true);
        
        
     // Add the tv Apps button

        BubbleTextView tvButton = (BubbleTextView)
                inflater.inflate(R.layout.hotseat_application, mContent, false);
        tvButton.setBackground(
                context.getResources().getDrawable(R.drawable.atv));
        tvButton.setContentDescription(context.getString(R.string.all_apps_button_label));
        tvButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mLauncher != null &&
                    (event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                    mLauncher.onClickTvButton(v);
                }
                return false;
            }
        });

        tvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (mLauncher != null) {
                    mLauncher.onClickTvButton(v);
                }
            }
        });

        // Note: We do this to ensure that the hotseat is always laid out in the orientation of
        // the hotseat in order regardless of which orientation they were added
         x = getCellXFromOrder(1);
         y = getCellYFromOrder(1);
         lp = new CellLayout.LayoutParams(x,y,1,1);
        lp.canReorder = false;
        mContent.addViewToCellLayout(tvButton, -1, 0, lp, true);
        
        // Add the player Apps button

        BubbleTextView playerButton = (BubbleTextView)
                inflater.inflate(R.layout.hotseat_application, mContent, false);
        playerButton.setBackground(
                context.getResources().getDrawable(R.drawable.player));
        playerButton.setContentDescription(context.getString(R.string.all_apps_button_label));
        playerButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mLauncher != null &&
                    (event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                    mLauncher.onClickPlayerButton(v);
                }
                return false;
            }
        });

        playerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (mLauncher != null) {
                    mLauncher.onClickPlayerButton(v);
                }
            }
        });

        // Note: We do this to ensure that the hotseat is always laid out in the orientation of
        // the hotseat in order regardless of which orientation they were added
         x = getCellXFromOrder(2);
         y = getCellYFromOrder(2);
         lp = new CellLayout.LayoutParams(x,y,1,1);
        lp.canReorder = false;
        mContent.addViewToCellLayout(playerButton, -1, 0, lp, true);
        
        
        // Add the vending Apps button

        BubbleTextView vendingButton = (BubbleTextView)
                inflater.inflate(R.layout.hotseat_application, mContent, false);
        vendingButton.setBackground(
                context.getResources().getDrawable(R.drawable.google_play));
        vendingButton.setContentDescription(context.getString(R.string.all_apps_button_label));
        vendingButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mLauncher != null &&
                    (event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                    mLauncher.onClickVendingButton(v);
                }
                return false;
            }
        });

        vendingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (mLauncher != null) {
                    mLauncher.onClickVendingButton(v);
                }
            }
        });

        // Note: We do this to ensure that the hotseat is always laid out in the orientation of
        // the hotseat in order regardless of which orientation they were added
         x = getCellXFromOrder(3);
         y = getCellYFromOrder(3);
         lp = new CellLayout.LayoutParams(x,y,1,1);
        lp.canReorder = false;
        mContent.addViewToCellLayout(vendingButton, -1, 0, lp, true);
        
        // Add the browser Apps button

        BubbleTextView browserButton = (BubbleTextView)
                inflater.inflate(R.layout.hotseat_application, mContent, false);
        browserButton.setBackground(
                context.getResources().getDrawable(R.drawable.chrome));
        browserButton.setContentDescription(context.getString(R.string.all_apps_button_label));
        browserButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mLauncher != null &&
                    (event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                    mLauncher.onClickBrowserButton(v);
                }
                return false;
            }
        });

        browserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (mLauncher != null) {
                    mLauncher.onClickBrowserButton(v);
                }
            }
        });

        // Note: We do this to ensure that the hotseat is always laid out in the orientation of
        // the hotseat in order regardless of which orientation they were added
         x = getCellXFromOrder(5);
         y = getCellYFromOrder(5);
         lp = new CellLayout.LayoutParams(x,y,1,1);
        lp.canReorder = false;
        mContent.addViewToCellLayout(browserButton, -1, 0, lp, true);
        
        BubbleTextView settingsButton = (BubbleTextView)
                inflater.inflate(R.layout.hotseat_application, mContent, false);
        settingsButton.setBackground(
                context.getResources().getDrawable(R.drawable.setting));
        settingsButton.setContentDescription(context.getString(R.string.all_apps_button_label));
        settingsButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mLauncher != null &&
                    (event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                    mLauncher.onClickSettingsButton(v);
                }
                return false;
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (mLauncher != null) {
                    mLauncher.onClickSettingsButton(v);
                }
            }
        });

        // Note: We do this to ensure that the hotseat is always laid out in the orientation of
        // the hotseat in order regardless of which orientation they were added
         x = getCellXFromOrder(6);
         y = getCellYFromOrder(6);
         lp = new CellLayout.LayoutParams(x,y,1,1);
        lp.canReorder = false;
        mContent.addViewToCellLayout(settingsButton, -1, 0, lp, true);
        
        //remove focus anim
        /*
        allAppsButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {        
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                    // TODO Auto-generated method stub
                    if(hasFocus){
                    	int[] pos =new int[2];
                    	v.getLocationOnScreen(pos);
                    	mLauncher.sendFocusMessage(pos[0], pos[1], v.getWidth(), v.getHeight(),1);                           
                    }
            }
        });
        tvButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {        
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                    // TODO Auto-generated method stub
                    if(hasFocus){
                    	int[] pos =new int[2];
                    	v.getLocationOnScreen(pos);
                    	mLauncher.sendFocusMessage(pos[0], pos[1], v.getWidth(), v.getHeight(),1);                           
                    }
            }
        });
        playerButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {        
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                    // TODO Auto-generated method stub
                    if(hasFocus){
                    	int[] pos =new int[2];
                    	v.getLocationOnScreen(pos);
                    	mLauncher.sendFocusMessage(pos[0], pos[1], v.getWidth(), v.getHeight(),1);                           
                    }
            }
        });
        vendingButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {        
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                    // TODO Auto-generated method stub
                    if(hasFocus){
                    	int[] pos =new int[2];
                    	v.getLocationOnScreen(pos);
                    	mLauncher.sendFocusMessage(pos[0], pos[1], v.getWidth(), v.getHeight(),1);                           
                    }
            }
        });
        browserButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {        
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                    // TODO Auto-generated method stub
                    if(hasFocus){
                    	int[] pos =new int[2];
                    	v.getLocationOnScreen(pos);
                    	mLauncher.sendFocusMessage(pos[0], pos[1], v.getWidth(), v.getHeight(),1);                           
                    }
            }
        });
        settingsButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {        
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                    // TODO Auto-generated method stub
                    if(hasFocus){
                    	int[] pos =new int[2];
                    	v.getLocationOnScreen(pos);
                    	mLauncher.sendFocusMessage(pos[0], pos[1], v.getWidth(), v.getHeight(),1);                           
                    }
            }
        });*/
    }
}

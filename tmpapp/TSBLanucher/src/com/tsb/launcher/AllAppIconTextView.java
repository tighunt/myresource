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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * TextView that draws a bubble behind the text. We cannot use a LineBackgroundSpan
 * because we want to make the bubble taller than the text and TextView's clip is
 * too aggressive.
 */
public class AllAppIconTextView extends LinearLayout  {
    static final float CORNER_RADIUS = 4.0f;
    static final float SHADOW_LARGE_RADIUS = 4.0f;
    static final float SHADOW_SMALL_RADIUS = 1.75f;
    static final float SHADOW_Y_OFFSET = 2.0f;
    static final int SHADOW_LARGE_COLOUR = 0xDD000000;
    static final int SHADOW_SMALL_COLOUR = 0xCC000000;
    static final float PADDING_H = 8.0f;
    static final float PADDING_V = 0.0f;
    private int mPrevAlpha = -1;
    private Bitmap mPressedOrFocusedBackground;

    private Drawable mBackground;


    private CheckLongPressHelper mLongPressHelper;
    private String txtBack ="";
    
    private TextView mText;
    private ImageView mIcon;

    public AllAppIconTextView(Context context) {
        super(context);
        init(context);
    }

    public AllAppIconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AllAppIconTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mLongPressHelper = new CheckLongPressHelper(this);
        mBackground = getBackground();
        this.setOrientation(VERTICAL);
        this.setGravity(Gravity.CENTER_HORIZONTAL);
        int size = context.getResources().getInteger(R.integer.allapp_icon_size);
        mIcon = new ImageView(context);
        	addView(mIcon,  new LinearLayout.LayoutParams(
        			size,size));
        mText = new TextView(context);
        addView(mText, new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        mText.setTextAppearance(context, R.style.WorkspaceIcon);
        mText.setFocusable(true);

    }

    public void applyFromShortcutInfo(ShortcutInfo info, IconCache iconCache) {
        Bitmap b = info.getIcon(iconCache);
        setIcon(new FastBitmapDrawable(b));
        //setCompoundDrawablesWithIntrinsicBounds(null,
                //new FastBitmapDrawable(b),
                //null, null);
        //setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
        setViewText(info.title);
        txtBack = (String) info.title;
        setTag(info);
    }
    
    public void applyFromApplicationInfo(ApplicationInfo info, boolean scaleUp,
            PagedViewIcon.PressedCallback cb) {
        Bitmap b = info.iconBitmap;
        setIcon(new FastBitmapDrawable(b));
        //setCompoundDrawablesWithIntrinsicBounds(null,
                //new FastBitmapDrawable(b),
                //null, null);
        //setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
        setViewText(info.title);
        txtBack = (String) info.title;
        setTag(info);
    }
    
    public void setViewText(CharSequence title) {
        mText.setText(title);
    }
    public void setIcon(Drawable bullet) {
        mIcon.setImageDrawable(bullet);
    }
    
    public Drawable getIcon() {
        return mIcon.getDrawable();
    }
    
    public void setBitmap(Bitmap b ) {
    	if(b == null)
    		//setCompoundDrawablesWithIntrinsicBounds(null,
                    //null,
                   // null, null);
    		setIcon(null);
    	else
    		//setCompoundDrawablesWithIntrinsicBounds(null,
                    //new FastBitmapDrawable(b),
                    //null, null);
    		setIcon(new FastBitmapDrawable(b));
    }



    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mBackground || super.verifyDrawable(who);
    }

    @Override
    public void setTag(Object tag) {
        if (tag != null) {
            LauncherModel.checkItemInfo((ItemInfo) tag);
        }
        super.setTag(tag);
    }
 
    void clearPressedOrFocusedBackground() {
        mPressedOrFocusedBackground = null;
        //setCellLayoutPressedOrFocusedIcon();
    }

    Bitmap getPressedOrFocusedBackground() {
        return mPressedOrFocusedBackground;
    }

    int getPressedOrFocusedBackgroundPadding() {
        return HolographicOutlineHelper.MAX_OUTER_BLUR_RADIUS / 2;
    }

   
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mBackground != null) mBackground.setCallback(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mBackground != null) mBackground.setCallback(null);
    }

    @Override
    protected boolean onSetAlpha(int alpha) {
        if (mPrevAlpha != alpha) {
            mPrevAlpha = alpha;
            super.onSetAlpha(alpha);
        }
        return true;
    }

    @Override
    public void cancelLongPress() {
        super.cancelLongPress();

        mLongPressHelper.cancelLongPress();
    }

	public String getTxtBack() {
		return txtBack;
	}
	
	public CharSequence getText() {
		return mText.getText();
	}

	public void setTxtBack(String txtBack) {
		this.txtBack = txtBack;
	}
	
	public void restoreTxt(){
		this.setViewText(txtBack);
	}
	
	public void setTextColor(int color){
		mText.setTextColor(color);
	}

	public void setText(String string) {
		// TODO Auto-generated method stub
		mText.setText(string);
	}
}

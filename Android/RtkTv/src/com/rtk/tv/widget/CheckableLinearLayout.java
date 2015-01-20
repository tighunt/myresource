
package com.rtk.tv.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.LinearLayout;

public class CheckableLinearLayout extends LinearLayout implements Checkable {
    private boolean checked;
    private static final int[] CheckedStateSet = {
            android.R.attr.state_checked
    };

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    protected  void setChildCheckedRecrusive(ViewGroup view, boolean checked){

        final int count = view.getChildCount();
        for( int i = 0; i< count; i++){
            final View child = view.getChildAt(i);
            if(child instanceof  ViewGroup){
                if(child instanceof Checkable)
                    ((Checkable) child).setChecked(checked);
                setChildCheckedRecrusive((ViewGroup)child,checked);
            }
            else{
                if(child instanceof Checkable){
                    ((Checkable) child).setChecked(checked);
                }
            }
        }
    }
    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
        if(checked){
        setChildCheckedRecrusive(this,checked);
        refreshDrawableState();
        }
    }

    @Override
    public void toggle() {
        setChecked(!checked);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CheckedStateSet);
        }
        return drawableState;
    }

    @Override
    public boolean performClick() {
        toggle();
        return super.performClick();
    }
}


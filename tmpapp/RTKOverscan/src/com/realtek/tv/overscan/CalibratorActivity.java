
package com.realtek.tv.overscan;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.view.DisplayInfo;
import android.view.IWindowManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManagerGlobal;

public class CalibratorActivity extends Activity {

    private static final float MAX_SCAN = 0.05F;

    private View mViewUp;
    private View mViewDown;
    private View mViewLeft;
    private View mViewRight;

    IWindowManager mWindowManager;

    private DisplayInfo mDisplay;

    private int mMaxOverscanHorizontal;
    private int mMaxOverscanVertical;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calibrator);
        mViewUp = findViewById(R.id.up);
        mViewDown = findViewById(R.id.down);
        mViewLeft = findViewById(R.id.left);
        mViewRight = findViewById(R.id.right);

        mDisplay = new DisplayInfo();
        getWindowManager().getDefaultDisplay().getDisplayInfo(mDisplay);

        DisplayMetrics metrics = new DisplayMetrics();
        mDisplay.getAppMetrics(metrics);
        mMaxOverscanHorizontal = (int) (metrics.noncompatWidthPixels * MAX_SCAN);
        mMaxOverscanVertical = (int) (metrics.noncompatHeightPixels * MAX_SCAN);
        mWindowManager = WindowManagerGlobal.getWindowManagerService();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                mViewUp.setSelected(true);
                mViewDown.setSelected(true);
                mDisplay.overscanTop -= 1;
                mDisplay.overscanBottom -= 1;
                if (mDisplay.overscanTop <= 0 || mDisplay.overscanBottom <= 0) {
                    mDisplay.overscanTop = 0;
                    mDisplay.overscanBottom = 0;
                }
                setOverscan();
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                mViewUp.setSelected(true);
                mViewDown.setSelected(true);
                mDisplay.overscanTop += 1;
                mDisplay.overscanBottom += 1;
                if (mDisplay.overscanTop > mMaxOverscanVertical
                        || mDisplay.overscanBottom > mMaxOverscanVertical) {
                    mDisplay.overscanTop = mMaxOverscanVertical;
                    mDisplay.overscanBottom = mMaxOverscanVertical;
                }
                setOverscan();
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            	mViewLeft.setSelected(true);
                mViewRight.setSelected(true);
                mDisplay.overscanLeft += 1;
                mDisplay.overscanRight += 1;
                if (mDisplay.overscanLeft > mMaxOverscanHorizontal
                        || mDisplay.overscanRight > mMaxOverscanHorizontal) {
                    mDisplay.overscanLeft = mMaxOverscanHorizontal;
                    mDisplay.overscanRight = mMaxOverscanHorizontal;
                }
                setOverscan();
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                mViewLeft.setSelected(true);
                mViewRight.setSelected(true);
                mDisplay.overscanLeft -= 1;
                mDisplay.overscanRight -= 1;
                if (mDisplay.overscanLeft <= 0 || mDisplay.overscanRight <= 0) {
                    mDisplay.overscanLeft = 0;
                    mDisplay.overscanRight = 0;
                }
                setOverscan();
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                finish();
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                mViewUp.setSelected(false);
                mViewDown.setSelected(false);
                mViewLeft.setSelected(false);
                mViewRight.setSelected(false);
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    private void setOverscan() {
        try {
            mWindowManager.setOverscan(0, mDisplay.overscanLeft, mDisplay.overscanTop,
                    mDisplay.overscanRight, mDisplay.overscanBottom);
        } catch (RemoteException e) {
        }
    }
}

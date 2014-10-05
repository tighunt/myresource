
package com.realtek.tv.overscan;

import android.app.Activity;
import android.app.TvManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

public class BoxCalibratorActivity extends Activity {

    private static final String TAG = "Overscan";

    private static final int MAX_SCAN = 0;
    private static final int MIN_SCAN = -50;
    private static final int STEP_SCAN = 5;

    private View mViewUp;
    private View mViewDown;
    private View mViewLeft;
    private View mViewRight;

    private TvManager mTvManager;

    private int mOverscanHorizontal;
    private int mOverscanVertical;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calibrator);
        mViewUp = findViewById(R.id.up);
        mViewDown = findViewById(R.id.down);
        mViewLeft = findViewById(R.id.left);
        mViewRight = findViewById(R.id.right);

        mTvManager = (TvManager) getSystemService(TV_SERVICE);

        mOverscanHorizontal = mTvManager.getBoxZoom(0, TvManager.ZOOM_DIRECTION_LEFT);
        mOverscanVertical = mTvManager.getBoxZoom(0, TvManager.ZOOM_DIRECTION_TOP);
        Log.v(TAG, String.format("left=%d, top=%d", mOverscanHorizontal, mOverscanVertical));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                mViewUp.setSelected(true);
                mViewDown.setSelected(true);
                if (mOverscanVertical > MIN_SCAN) {
                    setOverscan(0, -STEP_SCAN, 0, -STEP_SCAN);
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                mViewUp.setSelected(true);
                mViewDown.setSelected(true);
                if (mOverscanVertical < MAX_SCAN) {
                    setOverscan(0, STEP_SCAN, 0, STEP_SCAN);
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                mViewLeft.setSelected(true);
                mViewRight.setSelected(true);
                if (mOverscanHorizontal > MIN_SCAN) {
                    setOverscan(-STEP_SCAN, 0, -STEP_SCAN, 0);
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                mViewLeft.setSelected(true);
                mViewRight.setSelected(true);
                if (mOverscanHorizontal < MAX_SCAN) {
                    setOverscan(STEP_SCAN, 0, STEP_SCAN, 0);
                }
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

    private void setOverscan(int left, int top, int right, int bottom) {
        mTvManager.setBoxZoom(0, TvManager.ZOOM_DIRECTION_TOP, top);
        mTvManager.setBoxZoom(0, TvManager.ZOOM_DIRECTION_BOTTOM, bottom);
        mTvManager.setBoxZoom(0, TvManager.ZOOM_DIRECTION_LEFT, left);
        mTvManager.setBoxZoom(0, TvManager.ZOOM_DIRECTION_RIGHT, right);
        
        mOverscanHorizontal = mTvManager.getBoxZoom(0, TvManager.ZOOM_DIRECTION_LEFT);
        mOverscanVertical = mTvManager.getBoxZoom(0, TvManager.ZOOM_DIRECTION_TOP);
        Log.v(TAG, String.format("left=%d, top=%d", mOverscanHorizontal, mOverscanVertical));
        
        mTvManager.setBoxZoomBoot(true);
    }
}

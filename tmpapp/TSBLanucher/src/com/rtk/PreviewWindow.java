package com.rtk;

import android.app.TvManager;
import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.View.OnLayoutChangeListener;

public class PreviewWindow {
    
    private static final boolean VERBOSE = true;
    private static final String TAG = "PreviewWindow";

    // Preview status
    private final Rect mPreviewRect = new Rect();
    private boolean mIsVideoAreaOn = false;

    private boolean mBypassSetSource = false;

    // Source
    private int mPreviewSource;

    // View
    private View mViewPreview;
    private View mViewDecor;

    private final TvManager mTv;

    public PreviewWindow(Context context) {
        mTv = (TvManager) context.getSystemService(Context.TV_SERVICE);
        mPreviewSource = mTv.getCurLiveSource();
    }

    /**
     * onResume
     * @param source
     */
    public void updateSource(int source) {
        if (VERBOSE) {
            Log.v(TAG, "updateSource: src=" + source);
        }
        
        if (!mBypassSetSource) {
            if (!mPreviewRect.isEmpty()) {
                mTv.setSourceAndDisplayWindow(source,
                        mPreviewRect.left, mPreviewRect.top, mPreviewRect.width(), mPreviewRect.height());
            } else {
                // Not layout yet.
                if (VERBOSE) {
                    Log.v(TAG, "setSourceAndDisplayWindow: Rect is empty");
                }
            }
        }
        
        mPreviewSource = source;
    }

    /**
     * onLayoutChanged
     * @param r
     */
    private void updateDisplayWindowRect(Rect r) {
        if (VERBOSE) {
            Log.v(TAG, "updateDisplayWindowRect: " + r.toShortString());
        }
        
        if (r.equals(mPreviewRect)) {
            if (VERBOSE) {
                Log.v(TAG, "updateDisplayWindowRect: Rect is not changed");
            }
            return;
        }
        
        mPreviewRect.set(r);
        
        if (!mBypassSetSource) {
            mTv.setSourceAndDisplayWindow(mPreviewSource,
                    mPreviewRect.left, mPreviewRect.top, mPreviewRect.width(), mPreviewRect.height());
        }
        if (mIsVideoAreaOn) {
            mTv.setVideoAreaOn(mPreviewRect.left, mPreviewRect.top, mPreviewRect.width(), mPreviewRect.height(), 0);
        }
    }

    public void resetSourceAndDisplayWindow() {
        mTv.setSourceAndDisplayWindow(mPreviewSource,
                mPreviewRect.left, mPreviewRect.top, mPreviewRect.width(), mPreviewRect.height());
    }

    public void hide() {
        mTv.setVideoAreaOff(0);
        mIsVideoAreaOn = false;
    }

    public void show() {
        mTv.setVideoAreaOn(mPreviewRect.left, mPreviewRect.top, mPreviewRect.width(), mPreviewRect.height(), 0);
        mIsVideoAreaOn = true;
    }

    public void setBypassSetSource(boolean bypass) {
        mBypassSetSource = bypass;
        if (VERBOSE) {
            Log.v(TAG, "setBypassSetRatio: " + bypass);
        }
    }

    public void bindView(View window, View decor, int currentSource) {
        // Make sure previous one is unbind
        unbindView();

        mViewPreview = window;
        mViewDecor = decor;
        mViewPreview.addOnLayoutChangeListener(mOnPreviewLayoutChange);
        updateSource(currentSource);
        updateRectByView();
    }

    public void unbindView() {
        if (mViewPreview == null) {
            return;
        }
        mViewPreview.removeOnLayoutChangeListener(mOnPreviewLayoutChange);
        mViewPreview = null;
        mViewDecor = null;
        mPreviewRect.setEmpty();
    }
    
    private OnLayoutChangeListener mOnPreviewLayoutChange = new OnLayoutChangeListener() {
        
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
                int oldTop, int oldRight, int oldBottom) {
            updateRectByView();
        }
    };
    
    private void updateRectByView() {
        if (mViewPreview.getVisibility() == View.VISIBLE) {
            Rect r = new Rect();
            if(mViewPreview.getGlobalVisibleRect(r)) {
                r.bottom -= mViewDecor.getHeight();
                updateDisplayWindowRect(r);
            } else {
                Log.e(TAG, "Failed to update preview position");
            }
        } else {
            hide();
        }
    }
}

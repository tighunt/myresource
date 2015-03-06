package com.haier.launcher2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

public class FolderEditText extends EditText {

    private Folder mFolder;
    private View mLastFocusView = null;

    public FolderEditText(Context context) {
        super(context);
    }

    public FolderEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FolderEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setFolder(Folder folder) {
        mFolder = folder;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        // Catch the back button on the soft keyboard so that we can just close the activity
        if (event.getKeyCode() == android.view.KeyEvent.KEYCODE_BACK) {
            mFolder.doneEditingFolderName(true);
        }
        return super.onKeyPreIme(keyCode, event);
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		//System.out.println("FolderEditText.java, onKeyDown keyCode=" + keyCode);
		if (KeyEvent.KEYCODE_DPAD_DOWN == keyCode) {
			if (mLastFocusView != null) {
				mLastFocusView.requestFocus();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void setLastFocusView(View v) {
		mLastFocusView = v;
	}
    
    
}

package com.rtk.dmp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Message;

public class AndroidMessageBox {	//only call in Main UI thread
	static final int alertMessageOnly_type1 = 0x11;
	static final int alertMessageWithOkCancel1 = 0x21;
	
	static private final int MSG_DELAY_DISMISS = 0x1;
	
	public static void alertMessageOnly(Context mContext, String message, int type, int delay) {
		Handler H = null;
		switch (type) {
			case alertMessageOnly_type1: {
				final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
				alertDialog.setMessage(message);
				alertDialog.show();
				if(delay > 0) {
					H = new Handler(mContext.getMainLooper()) {
						@Override
						public void dispatchMessage(Message msg) {
							// TODO Auto-generated method stub
							if(msg.what == MSG_DELAY_DISMISS) {
								if(alertDialog != null && alertDialog.isShowing())
									alertDialog.cancel();
							}
							super.dispatchMessage(msg);
						}
					};
					H.sendEmptyMessageDelayed(MSG_DELAY_DISMISS, delay);
				}
			} break;
			default: {
			} break;
		}
	}
	
	public static void alertMessageWithOkCancel(Context mContext, String message, int type, int delay,
			String buttonText, OnClickListener buttonListener,
			String button2Text, OnClickListener button2Listener) {
		Handler H = null;
		switch(type) {
			case alertMessageWithOkCancel1: {
				final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
				alertDialog.setMessage(message);
				alertDialog.setButton(buttonText, buttonListener);
				alertDialog.setButton2(button2Text, button2Listener);
				alertDialog.show();
				if(delay > 0) {
					H = new Handler(mContext.getMainLooper()) {
						@Override
						public void dispatchMessage(Message msg) {
							// TODO Auto-generated method stub
							if(msg.what == MSG_DELAY_DISMISS) {
								if(alertDialog != null && alertDialog.isShowing())
									alertDialog.cancel();
							}
							super.dispatchMessage(msg);
						}
					};
					H.sendEmptyMessageDelayed(MSG_DELAY_DISMISS, delay);
				}
			} break;
			default:{
				
			} break;
		}
	}
}

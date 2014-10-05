package com.realtek.sync;

import android.util.Log;

public class BinarySemaphore {
	String TAG = "BinarySemaphore";
	private boolean locked = false;
	BinarySemaphore(int initial) {
		locked = (initial == 0);
	}
	public synchronized void waitForNotify() throws InterruptedException {
		while (locked) {
			Log.e(TAG, "start to wait");
			wait(15000);
		}
		Log.e(TAG, "start to wait end!");
		locked = true;
	}
	
	public synchronized void notifyToWakeup() {
		if (locked) {
			Log.e(TAG, "start to notify");
			notify();
		}
		Log.e(TAG, "start to notify end!");
		locked = false;
	}
}

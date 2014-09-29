package com.bamutian.util;

import java.util.Stack;
import android.app.Application;
import android.app.LocalActivityManager;

public class ApplicationData extends Application {

	public static Stack<String> originalStack;
	public static Stack<String> forwardStack;


	/**
	 * To get the LocalActivityManager in the ActivityGroup.
	 */
	public static LocalActivityManager activityManager = null;


}

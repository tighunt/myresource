
package com.rtk.tv.widget;

import android.text.Editable;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import java.util.Calendar;
import java.util.Locale;

public class EditTextController {

	private static final int LENGTH_CHANNEL = 3;
	
	public static void bindChannelEditor(TextView tv) {
		bindNumberEditor(tv, LENGTH_CHANNEL);
	}
	
	public static void bindNumberEditor(final TextView tv, final int maxDigits) {
		bindNumberEditor(tv, maxDigits, null);
	}

	public static void bindNumberEditor(final TextView tv, final int maxDigits, final OnEditorActionListener listener) {
		tv.setMovementMethod(null);
		final StringBuilder builder = new StringBuilder(maxDigits);
		tv.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				int n = 0;
				int action = event.getAction();
				switch(keyCode) {
					case KeyEvent.KEYCODE_0:
					case KeyEvent.KEYCODE_1:
					case KeyEvent.KEYCODE_2:
					case KeyEvent.KEYCODE_3:
					case KeyEvent.KEYCODE_4:
					case KeyEvent.KEYCODE_5:
					case KeyEvent.KEYCODE_6:
					case KeyEvent.KEYCODE_7:
					case KeyEvent.KEYCODE_8:
					case KeyEvent.KEYCODE_9:
						n = keyCode - KeyEvent.KEYCODE_0;
						break;
					default:
						return false;
				}
				if (action == KeyEvent.ACTION_UP) {
					// Delete all if we exceed the limit.
					if (builder.length() >= maxDigits) {
						builder.delete(0, builder.length());
						
					// Do not allow numbers started with zero.
					} else if (builder.length() == 1 && builder.charAt(0) == '0') {
						builder.delete(0, 1);
					}
					builder.append(n);
					tv.setText(builder.toString());
					if (listener != null) {
						listener.onEditorAction(tv, EditorInfo.IME_ACTION_NONE, event);
					}
				}
				return true;
			}
		});
	}

	public static void bindTimeEditor(final EditText edit) {
		edit.setMovementMethod(null);
		final StringBuilder builder = new StringBuilder(5);
		edit.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				int n = 0;
				int action = event.getAction();
				switch(keyCode) {
					case KeyEvent.KEYCODE_0:
					case KeyEvent.KEYCODE_1:
					case KeyEvent.KEYCODE_2:
					case KeyEvent.KEYCODE_3:
					case KeyEvent.KEYCODE_4:
					case KeyEvent.KEYCODE_5:
					case KeyEvent.KEYCODE_6:
					case KeyEvent.KEYCODE_7:
					case KeyEvent.KEYCODE_8:
					case KeyEvent.KEYCODE_9:
						n = keyCode - KeyEvent.KEYCODE_0;
						break;
					default:
						return false;
				}
				if (action == KeyEvent.ACTION_UP) {
					if (builder.length() >= 5) {
						builder.delete(0, builder.length());
					}
					builder.append(n);
					if (builder.length() == 2) {
						builder.append(':');
					}
					edit.setText(builder.toString());
				}
				return true;
			}
		});
	}
	
	public static void bindDateEditor(final EditText edit) {
		edit.setMovementMethod(null);
		final StringBuilder builder = new StringBuilder(5);
		edit.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				int n = 0;
				int action = event.getAction();
				switch(keyCode) {
					case KeyEvent.KEYCODE_0:
					case KeyEvent.KEYCODE_1:
					case KeyEvent.KEYCODE_2:
					case KeyEvent.KEYCODE_3:
					case KeyEvent.KEYCODE_4:
					case KeyEvent.KEYCODE_5:
					case KeyEvent.KEYCODE_6:
					case KeyEvent.KEYCODE_7:
					case KeyEvent.KEYCODE_8:
					case KeyEvent.KEYCODE_9:
						n = keyCode - KeyEvent.KEYCODE_0;
						break;
					default:
						return false;
				}
				if (action == KeyEvent.ACTION_UP) {
					// First char must be '0' or '1'
					if ((builder.length() == 0 || builder.length() >= 5)&& (n != 0 && n != 1)) {
						return true;
					}
					
					if (builder.length() >= 5) {
						builder.delete(0, builder.length());
					
					// Check month
					} else if (builder.length() == 1) {
						int i = Integer.parseInt(builder.toString()) * 10 + n;
						if (i > 12) {
							return true;
						}
					} else if (builder.length() == 3) {
					}
					
					builder.append(n);
					
					if (builder.length() == 2) {
						builder.append('/');
					}
					edit.setText(builder.toString());
				}
				return true;
			}
		});
	}

	public static void bindWeekEditor(final EditText edit) {
		edit.setMovementMethod(null);
		edit.setKeyListener(new KeyListener() {
			
			@Override
			public boolean onKeyUp(View view, Editable text, int keyCode, KeyEvent event) {
				Calendar c = Calendar.getInstance();
				int week = -1;
				switch(keyCode) {
					case KeyEvent.KEYCODE_0:
					case KeyEvent.KEYCODE_7:
						week = Calendar.SUNDAY;
						break;
					case KeyEvent.KEYCODE_1:
						week = Calendar.MONDAY;
						break;
					case KeyEvent.KEYCODE_2:
						week = Calendar.TUESDAY;
						break;
					case KeyEvent.KEYCODE_3:
						week = Calendar.WEDNESDAY;
						break;
					case KeyEvent.KEYCODE_4:
						week = Calendar.THURSDAY;
						break;
					case KeyEvent.KEYCODE_5:
						week = Calendar.FRIDAY;
						break;
					case KeyEvent.KEYCODE_6:
						week = Calendar.SATURDAY;
						break;
					default:
						break;
				}
				if (week >= 0) {
					c.set(Calendar.DAY_OF_WEEK, week);
					edit.setText(c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()));
					edit.setTag(Integer.valueOf(week));
					return true;
				}
				return false;
			}
			
			@Override
			public boolean onKeyOther(View view, Editable text, KeyEvent event) {
				return false;
			}
			
			@Override
			public boolean onKeyDown(View view, Editable text, int keyCode, KeyEvent event) {
				return false;
			}
			
			@Override
			public int getInputType() {
				return 0;
			}
			
			@Override
			public void clearMetaKeyState(View view, Editable content, int states) {
				
			}
		});
	}
}

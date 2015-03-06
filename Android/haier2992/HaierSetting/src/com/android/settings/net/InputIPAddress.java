package com.android.settings.net;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * 自定义EditText，用于用户输入IP地址的判断
 *
 * @author ducj (ducj@biaoqi.com.cn)
 * @date 2011-11-14 
 * @since 1.0
 */
public class InputIPAddress extends EditText {

	public InputIPAddress(Context context) {
		super(context);
	}

	public InputIPAddress(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public InputIPAddress(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onTextChanged(CharSequence text, int start, int before,
			int after) {
		super.onTextChanged(text, start, before, after);
		
		String input = text.toString().trim();
		////[2012-1-14 ]由于输入后没有删除功能，所以当初人超出ip范围是时，将文本设置为最后一次输入的值。
		if (0 != input.length()){
			
			int cursorLoc = getSelectionStart();///获取光标位置。
			int index = cursorLoc > 0 ? (cursorLoc - 1) : 0 ;
			char lastChar = input.charAt(index);
			
			if (input.length() <= 3){
				int ip = Integer.parseInt(input);
				if (ip >= 256) {
					setText(String.valueOf(lastChar));
					setSelection(length());
				}
			}
			else{
				setText(String.valueOf(lastChar));
				setSelection(length());
			}
		}
		
		/*
		if (input.length() != 0 && input.length() <= 3) {

			int ip = Integer.parseInt(input);
			
			if (ip >= 256) {
				setText("");
			}
		}
		*/
		
		
	}
}

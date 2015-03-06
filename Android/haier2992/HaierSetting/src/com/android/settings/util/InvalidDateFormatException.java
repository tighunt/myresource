package com.android.settings.util;

public class InvalidDateFormatException extends Exception {

	//正确日期格式
	private String[] dateFormat={"YYYYMMDD","YYYY MM DD","YYYY/MM/DD"};
	
	public InvalidDateFormatException() {
		// TODO Auto-generated constructor stub
	}

	public InvalidDateFormatException(String message) {
		super(message);
	}

	public InvalidDateFormatException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public InvalidDateFormatException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
	
	

}

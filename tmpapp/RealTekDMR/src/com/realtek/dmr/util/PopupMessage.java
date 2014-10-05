package com.realtek.dmr.util;

import com.realtek.dmr.R;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PopupMessage extends PopupWindow{
	
	private Activity context;
	private RelativeLayout rp = null;
	public TextView message = null;
	
	LayoutInflater mInflater=null;
	
	public PopupMessage(Activity mContext)
	{
		super(mContext);
		
		/*int height = 260;
		int width = 678;
		setHeight(height);
		setWidth(width);*/
		
		this.context=mContext;
		
		mInflater = LayoutInflater.from(context);
	    rp=(RelativeLayout) mInflater.inflate(R.layout.message, null);
	    
	    message = (TextView)rp.findViewById(R.id.msg);
	    
	    setContentView(rp);	
	}
	
	public void setMessage(String s)
	{
		message.setText(s);
	}
	
	public void setMessageColor(int color)
	{
		message.setTextColor(color);
	}
	
	public void setMessageLeft()
	{
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) message.getLayoutParams();
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		lp.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);
		lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		message.setGravity(Gravity.LEFT);
		message.setLayoutParams(lp);
	}
	
	public void setMessageRight()
	{
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) message.getLayoutParams();
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		lp.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);
		lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		message.setGravity(Gravity.RIGHT);
		message.setLayoutParams(lp);
	}
	
	public void setMessageCenterHorizotal()
	{
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) message.getLayoutParams();
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
		lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		message.setGravity(Gravity.CENTER);
		message.setLayoutParams(lp);
	}
	
	public void show()//40,850
	{
		 if (this != null){
			 this.dismiss();
		 }
		
		rp.setBackgroundResource(R.drawable.message_box_bg);
		
		setMessageLeft();
		
		int height = 260;
		int width = 678;
		setHeight(height);
		setWidth(width);
		
		this.setFocusable(false);
		this.setOutsideTouchable(true);
		this.showAtLocation(rp, Gravity.CENTER, 0, 0);
	}
	
	public void show(int resid, int height, int width, int gravity, int x, int y)
	{
		this.dismiss();
		
		rp.setBackgroundResource(resid);
		setHeight(height);
		setWidth(width);
		
		this.setFocusable(false);
		this.setOutsideTouchable(true);
		this.showAtLocation(rp, gravity, x, y);
	}
	
	public void justShow() {
		if (this != null){
			 this.dismiss();
		 }
		
		rp.setBackgroundResource(R.drawable.message_box_bg);
		
		setMessageLeft();
		
		int height = 260;
		int width = 678;
		setHeight(height);
		setWidth(width);
		this.showAtLocation(rp, Gravity.CENTER, 0, 0);
	}
}

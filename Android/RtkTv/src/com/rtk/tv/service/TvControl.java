package com.rtk.tv.service;

import com.rtk.tv.data.ChannelInfo;
import android.content.Context;

public class TvControl {

	private String curInputId;
	private ChannelInfo curChannelInfo;
	private ChannelInfo[] channelList;
	private Context mContext;
	public TvControl(Context context) {
		mContext = context;
	}

	public ChannelInfo[] getChannels(){
		return channelList;
	}

	public void setChannels(ChannelInfo[] channels){
		channelList = channels;
	}


	public ChannelInfo getCurrentChannelInfo(){
		return curChannelInfo;
	}


	public void setCurrentChannelInfo(ChannelInfo info) {
		curChannelInfo = info;
	}


	public String getCurInputId() {
		return curInputId;
	}


	public void setCurInputId(String inputId) {
		curInputId = inputId;
	}

	public void reset() {
		curInputId = null;
		curChannelInfo = null;
	}
}

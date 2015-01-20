
package com.rtk.tv.service;

import com.rtk.tv.data.ChannelInfo;

interface ITvService {
	ChannelInfo[] getChannels();
	void setChannels(in ChannelInfo[] channels);
	ChannelInfo getCurrentChannelInfo();
	void setCurrentChannelInfo(in ChannelInfo info);
	String getCurInputId();
	void setCurInputId(in String inputId);
	void reset();
}
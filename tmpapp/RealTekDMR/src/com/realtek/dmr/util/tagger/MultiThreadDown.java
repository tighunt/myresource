package com.realtek.dmr.util.tagger;


import java.text.SimpleDateFormat;
import com.realtek.dmr.util.DownUtil;

import android.annotation.SuppressLint;
import android.os.Environment;

@SuppressLint({ "HandlerLeak", "SdCardPath" })
public class MultiThreadDown
{
    public static String url;
	public DownUtil downUtil;
	int datelen = 0;
	public String filename;
	public MultiThreadDown(String uri,String type)
	{
		String path = Environment.getExternalStorageDirectory().getPath();
		url = uri;
		SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd   hh:mm:ss");     
		String   date   =   sDateFormat.format(new   java.util.Date());
		datelen = date.length();
		filename = date.substring(datelen-8,datelen);
		if(type.equals("mp3"))
		     downUtil = new DownUtil(url,path+"/"+filename+"a.mp3");
		else if(type.equals("wma"))
		     downUtil = new DownUtil(url,path+"/"+filename+"c.wma");
		else if(type.equals("mp4"))
		     downUtil = new DownUtil(url,path+"/"+filename+"d.mp4");
		else if(type.equals("flac"))
		     downUtil = new DownUtil(url,path+"/"+filename+"e.flac");
		else if(type.equals("ogg"))
		     downUtil = new DownUtil(url,path+"/"+filename+"f.ogg");
		try
		{
			downUtil.download();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}	
	}
}
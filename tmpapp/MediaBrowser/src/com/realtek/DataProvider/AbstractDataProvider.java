package com.realtek.DataProvider;

import java.io.File;

public abstract class AbstractDataProvider
{
	private String mName;
	
	public void SetName(String name) 
	{ 
		this.mName =name;
	}
	
	public String GetName()
	{	
		return mName;
	}
	 
	public abstract String GetDataAt     (int i);
	public abstract String GetTitleAt    (int i);
	public abstract String GetMimeTypeAt (int i);
	public abstract int    GetIdAt       (int i);
	public abstract int    GetFileTypeAt (int i);
	public abstract int    GetSize       ();
	public abstract File GetFileAt(int i);
	public abstract void sortListByType();
	public abstract int getDirnum();
	//add by jessie
	public abstract String GetCUEListTitleAt(int i);
	public abstract int GetFileSubTypeAt(int i);
	public abstract String GetFileTotalTime(int i);
	public abstract String GetFileStartTime(int i);
	public abstract String GetFileEndTime(int i);
	public abstract String GetFileDate(int i);
	public abstract String GetFileAlbumName(int i);
	public abstract String GetFilePerformer(int i);
	public abstract boolean isUsbPlugin();
	//end add
	
}

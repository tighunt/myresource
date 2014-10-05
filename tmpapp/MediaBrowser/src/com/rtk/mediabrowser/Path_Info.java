package com.rtk.mediabrowser;

import java.util.ArrayList;
import android.util.Log;


import com.rtk.mediabrowser.Level_Info;

public class Path_Info 
{
	
	public int dirLevel = -1; 
	public ArrayList<Level_Info> curPathArr = new ArrayList<Level_Info>();
	
	// level info handling
	public void addLevelInfo(String path)
	{
	    Level_Info info = new Level_Info();
	    info.path = path;
	    info.position = 0;
	    info.firstVisibleItem = 0;
	    info.lastVisibleItem = 0;
	    curPathArr.add(info);
	    dirLevel++;
	}

	public void addPhotoLevelInfo(int moveLine, int movePage)
	{
	    Level_Info info = new Level_Info();
	    info.path = null;
	    info.position = 0;
	    info.firstVisibleItem = moveLine;
	    info.lastVisibleItem = movePage;
	    
	    curPathArr.add(info);
	    dirLevel++;
	}
    public void cleanLevelInfo()
    {
        curPathArr.clear();
	    dirLevel = -1;     		            
	}
		    
    public void backToLastLevel() 
	{
        Log.d("MediaBrowser", "backToLastLevel");
        curPathArr.remove(dirLevel);
		dirLevel--;		            	
		//getFileListByPath(curPathArr.get(dirLevel).path, MediaUtils.getFileExt(cur_type));
	} 

	public void backToPhotoLastLevel() 
	{
        Log.d("MediaBrowser", "backToPhotoLastLevel");
        curPathArr.remove(dirLevel);
		dirLevel--;		            	
		//getFileListByPath(curPathArr.get(dirLevel).path, MediaUtils.getFileExt(cur_type));
	}  
    
    //hartley add
    public void backToDeviceLevel()
    {
    	while(dirLevel > 0)
    	{
	    	curPathArr.remove(dirLevel);
			dirLevel--;	
    	}
    }
    public String getDeviceLevelPath()
    {
    	return curPathArr.get(0).path;
    }
    
    public void setLevelFocus(int level, int position)
    {
        curPathArr.get(level).position = position;
    }
    
    public void setLastLevelFocus(int position)
    {
        curPathArr.get(dirLevel).position = position;
    }
    
    public void setLastFirstVisibleItem(int firstVisibleItem) {
    	curPathArr.get(dirLevel).firstVisibleItem = firstVisibleItem;
    }
    
	public void setLastLastVisibleItem(int lastVisibleItem) {
		curPathArr.get(dirLevel).lastVisibleItem = lastVisibleItem;
	}
    
	    
	public int getLastLevel()
	{
	    return dirLevel;
	}
	    
	public String getLastLevelPath()
	{
	    return curPathArr.get(dirLevel).path;
	}
	    
	   
	public int getLastLevelFocus()
	{
	    return curPathArr.get(dirLevel).position;
	}

	public int getLastFirstVisibleItem() {
		return curPathArr.get(dirLevel).firstVisibleItem;
	}
	
	public int getLastLastVisibleItem() {
		return curPathArr.get(dirLevel).lastVisibleItem;
	}
	
    public void setLastLevelLastnum(int lastnum)
    {
        curPathArr.get(dirLevel).lastnum = lastnum;
    }
    
    public int getLastLevelLastnum()
    {
        return curPathArr.get(dirLevel).lastnum;
    }
}

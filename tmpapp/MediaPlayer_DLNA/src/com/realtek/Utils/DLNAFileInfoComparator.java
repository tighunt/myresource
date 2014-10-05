package com.realtek.Utils;

import java.util.Comparator;

import android.util.Log;

public class DLNAFileInfoComparator implements Comparator<DLNAFileInfo>{

	protected int mode;
	private int type;
	public DLNAFileInfoComparator(int mode){
		this.mode = mode;
	}
	public DLNAFileInfoComparator(int type,int mode){
		this.mode = mode;
		this.type = type;
	}
	
	@Override
	public int compare(DLNAFileInfo object1, DLNAFileInfo object2) {
        int type1 = object1.getFileType();
        int type2 = object2.getFileType();
        int result=0;
        if(type1 > type2)
        {
        	result = -1;
        	return result;
        }
        else if(type1 < type2)
        {
        	result = 1;
        	return result;
        }
		String  m1=object1.getFileName();
        String  m2=object2.getFileName();
        if(type == 1){
    		m1=object1.getFileDate();
            m2=object2.getFileDate();
        }
        if(m1.compareToIgnoreCase(m2)>0)
        {
            result=mode;
        }
        if(m1.compareToIgnoreCase(m2)<0)
        {
            result=-mode;
        }
        Log.e("test", m1 + result +" " +m2);
        return result;
	}
}


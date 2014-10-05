package com.rtk.mediabrowser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

public class BookMark {
	
	private final static String TAG = "BookMark";

	private String bookMarkFileName = null;
	//public Context mContext = null;
	private int maxCount = 1;
	
	public class BookMarkFile
	{	
		int fileNameLen;
		String fileName;
		int subtitleTrack;
		int subtitleEnable;
		int audioTrack;
		int drmStatus;
		int isDivxVODFile;
		int bufferLen;
		byte[] navBuffer;
	}
	
	public ArrayList<BookMarkFile> bookMarkList = new ArrayList<BookMarkFile>();
	
	public BookMark(/*Context context,*/ String fileName) {
		//mContext = context;
		bookMarkFileName = fileName;
		cleanBookMark();
		readBookMark();
	}
	
	public void cleanBookMark()
	{
		bookMarkList.clear();
	}
	
	public int bookMarkLength()
	{
		return bookMarkList.size();
	}
	
	public void addBookMark(String fileName, int subtitleNum, int subtitleOn, int audioNum, int drmStatus, int isDivxVODFile, byte[] buffer)
	{
		BookMarkFile mark = new BookMarkFile();
		
		mark.fileNameLen = fileName.getBytes().length;
		mark.fileName = fileName;
		mark.subtitleTrack = subtitleNum;
		mark.subtitleEnable = subtitleOn;
		mark.audioTrack = audioNum;
		mark.drmStatus = drmStatus;
		mark.isDivxVODFile = isDivxVODFile;
		mark.bufferLen = buffer.length;
		mark.navBuffer = buffer;
		
		Log.v(TAG, "Add a BookMark:");
		Log.v(TAG, "  name:" + mark.fileName + ", length:" + mark.fileNameLen);
		Log.v(TAG, "  buffer length:" + mark.bufferLen);
		
		if (bookMarkList.size() < maxCount)
		{
			bookMarkList.add(mark);
		}else 
		{
			bookMarkList.remove(0);
			bookMarkList.add(mark);
		}
	}
	
	public void removeBookMark(int index)
	{
		if (index >= 0 && index < bookMarkList.size())
			bookMarkList.remove(index);
	}
	
	public void removeBookMark(String name)
	{
		if(name != null)
		{
			int index = findBookMark(name);
			if (index >= 0)
				bookMarkList.remove(index);
		}
	}
	
	public int findBookMark(String name)
	{
		int i = 0;
		int length = bookMarkList.size();
		
		while (i < length)
     	{
			if (bookMarkList.get(i).fileName.compareTo(name)== 0)
     			break;
			
     		i++;
     	}
		
		if (i == length)
			return -1;
		
		return i;
	}
	
	
	public void readBookMark()
	{
		if (bookMarkFileName == null)
		{
			Log.e(TAG, "BookMark file name null!");
			return;
		}
		
		try {
			File f = new File(bookMarkFileName);
			FileInputStream fis = new FileInputStream(f);
			//FileInputStream fis = mContext.openFileInput(bookMarkFileName);
			int hasRead = 0;
			byte[] buf = new byte[4];
			hasRead = fis.read(buf, 0, 4);
			while(hasRead > 0)
			{
				/*
				BookMarkFile mark = new BookMarkFile();
				
				// BookMarkFile.fileNameLen
				mark.fileNameLen = byte2int(buf);
				
				// BookMarkFile.fileName
				byte[] nameBuf = new byte[mark.fileNameLen];
				fis.read(nameBuf, 0, mark.fileNameLen);
				mark.fileName = new String(nameBuf);
				
				// BookMarkFile.bufferLen
				fis.read(buf, 0, 4);
				mark.bufferLen = byte2int(buf);
				
				// BookMarkFile.markBuffer
				byte[] markBuf = new byte[mark.bufferLen];
				fis.read(markBuf, 0, mark.bufferLen);
				mark.markBuffer = markBuf;
				
				Log.v(TAG, "Read a BookMark");
				//Log.v(TAG, "  fileNameLen: " + mark.fileNameLen);
				//Log.v(TAG, "  fileName: " + mark.fileName);
				//Log.v(TAG, "  bufferLen: " + mark.bufferLen);
				
				bookMarkList.add(mark);
				*/
				
				int length = 0;
				
				length = byte2int(buf);
				byte[] nameBuf = new byte[length];
				fis.read(nameBuf, 0, length);
				String name = new String(nameBuf);
				
				fis.read(buf, 0, 4);
				int subtitleNum = byte2int(buf);
				
				fis.read(buf, 0, 4);
				int subtitleOn = byte2int(buf);
				
				fis.read(buf, 0, 4);
				int audioNum = byte2int(buf);
				
				fis.read(buf, 0, 4);
				int drmStatus = byte2int(buf);
				
				fis.read(buf, 0, 4);
				int isDivxVODFile = byte2int(buf);
				
				fis.read(buf, 0, 4);
				length = byte2int(buf);
				byte[] markBuf = new byte[length];
				fis.read(markBuf, 0, length);
				
				addBookMark(name, subtitleNum, subtitleOn, audioNum, drmStatus, isDivxVODFile, markBuf);
				
				hasRead = fis.read(buf, 0, 4);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeBookMark()
	{
		try {
			//mContext.deleteFile(bookMarkFileName);
			
			File f = new File(bookMarkFileName);
			f.delete();
			
			FileOutputStream fos = new FileOutputStream(f, true);
			//FileOutputStream fos = mContext.openFileOutput(bookMarkFileName, Context.MODE_APPEND);
			int i = 0;
			int count = bookMarkList.size();
			while (i < count)
			{
				BookMarkFile mark = bookMarkList.get(i);
				
				Log.v(TAG, "Write a BookMark: " + mark.fileName);
				
				fos.write(int2byte(mark.fileNameLen));
				fos.write(mark.fileName.getBytes());
				fos.write(int2byte(mark.subtitleTrack));
				fos.write(int2byte(mark.subtitleEnable));
				fos.write(int2byte(mark.audioTrack));
				fos.write(int2byte(mark.drmStatus));
				fos.write(int2byte(mark.isDivxVODFile));
				fos.write(int2byte(mark.bufferLen));
				fos.write(mark.navBuffer);
				i++;
			}
			fos.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getSubtitleTrack(int index)
	{
		if (index >= 0 && index < bookMarkList.size())
			return bookMarkList.get(index).subtitleTrack;
		
		return -1;
	}
	
	public int isSubtitleOn(int index)
	{
		if (index >= 0 && index < bookMarkList.size())
			return bookMarkList.get(index).subtitleEnable;
		
		return 0;
	}
	
	public int getAudioTrack(int index)
	{
		if (index >= 0 && index < bookMarkList.size())
			return bookMarkList.get(index).audioTrack;
		
		return -1;
	} 
	
	public byte[] getNavBuffer(int index)
	{
		if (index >= 0 && index < bookMarkList.size())
			return bookMarkList.get(index).navBuffer;
		
		return null;
	}
	
	public static int byte2int(byte[] res)
	{
		int targets = ((char)res[0] | 
					((char)(res[1] & 0xff) << 8)| 
					((char)(res[2] & 0xff) << 16) | 
					((char)(res[3] & 0xff) << 24)); 
		return targets;
	}
	
	public static byte[] int2byte(int data)
	{
		byte [] targets = new byte [4];
		
		targets[0] = (byte)(data & 0xff);
		targets[1] = (byte)((data >> 8) & 0xff);
		targets[2] = (byte)((data >> 16) & 0xff);
		targets[3] = (byte)((data >> 24) & 0xff);
		
		return targets;
	}
	
	
}

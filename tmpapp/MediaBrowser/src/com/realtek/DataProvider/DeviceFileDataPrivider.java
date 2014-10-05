package com.realtek.DataProvider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;


import android.content.res.XmlResourceParser;
import android.util.Log;
import com.realtek.DataProvider.AbstractDataProvider;
import com.realtek.DataProvider.FileFilterType;



import com.realtek.Utils.Element;
import com.realtek.Utils.FileInfo;
import com.realtek.Utils.MimeTypeParser;
import com.realtek.Utils.MimeTypes;
import com.realtek.Utils.ParseException;
import com.realtek.Utils.PlayList;
import com.realtek.Utils.utilCue;
import com.realtek.Utils.utilCue.CueFileBean;
import com.realtek.Utils.utilCue.CueSongBean;
import com.rtk.mediabrowser.MediaApplication;
import com.rtk.mediabrowser.MediaBrowserConfig;




public class DeviceFileDataPrivider  extends AbstractDataProvider 
{	
	private static final String TAG = "kelly-FileMangerActivity";
	private String mPath="/tmp/usbmounts/";
	public static final String sdPath="/mnt/"+MediaApplication.internalStorage +"/";
	private int   mFileType=FileFilterType.DEVICE_FILE_PHOTO|FileFilterType.DEVICE_FILE_DIR;
	private int   mLimitCnt=0;
	private int   mSortMode=0;
	private MimeTypes mMimeTypes=null;
	private int dirnum =0;
	private boolean isUsbPlugin = false;
	
	private ArrayList<FileInfo> mFileList =  new ArrayList<FileInfo>(); 
	
	public DeviceFileDataPrivider (String path, int fileType,int limit,int sortMode,MimeTypes mimeTypes)
	{
		super.SetName("DeviceFileDataPrivider");
		
	    mPath     =path;
	    mFileType =fileType; 
	    mLimitCnt =limit;
	    mSortMode =sortMode;
	    mMimeTypes = mimeTypes;
	    dirnum = 0;
	    CreateFileListArray();
		
	}
	
	public String GetDataAt     (int i)
	{
		if( mFileList!=null && i>=0 && i<this.GetSize())
		{
			File file =  (File)this.mFileList.get(i).getmFile();
			
			return file.getPath();
			
		}	
		return null;
	}

	public File GetFileAt(int i)
	{
		if(mFileList!=null &&i>=0 && i<this.GetSize())
		{
			File file =  (File)this.mFileList.get(i).getmFile();
			
			return file;
		}	
		return null;
	}

	public String GetTitleAt    (int i)
	{
		if(mFileList!=null && i>=0 && i<this.GetSize())
		{
			File file =  (File)this.mFileList.get(i).getmFile();
			
			return file.getName();
			
		}	
		return null;
	}
	
	//add by jessie
	@Override
	public String GetCUEListTitleAt(int i)
	{
		if(mFileList!=null && i>=0 && i<this.GetSize())
		{
			String fileName = this.mFileList.get(i).getFileName();
			
			return fileName;
			
		}	
		return null;
	}
	//end add
	
	public  String GetMimeTypeAt (int i)
	{
		if(mFileList!=null && i>=0 && i<this.GetSize())
		{
			File file =  (File)this.mFileList.get(i).getmFile();
			
			return mMimeTypes.getMimeType(file.getName());
			
		}	
		return null;
	}
	
	public int GetIdAt(int i)
	{
		
		if(mFileList!=null && i>=0 && i<this.GetSize())
		{
			return i;
			
		}	
		return -1;
	}
	
	public int GetSize ()
	{
		if(mFileList == null) return 0;
		int size =this.mFileList.size();
		
		if(size<=0) size = 0;
		
		return size;
	}
	
	public int GetFileTypeAt(int i)
	{
		if(mFileList!=null && i>=0 && i<this.GetSize())
		{
			//Log.d(TAG,"GetFileTypeAt:"+mFileTypeList[i]);
			return this.mFileList.get(i).getmFileType();
			/*
			File file =  (File)this.mFileList.get(i);
			
			if(file.isDirectory())
			{
				return FileFilterType.DEVICE_FILE_DIR;
			}	
			else if(this.mMimeTypes.isImageFile(file.getName()))
			{
				return FileFilterType.DEVICE_FILE_PHOTO;
			}	
			else if(this.mMimeTypes.isAudioFile(file.getName()))
			{
				return FileFilterType.DEVICE_FILE_AUDIO;
			}
			else if(this.mMimeTypes.isAudioFile(file.getName()))
			{
				return FileFilterType.DEVICE_FILE_AUDIO;
			}
			else if(this.mMimeTypes.isVideoFile(file.getName()))
			{
				return FileFilterType.DEVICE_FILE_VIDEO;
			}*/
			
		}
		
		return FileFilterType.DEVICE_FILE_NONE;
	}
	
	public boolean isUsbPlugin(){
		return isUsbPlugin;
	}
	
	//add by jessie
	@Override
	public int GetFileSubTypeAt(int i)
	{
		if(mFileList!=null && i>=0 && i<this.GetSize())
		{
			return this.mFileList.get(i).getmSubType();
		}
		
		return FileFilterType.DEVICE_FILE_NONE;
	}
	
	@Override
	public String GetFileTotalTime(int i)
	{
		if(mFileList!=null && i>=0 && i<this.GetSize())
		{
			return this.mFileList.get(i).getTime();
		}
		
		return "00:00:00";
	}
	
	@Override
	public String GetFileStartTime(int i)
	{
		if(mFileList!=null && i>=0 && i<this.GetSize())
		{
			return this.mFileList.get(i).getCueStartIndex();
		}
		
		return "00:00:00";
	}
	
	@Override
	public String GetFileEndTime(int i)
	{
		if(mFileList!=null && i>=0 && i<this.GetSize())
		{
			return this.mFileList.get(i).getCueEndIndex();
		}
		return "00:00:00";
	}
	
	@Override
	public String GetFileDate(int i) {
		if(mFileList!=null && i>=0 && i<this.GetSize())
		{
			return this.mFileList.get(i).getCueDate();
		}
		return null;
	}

	@Override
	public String GetFileAlbumName(int i) {
		if(mFileList!=null && i>=0 && i<this.GetSize())
		{
			return this.mFileList.get(i).getAlbumName();
		}
		return null;
	}

	@Override
	public String GetFilePerformer(int i) {
		if(mFileList!=null && i>=0 && i<this.GetSize())
		{
			return this.mFileList.get(i).getPerformer();
		}
		return null;
	}
	//end add
	
	private void CreateFileListArray()
	{
	    //Log.e(TAG, "CreateFileListArray");
		// add by jessie
		if (MediaBrowserConfig.HAVE_PLAYLIST == true && this.mMimeTypes.isVDirFile(mPath)) {
			if (this.mMimeTypes.getMimeType(mPath).contains("cue")) {
				File cueFile = new File(mPath); 
				utilCue utilCue = new utilCue();
				CueFileBean bean = utilCue.parseCueFile(cueFile); 
				String fileName = bean.getFileName();
				String albumName = bean.getAlbumName();
				String performer = bean.getPerformer();
				String date = bean.getDate();
				String filePath = mPath.substring(0, mPath.lastIndexOf("/") + 1);
				filePath += fileName;
				List<CueSongBean> songs = bean.getSongs();
				int totalLen = songs.size();
				for (int i = 0; i < totalLen; i++) {
					String title = songs.get(i).getTitle();
					FileInfo finfo = new FileInfo(FileFilterType.DEVICE_FILE_AUDIO, FileFilterType.DEVICE_FILE_AUDIO_TRACK, 
							new File(filePath),title,filePath);	
					String startTime = songs.get(i).getIndexBegin();
					String endTime = songs.get(i).getIndexEnd();
					String playTime = "";
					if(startTime == null ){
						startTime = songs.get(i-1).getIndexEnd();
					}
					if(endTime == null && i < totalLen - 1){
						endTime = songs.get(i+1).getIndexBegin();
					}
					if(i < totalLen - 1){
						playTime = utilCue.getPlayTime(startTime, endTime);
						finfo.setTime(playTime);
					}else{
						//the last track's endTime is not provided, so do it in FileListAdapter.getView()
						finfo.setTime("--:--:--");
					}
					finfo.setCueStartIndex(startTime);
					finfo.setCueEndIndex(endTime);
					finfo.setCueDate(date);
					finfo.setAlbumName(albumName);
					finfo.setPerformer(performer);
					this.mFileList.add(finfo);
				}	//add end	
			}else if(this.mMimeTypes.getMimeType(mPath).contains("/m3u8") ||
					this.mMimeTypes.getMimeType(mPath).contains("/m3u")){
				try {
					FileInputStream fis = new FileInputStream(mPath);
					BufferedInputStream in = new BufferedInputStream(fis);

					PlayList playList = PlayList.parse(new InputStreamReader(
							in, "UTF-8"));
					for (Iterator itor = playList.iterator(); itor.hasNext();) {
						Element element = (Element) itor.next();
						String path = "";
						if (element.getURI().getPath().startsWith("/")) {
							path = element.getURI().getPath();
						} else {
							path = mPath.substring(0,
									mPath.lastIndexOf('/') + 1)
									+ element.getURI().getPath();
						}
						FileInfo fileInfo = new FileInfo(
								FileFilterType.DEVICE_FILE_AUDIO,
								new File(path));
						this.mFileList.add(fileInfo);
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}// gmaui add end
		}else{	
	        
       	 if(mPath.equals("/storage/udisk/") || mPath.equals("/storage/udisk") )
       	 {
       		 FileInfo fileInfo = new FileInfo(FileFilterType.DEVICE_FILE_DEVICE,new File(sdPath));
       		 this.mFileList.add(fileInfo); 
       		 dirnum ++;
       	 }
			 File file = new File(mPath);
			 File[] files = file.listFiles(); 
			 
			 if(files == null) 
			 	return;
			 
			// Log.e(TAG, "CreateFileListArray:"+files.length);
			 
			 if(files.length <=0) {
				 isUsbPlugin = false;
				 return;		 
			 }
			 
			 isUsbPlugin = true;
			 int j=0;
			 int tmpnum = files.length > MediaApplication.MAXFILENUM ? MediaApplication.MAXFILENUM : files.length ;
			 for(int i = 0; i < tmpnum; i++) 
			 {
				 
				 String filename = files[i].getName();
				 
	             if( (files[i].isDirectory()==true) &&
	                 (filename.equals(".") ==false) &&
	                 (filename.equals("..")==false) &&
	                 ((mFileType&FileFilterType.DEVICE_FILE_DIR) == FileFilterType.DEVICE_FILE_DIR)
	                )     
	             {
	            	 FileInfo fileInfo;
	            	 if(mPath.equals("/storage/udisk/") || mPath.equals("/storage/udisk") )
	            	 {
	            		 fileInfo = new FileInfo(FileFilterType.DEVICE_FILE_DEVICE,files[i]);
	            	 }
	            	 else
	            		 fileInfo = new FileInfo(FileFilterType.DEVICE_FILE_DIR,files[i]);
	            	 this.mFileList.add(fileInfo); 
	            	 dirnum ++;
	            	 j++;
	             }
	             else if( ((mFileType&FileFilterType.DEVICE_FILE_PHOTO) == FileFilterType.DEVICE_FILE_PHOTO) &&
	            		  (this.mMimeTypes.isImageFile(filename) == true)
	            		)
	             {
	            	 FileInfo fileInfo = new FileInfo(FileFilterType.DEVICE_FILE_PHOTO,files[i]);
	            	 this.mFileList.add(fileInfo);
	            	 j++;
	             }
	             else if( ((mFileType&FileFilterType.DEVICE_FILE_VIDEO) == FileFilterType.DEVICE_FILE_VIDEO) &&
	           		  (this.mMimeTypes.isVideoFile(filename) == true)
	           		)
	             {
	            	 FileInfo fileInfo = new FileInfo(FileFilterType.DEVICE_FILE_VIDEO,files[i]);
	            	 this.mFileList.add(fileInfo);
	            	 j++;
	             }
	             else if( ((mFileType&FileFilterType.DEVICE_FILE_AUDIO) == FileFilterType.DEVICE_FILE_AUDIO) &&
	              		  (this.mMimeTypes.isAudioFile(filename) == true)
	              		)
	             {
	            	FileInfo fileInfo = new FileInfo(FileFilterType.DEVICE_FILE_AUDIO,files[i]);
					this.mFileList.add(fileInfo);
	               	j++;
	             }
	             else if( ((mFileType & FileFilterType.DEVICE_FILE_VDIR) == FileFilterType.DEVICE_FILE_VDIR) &&
	              		  (this.mMimeTypes.isVDirFile(filename) == true)
	              		){
	            	 FileInfo fileInfo = new FileInfo(FileFilterType.DEVICE_FILE_VDIR,files[i]);
	            	 this.mFileList.add(fileInfo);
	             }
			 }      
         }
	}

	public int getDirnum() {
		return dirnum;
	}

	@SuppressWarnings("unchecked")
	public void sortListByType() {
		Collections.sort(mFileList, new SortByType());
	}

	@SuppressWarnings("rawtypes")
	class SortByType implements Comparator {
		public int compare(Object o1, Object o2) {
			FileInfo s1 = (FileInfo) o1;
			FileInfo s2 = (FileInfo) o2;
			if(s1.getmFileType() == s2.getmFileType()){
				if(s1.getPath() != null && s2.getPath() != null)
					return s1.getPath().compareToIgnoreCase(s2.getPath());
			}
			else if (s1.getmFileType() == FileFilterType.DEVICE_FILE_DIR
					&& s2.getmFileType() != FileFilterType.DEVICE_FILE_DIR)
				return -1;
			else if (s1.getmFileType() != FileFilterType.DEVICE_FILE_DIR
					&& s2.getmFileType() == FileFilterType.DEVICE_FILE_DIR)
				return 1;
			else if ((s1.getmFileType() & FileFilterType.DEVICE_FILE_VDIR) == FileFilterType.DEVICE_FILE_VDIR
					&& (s2.getmFileType() & FileFilterType.DEVICE_FILE_VDIR) != FileFilterType.DEVICE_FILE_VDIR)
				return -1;
			else if ((s1.getmFileType() & FileFilterType.DEVICE_FILE_VDIR) != FileFilterType.DEVICE_FILE_VDIR
					&& (s2.getmFileType() & FileFilterType.DEVICE_FILE_VDIR) == FileFilterType.DEVICE_FILE_VDIR)
				return 1;
			return 0;
		}
	}
	
}

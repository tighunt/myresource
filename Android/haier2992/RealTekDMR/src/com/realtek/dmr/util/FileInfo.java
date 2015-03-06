package com.realtek.dmr.util;

import java.io.File;

public class FileInfo {
	int mFileType;
	File mFile;
	String fileName;
	String time ="";
	//add by jessie
	int mSubType;
	String cueStartIndex = "";
	String cueEndIndex = "";
	String cueDate = "";
	String albumName = "";
	String performer = "";
	//end add
	String path ="";
	String date;
	int canPlay = 0; //0:unknown 1:yes -1;no
	boolean play = false;
	boolean hasImage = true;
	boolean isLoading = false;
	public boolean isHasImage() {
		return hasImage;
	}
	public void setHasImage(boolean hasImage) {
		this.hasImage = hasImage;
	}
	public FileInfo(int mFileType, File mFile) {
		this.mFileType = mFileType;
		this.mFile = mFile;
	}
	public FileInfo(int mFileType, File mFile, String fileName) {
		this.mFileType = mFileType;
		this.mFile = mFile;
		this.fileName = fileName;
	}
	public FileInfo(int mFileType, File mFile, String fileName, String path) {
		this.mFileType = mFileType;
		this.mFile = mFile;
		this.fileName = fileName;
		this.path = path;
	}
	public FileInfo(int mFileType, File mFile, String fileName, String path,String performer,String date) {
		this.mFileType = mFileType;
		this.mFile = mFile;
		this.fileName = fileName;
		this.path = path;
		this.performer = performer;
		this.date = date;
	}
	//hartley add
	public FileInfo(int mFileType, String fileName, String path)
	{
		this.mFileType = mFileType;
		this.fileName = fileName;
		this.path = path;
	}
	
	//add by jessie
	public FileInfo(int mFileType, int subType, File mFile, String fileName, String path) {
		this.mFileType = mFileType;
		this.mSubType = subType;
		this.mFile = mFile;
		this.fileName = fileName;
		this.path = path;
	}
	
	public FileInfo(int mFileType, int subType, File mFile, String fileName, String path, String time, String startIndex, String endIndex, String date, String albumName, String performer) {
		this.mFileType = mFileType;
		this.mSubType = subType;
		this.mFile = mFile;
		this.fileName = fileName;
		this.path = path;
		this.time = time;
		this.cueStartIndex = startIndex;
		this.cueEndIndex = endIndex;
		this.cueDate = date;
		this.albumName = albumName;
		this.performer = performer;
	}
	
	public int getmSubType() {
		return mSubType;
	}
	public void setmSubType(int mSubType) {
		this.mSubType = mSubType;
	}
	public String getCueStartIndex() {
		return cueStartIndex;
	}
	public void setCueStartIndex(String cueStartIndex) {
		this.cueStartIndex = cueStartIndex;
	}
	public String getCueEndIndex() {
		return cueEndIndex;
	}
	public void setCueEndIndex(String cueEndIndex) {
		this.cueEndIndex = cueEndIndex;
	}
	
	public String getCueDate() {
		return cueDate;
	}
	public void setCueDate(String cueDate) {
		this.cueDate = cueDate;
	}
	public String getAlbumName() {
		return albumName;
	}
	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}
	public String getPerformer() {
		return performer;
	}
	public void setPerformer(String performe) {
		this.performer = performe;
	}
	//end add
	public int getmFileType() {
		return mFileType;
	}
	public void setmFileType(int mFileType) {
		this.mFileType = mFileType;
	}
	public File getmFile() {
		return mFile;
	}
	public void setmFile(File mFile) {
		this.mFile = mFile;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getPath() {
		if(path == null && mFile !=null || path!=null && path.length()<=0)
		{
			return mFile.getAbsolutePath();
		}
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public boolean isPlay() {
		return play;
	}
	public void setPlay(boolean play) {
		this.play = play;
	}
	public int getCanPlay() {
		return canPlay;
	}
	public void setCanPlay(int canPlay) {
		this.canPlay = canPlay;
	}
	public boolean isLoading() {
		return isLoading;
	}
	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
}

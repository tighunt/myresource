package com.realtek.dmr.util;

public class DLNAFileInfo {

	int index;
	int fileType;
	String fileName;
	String uniqueCharID;
	String filePath;
	String fileDate;
	String performer;
	String albumName;
	String time;
	boolean hasImage = true;
	boolean isLoading = false;
	int canPlay = 0; //0:unknown 1:yes -1;no

	String date_exif   = null;
	int oriention_exif = -1;
	int rotateMode     = -1;
	int height =0;
	int width  =0;
	
	public DLNAFileInfo(int index, String fileName) {
		this.index = index;
		this.fileName = fileName;
	}

	public DLNAFileInfo(int index, String fileName, String uniqueCharID,
			int fileType, String filePath, String fileDate, String performer, String albumName) {
		this.index = index;
		this.fileName = fileName;
		this.uniqueCharID = uniqueCharID;
		this.fileType = fileType;
		this.filePath = filePath;
		this.fileDate = fileDate;
		this.performer = performer;
		this.albumName = albumName;
	}
	
	public DLNAFileInfo(int index, String fileName, String uniqueCharID,
			int fileType, String filePath, String fileDate, int oriention_exif,
			int rotateMode ,String date_exif) {
		this.index = index;
		this.fileName = fileName;
		this.uniqueCharID = uniqueCharID;
		this.fileType = fileType;
		this.filePath = filePath;
		this.fileDate = fileDate;

		this.date_exif = date_exif;
		this.oriention_exif = oriention_exif;
		this.rotateMode = rotateMode;
	}
	public void setRotateMode(int rotateMode)
	{
		this.rotateMode = rotateMode;
	}
	public int getRotateMode()
	{
		return this.rotateMode;
	}
	public void setOrientionExif(int oriention_exif)
	{
		this.oriention_exif = oriention_exif;
	}
	public int getOrientionExif()
	{
		return this.oriention_exif;
	}
	public void setDateExif(String date_exif)
	{
		this.date_exif = date_exif;
	}
	public String getDateExif()
	{
		return this.date_exif;
	}
	
	public void setHeight(int height)
	{
		this.height = height;
	}
	public void setWidth(int width)
	{
		this.width = width;
	}
	public int getHeight()
	{
		return this.height;
	}
	public int getWidth()
	{
		return this.width;
	}
	
	
	
	public int getIndex() {
		return index;
	}

	public String getFileName() {
		return fileName;
	}

	public String getUniqueCharID() {
		return uniqueCharID;
	}

	public int getFileType() {
		return fileType;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getFileDate() {
		return fileDate;
	}
	
	public String getPerformer() {
		return performer;
	}
	
	public String getAlbumName() {
		return albumName;
	}
	
	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	public boolean isHasImage() {
		return hasImage;
	}

	public void setHasImage(boolean hasImage) {
		this.hasImage = hasImage;
	}
	
	public boolean isLoading() {
		return isLoading;
	}
	
	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}
	
	public int getCanPlay() {
		return canPlay;
	}
	
	public void setCanPlay(int canPlay) {
		this.canPlay = canPlay;
	}
	
	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
}

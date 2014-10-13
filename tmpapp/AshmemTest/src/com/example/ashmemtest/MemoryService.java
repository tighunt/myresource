package com.example.ashmemtest;

import java.io.FileDescriptor;
import java.io.IOException;

import android.os.Parcel;
import android.os.MemoryFile;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import java.io.InputStream;
import java.io.FileInputStream;


public class MemoryService extends IMemoryService.Stub {
	private final static String LOG_TAG = "MemoryService";
	private MemoryFile file = null;
	
	public MemoryService() {
		try {
                        file = new MemoryFile("MemoryFileTest", 4);
                        setValue(0);
                }
                catch(IOException ex) {
                        Log.i(LOG_TAG, "Failed to create memory file.");
                        ex.printStackTrace();
                }
	}
	
	public InputStream getInputStream() {
		

		    InputStream is = file.getInputStream();
  
        return is; 
	}
	InputStream mf = null;
	    public ParcelFileDescriptor getFileDescriptor() {  
        Log.i(LOG_TAG, "Get File Descriptor.");  
  
        ParcelFileDescriptor pfd = null;  
  
        try {  
        	FileDescriptor fd = file.getFileDescriptor();
          pfd = (fd!= null ? new ParcelFileDescriptor(fd) : null); 
        } catch(IOException ex) {  
            Log.i(LOG_TAG, "Failed to get file descriptor.");  
            ex.printStackTrace();  
        }  
  
        return pfd;  
    }
		/*public ParcelFileDescriptor getFileDescriptor() {
		

		    ParcelFileDescriptor pfd = null;  
  			int val =0;
        try {  
        	        FileDescriptor fd = file.getFileDescriptor();
        	        Log.i(LOG_TAG, "" +fd.toString());  
        	        if(mf == null)
        	            mf = new FileInputStream(fd);
	    						if(mf != null) {
										try {
	    										byte[] buffer = new byte[4];
	    										mf.read(buffer, 0, 4);    			
	    										val = (buffer[0] << 24) | ((buffer[1] & 0xFF) << 16) | ((buffer[2] & 0xFF) << 8) | (buffer[3] & 0xFF);
	    										Log.i(LOG_TAG, "Get value2 " + val + " from memory file. ");
	    										
	    										mf.read(buffer, 0, 4);    			
	    										val = (buffer[0] << 24) | ((buffer[1] & 0xFF) << 16) | ((buffer[2] & 0xFF) << 8) | (buffer[3] & 0xFF);
	    										Log.i(LOG_TAG, "Get value3 " + val + " from memory file. ");
												} catch(Exception exp) {
														Log.i(LOG_TAG, "Failed to read bytes from memory file.");
														exp.printStackTrace();
													}finally{
															try{
																	mf.close();
																}catch(Exception es){}
														}
									}
									
        	        pfd = (fd!= null ? new ParcelFileDescriptor(fd) : null); 
        	        FileDescriptor fds = pfd.getFileDescriptor();
						if(fds == null) {
							Log.i(LOG_TAG, "Failed to get memeory file descriptor.");                    
						}	
						Log.i(LOG_TAG, "" +fds.toString());
        } catch(IOException ex) {  
            Log.i(LOG_TAG, "Failed to get file descriptor.");  
            ex.printStackTrace();  
        }  
  
        return pfd; 
	}*/
	
	public void setValue(int val) {
		if(file == null) {
			return;
		}

		byte[] buffer = new byte[4];   
		buffer[0] = (byte)((val >>> 24) & 0xFF);
		buffer[1] = (byte)((val >>> 16) & 0xFF);
		buffer[2] = (byte)((val >>> 8) & 0xFF); 
		buffer[3] = (byte)(val & 0xFF);
		
		try {
			file.writeBytes(buffer, 0, 0, 4);
			Log.i(LOG_TAG, "Set value " + val + " to memory file. ");
			file.readBytes(buffer, 0,0, 4);	    			
	    val = (buffer[0] << 24) | ((buffer[1] & 0xFF) << 16) | ((buffer[2] & 0xFF) << 8) | (buffer[3] & 0xFF);
	    Log.i(LOG_TAG, "Get value " + val + " from memory file. ");
		}
		catch(IOException ex) {
			Log.i(LOG_TAG, "Failed to write bytes to memory file.");
			ex.printStackTrace();
		}
	}
}

package com.example.ashmemtest;

import java.io.FileDescriptor;
import java.io.IOException;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.MemoryFile;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.os.Build;
import android.os.ServiceManager;
import java.io.FileInputStream;

public class MainActivity extends Activity  implements OnClickListener {
		private final static String LOG_TAG = "MainActivity";
		
		IMemoryService memoryService = null;
		MemoryFile memoryFile = null;
		
		private EditText valueText = null;
		private Button readButton = null;
		private Button writeButton = null;
		private Button clearButton = null;
		
	    	@Override
	    	public void onCreate(Bundle savedInstanceState) {
	        	super.onCreate(savedInstanceState);
	        	setContentView(R.layout.main);

			IMemoryService ms = getMemoryService();
			if(ms == null) {        
	        		startService(new Intent("com.example.ashmemtest.server"));
			} else {
				Log.i(LOG_TAG, "Memory Service has started.");
			}

	        	valueText = (EditText)findViewById(R.id.edit_value);
	        	readButton = (Button)findViewById(R.id.button_read);
	        	writeButton = (Button)findViewById(R.id.button_write);
	        	clearButton = (Button)findViewById(R.id.button_clear);

			readButton.setOnClickListener(this);
	        	writeButton.setOnClickListener(this);
	        	clearButton.setOnClickListener(this);
	        
	        	Log.i(LOG_TAG, "Client Activity Created.");
	    	}

	    	@Override
	    	public void onResume() {
			super.onResume();

			Log.i(LOG_TAG, "Client Activity Resumed.");
	    	}

	    	@Override
	    	public void onPause() {
			super.onPause();

			Log.i(LOG_TAG, "Client Activity Paused.");
	    	}
	    
	    	@Override
	    	public void onClick(View v) {
	    		if(v.equals(readButton)) {
	    			int val = 0;
	    		
	    			/*FileInputStream mf = getMemoryFile();
	    			if(mf != null) {
					try {
	    					byte[] buffer = new byte[4];
	    					mf.read(buffer, 0, 4);
	    			
	    					val = (buffer[0] << 24) | ((buffer[1] & 0xFF) << 16) | ((buffer[2] & 0xFF) << 8) | (buffer[3] & 0xFF);
					} catch(Exception ex) {
						Log.i(LOG_TAG, "Failed to read bytes from memory file.");
						ex.printStackTrace();
					}finally{
							try{
								mf.close();
							}catch(Exception es){}
					}
	    			}	*/
	    			
	    			                MemoryFile mf = getMemoryFile();  
                if(mf != null) {  
                try {  
                        byte[] buffer = new byte[4];  
                        mf.readBytes(buffer, 0, 0, 4);  
                  
                        val = (buffer[0] << 24) | ((buffer[1] & 0xFF) << 16) | ((buffer[2] & 0xFF) << 8) | (buffer[3] & 0xFF);  
                } catch(IOException ex) {  
                    Log.i(LOG_TAG, "Failed to read bytes from memory file.");  
                    ex.printStackTrace();  
                }  
                }
	    		
	    			String text = String.valueOf(val);
	    			valueText.setText(text);
	    		} else if(v.equals(writeButton)) {
	    			String text = valueText.getText().toString();
	    			int val = Integer.parseInt(text);
	    		
	    			IMemoryService ms = getMemoryService();
	    			if(ms != null) {
					try {
	    					ms.setValue(val);
					} catch(RemoteException ex) {
						Log.i(LOG_TAG, "Failed to set value to memory service.");
						ex.printStackTrace();
					}
	    			}
	    		} else if(v.equals(clearButton)) {
	    			String text = "";
	    			valueText.setText(text);
	    		}
	    	}
	    
	    	private IMemoryService getMemoryService() {
	    		if(memoryService != null) {
	    			return memoryService;
	    		}
	    	
	    		memoryService = IMemoryService.Stub.asInterface(
	                			ServiceManager.getService("AnonymousSharedMemory"));

			Log.i(LOG_TAG, memoryService != null ? "Succeed to get memeory service." : "Failed to get memory service.");
	    	
	    		return memoryService;
	    	}
	    

	    	/*private FileInputStream getMemoryFile() {

	    		
	    		IMemoryService ms = getMemoryService();
			if(ms == null) {        
	        		startService(new Intent("com.example.ashmemtest.server"));
			} else {
				Log.i(LOG_TAG, "Memory Service has started.");
			}
	    		if(ms != null) {
				try {
	    				ParcelFileDescriptor pfd = ms.getFileDescriptor();
					if(pfd == null) {
						Log.i(LOG_TAG, "Failed to get memory file descriptor.");
						return null;
					}
					Log.i(LOG_TAG, "" +pfd.toString());
					try {
						FileDescriptor fd = pfd.getFileDescriptor();
						if(fd == null) {
							Log.i(LOG_TAG, "Failed to get memeory file descriptor.");
							return null;                      
						}	
						Log.i(LOG_TAG, "" +fd.toString());
								return new FileInputStream(fd);
	    					//memoryFile = new MemoryFile("MemoryFileTest", 4);
					} catch(Exception ex) {
						Log.i(LOG_TAG, "Failed to create memory file.");
						ex.printStackTrace();
					}
	    			} catch(RemoteException ex) {
					Log.i(LOG_TAG, "Failed to get file descriptor from memory service.");
					ex.printStackTrace();
				}
			}
	    	
	    		return null;
	    	}*/
	    	
	    	        private MemoryFile getMemoryFile() {  
            if(memoryFile != null) {  
                return memoryFile;  
            }  
              
            IMemoryService ms = getMemoryService();  
            if(ms != null) {  
            try {  
                    ParcelFileDescriptor pfd = ms.getFileDescriptor();  
                if(pfd == null) {  
                    Log.i(LOG_TAG, "Failed to get memory file descriptor.");  
                    return null;  
                }  
  
                try {  
                    FileDescriptor fd = pfd.getFileDescriptor();  
                    if(fd == null) {  
                        Log.i(LOG_TAG, "Failed to get memeory file descriptor.");  
                        return null;                        
                    }     
  
                        memoryFile = new MemoryFile(fd, 4, "r");  
                } catch(IOException ex) {  
                    Log.i(LOG_TAG, "Failed to create memory file.");  
                    ex.printStackTrace();  
                }  
                } catch(RemoteException ex) {  
                Log.i(LOG_TAG, "Failed to get file descriptor from memory service.");  
                ex.printStackTrace();  
            }  
        }  
          
            return memoryFile;  
        }
	}
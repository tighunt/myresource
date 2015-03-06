/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rtk.android.miracast;

import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pWfdInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.GroupInfoListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.net.wifi.p2p.WifiP2pManager.DialogListener;
/*added by kavin*/
import android.net.wifi.p2p.WifiP2pManager.PersistentGroupInfoListener;
import android.net.wifi.p2p.WifiP2pGroupList;
import android.util.DisplayMetrics;

import android.widget.TextView.OnEditorActionListener;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.KeyEvent;

import android.app.TvManager;
import com.rtk.android.miracast.WFDDeviceList.DeviceActionListener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.CountDownTimer;


public class MiracastActivity extends Activity implements ChannelListener, PersistentGroupInfoListener, DeviceActionListener {

    public static final String TAG = "RTKMiracast";

	public static final int WFD_URL = 0;
	public static final int WFD_CREATEGROUP = 1;
	public static final int WFD_PINANY = 2;
	public static final int WFD_RESET = 3;

	public static final int WFD_STATE_INIT = 0;
	public static final int WFD_STATE_READY = 1;
	public static final int WFD_STATE_CONNECTING = 2;
	public static final int WFD_STATE_CONNECTED = 3;
	public static final int WFD_STATE_DISCONNECTED = 4;
	public int curState = WFD_STATE_INIT;

	private static TvManager mTV;
	private static final int MENU_ID_MIRACAST = Menu.FIRST;
    private static final int MENU_ID_SETTING = Menu.FIRST + 1;
    private WifiP2pManager manager;
	public WifiManager mWifiManager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;
    public WifiP2pDevice mCurConnectedDevice;
    private String ipAddr;
    public TextView text;
    public TextView myDevName;
	public TextView myPin;
    public EditText myShellCmd;
    public WifiP2pDevice thisDev;
	public String PIN = null;

	private static final int DEFAULT_CONTROL_PORT = 8554;
	private static final int MAX_THROUGHPUT = 50;
	public boolean mWfdEnabled = false;
	public boolean mWfdEnabling = false;
	public String devname=null;

	public WFDDeviceList mDeviceList = new WFDDeviceList(this);
	public WFDDeviceDetail mDeviceDetail = new WFDDeviceDetail(this);
	
	/*added by kavin*/
	 WifiP2pGroupList grouplist;
    public  TextView mWpsTextView;
    private Handler mHandler = new Handler();
	public CountDownTimer timer_flag=null;

    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
		updateWfdEnableState(isWifiP2pEnabled);
		if(isWifiP2pEnabled)
				WFDDiscover();
		String str = "SET p2p_go_intent " + "0";
		if (manager != null && channel != null) {
			/*	manager.execCmd(channel, str,new ActionListener() {
					@Override
					public void onSuccess() {
						Log.d("lynn", "go intend exec cmd onSuccess");
					}   
					@Override
					public void onFailure(int reason) {
						Log.e("lynn", "go intend exec cmd onFailure");
					}   
				});*/
		}
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		mTV = (TvManager) this.getSystemService("tv");
 	    mTV.setSource(TvManager.SOURCE_PLAYBACK);
        //getWindow().setUiOptions(0);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,       
                      WindowManager.LayoutParams. FLAG_FULLSCREEN); 
		getRight2Left(this);
        LayoutInflater flater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE); 
        //tiny_li  enterDialog   start
		 mWpsTextView = (TextView)findViewById(R.id.wps_dialog_txt);
	     myPin = (TextView)findViewById(R.id.mPin);
		 ready();
		 Log.e(TAG,"reday");
		 
	//tiny_li  enterDialog finish	
        myShellCmd =  (EditText)findViewById(R.id.shell_cmd);
        myShellCmd.setVisibility(View.INVISIBLE);
        myShellCmd.setOnEditorActionListener(new OnEditorActionListener() {  
            @Override  
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {  
			String cmd = v.getText().toString();
			if(cmd.length() > 10)
			{
				Log.d("[baili]Miracast", "exec cmd: " + cmd);
				/*manager.execCmd(channel, cmd, new ActionListener() {
						@Override
						public void onSuccess() {
						Log.d("[baili]Miracast", "exec cmd onSuccess");
						}
						@Override
						public void onFailure(int reason) {
						Log.e("[baili]Miracast", "exec cmd onFailure");
						}
					});*/
				v.setText("");
			}
            return true;  
            }  
        });  
       
        // add necessary intent values to be matched.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        /*added by kavin*/
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PERSISTENT_GROUPS_CHANGED_ACTION);
        intentFilter.addAction("MediaPlayershutdown");
		intentFilter.addAction("android.intent.action.POWER_DOWN");
		//baili_feng enable wifi if wifi is not enabled
		mWifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		if(mWifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
			if (mWifiManager.setWifiEnabled(true)) {
				Log.d(TAG, "setWifiEnabled ok");
			} else {
				Log.d(TAG, "setWifiEnabled failed");
			}
		}

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
		
		Log.d("Lynn", "setDialogListener");
		manager.setDialogListener(channel, null);
		manager.setDialogListener(channel, new DialogListener() {
			@Override
			public void onShowPinRequested(String pin) {
				Log.d("Lynn", "onShowPinRequested");
				if (pin != null) {
					Log.d("Lynn", "" + pin);
					myPin.setText(String.format(getString(R.string.wifi_wps_pin), pin));
				} else {
					Log.d("Lynn", "" + "pin null");
					myPin.setText(String.format(getString(R.string.wifi_wps_pin), "NULL"));
				}
			}

			@Override
			public void onConnectionRequested(WifiP2pDevice device, WifiP2pConfig config) {
				Log.d("Lynn", "onConnectionRequested device :" + device);
				Log.d("Lynn", "onConnectionRequested config :" + config);
				Log.i(TAG, "WFD_STATE_CONNECTING");
				curState = MiracastActivity.WFD_STATE_CONNECTING;
				String str = getString(R.string.wfd_connecting);
				updateString(str);
				config.groupOwnerIntent = 0;
				if(config.wps.setup == WpsInfo.PBC) {
					Log.i(TAG, "connect to " + config);
					manager.connect(channel, config, new ActionListener() {
						@Override
						public void onSuccess() {
							Log.d(TAG, "connect ok");
						}
						@Override
						public void onFailure(int reason) {
							Log.d(TAG, "connect failed :" + reason);
						}
					});
				} else if(config.wps.setup == WpsInfo.DISPLAY) {
					config.wps.pin = PIN;
					Log.i(TAG, "connect to " + config);
					manager.connect(channel, config, new ActionListener() {
						@Override
						public void onSuccess() {
							Log.d(TAG, "connect ok");
						}
						@Override
						public void onFailure(int reason) {
							Log.d(TAG, "connect failed :" + reason);
						}
					});
				}
			}

			@Override
			public void onAttached(){
				Log.d("Lynn", "onAttached");
			}

			@Override
			public void onDetached(int reason) {
				Log.d("Lynn", "onDetached");
			}
		});

    }
    
    /** register the BroadcastReceiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG,"onResume()");
		if(receiver == null) {
				receiver = new MiracastBroadcastReceiver(manager, channel, this);
				registerReceiver(receiver, intentFilter);
		}
	}
		
	public void resume()
	{	
		if(timer_flag != null) { 
			timer_flag.cancel(); timer_flag = null;
		}
		Log.e(TAG,"time4 new");
		timer_flag = new CountDownTimer(60000, 60000) { 		
			public void onTick(long millisUntilFinished) {		
			} 		
			public void onFinish() { 
				Log.e(TAG,"onFinish()  tiny_li--------------------s");
				retry(getString(R.string.connect_try));
				mWifiManager.setWifiEnabled(false);
			}  
		}.start();

        text = (TextView)findViewById(R.id.textView);        
        text.setText(R.string.TV);
        myDevName = (TextView)findViewById(R.id.myDevName);  
		if(mDeviceDetail != null & mDeviceDetail.thisDevice != null)
		{
			String stid = mDeviceDetail.thisDevice.deviceName + "_" +mDeviceDetail.thisDevice.deviceAddress.toUpperCase(Locale.getDefault());
            myDevName.setText(stid);
		}

		//removePersistentGroup();
		if(isWifiP2pEnabled)
				WFDDiscover();
		PIN = GetPIN();
		//PIN = "12345670";
		Log.d("Lynn", "" + PIN);
		myPin.setText(String.format(getString(R.string.wifi_wps_pin), PIN));
		curState = WFD_STATE_READY;
    }

    @Override
    public void onPause() {
        super.onPause();
        //unregisterReceiver(receiver);
        Log.i(TAG,"onPause()");        
    }
    
    @Override
    public void onStop() {
        super.onStop();
        //unregisterReceiver(receiver);
        Log.i(TAG,"onStop()");        
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
		manager.setDialogListener(channel, null);
		if(receiver != null)
		{
         unregisterReceiver(receiver);
        }		
		if(timer_flag != null) {
        Log.i(TAG, "onDestroy(): timer_flag.cancel() enter");		
		timer_flag.cancel(); timer_flag = null;
		Log.i(TAG, "onDestroy() timer_flag.cancel() finish");
		}       
    }
    
    /**
     * Remove all peers and clear all fields. This is called on
     * BroadcastReceiver receiving a state change event.
     */
    public void resetData() {
		//setIsWifiP2pEnabled(false);
        if (mDeviceList != null) {
            mDeviceList.clearPeers();
        }
        if (mDeviceDetail != null) {
            //fragmentDetails.resetViews();
        }
    }

	/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// add menu MiracastTest
        menu.add(Menu.NONE, MENU_ID_MIRACAST, Menu.NONE, R.string.miracast_list);
        // add menu settings 
        menu.add(Menu.NONE, MENU_ID_SETTING, Menu.NONE, R.string.miracast_setting);
        return super.onCreateOptionsMenu(menu); 
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch (item.getItemId()) {
    	case MENU_ID_MIRACAST:
    		Intent intent = new Intent(this, MiracastTestList.class);
    		startActivity(intent);
    		return true;
    	case MENU_ID_SETTING:
    		return true;
    	default:
    		return false;
    	}
    }
	*/

    public void showDetails(WifiP2pDevice device) {
    }

    public void connect(WifiP2pConfig config) {
        manager.connect(channel, config, new ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MiracastActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void disconnect() {
        //fragment.resetViews();
        manager.removeGroup(channel, new ActionListener() {

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);

            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "remove Group is OK");
            }

        });
    }

    @Override
    public void onChannelDisconnected() {
        // we will try once more
        if (manager != null && !retryChannel) {
            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
            resetData();
            retryChannel = true;
            manager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this,
                    "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void cancelDisconnect() {

        /*
         * A cancel abort request by user. Disconnect i.e. removeGroup if
         * already connected. Else, request WifiP2pManager to abort the ongoing
         * request
         */
        if (manager != null) {
            if (mDeviceList.getDevice() == null
                    || mDeviceList.getDevice().status == WifiP2pDevice.CONNECTED) {
                disconnect();
				  Log.i(TAG, "mDeviceList.getDevice() == null: " );
            } else if (mDeviceList.getDevice().status == WifiP2pDevice.AVAILABLE
                    || mDeviceList.getDevice().status == WifiP2pDevice.INVITED) {

                manager.cancelConnect(channel, new ActionListener() {

                    @Override
                    public void onSuccess() {
                        Toast.makeText(MiracastActivity.this, "Aborting connection",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(MiracastActivity.this,
                                "Connect abort request failed. Reason Code: " + reasonCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

	public void getPeerAddress(){
		new Thread(new Runnable(){
			@Override
			public void run(){
	            String Address= null;
				String port = null;
				String ip = null;
				String name = null;
				int count = 0;

			    if(mCurConnectedDevice!=null)
			    {
					name = mCurConnectedDevice.deviceName;
					Address=mCurConnectedDevice.deviceAddress;
					Log.i(TAG, "Device device kavin getIpAddress is: " + Address);   
			    }

				if(mCurConnectedDevice != null && mCurConnectedDevice.wfdInfo != null){
					if(mCurConnectedDevice.wfdInfo.getControlPort() > 0)
					{
						port = String.valueOf(mCurConnectedDevice.wfdInfo.getControlPort());
					}
				}
				Log.i(TAG, "Device port is: " + port);
			    
				if(mDeviceDetail.connectionInfo.isGroupOwner)
				{
					while(count < 20){
						ip = mDeviceDetail.thisDevice.getPeerIPAddr(Address);
						if(ip != null) break;
						try{
							Thread.sleep(800);
							count++;
						}catch(InterruptedException e){
							e.printStackTrace();
						}
					}
				} else {
					ip = mDeviceDetail.connectionInfo.groupOwnerAddress.getHostAddress();
				}
				Log.i(TAG, "Device ip is: " + ip);
				Log.i(TAG, "Device name is: " + name);

				if(ip != null && port != null)
				{
					String url = "miracast://" + ip + ":" + port;
					Message msg=new Message();
					msg.what=WFD_URL;
					Bundle b =new Bundle();
					b.putString("URL", url);
					b.putString("device", name);
					msg.setData(b);
					mDeviceDetail.mHandler.sendMessage(msg);
				}
				else {
					Message msg=new Message();
					msg.what=WFD_RESET;
					mDeviceDetail.mHandler.sendMessage(msg);
				}
			}
		}).start();
		
		Log.i(TAG, "after reading ip thread start()");
    }

    public void updateWfdEnableState(boolean isEnable) {
        if (isEnable) {
			setDeviceName("REALTEK-LEDTV");
            // WFD should be enabled.
            if (!mWfdEnabled && !mWfdEnabling) {
                mWfdEnabling = true;

                WifiP2pWfdInfo wfdInfo = new WifiP2pWfdInfo();
                wfdInfo.setWfdEnabled(true);
                wfdInfo.setDeviceType(WifiP2pWfdInfo.PRIMARY_SINK);
                wfdInfo.setSessionAvailable(true);
                wfdInfo.setControlPort(DEFAULT_CONTROL_PORT);
                wfdInfo.setMaxThroughput(MAX_THROUGHPUT);
                manager.setWFDInfo(channel, wfdInfo, new ActionListener() {
                    @Override
                    public void onSuccess() {
						Log.i(TAG, "Successfully set WFD info.");
                        if (mWfdEnabling) {
                            mWfdEnabling = false;
                            mWfdEnabled = true;
                            //reportFeatureState();
                        }
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.i(TAG, "Failed to set WFD info with reason " + reason + ".");
                        mWfdEnabling = false;
                    }
                });
            }
        } else {
            // WFD should be disabled.
            mWfdEnabling = false;
            mWfdEnabled = false;
            //reportFeatureState();
            disconnect();
        }
    }
    
    /*added by kavin*/
    public void onPersistentGroupInfoAvailable(WifiP2pGroupList groups) {        
       grouplist=groups;
	   Log.d(TAG, "onPersistentGroupInfoAvailable: " + groups);
	   if(grouplist!=null)
	   {
			   for(WifiP2pGroup group:grouplist.getGroupList()) {
					   Log.d(TAG, "GROUP: " + group.getNetworkName() + " - " + group.getPassphrase());
			   }
	   }
    }  
    public void removePersistentGroup()
    {
		Log.i(TAG, "removePersistentGroup");
        if(grouplist!=null)
        {
			int size = grouplist.getGroupList().size();
            Log.d(TAG, "grouplist size is :" + size);
			if(size == 0) {
			   Message msg=new Message();
			   msg.what=WFD_CREATEGROUP;
			   mDeviceDetail.mHandler.sendMessage(msg);
			}
			int count = 0;
			for(WifiP2pGroup group:grouplist.getGroupList()) {
				if(++count < size) {
            	manager.deletePersistentGroup(channel,group.getNetworkId(),
                            new WifiP2pManager.ActionListener() {
                                 public void onSuccess() {
                                      Log.d(TAG, " delete groupPersistent success");
                                 }
                                 public void onFailure(int reason) {
                                      Log.d(TAG, " delete groupPersistent fail " + reason);
                                 }
                             });
				}
				else {
            	manager.deletePersistentGroup(channel,group.getNetworkId(),
                            new WifiP2pManager.ActionListener() {
                                 public void onSuccess() {
                                      Log.d(TAG, "LAST delete groupPersistent success");
									  Message msg=new Message();
									  msg.what=WFD_CREATEGROUP;
									  mDeviceDetail.mHandler.sendMessage(msg);
                                 }
                                 public void onFailure(int reason) {
                                      Log.d(TAG, "LAST delete groupPersistent fail " + reason);
									  Message msg=new Message();
									  msg.what=WFD_CREATEGROUP;
									  mDeviceDetail.mHandler.sendMessage(msg);
                                 }
                             });
				}
			}
       } else {
               Log.d(TAG, "NO delete groupPersistent");
			   Message msg=new Message();
			   msg.what=WFD_CREATEGROUP;
			   mDeviceDetail.mHandler.sendMessage(msg);
	   }
    }

	public void createNewGroup() {
			Log.i(TAG, "createNewGroup");
			manager.createGroup(channel, new ActionListener() {    	
				@Override
				public void onFailure(int reasonCode) {
					Log.d(TAG, "GroupCreate failed. Reason :" + reasonCode);
					Message msg=new Message();
					msg.what=WFD_PINANY;
					mDeviceDetail.mHandler.sendMessageDelayed(msg,1000);
				}

				@Override
				public void onSuccess() {
					Log.d(TAG, "GroupCreate success");
					Message msg=new Message();
					msg.what=WFD_PINANY;
					mDeviceDetail.mHandler.sendMessageDelayed(msg,1000);
				}
			});
	}

	public void pinAny() {
			Log.i(TAG, "pinAny");
			// start pin any
			WifiP2pConfig config = new WifiP2pConfig();
			config.wps.setup = WpsInfo.DISPLAY;
			curState = WFD_STATE_READY;

			Log.d("Lynn", "start pin any");
			manager.connect(channel, config, new ActionListener() {
				@Override
				public void onSuccess() {
					Log.d("Lynn", "start pin any success");
				}
				@Override
				public void onFailure(int reason) {
					Log.d("Lynn", "start pin any failed :" + reason);
				}
			});
	}

	public void WFDDiscover() {
		manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
			@Override
			public void onSuccess() {
				Log.i(TAG, "Discovery Initiated");
				
			}

			@Override
			public void onFailure(int reasonCode) {
				Log.e(TAG, "Discovery Failed");
			}
		});
	}

	//tiny_li
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (event.getAction() == KeyEvent.ACTION_DOWN) {
		    Log.i(TAG, "KEYCODE="+keyCode);
        	if (keyCode ==111 /*KeyEvent.KEYCODE_ESCAPE */|| keyCode == KeyEvent.KEYCODE_BACK ) {
					if(timer_flag != null) {
					Log.i(TAG, "KeyEvent.KEYCODE_ESCAPE: timer_flag.cancel() enter");
					timer_flag.cancel(); timer_flag = null;
					Log.i(TAG, "KeyEvent.KEYCODE_ESCAPE: timer_flag.cancel() finish");
					}
					mWifiManager.setWifiEnabled(true);
					finish();
        		return true;
			}
    	}
    	
    	return super.onKeyDown(keyCode, event);
	}

	public void retry(String str)
	{
		if(timer_flag != null) { 
			timer_flag.cancel(); timer_flag = null;
		}
		curState = WFD_STATE_INIT;

		AlertDialog isTry = new AlertDialog.Builder(this).create();  
		isTry.setCanceledOnTouchOutside(false);
		isTry.setMessage(str); 
		isTry.setButton(getString(R.string.no),new DialogInterface.OnClickListener() {		   
			public void onClick(DialogInterface dialog, int id) {
				if(timer_flag != null) {
					Log.i(TAG, "AlertDialog.BUTTON_NEGATIVE: timer_flag.cancel() enter");					
					timer_flag.cancel(); timer_flag = null;
					Log.i(TAG, "AlertDialog.BUTTON_NEGATIVE: timer_flag.cancel() finish");					
				}
				finish();
				return	;					
			}	   
		}); 
		isTry.setButton2(getString(R.string.yes), new DialogInterface.OnClickListener() { 		   
			public void onClick(DialogInterface dialog, int id) { 				
				mWpsTextView.setText(R.string.Ready);
				Log.e(TAG, " isTry.setButton2 yes");
				mWifiManager.setWifiEnabled(true);
				resume();
				return	;				
			}	   
		});  

		isTry.show();
	}

	void ready()
	{
		curState = WFD_STATE_INIT;
	     AlertDialog isEnter = new AlertDialog.Builder(this).create();  
         isEnter.setCanceledOnTouchOutside(false);
         isEnter.setMessage(getString(R.string.Ready_dialog));  
         isEnter.setButton(getString(R.string.no), new DialogInterface.OnClickListener() {		   
					public void onClick(DialogInterface dialog, int id) {
						mWifiManager.setWifiEnabled(true);
                        finish(); 
                        return;						
					}	   
					});
		isEnter.setButton2(getString(R.string.yes),new DialogInterface.OnClickListener() { 		   
				public void onClick(DialogInterface dialog, int id) { 
                   resume();			   
				 mWpsTextView.setText(R.string.Ready); 
                 return;					 
				 }	   
				});
        
         isEnter.show(); 
	}
	
	public void getRight2Left(Context cnt)
	{
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int densityDpi = metric.densityDpi;
		String language= cnt.getResources().getConfiguration().locale.getLanguage();
		if(language.equals("ar")||language.equals("iw")||language.equals("fa"))
			setContentView(R.layout.toshiba);
		else
		{
		    if(densityDpi==160)
		    setContentView(R.layout.wait);
		    else
		        setContentView(R.layout.rtk);
		}
	}

	public void updateString(String str) {
		mWpsTextView.setText(str);
	}

	String  GetPIN()
	{
		//int tmp [] = {1,2,3,4,5,6,7};
		int r=0;
		int snum = 0;
		int checksnum = 0;
		String pin = "";
		for (int i=0;i<7;i++) {

			r=(int)(Math.random()*10);
			//r = tmp[i];
			snum = snum*10+r;
			pin += r;
		}	

		checksnum = ComputeCheckSum(snum);
		return pin+checksnum;
	}

	int ComputeCheckSum ( int PIN)
	{
		long  acccum = 0;
		PIN *= 10;
		acccum += 3*((PIN/10000000)%10);
		acccum += 1*((PIN/1000000)%10);
		acccum += 3*((PIN/100000)%10);
		acccum += 1*((PIN/10000)%10);
		acccum += 3*((PIN/1000)%10);
		acccum += 1*((PIN/100)%10);
		acccum += 3*((PIN/10)%10);
		int digit = (int)(acccum %10);
		return ((10-digit)%10);
	}

	public void setDeviceName(String str) {
		Log.d(TAG, "setDeviceName :" + str);
		manager.setDeviceName(channel, str, new ActionListener() {
			@Override
			public void onSuccess() {
				Log.d(TAG, "setDeviceName ok");
			}   
			@Override
			public void onFailure(int reason) {
				Log.d(TAG, "setDeviceName failed :" + reason);
			}   
		}); 
	} 
}

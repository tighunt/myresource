package com.rtk.mediabrowser;


import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;


import android.app.Activity;
import android.app.Instrumentation;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnHoverListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import android.app.TvManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

public class DivxSetup extends Activity {

	private String TAG = "DivxSetup";
	private Resources m_ResourceMgr = null;
	private ListView m_lvDivxList;
	private ImageView focusView;
	private View m_rlClues;
	private DivXListAdapter m_adpDivxListAdapter = null;
	private TextView m_tvTitleRegisterChoice; 
	private TextView m_tvClue1,
					 m_tvClue2,
					 m_tvClue3;
	private TextView guide_arrows = null;
	private TextView guide_select = null;
	private TextView guide_ok = null;
	private TextView guide_enter = null;
	private ImageView registerChoice_bg = null;
	
	private Button m_btnChoice = null;
	private Button bt_yes = null;
	private Button bt_no = null;
	
	private View registerChoice = null;
	private View bt_single = null;
	private View bt_yes_no = null;
	
	
	private boolean mIsActivateTest =true;
	private TvManager mTv;
	Message_not_avaible msg_notavaible = null;
	long mLastNotAvailableShowTime = 0;
	private Handler mCheckTimerHandler = null;
	
	QuickMenu quickmenu = null;
    QuickMenuAdapter qkAdapter = null;
    int mQuickMenuIndex = 0;
    
	String[] arr = new String[2];
	String[] divx_register_clues   = new String[5];
	String[] divx_confirm_clues    = new String[4];
	String[] divx_deregister_clues = new String[4];
	
	LayoutInflater mInflater = null;
	
	private MediaApplication mediaApp = null;
	private MediaPlayer mPlayer = null;
	private BookMark mVideoBookMark = null;
	
	private Locale locale = null;
	private String language = null;
	
	private String mRegisterFileName = null;
	
	private File mRegisterFile = null;
	
	private boolean isSetupStatus = false;
	private boolean isDivxVODStatus = false;
	private boolean isRegister = false;

	private boolean isRight2Left = false;
	
	int fromX,fromY,toX,toY;
	private Handler handler;
	private boolean foucsVisiable = false;
	
	private final int FOCUS_MSG = 5;
	private final int UNFOCUS_MSG = 6;
	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        isRight2Left = MediaBrowserConfig.getRight2Left(getApplicationContext());
	        if(isRight2Left)
	        	setContentView(R.layout.divx_a);
	        else
	        	setContentView(R.layout.divx);
	        m_ResourceMgr = this.getResources();
	        mediaApp = (MediaApplication)getApplication();
	        mPlayer = mediaApp.getMediaPlayer();
	        
	        String path = getFilesDir().getPath();
	        String fileName = path.concat("/VideoBookMark.bin");
	        mVideoBookMark = mediaApp.getBookMark(fileName); //new BookMark(fileName);
	        
	        mRegisterFileName = path.concat("/RegisterFile.bin");
	        mRegisterFile = new File(mRegisterFileName);
	        
	        mTv = (TvManager) this.getSystemService("tv");
	        
	        //First time registration
	        if(mTv.isDeviceActivated() && !mRegisterFile.isFile()) 
			{
	        	try {
					mRegisterFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	        guide_arrows = (TextView)findViewById(R.id.divx_guide_arrows);
	        guide_select = (TextView)findViewById(R.id.divx_guide_select);
	        guide_ok = (TextView)findViewById(R.id.divx_guide_ok);
	        guide_enter = (TextView)findViewById(R.id.divx_guide_enter);
	        
	        guide_arrows.setVisibility(View.INVISIBLE);
	        guide_select.setVisibility(View.INVISIBLE);
	        
	        focusView = (ImageView)findViewById(R.id.focus);
	        
	        registerChoice_bg = (ImageView)findViewById(R.id.registerChoice_bg);
	        registerChoice = (View)findViewById(R.id.registerChoice);
	        m_lvDivxList = (ListView)registerChoice.findViewById(R.id.menu_list);
	        
	        m_rlClues = (View)findViewById(R.id.clues_relativelayout);
	        bt_single = (View)m_rlClues.findViewById(R.id.bt_single);
	        m_btnChoice = (Button)bt_single.findViewById(R.id.option);
	        
	        bt_yes_no = (View)m_rlClues.findViewById(R.id.bt_yes_no);
	        bt_yes = (Button)bt_yes_no.findViewById(R.id.bt_yes);
	        bt_no = (Button)bt_yes_no.findViewById(R.id.bt_no);
	        bt_yes.requestFocus();
	        
	        
	        m_tvTitleRegisterChoice = (TextView)findViewById(R.id.title_registerChoice);	        
	        m_tvTitleRegisterChoice.setText((String) m_ResourceMgr.getText(R.string.divx_setup));
            
	        m_tvClue1 = (TextView)findViewById(R.id.clue1);
	        m_tvClue2 = (TextView)findViewById(R.id.clue2);
	        m_tvClue3 = (TextView)findViewById(R.id.clue3);
	        
	        arr[0] = (String) m_ResourceMgr.getText(R.string.divx_item_register);
	        arr[1] = (String) m_ResourceMgr.getText(R.string.divx_item_deregister);
	        
	        divx_register_clues[0] = (String) m_ResourceMgr.getText(R.string.divx_register_clue0);
	        divx_register_clues[1] = (String) m_ResourceMgr.getText(R.string.divx_register_clue1);
	        divx_register_clues[2] = (String) m_ResourceMgr.getText(R.string.divx_register_clue2);
			divx_register_clues[3] = (String) m_ResourceMgr.getText(R.string.divx_register_clue3);
			divx_register_clues[4] = (String) m_ResourceMgr.getText(R.string.divx_register_clue4);

	        divx_confirm_clues[0]  = (String) m_ResourceMgr.getText(R.string.divx_confirm_clue0);
	        divx_confirm_clues[1]  = (String) m_ResourceMgr.getText(R.string.divx_confirm_clue1);
	        divx_confirm_clues[2]  = (String) m_ResourceMgr.getText(R.string.divx_confirm_clue2);
	        divx_confirm_clues[3]  = (String) m_ResourceMgr.getText(R.string.divx_confirm_clue3);
	        
	        divx_deregister_clues[0]  = (String) m_ResourceMgr.getText(R.string.divx_deregister_clue0);
	        divx_deregister_clues[1]  = (String) m_ResourceMgr.getText(R.string.divx_deregister_clue1);
	        divx_deregister_clues[2]  = (String) m_ResourceMgr.getText(R.string.divx_deregister_clue2);
	        
	        locale = getResources().getConfiguration().locale;
			language = locale.getISO3Language();
			if(language.compareTo("zho") == 0)
				divx_deregister_clues[3] = (String) m_ResourceMgr.getText(R.string.divx_deregister_clue3);
	        
	        m_adpDivxListAdapter = new  DivXListAdapter(this);
	        
	        m_lvDivxList.setAdapter(m_adpDivxListAdapter);
	        m_lvDivxList.setOnItemClickListener(Setup_itemClickListener);
	        isSetupStatus = true;
	    	isDivxVODStatus = false;
	    	
	    	fromX =0;
	        fromY =0;
	        toX = 623;
	    	setListTouch();
	    	
	    	handler = new Handler(){
	    		public void handleMessage(Message msg) {
	    			switch(msg.what){
	    			case FOCUS_MSG:
		            	if(!foucsVisiable)
		            	{
		            		focusView.setImageDrawable(
		            				getApplicationContext().getResources().getDrawable(
		            						R.drawable.list_common_item_focus));
		            		focusView.setVisibility(View.VISIBLE);
		            		setFocusLayout(toX,toY);
		            		foucsVisiable = true;
		            	}
		            	else{
		    			        setFocusLayout(toX,toY);
		        		}
					break;
					case UNFOCUS_MSG:
						//remove animation
		            	if(foucsVisiable)
		            	{
		            		focusView.setImageResource(R.drawable.blank);
		            		focusView.setVisibility(View.INVISIBLE);
		            		foucsVisiable = false;
		            	}
					break;
	    			}
	    		}
	    	};
	        
	        if(!mRegisterFile.isFile())
	        {
	        	m_lvDivxList.setSelection(0);
	        }else
	        {
	        	if(mTv.isDeviceActivated())
	        		m_lvDivxList.setSelection(1);
	        	else
	        		m_lvDivxList.setSelection(0);
	        }
	        
	        m_lvDivxList.requestFocus(); 
	        m_lvDivxList.setFocusableInTouchMode(true);
	        
	        mCheckTimerHandler = new Handler(){
				@Override  
		        public void handleMessage(Message msg)  
		        { 
		        	switch (msg.what)  
		            {  
		              case 2:
		            	  if(msg_notavaible.isShowing())
		            	  {
		            		  msg_notavaible.dismiss();
		            	  }
		            	  break;     	
		             default:
		            	 break;
		            }  	          
		          super.handleMessage(msg);  
		        }  
		    };
	        
	     // createQuickMenu();
	        qkAdapter = new QuickMenuAdapter(DivxSetup.this);
	        quickmenu=new QuickMenu(
	        		DivxSetup.this, qkAdapter);
	        OnKeyListener quickmenuKeyListener = new OnKeyListener()
	        {
	        	@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (event.getAction() == KeyEvent.ACTION_DOWN)
		        	{
						if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
						{
							if (mQuickMenuIndex == 0)
							{
								ComponentName componetName = new ComponentName("com.tsb.tv", "com.tsb.tv.Tv_strategy");
								Intent intent= new Intent();
								intent.setComponent(componetName);
								startActivity(intent);
							}
							else if(mQuickMenuIndex == 1)
							{
								ComponentName componetName = new ComponentName("com.android.settings", "com.android.settings.Settings");
								Intent intent= new Intent();
								intent.setComponent(componetName);
								startActivity(intent);
							}
							
							return true;
						}
			        	if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
			        		return true;
			        	else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
			        		return true;
			        	else if (keyCode == KeyEvent.KEYCODE_DPAD_UP)
			        	{
			        		ListView quickMenuContent = quickmenu.getListView();
			        		int position = quickMenuContent.getSelectedItemPosition();
			        		if(position == 0)
			        		{
			        			quickMenuContent.setSelection(quickMenuContent.getCount()-1);
			        		}
			        		return false;		
			        	}
			        	else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
			        	{
			        		ListView quickMenuContent = quickmenu.getListView();
			        		int position = quickMenuContent.getSelectedItemPosition();
			        		if(position == quickMenuContent.getCount()-1)
			        		{
			        			quickMenuContent.setSelection(0);
			        		}
			        		return false;
			        	}
		        	}
					else if (event.getAction() == KeyEvent.ACTION_UP)
					{
						if (keyCode == KeyEvent.KEYCODE_Q || keyCode == 227 || keyCode == KeyEvent.KEYCODE_BACK)
			        		quickmenu.dismiss();
						else if(keyCode ==  KeyEvent.KEYCODE_MENU || keyCode == 220)
						{
							if(null == msg_notavaible)
							{
								msg_notavaible = new Message_not_avaible(DivxSetup.this);
							}
							
							msg_notavaible.show_msg_notavailable();
							
							mLastNotAvailableShowTime = (new Date(System.currentTimeMillis())).getTime();
							new Thread(new Runnable() {
	            	    		public void run() {
	            	    			long curtime = 0;
	            	    			while(true)
	            	    			{
	            	    				if(msg_notavaible.isShowing() == false)
	            	    					break;
	            	    				curtime = (new Date(System.currentTimeMillis())).getTime();
	            	    				if(curtime - mLastNotAvailableShowTime > 3000)
	            		    			{
	            		    				Message msg = new Message();
	            		    				msg.what = 2;
	            		    				mCheckTimerHandler.sendMessage(msg);
	            		    			}            	    				 
	            	    				try {
	            							Thread.sleep(100);
	            						} catch (InterruptedException e) {
	            							e.printStackTrace();
	            						}	
	            	    			}
	            	    		}
	            	    	}).start();
						}
					}
					return false;
				}
	        	
	        };
	        OnItemSelectedListener quickmenuItemSelectedListener = new OnItemSelectedListener()
	        {

	        	@Override
				public void onItemSelected(AdapterView<?> arg0, View view,
						int position, long arg3) {
	        		mQuickMenuIndex = position;
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					Log.d(TAG, "Quick Menu ListView onNothingSelected");
				}
	        	
	        };
	        quickmenu.AddOnItemSelectedListener(quickmenuItemSelectedListener);
	        quickmenu.AddOnKeyClickListener(quickmenuKeyListener);

	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    }
	
	private void setListTouch(){
		/*m_lvDivxList.requestFocus();
		m_lvDivxList.requestFocusFromTouch();
		m_lvDivxList.setSelector(R.drawable.selector_list_background);
		m_lvDivxList.setSelection(0);*/
		m_lvDivxList.setOnHoverListener(new OnHoverListener(){
        	@Override
            public boolean onHover(View v,MotionEvent event){

            	switch(event.getAction()){
            	case MotionEvent.ACTION_HOVER_ENTER:
            		Log.e(TAG, "MotionEvent.ACTION_HOVER_ENTER");
            		m_lvDivxList.setSelector(R.drawable.selector_list_background2);
            		m_adpDivxListAdapter.notifyDataSetChanged();
            		break;
            	case MotionEvent.ACTION_HOVER_MOVE:
            		Log.e(TAG, "MotionEvent.ACTION_HOVER_MOVE");
            		if(!foucsVisiable){
            			m_lvDivxList.setSelector(R.drawable.selector_list_background2);
            			m_adpDivxListAdapter.notifyDataSetChanged();
            		}
            		transY2Pos((int)event.getRawY());
            		break;

            	case MotionEvent.ACTION_HOVER_EXIT:
            		Log.e(TAG, "MotionEvent.ACTION_HOVER_EXIT");
            		HideFocusAction();
            		m_lvDivxList.setSelector(R.drawable.selector_list_background);
            		m_adpDivxListAdapter.notifyDataSetChanged();
            		break;
            	}
            	return false;
        	}
        });
	}
	
	private static final int listTop = 489;
    private static final int listH = 69;
    private Instrumentation in =new Instrumentation();
    
	private void transY2Pos(int y){
		int line =(y - listTop)/listH;
		if(line > m_adpDivxListAdapter.getCount() -1)
			line = m_adpDivxListAdapter.getCount() -1;
		toY = listTop + line * listH;
		if(line <0 && foucsVisiable){
	    	handler.removeMessages(UNFOCUS_MSG);
	        Message msg = handler.obtainMessage(UNFOCUS_MSG);
	        handler.sendMessage(msg);
		}else if(line >=0){
			if(m_adpDivxListAdapter.getCount() == 1)
			{
				setHoverAction();
				m_lvDivxList.setSelection(line);
			}
			else if(m_adpDivxListAdapter.getCount() == 2)
			{
				if(!mRegisterFile.isFile())
       			{
					if(line != 0)
						toY = listTop;
					setHoverAction();
					
					m_lvDivxList.setSelection(0);
       			}else 
       			{
       				if(mTv.isDeviceActivated())
       				{
       					if(line != 1)
							toY = listTop + listH;
       					setHoverAction();
       					
						m_lvDivxList.setSelection(1);
       				}else
       				{
       					setHoverAction();
       					m_lvDivxList.setSelection(line);
       				}
       			}
			}
		}

		if(m_lvDivxList.getSelectedItemPosition() <0){
			fakeKeyUp();
		}
	}
	
	private void HideFocusAction(){
		handler.removeMessages(UNFOCUS_MSG);
		Message msg = handler.obtainMessage(UNFOCUS_MSG);
		handler.sendMessage(msg);
	}
	
	private void setHoverAction(){
    	handler.removeMessages(FOCUS_MSG);
        Message msg = handler.obtainMessage(FOCUS_MSG);
        handler.sendMessage(msg);
	}
	// sendKeyDownUpSync not able to work in main thread
	
	private void fakeKeyUp(){
		new Thread(new Runnable() { 
			public void run()
			{ 
				in.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP);
			} }).start();
	}
	
	private void setFocusLayout(int x, int y)
	{
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) focusView.getLayoutParams();
		lp.leftMargin =  x;
		lp.topMargin =  y;
		focusView.setLayoutParams(lp);
	}
	
	private void hideRegisterLayout()
	{
		HideFocusAction();
		registerChoice.setVisibility(View.INVISIBLE);
		registerChoice_bg.setVisibility(View.INVISIBLE);
	}
	
	private void showRegisterLayout()
	{
		registerChoice.setVisibility(View.VISIBLE);
		registerChoice_bg.setVisibility(View.VISIBLE);
	}
	
	 @Override
	    public boolean dispatchKeyEvent(KeyEvent event)
	    {
		 	if(event.getAction() == KeyEvent.ACTION_UP)
	    	{
		    	switch(event.getKeyCode())
		    	{
				    case KeyEvent.KEYCODE_F:
					{
						mIsActivateTest =false;
					System.out.println("mIsActivateTest is :"+mIsActivateTest);
					break;
					}
				    case KeyEvent.KEYCODE_T:
					{
						mIsActivateTest =true;
					
						System.out.println("mIsActivateTest is :"+mIsActivateTest);
						break;
					}
				    case KeyEvent.KEYCODE_Q :
				    case 227: 
		        	{
		        		
		        		if(quickmenu.isShowing() == true)
		        			quickmenu.dismiss();
		        		else
		        		{
		        			quickmenu.showQuickMenu(14, 14);
		        			quickmenu.setTimeout();
		        		}
		        		break;
		        	}
		    	}		    	
		    }
			return super.dispatchKeyEvent(event);
	    
	    }
	 
	 private OnItemSelectedListener itemSelectedListener = new OnItemSelectedListener() {
		 public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
		 }
		 public void onNothingSelected(AdapterView<?> arg0) {
		 }
	 };
	 
	 private class DivXListAdapter extends BaseAdapter
	 {
		 private Context mContext;
		 private int[] textColor = new int[]{R.color.white,R.color.grey};
		 public DivXListAdapter(Context mContext)
		 {
			 this.mContext = mContext;
		 }
		 class ViewHolder {
			 TextView divxText;
		 }
		 
		 @Override
		 public int getCount() {
			 // TODO Auto-generated method stub
			 if(isSetupStatus)
				 return 1;
			 else
				 return 2;
		 }

		 @Override
		 public Object getItem(int position) {
			 // TODO Auto-generated method stub
			 return null;
		 }

		 @Override
		 public long getItemId(int position) {
			 // TODO Auto-generated method stub
			 return 0;
		 }
  
		 @Override
		 public View getView(int position, View convertView, ViewGroup parent) {
			 // TODO Auto-generated method stub
			 ViewHolder holder;
			 
			 holder = new ViewHolder();
			 
			 if(convertView == null)
			 {	
				 mInflater = LayoutInflater.from(DivxSetup.this);
				 if(isRight2Left)
					 convertView = mInflater.inflate(R.layout.divx_list_a, null);
				 else
					 convertView = mInflater.inflate(R.layout.divx_list, null);
			     holder.divxText = (TextView)convertView.findViewById(R.id.divx_item);
			     convertView.setTag(holder);
			 }	
			 else 
				 holder = (ViewHolder)convertView.getTag();
			 
			 if(isSetupStatus)
			 {
				 if(position == 0)
				 {
					 holder.divxText.setText(R.string.divx_title);
					 XmlResourceParser xpp=mContext.getResources().getXml(R.drawable.list_text_color); 
					 try {
						 ColorStateList csl= ColorStateList.createFromXml(getResources(),xpp);
	
						 holder.divxText.setTextColor(csl);
	
					 } catch (Exception e) {
						// TODO: handle exception
						 e.printStackTrace();
					 }
				 }
			 }else
			 {
				 if(position == 0)
				 {
					 holder.divxText.setText(arr[0]);	
					 if(mTv.isDeviceActivated())
					 {
						 holder.divxText.setTextColor(mContext.getResources().getColor(textColor[1]));
					 }
					 else
					 { 
						 XmlResourceParser xpp=mContext.getResources().getXml(R.drawable.list_text_color); 
						 try {
							 ColorStateList csl= ColorStateList.createFromXml(getResources(),xpp);
		
							 holder.divxText.setTextColor(csl);
		
						 } catch (Exception e) {
							// TODO: handle exception
							 e.printStackTrace();
						 }
					 }
				 } 
				 else if(position == 1)
				 {
					 holder.divxText.setText(arr[1]);
					
					 if(!mRegisterFile.isFile())
					 {
						 holder.divxText.setTextColor(mContext.getResources().getColor(textColor[1]));
					 }
					 else
					 {
						 XmlResourceParser xpp=mContext.getResources().getXml(R.drawable.list_text_color); 
						 try {
							 ColorStateList csl= ColorStateList.createFromXml(getResources(),xpp);

							 holder.divxText.setTextColor(csl);

						 } catch (Exception e) {
							// TODO: handle exception
							 e.printStackTrace();
						 }
					 }
				 }
			 }
			
			 return convertView;
		 }
	 }
	 private class Message_not_avaible extends PopupWindow
		{
			private Activity context;
			private RelativeLayout rp = null;
			public TextView message = null;
			
			LayoutInflater mInflater=null;
			
			Message_not_avaible(Activity mContext)
			{
				super(mContext);					
				
				this.context=mContext;
				
				mInflater = LayoutInflater.from(context);
			    rp=(RelativeLayout) mInflater.inflate(R.layout.message_not_available, null);
			    message = (TextView)rp.findViewById(R.id.not_available);    
			    setContentView(rp);	
			}
			
			public void show_msg_notavailable()
			{
				TextPaint paint = message.getPaint(); 
				int len = (int) paint.measureText((String) m_ResourceMgr.getText(R.string.toast_not_available))+102;
				message.setText(m_ResourceMgr.getText(R.string.toast_not_available));
				setHeight(72);
				setWidth(len);
				
				this.setFocusable(true);
				this.setOutsideTouchable(true);
				this.showAtLocation(rp, Gravity.LEFT| Gravity.BOTTOM, 18, 18);
				
			}	
		}
	 
	 class CluesOnKeyListener implements OnKeyListener
	    {
	        public boolean onKey(View view, int keyCode, KeyEvent event)
	        { 
	        	if (event.getAction() == KeyEvent.ACTION_DOWN)
	        	{
		        	if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
		        	{                            
		        		if(m_btnChoice.getTag() == "yes")
		        		{         
		        			m_btnChoice.setTag("no");
		        			m_btnChoice.setBackgroundResource(R.drawable.list_common_item_focus);  
		        			m_btnChoice.setText(divx_confirm_clues[3]);
		        			m_btnChoice.requestFocus();
		        		} 
		        		else if(m_btnChoice.getTag() == "no")
		        		{
		        			m_btnChoice.setTag("yes");
		        			m_btnChoice.setText(divx_confirm_clues[2]);
		        			m_btnChoice.setBackgroundResource(R.drawable.list_common_item_focus);	
		        			m_btnChoice.requestFocus();
		        		}		        			
		        	}
	        	}
	        	
	        	return false;
	        }
	    }
	 class QuickMenuAdapter extends BaseAdapter 
	 {
		public View LastSelectedItem_View = null;
		int[] menu_name = new int[] {R.string.quick_menu_tvapp, R.string.quick_menu_sysset};
	    String[] menu_option = new String[] {"", ""};
	        
	    int[] visibility = new int[]{
	    	View.INVISIBLE,
			View.INVISIBLE,
		};
	    private LayoutInflater mInflater;
			
		class ViewHolder {
			TextView menu_name;
			ImageView left;
			TextView menu_option;
			ImageView right;
		}
			
			
		public QuickMenuAdapter(Context context)
	    {
			mInflater = LayoutInflater.from(context);
	    }
			
			
		public void setVisibility(int position,int isVisible)
		{
			visibility[position]=isVisible;				
		}
	       
	    @Override
	    public int getCount() {
	    	return 2;
	    }

	    public int getViewTypeCount() {
	      	return 1;
	    }
	        
	    public int getItemViewType(int position) {
	       	return 0;
	    }
	        
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	ViewHolder holder;
	        	
	        if (convertView == null) {
	        	
	        	if(true == MediaBrowserConfig.getRight2Left(getApplicationContext()))
        			convertView = mInflater.inflate(R.layout.quick_list_row_a, null);
                else
                	convertView = mInflater.inflate(R.layout.quick_list_row, null);
	        	
		       	holder = new ViewHolder();
		       	holder.menu_name = (TextView)convertView.findViewById(R.id.menu_name);
		       	holder.menu_option = (TextView)convertView.findViewById(R.id.menu_option);
		       	holder.left = (ImageView)convertView.findViewById(R.id.left_arrow);
		       	holder.right = (ImageView)convertView.findViewById(R.id.right_arrow);
		       	convertView.setTag(holder);
	        } 
	        else 
	        {
	        	holder = (ViewHolder)convertView.getTag();
	        }
	        
	        holder.menu_name.setText(menu_name[position]);
	        holder.menu_option.setText(menu_option[position]);

	        holder.left.setVisibility(visibility[position]);
	        holder.right.setVisibility(visibility[position]);
	            
	        return convertView;
	    }
	        
	    @Override
	    public Object getItem(int position) {
	    	return null;
	    }
	        	
	    @Override
	    public long getItemId(int position) {
	        return 0;
	    }
	 }
	        

	 private OnItemClickListener itemClickListener = new OnItemClickListener() 
	 {	 
		 public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
		 {
			 if (position == 0)
			 {
				 if(!mTv.isDeviceActivated())
				 {
					 hideRegisterLayout();
					 Log.d(TAG, "DivX Register");
					 
					 m_rlClues.setVisibility(View.VISIBLE);
					 m_rlClues.setLayoutParams(m_rlClues.getLayoutParams());
					
					 Locale locale = getResources().getConfiguration().locale;
					 String language = locale.getISO3Language();
					 if(language.compareTo("zho") == 0)
					 {
						 m_tvClue1.setVisibility(View.VISIBLE);
						 m_tvClue1.setText(divx_register_clues[0]+"\n"+divx_register_clues[3]);
						 m_tvClue2.setVisibility(View.VISIBLE);
						 m_tvClue2.setText(divx_register_clues[1] + mTv.registerDivX());
						 m_tvClue3.setVisibility(View.VISIBLE);
						 m_tvClue3.setText(divx_deregister_clues[3]+divx_register_clues[4]+divx_register_clues[2]);
					 }else
					 {
						 m_tvClue1.setVisibility(View.VISIBLE);
						 m_tvClue1.setText(divx_register_clues[0]+"\n"+divx_register_clues[3]);
						 m_tvClue2.setVisibility(View.VISIBLE);
						 m_tvClue2.setText(divx_register_clues[1] + mTv.registerDivX());
						 m_tvClue3.setVisibility(View.VISIBLE);
						 m_tvClue3.setText(divx_register_clues[2]+"\n"+divx_register_clues[4]);
					 }
					 
					 bt_yes_no.setVisibility(View.INVISIBLE);
					 bt_single.setVisibility(View.VISIBLE);
				     
				     isSetupStatus = false;
				     isDivxVODStatus = false;
				     isRegister = true;
				     
				     guide_arrows.setVisibility(View.INVISIBLE);
				     guide_select.setVisibility(View.INVISIBLE);
				     guide_ok.setText(DivxSetup.this.getResources().getString(R.string.guide_back));
				     guide_enter.setText(DivxSetup.this.getResources().getString(R.string.guide_return));
				     
				     m_btnChoice.requestFocus();
				     m_btnChoice.setOnClickListener(new OnClickListener()
				     {
						@Override
						public void onClick(View v) 
						{	
							m_rlClues.setVisibility(View.INVISIBLE);
							
							showRegisterLayout();
							m_adpDivxListAdapter.notifyDataSetChanged();
							
							m_lvDivxList.requestFocusFromTouch();
							m_lvDivxList.requestFocus();
							m_lvDivxList.setSelection(0);
							m_lvDivxList.setSelector(R.drawable.selector_list_background);
							
							guide_arrows.setVisibility(View.INVISIBLE);
						    guide_select.setVisibility(View.INVISIBLE);
						    guide_ok.setText(DivxSetup.this.getResources().getString(R.string.guide_ok));
						    guide_enter.setText(DivxSetup.this.getResources().getString(R.string.guide_enter));
						}
				     });
				 }else
				 {
					return;
				 }
			 }
			 else if(position == 1)
			 {
				 if(mRegisterFile.isFile())
				 {
					 hideRegisterLayout();
					 
					 isSetupStatus = false;
				     isDivxVODStatus = false;
				     isRegister = false;
					 
					 Log.d(TAG, "DivX Deregister");
					 m_rlClues.setVisibility(View.VISIBLE);
					 m_rlClues.setLayoutParams(m_rlClues.getLayoutParams());
					 if(mTv.isDeviceActivated())
					 {
						final int ori_height = m_rlClues.getLayoutParams().height;
						m_rlClues.getLayoutParams().height = 240;
						//when textsize is 18sp, 240 unit fit to 2 lines; 
						//every one more line added,we need add 34 unit
						m_rlClues.invalidate();  //or  view.requestLayout()
						m_tvClue1.setVisibility(View.VISIBLE);
						m_tvClue1.setText(divx_confirm_clues[0]);
						m_tvClue2.setVisibility(View.VISIBLE);
						m_tvClue2.setText(divx_confirm_clues[1]);
						m_tvClue3.setVisibility(View.GONE);
						
						guide_arrows.setVisibility(View.VISIBLE);
		     			guide_select.setVisibility(View.VISIBLE);
		     			guide_ok.setText(DivxSetup.this.getResources().getString(R.string.guide_ok));
		     			guide_enter.setText(DivxSetup.this.getResources().getString(R.string.guide_enter));
					    
		     			bt_yes_no.setVisibility(View.VISIBLE);
						bt_single.setVisibility(View.INVISIBLE);
						bt_yes.requestFocus();
						bt_yes.setOnClickListener(new OnClickListener(){

							@Override
							public void onClick(View v) {
								m_rlClues.getLayoutParams().height = ori_height;
								m_rlClues.invalidate();
								
								m_tvClue1.setVisibility(View.VISIBLE);
								m_tvClue2.setVisibility(View.VISIBLE);
								m_tvClue3.setVisibility(View.VISIBLE);
								
								guide_arrows.setVisibility(View.VISIBLE);
				     			guide_select.setVisibility(View.VISIBLE);
				     			guide_ok.setText(DivxSetup.this.getResources().getString(R.string.guide_ok));
				     			guide_enter.setText(DivxSetup.this.getResources().getString(R.string.guide_enter));
						       
						        ResetDivxState();
								
								if(language.compareTo("zho") == 0)
								{
									m_tvClue1.setText(divx_deregister_clues[0]+"\n"+mTv.deregisterDivX());
									m_tvClue2.setText(divx_deregister_clues[3]+divx_register_clues[4]+divx_deregister_clues[1]);
									m_tvClue3.setText(divx_deregister_clues[2]);
								}else
								{
									m_tvClue1.setText(divx_deregister_clues[0]+"\n"+mTv.deregisterDivX());
									m_tvClue2.setText(divx_deregister_clues[1]+"\n"+divx_register_clues[4]);
									m_tvClue3.setText(divx_deregister_clues[2]);
								}
								
								bt_yes.requestFocus();
								bt_yes.setOnClickListener(new OnClickListener(){

									@Override
									public void onClick(View v) {

										guide_arrows.setVisibility(View.INVISIBLE);
									    guide_select.setVisibility(View.INVISIBLE);
									    guide_ok.setText(DivxSetup.this.getResources().getString(R.string.guide_back));
									    guide_enter.setText(DivxSetup.this.getResources().getString(R.string.guide_return));
										
										if(language.compareTo("zho") == 0)
										{
											m_tvClue1.setVisibility(View.VISIBLE);
											m_tvClue1.setText(divx_register_clues[0]+"\n"+divx_register_clues[3]);
											m_tvClue2.setVisibility(View.VISIBLE);
											m_tvClue2.setText(divx_register_clues[1] + mTv.registerDivX());
											m_tvClue3.setVisibility(View.VISIBLE);
											m_tvClue3.setText(divx_deregister_clues[3]+divx_register_clues[4]+divx_register_clues[2]);
										}else
										{
											m_tvClue1.setVisibility(View.VISIBLE);
											m_tvClue1.setText(divx_register_clues[0]+"\n"+divx_register_clues[3]);
											m_tvClue2.setVisibility(View.VISIBLE);
											m_tvClue2.setText(divx_register_clues[1] + mTv.registerDivX());
											m_tvClue3.setVisibility(View.VISIBLE);
											m_tvClue3.setText(divx_register_clues[2]+"\n"+divx_register_clues[4]);
										}
									    
										bt_yes_no.setVisibility(View.INVISIBLE);
										bt_single.setVisibility(View.VISIBLE);
										m_btnChoice.requestFocus();
									    m_btnChoice.setOnClickListener(new OnClickListener(){
											@Override
											public void onClick(View v) {
											// TODO Auto-generated method stub
												m_rlClues.setVisibility(View.INVISIBLE);
												m_adpDivxListAdapter.notifyDataSetChanged();
												showRegisterLayout();
												
												m_lvDivxList.requestFocusFromTouch();
												m_lvDivxList.requestFocus();
												m_lvDivxList.setSelection(1);
												m_lvDivxList.setSelector(R.drawable.selector_list_background);
												
												guide_arrows.setVisibility(View.INVISIBLE);
								     			guide_select.setVisibility(View.INVISIBLE);
								     			guide_ok.setText(DivxSetup.this.getResources().getString(R.string.guide_ok));
								     			guide_enter.setText(DivxSetup.this.getResources().getString(R.string.guide_enter));
											}
								        });
									}
								});
						        
								bt_no.setOnClickListener(new OnClickListener(){

									@Override
									public void onClick(View v) {
										m_rlClues.setVisibility(View.INVISIBLE);
										m_adpDivxListAdapter.notifyDataSetChanged();
										showRegisterLayout();
										
										m_lvDivxList.requestFocusFromTouch();
										m_lvDivxList.requestFocus();
										m_lvDivxList.setSelection(1);
										m_lvDivxList.setSelector(R.drawable.selector_list_background);
										
										guide_arrows.setVisibility(View.INVISIBLE);
						     			guide_select.setVisibility(View.INVISIBLE);
						     			guide_ok.setText(DivxSetup.this.getResources().getString(R.string.guide_ok));
						     			guide_enter.setText(DivxSetup.this.getResources().getString(R.string.guide_enter));
									}
									
								});
							}
						});
						
					   bt_no.setOnClickListener(new OnClickListener(){

							@Override
							public void onClick(View v) {
								m_rlClues.getLayoutParams().height = ori_height;
								m_rlClues.invalidate();
								
								m_rlClues.setVisibility(View.INVISIBLE);
								m_adpDivxListAdapter.notifyDataSetChanged();
								showRegisterLayout();
								
								m_lvDivxList.requestFocusFromTouch();
								m_lvDivxList.requestFocus();
								m_lvDivxList.setSelection(1);
								m_lvDivxList.setSelector(R.drawable.selector_list_background);
								
								guide_arrows.setVisibility(View.INVISIBLE);
				     			guide_select.setVisibility(View.INVISIBLE);
				     			guide_ok.setText(DivxSetup.this.getResources().getString(R.string.guide_ok));
				     			guide_enter.setText(DivxSetup.this.getResources().getString(R.string.guide_enter));
							}
					   });
				}
				else
				{
					m_tvClue1.setVisibility(View.VISIBLE);
					m_tvClue2.setVisibility(View.VISIBLE);
					m_tvClue3.setVisibility(View.VISIBLE);
		
					if(language.compareTo("zho") == 0)
					{
						m_tvClue1.setText(divx_deregister_clues[0]+"\n"+mTv.deregisterDivX());
						m_tvClue2.setText(divx_deregister_clues[3]+divx_register_clues[4]+divx_deregister_clues[1]);
						m_tvClue3.setText(divx_deregister_clues[2]);
					}else
					{
						m_tvClue1.setText(divx_deregister_clues[0]+"\n"+mTv.deregisterDivX());
						m_tvClue2.setText(divx_deregister_clues[1]+"\n"+divx_register_clues[4]);
						m_tvClue3.setText(divx_deregister_clues[2]);
					}
			        
			        guide_arrows.setVisibility(View.VISIBLE);
				    guide_select.setVisibility(View.VISIBLE);
				    guide_ok.setText(DivxSetup.this.getResources().getString(R.string.guide_ok));
				    guide_enter.setText(DivxSetup.this.getResources().getString(R.string.guide_enter));
				    
				    bt_yes_no.setVisibility(View.VISIBLE);
					bt_single.setVisibility(View.INVISIBLE);
					bt_yes.requestFocus();
					bt_yes.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							guide_arrows.setVisibility(View.INVISIBLE);
						    guide_select.setVisibility(View.INVISIBLE);
						    guide_ok.setText(DivxSetup.this.getResources().getString(R.string.guide_back));
						    guide_enter.setText(DivxSetup.this.getResources().getString(R.string.guide_return));
							
						    Locale locale = getResources().getConfiguration().locale;
							String language = locale.getISO3Language();
							if(language.compareTo("zho") == 0)
							{
								m_tvClue1.setVisibility(View.VISIBLE);
								m_tvClue1.setText(divx_register_clues[0]+"\n"+divx_register_clues[3]);
								m_tvClue2.setVisibility(View.VISIBLE);
								m_tvClue2.setText(divx_register_clues[1] + mTv.registerDivX());
								m_tvClue3.setVisibility(View.VISIBLE);
								m_tvClue3.setText(divx_deregister_clues[3]+divx_register_clues[4]+divx_register_clues[2]);
							}else
							{
								m_tvClue1.setVisibility(View.VISIBLE);
								m_tvClue1.setText(divx_register_clues[0]+"\n"+divx_register_clues[3]);
								m_tvClue2.setVisibility(View.VISIBLE);
								m_tvClue2.setText(divx_register_clues[1] + mTv.registerDivX());
								m_tvClue3.setVisibility(View.VISIBLE);
								m_tvClue3.setText(divx_register_clues[2]+"\n"+divx_register_clues[4]);
							}
						  
						    ResetDivxState();
						    
						    bt_yes_no.setVisibility(View.INVISIBLE);
							bt_single.setVisibility(View.VISIBLE);
							m_btnChoice.requestFocus();
						    m_btnChoice.setOnClickListener(new OnClickListener(){
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									m_rlClues.setVisibility(View.INVISIBLE);
									m_adpDivxListAdapter.notifyDataSetChanged();
									showRegisterLayout();
									
									m_lvDivxList.requestFocusFromTouch();
									m_lvDivxList.requestFocus();
									m_lvDivxList.setSelection(1);
									m_lvDivxList.setSelector(R.drawable.selector_list_background);
									
									guide_arrows.setVisibility(View.INVISIBLE);
								    guide_select.setVisibility(View.INVISIBLE);
								    guide_ok.setText(DivxSetup.this.getResources().getString(R.string.guide_ok));
								    guide_enter.setText(DivxSetup.this.getResources().getString(R.string.guide_enter));
								}
					        });
						}
					});
					bt_no.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							m_rlClues.setVisibility(View.INVISIBLE);
							m_adpDivxListAdapter.notifyDataSetChanged();
							showRegisterLayout();
							
							m_lvDivxList.requestFocusFromTouch();
							m_lvDivxList.requestFocus();
							m_lvDivxList.setSelection(1);
							m_lvDivxList.setSelector(R.drawable.selector_list_background);
							
							guide_arrows.setVisibility(View.INVISIBLE);
						    guide_select.setVisibility(View.INVISIBLE);
						    guide_ok.setText(DivxSetup.this.getResources().getString(R.string.guide_ok));
						    guide_enter.setText(DivxSetup.this.getResources().getString(R.string.guide_enter));
						}
						
					});
				}  
			}
		 }
	}
};
	 private class ListViewOnKeyListener implements OnKeyListener
	 {
	     public boolean onKey(View view, int keyCode, KeyEvent event)
	     {
	       	Log.d(TAG,"ListViewOnKeyListener");
	        	
	       	if(event.getAction() == KeyEvent.ACTION_DOWN)
	       	{
	       		m_lvDivxList.requestFocus();
	       		
		       	switch(keyCode)
		       	{
		       		case KeyEvent.KEYCODE_DPAD_DOWN:
		       		case KeyEvent.KEYCODE_DPAD_UP:
		       		{	if(m_adpDivxListAdapter.getCount() == 1)
		       			{
			       			m_lvDivxList.requestFocusFromTouch();
							m_lvDivxList.requestFocus();
							m_lvDivxList.setSelection(0);
							m_lvDivxList.setSelector(R.drawable.selector_list_background);
		       			}else if(m_adpDivxListAdapter.getCount() == 2)
		       			{
		       				if(!mRegisterFile.isFile())
			       			{
			       				m_lvDivxList.requestFocusFromTouch();
								m_lvDivxList.requestFocus();
								m_lvDivxList.setSelection(0);
								m_lvDivxList.setSelector(R.drawable.selector_list_background);
			       			}else 
			       			{
			       				if(mTv.isDeviceActivated())
			       				{
			       					m_lvDivxList.requestFocusFromTouch();
									m_lvDivxList.requestFocus();
									m_lvDivxList.setSelection(1);
									m_lvDivxList.setSelector(R.drawable.selector_list_background);
			       				}else
			       				{
			       					if(m_lvDivxList.getSelectedItemPosition() == 0)
			       					{
			       						m_lvDivxList.requestFocusFromTouch();
										m_lvDivxList.requestFocus();
										m_lvDivxList.setSelection(1);
										m_lvDivxList.setSelector(R.drawable.selector_list_background);
			       					}
			       					else
			       					{
			       						m_lvDivxList.requestFocusFromTouch();
										m_lvDivxList.requestFocus();
										m_lvDivxList.setSelection(0);
										m_lvDivxList.setSelector(R.drawable.selector_list_background);
			       					}
			       				}
			       			}
		       			}
		       			return true;
		       		}
		       	}
	       	}
			return false;       
	    }
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}
	 
	 @Override
	 public void onPause(){
		 super.onPause();
	 }
	 
	 @Override
	 public void onStop(){
		 super.onStop();
	 }
	 
	 @Override
	 public void onRestart(){
		 super.onRestart();
	 }
	 
	 @Override
	 public void onDestroy(){
		 mediaApp.releaseMediaPlayer();
		 super.onDestroy();
	 }
	 
	 public void ResetDivxState()
	 {
		 try {
				mPlayer.setPlayerType(6); //use RTK_MediaPlayer
				mPlayer.setDataSource("/tmp/1.mp3");
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        mPlayer.execResetDivxState();
	        
	        for(int i = 0; i< mVideoBookMark.bookMarkLength(); i++)
	        {
	        	int status = mVideoBookMark.bookMarkList.get(i).drmStatus;
	        	if(status != DivxParser.NAV_DIVX_DRM_NONE)
	        	{
	        		mVideoBookMark.removeBookMark(i);
	        		mVideoBookMark.writeBookMark();
	        	}
	        }
	 }
	 
	 private OnItemClickListener Setup_itemClickListener = new OnItemClickListener() 
	 {	 
		 public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
		 { 
			 //m_lvDivxList.setY(40);
			 m_tvTitleRegisterChoice.setText((String) m_ResourceMgr.getText(R.string.divx_title));
			 
			 m_adpDivxListAdapter.notifyDataSetChanged();
		     m_lvDivxList.setOnItemSelectedListener(itemSelectedListener);
		     m_lvDivxList.setOnKeyListener(new ListViewOnKeyListener());
		     m_lvDivxList.setOnItemClickListener(itemClickListener);
		     
		     guide_arrows.setVisibility(View.INVISIBLE);
		     guide_select.setVisibility(View.INVISIBLE);
		     guide_ok.setText(DivxSetup.this.getResources().getString(R.string.guide_ok));
		     guide_enter.setText(DivxSetup.this.getResources().getString(R.string.guide_enter));
		     
		     isSetupStatus = false;
		     isDivxVODStatus = true;
		 }
	 };
	 
	 public boolean onKeyDown(int keyCode, KeyEvent event) { 
		 switch (keyCode) {
		 	case KeyEvent.KEYCODE_ESCAPE:
	     	case KeyEvent.KEYCODE_BACK:
	     	{
	     		if(isSetupStatus)
	     		{
	     			this.finish();
	     		}else
	     		{
	     			if(isDivxVODStatus)
	     			{
	     				//m_lvDivxList.setY(60);
	     				m_tvTitleRegisterChoice.setText((String) m_ResourceMgr.getText(R.string.divx_setup));
	     				
	     				m_adpDivxListAdapter.notifyDataSetChanged();
	     		        m_lvDivxList.setOnItemClickListener(Setup_itemClickListener);
	     		        m_lvDivxList.setOnKeyListener(null);
	     		        m_lvDivxList.setOnItemSelectedListener(null);
	     		        
	     		        isSetupStatus = true;
	     		    	isDivxVODStatus = false;
	     			}else
	     			{
	     				m_rlClues.setVisibility(View.INVISIBLE);
						m_adpDivxListAdapter.notifyDataSetChanged();
						showRegisterLayout();
						m_lvDivxList.requestFocus();
	     				
	     				if(isRegister)
	     				{
							m_lvDivxList.setSelection(0);
	     				}else
	     				{
							m_lvDivxList.setSelection(1);
	     				}
	     				
	     				isSetupStatus = false;
	     		    	isDivxVODStatus = true;
	     			}
	     			
	     			guide_arrows.setVisibility(View.INVISIBLE);
	     			guide_select.setVisibility(View.INVISIBLE);
	     			guide_ok.setText(DivxSetup.this.getResources().getString(R.string.guide_ok));
	     			guide_enter.setText(DivxSetup.this.getResources().getString(R.string.guide_enter));
	     		}
	     		return true;
	     	}
	     	default:
	     		break;
		 }
		 return super.onKeyDown(keyCode, event);
	 }
	 
}

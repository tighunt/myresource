package com.rtk.mediabrowser;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.realtek.DataProvider.AbstractDataProvider;
import com.realtek.DataProvider.FileFilterType;
import com.realtek.Utils.MarqueeTextView;
import com.realtek.bitmapfun.util.CommonFrargmentWithImageWorker;
import com.realtek.bitmapfun.util.ImageCache;
import com.realtek.bitmapfun.util.ImageFetcher;
import com.realtek.bitmapfun.util.ImageWorker;
import com.realtek.bitmapfun.util.LoadingControl;
import com.realtek.bitmapfun.util.PicSize;
import com.realtek.bitmapfun.util.ReturnSizes;
import android.app.Activity;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView.OnItemClickListener;
import android.view.View.OnKeyListener;

public class GridViewFragment  extends CommonFrargmentWithImageWorker 
{
    private ReturnSizes mReturnSizes;
    private GridView    mGridView=null;
    private String      TAG ="GridViewFragement" ; 
    public GridViewAdapter mGridViewAdapter=null;
    public DirectionControl directionControl =null;
    private MediaApplication map = null;
	private int lastPathLevel = -1;
	private Path_Info pathInfo = new Path_Info();;
    
    public static final String IMAGE_CACHE_DIR = "images";       
    
    public interface UiListener
    {        
        public AbstractDataProvider getCurrentDataProvider();
        public void onItemSelected(int position);
        public void onItemClicked (int position);
        public int  getFocusIndex();
        public boolean onBackClicked();
        public boolean onKeyClicked(View view,int keyCode,KeyEvent event,int position,int iconNum,int firstVisibleItem,int lastVisibleItem);
        
        public void startLoadingIcon(); //Added for GridViewFragment.GridViewLoadingControl.startLoading()
        public void stopLoadingIcon();  //Added for GridViewFragment.GridViewLoadingControl.stopLoading()
        public int getPathLevel();
    }   
    
    private UiListener mUiCallback;
    public class GridViewLoadingControl implements LoadingControl
    {
    	@Override
    	public void startLoading() {
    		// TODO Auto-generated method stub
    		/* //Use "mUiCallback.startLoadingIcon();" to replace these codes.
    		Message msg = new Message();
    		msg.what=0;
    		GridViewActivity.loading_icon_system_handle.sendMessage(msg);
    		*/
    		mUiCallback.startLoadingIcon();
    	}

    	@Override
    	public void stopLoading() {
    		// TODO Auto-generated method stub
    		/* //Use "mUiCallback.stopLoadingIcon();" to replace these codes.
    		Message msg = new Message();
    		msg.what=1;
    		GridViewActivity.loading_icon_system_handle.sendMessage(msg);
    		*/
    		mUiCallback.stopLoadingIcon();
    	}    	
    }
    public  GridViewLoadingControl gv_loadingcontrol=new GridViewLoadingControl();    
       
    @Override
    public void onResume()
    {
        super.onResume();        
        Log.d(TAG,"onResume:");       
    }
    
    
    @Override
    public void onDestroyView()
    {    	 
    	super.onDestroyView();
    }   
    @Override
    public void onDestroy()
    {	
    	Log.d(TAG,"onDestroy");
    	super.onDestroy();
		if (pathInfo != null)
			pathInfo.cleanLevelInfo();
    }
        
    @Override
    public void onPause()
    {   	
    	 Log.d(TAG,"onPause");
    	 int childCount = mGridView.getChildCount();
         for(int i = 0; i < childCount; i++) 
         {
        	 //Log.d(TAG,"onPause cancel decode:"+i);
             View v = mGridView.getChildAt(i);
             cancelDecode(v);
         }
         
         super.onPause();
    }    
    @Override
    public void onAttach(Activity activity) {       
        super.onAttach(activity);      
        try{
            mUiCallback = (UiListener) activity; // check if the interface is implemented
        }catch(ClassCastException e){
            e.printStackTrace();
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) 
    {
        Log.d(TAG,"onViewCreated inflater");        

        return inflater.inflate(R.layout.fragment_gridview, container, false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) 
    {
        super.onViewCreated(view, savedInstanceState);                
        Log.d(TAG,"onViewCreated view");       
        mGridView=(GridView)(view.findViewById(R.id.gridview));
        setGridView();     
    }
    public GridView getGridView()
    {
    	return mGridView;
    }
    
    public void RefreshGridView(int initPos)
    {
        Log.d(TAG,"refreshGridView"); 
        int childCount = mGridView.getChildCount();
        for(int i = 0; i < childCount; i++) 
        {
            View v = mGridView.getChildAt(i);
            if(v !=null)
            {
            	cancelDecode(v);
            }            
        }

		int mLine = 0;
		int mPage = 0;
        int tmpPathLevel = mUiCallback.getPathLevel();
		if(tmpPathLevel > lastPathLevel){
			pathInfo.addPhotoLevelInfo(mGridViewAdapter.moveLine, mGridViewAdapter.movePage);
			mGridViewAdapter.moveLine = 0;
			mGridViewAdapter.movePage = 0;
		}else if(tmpPathLevel < lastPathLevel){
			mLine = pathInfo.getLastFirstVisibleItem();
			mPage = pathInfo.getLastLastVisibleItem();
			pathInfo.backToPhotoLastLevel();
			mGridViewAdapter.moveLine = mLine;
			mGridViewAdapter.movePage = mPage;
			mGridViewAdapter.count = mUiCallback.getCurrentDataProvider().GetSize() - mLine * 4;
		}else {
			mLine = mGridViewAdapter.moveLine;
			mPage = mGridViewAdapter.movePage;
		}

		lastPathLevel = tmpPathLevel;
        
        int focurPos = initPos - mPage * 12 - mLine * 4;
        mGridView.setSelected(true); 
        mGridView.setSelection(focurPos);
   
        mGridViewAdapter.notifyDataSetChanged();
        mGridView.invalidateViews();
    }    
    
    @SuppressWarnings("unused")
	private class GridViewOnKeyListener implements OnKeyListener
    {
    	
        public boolean onKey(View view, int keyCode, KeyEvent event)
        {
        	if(event.getAction()==KeyEvent.ACTION_DOWN)
        	{
	        	int position = mGridView.getSelectedItemPosition();
	        	int iconNum=mGridView.getCount();
	        	int firstVisibleItem=mGridView.getFirstVisiblePosition();
	        	int lastVisibleItem=mGridView.getLastVisiblePosition();
	        	return mUiCallback.onKeyClicked(view,keyCode,event,position,iconNum,firstVisibleItem,lastVisibleItem);
        	}
			return false;       
        }
    }
    
    public GridViewAdapter getGridViewAdapter()
    {
    	return mGridViewAdapter;
    }
    public class DirectionControl
    {
    	private GridViewAdapter adapter = null;
    	private int positionCurrent_allitems = 0;
    	private int positionCurrent_pageitem = 0;
    	DirectionControl()
    	{
    		adapter = getGridViewAdapter();
    		positionCurrent_pageitem = mGridView.getSelectedItemPosition();
    		positionCurrent_allitems = positionCurrent_pageitem + adapter.moveLine*4 + adapter.movePage*12;
    	}
    	public void RefreshPosition()
    	{
    		adapter.RefreshData();
    		positionCurrent_pageitem = mGridView.getSelectedItemPosition();
    		positionCurrent_allitems = positionCurrent_pageitem + adapter.moveLine*4 + adapter.movePage*12;
    		mGridView.requestFocus();
    		mGridView.invalidate();
    	}
    	public void setPosition(int PageitemPos)
        {   		
    		positionCurrent_pageitem = PageitemPos;
    		positionCurrent_allitems = positionCurrent_pageitem + adapter.moveLine*4 + adapter.movePage*12;
        }
    	public int getPositionCurrent_pageitem()
    	{
    		return positionCurrent_pageitem;
    	}
    	public int getPositionCurrent_allitems()
    	{
    		return positionCurrent_allitems;
    	}
    	public GridViewAdapter GetAdapter()
    	{
    		return adapter;
    	}
    	public void DoSelectPosition(int position)
    	{
    		mGridView.setSelection(position);
    	}
    	public void OnItemSelected_dir_ctrl()
    	{
    		int reLocatedPosition=ReLocatePosition(mGridView.getSelectedItemPosition());
    	    mUiCallback.onItemSelected(reLocatedPosition);
    	}
    	public void BackToMultiView(int position, int lastPos_All, int lastPos_Page)
    	{
    		int curPosition=0;
    		if(position == -2)//plug out usb when photo full screen;
    		{
    			curPosition=0;
    		}
    		else
    		{
    			curPosition=position+adapter.dataProvider.getDirnum();
    		}
    		if(lastPos_Page<4)//when lastPos_Page is at line one
    		{
    			if((curPosition-lastPos_All+lastPos_Page<4))
    			{
    				//focus will be at line one,gridview will change backward
    				adapter.RefreshData();    		
    	    		adapter.movePage=0;
    	    		adapter.moveLine = curPosition/4;   
    				adapter.count=(adapter.dataProvider.GetSize()-adapter.moveLine*4>=12)?
    	    				12
    	    				:
    	    				adapter.dataProvider.GetSize()-adapter.moveLine*4;
    	    		adapter.notifyDataSetChanged();
    	    		mGridView.setSelection(curPosition%4);
    	    		OnItemSelected_dir_ctrl();
    	    		    	    	
    			}
    			else if(curPosition-lastPos_All+lastPos_Page<12)
    			{
    				//focus will be at line two and line three,gridview will not change
    				adapter.RefreshData();    		
    	    		adapter.movePage=0;
    	    		adapter.moveLine=lastPos_All/4;
    	    		adapter.count=(adapter.dataProvider.GetSize()-adapter.moveLine*4>=12)?
    	    				12
    	    				:
    	    				adapter.dataProvider.GetSize()-adapter.moveLine*4;
    	    		adapter.notifyDataSetChanged();
    	    		mGridView.setSelection(lastPos_Page+curPosition-lastPos_All);
    	    		OnItemSelected_dir_ctrl();
    	    		   	    	
    			}
    			else if(curPosition-lastPos_All+lastPos_Page >= 12)
    			{
    				//focus will be at line  three,gridview will change forward
    				adapter.RefreshData();    		
    	    		adapter.movePage=0;
    	    		adapter.moveLine=(lastPos_All)/4+(curPosition-lastPos_All+lastPos_Page-12)/4+1;
    	    		adapter.count=(adapter.dataProvider.GetSize()-adapter.moveLine*4>=12)?
    	    				12
    	    				:
    	    				adapter.dataProvider.GetSize()-adapter.moveLine*4;
    	    		adapter.notifyDataSetChanged();
    	    		mGridView.setSelection(8+curPosition%4);
    	    		OnItemSelected_dir_ctrl();
    	    		   	    	
    			}
    		}
    		else if(lastPos_Page<8)//when lastPos_Page is at line two
    		{
    			if(curPosition-(lastPos_All-lastPos_Page)<0)
    			{
    				//focus will be at line one,gridview will change backward
    				adapter.RefreshData();    		
    	    		adapter.movePage=0;
    	    		adapter.moveLine=(lastPos_All-4)/4-((lastPos_All-lastPos_Page-curPosition-1)/4+1);
    	    		adapter.count=(adapter.dataProvider.GetSize()-adapter.moveLine*4>=12)?
    	    				12
    	    				:
    	    				adapter.dataProvider.GetSize()-adapter.moveLine*4;
    	    		adapter.notifyDataSetChanged();
    	    		mGridView.setSelection(curPosition%4);
    	    		OnItemSelected_dir_ctrl();   	    		   	    
    				
    			}
    			else if((curPosition-(lastPos_All-lastPos_Page))<12
    				&&(curPosition-(lastPos_All-lastPos_Page)>=0))
    			{
    				//focus will be at line three or line two or line one,gridview will not change
    				adapter.RefreshData();    		
    	    		adapter.movePage=0;
    	    		adapter.moveLine=(lastPos_All-4)/4;
    	    		adapter.count=(adapter.dataProvider.GetSize()-adapter.moveLine*4>=12)?
    	    				12
    	    				:
    	    				adapter.dataProvider.GetSize()-adapter.moveLine*4;
    	    		adapter.notifyDataSetChanged();
    	    		mGridView.setSelection(curPosition-(lastPos_All-lastPos_Page));
    	    		OnItemSelected_dir_ctrl();
    	    		
    	    	}
    			else if(curPosition-lastPos_All+lastPos_Page>12)
    			{
    				//focus will be at line three,gridview will change forward
    				adapter.RefreshData();    		
    	    		adapter.movePage=0;
    	    		adapter.moveLine=(lastPos_All-4)/4+(curPosition-lastPos_All+lastPos_Page-12)/4+1;
    	    		adapter.count=(adapter.dataProvider.GetSize()-adapter.moveLine*4>=12)?
    	    				12
    	    				:
    	    				adapter.dataProvider.GetSize()-adapter.moveLine*4;
    	    		adapter.notifyDataSetChanged();
    	    		mGridView.setSelection(8+curPosition%4);
    	    		OnItemSelected_dir_ctrl();
    	    		    	    	
    			}
    		}
    		else if(lastPos_Page<12)
    		{
    			if((curPosition-lastPos_All+lastPos_Page>12))
    			{
    				//focus will be at line three,gridview will change forward
    				adapter.RefreshData();    		
    	    		adapter.movePage=0;
    	    		adapter.moveLine=(lastPos_All-8)/4+(curPosition-lastPos_All+lastPos_Page-12)/4+1;
    	    		adapter.count=(adapter.dataProvider.GetSize()-adapter.moveLine*4>=12)?
    	    				12
    	    				:
    	    				adapter.dataProvider.GetSize()-adapter.moveLine*4;
    	    		adapter.notifyDataSetChanged();
    	    		mGridView.setSelection(8+curPosition%4);
    	    		OnItemSelected_dir_ctrl();
    	    		   
    			}
    			else if((curPosition>=lastPos_All&&curPosition-lastPos_All+lastPos_All%4<4)
    					||curPosition<lastPos_All&&curPosition>=(lastPos_All-lastPos_Page))
    			{
    				//focus will be at line three or line two or line three,gridview will not change
    				adapter.RefreshData();    		
    	    		adapter.movePage=0;
    	    		adapter.moveLine=(lastPos_All-8)/4;
    	    		adapter.count=(adapter.dataProvider.GetSize()-adapter.moveLine*4>=12)?
    	    				12
    	    				:
    	    				adapter.dataProvider.GetSize()-adapter.moveLine*4;
    	    		adapter.notifyDataSetChanged();
    	    		mGridView.setSelection(curPosition-(lastPos_All-lastPos_Page));
    	    		OnItemSelected_dir_ctrl();
    	    		
    			}
    			else if(curPosition<lastPos_All-lastPos_Page)
    			{
    				//focus will be at line one,gridview will change backward
    				adapter.RefreshData();    		
    	    		adapter.movePage=0;
    	    		adapter.moveLine=(lastPos_All-8)/4-((lastPos_All-lastPos_Page-curPosition+1-1+1-1-1)/4+1);
    	    		adapter.count=(adapter.dataProvider.GetSize()-adapter.moveLine*4>=12)?
    	    				12
    	    				:
    	    				adapter.dataProvider.GetSize()-adapter.moveLine*4;
    	    		adapter.notifyDataSetChanged();
    	    		mGridView.setSelection(curPosition%4);
    	    		OnItemSelected_dir_ctrl();
    			}   			
    		}
    		RefreshPosition();
    	}
    	public void RightOperation()
    	{
    		if(positionCurrent_allitems == adapter.countTotal-1)
    		{
    			;//selected item is the last item of all items,right key is invalid
    		}
    		else if(positionCurrent_pageitem < 11 )
    		{
    			//selected item is not the last item in a page, move 1 step right
    			mGridView.setSelection(positionCurrent_pageitem +1);
    		}
    		else if(positionCurrent_pageitem == 11)
    		{
    			adapter.moveLine=adapter.moveLine+1;
    			if(adapter.countTotal-1-positionCurrent_allitems >= 4)
    			{
    				adapter.count=12;
    			}
    			else if(adapter.countTotal-1-positionCurrent_allitems < 4)
    			{
    				adapter.count=8+adapter.countTotal%4;
    			}
    			adapter.notifyDataSetChanged();
    			mGridView.setSelection(8);
    		}
    		OnItemSelected_dir_ctrl();
    		RefreshPosition();
    	}
    	public void LeftOperation()
    	{
    		
    		if(positionCurrent_pageitem > 0 )
    		{
    			//selected item is not the first item in a page, move 1 step left
    			mGridView.setSelection(positionCurrent_pageitem -1);
    		}
    		else if(positionCurrent_allitems == 0)
    		{
    			;//selected item is the first item of all items,left key is invalid
    		}
    		else if(positionCurrent_allitems > 0)
    		{
    			adapter.moveLine=adapter.moveLine - 1;
    			adapter.count=adapter.count+4>12?12:adapter.count+4;
    			adapter.notifyDataSetChanged();
    			mGridView.setSelection(3);
    		}
    		
    		OnItemSelected_dir_ctrl();
    		
    		RefreshPosition();
    	}
    	public void DownOperation()
    	{
    		if(positionCurrent_pageitem < 8)
    		{
    			//selected item is in line 1 or line 2 of a page
    			if(positionCurrent_allitems+4 > adapter.countTotal-1)
    				//when the next line is out of countTotal index,go to the top line
    			{
    				//mGridView.setSelection(mGridView.getLastVisiblePosition());
    				if(positionCurrent_allitems/4 == (adapter.countTotal-1)/4-1)
    				{
    					mGridView.setSelection(positionCurrent_pageitem+adapter.countTotal-1-positionCurrent_allitems);
    				}
    				else
    				{
    					adapter.moveLine=0;
    					adapter.movePage=0;
    					adapter.count=adapter.dataProvider.GetSize()>12?12:adapter.dataProvider.GetSize();
    					adapter.notifyDataSetChanged();
    					mGridView.setSelection(positionCurrent_pageitem%4);
    				}	
    			}    				
    			else
    				mGridView.setSelection(positionCurrent_pageitem+4);
    		}
    		else if(positionCurrent_pageitem >= 8)
    		{
    			//selected item is in line 3 of a page
    			if((adapter.countTotal-1)/4==positionCurrent_allitems/4)
				{
    				//selected item is in last line of all items
					adapter.moveLine=0;
					adapter.movePage=0;
					adapter.count=adapter.dataProvider.GetSize()>12?12:adapter.dataProvider.GetSize();
					adapter.notifyDataSetChanged();
					mGridView.setSelection(positionCurrent_pageitem%4);
				}
    			else if(positionCurrent_allitems+4 > adapter.countTotal-1)
    			{
    				adapter.moveLine=adapter.moveLine+1;
    				adapter.count=8+adapter.countTotal%4;
    				adapter.notifyDataSetChanged();
    				mGridView.setSelection(8+adapter.countTotal%4-1);   				
    			}
    			else
    			{
    				adapter.moveLine=adapter.moveLine+1;
    				if((adapter.countTotal-1-4)/4 == positionCurrent_allitems/4)
    					adapter.count=9+(adapter.countTotal-1)%4;
    				else
    					adapter.count=12;
	    			adapter.notifyDataSetChanged();
	    			mGridView.setSelection(positionCurrent_pageitem);
    			}
    		}   		
    		OnItemSelected_dir_ctrl();  
    		
    		RefreshPosition();
    	}
    	public void UpOperation()
    	{

    		int itemnum_bottom=mGridView.getLastVisiblePosition()-7<=0?0:mGridView.getLastVisiblePosition()-7;
    		if(positionCurrent_pageitem > 3)
    		{
    			//selected item is in line 3 or line 2 of a page
    				mGridView.setSelection(positionCurrent_pageitem - 4);
    		}
    		else if(positionCurrent_pageitem <= 3)
    		{
    			if(positionCurrent_allitems/4==0)
    			{
    				//;//if focus on top line, nothing to do
    				//if focus on top line, go to the bottom line
    				if(adapter.countTotal <= 12)
    				{
    					if((adapter.countTotal-1)%4 <= positionCurrent_pageitem%4)
    					{
    						mGridView.setSelection(adapter.countTotal-1);
    					}
    					else
    					{
    						mGridView.setSelection((adapter.countTotal-1)/4*4+positionCurrent_pageitem);
    					}
    				}
    				else
    				{
    					adapter.movePage = 0;
    					adapter.moveLine = (adapter.countTotal)/4+((adapter.countTotal)%4==0?0:1)-3;
    					int last_line_num=adapter.countTotal%4==0?4:adapter.countTotal%4;
    					adapter.count=8 + last_line_num;
    					adapter.notifyDataSetChanged();
    					if(positionCurrent_pageitem < last_line_num)
    						mGridView.setSelection(8+positionCurrent_pageitem);
    					else
    						mGridView.setSelection(8+adapter.countTotal%4-1);    					
    				}
    				//end
    				
    			}
    			else 
    			{   //roll up 1 line
    				adapter.moveLine=adapter.moveLine-1;
    				adapter.count=4+mGridView.getLastVisiblePosition()-itemnum_bottom+1;
    				adapter.notifyDataSetChanged();
    				mGridView.setSelection(positionCurrent_pageitem);
    			}
    		}    		
    		OnItemSelected_dir_ctrl();    			
    		
    		RefreshPosition();
    	}
    	public void PageUpOperation()
    	{
    		int itemnum_bottom=mGridView.getLastVisiblePosition()-7<=0?0:mGridView.getLastVisiblePosition()-7;
    		int itemnum_middle_bottom=mGridView.getLastVisiblePosition()-3<=0?0:mGridView.getLastVisiblePosition()-3;
    		if(positionCurrent_allitems-positionCurrent_pageitem == 0)
    		{
    			//mGridView.setSelection(0);//when focus in first page, setSelection(0)
    			
    			//when focus in first page,go to last page
    			if(positionCurrent_pageitem/4==0)
    			{
    				if(adapter.countTotal <= 12)
    				{
    					;
    				}
    				else
    				{
    					adapter.movePage = 0;
    					adapter.moveLine = (adapter.countTotal)/4+((adapter.countTotal)%4==0?0:1)-3;
    					int last_line_num=adapter.countTotal%4==0?4:adapter.countTotal%4;
    					adapter.count=8 + last_line_num;
    					adapter.notifyDataSetChanged();
    					mGridView.setSelection(0);
    				}
    			}
    			else   				
    			{
    				mGridView.setSelection(0);
    			}
    			//end
    		}
    		else if(positionCurrent_allitems-positionCurrent_pageitem-12>=0)
    		{
    			adapter.movePage=adapter.movePage-1;
    			adapter.count=12;
    			adapter.notifyDataSetChanged();
    			mGridView.setSelection(0);		
    		}
    		else if(positionCurrent_allitems-positionCurrent_pageitem==8)
    		{
    			adapter.moveLine=adapter.moveLine-2;
    			adapter.count=8+mGridView.getLastVisiblePosition()-itemnum_middle_bottom+1;
    			adapter.notifyDataSetChanged();
    			mGridView.setSelection(0);	
    			
    		}
    		else if(positionCurrent_allitems-positionCurrent_pageitem==4)
    		{
    			adapter.moveLine=adapter.moveLine-1;
    			adapter.count=4+mGridView.getLastVisiblePosition()-itemnum_bottom+1;
    			adapter.notifyDataSetChanged();
    			mGridView.setSelection(0);	    			
    		}    		
    		OnItemSelected_dir_ctrl();
    		
    		RefreshPosition();
    	}
    	public void PageDownOperation()
    	{
    		
    		if(positionCurrent_allitems-positionCurrent_pageitem+12>adapter.countTotal-1)
    		{
    			//no more next line,go to the last item
    			//mGridView.setSelection(adapter.countTotal-1-(positionCurrent_allitems-positionCurrent_pageitem));
    			
    			//no more next line,go to the first page
    			if(positionCurrent_allitems/4==(adapter.countTotal-1)/4)//last line,go to the first page
    			{
    				adapter.moveLine=0;
					adapter.movePage=0;
					adapter.count=adapter.dataProvider.GetSize()>12?12:adapter.dataProvider.GetSize();
					adapter.notifyDataSetChanged();
					mGridView.setSelection(0);
    			}
    			else//not last line,go to the last item
    			{
    				mGridView.setSelection(adapter.countTotal-1-(positionCurrent_allitems-positionCurrent_pageitem));
    			}
    		}
    		else if(positionCurrent_allitems+(mGridView.getLastVisiblePosition()-positionCurrent_pageitem)+12
    				<=adapter.countTotal-1)
    		{
    			adapter.movePage=adapter.movePage+1;
    			adapter.count=12;
    			adapter.notifyDataSetChanged();
    			mGridView.setSelection(0);
    		}
    		else
    		{
    			adapter.movePage=adapter.movePage+1;
    			adapter.count=adapter.countTotal-1-
    					(positionCurrent_allitems+(mGridView.getLastVisiblePosition()-positionCurrent_pageitem));
    			adapter.notifyDataSetChanged();
    			mGridView.setSelection(0);
    		}   		
    		
    		OnItemSelected_dir_ctrl();   	
    		
    		RefreshPosition();
    	}    	    	
    }
        
    private class GridViewtemSelectedListener implements OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position , long id)
        {   
            Log.d(TAG,"GridViewAdapter focuseIndex:"+mGridView.getSelectedItemPosition());

        }
        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            Log.d("kellykelly","onNothingSelected:");
        }           
    }
       
    private class GridViewItemClickListener implements OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
        {   
 
        	int reLocatedPosition=ReLocatePosition(position);
            mUiCallback.onItemClicked(reLocatedPosition);
        }
    }    
      
    @Override
    protected void initImageWorker()
    {
        //Log.d("kelly","initImageWorker");
        mReturnSizes =  new ReturnSizes(156, 137);
        mImageWorker = new ImageFetcher(getActivity(), null, mReturnSizes.getWidth(),
                mReturnSizes.getHeight());
  
        mImageWorker.setImageCache(ImageCache.findOrCreateCache(getActivity(),
                IMAGE_CACHE_DIR));
        /*mImageWorker.setImageCache(ImageCache.findOrCreateCache((FragmentActivity) mContext,
                "images"));*/
        mImageWorker.setImageFadeIn(false);
    }
    public PicSize getSizeCache(String path)
    {
    	return mImageWorker.getImageCache().getSizeFromMemCache(path);
    }
    public void setSizeCache(String path,PicSize ps)
    {
    	mImageWorker.getImageCache().addPicSizeToCache(path,ps);
    	//mImageWorker.getImageCache().showsizecache();//for debug
    }
    public int ReLocatePosition(int position) {
		// TODO Auto-generated method stub
    	mGridViewAdapter.RefreshData();
    	Log.v(TAG,"move line is "+mGridViewAdapter.moveLine+", move page is "+mGridViewAdapter.movePage);
        position=position+mGridViewAdapter.moveLine*4+mGridViewAdapter.movePage*12;
        return position;
	}


	public void cancelDecode(View view)
    {
        if(view !=null)
        {    
            ImageView imageView = (ImageView)view.findViewById(R.id.grid_img);
            ImageWorker.cancelWork(imageView);
        }
    }    
    
    private void setGridView()
    {
        Log.d(TAG,"setGridView");
       
        mGridView.setOnItemSelectedListener(new GridViewtemSelectedListener());
        mGridView.setOnItemClickListener(new GridViewItemClickListener());
//      mGridView.setOnKeyListener(new GridViewOnKeyListener());
        mGridViewAdapter = new GridViewAdapter();
        mGridView.setNumColumns(4);
        mGridView.setFocusable(true);
        mGridView.requestFocus();
        mGridView.setSelection(0);
        mGridView.setSelected(true);
        mGridView.setClickable(true);

        mGridView.setAdapter(mGridViewAdapter);
        mGridView.setFocusable(true);
        mGridView.requestFocus();
        mGridView.setSelection(0);
        mGridView.setSelected(true);
        mGridView.setClickable(true);
       
        directionControl = new DirectionControl();

    }
   
    class GridViewAdapter extends BaseAdapter 
    {        
    	private AbstractDataProvider dataProvider = null;
    	public int moveLine=0;
    	public int movePage=0;
    	public int positionCurrent=0;
    	public int pageCurrent = 0;		
    	public int countTotal = 0;
    	public int pageTotal = 0;
    	public int count = 0;
    	GridViewAdapter()
    	{
    		dataProvider = mUiCallback.getCurrentDataProvider();
    		if(dataProvider != null)
    		{
    			countTotal = dataProvider.GetSize();
    			pageTotal = countTotal/12+1;
    			
    			pageCurrent=0;
    		}
    		
    		if((countTotal-(pageCurrent+1)*12) >= 0)        
            	count = 12;
            	//	mUiCallback.getCurrentDataProvider().GetSize();
            else
            	count = countTotal-pageCurrent*12;
    	}
    	public void RefreshData()
    	{
    		dataProvider = mUiCallback.getCurrentDataProvider();
    		if(dataProvider != null)
    		{
    			countTotal = dataProvider.GetSize();
    			pageTotal = countTotal/12+1;
    		}
    	}
        
        @Override
        public int getCount() {
            return count;
        }
        
        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) 
        {
        	ImageView imageView = null;
            // MarqueeTextView 
            TextView tv = null;
            View itemView = null;
            int pos = position;
            if (position == mUiCallback.getFocusIndex()) {
  			}
        	
            if (convertView == null) 
            {   
                Log.d(TAG,"getview:convertView==null :"+position); 
                // if it's not recycled, initialize some attributes
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                itemView = inflater.inflate(R.layout.item_of_gridview, null);
                imageView = (ImageView)(itemView.findViewById(R.id.grid_img));
                tv = (MarqueeTextView)(itemView.findViewById(R.id.grid_text));                
            } 
            else 
            {
                Log.d(TAG,"getview:convertView!=null :"+position); 
                itemView = convertView;
                imageView = (ImageView)(itemView.findViewById(R.id.grid_img));
                tv = (MarqueeTextView)(itemView.findViewById(R.id.grid_text));
            }
            
            Log.d(TAG,"getview imageview  :"+ imageView.toString());             
            
            if(imageView != null)
            {   
                 String path = null;
                 position = position + movePage*12+moveLine*4;
                 int type = mUiCallback.getCurrentDataProvider().GetFileTypeAt(position);
                 switch (type)
                 {
                     case FileFilterType.DEVICE_FILE_DIR:
                     {
                          imageView.setBackgroundResource(R.drawable.photo_list_item_folder);
                          path = "android.resource://" + R.drawable.photo_list_item_folder;
                          imageView.setImageResource(R.drawable.blank);
                     }
                     break;  
                     
                     case FileFilterType.DEVICE_FILE_DEVICE:
                     {
                         if(mUiCallback.getCurrentDataProvider().GetTitleAt(position).equals(MediaApplication.internalStorage)){
                    		 imageView.setBackgroundResource(R.drawable.photo_list_item_internal);
                    		 path = "android.resource://" + R.drawable.photo_list_item_internal;
                    		 imageView.setImageResource(R.drawable.blank);
                    	 }else{
	                         imageView.setBackgroundResource(R.drawable.photo_list_item_usb);
	                         path = "android.resource://" + R.drawable.photo_list_item_usb;
	                         imageView.setImageResource(R.drawable.blank);
                    	 }
                     }
                     break;
                     
                     default:
                     case  FileFilterType.DEVICE_FILE_PHOTO:
                     {
                         //Log.d(TAG,"photo :"+position); 
                         imageView.setBackgroundResource(R.drawable.photo_list_item_image);
                         path = mUiCallback.getCurrentDataProvider().GetDataAt(position);
                     }  
                     break;
                 }
                 
                 tv.setText(mUiCallback.getCurrentDataProvider().GetTitleAt(position));               
                 
                 if(type == FileFilterType.DEVICE_FILE_PHOTO){
                	 ExifInterface exif = null;
                     String exifpath=dataProvider.GetDataAt(position);
         	    	try {
         	    		exif = new ExifInterface(exifpath);
         	    	} catch (IOException e) {
         	    		// TODO Auto-generated catch block
         	    		e.printStackTrace();
         	    	}
         	    	byte[] bThumbnail=exif.getThumbnail();
         	        boolean isExif = bThumbnail == null? false:true;
         	        if(mImageWorker != null)      
                    {
         	        	mImageWorker.loadImage(exifpath, imageView, gv_loadingcontrol,isExif);
                    }

                 }
            }
            return itemView;        
        }   
    }
	public void loadPreviewImage(ImageView preview, TextView exifText,int position) {
		// TODO Auto-generated method stub
		String path = null;
        int type = mUiCallback.getCurrentDataProvider().GetFileTypeAt(position);
        switch (type)
        { 
            case FileFilterType.DEVICE_FILE_DIR:
            {
            	 preview.setBackgroundResource(R.drawable.image_mbw_prev_folder);
                 path = "android.resource://" + R.drawable.image_mbw_prev_folder;
                 preview.setImageResource(R.drawable.blank);
            }
            break;  
            
            case FileFilterType.DEVICE_FILE_DEVICE:
            {
            	if(mUiCallback.getCurrentDataProvider().GetTitleAt(position).equals(MediaApplication.internalStorage)){
            		preview.setBackgroundResource(R.drawable.video_listlist_internal);
	                path = "android.resource://" + R.drawable.video_listlist_internal;
	                preview.setImageResource(R.drawable.blank);
            	}else{
	            	preview.setBackgroundResource(R.drawable.list_common_usb);
	                path = "android.resource://" + R.drawable.list_common_usb;
	                preview.setImageResource(R.drawable.blank);
            	}
            }
            break;
            
            default:
            case  FileFilterType.DEVICE_FILE_PHOTO:
            {
                //Log.d(TAG,"photo :"+position); 
                preview.setBackgroundResource(R.drawable.photo_list_item_image);
                path = mUiCallback.getCurrentDataProvider().GetDataAt(position);
            }  
            break;
        }
        if(mImageWorker == null)      
        {
     		Log.e(TAG,"mImageWorker==null?");
     		initImageWorker();
        }
        if(type == FileFilterType.DEVICE_FILE_DIR || type == FileFilterType.DEVICE_FILE_DEVICE)
     	   return;
        
        mImageWorker.setExitTasksEarly(false);
        ExifInterface exif = null;
        String exifpath=mUiCallback.getCurrentDataProvider().GetDataAt(position);
		if(exifpath != null){
	    	try {
	    		exif = new ExifInterface(exifpath);
	    	} catch (IOException e) {
	    		// TODO Auto-generated catch block
	    		e.printStackTrace();
	    	}
	        byte[] bThumbnail=exif.getThumbnail();
	        boolean isExif = bThumbnail == null? false:true;
	        
	        if(mImageWorker != null)      
	        {
	     		mImageWorker.loadImage(exifpath, preview, exifText,gv_loadingcontrol,isExif);
	        }
		}
        
     //   mImageWorker.loadImage(path, preview, exifText,gv_loadingcontrol,type);
	}	
}

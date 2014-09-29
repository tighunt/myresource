package com.logan.weibo.ui;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.logan.R;
import com.logan.util.FileUtils;
import com.logan.util.ImageUtils;
import com.logan.util.QImageUtil;
import com.logan.util.StringUtils;
import com.logan.util.UIHelper;

/**
 * 图片对话框
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ImageDialog extends Activity{
	private final String TAG = "ImageDialog";
	private ViewSwitcher mViewSwitcher;
	private Button btn_preview;
	private ImageView mImage;
	private Thread thread;
	private static Handler handler;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_dialog);
        this.initView();
        this.initData();
    }
    
    private View.OnTouchListener touchListener = new View.OnTouchListener(){
		public boolean onTouch(View v, MotionEvent event) {
			thread.interrupt();
			handler = null;
			finish();
			return true;
		}
	};
    
    private void initView()
    {
    	mViewSwitcher = (ViewSwitcher)findViewById(R.id.imagedialog_view_switcher); 
    	mViewSwitcher.setOnTouchListener(touchListener);
    	btn_preview = (Button)findViewById(R.id.imagedialog_preview_button);
    	btn_preview.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				String medium_imgUrl = getIntent().getStringExtra("medium_imgUrl");
				UIHelper.showImageZoomDialog(v.getContext(), medium_imgUrl);
				finish();
			}
		});
       
        mImage = (ImageView)findViewById(R.id.imagedialog_image);
        mImage.setOnTouchListener(touchListener);
    }    
    
    @SuppressLint("HandlerLeak")
	private void initData() 
    {
		final String imgURL = getIntent().getStringExtra("img_url");		
		final String ErrMsg = getString(R.string.msg_load_image_fail);
		handler = new Handler(){
			public void handleMessage(Message msg) {
				if(msg.what==1 && msg.obj != null){
					mImage.setImageBitmap((Bitmap)msg.obj);
					mViewSwitcher.showNext();
				}else{
					UIHelper.ToastMessage(ImageDialog.this, ErrMsg);
					finish();
				}
			}
		};
		thread = new Thread(){
			public void run() {
				Message msg = new Message();
				Bitmap bmp = null;
		    	String filename = FileUtils.getFileName(imgURL);
				try {
					//读取本地图片
					if(imgURL.endsWith("portrait.gif") || StringUtils.isEmpty(imgURL)){
						bmp = BitmapFactory.decodeResource(mImage.getResources(), R.drawable.widget_dface);
					}
					if(bmp == null){
						bmp = QImageUtil.getBitmapFromUrl(imgURL);
						if(bmp != null){
							try {
		                    	//写图片缓存
								ImageUtils.saveImage(mImage.getContext(), filename, bmp);
							} catch (IOException e) {
								e.printStackTrace();
							}
							//缩放图片
							bmp = ImageUtils.reDrawBitMap(ImageDialog.this, bmp);
						}
					}
					msg.what = 1;
					msg.obj = bmp;
				} catch (Exception e) {
					e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
				}
				if(handler != null && !isInterrupted())
					handler.sendMessage(msg);
			}
		};
		thread.start();
    }
}

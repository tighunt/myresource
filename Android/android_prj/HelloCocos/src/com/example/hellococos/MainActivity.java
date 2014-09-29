package com.example.hellococos;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.grid.CCLiquid;
import org.cocos2d.actions.grid.CCShaky3D;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCScaleBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.interval.CCSpawn;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccGridSize;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
	private CCGLSurfaceView mGLSurfaceView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // set the window status, no tile, full screen and don't sleep
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mGLSurfaceView = new CCGLSurfaceView(this);

        setContentView(mGLSurfaceView);
                
     // attach the OpenGL view to a window
        CCDirector.sharedDirector().attachInView(mGLSurfaceView);

        // no effect here because device orientation is controlled by manifest
        CCDirector.sharedDirector().setDeviceOrientation(CCDirector.kCCDeviceOrientationPortrait);
        
        // show FPS
        // set false to disable FPS display, but don't delete fps_images.png!!
        CCDirector.sharedDirector().setDisplayFPS(true);

        // frames per second
        CCDirector.sharedDirector().setAnimationInterval(1.0f / 60);

        CCScene scene = TemplateLayer.scene();

        // Make the Scene active
        CCDirector.sharedDirector().runWithScene(scene);
        
        //setContentView(R.layout.activity_main);s
        
    }
    
    @Override
    public void onStart() {
        super.onStart();        
    }

    @Override
    public void onPause() {
        super.onPause();

        CCDirector.sharedDirector().pause();
    }

    @Override
    public void onResume() {
        super.onResume();

        CCDirector.sharedDirector().resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        CCDirector.sharedDirector().end();
    }
    
    static class TemplateLayer extends CCLayer {
        CCLabel lbl;
        CCSprite icon[] = null;
        
    	public static CCScene scene() {
    		CCScene scene = CCScene.node();
    		CCLayer layer = new TemplateLayer();
    		
    		scene.addChild(layer);
    		
    		return scene;
    	}

    	/*public boolean mFirstFrame = true;
    	public void visit(GL10 gl) {
    	    super.visit(gl);
    	    if (!mFirstFrame)
    	    	CCDirector.mFboNeedUpdate[2] = false;
    	    if (mFirstFrame)
    	    	mFirstFrame = false;
        }*/
    	
        protected TemplateLayer() {

        	this.setIsTouchEnabled(true);

            lbl = CCLabel.makeLabel("Hello World!", "DroidSans", 24);

            addChild(lbl, 0);
            lbl.setPosition(CGPoint.ccp(160*2, 240*2));
            
            CCSprite bg = new CCSprite("earth.jpg");
            bg.setPosition(CGPoint.ccp(1280, 720));
            addChild(bg, 0);
            
            icon = new CCSprite[5];
            
            icon[0] = new CCSprite("earth.jpg");
            icon[0].setPosition(CGPoint.ccp(160*2, 84*2));
            icon[0].setScale(0.3f);
            addChild(icon[0], 1);
            
            icon[1] = new CCSprite("moon.jpg");
            icon[1].setPosition(CGPoint.ccp(320*2, 168*2));
            icon[1].setScale(0.2f);
            addChild(icon[1], 0);
            
            icon[2] = new CCSprite("moon.jpg");
            icon[2].setPosition(CGPoint.ccp(960*2, 168*2));
            icon[2].setScale(0.2f);
            addChild(icon[2], 0);
            
            icon[3] = new CCSprite("moon.jpg");
            icon[3].setPosition(CGPoint.ccp(320*2, 504*2));
            icon[3].setScale(0.2f);
            addChild(icon[3], 0);
            
            icon[4] = new CCSprite("moon.jpg");
            icon[4].setPosition(CGPoint.ccp(960*2, 504*2));
            icon[4].setScale(0.2f);
            addChild(icon[4], 0);            
            
            CCMoveBy move1 = CCMoveBy.action(4, CGPoint.ccp(960*2, 0));
            CCMoveBy move2 = CCMoveBy.action(3, CGPoint.ccp(0, 504*2));
            CCMoveBy move3 = CCMoveBy.action(5, CGPoint.ccp(960*2, 504*2));
            CCSequence mov = CCSequence.actions(
            		move1, move2,
            		move3.reverse(), move3,
            		move1.reverse(), move2.reverse());
            CCRepeatForever repMov = CCRepeatForever.action(mov);
            
            CCAnimation ani = CCAnimation.animation("animation", 1);
            ani.addFrame("earth.jpg");
            ani.addFrame("moon.jpg");
            CCAnimate animate = CCAnimate.action(ani);
            CCRepeatForever repAnim = CCRepeatForever.action(animate);   
            
            CCRotateBy rot = CCRotateBy.action(10, 360);
                   
            CCScaleBy scale = CCScaleBy.action(2, 2);
            CCSequence scales = CCSequence.actions(scale, scale.reverse());
            CCRepeatForever repScale = CCRepeatForever.action(scales);   
                        
            icon[0].runAction(repMov);
            icon[1].runAction(rot.copy());
            icon[2].runAction(rot.copy());
            icon[3].runAction(repScale);
            icon[4].runAction(repAnim);
       }

        @Override
        public boolean ccTouchesBegan(MotionEvent event) {
            CGPoint convertedLocation = CCDirector.sharedDirector()
            	.convertToGL(CGPoint.make(event.getX(), event.getY()));

            String title = String.format("touch at point(%.2f, %.2f)",
            			convertedLocation.x, convertedLocation.y);

            if (lbl != null) {
            	lbl.setString(title);
            }
            
            return CCTouchDispatcher.kEventHandled;
        }

    }
}

package org.cocos2d.grid;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11ExtensionPack;
import android.opengl.GLES11Ext;
import android.util.Log;

import org.cocos2d.config.ccMacros;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCTexture2D;
/** FBO class that grabs the the contents of the screen */
public class CCGrabberRtk {
	int		fbo[] = new int[4];
	int		oldFBO[] = new int[1];
	int		textures[] = new int[4];
    
	public CCGrabberRtk() {
		// generate FBO
		if (CCDirector.gl instanceof GL11ExtensionPack) {
			GL11ExtensionPack gle = (GL11ExtensionPack)CCDirector.gl;
			GL10 gl = CCDirector.gl;
			try {
				gle.glGenFramebuffersOES(4, fbo, 0);
				gl.glGenTextures(4, textures, 0);
			} catch (Exception e) {

			}
			gle.glGetIntegerv(GL11ExtensionPack.GL_FRAMEBUFFER_BINDING_OES, oldFBO, 0);
			for (int i=0; i<4; ++i){
				gl.glBindTexture(gl.GL_TEXTURE_2D, textures[i]);
				gl.glTexParameterf(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MIN_FILTER, gl.GL_LINEAR);
				gl.glTexParameterf(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MAG_FILTER, gl.GL_LINEAR);
				gl.glTexParameterf(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_WRAP_S, gl.GL_REPEAT);
				gl.glTexParameterf(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_WRAP_T, gl.GL_REPEAT);
				gl.glTexEnvf(gl.GL_TEXTURE_ENV, gl.GL_TEXTURE_ENV_MODE, gl.GL_REPLACE);
				gl.glTexImage2D(gl.GL_TEXTURE_2D, 0, GLES11Ext.GL_BGRA, CCDirector.fboWidth, CCDirector.fboHeight, 0, GLES11Ext.GL_BGRA, GL10.GL_UNSIGNED_BYTE, null);
				
				gle.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, fbo[i]);

				// associate texture with FBO
				gle.glFramebufferTexture2DOES(i + 1,
						GL11ExtensionPack.GL_COLOR_ATTACHMENT0_OES, 
						GL10.GL_TEXTURE_2D, textures[i], 0);
				
				// check if it worked (probably worth doing :) )
				int status = gle.glCheckFramebufferStatusOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES);
				
				if (status != GL11ExtensionPack.GL_FRAMEBUFFER_COMPLETE_OES) {
					return;
					// throw new Exception("Frame Grabber: Could not attach texture to framebuffer");
				}
			}
			gle.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, oldFBO[0]);
		}
	}

	public void beforeRender(int fboIndex) {
		if (!(CCDirector.gl instanceof GL11ExtensionPack)) {
			return;
		}
		GL11ExtensionPack gl = (GL11ExtensionPack) CCDirector.gl;
		gl.glGetIntegerv(GL11ExtensionPack.GL_FRAMEBUFFER_BINDING_OES, oldFBO, 0);
		try {
			gl.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, fbo[fboIndex]);

			// BUG XXX: doesn't work with RGB565.
			((GL10)gl).glClearColor(0,0,0,0);

			// BUG #631: To fix #631, uncomment the lines with #631
			// Warning: But it CCGrabber won't work with 2 effects at the same time
			//	glClearColor(0.0f,0.0f,0.0f,1.0f);	// #631

			((GL10)gl).glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);	

			//	glColorMask(TRUE, TRUE, TRUE, FALSE);	// #631
		} catch (Exception e) {

		}
	}

	public void afterRender(int fboIndex) {
		if (!(CCDirector.gl instanceof GL11ExtensionPack)) {
			return;
		}
		GL11ExtensionPack gl = (GL11ExtensionPack) CCDirector.gl;
		try {
			gl.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, oldFBO[0]);
		} catch (Exception e) {

		}
		//	glColorMask(TRUE, TRUE, TRUE, TRUE);	// #631
	}

	@Override
	public void finalize() throws Throwable  {
		if (!(CCDirector.gl instanceof GL11ExtensionPack)) {
        } else {
            GL11ExtensionPack gle = (GL11ExtensionPack) CCDirector.gl;
            ccMacros.CCLOGINFO("cocos2d: deallocing %@", this.toString());
            try {
                gle.glDeleteFramebuffersOES(4, fbo, 0);
                CCDirector.gl.glDeleteTextures(4, textures, 0);
            } catch (Exception e) {

            }
        }
        super.finalize();
	}
}


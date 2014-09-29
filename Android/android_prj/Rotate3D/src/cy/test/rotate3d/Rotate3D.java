package cy.test.rotate3d;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class Rotate3D extends Animation {
	public final static int BOUNDARY_LEFT = 0;
	public final static int BOUNDARY_RIGHT = 1;
	
	private int type;
	private float fromDegree;	// 旋转起始角度
	private float toDegree;		// 旋转终止角度
	private float mCenterX;		// 旋转中心x
	private float mCenterY;		// 旋转中心y
	private Camera mCamera;

	
	public void setType(int type){
		this.type = type;
		
	}
	public Rotate3D(float fromDegree, float toDegree, float depthZ, float centerX, float centerY) {
		this.fromDegree = fromDegree;
		this.toDegree = toDegree;
		this.mCenterX = centerX;
		this.mCenterY = centerY;
	}
	public Rotate3D(float fromDegree, float toDegree, float depthZ, float centerX, float centerY, int type) {
		this.fromDegree = fromDegree;
		this.toDegree = toDegree;
		this.mCenterX = centerX;
		this.mCenterY = centerY;
		this.type = type;
	}

	@Override
	public void initialize(int width, int height, int parentWidth, int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		mCamera = new Camera();
	}


	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		final float FromDegree = fromDegree;
		float degrees = FromDegree + (toDegree - fromDegree) * interpolatedTime;	// 旋转角度（angle）
		final float centerX = mCenterX;
		final float centerY = mCenterY;
		final Matrix matrix = t.getMatrix();

		
		if (degrees <= -79.5f) {
			degrees = -90.0f;
			mCamera.save();
			mCamera.rotateY(degrees);		// 旋转
			mCamera.getMatrix(matrix);
			mCamera.restore();
		} else if (degrees >= 79.5f) {
			degrees = 90.0f;
			mCamera.save();
			mCamera.rotateY(degrees);
			mCamera.getMatrix(matrix);
			mCamera.restore();
		} else {
			mCamera.save();
			mCamera.translate(degrees / 90 * centerX*2, 0, 0);
			mCamera.rotateY(degrees * RotateActivity.params);
//			mCamera.translate(0, 0, centerX);		// 位移x
//			mCamera.rotateY(degrees);
//			mCamera.translate(0, 0, -centerX);
			mCamera.getMatrix(matrix);
			mCamera.restore();
		}

		if(type == BOUNDARY_LEFT){
			matrix.preTranslate(0, -centerY);
			matrix.postTranslate(0, centerY);
		}else{
			matrix.preTranslate(-centerX*2, -centerY);
			matrix.postTranslate(centerX*2, centerY);
		}
		
	}
}




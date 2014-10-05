
package com.realtek.camera.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.ImageButton;

public class ImageThumbButton extends ImageButton {

    private Animation mAnimation;
    private final Transformation mTransform = new Transformation();

    public ImageThumbButton(Context context) {
        this(context, null, 0);
    }

    public ImageThumbButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageThumbButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected Animation onCreateAnimation() {
        Animation anim = new ScaleAnimation(0.5F, 1F, 0.5F, 1F, getWidth() / 2F, getHeight() / 2F);
        anim.setDuration(150);
        return anim;
    }

    /**
     * Sets a bitmap with animations.
     * 
     * @param uri
     */
    public void updateImageBitmap(Bitmap bitmap) {
        mAnimation = onCreateAnimation();
        mAnimation.start();
        setImageBitmap(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mAnimation != null) {
            if (mAnimation.getTransformation(AnimationUtils.currentAnimationTimeMillis(),
                    mTransform)) {
                invalidate();
            }
            canvas.save();
            canvas.concat(mTransform.getMatrix());
            super.onDraw(canvas);
            canvas.restore();
        } else {
            super.onDraw(canvas);
        }
    }

}

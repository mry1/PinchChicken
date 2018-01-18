package com.louis.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by louis on 18-1-18.
 */

@SuppressLint("AppCompatCustomView")
public class ZoomImageView extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener {
    private boolean mOnce = false;
    /**
     * 缩放起始值
     */
    private float mInitScale;
    /**
     * 手指放大缩小的值
     */
    private float mMidScale;
    /**
     * 缩放最大值
     */
    private float mMaxScale;
    private Matrix mScaleMatrix;

    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mScaleMatrix = new Matrix();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }


    /**
     * 获取ImageView加载完成的图片
     */
    @Override
    public void onGlobalLayout() {
        if (!mOnce) {
            int width = getWidth();
            int height = getHeight();
            Drawable d = getDrawable();
            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();
            float scale = 1.0f;
            if (dw > width && dh < height) {
                scale = width * 1.0f / dw;
            }
            if (dw < width && dh > height) {
                scale = height * 1.0f / dh;
            }
            if (dw > width && dh > height) {
                scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
            }
            if (dw < width && dh < height) {
                scale = Math.max(width * 1.0f / dw, height * 1.0f / dh);
            }

            /**
             * 得到初始化缩放比例
             */
            mInitScale = scale;
            mMidScale = 2 * scale;
            mMaxScale = 4 * scale;

            // 将图片移动至控件中心
            int dx = width / 2 - dw / 2;
            int dy = height / 2 - dh / 2;

            mScaleMatrix.postScale(mInitScale, mInitScale, width / 2, height / 2);
            mScaleMatrix.postTranslate(dx, dy);
            setImageMatrix(mScaleMatrix);

            mOnce = true;
        }
    }
}

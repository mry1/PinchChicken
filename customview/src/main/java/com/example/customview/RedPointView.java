package com.louis.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by louis on 18-1-12.
 */

public class RedPointView extends FrameLayout {

    private PointF mStartPoint;
    private PointF mCurPoint;
    private Paint mPaint;
    private Path mPath;
    private boolean mTouch = false;
    private float mRadius = 20;
    private TextView mTipTextView;
    private ImageView exploredImageView;
    private int RADIUS_LIMITS = 9;

    public RedPointView(@NonNull Context context) {
        this(context, null);
    }

    public RedPointView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RedPointView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mStartPoint = new PointF(500, 500);
        mCurPoint = new PointF();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
        mPath = new Path();

        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mTipTextView = new TextView(getContext());
        mTipTextView = new TextView(getContext());
        mTipTextView.setLayoutParams(layoutParams);
        mTipTextView.setPadding(10, 10, 10, 10);
        mTipTextView.setBackgroundResource(R.drawable.tv_bg);
        mTipTextView.setTextColor(Color.WHITE);
        mTipTextView.setText("99+");
        mTipTextView.setTextSize(8);

        exploredImageView = new ImageView(getContext());
        exploredImageView.setLayoutParams(layoutParams);
        exploredImageView.setVisibility(INVISIBLE);

        addView(mTipTextView);

    }

    //    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void dispatchDraw(Canvas canvas) {
//        int layer = canvas.saveLayer(0, 0, getWidth(), getHeight(), mPaint, Canvas.ALL_SAVE_FLAG);
        if (mTouch) {
            calPath();
            if (mRadius >= RADIUS_LIMITS) {
                canvas.drawCircle(mStartPoint.x, mStartPoint.y, mRadius, mPaint);
                canvas.drawPath(mPath, mPaint);
                canvas.drawCircle(mCurPoint.x, mCurPoint.y, mRadius, mPaint);
            }
            mTipTextView.setX(mCurPoint.x - mTipTextView.getWidth() / 2);
            mTipTextView.setY(mCurPoint.y - mTipTextView.getHeight() / 2);
        } else {
            mTipTextView.setX(mStartPoint.x - mTipTextView.getWidth() / 2);
            mTipTextView.setY(mStartPoint.y - mTipTextView.getHeight() / 2);
        }

//        canvas.restore();
        super.dispatchDraw(canvas);

    }

    private void calPath() {
        float x = mCurPoint.x;
        float y = mCurPoint.y;
        float startX = mStartPoint.x;
        float startY = mStartPoint.y;
        float dx = x - startX;
        float dy = y - startY;
        double a = Math.atan(dy / dx);
        float offsetX = (float) (mRadius * Math.sin(a));
        float offsetY = (float) (mRadius * Math.cos(a));
        // P1坐标
        float x1 = startX + offsetX;
        float y1 = startY - offsetY;
        // P2坐标
        float x2 = x + offsetX;
        float y2 = y - offsetY;
        // P3坐标
        float x3 = x - offsetX;
        float y3 = y + offsetY;
        // P4坐标
        float x4 = startX - offsetX;
        float y4 = startY + offsetY;

        float anchorX = (x + startX) / 2;
        float anchorY = (y + startY) / 2;

        float distance = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        mRadius = 20 - distance / 15;
        if (mRadius < RADIUS_LIMITS) {
            exploredImageView.setX(mCurPoint.x - exploredImageView.getWidth() / 2);
            exploredImageView.setY(mCurPoint.y - exploredImageView.getHeight() / 2);
            // TODO: 18-1-12 播放消息消失帧动画

        }

        mPath.reset();
        mPath.moveTo(x1, y1);
        mPath.quadTo(anchorX, anchorY, x2, y2);
        mPath.lineTo(x3, y3);
        mPath.quadTo(anchorX, anchorY, x4, y4);
        mPath.lineTo(x1, y1);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 判断手指下落的位置是不是在textView中
                int[] location = new int[2];
                mTipTextView.getLocationOnScreen(location);
                Rect rect = new Rect();
                rect.left = location[0];
                rect.top = location[1];
                rect.right = mTipTextView.getWidth() + location[0];
                rect.bottom = mTipTextView.getHeight() + location[1];
                if (rect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    mTouch = true;
                }

                break;
            case MotionEvent.ACTION_UP:
                mTouch = false;
                if (mRadius < RADIUS_LIMITS) {
                    mTipTextView.setVisibility(GONE);
                }
                break;
            case MotionEvent.ACTION_MOVE:

                break;
        }
        mCurPoint.set(event.getX(), event.getY());
        invalidate();
        return true;
    }
}

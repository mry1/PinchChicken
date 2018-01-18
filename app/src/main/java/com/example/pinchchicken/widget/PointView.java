package com.louis.pinchchicken.widget;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.louis.pinchchicken.bean.Point;

/**
 * Created by louis on 17-12-23.
 */

public class PointView extends View {

    private Point mCurPoint;
    private ValueAnimator animator;
    private ValueAnimator smallAnimation;

    public PointView(Context context) {
        super(context);
    }

    public PointView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PointView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCurPoint != null) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(300, 300, mCurPoint.getRadius(), paint);
        }

    }


    public void doPointScaleOutAnimation(int progress) {
        animator = ValueAnimator.ofObject(new PointEvaluator(), new Point(progress), new Point(100));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurPoint = (Point) animation.getAnimatedValue();
                invalidate();
            }
        });
        float v = (float) (100 - progress) / 100;
        animator.setDuration((long) (2000 * v));
//        animator.setRepeatCount(ValueAnimator.INFINITE);
//        animator.setRepeatMode(ValueAnimator.REVERSE);
//        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    public void doPointScaleInAnimation(int progress) {
        smallAnimation = ValueAnimator.ofObject(new PointEvaluator(), new Point(progress), new Point(0));
        smallAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurPoint = (Point) animation.getAnimatedValue();
                invalidate();
            }
        });

        float v = (float) progress / 100;
        smallAnimation.setDuration((long) (2000 * v));
//        smallAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        smallAnimation.start();
    }

    public void stopPointAnimation() {
        if (animator != null) {
            animator.cancel();
        }

    }


    public class PointEvaluator implements TypeEvaluator<Point> {

        @Override
        public Point evaluate(float fraction, Point startValue, Point endValue) {
            int start = startValue.getRadius();
            int end = endValue.getRadius();

            return new Point((int) (start + (fraction * (end - start))));
        }
    }


}

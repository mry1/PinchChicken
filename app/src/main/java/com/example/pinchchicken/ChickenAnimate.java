package com.example.pinchchicken;

import android.content.Context;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;

import com.richpath.RichPath;
import com.richpath.RichPathView;
import com.richpathanimator.AnimationListener;
import com.richpathanimator.RichPathAnimator;

/**
 * Created by weisc on 17-12-28.
 */

public class ChickenAnimate {
    private RichPathView mChickenView;
    private Context mContext;

    private String pathLeftWingUp;
    private String pathLeftWingDown;
    private String pathRightWingUp;
    private String pathRightWingDown;
    private RichPath leftWing;
    private RichPath rightWing;

    private DecelerateInterpolator decelerateInterpolator;
    private float mValue;
    private boolean isAnimating = false;

    public ChickenAnimate(Context context, RichPathView chickenView) {
        mContext = context;
        mChickenView = chickenView;
        initResource();
        decelerateInterpolator = new DecelerateInterpolator();
    }


    public void animate(float value) {
        mValue = value;
        if (!isAnimating) {
            if (value > 0) {
                isAnimating = true;
                animateWings();
            }
        } else {
            if (value <= 0) {
                isAnimating = false;
            }
        }

    }

    private void initResource() {
        pathLeftWingUp = getString(R.string.path_left_wing_up);
        pathLeftWingDown = getString(R.string.path_left_wing_down);
        pathRightWingUp = getString(R.string.path_right_wing_up);
        pathRightWingDown = getString(R.string.path_right_wing_down);
        leftWing = mChickenView.findRichPathByName(getString(R.string.left_wing));
        rightWing = mChickenView.findRichPathByName(getString(R.string.right_wing));
    }

    private void animateWings() {
        long duration = (long) (1000 - mValue * 9) / 2;

        Log.d("animate", "animate: " + isAnimating + " " + duration);

        RichPathAnimator.animate(leftWing)
                .interpolator(decelerateInterpolator)
                .pathData(pathLeftWingDown)
                .duration(duration)

                .andAnimate(rightWing)
                .interpolator(decelerateInterpolator)
                .pathData(pathRightWingDown)
                .duration(duration)

                .thenAnimate(leftWing)
                .interpolator(decelerateInterpolator)
                .pathData(pathLeftWingUp)
                .duration(duration)

                .andAnimate(rightWing)
                .interpolator(decelerateInterpolator)
                .pathData(pathRightWingUp)
                .duration(duration)
                .animationListener(animationListener)
                .start();
    }

    private String getString(int resId) {
        return mContext.getString(resId);
    }

    private final AnimationListener animationListener = new AnimationListener() {
        @Override
        public void onStart() {

        }

        @Override
        public void onStop() {
            if (isAnimating) {
                animateWings();
            }
        }
    };


}

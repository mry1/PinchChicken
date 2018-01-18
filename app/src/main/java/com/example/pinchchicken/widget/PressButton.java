package com.louis.pinchchicken.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * Created by louis on 17-12-19.
 */

@SuppressLint("AppCompatCustomView")
public class PressButton extends Button {
    private Context mContext;

    public PressButton(Context context) {
        super(context);
        mContext = context;
    }

    public PressButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

    }

    public PressButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按下
                onPressListener.onPress();
                break;
            case MotionEvent.ACTION_UP:
                //起来
                onPressListener.onRelease();
                break;
        }

        return super.onTouchEvent(event);
    }

    private OnPressListener onPressListener;

    public void setOnPressListener(OnPressListener onPressListener) {
        this.onPressListener = onPressListener;
    }

    public interface OnPressListener {
        /**
         * 按住button
         */
        void onPress();

        /**
         * 松开button
         */
        void onRelease();
    }

}

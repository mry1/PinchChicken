package com.example.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

/**
 * Created by louis on 18-1-6.
 */

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private Paint mPaint;
    private int WIDTH;
    private int HEIGHT;
    private BitmapFactory.Options options;
    private AnimThread thread;
    private int index = 0;
    private boolean isThreadRunning = false;
    private long mGapTime = 100;// 每帧动画持续时间

    public MySurfaceView(Context context) {
        this(context, null);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        WindowManager manger = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        manger.getDefaultDisplay().getMetrics(displayMetrics);
        WIDTH = displayMetrics.widthPixels;
        HEIGHT = displayMetrics.heightPixels;
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;


    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new AnimThread();
        thread.start();
        isThreadRunning = true;

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (thread != null)
            thread.stopThread();

    }

    public class AnimThread extends Thread {
        @Override
        public void run() {
            super.run();
            // 创建Bitmap并显示
            Canvas canvas = mHolder.lockCanvas();


            while(isThreadRunning){
                drawView();
                try {
                    Thread.sleep(mGapTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }

        public void stopThread() {
//            isrunning = false;
            try {
                join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void drawView() {


    }

}

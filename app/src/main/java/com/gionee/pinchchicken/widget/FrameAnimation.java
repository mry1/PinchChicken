package com.gionee.pinchchicken.widget;

/**
 * Created by louis on 18-1-6.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LruCache;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.gionee.pinchchicken.PinchActivity;
import com.gionee.pinchchicken.R;
import com.gionee.pinchchicken.SoundUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class FrameAnimation extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mSurfaceHolder;

    private boolean mIsThreadRunning = true; // 线程运行开关
    public static boolean mIsDestroy = false;// 是否已经销毁

    private int[] mBitmapResourceIds;// 用于播放动画的图片资源id数组
    private int totalCount;//资源总数
    private Canvas mCanvas;
    private Bitmap mBitmap;// 显示的图片
    private SparseArray<WeakReference<Bitmap>> weakBitmaps;
    private LruCache<Integer, Bitmap> lruBitmap;

    private int mCurrentIndex;// 当前动画播放的位置
    private boolean mIsRepeat = false;
    public static final int FLAG_PLAY_IN_ORDER = 0;
    public static final int FLAG_PLAY_IN_REVERSE_ORDER = 1;
    public static final int FLAG_INIT = 2;
    private int flag = FLAG_PLAY_IN_ORDER;

    private final String TAG = FrameAnimation.class.getSimpleName();
    private OnFrameFinishedListener mOnFrameFinishedListener;// 动画监听事件
    private AnimThread animThread;
    private BitmapFactory.Options options;

    public FrameAnimation(Context context) {
        this(context, null);
        initView();
    }

    public FrameAnimation(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initView();

    }

    public FrameAnimation(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {

        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);

        // 白色背景
        setZOrderOnTop(true);
        setZOrderMediaOverlay(true);
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        weakBitmaps = new SparseArray<WeakReference<Bitmap>>();

        // LruCache通过构造函数传入缓存值，以KB为单位。
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // 使用最大可用内存值的1/8作为缓存的大小。
        int cacheSize = maxMemory / 4;
        lruBitmap = new LruCache<Integer, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(Integer key, Bitmap bitmap) {
                // 重写此方法来衡量每张图片的大小，默认返回图片数量。
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void setCurrentIndext(int index) {
        this.mCurrentIndex = index;
    }

    public void stopThread() {
        mIsThreadRunning = false;
        try {
            animThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始动画
     */
    public void start() {
        if (!mIsDestroy) {
//            mCurrentIndex = 0;
            mIsThreadRunning = true;
            animThread = new AnimThread();
            animThread.start();
        } else {
            // 如果SurfaceHolder已经销毁抛出该异常
            try {
                throw new Exception("IllegalArgumentException:Are you sure the SurfaceHolder is not destroyed");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class AnimThread extends Thread {
        @Override
        public void run() {
            super.run();

            if (mOnFrameFinishedListener != null) {
                mOnFrameFinishedListener.onStart();
            }

            // 每隔mGapTime刷新屏幕
            while (mIsThreadRunning) {
//                Log.d(TAG, "========");
                drawView();
                try {
//                    Thread.sleep(mGapTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (mOnFrameFinishedListener != null) {
                mOnFrameFinishedListener.onStop();
            }

        }
    }


    public void setFlag(int flag) {
        this.flag = flag;
    }

    public void addBitmapToMemoryCache(int key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            lruBitmap.put(key, bitmap);
            Log.d(TAG, "addBitmapToMemoryCache: " + lruBitmap.get(key));
        }
    }

    public Bitmap getBitmapFromMemCache(int key) {
        return lruBitmap.get(key);
    }

    /**
     * 制图方法
     */
    private void drawView() {
        // 无资源文件退出
        if (mBitmapResourceIds == null) {
            Log.e("frameview", "the bitmapsrcIDs is null");

            mIsThreadRunning = false;

            return;
        }

        // 锁定画布
        if (mSurfaceHolder != null) {
            mCanvas = mSurfaceHolder.lockCanvas();
        }
        try {
            if (mSurfaceHolder != null && mCanvas != null) {

                mCanvas.drawColor(Color.WHITE);

                mBitmap = getBitmapFromMemCache(mCurrentIndex);
                Log.d(TAG, "drawView: mCurrentIndex = " + mCurrentIndex);
                if (mBitmapResourceIds != null && mBitmapResourceIds.length > 0) {
                    if (mBitmap == null) {
                        //Log.d(TAG, "bitmap == null======" + lruBitmap.putCount());
                        mBitmap = BitmapFactory.decodeResource(getResources(), mBitmapResourceIds[mCurrentIndex]);
                        addBitmapToMemoryCache(mCurrentIndex, mBitmap);
                    } else {
                        Log.d(TAG, "bitmap != null");

                    }
                }

                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setStyle(Paint.Style.STROKE);
                Rect mSrcRect, mDestRect;
                mSrcRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
                mDestRect = new Rect(0, 0, getWidth(), getHeight());

                // 清屏
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                mCanvas.drawPaint(paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

                mCanvas.drawBitmap(mBitmap, mSrcRect, mDestRect, paint);


                // 播放到最后一张图片
                if (mCurrentIndex == totalCount - 1) {
                    //TODO 设置重复播放
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            switch (flag) {
                case FLAG_PLAY_IN_ORDER:
                    mCurrentIndex++;
                    break;
                case FLAG_PLAY_IN_REVERSE_ORDER:
                    mCurrentIndex--;
                    break;
                case FLAG_INIT:
                    mCurrentIndex = 0;
                    mIsThreadRunning = false;
                    break;
            }


            if (mCurrentIndex >= totalCount) {
                mCurrentIndex = totalCount - 1;
            }
            if (mCurrentIndex < 0) {
                mCurrentIndex = 0;
                mIsThreadRunning = false;
            }

            if (mCanvas != null) {
                // 将画布解锁并显示在屏幕上
                if (mSurfaceHolder != null) {
                    mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                }
            }

            if (mBitmap != null) {
                // 收回图片
                if (mBitmap.isRecycled()) {
                    mBitmap.recycle();
                }
            }

        }
    }

    /**
     * 设置动画播放素材的id
     *
     * @param bitmapResourceIds 图片资源id
     */
    public void setBitmapResoursID(int[] bitmapResourceIds) {
        this.mBitmapResourceIds = bitmapResourceIds;
        totalCount = bitmapResourceIds.length;
    }

    /**
     * 结束动画
     */
    public void stop() {
        mIsThreadRunning = false;
    }

    /**
     * 继续动画
     */
    public void reStart() {
        mIsThreadRunning = true;
    }

    /**
     * 设置动画监听器
     */
    public void setOnFrameFinisedListener(OnFrameFinishedListener onFrameFinishedListener) {
        this.mOnFrameFinishedListener = onFrameFinishedListener;
    }

    /**
     * 动画监听器
     *
     * @author qike
     */
    public interface OnFrameFinishedListener {

        /**
         * 动画开始
         */
        void onStart();

        /**
         * 动画结束
         */
        void onStop();
    }

    /**
     * 当用户点击返回按钮时，停止线程，反转内存溢出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 当按返回键时，将线程停止，避免surfaceView销毁了,而线程还在运行而报错
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mIsThreadRunning = false;
        }

        return super.onKeyDown(keyCode, event);
    }


}

package com.gionee.pinchchicken.widget;

/**
 * Created by louis on 18-1-6.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LruCache;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.gionee.pinchchicken.BitmapProduceThread;
import com.jakewharton.disklrucache.DiskLruCache;

import java.util.ArrayList;

public class FrameAnimation extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mSurfaceHolder;

    private boolean mIsThreadRunning = true; // 线程运行开关
    public static boolean mIsDestroy = false;// 是否已经销毁

    private int[] mBitmapResourceIds;// 用于播放动画的图片资源id数组
    private ArrayList<String> mBitmapResourcePaths;// 用于播放动画的图片资源path数组
    private int totalCount;//资源总数
    private Canvas mCanvas;
    private Bitmap mBitmap;// 显示的图片
    private LruCache<Integer, Bitmap> lruBitmap;


    private int mCurrentIndex;// 当前动画播放的位置
    private int mGapTime = 200;// 每帧动画持续存在的时间
    public static final int FLAG_PLAY_IN_ORDER = 0;
    public static final int FLAG_PLAY_IN_REVERSE_ORDER = 1;
    public static final int FLAG_INIT = 2;
    private int flag = FLAG_PLAY_IN_ORDER;

    private final String TAG = FrameAnimation.class.getSimpleName();
    private OnFrameFinishedListener mOnFrameFinishedListener;// 动画监听事件
    private AnimThread animThread;
    private BitmapFactory.Options options;
    private DiskLruCache.Editor edit;
    private SparseArray<Bitmap> bitmapSparseArray;

    public FrameAnimation(Context context) {
        this(context, null);
        init(context);
    }

    public FrameAnimation(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public FrameAnimation(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);

    }

    private void init(Context context) {

        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);

        // 白色背景
        setZOrderOnTop(true);
        setZOrderMediaOverlay(true);
        options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        options.inPreferredConfig = Bitmap.Config.ALPHA_8;
        // LruCache通过构造函数传入缓存值，以KB为单位。
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // 使用最大可用内存值的1/2作为缓存的大小。
        int cacheSize = maxMemory;
        lruBitmap = new LruCache<Integer, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(Integer key, Bitmap bitmap) {
                // 重写此方法来衡量每张图片的大小，默认返回图片数量。
                return bitmap.getByteCount() / 1024;
//                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
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
        // 当surfaceView销毁时, 停止线程的运行. 避免surfaceView销毁了线程还在运行而报错.
//        mIsThreadRunning = false;
//        try {
//            Thread.sleep(mGapTime);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        mIsDestroy = true;
        Log.d(TAG, "===surfaceView被销毁");
        clearLruCache();
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

    public void clearLruCache() {
        lruBitmap = null;
    }

    /**
     * 开始动画
     */
    public void start() {
        if (!mIsDestroy) {
            mIsThreadRunning = true;
            animThread = new AnimThread();
            animThread.start();
            new BitmapProduceThread(mCurrentIndex, getContext(), lruBitmap, options, mBitmapResourceIds).start();
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
//                    Thread.sleep(mCurrentIndex);
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
        if (mBitmapResourceIds == null && mBitmapResourcePaths == null) {
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
                if (mBitmapResourceIds != null && mBitmapResourceIds.length > 0) {
                    if (mBitmap == null) {
                        Log.d(TAG, "bitmap == null======" + lruBitmap.size());
                        long l = SystemClock.currentThreadTimeMillis();
                        mBitmap = BitmapFactory.decodeStream(getResources().openRawResource(mBitmapResourceIds[mCurrentIndex]), null, options);
                        Log.d(TAG, "=========" + (SystemClock.currentThreadTimeMillis() - l));
                        addBitmapToMemoryCache(mCurrentIndex, mBitmap);

                    } else {
                        Log.d(TAG, "bitmap != null");

                    }
                } else if (mBitmapResourcePaths != null && mBitmapResourcePaths.size() > 0) {
                    if (mBitmap == null) {
                        mBitmap = BitmapFactory.decodeStream(getResources().openRawResource(mBitmapResourceIds[mCurrentIndex]), null, options);
                        addBitmapToMemoryCache(mCurrentIndex, mBitmap);
                    }
                }

                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setStyle(Paint.Style.STROKE);

                // 清屏
                paint.setXfermode(new PorterDuffXfermode(
                        PorterDuff.Mode.CLEAR));
                mCanvas.drawPaint(paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

                Matrix mMatrix = new Matrix();
                mMatrix.postScale((float) getWidth() / mBitmap.getWidth(),
                        (float) getHeight() / mBitmap.getHeight());
                long l = SystemClock.currentThreadTimeMillis();
                mCanvas.drawBitmap(mBitmap, mMatrix, paint);
//                Log.d(TAG, "=========" + (SystemClock.currentThreadTimeMillis() - l));

                // 播放到最后一张图片
                if (mCurrentIndex == totalCount - 1) {
                    //TODO 设置重复播放
                    //播放到最后一张，当前index置零
//                    mCurrentIndex = 0;
                }


                if (mBitmap != null) {
                    // 收回图片
                    if (mBitmap.isRecycled()) {
                        mBitmap.recycle();
                        mBitmap = null;
                    }
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
                    mBitmap = null;
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
        for (int i = 0; i < 3; i++) {

        }
    }

    /**
     * 设置动画播放素材的路径
     *
     * @param bitmapResourcePaths
     */
    public void setmBitmapResourcePath(ArrayList bitmapResourcePaths) {
        this.mBitmapResourcePaths = bitmapResourcePaths;
        totalCount = bitmapResourcePaths.size();
    }

    /**
     * 设置每帧时间
     */
    public void setGapTime(int gapTime) {
        this.mGapTime = gapTime;
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

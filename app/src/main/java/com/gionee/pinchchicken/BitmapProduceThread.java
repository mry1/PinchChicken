package com.gionee.pinchchicken;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.util.Log;
import android.util.LruCache;

import com.gionee.pinchchicken.widget.FrameAnimation;

/**
 * Created by louis on 18-1-9.
 */

public class BitmapProduceThread extends Thread {
    private int[] mBitmapResourceIds;// 用于播放动画的图片资源id数组
    private int mCurrentIndex = 0;
    private Context context;
    private final String TAG = BitmapProduceThread.class.getSimpleName();
    LruCache<Integer, Bitmap> lruBitmap;
    BitmapFactory.Options options;

    public BitmapProduceThread(int index, Context context, LruCache<Integer, Bitmap> lruBitmap, BitmapFactory.Options options, int[] mBitmapResourceIds) {
        this.context = context;
        this.mCurrentIndex = index;
        this.lruBitmap = lruBitmap;
        this.options = options;
        this.mBitmapResourceIds = mBitmapResourceIds;
    }

    @Override
    public void run() {
        super.run();
        // 不断创建bitmap
        while (mCurrentIndex <= 100) {
            try {
                // 实例化Bitmap
                Bitmap mBitmap = BitmapFactory.decodeStream(context.getResources().openRawResource(mBitmapResourceIds[mCurrentIndex]), null, options);
                addBitmapToMemoryCache(mCurrentIndex, mBitmap);
                mCurrentIndex++;

            } catch (OutOfMemoryError e) {

            }
        }

    }

    public Bitmap getBitmapFromMemCache(int key) {
        return lruBitmap.get(key);
    }

    public void addBitmapToMemoryCache(int key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            lruBitmap.put(key, bitmap);
        }
    }
}

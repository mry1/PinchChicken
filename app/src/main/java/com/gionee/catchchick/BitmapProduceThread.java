package com.gionee.catchchick;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import com.gionee.catchchick.utils.CacheUtils;

/**
 * Created by louis on 18-1-9.
 */

public class BitmapProduceThread extends Thread {
    private int[] mBitmapResourceIds;// 用于播放动画的图片资源id数组
    private int mCurrentIndex = 0;
    private Context context;
    private final String TAG = BitmapProduceThread.class.getSimpleName();
    BitmapFactory.Options options;

    public BitmapProduceThread(int index, Context context, BitmapFactory.Options options, int[] mBitmapResourceIds) {
        this.context = context;
        this.mCurrentIndex = index;
        this.options = options;
        this.mBitmapResourceIds = mBitmapResourceIds;
    }

    @Override
    public void run() {
        super.run();
        // 不断创建bitmap
        while (mCurrentIndex <= 100) {
//            try {
            // 实例化Bitmap
            Bitmap mBitmap = BitmapFactory.decodeStream(context.getResources().openRawResource(mBitmapResourceIds[mCurrentIndex]), null, options);
            CacheUtils.addBitmapToMemoryCache(mCurrentIndex, mBitmap);
            mCurrentIndex++;

            if (mBitmap != null) {
                if (mBitmap.isRecycled()) {
                    mBitmap.recycle();
                    mBitmap = null;
                }
            }
//            } catch (OutOfMemoryError e) {
//
//            }
        }

    }

}

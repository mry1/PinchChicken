package com.gionee.catchchick.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;


import com.gionee.catchchick.source.BgSrc;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * File Description:
 * Author:weisc
 * Create Date:18-1-10
 * Change List:
 */

public class BitmapLoader {
    private static final String TAG = "BitmapLoader";
    //    private static final int DECODE_THREADS =1;
    private static BitmapLoader sInstance;
    private final BitmapCache mCache = new BitmapCache();
    private int[] mBitmapResourceIds;// 用于播放动画的图片资源id数组
    private int mTotalCount;//资源总数

    private int mLastKey;


    private final ExecutorService mThreadPool;
    private boolean mFirst;


    private BitmapLoader() {

//        mThreadPool = Executors.newFixedThreadPool(DECODE_THREADS);
        mThreadPool = Executors.newCachedThreadPool();
        mFirst = true;
        this.mBitmapResourceIds = BgSrc.srcId;
        mTotalCount = mBitmapResourceIds.length;
    }

    public synchronized static BitmapLoader getLoaderInstance() {
        if (sInstance == null) {
            sInstance = new BitmapLoader();
        }
        return sInstance;
    }


    public void loadResources(Context context, int from, int to) {
        if (mBitmapResourceIds != null) {
            from = from < 0 ? 0 : from;
            to = to < mTotalCount ? to : mTotalCount;
            for (int i = from; i < to; i++) {
                InputStream inputStream = context.getResources().openRawResource(mBitmapResourceIds[i]);
                mThreadPool.execute(new DecodeBitmapThread(inputStream, i));
            }
        }
    }

    public Bitmap loadResources(Context context, int key) {
        if (mBitmapResourceIds != null && key < mTotalCount && key >= 0) {
            int scope = 5;
            int half = scope >> 1;
            if (mFirst) {
                mFirst = false;
                loadResources(context, key, key + scope);
                return mCache.get(0);
            } else {
                int right = key + half, left = key - half;
                int needLoad = -1;
                if (key > mLastKey) {
                    if (right < mTotalCount) {
                        needLoad = right;
                        if (left - 1 >= 0) {
                            mCache.remove(left - 1);
                        }
                    }
                } else {
                    if (left >= 0) {
                        needLoad = left;
                        if (right + 1 < mTotalCount) {
                            mCache.remove(right + 1);
                        }
                    }
                }
                if (needLoad != -1) {
                    InputStream inputStream = context.getResources().openRawResource(mBitmapResourceIds[needLoad]);
                    mThreadPool.execute(new DecodeBitmapThread(inputStream, needLoad));
                }
            }
            mLastKey = key;
        }
        return mCache.get(key);

    }


    public void destroy() {
        mThreadPool.shutdown();
        mCache.clear();
        sInstance = null;
    }


    private class DecodeBitmapThread implements Runnable {

        InputStream inputStream;
        int key;

        public DecodeBitmapThread(InputStream inputstream, int key) {
            this.inputStream = inputstream;
            this.key = key;
        }

        @Override
        public void run() {
            try {
                Bitmap bitmap = BitmapCache.decodeBitmapFromStream(inputStream);
                mCache.put(key, bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


}

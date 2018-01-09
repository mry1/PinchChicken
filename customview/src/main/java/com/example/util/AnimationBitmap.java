package com.example.util;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * File Description:
 * Author:weisc
 * Create Date:18-1-8
 * Change List:
 */

public class AnimationBitmap {
    private final LruCache<Integer, Bitmap> cache;
    private final Set<SoftReference<Bitmap>> recycleBitmap = Collections.synchronizedSet(new HashSet<SoftReference<Bitmap>>());

    private final int MAX_SIZE = 10;
    private final Object lock = new Object();

    public int getBitmapNum() {
        return bitmapNum;
    }

    public void setBitmapNum(int bitmapNum) {
        this.bitmapNum = bitmapNum;
    }

    private int bitmapNum;

    AnimationBitmap() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int mCacheSize = maxMemory / 8;
        cache = new LruCache<Integer, Bitmap>(mCacheSize) {
            @Override
            protected void entryRemoved(boolean evicted, Integer key,
                                        Bitmap oldValue, Bitmap newValue) {
                recycleBitmap.add
                        (new SoftReference<>(oldValue));
            }
        };

    }

    public synchronized void put(int key, Bitmap bitmap) {
        Log.d("weisc", "put: "+key);
        if (cache.size() == MAX_SIZE) {
            try {
                Log.d("weisc", "put before wait " + key);
                wait();
                Log.d("weisc", "put after wait " + key);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        cache.put(key, bitmap);
        notify();
    }


    public synchronized Bitmap get(int key) {
        Bitmap bitmap;
        while ((cache.get(key)) == null) {
            try {
                Log.d("weisc", "get before wait " + key);
                wait();
                Log.d("weisc", "get after wait " + key);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d("weisc", "get: "+key);
        bitmap = cache.remove(key);
        notify();
        return bitmap;
    }

    public boolean contain(int key) {
        return !(cache.get(key) == null);
    }

    protected Bitmap getBitmapFromReusableSet() {
        Bitmap bitmap = null;
        if (recycleBitmap != null && !recycleBitmap.isEmpty()) {
            synchronized (recycleBitmap) {
                final Iterator<SoftReference<Bitmap>> iterator = recycleBitmap.iterator();
                Bitmap item;
                while (iterator.hasNext()) {
                    item = iterator.next().get();

                    if (null != item && item.isMutable()) {
                        bitmap = item;
                        iterator.remove();
                        break;
                    } else {
                        iterator.remove();
                    }
                }
            }
        }
        return bitmap;
    }

}

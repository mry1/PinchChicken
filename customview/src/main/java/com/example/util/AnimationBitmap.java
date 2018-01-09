package com.example.util;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.util.SparseIntArray;

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

    private final SparseIntArray testMap = new SparseIntArray();

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
                testMap.delete(key);
            }
        };

    }

    public synchronized void put(int key, Bitmap bitmap) {
        Log.d("check", "put: " + key);
        if (cache.size() == MAX_SIZE) {
            try {
//                Log.d("check", "put before wait " + key);
                wait();
//                Log.d("check", "put after wait " + key);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        cache.put(key, bitmap);
        testMap.put(key, key);
        Log.d("check", "put key " + key + " key map : " + testMap.toString());
        notify();
    }


    public synchronized Bitmap get(int key) {
        Bitmap bitmap;
        while ((cache.get(key)) == null) {
            try {
//                Log.d("check", "get before wait " + key);
                wait();
//                Log.d("check", "get after wait " + key);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d("check", "get key " + key + " key map : " + testMap.toString());
//        Log.d("check", "get: cache size " + cache.size() + " recycleBitmap size " + recycleBitmap.size());

        bitmap = cache.get(key);
        notify();
        return bitmap;
    }

    public synchronized void remove(int key) {
        cache.remove(key);
        notify();
    }

    public void clear() {
        cache.evictAll();
    }

    public synchronized void removeUnusedCache(int key) {
        for (int i = 0; i <= key - 3; i++) {
            cache.remove(i);
        }
        for (int i = key + 3; i < bitmapNum; i++) {
            cache.remove(i);
        }
        notify();

    }

    public boolean isFull() {
        return cache.size() == MAX_SIZE;
    }

    public synchronized boolean contain(int key) {
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

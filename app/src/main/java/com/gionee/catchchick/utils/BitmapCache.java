package com.gionee.catchchick.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;
import android.util.SparseIntArray;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static android.content.ContentValues.TAG;

/**
 * File Description:
 * Author:weisc
 * Create Date:18-1-10
 * Change List:
 */

public class BitmapCache {
    private final static Set<SoftReference<Bitmap>> recycleBitmap = Collections.synchronizedSet(new HashSet<SoftReference<Bitmap>>());
    private final LruCache<Integer, Bitmap> cache;
    private final SparseIntArray removeInFuture = new SparseIntArray();
    private final SparseIntArray testMap = new SparseIntArray();


    public int getBitmapNum() {
        return bitmapNum;
    }

    public void setBitmapNum(int bitmapNum) {
        this.bitmapNum = bitmapNum;
    }

    private int bitmapNum;

    public BitmapCache() {
        // LruCache通过构造函数传入缓存值，以KB为单位。
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // 使用最大可用内存值的1/2作为缓存的大小。
        int cacheSize = maxMemory * 4 / 5;
        cache = new LruCache<Integer, Bitmap>(cacheSize) {
            @Override
            protected void entryRemoved(boolean evicted, Integer key,
                                        Bitmap oldValue, Bitmap newValue) {
                recycleBitmap.add
                        (new SoftReference<>(oldValue));
                testMap.delete(key);
            }

            @Override
            protected int sizeOf(Integer key, Bitmap bitmap) {
                // 重写此方法来衡量每张图片的大小，默认返回图片数量。
                return bitmap.getByteCount() / 1024;
//                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };

    }

    public void logMap(String msg) {
        Log.d("weisc_final", msg + " testMap: " + testMap + " removeInFuture: " + removeInFuture + " recycle size: " + recycleBitmap.size() + " cache size: " + cache.size());
    }

    public synchronized void put(int key, Bitmap bitmap) {
        int needRemoveCount = removeInFuture.get(key, 0);
        if (needRemoveCount == 0) {
            cache.put(key, bitmap);
            testMap.put(key, key);
        } else {
            if (--needRemoveCount == 0) {
                removeInFuture.delete(key);
            } else {
                removeInFuture.put(key, needRemoveCount);
            }
        }
        logMap("");
        notify();
    }


    public synchronized Bitmap get(int key) {
        Bitmap bitmap;
        while ((cache.get(key)) == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        bitmap = cache.get(key);
        notify();
        return bitmap;
    }

    public synchronized void remove(int key) {
        if (!contain(key)) {
            int oldValue = removeInFuture.get(key, 0);
            removeInFuture.put(key, oldValue + 1);
            return;
        }
        cache.remove(key);
    }

    public void clear() {
        cache.evictAll();

    }


    public boolean contain(int key) {
        return !(cache.get(key) == null);
    }

    public static Bitmap decodeBitmapFromStream(InputStream inputStream) {
        long start = System.currentTimeMillis();
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        options.inPreferredConfig = Bitmap.Config.ALPHA_8;
        addInBitmapOptions(options);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        Log.d(TAG, "decode From Stream" + (System.currentTimeMillis() - start));
        return bitmap;
    }

    private static Bitmap getBitmapFromReusableSet() {
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


    private static void addInBitmapOptions(BitmapFactory.Options options) {
        options.inMutable = true;
        Bitmap inBitmap = getBitmapFromReusableSet();
        if (inBitmap != null) {
            options.inBitmap = inBitmap;
        }
    }

}

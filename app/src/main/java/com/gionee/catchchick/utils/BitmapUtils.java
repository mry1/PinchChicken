package com.gionee.catchchick.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by louis on 18-1-8.
 */

public class BitmapUtils {
    private LruCache<Integer, Bitmap> lruBitmap;
    DiskLruCache mDiskLruCache = null;
    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 50;

    public void init(Context context) {
        // LruCache通过构造函数传入缓存值，以KB为单位。
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // 使用最大可用内存值的1/8作为缓存的大小。
        int cacheSize = maxMemory / 4;
        lruBitmap = new LruCache<Integer, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(Integer key, Bitmap bitmap) {
                // 重写此方法来衡量每张图片的大小，默认返回图片数量。
//                return bitmap.getByteCount() / 1024;
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };

        try {
            File cacheDir = CacheUtils.getDiskCacheDir(context, "bitmap");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            mDiskLruCache = DiskLruCache.open(cacheDir, CacheUtils.getAppVersion(context), 1, DISK_CACHE_SIZE);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap loadBitmap(String key) throws IOException {
        Bitmap bitmap = getBitmapFromMemCache(Integer.valueOf(key));
        if (bitmap != null) {
            return bitmap;
        }
        bitmap = getBitmapFromDiskCache(key);
        if (bitmap != null) {
            return bitmap;
        }
        return bitmap;
    }

    public void addBitmapToDiskCache(String key, Bitmap bitmap){
        try {
            DiskLruCache.Editor edit = mDiskLruCache.edit(key);
            if (edit != null){
                OutputStream outputStream = edit.newOutputStream(0);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getBitmapFromDiskCache(String key) throws IOException {
        if (mDiskLruCache == null) {
            return null;
        }

        Bitmap bitmap = null;
        String MD5key = CacheUtils.hashKeyForDisk(key);
        DiskLruCache.Snapshot snapShot = mDiskLruCache.get(MD5key);
        if (snapShot != null) {
            FileInputStream fileInputStream = (FileInputStream) snapShot.getInputStream(0);
            FileDescriptor fileDescriptor = fileInputStream.getFD();
            bitmap = BitmapFactory.decodeStream(fileInputStream);
            if (bitmap != null) {
                addBitmapToMemoryCache(Integer.valueOf(key), bitmap);
            }
        }

        return bitmap;

    }

    public void addBitmapToMemoryCache(int key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            lruBitmap.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(int key) {
        return lruBitmap.get(key);
    }

    private void compressImage(Bitmap image, int reqSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，
        int options = 100;
        while (baos.toByteArray().length / 1024 > reqSize) { // 循环判断压缩后的图片是否大于reqSize，大于则继续压缩
            baos.reset();//清空baos
            image.compress(Bitmap.CompressFormat.PNG, options, baos);// 这里压缩options%，把压缩后的数据放到baos中
            options -= 10;
        }
        // 把压缩后的baos放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        //decode图片
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
    }

}

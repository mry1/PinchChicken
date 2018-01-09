package com.example.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IdRes;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * File Description:
 * Author:weisc
 * Create Date:18-1-3
 * Change List:
 */

public class AnimationLoader {
    private static final String TAG = "AnimationLoader";
    private static final int DECODE_THREADS = 2;
    private static final int MSG_LOAD = 1;
    private static final int MSG_LOAD_SINGLE = 2;
    private static AnimationLoader sInstance;

    private AnimationList mAnimationList;
    private Animation mDefaultAnimation;
    private HandlerThread mThread;
    private Handler mHandler;
    private AnimationLoaderListener mListener;
    private final AnimationBitmap mAnimationBitmap = new AnimationBitmap();

    private final ExecutorService mThreadPool;


    private AnimationLoader() {
        mThread = new HandlerThread("AnimationLoaderThread");
        mThread.start();
        mHandler = new AnimationLoaderHandler(mThread.getLooper());

        mThreadPool = Executors.newFixedThreadPool(DECODE_THREADS);
    }

    public synchronized static AnimationLoader getLoaderInstance() {
        if (sInstance == null) {
            sInstance = new AnimationLoader();
        }
        return sInstance;
    }


    public void setAnimationLoaderListener(AnimationLoaderListener listener) {
        this.mListener = listener;
    }


    private Animation findAnimation(String name) {
        if (name == null || mAnimationList == null) {
            return null;
        }
        List<Animation> animationList = mAnimationList.getAnimations();
        for (Animation animation : animationList) {
            if (name.equals(animation.getName())) {
                return animation;
            }
        }
        return null;
    }

    private void loadAnimationList(Context context) throws IOException {
        InputStreamReader inputStreamReader = null;
        try {
            InputStream inputStream = context.getAssets().open("animation.json");
            inputStreamReader = new InputStreamReader(inputStream);

            mAnimationList = new Gson().fromJson(inputStreamReader, AnimationList.class);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (mAnimationList != null) {
            AssetManager assetManager = context.getAssets();
            mDefaultAnimation = findAnimation(mAnimationList.getDefaultAnimation());
            String[] resList = assetManager.list(mDefaultAnimation.getPath());
            mDefaultAnimation.setResList(resList);
            mAnimationBitmap.setBitmapNum(resList.length);
        }
        if (mListener != null) {
            mListener.onLoaderCreated(mAnimationBitmap);
        }
    }

    public synchronized void loadResources(Context context, int from, int to) {
        if (from < 0) {
            return;
        }
        Log.d(TAG, "loadResources: from " + from + " to " + to);
        try {
            if (mAnimationList == null) {
                loadAnimationList(context);
            }
            if (Thread.currentThread() != mThread) {
                Message msg = Message.obtain();
                msg.obj = context;
                msg.arg1 = from;
                msg.arg2 = to;
                msg.what = MSG_LOAD;
                mHandler.sendMessage(msg);
                return;
            }
            if (mDefaultAnimation != null) {
//                if (mListener != null) {
//                    mListener.onLoad(mAnimationBitmap);
//                }
                AssetManager assetManager = context.getAssets();
                String parentPath = mDefaultAnimation.getPath();
                String path;
                String[] resList = mDefaultAnimation.getResList();
                if (resList != null && to < resList.length) {
                    for (int i = from; i < to; i++) {
                        path = parentPath + File.separator + resList[i];
                        mThreadPool.execute(new DecodeBitmapThread(assetManager, path, i));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public synchronized void loadResources(Context context, int resId) {
        if (resId < 0) {
            return;
        }
        try {
            if (mAnimationList == null) {
                loadAnimationList(context);
            }
            if (Thread.currentThread() != mThread) {
                Message msg = Message.obtain();
                msg.obj = context;
                msg.arg1 = resId;
                msg.what = MSG_LOAD_SINGLE;
                mHandler.sendMessage(msg);
                return;
            }
            if (mDefaultAnimation != null) {
                AssetManager assetManager = context.getAssets();
                String parentPath = mDefaultAnimation.getPath();
                String path;
                String[] resList = mDefaultAnimation.getResList();
                if (resList != null) {
                    for (int i = resId; i > resId - 3 && i >= 0; i--) {
                        if (!mAnimationBitmap.contain(i)) {
                            path = parentPath + File.separator + resList[i];
                            mThreadPool.execute(new DecodeBitmapThread(assetManager, path, i));
                        }
                    }
                    for (int i = resId; i < resId + 3 & i < mAnimationBitmap.getBitmapNum(); i++) {
                        if (!mAnimationBitmap.contain(i))
                            Log.d(TAG, "loadResources: " + i);
                        {
                            path = parentPath + File.separator + resList[i];
                            mThreadPool.execute(new DecodeBitmapThread(assetManager, path, i));
                        }
                    }

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public Bitmap decodeBitmapFromStream(InputStream inputStream) throws IOException {
        long start = System.currentTimeMillis();
        final BitmapFactory.Options options = new BitmapFactory.Options();
        addInBitmapOptions(options);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        Log.d(TAG, "decode From Stream" + (System.currentTimeMillis() - start));
        return bitmap;
    }


    private void addInBitmapOptions(BitmapFactory.Options options) {
        options.inMutable = true;
        Bitmap inBitmap = mAnimationBitmap.getBitmapFromReusableSet();
        if (inBitmap != null) {
            options.inBitmap = inBitmap;
        }
    }


    private class DecodeBitmapThread implements Runnable {
        AssetManager assetManager;
        String path;
        int key;

        public DecodeBitmapThread(AssetManager assetManager, String path, int key) {
            this.assetManager = assetManager;
            this.path = path;
            this.key = key;
        }

        @Override
        public void run() {
            try {
                InputStream inputStream = assetManager.open(path);
                Bitmap bitmap = decodeBitmapFromStream(inputStream);
                mAnimationBitmap.put(key, bitmap);
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private class AnimationLoaderHandler extends Handler {
        public AnimationLoaderHandler() {
            super();
        }

        public AnimationLoaderHandler(Callback callback) {
            super(callback);
        }

        public AnimationLoaderHandler(Looper looper) {
            super(looper);
        }

        public AnimationLoaderHandler(Looper looper, Callback callback) {
            super(looper, callback);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD: {
                    Context context = (Context) msg.obj;
                    int from = msg.arg1;
                    int to = msg.arg2;
                    loadResources(context, from, to);
                    break;
                }
                case MSG_LOAD_SINGLE: {
                    Context context = (Context) msg.obj;
                    int resId = msg.arg1;
                    loadResources(context, resId);
                    break;
                }
            }

        }
    }

    public interface AnimationLoaderListener {
        void onLoad(AnimationBitmap animationBitmap);

        void onLoaderCreated(AnimationBitmap animationBitmap);
    }

}

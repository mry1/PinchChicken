package com.example.customview;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class MainActivity extends AppCompatActivity {

    private ImageView iv;
    private int picIndex;
    int i = 0;
    private FrameAnimation mFrameAnimation;
    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        iv = findViewById(R.id.iv);
//        picIndex = 0x7f060001;
//        mHandler.sendEmptyMessage(2);
//        mFrameAnimation = (FrameAnimation) findViewById(R.id.frame_animation);
//
//        initAnimation();
    }

    private void initAnimation() {
        //设置资源文件
        mFrameAnimation.setBitmapResoursID(BgSrc.srcId);
        //设置监听事件
        mFrameAnimation.setOnFrameFinisedListener(new FrameAnimation.OnFrameFinishedListener() {
            @Override
            public void onStop() {
                Log.e(TAG, "stop");

            }

            @Override
            public void onStart() {
                Log.e(TAG, "start");
                Log.e(TAG, Runtime.getRuntime().totalMemory() / 1024 + "k");
            }
        });
        mFrameAnimation.setFlag(FrameAnimation.FLAG_INIT);
        //设置单张图片展示时长
        mFrameAnimation.setGapTime(150);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mHandler.removeMessages(2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                //手指放下
////                mFrameAnimation.setCurrentIndext(0);
//                mFrameAnimation.setFlag(FrameAnimation.FLAG_PLAY_IN_ORDER);
//                mFrameAnimation.start();
//
//                break;
//            case MotionEvent.ACTION_UP:
//                //手指抬起
//                mFrameAnimation.setFlag(FrameAnimation.FLAG_PLAY_IN_REVERSE_ORDER);
//
//                break;
//        }
        return super.onTouchEvent(event);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 2:
                    if (i > 100) {
                        mHandler.removeMessages(2);
                        break;
                    }
                    Glide.with(MainActivity.this).load(picIndex + i).dontAnimate().listener(new RequestListener<Integer, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            // 加载完成
                            mHandler.sendEmptyMessageDelayed(2, 100);
                            i++;
                            return false;
                        }
                    }).into(iv);
                    System.out.println("========" + i);
                    break;
            }


        }


    };
}

package com.gionee.pinchchicken;

import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;

import com.gionee.pinchchicken.bean.BgSrc;
import com.gionee.pinchchicken.widget.FrameAnimation;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PinchActivity extends AppCompatActivity {


    @BindView(R.id.frame_animation)
    FrameAnimation mFrameAnimation;

    private SoundPool soundPool;
    private int playID = 0;
    private Unbinder unbinder;

    private final String TAG = PinchActivity.class.getSimpleName();

    /**
     * liminglin 添加声音相关变量
     */
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        initSoundPool();
        initAnimation();
    }

    /**
     * liminglin
     *
     * 初始化 SoundPool
     */
    private void initSoundPool() {
        SoundUtils.initSource(BgSrc.rawIds);
        soundPool = SoundUtils.getSoundPool();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouchEvent: soundPool = " + soundPool);
                //手指放下
                playID = SoundUtils.playSound(soundPool,this,R.raw.fechick);
                playMusic();
                // mFrameAnimation.setCurrentIndext(0);
                mFrameAnimation.setFlag(FrameAnimation.FLAG_PLAY_IN_ORDER);
//                mFrameAnimation.start();

                break;
            case MotionEvent.ACTION_UP:
                //手指抬起
                mFrameAnimation.setFlag(FrameAnimation.FLAG_PLAY_IN_REVERSE_ORDER);

                SoundUtils.stopSound(soundPool, playID);

                break;
        }
        return super.onTouchEvent(event);
    }

    private void initAnimation() {
        //设置资源文件
        mFrameAnimation.setBitmapResoursID(BgSrc.srcId);
        //设置监听事件
        /*mFrameAnimation.setOnFrameFinisedListener(new FrameAnimation.OnFrameFinishedListener() {
            @Override
            public void onStop() {
                Log.e(TAG, "stop");

            }

            @Override
            public void onStart() {
                Log.e(TAG, "start");
                Log.e(TAG, Runtime.getRuntime().totalMemory() / 1024 + "k");
            }
        });*/
        mFrameAnimation.setFlag(FrameAnimation.FLAG_INIT);
        //设置单张图片展示时长
        mFrameAnimation.setGapTime(150);
    }

    /**
     * liminglin
     *
     * 播放音乐
     */
    private void playMusic(){
        currentIndex = mFrameAnimation.getmCurrentIndex();
        Log.d(TAG, "playMusic: currentIndex = " + currentIndex);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }


}

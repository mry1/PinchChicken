package com.gionee.catchchick;

import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.gionee.catchchick.source.BgSrc;
import com.gionee.catchchick.utils.SoundUtils;
import com.gionee.catchchick.widget.FrameAnimation;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PinchActivity extends AppCompatActivity {


    @BindView(R.id.frame_animation)
    FrameAnimation mFrameAnimation;

    private Unbinder unbinder;

    private final String TAG = PinchActivity.class.getSimpleName();

    /**
     * liminglin 添加声音相关变量
     */
    private int currentIndex;
    private int gamePlayID;
    private SoundPool gameSoundPool;
    private SoundPool backgroundSoundPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        initAnimation();
    }

    /**
     * liminglin
     * <p>
     * 初始化 SoundPool
     */
    private void initSoundPool() {
        SoundUtils.initSource(BgSrc.rawIds);
        backgroundSoundPool = SoundUtils.getBackgroundSoundPool();
        gameSoundPool = SoundUtils.getGameSoundPool();
        SoundUtils.loadBackgroundSoundPool(backgroundSoundPool,this);
        SoundUtils.loadGameSoundPool(gameSoundPool, this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //手指放下
                gamePlayID = SoundUtils.playGameSound(gameSoundPool);
                // TODO 当 currentIndex = 100 的时候停止播放音频
                // mFrameAnimation.setCurrentIndex(0);
                mFrameAnimation.setFlag(FrameAnimation.FLAG_PLAY_IN_ORDER);
//                mFrameAnimation.start();

                break;
            case MotionEvent.ACTION_UP:
                //手指抬起
                mFrameAnimation.setFlag(FrameAnimation.FLAG_PLAY_IN_REVERSE_ORDER);

                SoundUtils.stopSound(gameSoundPool,gamePlayID);

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




    @Override
    protected void onResume() {
        super.onResume();
        initSoundPool();
        SoundUtils.playBackgroundSound(backgroundSoundPool);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        backgroundSoundPool.release();
        gameSoundPool.release();
    }


}

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        soundPool = SoundUtils.getSoundPool(PinchActivity.this, R.raw.fechick);

        initAnimation();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //手指放下
//                mFrameAnimation.setCurrentIndext(0);
                mFrameAnimation.setFlag(FrameAnimation.FLAG_PLAY_IN_ORDER);
                mFrameAnimation.start();


                playID = SoundUtils.playSound(soundPool);
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
        unbinder.unbind();
    }


}

package com.example.pinchchicken;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.pinchchicken.widget.PointView;
import com.example.pinchchicken.widget.PressButton;

public class PinchActivity extends AppCompatActivity {

    private PressButton btnPlay;
    private SoundPool soundPool;
    private int playID = 0;
    private Button btn1;
    private ValueAnimator scaleAnimation;
    private PointView pv;
    ProgressBar mProgressBar;
    private static final int MSG_PROGRESS_UPDATE = 0x110;
    private ProgressBar pb_org;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        mProgressBar = findViewById(R.id.progress_bar);
        mProgressBar = findViewById(R.id.pb_org);
        btnPlay = findViewById(R.id.btn_play);
        btn1 = findViewById(R.id.btn1);
        pv = findViewById(R.id.pv);
        //        this.bezier = findViewById(R.id.bezier);


        btnPlay.setOnPressListener(new PressButton.OnPressListener() {
            @Override
            public void onPress() {
                soundPool = getSoundPool(PinchActivity.this, R.raw.fechick);
                playID = playSound(soundPool);
//                scaleAnimation = startAnimation(btn1);
                pv.doPointScaleOutAnimation(mProgressBar.getProgress());
                mHandler.removeMessages(2);
                mHandler.sendEmptyMessage(MSG_PROGRESS_UPDATE);
            }

            @Override
            public void onRelease() {
                pv.stopPointAnimation();
                pv.doPointScaleInAnimation(mProgressBar.getProgress());
                stopSound(soundPool, playID);
//                stopAnimation(scaleAnimation);
                mHandler.removeMessages(MSG_PROGRESS_UPDATE);
                mHandler.sendEmptyMessage(2);

            }
        });


    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_PROGRESS_UPDATE:
                    int progress = mProgressBar.getProgress();
                    mProgressBar.setProgress(++progress);
                    if (progress >= 100) {
                        mHandler.removeMessages(MSG_PROGRESS_UPDATE);
                    }
                    mHandler.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 20);
                    break;
                case 2:
                    int progress1 = mProgressBar.getProgress();
                    mProgressBar.setProgress(--progress1);
                    if (progress1 <= 0) {
                        mHandler.removeMessages(2);
                    }
                    mHandler.sendEmptyMessageDelayed(2, 20);
                    break;
            }


        }


    };

    public ValueAnimator startAnimation(final View v) {


        ObjectAnimator colorAnimator1 = ObjectAnimator.ofInt(v, "BackgroundColor", 0xffff00ff, 0xffffff00, 0xffff00ff);
        colorAnimator1.setDuration(1000);
        colorAnimator1.setEvaluator(new ArgbEvaluator());
//        animator1.setRepeatMode(ValueAnimator.REVERSE);
//        animator1.start();

        ValueAnimator translateAnimator = ValueAnimator.ofInt(0, 600);
        translateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                System.out.println("========" + value);
                v.layout(v.getLeft(), value, v.getRight(), value + v.getHeight());
            }
        });


        translateAnimator.setDuration(1000);
        translateAnimator.setInterpolator(new BounceInterpolator());
//        translateAnimator.setRepeatCount(ValueAnimator.INFINITE);
//        translateAnimator.setRepeatMode(ValueAnimator.REVERSE);
//        translateAnimator.start();

        AnimatorSet set = new AnimatorSet();
        AnimatorSet.Builder builder = set.play(colorAnimator1);
        builder.before(translateAnimator);
        set.start();
//        ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.4f, 0.0f, 1.4f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        scaleAnimation.setDuration(700);
//        scaleAnimation.setFillAfter(true);
//        v.startAnimation(scaleAnimation);
        return translateAnimator;
    }

    public void stopAnimation(ValueAnimator a) {
        a.cancel();
    }

    /**
     * 停止播放
     */
    public static void stopSound(SoundPool soundPool, int playID) {
        soundPool.stop(playID);
        soundPool.release();
    }

    /**
     * 开始播放声音，返回声音ID
     *
     * @param soundPool
     * @return
     */
    public static int playSound(SoundPool soundPool) {
        final int[] playID = {0};
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                playID[0] = soundPool.play(1, 1, 1, 0, -1, 1);
            }
        });
        //第一个参数id，即传入池中的顺序，第二个和第三个参数为左右声道，第四个参数为优先级，第五个是否循环播放，0不循环，-1循环
        //最后一个参数播放比率，范围0.5到2，通常为1表示正常播放
//        soundPool.play(1, 1, 1, 0, 0, 1);
        //回收Pool中的资源
        //soundPool.release();
        return playID[0];

    }

    /**
     * 获取SoundPool
     *
     * @param context
     * @param rawId
     * @return
     */
    public SoundPool getSoundPool(Context context, int rawId) {
        SoundPool soundPool;
        if (Build.VERSION.SDK_INT >= 21) {
            SoundPool.Builder builder = new SoundPool.Builder();
            //传入音频的数量
            builder.setMaxStreams(1);
            //AudioAttributes是一个封装音频各种属性的类
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            //设置音频流的合适属性
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            builder.setAudioAttributes(attrBuilder.build());
            soundPool = builder.build();
        } else {
            //第一个参数是可以支持的声音数量，第二个是声音类型，第三个是声音品质
            soundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 5);
        }
        //第一个参数Context,第二个参数资源Id，第三个参数优先级
        soundPool.load(context, rawId, 1);
        return soundPool;
    }
}

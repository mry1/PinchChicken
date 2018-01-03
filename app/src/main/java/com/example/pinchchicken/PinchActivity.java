package com.example.pinchchicken;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.example.pinchchicken.widget.PressButton;
import com.richpath.RichPathView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PinchActivity extends AppCompatActivity {


    private static final int MSG_PROGRESS_UP = 0x110;
    private static final int MSG_PROGRESS_DOWN = 0x120;
    private static final int DELAY_MILLS = 20;

    @BindView(R.id.btn_play)
    PressButton btnPlay;
    @BindView(R.id.pb_org)
    ProgressBar mProgressBar;
    @BindView(R.id.chicken_view1)
    RichPathView mChickenView;
    ChickenAnimate mChickenAnimate;


    private SoundPool soundPool;
    private int playID = 0;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        mChickenAnimate = new ChickenAnimate(this, mChickenView);
        soundPool = SoundUtils.getSoundPool(PinchActivity.this, R.raw.fechick);

        btnPlay.setOnPressListener(new PressButton.OnPressListener() {
            @Override
            public void onPress() {
                playID = SoundUtils.playSound(soundPool);
                mHandler.removeMessages(MSG_PROGRESS_DOWN);
                mHandler.sendEmptyMessage(MSG_PROGRESS_UP);
            }

            @Override
            public void onRelease() {
                SoundUtils.stopSound(soundPool, playID);
                mHandler.removeMessages(MSG_PROGRESS_UP);
                mHandler.sendEmptyMessage(MSG_PROGRESS_DOWN);

            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_PROGRESS_UP:
                    int progress = mProgressBar.getProgress();
                    mChickenAnimate.animate(progress);
                    mProgressBar.setProgress(++progress);
                    if (progress >= 100) {
                        mHandler.removeMessages(MSG_PROGRESS_UP);
                    }
                    mHandler.sendEmptyMessageDelayed(MSG_PROGRESS_UP, DELAY_MILLS);
                    break;
                case MSG_PROGRESS_DOWN:
                    int progress1 = mProgressBar.getProgress();
                    mChickenAnimate.animate(progress1);
                    mProgressBar.setProgress(--progress1);
                    if (progress1 <= 0) {
                        mHandler.removeMessages(MSG_PROGRESS_DOWN);
                    }
                    mHandler.sendEmptyMessageDelayed(MSG_PROGRESS_DOWN, DELAY_MILLS);
                    break;
            }


        }


    };


}

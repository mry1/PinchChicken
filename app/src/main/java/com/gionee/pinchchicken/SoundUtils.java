package com.gionee.pinchchicken;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import java.io.IOException;

/**
 * Created by louis on 17-12-21.
 */

public class SoundUtils {

    /**
     * liminglin 添加变量
     */
    private static final String TAG = "SoundUtils";
    private static SoundPool soundPool;
    private static int[] rawRes;

    /**
     * 播放声音 不能同时播放多种音频
     * 消耗资源较大
     *
     * @param rawId
     */
    public static void playSoundByMedia(Context context, int rawId) {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);
            try {
                AssetFileDescriptor file = context.getResources().openRawResourceFd(
                        rawId);
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(0.50f, 0.50f);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    /**
     * liminglin
     *
     * 单例获取SoundPool
     *
     * @return
     */
    public static SoundPool getSoundPool() {
        if(soundPool == null) {
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
            return soundPool;
        }else{
            return soundPool;
        }
    }

    /**
     * liminglin
     *
     * 初始化音频资源
     */
    public static void initSource(int[] rawIds){
        rawRes = new int[rawIds.length];
        for(int i = 0 ; i < rawIds.length ; i++){
            rawRes[i] = rawIds[i];
        }
    }

    /**
     * liminglin
     *
     * 开始播放声音，返回声音ID
     *
     * @param soundPool
     * @return
     */
    public static int playSound(SoundPool soundPool,Context context,int rawId) {

        //第一个参数Context,第二个参数资源Id，第三个参数优先级
        final int soundId = soundPool.load(context, rawId, 1);
        Log.d(TAG, "playSound: soundId = " + soundId);
        final int[] playID = {0};
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            //第一个参数id，即传入池中的顺序，第二个和第三个参数为左右声道，第四个参数为优先级，第五个是否循环播放，0不循环，-1循环
            //最后一个参数播放比率，范围0.5到2，通常为1表示正常播放
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                playID[0] = soundPool.play(soundId, 1, 1, 0, -1, 1);
            }
        });
        Log.d(TAG, "playSound: playID[0] = " + playID[0]);
        return playID[0];

    }

    /**
     * 停止播放
     */
    public static void stopSound(SoundPool soundPool, int playID) {

        Log.d(TAG, "stopSound: playID + " + playID);
        soundPool.stop(playID);
        //回收Pool中的资源
        soundPool.release();
    }
}

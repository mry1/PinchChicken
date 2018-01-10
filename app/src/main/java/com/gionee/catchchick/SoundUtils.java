package com.gionee.catchchick;

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
    private static int[] soundIDs;

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
     * <p>
     * 单例获取SoundPool
     *
     * @return
     */
    public static SoundPool getSoundPool() {
        if (soundPool == null) {
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
        } else {
            return soundPool;
        }
    }

    /**
     * liminglin
     * <p>
     * 初始化音频资源
     */
    public static void initSource(int[] rawIds) {
        rawRes = new int[rawIds.length];
        for (int i = 0; i < rawIds.length; i++) {
            rawRes[i] = rawIds[i];
        }
    }

    private static void loadSoundPool(SoundPool soundPool, Context context) {
        if (rawRes.length == 0) {
            return;
        }

        for (int i = 0; i < rawRes.length; i++) {
            //第一个参数Context,第二个参数资源Id，第三个参数优先级
            soundIDs[i] = soundPool.load(context, rawRes[i], 1);
            Log.d(TAG, "loadSoundPool: soundIDs[" + i + "] = " + soundIDs[i]);
        }
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                Log.d(TAG, "onLoadComplete: load resources success");
            }
        });
    }

    /**
     * liminglin
     * <p>
     * 开始播放声音，返回声音ID
     *
     * @param soundPool
     * @return
     */
    public static int playSound(SoundPool soundPool, Context context, int playIndex) {

        if (soundIDs.length == 0) {
            loadSoundPool(soundPool, context);
        }
        int[] playID = new int[soundIDs.length];
        //第一个参数id，即传入池中的顺序，第二个和第三个参数为左右声道，第四个参数为优先级，第五个是否循环播放，0不循环，-1循环
        //最后一个参数播放比率，范围0.5到2，通常为1表示正常播放
        switch (playIndex) {
            case 0: // background
                playID[0] = soundPool.play(soundIDs[0],1,1,0,-1,1);
                Log.d(TAG, "playSound: playID[" + 0 + "] = " + playID[0]);
                return playID[0];
            case 1:
                playID[1] = soundPool.play(soundIDs[1],1,1,0,-1,1);
                Log.d(TAG, "playSound: playID[" + 1 + "] = " + playID[1]);
                return playID[1];
            case 2:
                playID[2] = soundPool.play(soundIDs[2],1,1,0,-1,1);
                Log.d(TAG, "playSound: playID[" + 2 + "] = " + playID[2]);
                return playID[2];
            case 3:
                playID[3] = soundPool.play(soundIDs[3],1,1,0,-1,1);
                Log.d(TAG, "playSound: playID[" + 3 + "] = " + playID[3]);
                return playID[3];
            case 4:
                playID[4] = soundPool.play(soundIDs[4],1,1,0,-1,1);
                Log.d(TAG, "playSound: playID[" + 4 + "] = " + playID[4]);
                return playID[4];
            case 5:
                playID[5] = soundPool.play(soundIDs[5],1,1,0,-1,1);
                Log.d(TAG, "playSound: playID[" + 5 + "] = " + playID[5]);
                return playID[5];
            default:
                throw new IndexOutOfBoundsException("资源下标越界!");
        }
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

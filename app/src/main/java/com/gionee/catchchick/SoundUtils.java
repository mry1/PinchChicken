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
    private static SoundPool gameSoundPool;
    private static SoundPool backgroundSoundPool;
    private static int[] rawRes;
    private static int[] soundIDs;
    private static int[] playID;

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
                AssetFileDescriptor file = context.getResources().openRawResourceFd(rawId);
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
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
     * 单例获取背景音乐的资源SoundPool
     * @return
     */
    public static SoundPool getBackgroundSoundPool(){
        if (backgroundSoundPool == null) {
            if (Build.VERSION.SDK_INT >= 21) {
                SoundPool.Builder builder = new SoundPool.Builder();
                //传入音频的数量
                builder.setMaxStreams(1);
                //AudioAttributes是一个封装音频各种属性的类
                AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
                //设置音频流的合适属性
                attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
                builder.setAudioAttributes(attrBuilder.build());
                backgroundSoundPool = builder.build();
            } else {
                //第一个参数是可以支持的声音数量，第二个是声音类型，第三个是声音品质
                backgroundSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 5);
            }
            return backgroundSoundPool;
        } else {
            return backgroundSoundPool;
        }
    }

    /**
     * liminglin
     *
     * 单例获取播放过程中的资源SoundPool
     *
     * @return
     */
    public static SoundPool getGameSoundPool() {
        if (gameSoundPool == null) {
            if (Build.VERSION.SDK_INT >= 21) {
                SoundPool.Builder builder = new SoundPool.Builder();
                //传入音频的数量
                builder.setMaxStreams(1);
                //AudioAttributes是一个封装音频各种属性的类
                AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
                //设置音频流的合适属性
                attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
                builder.setAudioAttributes(attrBuilder.build());
                gameSoundPool = builder.build();
            } else {
                //第一个参数是可以支持的声音数量，第二个是声音类型，第三个是声音品质
                gameSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 5);
            }
            return gameSoundPool;
        } else {
            return gameSoundPool;
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
        soundIDs = new int[rawRes.length];
        playID = new int[rawRes.length];
    }

    /**
     * liminglin
     *
     * 加载背景音乐的 soundpool
     * @param soundPool
     * @param context
     */
    public static void loadBackgroundSoundPool(SoundPool soundPool,Context context){
        if (rawRes.length == 0) {
            return;
        }

        soundIDs[0] = soundPool.load(context,rawRes[0],1);
        Log.d(TAG, "loadGameSoundPool: soundIDs[" + 0 + "] = " + soundIDs[0]);

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                Log.d(TAG, "onLoadComplete: load background resources success");
            }
        });
    }

    /**
     * liminglin
     *
     * 加载游戏音乐的 soundpool
     * @param soundPool
     * @param context
     */
    public static void loadGameSoundPool(SoundPool soundPool, Context context) {
        if (rawRes.length == 0) {
            return;
        }

        for (int i = 1; i < rawRes.length; i++) {
            //第一个参数Context,第二个参数资源Id，第三个参数优先级
            if (soundIDs[i] == 0) {
                soundIDs[i] = soundPool.load(context, rawRes[i], 1);
                Log.d(TAG, "loadGameSoundPool: soundIDs[" + i + "] = " + soundIDs[i]);
            }
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
     *
     * 播放背景音乐
     * @param soundPool
     * @return
     */
    public static int playBackgroundSound(SoundPool soundPool) {
        return playGameSound(soundPool, 1);
    }

    /**
     * liminglin
     * <p>
     * 播放游戏声音
     * @param soundPool
     * @return
     */
    public static int playGameSound(SoundPool soundPool, int playIndex) {

        if (playIndex == 0) {
            throw new IndexOutOfBoundsException("读取资源异常！");
        }

        if (playIndex == 1) {
            playID[playIndex - 1] = soundPool.play(soundIDs[playIndex - 1], 1, 1, 0, -1, 1);
            return playID[playIndex - 1];
        }

        //第一个参数id，即传入池中的顺序，第二个和第三个参数为左右声道，第四个参数为优先级，第五个是否循环播放，0不循环，-1循环
        //最后一个参数播放比率，范围0.5到2，通常为1表示正常播放
        switch (playIndex) {
            case 2:
                playID[playIndex - 1] = soundPool.play(soundIDs[playIndex - 1], 1, 1, 0, 1, 1);
                break;
            case 3:
                playID[playIndex - 1] = soundPool.play(soundIDs[playIndex - 1], 1, 1, 0, 1, 1);
                break;
            case 4:
                playID[playIndex - 1] = soundPool.play(soundIDs[playIndex - 1], 1, 1, 0, 1, 1);
                break;
            case 5:
                playID[playIndex - 1] = soundPool.play(soundIDs[playIndex - 1], 1, 1, 0, 1, 1);
                break;
            case 6:
                playID[playIndex - 1] = soundPool.play(soundIDs[playIndex - 1], 1, 1, 0, 1, 1);
                break;
            default:
                throw new IndexOutOfBoundsException("读取资源异常！");
        }

        Log.d(TAG, "playGameSound: playID[" + (playIndex - 1) + "] = " + playID[playIndex - 1]);
        return playID[playIndex - 1];
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

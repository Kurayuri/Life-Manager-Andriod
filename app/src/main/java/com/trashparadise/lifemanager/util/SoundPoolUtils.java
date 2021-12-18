package com.trashparadise.lifemanager.util;

import android.content.Context;
import android.media.SoundPool;

import com.trashparadise.lifemanager.R;

public class SoundPoolUtils {
    private static SoundPoolUtils soundPoolUtil;
    private SoundPool soundPool;

    //单例模式
    public static SoundPoolUtils getInstance(Context context) {
        if (soundPoolUtil == null)
            soundPoolUtil = new SoundPoolUtils(context);
        return soundPoolUtil;
    }

    private SoundPoolUtils(Context context) {
        soundPool = new SoundPool.Builder().build();
        //加载音频文件
        soundPool.load(context, R.raw.todo, 1);
        soundPool.load(context, R.raw.done, 1);
    }

    public void play(int number) {
        soundPool.play(number, 1, 1, 0, 0, 1);
    }}

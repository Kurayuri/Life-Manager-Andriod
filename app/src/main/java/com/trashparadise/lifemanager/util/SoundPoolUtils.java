package com.trashparadise.lifemanager.util;

import android.content.Context;
import android.media.SoundPool;

import com.trashparadise.lifemanager.R;

public class SoundPoolUtils {
    private static SoundPoolUtils soundPoolUtils;
    private SoundPool soundPool;

    public static SoundPoolUtils getInstance(Context context) {
        if (soundPoolUtils == null)
            soundPoolUtils = new SoundPoolUtils(context);
        return soundPoolUtils;
    }

    private SoundPoolUtils(Context context) {
        soundPool = new SoundPool.Builder().build();
        soundPool.load(context, R.raw.todo, 1);
        soundPool.load(context, R.raw.done, 1);
    }

    public void play(int number) {
        soundPool.play(number, 1, 1, 0, 0, 1);
    }
}

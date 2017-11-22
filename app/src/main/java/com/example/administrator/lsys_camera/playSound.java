package com.example.administrator.lsys_camera;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Created by ssg88 on 2017-05-25.
 */

public class playSound {
    SoundPool soundPool;
    int soundId;
    public void setSound(Context context)
    {

        soundPool= new SoundPool(1, AudioManager.STREAM_MUSIC,0);
        soundId=soundPool.load(context,R.raw.shuttersound,1);

    }

    public void playSound()
    {
        soundPool.play(soundId,1.0f,  1,  -1,  0,1);
    }
}

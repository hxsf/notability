package me.hxsf.notability.until;

import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by hxsf on 15－12－03.
 */
//TODO
public class Player {
    static private MediaPlayer mediaPlayer=null;

    static public void play(String audioPath) {
        audioPath = Environment.getExternalStorageDirectory().getPath() + "/" + audioPath;
//        mediaPlayer.set
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }else{
            mediaPlayer.reset();
        }
        try {
            Log.v("audioPath",audioPath);
            mediaPlayer.setDataSource(audioPath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static public void stop() {
        mediaPlayer.stop();
    }

    static public void pause() {
        mediaPlayer.pause();
    }
}

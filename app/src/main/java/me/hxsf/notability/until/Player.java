package me.hxsf.notability.until;

import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
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
            Log.v("audioPath", audioPath);
//            mediaPlayer.setDataSource(audioPath);
//            TODO  send a message of the path has problem(do not sure it is best function)
            File audioFile=new File(audioPath);
            if(!audioFile.exists()){
                return;
            }else {
                FileInputStream inputStream = new FileInputStream(audioFile);
                mediaPlayer.setDataSource(inputStream.getFD());
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    static public void pause() {
        mediaPlayer.pause();
    }
}

package me.hxsf.notability.until;


import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by hxsf on 15－12－03.
 */
public class Recorder {

    static MediaRecorder mRecorder;

    static public void startRecording(String path, String fileName) {
        String dir = Environment.getExternalStorageDirectory().getPath() + "/" + path;
        File fdir = new File(dir);
        if (!fdir.exists()) {
            fdir.mkdirs();
        }
        String mFileName = dir + "/" + fileName;
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//音频来源
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);//输出格式
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//编码格式
        mRecorder.setOutputFile(mFileName);//输出路径
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder.start();
    }

    static public void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }


}

package me.hxsf.notability.until;


import android.media.MediaRecorder;

import java.io.IOException;

/**
 * Created by hxsf on 15－12－03.
 */
public class Recorder {

    MediaRecorder mRecorder;

    public void startRecording(String fileName) {
        String mFileName = fileName;
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//音频来源
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//输出格式
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//编码格式
        mRecorder.setOutputFile(mFileName);//输出路径
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder.start();
    }

    public void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }


}

package me.hxsf.notability.data;

import android.graphics.Canvas;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * Created by chen on 2015/11/28.
 * Paragraoh 类： 存储记录的一个片段
 *         封装了这个片段的录音，构成这个片段的笔画，画布
 */
public class Paragraph {
    public String audio;
    public ArrayList<Line> lines;
    public boolean hasAudio;
    public Canvas canvas;
    /**
     * @param audio  记录声音
     * @param canvas 画布对象
     * @param hasAudio  判断是否有录音
     * @param lines  笔画集合
     */
    public Paragraph(String audio, Canvas canvas, boolean hasAudio, ArrayList<Line> lines) {
        this.audio = audio;
        this.canvas = canvas;
        this.hasAudio = hasAudio;
        this.lines = lines;
    }

    /**
     * @return
     */
    public boolean draw(){
        return true;
    }

    /**
     * @return
     */
    public boolean hasAudio(){
        if (audio==null)
            hasAudio=false;
        else
            hasAudio=true;
        return hasAudio;
    }
}

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
//    TODO audio的类型？
    public String audio;
    public ArrayList<Line> lines;
    public boolean hasAudio;
    private int nowIndex;

    /**
     * 没有音频的一段
     */
    public Paragraph() {
        lines=new ArrayList<>();
        hasAudio=false;
    }

    /**
     * @param hasAudio  表示有音频
     */
    public Paragraph( boolean hasAudio) {
        lines=new ArrayList<>();
        this.hasAudio=hasAudio;
    }

    /**
     * @param audio  记录声音
     * @param hasAudio  判断是否有录音
     * @param lines  笔画集合
     */
    public Paragraph(String audio, boolean hasAudio, ArrayList<Line> lines) {
        this.audio = audio;
        this.hasAudio = hasAudio;
        this.lines = lines;
        nowIndex=lines.size()-1;
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
    public boolean redo(){
        if (nowIndex<lines.size()){
            lines.get(nowIndex).redo();
            nowIndex++;
            return true;
        }
        return false;
    }
    public boolean undo(){
        if(nowIndex>=0){
            lines.get(nowIndex).undo();
            nowIndex--;
            return true;
        }
        return false;
    }
}

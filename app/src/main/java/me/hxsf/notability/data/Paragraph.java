package me.hxsf.notability.data;

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
        hasAudio = audio != null;
        return hasAudio;
    }
    public  void setLines(int index,Line line){
        lines.set(index,line);
    }
    public void addLine(Line line){
        lines.add(line);
    }
    public ArrayList<Line> getLines(){
        return lines;
    }
    public Line getLine(int index){
        return lines.get(index);
    }

    /*public boolean ic_menu_redo(){
        if (nowIndex<lines.size()){
            lines.get(nowIndex).ic_menu_redo();
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
    }*/
}
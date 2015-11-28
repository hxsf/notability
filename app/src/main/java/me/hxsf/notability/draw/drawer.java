package me.hxsf.notability.draw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.widget.ImageView;

import me.hxsf.notability.data.Line;
import me.hxsf.notability.data.Note;
import me.hxsf.notability.data.Paragraph;
import me.hxsf.notability.data.Pixel;

/**
 * Created by chen on 2015/11/28.
 */
public class drawer {
    public Canvas canvas;
    public  Bitmap bitmap;
    public ImageView imageView;
    public Note note;
    public Paragraph hasAudioParagraph;
    public  Paragraph paragraph;
    public  Line line;
    public Pixel pixel;
    public boolean hasAudio;

    public drawer(Bitmap bitmap, Canvas canvas, ImageView imageView) {
        this.bitmap = bitmap;
        this.canvas = canvas;
        this.imageView = imageView;
    }

    /**
     * 新建一个笔记本:new 一个Note对象和一个没有音频的Paragraph对象
     */
    public void onNewNote(){
        note=new Note();
        paragraph=new Paragraph();
        hasAudio=false;
    }

    /**
     * 点击录音之后，新建一个有音频的Paragraph 对象
     */
    public void onAudioClick(){
        hasAudioParagraph=new Paragraph(true);
        hasAudio=true;
    }

    /**
     * 当按下手指时，开始画
     * @param x  x 坐标
     * @param y  y 坐标
     * @param lastColor  绘制这个点的颜色
     */
    public void onDown(float x,float y,int lastColor){

    }
}

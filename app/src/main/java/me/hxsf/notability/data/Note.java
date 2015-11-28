package me.hxsf.notability.data;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by chen on 2015/11/28.
 * Note 类 ：存储一篇笔记
 *          封装了笔记的 标题，上一次修改的时间（方便 redo）,构成笔记的片段
 */
public class Note {
    private Bitmap minimap;
    private String title;
    private Date lastModified;
    private ArrayList<Paragraph> paragraphs;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");

    public String getTitle() {
        return title;
    }

    public Bitmap getMinimap() {
        return minimap;
    }

    public String getLastModified() {
        return sdf.format(lastModified);
    }

    public Note(Date lastModified, ArrayList<Paragraph> paragraphs, String title) {
        this.lastModified = lastModified;
        this.paragraphs = paragraphs;
        this.title = title;
    }

    /**
     * @param paragraph
     * @return
     */
    public boolean addParagraph(Paragraph paragraph){
        return paragraphs.add(paragraph);
    }

    /**
     * @param index
     * @return
     */
    public Paragraph getParagraph(int index){
        return paragraphs.get(index);
    }

    /**
     * @return
     */
    public Paragraph getParagraph(){
        return paragraphs.get(paragraphs.size() - 1);
    }
    public boolean removeParagraph(int index){
        //TODO 不确定 return 什么值
        paragraphs.remove(index);
        return true;
    }

    /**
     * 判断整个笔记是否有音频
     * @return
     */
    public boolean hasAudio(){
        for (int i=0;i<paragraphs.size()-1;i++)
            if (paragraphs.get(i).hasAudio)
                return true;
        return false;
    }

    /**
     * 修改标题
     * @param title 要修改的标题名
     */
    public void changeTitle(String title){
        this.title=title;
    }

    /**
     * 修改某一段的内容
     * @param index  要被替换的内容的位置
     * @param paragraph  要修改的内容
     */
    public void chnageParagraph(int index,Paragraph paragraph){
        paragraphs.set(index, paragraph);
    }
}

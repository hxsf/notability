package me.hxsf.notability.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by chen on 2015/11/28.
 * Note 类 ：存储一篇笔记
 *          封装了笔记的 标题，上一次修改的时间（方便 ic_menu_redo）,构成笔记的片段
 */
public class Note {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    public String title;
    public Date lastModified;
    //    TODO paragraph 类可以删除
    public ArrayList<Paragraph> paragraphs;
    public ArrayList<Line> lines;

    public Note() {
        title="未命名";
        paragraphs =new ArrayList<>();
    }

    public Note(Date lastModified, ArrayList<Paragraph> paragraphs, String title) {
        this.lastModified = lastModified;
        this.paragraphs = paragraphs;
        this.title = title;
    }

    /**
     * addParagraph：添加一个片段
     * @param paragraph  要添加的片段的内容
     * @return
     */
    public boolean addParagraph(Paragraph paragraph){
        return paragraphs.add(paragraph);
    }

    /**
     * getParagraph：获取片段
     * @param index 要获取的片段下标
     * @return
     */
    public Paragraph getParagraph(int index){
        return paragraphs.get(index);
    }

    /**
     * 重载：getParagraph：获取最后一个片段
     * @return
     */
    public Paragraph getParagraph(){
        return paragraphs.get(paragraphs.size() - 1);
    }

    public int getParagraphSize(){
        return paragraphs.size();
    }

    public boolean removeParagraph(int index){
        //TODO 不确定 return 什么值
        paragraphs.remove(index);
        return true;
    }

    /**
     * hasAudio：判断整个笔记是否有音频
     * @return
     */
    public boolean hasAudio(){
        for (int i=0;i<paragraphs.size()-1;i++)
            if (paragraphs.get(i).hasAudio)
                return true;
        return false;
    }

    /**
     * changeTitle：修改标题
     * @param title 要修改的标题名
     */
    public void changeTitle(String title){
        this.title=title;
    }

    /**
     * chnageParagraph：修改某一段的内容
     * @param index  要被替换的内容的位置
     * @param paragraph  要修改的内容
     */
    public void setParagraph(int index,Paragraph paragraph){
        paragraphs.set(index, paragraph);
    }

    public String getLastModified() {
        return sdf.format(lastModified);
    }

    public String getTitle() {
        return title;
    }
}

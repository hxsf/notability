package me.hxsf.notability.data;

import java.util.ArrayList;

/**
 * Created by chen on 2015/11/28.
 * Collection  类：存储标签名相同笔记
 *          封装了标签名，文件名，所有有相同标签的笔记
 */
public class Collection {
    public String title;
    public String tag;
    public ArrayList<Note> notes;

    /**
     * @param notes 记录内容的集合
     * @param tag  笔记类型
     * @param title  分类的标题
     */
    public Collection(ArrayList<Note> notes, String tag, String title) {
        this.notes = notes;
        this.tag = tag;
        this.title = title;
    }
    public Note getNote(int index){
        return  notes.get(index);
    }

    /**
     * 判断整篇笔记中有无录音
     * @return
     */
    public boolean hasAudio(){
        for (int i=0;i<notes.size()-1;i++)
            if(notes.get(i).hasAudio())
                return true;
        return false;
    }
    public boolean removeNote(int index){
        //TODO 与 Note 类中的 removeParagraph 函数一样不清楚函数类型应定义成什么
        int length=notes.size();
        notes.remove(index);
        if (length>notes.size())
            return true;
        return false;
    }

    /**
     * changTitle ：修改分类的名字
     * @param title 要修改的名字
     */
    public  void changTitle(String title){
        this.title=title;
    }

    /**
     * changeNote：更换笔记
     * @param index 被更新的笔记的位置
     * @param note 要更换的笔记
     */
    public void changeNote(int index,Note note){
        notes.set(index,note);
    }

    /**
     * changeTag：修改分类
     * @param tag  要替换的标签名
     */
    public  void changeTag(String tag){
        this.tag=tag;
    }
}
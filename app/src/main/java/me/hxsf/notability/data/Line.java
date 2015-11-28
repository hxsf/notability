package me.hxsf.notability.data;

import android.graphics.Canvas;

import java.util.ArrayList;

/**
 * Created by chen on 2015/11/28.
 * Line 类：存储笔画
 *      封装了笔画的颜色，构成这个笔画的所有像素点，画布
 */
public class Line {
    public int color;
    public ArrayList<Pixel> pixels;
    private Canvas canvas;

    /**
     * @param color  绘制线条的颜色
     * @param canvas  绘制面板
     * @param pixels  绘线的像素点集合
     */
    public Line(int color, Canvas canvas, ArrayList<Pixel> pixels) {
        this.color = color;
        this.canvas = canvas;
        this.pixels = pixels;
    }

    /**

     * @return
     */
    public boolean redo(){

        return false;
    }
    public boolean undo(){
        return false;
    }
}

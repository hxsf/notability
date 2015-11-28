package me.hxsf.notability.data;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

/**
 * Created by chen on 2015/11/28.
 * Line 类：存储笔画
 *      封装了笔画的颜色，构成这个笔画的所有像素点，画布
 */
public class Line {
    private int color;
    private ArrayList<Pixel> pixels;
    private Canvas canvas;
    private Paint paint=new Paint();//画笔
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

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public ArrayList<Pixel> getPixels() {
        return pixels;
    }

    public void setPixels(ArrayList<Pixel> pixels) {
        this.pixels = pixels;
    }

    public void redo(){
        paint.setColor(color);
        for (int i=0;i<pixels.size()-1;i++){
            canvas.drawLine(pixels.get(i).getX(),pixels.get(i).getY(),pixels.get(i+1).getX(),pixels.get(i+1).getY(),paint);
        }
    }
    public void undo(){
        for (int i=0;i<pixels.size()-1;i++){
            paint.setColor(pixels.get(i).getLastcolor());
            canvas.drawLine(pixels.get(i).getX(),pixels.get(i).getY(),pixels.get(i+1).getX(),pixels.get(i+1).getY(),paint);
        }
    }
}

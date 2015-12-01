package me.hxsf.notability.data;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by chen on 2015/11/28.
 * Line 类：存储笔画
 *      封装了笔触的颜色，粗度，构成这个笔画的所有像素点，画布
 */
public class Line {
    Bitmap lastArea;
    private int color;
    private float penSize;
    private ArrayList<Pixel> pixels;
    public  Line(int color,float penSize){
        pixels=new ArrayList<>();
        this.color=color;
        this.penSize=penSize;
    }
//    TODO 以 bitmap 存一块像素点
    public void setLastArea(float startX,float startY){
        lastArea=Bitmap.createBitmap((int)(startX+(penSize+1)/2),(int)(startY+(penSize+1)/2), Bitmap.Config.ARGB_8888);
    }
    public  void setLastArea(float startX,float startY,float endX,float endY){
        lastArea=Bitmap.createBitmap((int)(endX-startX+penSize),(int)(endY-startY+penSize), Bitmap.Config.ARGB_8888);
    }
    public float getPenSize() {
        return penSize;
    }

    public void setPenSize(float penSize) {
        this.penSize = penSize;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public ArrayList<Pixel> getPixels() {
        return pixels;
    }

    public void addPixel(Pixel pixels) {
        this.pixels.add(pixels);
    }

   /* public void redo(){
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
    }*/
}
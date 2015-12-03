package me.hxsf.notability.data;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by chen on 2015/11/28.
 * Line 类：存储笔画
 *      封装了笔触的颜色，粗度，构成这个笔画的所有像素点，画布
 */
public class Line {
    public boolean hasAudio;
    private int color;
    private  float penSize;
    private ArrayList<Pixel> pixels;
    private ArrayList<Bitmap> bitmaps;

    public  Line(int color,float penSize){
        pixels=new ArrayList<>();
        this.color=color;
        this.penSize=penSize;
        bitmaps = new ArrayList<>();
        hasAudio = false;
    }

    /**
     * @param bitmap 原画布
     * @param startX 起始点横坐标
     * @param startY 起始点纵坐标
     * @param endX   终止点横坐标
     * @param endY   终止点纵坐标
     */
    public Bitmap setBitmap(Bitmap bitmap, float startX, float startY, float endX, float endY) {
        int x = (int) (startX + penSize / 2);//算上笔触宽度之后的起始点横坐标
        int y = (int) (startY - penSize / 2);//算上笔触宽度之后的起始点纵坐标
        int width, height;
        if ((endX - startX) > 0)
            width = (int) (endX - startX + penSize);//算上笔触宽度之后需要截取位置的宽度
        else
            width = (int) (startX - endX + penSize);
        if ((endY - startY) > 0)
            height = (int) (endY - startY + penSize);//算上笔触宽度之后需要截取位置的高度
        else
            height = (int) (startY - endY + penSize);

        Log.v("test", "x:" + x + "   y=" + y + "   w=" + width + "   H=" + height);
        return Bitmap.createBitmap(bitmap, x, y, width, height);
    }

    public ArrayList<Bitmap> getBitmaps() {
        return bitmaps;
    }

    public Bitmap getBitmap(int index) {
        return bitmaps.get(index);
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

    public Pixel getPixel(int index) {
        return pixels.get(index);
    }

    public void addPixel(Pixel pixels) {
        this.pixels.add(pixels);
    }

   /* public void ic_menu_redo(){
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
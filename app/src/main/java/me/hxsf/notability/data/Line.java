package me.hxsf.notability.data;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by chen on 2015/11/28.
 * Line 类：存储笔画
 *      封装了笔触的颜色，粗度，构成这个笔画的所有像素点，画布
 */
public class Line  implements Serializable {
    private static final long serialVersionUID = 3L;
    public boolean hasAudio;
    private int color;
    private  float penSize;
    private ArrayList<Pixel> pixels;

    public  Line(int color,float penSize){
        pixels=new ArrayList<>();
        this.color=color;
        this.penSize=penSize;
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
        float x,y, width,height;
        x=((endX>startX)?startX:endX)-penSize/2;
        y=((endY>startY)?startY:endY)-penSize/2;
        width=((endX>startX)?(endX-startX):(startX-endX))+penSize;
        height=((endY>startY)?(endY-startY):(startY-endY))+penSize;
        Log.v("test", "x:" + x + "   y=" + y + "   w=" + width + "   H=" + height);
        return Bitmap.createBitmap(bitmap,(int)x, (int)y, (int)width, (int)height);
    }


    public Bitmap addNowBitmap(Bitmap bitmap,int width,int height){
        return Bitmap.createBitmap(bitmap,0,0,width,height);
    }
    /*public  Bitmap getNowBitmap(){
        return nowBitmap;
    }*/
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

}
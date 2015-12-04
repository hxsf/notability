package me.hxsf.notability.data;

import android.graphics.Bitmap;

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

    public Bitmap addNowBitmap(Bitmap bitmap,int width,int height){
        return Bitmap.createBitmap(bitmap,0,0,width,height);
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
        if (index < pixels.size()) {
            return pixels.get(index);
        } else {
            return null;
        }
    }

    public void addPixel(Pixel pixels) {
        this.pixels.add(pixels);
    }

}
package me.hxsf.notability.data;

/**
 * Created by chen on 2015/11/28.
 * Pixel 类:用于封装像素点和绘制该像素点的时间戳以及绘制该像素点之前该点的颜色
 */
public class Pixel {
    private float x;
    private float y;
    private long timestamp;
    private int lastcolor;

    /**
     * @param lastcolor  记录绘制某一像素点之前该像素点的颜色
     * @param x   表示像素点的 x 坐标
     * @param y  表示像素点的 y 坐标
     */
    public Pixel( float x, float y,int lastcolor) {
        this.lastcolor = lastcolor;
        this.x = x;
        this.y = y;
    }
    /**
     * @param lastcolor  记录绘制某一像素点之前该像素点的颜色
     * @param timestamp  记录绘制该像素点的时间戳
     * @param x  表示像素点的 x 坐标
     * @param y  表示像素点的 y 坐标
     */
    public Pixel(int lastcolor, long timestamp, float x, float y) {
        this.lastcolor = lastcolor;
        this.timestamp = timestamp;
        this.x = x;
        this.y = y;
    }

    public int getLastcolor() {
        return lastcolor;
    }

    public void setLastcolor(int lastcolor) {
        this.lastcolor = lastcolor;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}

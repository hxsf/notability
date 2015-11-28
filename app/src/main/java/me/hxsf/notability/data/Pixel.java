package me.hxsf.notability.data;

/**
 * Created by chen on 2015/11/28.
 * Pixel 类:用于封装像素点和绘制该像素点的时间戳以及绘制该像素点之前该点的颜色
 */
public class Pixel {
    public float x;//表示像素点的 x 坐标
    public float y;//表示像素点的 y 坐标
    public long timestamp;//记录绘制该像素点的时间戳
    public int lastcolor;//记录绘制某一像素点之前该像素点的颜色

}

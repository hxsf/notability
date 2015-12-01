package me.hxsf.notability.draw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.widget.ImageView;

import java.util.concurrent.LinkedBlockingQueue;

import me.hxsf.notability.data.Line;
import me.hxsf.notability.data.Note;
import me.hxsf.notability.data.Paragraph;
import me.hxsf.notability.data.Pixel;

/**
 * Created by chen on 2015/11/28.
 */
public class Drawer {
    private static final int Length = 100;
    Path path = new Path();
    Pixel startPixel;
    private Canvas canvas;  //画布
    private  Bitmap bitmap; //位图
    private Paint paint;//笔触
    //    private ImageView imageView; //画板
    private Note note; //存储笔记
    private  Paragraph paragraph; //存储段落
    private  Line line; //存储笔画
    private boolean hasAudio; //判断是否有录音，true 时有
    private int width;//imageview 画板的宽度
    private int height;//imageview 画板的高度
    private float  lastxX,lastY;//上一个点的坐标
    private int paragraphIndex,totalParagraphIndex;//记录note对象中paragraph 数组中的元素个数
    private int lineIndex,totalLineIndex;//记录paragraph对象中line数组中的元素个数
    private int font, rear;//队列指针
    private LinkedBlockingQueue<Pixel> queue;

    /**
     * @param imageView  画板
     * @param color  笔触颜色
     * @param penSize  笔触粗细
     */
    public Drawer(ImageView imageView, int color, float penSize) {
//        this.imageView = imageView;
        width=imageView.getWidth();
        height=imageView.getHeight();
        //初始化 bitmap 对象，将其的宽高设为imageView 的8倍，用于解决imageView 的放大问题，现在最多放大8倍
        bitmap = Bitmap.createBitmap(width * 1, height * 1, Bitmap.Config.ARGB_8888);
        //初始化canvas 对象，canvas 对象宽度与bitmap一致
        canvas=new Canvas(bitmap);
        //初始化画笔
        setPaint(color, penSize);
        //初始化指针
        paragraphIndex = totalParagraphIndex = 0;
        lineIndex = totalLineIndex = -1;
        //初始化队列信息
        queue = new LinkedBlockingQueue<Pixel>(Length);
        font = rear = -1;
    }

    /**
     * 初始化画笔
     * @param color 颜色
     * @param penSize 画笔粗细
     */
    private void setPaint(int color, float penSize) {
        paint = new Paint();
        paint.setColor(color);//设置笔触的颜色和宽度
        paint.setStrokeWidth(penSize);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);//抗锯齿
        paint.setDither(true);//防抖动
        paint.setFilterBitmap(true);
        paint.setSubpixelText(true);
    }

    /**
     * 改变笔触颜色
     * @param color 笔触颜色
     *
     */
    public void changePaint(int color) {
        paint.setColor(color);
    }

    /**
     * 改变笔触宽度
     *
     * @param penSize 笔触粗细
     */
    public void changePaint(float penSize) {
        paint.setStrokeWidth(penSize);
    }

    /**
     * 改变image画布的大小——>imageView放大或缩小时
     * @param width 宽度
     * @param height 高度
     */
    public  void resize(int width,int height){
        this.width=width;
        this.height=height;
    }

    /**
     * 新建一个笔记本:new 一个Note对象和一个没有音频的Paragraph对象
     */
    public void onNewNote(){
        note=new Note();
        paragraph=new Paragraph();
        hasAudio=false;
    }

    /**
     * 点击录音：将上一个无声音的Paragraph 对象添加到note对象中，新建一个有音频的Paragraph 对象
     * TODO 获取声音
     */
    public void onAudioClick() {
        if (paragraph != null) {
            note.addParagraph(paragraph);//将上一段没有音频的笔记添加到note对象中
        }
        totalParagraphIndex = paragraphIndex = note.getParagraphSize() - 1;//获取note中paragraph数组的最新长度
        paragraph=new Paragraph(true);//创建一个新的、有音频的paragraph 对象
        hasAudio=true;
    }

    /**
     * 关闭录音，将上一个有声音的Paragraph 对象添加到note对象中，新建一个无音频的Paragraph 对象
     */
    public void onAudioClose(){
        note.addParagraph(paragraph);//将上一段有音频的笔记添加到note对象中
        paragraphIndex = totalParagraphIndex = note.getParagraphSize() - 1;//获取note中paragraph数组的最新长度
        paragraph=new Paragraph();//创建一个新的、无音频的paragraph 对象
        hasAudio=false;
    }

    /**
     * 当手指按下时，开始写，新建一个 Line 对象
     * @param x   起始点的 x 坐标
     * @param y   起始点的 y 坐标
     */
    public void drawStart(float x,float y){
        line=new Line(paint.getColor(),paint.getStrokeWidth());//初始化line 对象
//        int preColor=bitmap.getPixel((int) x,(int)y);//获取绘制之前（x，y）点的颜色
        startPixel = new Pixel(x, y);
        startPixel.setIsNewLine(true);
        line.addPixel(startPixel);//将像素点添加到line中
        try {
            queue.put(startPixel);
        } catch (InterruptedException e){

        }
        lastxX=x; //起始点
        lastY=y;
    }
    /**
     * 当手指开始移动时，开始画
     * @param x  x 坐标
     * @param y  y 坐标
     */
    public void drawing(float x, float y) {

        //TODO lastcolor 是否需要保留
//        int lastColor=bitmap.getPixel((int) x,(int)y);//获取绘制之前（x，y）点的颜色
//        line.addPixel(new Pixel(x, y, lastColor)); //初始化一个新的像素点对象并添加到 line 对象中
        Pixel pixel = new Pixel(x, y);
        line.addPixel(pixel); //初始化一个新的像素点对象并添加到 line 对象中
        //添加到队列中
        try {
            queue.put(pixel);
        } catch (InterruptedException e) {
        }
        Bitmap temp = line.setBitmap(bitmap, lastxX, lastY, x, y);//截取画线之前的画布，用于undo
        line.addBitmap(temp);//添加
        Log.v("Drawer drawing", "drawing");
        lastxX=x;//更新起始点
        lastY=y;
    }

    /**
     * 笔画结束，手指抬起,将line 对象添加到paragraph 对象中
     */
    public void drawEnd(){
        //当没有撤销的操作需要恢复——>正常添加
        if (totalLineIndex==lineIndex) {
            paragraph.addLine(line);//将line 对象添加到paragraph 对象中
            lineIndex = totalLineIndex = paragraph.getLines().size() - 1;//获取paragraph中line数组的最新长度
            Log.v("Drawer drawEnd", "normal add，paragraph.size=" + paragraph.getLines().size());
        }
        //有可以恢复的操作——>撤销某些操作后重新输入
        else {
            paragraph.setLines(++lineIndex, line);//将line 对象添加到paragraph 对象中
            totalLineIndex = lineIndex;//将指针移动到最新更新处，即：不能再恢复回之前撤下的东西
            Log.v("Drawer drawEnd","set");
        }
    }

    public void draw(Canvas canvas) {
        float startX = 0, startY = 0;
        while (true) {
            try {
                startPixel = queue.take();
            } catch (InterruptedException e) {
                break;
            }
            if (startPixel.isNewLine()) {
                startX = startPixel.getX();
                startY = startPixel.getY();
                path.moveTo(startX, startY);
            } else {
                path.quadTo(startX, startY, startPixel.getX(), startPixel.getY());
            }
            canvas.drawPath(path, paint);
            Log.v("Drawer.draw", "  SX：" + startX + "  SY:" + startY);
            path.reset();
        }
    }

    /**
     * 向前，恢复操作
     */
    public boolean redo() {
        if (paragraphIndex <= totalLineIndex) {
            if (lineIndex < totalLineIndex) {
                Line line = note.getParagraph(paragraphIndex).getLine(++lineIndex);
                for (int i = line.getBitmaps().size() - 1; i > 0; i--) {
                    float endX = line.getPixels().get(i).getX();//获取当前点的坐标
                    float endY = line.getPixels().get(i).getY();
                    float startX = line.getPixels().get(i - 1).getX();//获取上一点的坐标
                    float startY = line.getPixels().get(i - 1).getY();
                    Bitmap temp = line.setBitmap(bitmap, startX, startY, endX, endY); //截取当前画布内容，为undo 准备
                    canvas.setBitmap(line.getBitmap(i));//重绘
                    line.getBitmaps().set(i, temp);//将被重绘部分的内容保存，以供undo 使用
                }
            } else {
                if ((paragraphIndex + 1) > totalParagraphIndex) {
                    return false;
                } else {
                    paragraphIndex++;
                    totalLineIndex = note.getParagraph(paragraphIndex).getLines().size();
                    lineIndex = -1;
                    redo();
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 向后，撤销操作
     */
    public boolean undo(){
        if(paragraphIndex>=0){//表示还有段落可以撤销
                if(lineIndex>=0) {//表示还有笔画可以撤销
//                    将这个笔画所在的方形区域用画之前的方形区域的颜色覆盖
                    Line line = note.getParagraph(paragraphIndex).getLine(lineIndex);
                    for (int i = line.getBitmaps().size() - 1; i > 0; i--) {
                        float endX = line.getPixels().get(i).getX();//获取当前点的坐标
                        float endY = line.getPixels().get(i).getY();
                        float startX = line.getPixels().get(i - 1).getX();//获取上一点的坐标
                        float startY = line.getPixels().get(i - 1).getY();
                        Bitmap temp = line.setBitmap(bitmap, startX, startY, endX, endY); //截取当前画布内容，为redo 准备
                        canvas.setBitmap(line.getBitmap(i));//重绘
                        line.getBitmaps().set(i, temp);//将被重绘部分的内容保存，以供redo 使用

                        Log.v("Drawer undo", "after undo");
                        Log.v("drawEnd", "after undo，line.size=" + paragraph.getLines().size() + "lineIndex"+lineIndex);
                    }
                    lineIndex--;
                } else {
                    paragraphIndex--;
                    totalLineIndex = lineIndex = note.getParagraph(paragraphIndex).getLines().size() - 1;
                    undo();
                }
            return true;
        } else
            Log.v("Drawer undo", "no more to undo");
            return false;
    }
}

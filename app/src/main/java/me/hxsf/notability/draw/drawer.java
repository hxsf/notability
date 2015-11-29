package me.hxsf.notability.draw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.ImageView;

import java.util.ArrayList;

import me.hxsf.notability.data.Line;
import me.hxsf.notability.data.Note;
import me.hxsf.notability.data.Paragraph;
import me.hxsf.notability.data.Pixel;

/**
 * Created by chen on 2015/11/28.
 */
public class drawer {
    private Canvas canvas;  //画布
    private  Bitmap bitmap; //位图
    private Paint paint;//笔触
    private ImageView imageView; //画板
    private Note note; //存储笔记
    private  Paragraph paragraph; //存储段落
    private  Line line; //存储笔画
    private boolean hasAudio; //判断是否有录音，true 时有
    private int width;//imageview 画板的宽度
    private int height;//imageview 画板的高度
    private float  lastxX,lastY;//上一个点的坐标
    private int paragraphIndex,totalParagraphIndex;//记录note对象中paragraph 数组中的元素个数
    private int lineIndex,totalLineIndex;//记录paragraph对象中line数组中的元素个数

    /**
     * @param imageView  画板
     * @param color  笔触颜色
     * @param penSize  笔触粗细
     */
    public drawer( ImageView imageView,int color,float penSize) {
        this.imageView = imageView;
        width=imageView.getWidth();
        height=imageView.getHeight();
        //初始化 bitmap 对象，将其的宽高设为imageView 的8倍，用于解决imageView 的放大问题，现在最多放大8倍
        bitmap=Bitmap.createBitmap(width * 8, height * 8, Bitmap.Config.ARGB_8888);
        //初始化canvas 对象，canvas 对象宽度与bitmap一致
        canvas=new Canvas(bitmap);
        //初始化笔触
        paint=new Paint();
        paint.setColor(color);//设置笔触的颜色和宽度
        paint.setStrokeWidth(penSize);
        //初始化指针
        paragraphIndex=totalParagraphIndex=lineIndex=totalLineIndex=0;
    }

    /**
     * 改变笔触颜色和宽度
     * @param color 笔触颜色
     * @param penSize  笔触粗细
     */
    public void changePaint(int color,float penSize){
        paint.setColor(color);
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
    public void onAudioClick(){
        note.addParagraph(paragraph);//将上一段没有音频的笔记添加到note对象中
        totalParagraphIndex=note.getParagraphSize();//获取note中paragraph数组的最新长度
        paragraph=new Paragraph(true);//创建一个新的、有音频的paragraph 对象
        hasAudio=true;
    }

    /**
     * 关闭录音，将上一个有声音的Paragraph 对象添加到note对象中，新建一个无音频的Paragraph 对象
     */
    public void onAudioClose(){
        note.addParagraph(paragraph);//将上一段有音频的笔记添加到note对象中
        paragraphIndex=totalParagraphIndex=note.getParagraphSize();//获取note中paragraph数组的最新长度
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
        int preColor=bitmap.getPixel((int) x,(int)y);//获取绘制之前（x，y）点的颜色
        line.addPixel(new Pixel(x, y, preColor));//将像素点添加到line中
        lastxX=x; //起始点
        lastY=y;
    }
    /**
     * 当手指开始移动时，开始画
     * @param x  x 坐标
     * @param y  y 坐标
     */
    public void drawing(float x,float y){
        int lastColor=bitmap.getPixel((int) x,(int)y);//获取绘制之前（x，y）点的颜色
        line.addPixel(new Pixel(x, y, lastColor)); //初始化一个新的像素点对象并添加到 line 对象中
        canvas.drawLine(lastxX, lastY, x, y, paint);//画线
        lastxX=x;//更新起始点
        lastY=y;
    }

    /**
     * 笔画结束，手指抬起,将line 对象添加到paragraph 对象中
     */
    public void drawEnd(){
        //当没有撤销的操作需要恢复——>正常添加
        if(totalLineIndex==paragraph.getLineSize()) {
            paragraph.addLine(line);//将line 对象添加到paragraph 对象中
            lineIndex=totalLineIndex = paragraph.getLineSize();//获取paragraph中line数组的最新长度
        }
        //有可以恢复的操作——>撤销某些操作后重新输入
        else{
            paragraph.setLines(lineIndex,line);//将line 对象添加到paragraph 对象中
            totalLineIndex=++lineIndex;//将指针移动到最新更新处，即：不能再恢复回之前撤下的东西
        }
    }

    /**
     * 向前，恢复操作
     */
    public boolean redo(){
        return false;
    }

    /**
     * 向后，撤销操作
     */
    public boolean undo(){
        if(paragraphIndex>=0){//表示还有段落可以撤销
                if(lineIndex>=0){//表示还有笔画可以撤销
                    //设置重绘时笔触的颜色和粗度
                    paint.setColor(note.getParagraph(paragraphIndex).getLine(lineIndex).getColor());
                    paint.setStrokeWidth(note.getParagraph(paragraphIndex).getLine(lineIndex).getPenSize());
                    //获取这一笔画的所有像素点
                    ArrayList<Pixel> pixels= note.getParagraph(paragraphIndex).getLine(lineIndex).getPixels();

                    //撤销这一笔(每个像素点用绘制那个像素点之前的颜色重绘一次)

                    for (int i=0;i<pixels.size();i++){
                        canvas.drawLine(pixels.get(i).getX(),pixels.get(i).getY(),pixels.get(i+1).getX(),pixels.get(i+1).getY(),paint);
                    }
                }
            return true;
        }else
            return false;
    }
}

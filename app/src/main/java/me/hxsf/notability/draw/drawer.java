package me.hxsf.notability.draw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import me.hxsf.notability.data.Line;
import me.hxsf.notability.data.Note;
import me.hxsf.notability.data.Paragraph;
import me.hxsf.notability.data.Pixel;

/**
 * Created by chen on 2015/11/28.
 */
public class Drawer {
    static Drawer drawer;
    Path path = new Path();
    private Bitmap bitmap; //位图
    private Canvas canvas;//TODO 是否需要定义canvas变量
    private Paint paint;//笔触
    private ImageView imageView; //画板
    private Note note; //存储笔记
    private Paragraph paragraph; //存储段落
    private Line line; //存储笔画
    private boolean hasAudio; //判断是否有录音，true 时有
    private int paragraphIndex,totalParagraphIndex;//记录note对象中paragraph 数组中的元素个数
    private int lineIndex,totalLineIndex;//记录paragraph对象中line数组中的元素个数


    private Queue<Bitmap> queue;
    private int front, rear, count;


    private List<Bitmap> stack;
    private List<Line> redoStack;

    /**
     //     * @param imageView  画板
     * @param color  笔触颜色
     * @param penSize  笔触粗细
     */
    public Drawer(ImageView imageView, int color, float penSize) {
        //初始化画笔
        setPaint(color, penSize);
        //初始化指针
        paragraphIndex = totalParagraphIndex = -1;
        lineIndex = totalLineIndex = -1;
        this.imageView = imageView;
        bitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
        Log.v("w&h", imageView.getWidth() + ",  " + imageView.getHeight());
        canvas=new Canvas(bitmap);


        queue = new LinkedList<>();
        count = 0;
        front = rear = 0;

        stack = new ArrayList<>();
        redoStack = new ArrayList<>();
    }

    public static Drawer getDrawer(ImageView imageView, int color, float penSize) {
        if (drawer == null) {
            drawer = new Drawer(imageView, color, penSize);
        }
        return drawer;
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
     * 新建一个笔记本:new 一个Note对象和一个没有音频的Paragraph对象
     */
    public void onNewNote(){
        note=new Note();
        paragraph=new Paragraph();
        line = new Line(paint.getColor(), paint.getStrokeWidth());//初始化line 对象
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
        totalParagraphIndex = paragraphIndex = note.getParagraphSize();//获取note中paragraph数组的最新长度
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
     * 当手指开始移动时，开始画
     * @param x  x 坐标
     * @param y  y 坐标
     */
    public void drawing(float x, float y) {
        Pixel pixel = new Pixel(x, y);
        line.addPixel(pixel); //初始化一个新的像素点对象并添加到 line 对象中
    }

    /**
     * 笔画结束，手指抬起,将line 对象添加到paragraph 对象中
     */
    public void drawEnd(){

        //添加bitmap 快照
        if (count < 5) {//队未满
            stack.add(line.addNowBitmap(bitmap, imageView.getWidth(), imageView.getHeight()));
            rear++;
            count++;
        } else {//队满，循环
            stack.set((rear) % 5, line.addNowBitmap(bitmap, imageView.getWidth(), imageView.getHeight()));
            rear = (rear + 1) % 5;
        }
        paragraph.addLine(line);
        line = new Line(paint.getColor(), paint.getStrokeWidth());//初始化一个新的line 对象
    }

    public void draw(BaseLine bl) {
        if (bl.isstart) {
            path.reset();
            drawEnd();//将上一条线插入到数组中
            path.moveTo(bl.x1, bl.y1);
        }
        drawing(bl.x1, bl.y1);
//        drawing(bl.x2, bl.y2);
        path.quadTo(bl.x1, bl.y1, (bl.x1 + bl.x2) / 2, (bl.y1 + bl.y2) / 2);
//        Log.v("path",bl.toString() );
        canvas.drawPath(path, paint);
        imageView.setImageBitmap(bitmap);
    }

    public void draw(BaseLine bl, long time) {
        if (bl.isstart) {
            path.reset();
            drawEnd();//将上一条线插入到数组中
            path.moveTo(bl.x1, bl.y1);
        }
        drawing(bl.x1, bl.y1);
//        drawing(bl.x2, bl.y2);
        path.quadTo(bl.x1, bl.y1, (bl.x1 + bl.x2) / 2, (bl.y1 + bl.y2) / 2);
//        Log.v("path",bl.toString() );
        canvas.drawPath(path, paint);
        imageView.setImageBitmap(bitmap);
    }

    /**
     * 向前，恢复操作
     */
    public void redo() {
        if (!redoStack.isEmpty()) {
            line = redoStack.get(redoStack.size() - 1);
            path.reset();
            path.moveTo(line.getPixel(0).getX(), line.getPixel(0).getY());
            for (int i = 1; i < line.getPixels().size(); i++) {
                Log.v("Redo", " " + i);
                Log.v("Redo,size", " " + line.getPixels().size());
                path.quadTo(line.getPixel(i - 1).getX(), line.getPixel(i - 1).getY(),
                        (line.getPixel(i - 1).getX() + line.getPixel(i).getX()) / 2, (line.getPixel(i - 1).getY() + line.getPixel(i).getY()) / 2);

                canvas.drawPath(path, paint);

                imageView.setImageBitmap(bitmap);
            }
            drawEnd();

               /*

                if(i==1)
                    baseLine=new BaseLine(true,line.getPixel(i-1).getX(),line.getPixel(i-1).getY(),
                                                line.getPixel(i).getX(),  line.getPixel(i).getY());
                else
                    baseLine=new BaseLine(false,line.getPixel(i-1).getX(),line.getPixel(i-1).getY(),
                                                 line.getPixel(i).getX(),line.getPixel(i).getY());

                Log.v("BaseLine",baseLine.toString());
                draw(baseLine);*/


            redoStack.remove(redoStack.size() - 1);
        }
    }
    /**
     * 向后，撤销操作
     */
    public void undo() {
        if (count != 0) {//队未空
            bitmap = stack.get((--rear + 5) % 5);//获取上一笔
            rear = (5 + rear) % 5;//后移
            count--;
            //将最后一个line对象取出，放入redoStack 栈中
            if (!paragraph.getLines().isEmpty()) {
                redoStack.add(paragraph.getLine(paragraph.getLines().size() - 1));
                paragraph.getLines().remove(paragraph.getLines().size() - 1);//清除这条line对象
            } else {
                totalParagraphIndex = note.getParagraphSize() - 1;//paragraph 的位置
                totalLineIndex = note.getParagraph(totalParagraphIndex).getLines().size() - 1; //paragraph 中line 的位置
                redoStack.add(note.getParagraph(totalParagraphIndex).getLine(totalLineIndex));
                note.getParagraph(totalParagraphIndex).getLines().remove(totalLineIndex);
            }
            canvas.setBitmap(bitmap);
            imageView.setImageBitmap(bitmap);
        }

    }
}

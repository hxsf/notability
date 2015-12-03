package me.hxsf.notability.draw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Stack;

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
    Handler handler;
    private UndoList<Bitmap> undolist;

    private Stack<Line> redoStack;

    private long time;


    /**
     //     * @param imageView  画板
     * @param color  笔触颜色
     * @param penSize  笔触粗细
     */
    public Drawer(final ImageView imageView, int color, float penSize) {
        //初始化画笔
        setPaint(color, penSize);
        //初始化指针
        paragraphIndex = totalParagraphIndex = -1;
        lineIndex = totalLineIndex = -1;
        this.imageView = imageView;
        bitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
        Log.v("w&h", imageView.getWidth() + ",  " + imageView.getHeight());
        canvas=new Canvas(bitmap);
        undolist = new UndoList<>();
        redoStack = new Stack<>();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                imageView.setImageBitmap(bitmap);
            }
        };

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
     */
    public void changePaint(int color) {
        paint.setColor(color);
    }

    /**
     * 改变笔触宽度
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
    public void onNewNote(Note note){
        this.note = note;
        paragraph = new Paragraph();
        line = new Line(paint.getColor(), paint.getStrokeWidth());//初始化line 对象
    }

    public void saveAll() {
       note.addParagraph(paragraph);
   }
    public Note getNote(){
        return note;
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

    public void setTime(Long time) {
        this.time = time;
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
        Pixel pixel;
        if (hasAudio) {
            pixel = new Pixel(time, x, y);
        } else {
            pixel = new Pixel(x, y);
        }
        line.addPixel(pixel); //初始化一个新的像素点对象并添加到 line 对象中
        Log.v("time", "X:" + pixel.getX() + "\t Y" + pixel.getY() + "\t time:" + pixel.getTimestamp());
    }

    /**
     * 笔画结束，手指抬起,将line 对象添加到paragraph 对象中
     */
    public void drawEnd(){
        Log.v("drawend", "start");
        //添加bitmap 快照
        paragraph.addLine(line);
        Log.v("drawend", "finish");
    }
    private void drawStart(){
        Bitmap b1 = Bitmap.createBitmap(bitmap,0,0,imageView.getWidth(),imageView.getHeight());
        undolist.add(b1);
        line = new Line(paint.getColor(), paint.getStrokeWidth());//初始化一个新的line 对象
    }

    public void draw(BaseLine bl) {
        if (bl.isstart==1) {
            path.reset();
            drawStart();
            path.moveTo(bl.x1, bl.y1);
        }
        drawing(bl.x1, bl.y1);
//        drawing(bl.x2, bl.y2);
        path.quadTo(bl.x1, bl.y1, (bl.x1 + bl.x2) / 2, (bl.y1 + bl.y2) / 2);
        canvas.drawPath(path, paint);
        imageView.setImageBitmap(bitmap);
        if(bl.isstart==-1) {
            drawEnd();
            redoStack.clear();
        }
    }


    /**
     * 向前，恢复操作
     */
    public void redo() {
        Log.v("redo", "start");
        if (!redoStack.isEmpty()) {
            Log.v("redo", "doing - Stack size = "+redoStack.size());
            drawStart();
            line = redoStack.pop();
            Log.v("redo", "doing - Lines num  = "+line.getPixels().size());
            path.reset();
            Pixel p1 = line.getPixel(0);
            if (p1 == null){
                Log.e("redo", "null");
                return;
            }
            path.moveTo(p1.getX(), p1.getY());
            for (int i = 1; i < line.getPixels().size(); i++) {
                Pixel p2 = line.getPixel(i);
                path.quadTo(p1.getX(), p1.getY(),(p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2);
                p1=p2;
            }
            Log.v("redo", "path " + path.toString());
            canvas.drawPath(path, paint);
            imageView.setImageBitmap(bitmap);
            drawEnd();
        }
        Log.v("redo", "finish");
    }
    /**
     * 向后，撤销操作
     */
    public void undo() {
        if (undolist.hasItem()) {//队未空
            bitmap = undolist.get();
            //将最后一个line对象取出，放入redoStack 栈中
            ArrayList<Line> lines = paragraph.getLines();
            if (!lines.isEmpty()) {
                Line ll = lines.remove(paragraph.getLines().size() - 1);
                redoStack.push(ll);
                Log.v("for redo", "1 - redoStack.size = " + redoStack.size());
            } else {
//                totalParagraphIndex = note.getParagraphSize() - 1;//paragraph 的位置
                Log.e("for redo", "2 - redoStack.size " + redoStack.size());
//                totalLineIndex = note.getParagraph(totalParagraphIndex).getLines().size() - 1; //paragraph 中line 的位置
//                redoStack.push(note.getParagraph(totalParagraphIndex).getLine(totalLineIndex));
//                note.getParagraph(totalParagraphIndex).getLines().remove(totalLineIndex);
            }
            canvas.setBitmap(bitmap);
            imageView.setImageBitmap(bitmap);
        }

    }

    Runnable show = new Runnable() {
        @Override
        public void run() {
//            TODO to change to timeline
            Paragraph paragraph;
            Line line;
            for (int i = 0; i < note.getParagraphSize(); i++) {
                paragraph = note.getParagraph(i);
                if (!paragraph.hasAudio) {//表示该段有音频
                    for (int j = 0; j < paragraph.getLines().size(); j++) {
                        line = paragraph.getLine(j);
                        path.reset();
                        Pixel p1 = line.getPixel(0);
                        path.moveTo(p1.getX(), p1.getY());
                        for (int k = 1; k < line.getPixels().size(); k++) {
                            Pixel p2 = line.getPixel(k);
                            path.quadTo(p1.getX(), p1.getY(), (p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2);
                            p1 = p2;
                            canvas.drawPath(path, paint);
                            handler.sendEmptyMessage(1);
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
//                            imageView.setImageBitmap(bitmap);
                        }
                    }
                }
            }
        }
    };

    public void startShow() {
        new Thread(show).start();
    }


    private class UndoList<T> {
        private ArrayList<T> list;
        private int p = 0;

        public UndoList() {
            this.list = new ArrayList<>();
        }

        public void add(T t){
            list.add(t);
            Log.v("undolist.add", "num = "+ list.size());
            if (list.size()>4){
                list.remove(0);
            }
        }
        public T get(){
            T bb = list.remove(list.size()-1);
            Log.v("undolist.get", "num = "+ list.size());
            return bb;
        }

        public boolean hasItem() {
            return this.list.size()>0?true:false;
        }
    }
}

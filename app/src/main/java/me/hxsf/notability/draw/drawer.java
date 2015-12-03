package me.hxsf.notability.draw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.widget.ImageView;

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

    /**
     //     * @param imageView  画板
     * @param color  笔触颜色
     * @param penSize  笔触粗细
     */
    public Drawer(ImageView imageView, int color, float penSize) {
        //初始化画笔
        setPaint(color, penSize);
        //初始化指针
        paragraphIndex = totalParagraphIndex = 0;
        lineIndex = totalLineIndex = -1;
        this.imageView = imageView;
        bitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
        Log.v("w&h", imageView.getWidth() + ",  " + imageView.getHeight());
        canvas=new Canvas(bitmap);
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
        if (line != null) {
            //当没有撤销的操作需要恢复——>正常添加
            if (totalLineIndex == lineIndex) {
                paragraph.addLine(line);//将line 对象添加到paragraph 对象中
                lineIndex = totalLineIndex = paragraph.getLines().size() - 1;//获取paragraph中line数组的最新长度
                Log.v("Drawer drawEnd", "normal add，paragraph.size=" + paragraph.getLines().size());
            }
            //有可以恢复的操作——>撤销某些操作后重新输入
            else {
                paragraph.setLines(++lineIndex, line);//将line 对象添加到paragraph 对象中
                totalLineIndex = lineIndex;//将指针移动到最新更新处，即：不能再恢复回之前撤下的东西
                Log.v("Drawer drawEnd", "set");
            }
            line = new Line(paint.getColor(), paint.getStrokeWidth());//初始化一个新的line 对象
        }
    }

    public void draw(BaseLine bl) {
        if (bl.isstart) {
            path.reset();
            drawEnd();
            path.moveTo(bl.x1, bl.y1);
        }
        //        drawing(bl.x1, bl.y1);
        drawing(bl.x2, bl.y2);
        path.quadTo(bl.x1, bl.y1, (bl.x1 + bl.x2) / 2, (bl.y1 + bl.y2) / 2);
//        Log.v("path",bl.toString() );
        canvas.drawPath(path, paint);
        imageView.setImageBitmap(bitmap);
    }

    /**
     * 向前，恢复操作
     * 伪代码：
     *      1：判断是否有可恢复的段落，若有：
     *          1.1：判断是否可有可恢复的line，若有：
     *              1.1.1：获取最近的，
     *              1.1.2：利用line 对象中保存的 画布对象重绘画布
     *              1.1.3：将重绘前的画布布局保存到line对象中，以供 undo
     *          1.2：若没有，表示当前段落已恢复完成，判断当前段落是否是最新的段落，若是，则表示没有操作可以恢复，返回 false
     *              否则表示还有段落可以恢复，段落指针加一，指到要恢复的段落，重新调用redo方法
     *
     */
    public boolean redo() {
        Line line;
        if (paragraphIndex <= totalLineIndex) {//判断是否有可恢复的段
            if (lineIndex < totalLineIndex) {//判断是否可有可恢复的line
//                获取最近的line 的对象
                if (paragraphIndex < totalLineIndex) {
                    line = note.getParagraph(paragraphIndex).getLine(++lineIndex);
                } else {
                    line = paragraph.getLine(++lineIndex);
                }
                for (int i = line.getBitmaps().size() - 1; i > 0; i--) {
                    float endX = line.getPixels().get(i).getX();//获取当前点的坐标
                    float endY = line.getPixels().get(i).getY();
                    float startX = line.getPixels().get(i - 1).getX();//获取上一点的坐标
                    float startY = line.getPixels().get(i - 1).getY();
                    Bitmap temp = line.setBitmap(bitmap, startX, startY, endX, endY); //截取当前画布内容，为undo 准备
                    canvas.setBitmap(line.getBitmap(i));//重绘
                    line.getBitmaps().set(i, temp);//将被重绘部分的内容保存，以供undo 使用
                }
            } else {//当前段落已没有可恢复的段落
//                判断 当前段落是否是最新的段落，若是，则表示没有操作可以恢复，返回 false
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
     * 伪代码：
     *      1：判断是否有可撤销的段落，若有：
     *          1.1：判断是否可有可撤销的line，若有：
     *              1.1.1：获取最近的line 对象，
     *              1.1.2：利用line 对象中保存的 画布对象重绘画布
     *              1.1.3：将重绘前的画布布局保存到line对象中，以供 undo
     *          1.2：若没有，表示当前段落已恢复完成，判断当前段落是否是最新的段落，若是，则表示没有操作可以恢复，返回 false
     *              否则表示还有段落可以恢复，段落指针加一，指到要恢复的段落，重新调用redo方法
     *
     */
    public boolean undo( ) {
        Line line;
        if(paragraphIndex>=0){//表示还有段落可以撤销
                if(lineIndex>=0) {//表示还有笔画可以撤销
//                    将这个笔画所在的方形区域用画之前的方形区域的颜色覆盖
                    if (paragraphIndex == totalParagraphIndex) {
                        //表示要撤消的这段是当前正在使用的段落，还没有被写入note中
                        line = paragraph.getLine(lineIndex--);
                    } else {
                        line = note.getParagraph(paragraphIndex).getLine(lineIndex--);
                    }
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
                } else {
                    if ((paragraphIndex - 1) >= 0) {
                        paragraphIndex--;
                        totalLineIndex = lineIndex = note.getParagraph(paragraphIndex).getLines().size() - 1;
                        undo();
                    } else {
                        return false;
                    }

                }
            return true;
        } else
            Log.v("Drawer undo", "no more to undo");
            return false;
    }
}

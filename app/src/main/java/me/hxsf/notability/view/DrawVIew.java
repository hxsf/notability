package me.hxsf.notability.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.concurrent.LinkedBlockingQueue;

import me.hxsf.notability.draw.BaseLine;
import me.hxsf.notability.draw.Drawer;

/**
 * Created by hxsf on 15－11－30.
 */
public class DrawView extends SurfaceView implements SurfaceHolder.Callback {
    LinkedBlockingQueue<BaseLine> bq = new LinkedBlockingQueue();
    private Drawing drawing; // SurfaceView通常需要自己单独的线程来播放动画
    private SurfaceHolder surfaceHolder;
    private Drawer drawer;

    public DrawView(Context context) {
        super(context);
        this.surfaceHolder = this.getHolder();
        this.surfaceHolder.addCallback(this);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.surfaceHolder = this.getHolder();
        this.surfaceHolder.addCallback(this);
        drawer = Drawer.getDrawer(Color.BLACK, 1f);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.v("sssss", "SurfaceView Created");
        this.drawing = new Drawing(this.surfaceHolder, bq, drawer);
        this.drawing.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.v("sssss", "SurfaceView changed");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v("sssss", "SurfaceView已经销毁");
        this.drawing.shut();
    }

    public boolean offer(BaseLine o) {
        return bq.offer(o);
    }

}


class Drawing extends Thread {

    private SurfaceHolder holder;
    private Canvas canvas;
    private LinkedBlockingQueue<BaseLine> bq;
    private Paint paint;
    private boolean isrun;
    private Drawer drawer;

    public Drawing(SurfaceHolder holder, LinkedBlockingQueue<BaseLine> b, Drawer d) {
        this.holder = holder;
        this.bq = b;
        drawer = d;
        paint = new Paint();
        paint.setAntiAlias(true);    //消除锯齿
        paint.setStyle(Paint.Style.STROKE);    //设置画笔风格为描边
        paint.setColor(Color.BLACK);
        isrun = true;
    }

    public void shut() {
        isrun = false;
    }
    @Override
    public void run() {
        Log.v("run", isrun + "");
        while (isrun) {
            BaseLine bl = bq.poll();
            while (bl != null) {
                Log.v("bl", bl.toString());
                canvas = this.holder.lockCanvas(); // 通过lockCanvas加锁并得到該SurfaceView的画布
                canvas.drawColor(Color.WHITE);
//                canvas.drawLine(bl.x1, bl.y1, bl.x2, bl.y2, paint);
                //TODO chenmeng
                canvas = drawer.draw(canvas,bl);
                this.holder.unlockCanvasAndPost(canvas); // 释放锁并提交画布进行重绘
                bl = bq.poll();
            }
//            try {
//                Log.v("s","a");
//                Thread.sleep(100); // 这个就相当于帧频了，数值越小画面就越流畅
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    }
}
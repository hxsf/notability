package me.hxsf.notability.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import java.util.concurrent.LinkedBlockingQueue;

import me.hxsf.notability.draw.BaseLine;
import me.hxsf.notability.draw.Drawer;

/**
 * Created by hxsf on 15－11－30.
 */
public class DrawView extends ImageView {
    LinkedBlockingQueue<BaseLine> bq = new LinkedBlockingQueue();
    private Drawing drawing; // SurfaceView通常需要自己单独的线程来播放动画
    private Drawer drawer;

    public DrawView(Context context) {
        super(context);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.post(() -> {
            this.drawer = Drawer.getDrawer(this, Color.BLACK, 1f);
            this.drawing = new Drawing(drawer, bq);
            this.drawing.start();
        });

    }

    public void surfaceDestroyed() {
        this.drawing.shut();
    }

    public boolean offer(BaseLine o) {
        return bq.offer(o);
    }

}


class Drawing extends Thread {
    private LinkedBlockingQueue<BaseLine> bq;
    private Paint paint;
    private boolean isrun;
    private Drawer drawer;

    public Drawing(Drawer drawer, LinkedBlockingQueue<BaseLine> b) {
        this.drawer = drawer;
        this.bq = b;

        paint = new Paint();
        paint.setAntiAlias(true);    //消除锯齿
        paint.setStyle(Paint.Style.STROKE);    //设置画笔风格为描边
        paint.setStrokeWidth(20f);
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
                drawer.draw(bl);
                //TODO chenmeng

                bl = bq.poll();
            }
        }
    }
}
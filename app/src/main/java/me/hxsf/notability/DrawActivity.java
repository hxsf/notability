package me.hxsf.notability;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

import me.hxsf.notability.draw.BaseLine;
import me.hxsf.notability.draw.Drawer;

public class DrawActivity extends AppCompatActivity {

    ImageView img;
    /*  Canvas canvas;
      Paint paint = new Paint();
      Bitmap bitmap;*/
    Drawer drawer;
    Drawing drawing;
    float lastx, lasty;
    boolean isstart;
    BaseLine line;
    LinkedBlockingQueue<BaseLine> bq = new LinkedBlockingQueue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        img = (ImageView) findViewById(R.id.draw_space);
        img.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isstart = true;
                        lastx = x;
                        lasty = y;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        line = new BaseLine(isstart, lastx, lasty, x, y);
                        Log.v("bl_Move", line.toString());
                        drawer.draw(line);
                        isstart = false;
                        lastx = x;
                        lasty = y;
                        return true;
                    case MotionEvent.ACTION_UP:
                        line = new BaseLine(isstart, lastx, lasty, x, y);
                        Log.v("bl_End ", line.toString());
                        drawer.draw(line);
                        isstart = false;
                        return true;
                    default:
                        return false;
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String a = getIntent().getStringExtra("title");
        if (a.equals("")) {
            a = "未命名 " + ((new SimpleDateFormat("yyyy-MM-dd hh:mm")).format(new Date()));
//            img.post(new Runnable() {
//                @Override
//                public void run() {
//                }
//            });
        } else {
            //TODO read file/database to get data;
        }

        getSupportActionBar().setTitle(a);
        img.post(new Runnable() {
            @Override
            public void run() {
                drawer = new Drawer(img, Color.BLACK, 1f);
                drawer.onNewNote();
//                drawing = new Drawing(drawer, bq);
//                drawing.start();
            }
        });

        Button undo = (Button) findViewById(R.id.undo);
        undo.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    drawer.undo();
                }
        });
        Button redo=(Button) findViewById(R.id.redo);
        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.redo();
            }
        });
    }

    /**
     * Called when a touch screen event was not handled by any of the views
     * under it.  This is most useful to process touch events that happen
     * outside of your window bounds, where there is no view to receive it.
     *
     * @ param event The touch screen event being processed.
     * @return Return true if you have consumed the event, false if you haven't.
     * The default implementation always returns false.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_draw, menu);
        return true;
    }


}

class Drawing extends Thread {
    private LinkedBlockingQueue<BaseLine> bq;
    private boolean isrun;
    private Drawer drawer;

    public Drawing(Drawer drawer, LinkedBlockingQueue<BaseLine> b) {
        this.drawer = drawer;
        this.bq = b;
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
            Log.v("bl", (bl == null) + "");
            while (bl != null) {
                Log.v("bl", bl.toString());
                drawer.draw(bl);
                //TODO chenmeng

                bl = bq.poll();
            }
        }
    }
}
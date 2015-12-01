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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.hxsf.notability.draw.BaseLine;
import me.hxsf.notability.draw.Drawer;
import me.hxsf.notability.view.DrawView;

public class DrawActivity extends AppCompatActivity {

    DrawView img;
    /*  Canvas canvas;
      Paint paint = new Paint();
      Bitmap bitmap;*/
    Drawer drawer;
    float lastx, lasty;
    boolean isstart;
    BaseLine line;

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

        img = (DrawView) findViewById(R.id.draw_space);
        img.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v("s", event.toString());
                float x = event.getX();
                float y = event.getY();
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    lastx = x;
                    lasty = y;
                    isstart = true;
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    line = new BaseLine(isstart, lastx, lasty, x, y);
                    img.offer(line);
                    lastx = x;
                    lasty = y;
                    isstart = false;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    line = new BaseLine(isstart, lastx, lasty, x, y);
                    img.offer(line);
                    isstart = false;
                }
                return true;
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String a = getIntent().getStringExtra("title");
        if (a.equals("")) {
            a = "未命名 " + ((new SimpleDateFormat("yyyy-MM-dd hh:mm")).format(new Date()));
//            img.post(new Runnable() {
//                @Override
//                public void run() {
                    drawer = Drawer.getDrawer(Color.BLACK, 10f);
                    drawer.onNewNote();
//                }
//            });
        } else {
            //TODO read file/database to get data;
        }

        getSupportActionBar().setTitle(a);
        //TODO 给undo 函数传 canvas 参数
      /*  Button button = (Button) findViewById(R.id.undo);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        drawer.undo();
                    }
                }
        );*/
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

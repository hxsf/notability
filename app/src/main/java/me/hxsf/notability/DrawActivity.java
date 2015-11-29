package me.hxsf.notability;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.hxsf.notability.draw.Drawer;

public class DrawActivity extends AppCompatActivity {

    ImageView img;
    float lastx, lasty;
    /*  Canvas canvas;
      Paint paint = new Paint();
      Bitmap bitmap;*/
    Drawer drawer;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String a = getIntent().getStringExtra("title");
        if (a.equals("")) {
            a = "未命名 " + ((new SimpleDateFormat("yyyy-MM-dd hh:mm")).format(new Date()));
            img.post(new Runnable() {
                @Override
                public void run() {
                    drawer = new Drawer(img, Color.BLACK, 10f);
                    drawer.onNewNote();
                }
            });
        } else {
            //TODO read file/database to get data;
        }

        getSupportActionBar().setTitle(a);
    }

    /**
     * Called when a touch screen event was not handled by any of the views
     * under it.  This is most useful to process touch events that happen
     * outside of your window bounds, where there is no view to receive it.
     *
     * @param event The touch screen event being processed.
     * @return Return true if you have consumed the event, false if you haven't.
     * The default implementation always returns false.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

//        float xx = event.getHistoricalX();
        float x = event.getX();
        float y = event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            drawer.drawStart(x, y);
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE){
            drawer.drawing(x, y);
            lastx = x;
            lasty = y;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            drawer.drawEnd();
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_draw, menu);
        return true;
    }
}

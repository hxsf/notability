package me.hxsf.notability;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.hxsf.notability.draw.BaseLine;
import me.hxsf.notability.draw.Drawer;
import me.hxsf.notability.until.SaveLoad;

public class DrawActivity extends AppCompatActivity {

    ImageView img;
    Drawer drawer;
    float lastx, lasty;
    boolean isstart;
    BaseLine line;
//    LinkedBlockingQueue<BaseLine> bq = new LinkedBlockingQueue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.drawtoolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                String msg = "";
                switch (menuItem.getItemId()) {
                    case R.id.nav_audio:
                        msg += "Click audio";
                        toolbar.getMenu().getItem(0).setIcon(R.drawable.ic_menu_mic_full);
                        break;
                    case R.id.nav_undo:
                        drawer.undo();
                        msg += "Click undo";
                        break;
                    case R.id.nav_redo:
                        msg += "Click ic_menu_redo";
                        break;
                    case R.id.nav_share:
                        msg += "Click share";
                        break;
                }

                if (msg.equals("")) {
                    msg = "none";
                }
                Toast.makeText(DrawActivity.this, msg, Toast.LENGTH_SHORT).show();
                Log.v("click", msg);
                return true;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.color_picker);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

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
        } else {
            //TODO read file/database to get data;
        }

        getSupportActionBar().setTitle(a);
        img.post(new Runnable() {
            @Override
            public void run() {
                drawer = new Drawer(img, Color.BLACK, 1f);
                drawer.onNewNote();
            }
        });

        /*TEST*/

//        BaseLine b = new BaseLine(true, 1, 2, 3, 4);
//        SaveLoad.save("Notability/C1/N2", "1.obj", b);
        BaseLine b = (BaseLine) SaveLoad.load("Notability/C1/N2/1.obj");
        Log.v("sss", b.toString());
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
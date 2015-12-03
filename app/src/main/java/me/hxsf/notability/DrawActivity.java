package me.hxsf.notability;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.hxsf.notability.data.Note;
import me.hxsf.notability.draw.BaseLine;
import me.hxsf.notability.draw.Drawer;
import me.hxsf.notability.until.Recorder;
import me.hxsf.notability.until.SaveLoad;

public class DrawActivity extends AppCompatActivity {
    String notepath;
    ImageView img;
    Drawer drawer;
    float lastx, lasty;
    int isstart;
    BaseLine line;
    long time = 0;
    private boolean isrecording = false;
    Runnable timer = new Runnable() {
        @Override
        public void run() {
            while (isrecording) {
                time += 100;
                drawer.setTime(time);
                Log.v("timer", "run " + time);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.drawtoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String a = getIntent().getStringExtra("title");
        if (a.equals("")) {
            a = "未命名 " + ((new SimpleDateFormat("yyyy-MM-dd hh时mm分")).format(new Date()));
            notepath = null;
        } else {
            notepath = "Notability/" + a + "/note.obj";
        }
        Log.v("a", a);
        getSupportActionBar().setTitle(a);
        img = (ImageView) findViewById(R.id.draw_space);

        final String finalA = a;
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                String msg = "";
                switch (menuItem.getItemId()) {
                    case R.id.nav_audio:
                        drawer.onAudioClick();
                        msg += "Click audio";
                        if (time == 0) {
                            isrecording = true;
                            new Thread(timer).start();
                            msg += " start";
                            Recorder.startRecording("Notability/" + finalA, "1.arm");
                            toolbar.getMenu().getItem(0).setIcon(R.drawable.ic_menu_mic_full);
                        } else {
                            msg += " stop, total " + time + " ms";
                            time = 0;
                            drawer.onAudioClose();
                            isrecording = false;
                            Recorder.stopRecording();
                            toolbar.getMenu().getItem(0).setIcon(R.drawable.ic_menu_mic);
                        }
                        break;
                    case R.id.nav_play:
                        drawer.startShow();
                        break;
                    case R.id.nav_undo:
                        drawer.undo();
                        msg += "Click undo";
                        break;
                    case R.id.nav_redo:
                        drawer.redo();
                        msg += "Click ic_menu_redo";
                        break;
                    case R.id.nav_share:
                        msg += "Click share";
                        break;
                }

                if (msg.equals("")) {
                    msg = "none";
                }
//                Toast.makeText(DrawActivity.this, msg, Toast.LENGTH_SHORT).show();
                Log.v("click", msg);
                return true;
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Navigation", "click");
                save();
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.color_picker);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO change save to colorpick

            }
        });

        img.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isstart = 1;
                        lastx = x;
                        lasty = y;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        line = new BaseLine(isstart, lastx, lasty, x, y);
//                        Log.v("bl_Move", line.toString());
                        drawer.draw(line);
                        isstart = 0;
                        lastx = x;
                        lasty = y;
                        return true;
                    case MotionEvent.ACTION_UP:
//                        Log.v("bl_End ", line.toString());
                        isstart = -1;
                        line = new BaseLine(isstart, lastx, lasty, x, y);
                        drawer.draw(line);
                        return true;
                    default:
                        return false;
                }
            }
        });
        img.post(new Runnable() {
            @Override
            public void run() {
                drawer = new Drawer(img, Color.BLACK, 1f);
                if (notepath == null) {
                    drawer.onNewNote();
                } else {
                    Note nnn = (Note) SaveLoad.load(notepath);
                    String png = Environment.getExternalStorageDirectory().getPath() + "/Notability/" + finalA + "/cache.png";
                    drawer.onNewNote(nnn, png);
                }
                Log.v("nn", "new Draw");
            }
        });
        /*TEST*/

//        BaseLine b = new BaseLine(true, 1, 2, 3, 4);
//        SaveLoad.save("Notability/C1/N2", "1.obj", b);
//        BaseLine b = (BaseLine) SaveLoad.load("Notability/C1/N2/1.obj");
//        Log.v("sss", b.toString());
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

    //    TODO collection 对象的名称和tag 名称
    public void save() {
        String path = "Notability/Default/" + drawer.getNote().getTitle();
        File paths = new File(path);
        if (!paths.exists()) {
            paths.mkdirs();
        }
        drawer.saveAll(path);
//        SaveLoad.save("Notability/"+collection.getTitle()+"/" + drawer.getNote().getTitle(), drawer.getNote().getTitle() + ".obj", drawer.getNote());
        Log.i("save", path + "/" + "note.obj");
        SaveLoad.save(path, "note.obj", drawer.getNote());
    }
}
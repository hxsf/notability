package me.hxsf.notability;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.hxsf.notability.data.Note;
import me.hxsf.notability.draw.BaseLine;
import me.hxsf.notability.draw.Drawer;
import me.hxsf.notability.until.Player;
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
    private long lastBackTime;

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

        Log.i("ImageView Size", "1 W: " + img.getMeasuredWidth() + " H: " + img.getMeasuredHeight());

        final String finalA = a;
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                String msg = "";
                switch (menuItem.getItemId()) {
                    case R.id.nav_audio:
                        msg += "Click audio";
                        if (time == 0) {
                            drawer.onAudioClick();
                            time=System.currentTimeMillis();
                            msg += " start";
                            Recorder.startRecording("Notability/Default/" + finalA, "1.arm");
                            toolbar.getMenu().getItem(0).setIcon(R.drawable.ic_menu_mic_full);
                        } else {
                            msg += " stop, total " + time + " ms";
                            time = 0;
                            drawer.onAudioClose();
                            Recorder.stopRecording();
                            toolbar.getMenu().getItem(0).setIcon(R.drawable.ic_menu_mic);
                        }
                        break;
                    case R.id.nav_play:
                        Player.play("Notability/" + finalA + "/1.arm");
//                        Player.play("Notability/" + finalA + "/"+ finalA +".arm");
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
                        Uri uri = Uri.fromFile(drawer.getCache(Environment.getExternalStorageDirectory().getPath() + "/Notability/" + finalA + "/cache.png"));
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("image/*");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        startActivity(Intent.createChooser(shareIntent, "请选择"));
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
        toolbar.setNavigationIcon(R.drawable.ic_done);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Navigation", "click");
                save();
                finish();
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
                        Log.v("Touch", "Down");
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        line = new BaseLine(isstart, lastx, lasty, x, y);
                        Log.v("Touch", "Move");
                        drawer.draw(line,System.currentTimeMillis()-time);
                        isstart = 0;
                        lastx = x;
                        lasty = y;
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (isstart == 1) {
                            Log.v("Touch", "Up ----- Click");
                            return true;
                        }
                        Log.v("Touch", "Up");
                        isstart = -1;
                        line = new BaseLine(isstart, lastx, lasty, x, y);
                        drawer.draw(line,System.currentTimeMillis()-time);
                        return true;
                    default:
                        return false;
                }
            }
        });
        img.post(new Runnable() {
            @Override
            public void run() {
                drawer = Drawer.newDrawer(img, Color.BLACK, 5f);
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
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        Player.stop();
        drawer.stopShow();
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

    /**
     * Take care of calling onBackPressed() for pre-Eclair platforms.
     *
     * @param keyCode
     * @param event
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long nowtime = System.currentTimeMillis();
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (nowtime - lastBackTime > 1000) {
                lastBackTime = nowtime;
                Toast.makeText(this, "再按一次返回键以返回上级（不保存）", Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //    TODO collection 对象的名称和tag 名称
    public void save() {
        String path = "Notability/Default/" + drawer.getNote().getTitle();
        File paths = new File(path);
        if (!paths.exists()) {
            paths.mkdirs();
        }
        drawer.saveAll(path);
        Log.i("save", path + "/" + "note.obj");
        SaveLoad.save(path, "note.obj", drawer.getNote());
    }
}


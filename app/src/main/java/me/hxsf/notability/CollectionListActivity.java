package me.hxsf.notability;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import me.hxsf.notability.data.Note;
import me.hxsf.notability.dummy.DummyContent;
import me.hxsf.notability.until.SaveLoad;
import me.hxsf.notability.view.BaseActivity;

/**
 * An activity representing a list of Collections. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link CollectionDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class CollectionListActivity extends BaseActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private Context context;

    @Override
    protected void onStart() {
        super.onStart();
        View view = getWindow().getDecorView().findViewById(R.id.rootview);//查找通过setContentView上的根布局
        if (view == null) return;
        final File basePath = new File(Environment.getExternalStorageDirectory().getPath() + "/Notability/Default/");
        if (basePath.exists()) {
            //引导过了
            System.out.println("not first");
            return;
        }
        System.out.println("show demo");
        File nomedia = new File(Environment.getExternalStorageDirectory().getPath() + "/Notability/.nomedia");

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.helloworld);
        File spicyDirectory = new File(Environment.getExternalStorageDirectory().getPath() + "/Notability/Default/Hello World");
        if (!spicyDirectory.exists()) {
            spicyDirectory.mkdirs();
        }
        File filename = new File(Environment.getExternalStorageDirectory().getPath() + "/Notability/Default/Hello World/cache.png");
        SaveLoad.save("Notability/Default/Hello World/", "note.obj", new Note("Hello World"));
        FileOutputStream out = null;
        try {
            filename.createNewFile();
            out = new FileOutputStream(filename);
            System.out.println("create fos");
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
                }
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            out = null;
        }
        if (!nomedia.exists()) {
            try {
                nomedia.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent(CollectionListActivity.this, DrawActivity.class);
        intent.putExtra("title", "Default/Hello World");
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(CollectionListActivity.this, DrawActivity.class);
                intent.putExtra("title", "");
                startActivity(intent);
            }
        });

        View recyclerView = findViewById(R.id.collection_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);


        if (findViewById(R.id.collection_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        context = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupRecyclerView((RecyclerView) findViewById(R.id.collection_list));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_activity_draw, menu);
        return true;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Notability");
        DummyContent.ITEM_MAP.clear();
        DummyContent.ITEMS.clear();
        if (!file.exists()) {
            file.mkdirs();
        }
        for (File dir : file.listFiles()) {
            if (dir.isDirectory()) {
                DummyContent.addItem(new DummyContent.DummyItem(dir.getName(), "分类", ""));
            }
        }
        final SimpleItemRecyclerViewAdapter mAdapter = new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS);
        recyclerView.setAdapter(mAdapter);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            //在这个回调 我们处理滑动
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                Log.d("asd", "onSwiped() called with " + "viewHolder = [" + viewHolder + "], direction = [" + direction + "]");
                //这里我们通过viewHolder获取position
                new AlertDialog.Builder(context).setTitle("确定删除")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int position = viewHolder.getAdapterPosition();
                                mAdapter.notifyItemRemoved(position);
                                dialog.dismiss();
                            }
                        }).show();
//                Toast.makeText(getActivity(), "拆散的position:"+position, Toast.LENGTH_SHORT).show();
            }

            // 暂时不处理移动事件...
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Log.d("asd", "onMove() called with " + "recyclerView = [" + recyclerView + "], viewHolder = [" + viewHolder + "], target = [" + target + "]");
                return false;
            }

        }).attachToRecyclerView(recyclerView);
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<DummyContent.DummyItem> mValues;
        //TODO read collects

        public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.collection_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(CollectionDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        CollectionDetailFragment fragment = new CollectionDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.collection_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, CollectionDetailActivity.class);
                        intent.putExtra(CollectionDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public DummyContent.DummyItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.collection_num);
                mContentView = (TextView) view.findViewById(R.id.collection_title);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
	}
}

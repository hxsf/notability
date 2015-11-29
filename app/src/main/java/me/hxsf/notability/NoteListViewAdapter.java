package me.hxsf.notability;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import me.hxsf.notability.data.Note;

/**
 * Created by hxsf on 15－11－27.
 *
 */
public class NoteListViewAdapter extends BaseAdapter {

    private ArrayList<Note> notelist = null;
    private Context context = null;

    /**
     * 构造函数,初始化Adapter,将数据传入
     * @param notelist
     * @param context
     */
    public NoteListViewAdapter(ArrayList<Note> notelist, Context context) {
        this.notelist = notelist;
        this.context = context;
    }

    @Override
    public int getCount() {
        return notelist == null ? 0 : notelist.size();
    }

    @Override
    public Object getItem(int position) {
        return notelist == null ? null : notelist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //装载view
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        View view = layoutInflater.inflate(R.layout.note, null);

        //获取控件
        final ImageView imageView = (ImageView) view.findViewById(R.id.minimap);
        final TextView createTime = (TextView) view.findViewById(R.id.lastModified);
        final TextView title = (TextView) view.findViewById(R.id.note_title);
        //对控件赋值
        final Note note = (Note) getItem(position);
        if (note != null) {
//            if (imageView != null) {
//                imageView.setImageBitmap(note.getMinimap());
//            }
            createTime.setText(note.getLastModified());
            title.setText(note.getTitle());
        }
        return view;
    }
}
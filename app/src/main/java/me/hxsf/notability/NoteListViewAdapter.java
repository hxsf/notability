package me.hxsf.notability;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;

import me.hxsf.notability.data.Note;
import me.hxsf.notability.until.FileHelper;
import me.hxsf.notability.view.BaseActivity;

/**
 * Created by hxsf on 15－11－27.
 *
 */
public class NoteListViewAdapter extends BaseAdapter {

    private ArrayList<Note> notelist = null;
    private Context context = null;

    private String collection;

    /**
     * 构造函数,初始化Adapter,将数据传入
     * @param notelist
     * @param context
     */
    public NoteListViewAdapter(ArrayList<Note> notelist, Context context) {
        this.notelist = notelist;
        this.context = context;
        collection = ((BaseActivity) context).collection;

        for (Note item : notelist) {
            System.out.println(item.getTitle() + " = " + item.getLastModified());
        }
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
    public View getView(int position, final View convertView, ViewGroup parent) {
        //装载view
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        final View view = layoutInflater.inflate(R.layout.note, null);
        final int pos = position;
        //获取控件
        final ImageView imageView = (ImageView) view.findViewById(R.id.minimap);
        final TextView createTime = (TextView) view.findViewById(R.id.lastModified);
        final TextView title = (TextView) view.findViewById(R.id.note_title);
        final View rename = view.findViewById(R.id.rename);
        final View move = view.findViewById(R.id.move);
        final View delete = view.findViewById(R.id.delete);
        //对控件赋值
        final Note note = (Note) getItem(position);
        if (note != null) {
//            if (imageView != null) {
//                imageView.setImageBitmap(note.getMinimap());
//            }
            createTime.setText(note.getLastModified());
            Log.i("last", " note.lastModified=" + note.getLastModified());
            title.setText(note.getTitle());
        }
        rename.setOnClickListener(new View.OnClickListener() {//重命名
            @Override
            public void onClick(View view1) {
                final TextView noteTag = (TextView) view.findViewById(R.id.note_title);//获取item元素
                final String name = (String) noteTag.getText();//获取note标题
                System.out.println("re ame  " + name);
                final EditText renameEdit = new EditText(context);
                renameEdit.setText(name);//设置初始值
                new AlertDialog.Builder(context).setTitle("重命名")//设置标题
                        .setView(renameEdit)//绑定编辑栏
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {//设置确定事件
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String newName = renameEdit.getText().toString();//获取新文件名
                                System.out.println("newName  :" + newName);
                                File newFile = new File(Environment.getExternalStorageDirectory().getPath() + "/Notability/" + collection + "/" + newName);
                                if (newFile.exists()) {//若文件名已存在
                                    Toast.makeText(context, "文件名已存在", Toast.LENGTH_SHORT).show();
                                } else {
                                    noteTag.setText(newName);
                                    File oldFile = new File(Environment.getExternalStorageDirectory().getPath() + "/Notability/" + collection + "/" + name);
                                    oldFile.renameTo(newFile);
                                }
                            }
                        })
                        .setNegativeButton("取消", null)//设置取消事件
                        .show();
            }
        });
        final Adapter adapter = this;
        move.setOnClickListener(new View.OnClickListener() {//移动
            @Override
            public void onClick(View view1) {
                final TextView noteTagToMove = (TextView) view.findViewById(R.id.note_title);//获取item元素
                final String move_name = (String) noteTagToMove.getText();//获取note标题
                System.out.println("move name  " + move_name);
                final String basePath = Environment.getExternalStorageDirectory().getPath() + "/Notability/";
                final String[] collectList = (new File(basePath)).list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        System.out.println(dir.getPath() + "/" + filename);
                        if ((new File(dir, filename)).isDirectory()) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
                //当前路径
                final String oldPath = basePath + collection + "/" + move_name;

                new AlertDialog.Builder(context).setTitle("移动到")
//                        设置单选对话框，显示所有的 collection 目录,确定目的地
                        .setItems(collectList, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //目标路径
                                String newPath = Environment.getExternalStorageDirectory().getPath() + "/Notability/" + collectList[which] + "/" + move_name;
                                if (FileHelper.moveDirectory(oldPath, newPath)) {
                                    Toast.makeText(context, "移动成功", Toast.LENGTH_SHORT).show();
                                    notelist.remove(pos);
                                    notifyDataSetChanged();
                                } else {
                                    Toast.makeText(context, "移动失败", Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }
                        })
                        .setNeutralButton("新建", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final EditText creatEdit = new EditText(context);
                                new AlertDialog.Builder(context).setTitle("新建分类")
                                        .setView(creatEdit)
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                String newCollcetion = creatEdit.getText().toString();
                                                File newCollectionFile = new File(Environment.getExternalStorageDirectory().getPath() + "/Notability/" + newCollcetion);
                                                if (newCollectionFile.exists()) {
                                                    Toast.makeText(context, "文件已存在", Toast.LENGTH_SHORT).show();
                                                    return;
                                                } else {
                                                    newCollectionFile.mkdirs();
                                                    String newPath = Environment.getExternalStorageDirectory().getPath() + "/Notability/" + newCollcetion + "/" + move_name;
                                                    if (FileHelper.moveDirectory(oldPath, newPath)) {
                                                        Toast.makeText(context, "移动成功", Toast.LENGTH_SHORT).show();
                                                        notelist.remove(pos);
                                                        notifyDataSetChanged();
                                                    } else {
                                                        Toast.makeText(context, "移动失败", Toast.LENGTH_SHORT).show();
                                                    }
                                                    dialogInterface.dismiss();
                                                }
                                            }
                                        })
                                        .setNegativeButton("取消", null)
                                        .show();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {//删除
            @Override
            public void onClick(View view1) {
                final TextView noteTagToDelete = (TextView) view.findViewById(R.id.note_title);//获取item元素
                System.out.println("delete name  " + (String) noteTagToDelete.getText());
                final String delete_name = (String) noteTagToDelete.getText();//获取note标题

                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Notability/" + collection + "/" + delete_name);
                if (file.exists()) {
                    FileHelper.removeDirectory(file);
                    notelist.remove(pos);
                    notifyDataSetChanged();
                } else {
                    System.out.println("file：" + file);
                    Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private void copyFolder(String oldPath, String newPath) {
        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a=new File(oldPath);
            String[] file=a.list();
            File temp=null;
            for (int i = 0; i < file.length; i++) {
                if(oldPath.endsWith(File.separator)){
                    temp=new File(oldPath+file[i]);
                }
                else{
                    temp=new File(oldPath+File.separator+file[i]);
                }
                if(temp.isFile()){
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ( (len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if(temp.isDirectory()){//如果是子文件夹
                    copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);
                }
            }
        }
        catch (Exception e) {
            System.out.println("移动失败");
            e.printStackTrace();
        }
    }

}
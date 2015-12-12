package me.hxsf.notability;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import me.hxsf.notability.data.Note;
import me.hxsf.notability.dummy.DummyContent;

/**
 * A fragment representing a single Collection detail screen.
 * This fragment is either contained in a {@link CollectionListActivity}
 * in two-pane mode (on tablets) or a {@link CollectionDetailActivity}
 * on handsets.
 */
public class CollectionDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    View rootView;
    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;
    private ArrayList<Note> noteArrayList = new ArrayList<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CollectionDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(getArguments().getString(ARG_ITEM_ID));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Notability/" + getArguments().getString(ARG_ITEM_ID));
        ((CollectionDetailActivity) getActivity()).collection = getArguments().getString(ARG_ITEM_ID);
        noteArrayList.clear();
        if (!file.exists()) {
            file.mkdirs();
        }
        for (File dir : file.listFiles()) {
            if (dir.isDirectory()) {
                File ff = new File(dir.getPath() + "/note.obj");
                if (!ff.exists()) {
                    continue;
                }
                long lastModified = ff.lastModified();
                noteArrayList.add(new Note(new Date(lastModified), null, dir.getName()));
            }
        }
        NoteListViewAdapter noteListViewAdapter = new NoteListViewAdapter(noteArrayList, getActivity());
        ListViewForScrollView listView = (ListViewForScrollView) rootView;
        listView.setAdapter(noteListViewAdapter);
        listView.initSlideMode(ListViewForScrollView.MOD_BOTH);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("s", position + "");
//                    final LinearLayout hide = (LinearLayout)view.findViewById(R.id.note);
                Intent intent = new Intent(getActivity(), DrawActivity.class);
                intent.putExtra("title", getArguments().getString(ARG_ITEM_ID) + "/" + ((TextView) view.findViewById(R.id.note_title)).getText());
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.collection_detail, container, false);
        return rootView;
    }
}

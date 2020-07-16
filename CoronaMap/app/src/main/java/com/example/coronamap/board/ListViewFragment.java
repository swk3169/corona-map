package com.example.coronamap.board;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.coronamap.MainActivity;
import com.example.coronamap.R;
import com.example.coronamap.adapter.ListviewAdapter;
import com.example.coronamap.model.BulletinboardModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListViewFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ArrayList<BulletinboardModel> items;

    public ListViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListViewFragment newInstance(String param1, String param2) {
        ListViewFragment fragment = new ListViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);

        final ListView listview = (ListView) view.findViewById(R.id.listview);
        DatabaseReference boardReference = FirebaseDatabase.getInstance().getReference().child("board");

        final ArrayList<BulletinboardModel> items = new ArrayList<>();
        final ListviewAdapter adapter = new ListviewAdapter(getContext(), items);

        listview.setAdapter(adapter);

        boardReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // 클래스 모델이 필요?
                for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                    //MyFiles filename = (MyFiles) fileSnapshot.getValue(MyFiles.class);
                    //하위키들의 value를 어떻게 가져오느냐???
                    String title = fileSnapshot.child("title").getValue(String.class);
                    String date = fileSnapshot.child("date").getValue(String.class);
                    String writer = fileSnapshot.child("id").getValue(String.class);
                    String content = fileSnapshot.child("content").getValue(String.class);

                    BulletinboardModel bbm = new BulletinboardModel();
                    bbm.setTitle(title);
                    bbm.setDate(date);
                    bbm.setId(writer);
                    bbm.setContent(content);


                    items.add(bbm);

                    Collections.sort(items, new Comparator<BulletinboardModel>() {
                        @Override
                        public int compare(BulletinboardModel bulletinboardModel, BulletinboardModel t1) {
                            return t1.getDate().compareTo(bulletinboardModel.getDate());
                        }
                    });
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG: ", "Failed to read value", databaseError.toException());
            }
        });



        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int position, long id) {

                //Intent intent = new Intent(getContext(), BoardView.class);

                BulletinboardModel bbm = (BulletinboardModel)adapterView.getItemAtPosition(position);

                ((MainActivity)getActivity()).replaceFragment(BoardViewFragment.newInstance(bbm.getTitle(), bbm.getContent()));
                //클릭한 아이템의 문자열을 가져옴
                //String selected_item = (String) adapterView.getItemAtPosition(position);

                //Log.d("tag", selected_item);
                //텍스트뷰에 출력
                //selected_item_textview.setText(selected_item);

                //intent.putExtra("title", bbm.getTitle());
                //intent.putExtra("content", bbm.getContent());
                //startActivity(intent);
            }
        });

        return view;
    }
}
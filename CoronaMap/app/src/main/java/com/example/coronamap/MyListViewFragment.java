package com.example.coronamap;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyListViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyListViewFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    FragmentManager manager;


    String userID= "";

    public MyListViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyListViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyListViewFragment newInstance(String param1, String param2) {
        MyListViewFragment fragment = new MyListViewFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_list_view, container, false);
        final ListView listview = (ListView) view.findViewById(R.id.my_listview);
        DatabaseReference boardReference = FirebaseDatabase.getInstance().getReference().child("board");

        final ArrayList<BulletinboardModel> items = new ArrayList<>();
        final ListviewAdapter adapter = new ListviewAdapter(getContext(), items);

        listview.setAdapter(adapter);

        userID = ((MainActivity)getActivity()).userID;


//        String key = boardReference.child("-MC7euoBGr5CBBHFqoha").child("id").getKey();
//        Log.d("key: " , key);

        boardReference.orderByChild("id").equalTo(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items.clear();
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


                BulletinboardModel bbm = (BulletinboardModel)adapterView.getItemAtPosition(position);

                ((MainActivity)getActivity()).replaceFragment(MyListViewManageFragment.newInstance(bbm.getTitle(), bbm.getContent(), bbm.getId(), bbm.getDate()));

            }
        });
        // Inflate the layout for this fragment
        return view;
    }
}
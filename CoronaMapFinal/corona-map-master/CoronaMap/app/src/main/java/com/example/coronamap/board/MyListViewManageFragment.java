package com.example.coronamap.board;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.coronamap.MainActivity;
import com.example.coronamap.R;
import com.example.coronamap.model.BulletinboardModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyListViewManageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyListViewManageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;
    private String mParam4;

    EditText et_mylist_title, et_mylist_content;

    public MyListViewManageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyListViewManageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyListViewManageFragment newInstance(String param1, String param2, String param3, String param4) {
        MyListViewManageFragment fragment = new MyListViewManageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
            mParam4 = getArguments().getString(ARG_PARAM4);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_list_view_manage, container, false);

        final DatabaseReference boardReference = FirebaseDatabase.getInstance().getReference().child("board");

        et_mylist_title = view.findViewById(R.id.et_mylist_title);
        et_mylist_content = view.findViewById(R.id.et_mylist_content);

        // Inflate the layout for this fragment
//        Bundle bundle = this.getArguments();
//        String title= bundle.getString("title");
//        String content = bundle.getString("content");
//        String writer = bundle.getString("writer");
//        String date = bundle.getString("date");

        final String title = mParam1;
        final String content = mParam2;
        final String id= mParam3;
        final String date= mParam4;

        et_mylist_title.setText(title);
        et_mylist_content.setText(content);


        Button modify_btn = view.findViewById(R.id.btn_modify);
        Button remove_btn = view.findViewById(R.id.btn_remove);
        Button cancle_btn = view.findViewById(R.id.btn_mylist_cancle);

        modify_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                boardReference.orderByChild("content").equalTo(content).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                            String key = appleSnapshot.getKey();
                            Log.e("Key", key);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy.MM.dd     HH:mm", Locale.KOREA);
                            Date currentTime = new Date();
                            String time =simpleDateFormat.format(currentTime);

                            Map<String, Object> taskMap = new HashMap<String, Object>();
                            taskMap.put(key, new BulletinboardModel(id,et_mylist_title.getText().toString(), time, et_mylist_content.getText().toString()));


                            boardReference.updateChildren(taskMap);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("tag", "onCancelled", databaseError.toException());
                    }
                });

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                MyListViewFragment myListViewFragment= new MyListViewFragment();
                ((MainActivity)getActivity()).replaceFragment(myListViewFragment);
//                ((MainActivity)getActivity()).replaceFragment(new MyListViewFragment());

            }
        });

        remove_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                boardReference.orderByChild("content").equalTo(content).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("tag", "onCancelled", databaseError.toException());
                    }
                });

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ((MainActivity)getActivity()).replaceFragment(new MyListViewFragment());
            }
        });

        cancle_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                MyListViewFragment myListViewFragment= new MyListViewFragment();
                ((MainActivity)getActivity()).replaceFragment(myListViewFragment);
            }
        });

        return view;
    }
}
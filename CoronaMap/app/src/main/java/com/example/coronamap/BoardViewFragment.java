package com.example.coronamap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BoardViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BoardViewFragment extends Fragment {
    TextView tv_listview_title, tv_listview_content;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BoardViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BoardViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BoardViewFragment newInstance(String param1, String param2) {
        BoardViewFragment fragment = new BoardViewFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_board_view, container, false);
        tv_listview_title = view.findViewById(R.id.tv_listview_title);
        tv_listview_content = view.findViewById(R.id.tv_listview_content);


        final String title = mParam1;
        final String content = mParam2;

        tv_listview_title.setText(title);
        tv_listview_content.setText(content);

        Button btn_back = view.findViewById(R.id.btn_back);

        btn_back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).replaceFragment(new ListViewFragment());
            }
        });
//        Intent intent = getIntent();
//        if(intent!=null){
//            String title = intent.getStringExtra("title");
//            String content = intent.getStringExtra("content");
//
//            tv_listview_content.setText(content);
//            tv_listview_title.setText(title);
//
//
//        }

        return view;
    }
}
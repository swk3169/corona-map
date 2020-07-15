package com.example.coronamap.board;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.coronamap.MainActivity;
import com.example.coronamap.R;
import com.example.coronamap.model.BulletinboardModel;
import com.example.coronamap.model.Clinic;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WriteBoardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WriteBoardFragment extends Fragment {

    ArrayList<Clinic> clinics;
    ArrayList<Location> clinic_address;

    //이메일 비밀번호 로그인 모듈 변수
    FirebaseAuth mAuth;
    //현재 로그인 된 유저 정보를 담을 변수
    FirebaseUser currentUser;
    static String userID;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText et_list_title, et_list_content;
    private DatabaseReference mDatabase;

    public WriteBoardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BulletinboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WriteBoardFragment newInstance(String param1, String param2) {
        WriteBoardFragment fragment = new WriteBoardFragment();
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
        View view = inflater.inflate(R.layout.fragment_bulletinboard, container, false);
        // Inflate the layout for this fragment


        //String s = myApp.getGlobalValue();

        clinics = ((MainActivity)getActivity()).clinics;
        clinic_address = ((MainActivity)getActivity()).clinic_address;

        et_list_title = view.findViewById(R.id.et_list_title);
        et_list_content = view.findViewById(R.id.et_list_content);

        mDatabase = FirebaseDatabase.getInstance().getReference();


        Button btn_list_save = view.findViewById(R.id.btn_list_save);
        Button btn_list_cancle = view.findViewById(R.id.btn_list_cancle);

        btn_list_save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String content = et_list_content.getText().toString();
                String title = et_list_title.getText().toString();

                userID= ((MainActivity)getActivity()).userID;


                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy.MM.dd     HH:mm", Locale.KOREA);
                Date currentTime = new Date();
                String time =simpleDateFormat.format(currentTime);

                //writeNewUser("1", title, content);
                if(userID!=null){
                    writeBulletinBoard(userID, title, time, content);
                } else {
                    Toast.makeText(getContext(), "로그인부터 하세요.", Toast.LENGTH_LONG).show();
                }

            }
        });

        btn_list_cancle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("clinic", clinics);
                intent.putExtra("clinic_addr", clinic_address);
                intent.putExtra("userID", userID);
                startActivity(intent);
//                HomeFragment homeFragment = new HomeFragment();
//                ((MainActivity)getActivity()).replaceFragment(homeFragment);

            }
        });

        return view;
    }

    private void writeBulletinBoard(String id, String title, String date, String content) {
        BulletinboardModel bbm = new BulletinboardModel(id, title, date, content);
        mDatabase.child("board").push().setValue(bbm)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        Toast.makeText(getContext(), "저장을 완료했습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("clinic", clinics);
                        intent.putExtra("clinic_addr", clinic_address);
                        intent.putExtra("userID", userID);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Toast.makeText(getContext(), "저장을 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
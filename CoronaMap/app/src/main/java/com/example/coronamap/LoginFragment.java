package com.example.coronamap;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    //이메일 비밀번호 로그인 모듈 변수
    ArrayList<Clinic> clinics;
    ArrayList<Location> clinic_address;
    private FirebaseAuth firebaseAuth;
    //현재 로그인 된 유저 정보를 담을 변수
    FirebaseUser currentUser;

    EditText et_id, et_pass;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        clinics = ((MainActivity)getActivity()).clinics;
        clinic_address = ((MainActivity)getActivity()).clinic_address;

        firebaseAuth = FirebaseAuth.getInstance();

        et_id = view.findViewById(R.id.et_login_id);
        et_pass = view.findViewById(R.id.et_login_pass);

        Button login_btn = view.findViewById(R.id.btn_login);
        Button login_cancle_btn = view.findViewById(R.id.btn_login_cancle);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!et_id.getText().toString().equals("") && !et_pass.getText().toString().equals("")) {
                    loginUser(et_id.getText().toString(), et_pass.getText().toString());
                } else {
                    Toast.makeText(getContext(), "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
                }
            }
        });

        login_cancle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("clinic", clinics);
                intent.putExtra("clinic_addr", clinic_address);
                startActivity(intent);
            }
        });

        return view;
    }

    public void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공
                            Toast.makeText(getContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                            //firebaseAuth.addAuthStateListener(firebaseAuthListener);
                            Log.w("LoginSuccess!!", "성공");

                            currentUser = firebaseAuth.getCurrentUser();

                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("clinic", clinics);
                            intent.putExtra("clinic_addr", clinic_address);
                            intent.putExtra("userID", currentUser.getEmail());
                            startActivity(intent);



                        } else {
                            // 로그인 실패
                            Toast.makeText(getContext(), "아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                            Log.w("LoginFail!!", "실패");
                        }
                    }
                });
    }

}
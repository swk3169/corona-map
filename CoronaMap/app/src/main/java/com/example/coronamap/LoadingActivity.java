package com.example.coronamap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LoadingActivity extends AppCompatActivity {
    Context context = this;
    final String TAG = "LoadingActivity";
    private String number;      // 연번
    private String sample;      // 채취가능여부
    private String city;        // 도시명
    private String district;    // 시군구
    private String name;        // 병원명
    private String address;     // 주소
    private String phoneNumber; // 대표전화번호

    private DatabaseReference mDatabase;
    private Clinic clinic = new Clinic();
    ArrayList<Clinic> clinicList = new ArrayList<Clinic>();
//
//    // 데몬(서브) 스레드
//    class AutoSaveThread implements Runnable {
//        public void save() {
//            System.out.println("작업 내용을 저장합니다.");
//        }
//
//        @Override
//        public void run() {
//            while(!Thread.currentThread().isInterrupted()) {
//                try {
//                    readData();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    break;
//                } finally {
//                    save(); // 1초 주기로 호출
//                }
//            }
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        Log.d(TAG, "onCreate");

                /*
        ArrayList<Clinic> clinics: 선별진료소 xml을 파싱하여 병원 정보를 가지고 있는 Clinic 객체 ArrayList
        ArrayList<Location> clinic_address: 파싱한 데이터에서 주소값만 불러와 위도, 경도의 값을 가지는 Location 객체를 따로 가지는 Location ArrayList
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Clinic> clinics = xml_parse();
                ArrayList<Location> clinic_address = new ArrayList<Location>();
                for(int i = 0 ; i < clinics.size(); i++) {
                    Log.d(TAG, "convert");
                    clinic_address.add(addrToPoint(context, clinics.get(i).getAddress()));
                } // 병원 주소만 위도경보로 변환하여 모아놓음
                Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                intent.putExtra("clinic", clinics);
                intent.putExtra("clinic_addr", clinic_address);
                startActivity(intent);
            }
        }).start();
        /*
        ArrayList<Clinic> clinics: 선별진료소 xml을 파싱하여 병원 정보를 가지고 있는 Clinic 객체 ArrayList
        ArrayList<Location> clinic_address: 파싱한 데이터에서 주소값만 불러와 위도, 경도의 값을 가지는 Location 객체를 따로 가지는 Location ArrayList
         */
//        // 메인 스레드
//        AutoSaveThread autoSaveThread = new AutoSaveThread();
//        Thread thread = new Thread(autoSaveThread);
//        thread.start();
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    ArrayList<Clinic> clinics = clinicList;
//                    ArrayList<Location> clinic_address = new ArrayList<Location>();
//                    for (int i = 0; i < clinics.size(); i++) {
//                        Log.d(TAG, "convert");
//                        clinic_address.add(addrToPoint(context, clinics.get(i).getAddress()));
//                    } // 병원 주소만 위도경보로 변환하여 모아놓음
//                    Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
//                    intent.putExtra("clinic", clinics);
//                    intent.putExtra("clinic_addr", clinic_address);
//                    startActivity(intent);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }) {
//        };
//        t.start();
//        try {
//            t.join();
//            readData();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        Log.d("thread", "메인 스레드 종료");
//    }

//    // Firebase 연동
//    public void readData() {
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//
//        mDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
//                    String a = childSnapshot.getValue().toString();
//                    //Log.d("TAG", "a 아무거나 출력값 : ", a);
//                    number = childSnapshot.child("number").getValue(String.class);
//                    sample = childSnapshot.child("sample").getValue(String.class);
//                    city = childSnapshot.child("city").getValue(String.class);
//                    district = childSnapshot.child("district").getValue(String.class);
//                    name = childSnapshot.child("name").getValue(String.class);
//                    address = childSnapshot.child("address").getValue(String.class);
//                    phoneNumber = childSnapshot.child("phoneNumber").getValue(String.class);
//
////                    clinic = new Clinic(number, sample, city, district, name, address, phoneNumber);
//                    clinic.setNumber(number);
//                    clinic.setSample(sample);
//                    clinic.setCity(city);
//                    clinic.setDistrict(district);
//                    clinic.setName(name);
//                    clinic.setAddress(address);
//                    clinic.setPhoneNumber(phoneNumber);
//                    clinicList.add(clinic);
//                    Log.d("why", clinic.getNumber());
//                    Log.d("why", "정보를 가져오는 것을 성공하였습니다.");
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.w("FireBaseData", "loadPost:onCancelled", error.toException());
//            }
//        });
    }
    private ArrayList<Clinic> xml_parse() {
        ArrayList<Clinic> clinicsList = new ArrayList<Clinic>();
        InputStream inputStream = getResources().openRawResource(R.raw.selectiveclinic_test);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        XmlPullParserFactory xmlPullParserFactory = null;
        XmlPullParser xmlPullParser = null;

        try {
            xmlPullParserFactory = XmlPullParserFactory.newInstance();
            xmlPullParser = xmlPullParserFactory.newPullParser();
            xmlPullParser.setInput(reader);

            Clinic clinic = null;
            int eventType = xmlPullParser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        Log.i(TAG, "xml START");
                        break;
                    case XmlPullParser.START_TAG:
                        String startTag = xmlPullParser.getName();
                        Log.i(TAG, "Start TAG :" + startTag);
                        if(startTag.equals("Row")) {
                            clinic = new Clinic();
                            Log.d(TAG, "clinic 추가");
                        }
                        else if(startTag.equals("연번")) {
                            clinic.setNumber(xmlPullParser.nextText());
                            Log.d(TAG, clinic.getNumber());
                            Log.d(TAG, "clinic 연번");
                        }
                        else if(startTag.equals("검체채취가능여부")) {
                            clinic.setSample(xmlPullParser.nextText());
                        }
                        else if(startTag.equals("시도")) {
                            clinic.setCity(xmlPullParser.nextText());
                        }
                        else if(startTag.equals("시군구")) {
                            clinic.setDistrict(xmlPullParser.nextText());
                        }
                        else if(startTag.equals("의료기관명")) {
                            clinic.setName(xmlPullParser.nextText());
                        }
                        else if(startTag.equals("주소")) {
                            clinic.setAddress(xmlPullParser.nextText());
                        }
                        else if(startTag.equals("대표전화번호")) {
                            clinic.setPhoneNumber(xmlPullParser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String endTag = xmlPullParser.getName();
                        Log.i(TAG,"End TAG : "+ endTag);
                        if (endTag.equals("Row")) {
                            clinicsList.add(clinic);
                        }
                        break;
                }
                try {
                    eventType = xmlPullParser.next();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try{
                if(reader !=null) reader.close();
                if(inputStreamReader !=null) inputStreamReader.close();
                if(inputStream !=null) inputStream.close();
            }catch(Exception e2){
                e2.printStackTrace();
            }
        }
        return clinicsList;
    }

    // 주소명으로 위도 경도를 구하는 메소드 (구글맵: Geocoder)
    public static Location addrToPoint(Context context, String addr) {
        Location location = new Location("");
        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocationName(addr,3);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(addresses != null) {
            for(int i = 0 ; i < addresses.size() ; i++) {
                Address latlng = addresses.get(i);
                location.setLatitude(latlng.getLatitude()); // 위도
                location.setLongitude(latlng.getLongitude());   // 경도
            }
        }
        return location;
    }
}

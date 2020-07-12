package com.example.coronamap;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mgoogleMap;
    private ClusterManager<MyItem> clusterManager;
    ArrayList<Clinic> clinics;
    ArrayList<Location> clinic_address;
    Context context = this;
    final String TAG = "LogMainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clinics = (ArrayList<Clinic>)getIntent().getSerializableExtra("clinic");
        clinic_address = (ArrayList<Location>)getIntent().getSerializableExtra("clinic_addr");
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
    }
    // 구글맵 렌더링
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mgoogleMap = googleMap;
        clusterManager = new ClusterManager<>(this, mgoogleMap);

        mgoogleMap.setOnCameraIdleListener(clusterManager);
        mgoogleMap.setOnMarkerClickListener(clusterManager);

        mgoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                Log.d(TAG, "Load");
                // 처음 구글맵이 로딩되면 서울시청으로 화면을 고정
                LatLng latLng = new LatLng(37.564214, 127.001699);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
                mgoogleMap.animateCamera(cameraUpdate);
                // 병원 개수만큼 item 추가
                for(int i = 0 ; i < clinics.size(); i++) {
                    MyItem clinicItem = new MyItem(clinic_address.get(i).getLatitude(), clinic_address.get(i).getLongitude(),
                            clinics.get(i).getName());
                    clusterManager.addItem(clinicItem);
                }
            }
        });
        // 마커를 묶어서 보여줌 (Cluster)
        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
            @Override
            public boolean onClusterClick(Cluster<MyItem> cluster) {
                LatLng latLng = new LatLng(cluster.getPosition().latitude, cluster.getPosition().longitude);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                // 지도가 켜졌을 때, 마커가 찍힌 점으로 이동
                mgoogleMap.moveCamera(cameraUpdate);
                return false;
            }
        });
        // 진료소 세부항목을 보여줌 (InfoWindow)
        mgoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String marker_number = null;
                for (int i = 0; i < clinics.size(); i++) {
                    if (clinics.get(i).findIndex(marker.getTitle()) != null) {
                        marker_number = clinics.get(i).findIndex(marker.getTitle());
                        Log.d(TAG, "marker_number " + marker_number);
                    }
                } // marker title로 clinic을 검색하여 number를 반환
                final int marker_ID_number = Integer.parseInt(marker_number);
                Log.d(TAG, "marker number = " + String.valueOf(marker_ID_number));
                Log.d(TAG, "marker clinic name = " + clinics.get(marker_ID_number).getName());
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("병원정보");
                builder.setMessage(
                        "이름 : " + clinics.get(marker_ID_number - 1).getName() +
                                "\n주소 : " + clinics.get(marker_ID_number - 1).getAddress() +
                                "\n병원전화번호 : " + clinics.get(marker_ID_number - 1).getPhoneNumber() +
                                "\n검체채취가능여부 : " + clinics.get(marker_ID_number - 1).getSample()
                );
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNegativeButton("전화걸기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + clinics.get(Integer.parseInt(String.valueOf(marker_ID_number))).getPhoneNumber())));
                    }
                });
                // 마커 클릭 시 Alert Dialog가 나오도록 설정
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }
}
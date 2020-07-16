package com.example.coronamap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.coronamap.auth.LoginFragment;
import com.example.coronamap.auth.SignUpFragment;
import com.example.coronamap.board.WriteBoardFragment;
import com.example.coronamap.board.ListViewFragment;
import com.example.coronamap.board.MyListViewFragment;
import com.example.coronamap.model.Clinic;
import com.example.coronamap.model.MyItem;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;

//import androidx.viewpager.widget.ViewPager;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentCallback
        , OnMapReadyCallback {
    BottomNavigationView bottomNavigationView;
    NavigationView navigationView;
    LoginFragment loginFragment;
    SignUpFragment signUpFragment;
    WriteBoardFragment bulletinboardFragment;
    ListViewFragment listviewFragment;
    MyListViewFragment myListViewFragment;

    FragmentManager manager;

    public String userID = "";
    int vlFlag = 0;

    Toolbar toolbar;

    private static final int REQUEST_CODE_PERMISSIONS = 1000;
    private GoogleMap mgoogleMap;
    private ClusterManager<MyItem> clusterManager;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    public ArrayList<Clinic> clinics;
    public ArrayList<Location> clinic_address;
    Context context = this;
    final String TAG = "LogMainActivity";

    SupportMapFragment supportMapFragment;
    Fragment fragment;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        clinics = (ArrayList<Clinic>) getIntent().getSerializableExtra("clinic");
        clinic_address = (ArrayList<Location>) getIntent().getSerializableExtra("clinic_addr");


//        SharedPreferences prefs = getSharedPreferences("sFile2", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//        try {
//            editor.putString("text", ObjectSerializer.serialize(clinics));
//            editor.putString("address", ObjectSerializer.serialize(clinic_address));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        editor.commit();
//
//        SharedPreferences sharedPreferences = this.getSharedPreferences("sFile2", Context.MODE_PRIVATE);
//        try {
//            clinics = (ArrayList<Clinic>) ObjectSerializer.deserialize(sharedPreferences.getString("text", ObjectSerializer.serialize(new ArrayList<Clinic>())));
//            clinic_address = (ArrayList<Location>) ObjectSerializer.deserialize(sharedPreferences.getString("address", ObjectSerializer.serialize(new ArrayList<Location>())));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }








        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        fragment = getSupportFragmentManager().findFragmentById(R.id.map);

        frameLayout = findViewById(R.id.main_layout);

        Log.d("oncreate", "oncreate");


        Intent intent = getIntent();
        if (intent != null) {
            userID = intent.getStringExtra("userID");
            if (userID != null) {
                Log.d("userID", userID);
            }

        }


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        bottomNavigationView = findViewById(R.id.bottomNavigationView); //프래그먼트 생성

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //homeFragment = new HomeFragment();
        //loginFragment = new LoginFragment();
        loginFragment = new LoginFragment();
        signUpFragment = new SignUpFragment();
        listviewFragment = new ListViewFragment();
        bulletinboardFragment = new WriteBoardFragment();
        myListViewFragment = new MyListViewFragment();


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int itemId = menuItem.getItemId();
                switch (itemId) {
                    case R.id.tab1: {
                        onFragmentSelected(0, null);
                        break;
                    }
                    case R.id.tab2: {
                        onFragmentSelected(1, null);
                        break;
                    }
                    case R.id.tab3: {
                        onFragmentSelected(2, null);
                        break;
                    }
                }
                return true;
            }
        });


        onFragmentSelected(0, null);
    }

    @Override
    public void onFragmentSelected(int position, Bundle bundle) {
        manager = getSupportFragmentManager();
        if (position == 0) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.show(fragment);
            ft.commit();
            frameLayout.setVisibility(View.INVISIBLE);
            //manager.beginTransaction().replace(R.id.main_layout, homeFragment).commit();
            //viewPager.setCurrentItem(0);
            toolbar.setTitle("코로나 클리닉");
        } else if (position == 1) {
            frameLayout.setVisibility(View.VISIBLE);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.hide(fragment);
            ft.commit();
            manager.beginTransaction().replace(R.id.main_layout, bulletinboardFragment).commit();
            //viewPager.setCurrentItem(1);
            toolbar.setTitle("게시판 작성");
        } else if (position == 2) {
            frameLayout.setVisibility(View.VISIBLE);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.hide(fragment);
            ft.commit();
            manager.beginTransaction().replace(R.id.main_layout, listviewFragment).commit();
            //viewPager.setCurrentItem(2);
            toolbar.setTitle("게시판");
        } else if (position == 3) {
            frameLayout.setVisibility(View.VISIBLE);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.hide(fragment);
            ft.commit();
            manager.beginTransaction().replace(R.id.main_layout, loginFragment).commit();
            //viewPager.setCurrentItem(3);
            toolbar.setTitle("로그인");
        } else if (position == 4) {
            frameLayout.setVisibility(View.VISIBLE);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.hide(fragment);
            ft.commit();
            manager.beginTransaction().replace(R.id.main_layout, signUpFragment).commit();
            //viewPager.setCurrentItem(4);
            toolbar.setTitle("회원가입");
        } else if (position == 5) {
            frameLayout.setVisibility(View.VISIBLE);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.hide(fragment);
            ft.commit();
            manager.beginTransaction().replace(R.id.main_layout, myListViewFragment).commit();
            toolbar.setTitle("My 게시판");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatemetWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav1) {
            Toast.makeText(this, "첫번째 메뉴 선택됨.", Toast.LENGTH_LONG).show();
            onFragmentSelected(3, null);

        } else if (id == R.id.nav2) {
            Toast.makeText(this, "두번째 메뉴 선택됨.", Toast.LENGTH_LONG).show();
            onFragmentSelected(4, null);
        } else if (id == R.id.nav3) {
            Toast.makeText(this, "세번째 메뉴 선택됨.", Toast.LENGTH_LONG).show();
            onFragmentSelected(5, null);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_layout, fragment).commit();      // Fragment로 사용할 MainActivity내의 layout공간을 선택합니다.
    }

    public FragmentManager getSupportFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        return fragmentManager;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // 구글맵 렌더링

        mgoogleMap = googleMap;
        clusterManager = new ClusterManager<>(this, mgoogleMap);

        mgoogleMap.setOnCameraIdleListener(clusterManager);
        mgoogleMap.setOnMarkerClickListener(clusterManager);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//
//        }
//        mgoogleMap.setMyLocationEnabled(true);
        mgoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

        mgoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                Log.d(TAG, "Load");

                // 처음 구글맵이 로딩되면 서울시청으로 화면을 고정
                LatLng latLng = new LatLng(37.564214, 127.001699);

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
                mgoogleMap.animateCamera(cameraUpdate);
                // 병원 개수만큼 item 추가
                for (int i = 0; i < clinics.size(); i++) {
                    MyItem clinicItem = new MyItem(clinic_address.get(i).getLatitude(), clinic_address.get(i).getLongitude(), clinics.get(i).getName());
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
//                Log.d(TAG, "marker number = " + String.valueOf(marker_ID_number));
//                Log.d(TAG, "marker clinic name = " + clinics.get(marker_ID_number).getName());
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
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + clinics.get(Integer.parseInt(String.valueOf(marker_ID_number - 1))).getPhoneNumber())));
                    }
                });
                // 마커 클릭 시 Alert Dialog가 나오도록 설정
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }


//    @Override
//    public void onMyLocationChange(Location location) {
//        if (location != null) {
//            LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
//            mgoogleMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
//            mgoogleMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
//        }
//    }
}

package com.example.project_nanlina;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.project_nanlina.login.ActivityLogIn;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;



public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, FragmentCallback {

    private FirebaseAuth mFirebaseAuth;
    private GoogleMap googleMap;

    Fragment fragment1;
    Fragment2 fragment2;
    Fragment fragment3;

    DrawerLayout drawer;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        바로가기 메뉴
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        drawer.setAlpha(0.85f); //배경이미지 투명도 주기
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toogle);
        toogle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        fragment1 = new Fragment();
//        fragment2 = new Fragment2();
//        fragment3 = new Fragment();

        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment1).commit();


        mFirebaseAuth = FirebaseAuth.getInstance();

        Button btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로그아웃하기
                mFirebaseAuth.signOut();

                Intent intent = new Intent(MainActivity.this, ActivityLogIn.class);
                startActivity(intent);
                finish();
            }
        });


         //회원 탈퇴
//         mFirebaseAuth.getCurrentUser().delete();


    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        this.googleMap = googleMap;
        //35.154794630480595, 126.92353898107554 1번 주차장
        //카메라를 주차장 1번으로 맞추고 확대 완료

        //카메라를 현재 위치로 고정
        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("latitude", 0);
        double longitude = intent.getDoubleExtra("longitude", 0);
        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(19));

        //마커 생성 코드
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("산수1동 제3주차장");
        googleMap.addMarker(markerOptions);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu1) {
            onFragmentSelected(0, null);
        }
//        else if (id == R.id.menu2) {
//            onFragmentSelected(1, null);
//        }
//        else if (id == R.id.menu3) {
//            onFragmentSelected(2, null);
//        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onFragmentSelected(int position, Bundle bundle) {
        Fragment curFragment = null;

        if (position == 0) {
            curFragment = fragment1;
        }
//        else if (position == 1) {
//            curFragment = fragment2;
//        }
//        else if (position == 2) {
//            curFragment = fragment3;
//        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container, curFragment).commit();
    }

}
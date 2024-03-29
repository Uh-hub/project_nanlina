package com.example.project_nanlina.controller;

//import androidx.appcompat.app.ActionBar;
//import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.view.GravityCompat;
//import androidx.drawerlayout.widget.DrawerLayout;
//import androidx.fragment.app.Fragment;

import android.Manifest;
//import android.app.Activity;
        import android.content.Intent;
        import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
//import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
        import android.location.Location;
        import android.os.Build;
import android.os.Bundle;
        import android.util.Log;
import android.view.View;
        import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_nanlina.databinding.ActivityMainBinding;
import com.example.project_nanlina.databinding.ParkingInfoBinding;
import com.example.project_nanlina.model.DatabaseHelper;
import com.example.project_nanlina.controller.parking.OnPMItemClickListener;
import com.example.project_nanlina.R;
import com.example.project_nanlina.controller.login.ActivityLogIn;
import com.example.project_nanlina.model.PMItem;
import com.example.project_nanlina.controller.parking.PMListAdapter;
import com.example.project_nanlina.controller.parking.ParkingInfo;
        import com.google.firebase.auth.FirebaseAuth;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
        import com.skt.Tmap.TMapView;

//유진아 혹시 이 코드를 나중에 볼까봐 적어
//구글맵을 티맵으로 바꿨고 길찾기 경로랑 사용자 현재 위치 나오게 하는 기능 구현했어!
//어차피 깃허브에 기록 있으니까 너가 쓴 코드는 지웠어ㅠㅠ 티맵 인증키 쉽게 받을 수 있으니까 받아서 너걸로 바꾸면 돼
public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    private FirebaseAuth mFirebaseAuth;

    // SQLlite 연동
    private PMListAdapter pmAdapter;
    SQLiteDatabase database;

    TMapView tMapView = null; // T map View
    TMapGpsManager tMapGPS = null; // GPS 사용

    private ActivityMainBinding binding;

    @Override
    public void onLocationChange(Location location) {
        tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        tMapView.setCenterPoint(location.getLongitude(), location.getLatitude());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tMapView = new TMapView(this); // T map View
        binding.linearLayoutTmap.addView(tMapView);
        tMapView.setSKTMapApiKey("l7xxd4933c1088df4195a25e3e31de55d514");

        tMapGPS = new TMapGpsManager(this); // GPS 사용

        tMapView.setZoomLevel(17);
        tMapView.setIconVisibility(true);
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN); // 한국어 사용

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        tMapGPS.setMinTime(1000);
        tMapGPS.setMinDistance(10);
        tMapGPS.setProvider(tMapGPS.NETWORK_PROVIDER);

        tMapGPS.OpenGps();

        tMapView.setTrackingMode(true);
        tMapView.setSightVisible(true);

////////////////////////////////////////////////////////////////////////////////////////////////////
        // 로그인 기능
        mFirebaseAuth = FirebaseAuth.getInstance();

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로그아웃하기
                mFirebaseAuth.signOut();
                Toast.makeText(MainActivity.this, "로그아웃되었습니다", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, ActivityLogIn.class);
                startActivity(intent);
                finish();
            }

        });

        //회원 탈퇴
//         mFirebaseAuth.getCurrentUser().delete();

////////////////////////////////////////////////////////////////////////////////////////////

        binding.button1.setSelected(true);   // 전체 버튼 눌린 상태로 유지
        binding.button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.button1.setSelected(true);
                binding.button2.setSelected(false);
                binding.button3.setSelected(false);
            }
        });

        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.button1.setSelected(false);
                binding.button2.setSelected(true);
                binding.button3.setSelected(false);
            }
        });

        binding.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.button1.setSelected(false);
                binding.button2.setSelected(false);
                binding.button3.setSelected(true);
            }
        });


        ////// 주차장 정보 리사이클러뷰
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerView.setLayoutManager(layoutManager);

        openDB();

        // 맨 처음 초기화 데이터 보여주기 (select) - 주차장 이름, 주소, 사진, 위도, 경도는 sqllite로 가져오기
        if (database != null) {
            String tableName = "pm_info";
            String query = "select id, name, address, latitude, longitude, photo, kickboard, bicycle from " + tableName;
            Cursor cursor = database.rawQuery(query, null);
            Log.v("test", "조회된 데이터 수 : " + cursor.getCount());

            pmAdapter = new PMListAdapter();

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                String id = cursor.getString(0);
                String name = cursor.getString(1);
                String address = cursor.getString(2);
                String latitude = cursor.getString(3);
                String longitude = cursor.getString(4);
                String photo = cursor.getString(5);
                String kickboard = cursor.getString(6);
                String bicycle = cursor.getString(7);

                // 주차장 위치에 마커 띄우기
                TMapMarkerItem mapMarkerItem = new TMapMarkerItem();
                TMapPoint tMapPoint = new TMapPoint(Double.parseDouble(latitude), Double.parseDouble(longitude));
                Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.poi_dot);
                mapMarkerItem.setIcon(bitmap);
                mapMarkerItem.setPosition(0.5f, 1.0f);
                mapMarkerItem.setTMapPoint(tMapPoint);
                tMapView.addMarkerItem("markerItem" + i, mapMarkerItem);

                pmAdapter.addItem(new PMItem(id, name, address, latitude, longitude, photo, kickboard, bicycle));

                pmAdapter.setOnItemClickListener(new OnPMItemClickListener() {
                    @Override
                    public void onItemClick(PMListAdapter.ViewHolder holder, View view, int position) {
                        PMItem item = pmAdapter.getItem(position);

                        Intent intent = new Intent(getApplicationContext(), ParkingInfo.class);
                        intent.putExtra("id", item.getId());
                        intent.putExtra("name", item.getName());
                        intent.putExtra("address", item.getAddress());
                        intent.putExtra("latitude", item.getLatitude());
                        intent.putExtra("longitude", item.getLongitude());
                        intent.putExtra("photo", item.getPhoto());
                        intent.putExtra("kickboard", item.getKickboard());
                        intent.putExtra("bicycle", item.getBicycle());

                        startActivity(intent);
//                    Toast.makeText(getApplicationContext(), "아이템 선택됨: " + item.getName(), Toast.LENGTH_LONG).show();
                    }
                });
            }
            cursor.close();
        } else {
            Log.e("test", "selectData() db없음.");
        }

        binding.recyclerView.setAdapter(pmAdapter);
    }


    public void openDB() {
        Log.v("test", "openDB() 실행");
        DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
        database = helper.getWritableDatabase();


        if (database != null) {
            Log.v("test", "DB 열기 성공!");
        } else {
            Log.e("test", "DB 열기 실패!");
        }
    }
}


package com.example.project_nanlina;

import androidx.annotation.NonNull;
//import androidx.appcompat.app.ActionBar;
//import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
//import androidx.core.view.GravityCompat;
//import androidx.drawerlayout.widget.DrawerLayout;
//import androidx.fragment.app.Fragment;

import android.Manifest;
//import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
//import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_nanlina.login.ActivityLogIn;
import com.example.project_nanlina.parking.PMItem;
import com.example.project_nanlina.parking.PMListAdapter;
import com.example.project_nanlina.parking.ParkingInfo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//유진아 혹시 이 코드를 나중에 볼까봐 적어
//구글맵을 티맵으로 바꿨고 길찾기 경로랑 사용자 현재 위치 나오게 하는 기능 구현했어!
//어차피 깃허브에 기록 있으니까 너가 쓴 코드는 지웠어ㅠㅠ 티맵 인증키 쉽게 받을 수 있으니까 받아서 너걸로 바꾸면 돼
public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    private FirebaseAuth mFirebaseAuth;
    private View mLayout; // Snackbar 사용하기 위해 View 필요

    // MySQL 연동
    private static String IP_ADDRESS = "10.0.2.2";
    private static String TAG2 = "phptest";
    private String mJsonString;

    // SQLlite 연동
    private RecyclerView recyclerView;
    private PMListAdapter pmAdapter;
    SQLiteDatabase database;

    TMapView tMapView = null; // T map View
    TMapGpsManager tMapGPS = null; // GPS 사용
    TMapData tMapData = null;
    TMapPoint start = null;

    public static double pLatitude;
    public static double pLongitude;

    @Override
    public void onLocationChange(Location location) {
        tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        tMapView.setCenterPoint(location.getLongitude(), location.getLatitude());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(this); // T map View
        linearLayout.addView(tMapView);
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

        ImageView btn_logout = findViewById(R.id.btn_logout);

        btn_logout.setOnClickListener(new View.OnClickListener() {
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
        // 화면 하단 주차장 정보
        recyclerView = findViewById(R.id.recyclerView);

        // 상단 분류 버튼 (전체, 전동킥보드, 전기자전거)
        Button button1 = findViewById(R.id.button1);   // 전체
        Button button2 = findViewById(R.id.button2);   // 전동킥보드
        Button button3 = findViewById(R.id.button3);   // 전기자전거

        button1.setSelected(true);   // 전체 버튼 눌린 상태로 유지
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button1.setSelected(true);
                button2.setSelected(false);
                button3.setSelected(false);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button1.setSelected(false);
                button2.setSelected(true);
                button3.setSelected(false);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button1.setSelected(false);
                button2.setSelected(false);
                button3.setSelected(true);
            }
        });


        ////// 주차장 정보 리사이클러뷰
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

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

        recyclerView.setAdapter(pmAdapter);
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


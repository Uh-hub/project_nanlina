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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_nanlina.login.ActivityLogIn;
import com.example.project_nanlina.parking.PMItem;
import com.example.project_nanlina.parking.PMItem2;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

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


public class ConnectionTest extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;


    //앱을 실행하기 위해 필요한 퍼미션 정의
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    Location mCurrentLocation;
    LatLng currentPosition;


    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;

    private View mLayout; // Snackbar 사용하기 위해 View 필요


    // MySQL 연동
    private static String IP_ADDRESS = "10.0.2.2";
    private static String TAG2 = "phptest";

    private String mJsonString;


    // SQLlite 연동
    private RecyclerView recyclerView;
    private PMListAdapter pmAdapter;
    SQLiteDatabase database;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        setContentView(R.layout.connection_test);


////////////////////////////////////////////////////////////////////////////////////////////////////
        // 로그인 기능
        mFirebaseAuth = FirebaseAuth.getInstance();

        ImageView btn_logout = findViewById(R.id.btn_logout);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로그아웃하기
                mFirebaseAuth.signOut();
                Toast.makeText(ConnectionTest.this, "로그아웃되었습니다", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ConnectionTest.this, ActivityLogIn.class);
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


//        // 맨 처음 초기화 데이터 보여주기 (select) - 주차장 이름, 주소, 사진, 위도, 경도는 sqllite로 가져오기
//        if (database != null) {
//            String tableName = "pm_info";
//            String query = "select id, name, address, latitude, longitude, photo from " + tableName;
//            Cursor cursor = database.rawQuery(query, null);
//            Log.v(TAG2, "조회된 데이터 수 : " + cursor.getCount());
//
//            pmAdapter = new PMListAdapter();
//
//            //위도 경도 정보 넘기기 위한 array 생성
//            ArrayList latitudeList = new ArrayList<Double>();
//            ArrayList longitudeList = new ArrayList<Double>();
//
//
//            for (int i = 0; i < cursor.getCount(); i++) {
//                cursor.moveToNext();
//                int id = cursor.getInt(0);
//                String name = cursor.getString(1);
//                String address = cursor.getString(2);
//                double latitude = cursor.getDouble(3);
//                double longitude = cursor.getDouble(4);
//                String photo = cursor.getString(5);
//
//
//                pmAdapter.addItem(new PMItem(name, address, photo));
//
//                pmAdapter.setOnItemClickListener(new OnPMItemClickListener() {
//                    @Override
//                    public void onItemClick(PMListAdapter.ViewHolder holder, View view, int position) {
//                        PMItem item = pmAdapter.getItem(position);
//
//                        Intent intent = new Intent(getApplicationContext(), ParkingInfo.class);
//                        intent.putExtra("name", item.getName());
//                        intent.putExtra("address", item.getAddress());
//                        intent.putExtra("photo", item.getPhoto());
////                        intent.putExtra("kickboard", item.getKickboard());
////                        intent.putExtra("bicycle", item.getBicycle());
////                        intent.putExtra("number", item.getNumber());
//
//                        startActivity(intent);
////                    Toast.makeText(getApplicationContext(), "아이템 선택됨: " + item.getName(), Toast.LENGTH_LONG).show();
//                    }
//                });
//            }
//            cursor.close();
//        }
//        else {
//            Log.e(TAG2, "selectData() db없음.");
//        }

        recyclerView.setAdapter(pmAdapter);


        ConnectionTest.GetData task = new ConnectionTest.GetData();
        task.execute("http://" + IP_ADDRESS + "/getjson.php", "");
    }


    public void onMapReady(GoogleMap googleMap) {

//        // 맨 처음 초기화 데이터 보여주기 (select) - 주차장 이름, 주소, 사진, 위도, 경도는 sqllite로 가져오기
//        if (database != null) {
//            String tableName = "pm_info";
//            String query = "select id, name, address, latitude, longitude, photo from " + tableName;
//            Cursor cursor = database.rawQuery(query, null);
//            Log.v(TAG2, "조회된 데이터 수 : " + cursor.getCount());
//
//            pmAdapter = new PMListAdapter();
//
//
//            for (int i = 0; i < cursor.getCount(); i++) {
//                cursor.moveToNext();
//                int id = cursor.getInt(0);
//                String name = cursor.getString(1);
//                String address = cursor.getString(2);
//                double latitude = cursor.getDouble(3);
//                double longitude = cursor.getDouble(4);
//                String photo = cursor.getString(5);
//
//
//            }
//        }
    }


    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(ConnectionTest.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG2, "response - " + result);

            if (result == null) {
                Log.d("error", errorString);
            } else {
                mJsonString = result;
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = params[1];

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG2, "response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();
            } catch (Exception e) {
                Log.d(TAG2, "GetData : Error", e);
                errorString = e.toString();

                return null;
            }
        }
    }

    private void showResult() {
        String TAG_JSON = "webnautes";  // 민서
//        String TAG_JSON = "noa_on_air";   // 유진
        String TAG_NAME = "name";
        String TAG_ADDRESS = "address";
        String TAG_IMAGE = "photo";
        String TAG_KICKBOARD = "kickboard";
        String TAG_ID = "id";
//        String TAG_LATITUDE = "latitude";
//        String TAG_LONGITUDE = "longitude";
        String TAG_GCOOTER = "gcooter";
        String TAG_DEER = "deer";
        String TAG_BEAM = "beam";
        String TAG_TALANG = "talang";
        String TAG_BICYCLE = "bicycle";

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            pmAdapter = new PMListAdapter();

            for (int i = 1; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);

                String name = item.getString(TAG_NAME);
                String address = item.getString(TAG_ADDRESS);
                String photo = item.getString(TAG_IMAGE);
                String kickboard = item.getString(TAG_KICKBOARD);
                String bicycle = item.getString(TAG_BICYCLE);
//                double latitude = item.getDouble(TAG_LATITUDE);
//                double longitude = item.getDouble(TAG_LONGITUDE);
                String gcooter = item.getString(TAG_GCOOTER);
                String deer = item.getString(TAG_DEER);
                String beam = item.getString(TAG_BEAM);
                String talang = item.getString(TAG_TALANG);
                String id = item.getString(TAG_ID);


                int number = Integer.parseInt(kickboard.replaceAll("[^0-9]",""))
                        + Integer.parseInt(bicycle.replaceAll("[^0-9]",""));
                String stNumber = Integer.toString(number);

//                int kickboard2 = Integer.parseInt(kickboard.replaceAll("[^0-9]",""));
//                int bicycle2 = Integer.parseInt(bicycle.replaceAll("[^0-9]",""));

                pmAdapter.addItem(new PMItem(name, address, photo, kickboard, bicycle, stNumber));

                pmAdapter.setOnItemClickListener(new OnPMItemClickListener() {
                    @Override
                    public void onItemClick(PMListAdapter.ViewHolder holder, View view, int position) {
                        PMItem item = pmAdapter.getItem(position);

                        Intent intent = new Intent(getApplicationContext(), ParkingInfo.class);
                        intent.putExtra("name", item.getName());
                        intent.putExtra("address", item.getAddress());
                        intent.putExtra("photo", item.getPhoto());
                        intent.putExtra("kickboard", item.getKickboard());
                        intent.putExtra("bicycle", item.getBicycle());
                        intent.putExtra("number", item.getNumber());

                        startActivity(intent);
//                    Toast.makeText(getApplicationContext(), "아이템 선택됨: " + item.getName(), Toast.LENGTH_LONG).show();
                    }
                });

            }
            recyclerView.setAdapter(pmAdapter);
        } catch (JSONException e) {
            Log.d(TAG2, "showResult : ", e);
        }
    }

    public void openDB() {
        Log.v(TAG2, "openDB() 실행");
        DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
        database = helper.getWritableDatabase();


        if (database != null) {
            Log.v(TAG2, "DB 열기 성공!");
        } else {
            Log.e(TAG2, "DB 열기 실패!");
        }
    }
}
//    pulic ArrayList<String> user_info(String user_id, String user_latitude, String user_longitude){
//
//}


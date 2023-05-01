package com.example.project_nanlina.controller.qrcode;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_nanlina.controller.parking.ParkingInfo;
import com.example.project_nanlina.databinding.ActivityInuseBinding;
import com.example.project_nanlina.view.ArriveParking;
import com.example.project_nanlina.view.FinishActivity;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

public class ActivityInUse extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    TMapView tMapView = null; // T map View
    TMapGpsManager tMapGPS = null; // GPS 사용
    TMapData tMapData = null;
    TMapPoint start = null;

    private ActivityInuseBinding binding;   // 뷰 바인딩

    @Override
    public void onLocationChange(Location location) {
        tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        tMapView.setCenterPoint(location.getLongitude(), location.getLatitude());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInuseBinding.inflate(getLayoutInflater());
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                start = tMapView.getCenterPoint();
                drawPath();
            }
        }, 7000);  // 가끔 로딩이 늦어지면 시작 장소가 틀리게 설정될 수 있음


        // 도착 버튼 눌렀을 때
        binding.btArrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 도착 지점의 위도, 경도
                TMapPoint location = tMapGPS.getLocation();
                double lat1 = location.getLatitude();
                double lng1 = location.getLongitude();

                Location location1 = new Location("point A");
                location1.setLatitude(lat1);
                location1.setLongitude(lng1);

                Location location2 = new Location("point B");
                location2.setLatitude(ParkingInfo.pLatitude);
                location2.setLongitude(ParkingInfo.pLongitude);

                double distance = location1.distanceTo(location2);

                if (distance < 500) {
                    Intent intent = new Intent(ActivityInUse.this, ArriveParking.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(ActivityInUse.this, "주차장으로 조금 더 가까이 가주세요", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void drawPath() {
        TMapPoint end = new TMapPoint(ParkingInfo.pLatitude, ParkingInfo.pLongitude);
        tMapData = new TMapData();
        tMapData.findPathData(start, end, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine tMapPolyLine) {
                tMapPolyLine.setLineColor(Color.BLUE);
                tMapView.addTMapPath(tMapPolyLine);
            }
        });
    }
}

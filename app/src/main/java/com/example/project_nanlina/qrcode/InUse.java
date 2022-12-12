package com.example.project_nanlina.qrcode;

import android.Manifest;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_nanlina.R;
import com.example.project_nanlina.parking.ParkingInfo;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

public class InUse extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    TMapView tMapView = null; // T map View
    TMapGpsManager tMapGPS = null; // GPS 사용
    TMapData tMapData = null;
    TMapPoint start = null;

    @Override
    public void onLocationChange(Location location) {
        tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        tMapView.setCenterPoint(location.getLongitude(), location.getLatitude());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inuse);

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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                start = tMapView.getCenterPoint();
                drawPath();
            }
        }, 7000);  // 가끔 로딩이 늦어지면 시작 장소가 틀리게 설정될 수 있음
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

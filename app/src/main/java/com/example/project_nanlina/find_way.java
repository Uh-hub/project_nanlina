//package com.example.project_nanlina;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.graphics.Color;
//import android.os.Bundle;
////경로 길찾기
//public class find_way extends AppCompatActivity {
//
//
//    TMapPoint tMapPointStart = new TMapPoint(37.570841, 126.985302); // SKT타워(출발지)
//    TMapPoint tMapPointEnd = new TMapPoint(37.551135, 126.988205); // N서울타워(목적지)
//
//try {
//        TMapPolyLine tMapPolyLine = new TMapData().findPathData(tMapPointStart, tMapPointEnd);
//        tMapPolyLine.setLineColor(Color.BLUE);
//        tMapPolyLine.setLineWidth(2);
//        tMapView.addTMapPolyLine("Line1", tMapPolyLine);
//
//    }catch(Exception e) {
//        e.printStackTrace();
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_find_way);
//    }
//}
//

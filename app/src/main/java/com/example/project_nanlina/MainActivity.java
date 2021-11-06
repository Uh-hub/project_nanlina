package com.example.project_nanlina;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.project_nanlina.login.ActivityLogIn;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;



public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FirebaseAuth mFirebaseAuth;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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
        LatLng latLng = new LatLng(35.154794630480595, 126.92353898107554);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(19));
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("산수1동 제3주차장");
        googleMap.addMarker(markerOptions);

    }

}
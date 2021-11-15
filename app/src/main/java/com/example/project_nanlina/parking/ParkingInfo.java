package com.example.project_nanlina.parking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_nanlina.R;
import com.example.project_nanlina.qrcode.QRCodeReader;

public class ParkingInfo extends AppCompatActivity {

    private Button qrcode;
    private TextView tvName;
    private TextView tvAddress;
    private TextView tvKickboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parking_info);

        qrcode = findViewById(R.id.qrcode);

        // qrcode 버튼 화면 이동
        qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QRCodeReader.class);
                startActivity(intent);
            }
        });


        // 주차장 이름, 주소, 킥보드 개수 가져오기
        String name = getIntent().getStringExtra("name");
        String address = getIntent().getStringExtra("address");
        String kickboard = getIntent().getStringExtra("kickboard");

        tvName = findViewById(R.id.tvName);
        tvAddress = findViewById(R.id.tvAddress);
        tvKickboard = findViewById(R.id.tvKickboard);

        tvName.setText(name);
        tvAddress.setText(address);
        tvKickboard.setText(kickboard);
    }
}

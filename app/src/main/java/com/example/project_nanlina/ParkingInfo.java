package com.example.project_nanlina;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ParkingInfo extends AppCompatActivity {

    private Button qrcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parking_info);

        qrcode = findViewById(R.id.qrcode);

        // qrcode 버튼 화면 이동
        qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QRCodeReader1.class);
                startActivity(intent);
            }
        });
    }
}

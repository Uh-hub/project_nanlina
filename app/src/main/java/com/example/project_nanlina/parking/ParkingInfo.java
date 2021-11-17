package com.example.project_nanlina.parking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.project_nanlina.R;
import com.example.project_nanlina.qrcode.QRCodeReader;

public class ParkingInfo extends AppCompatActivity {

    private Button qrcode;
    private TextView tvName;
    private TextView tvAddress;
    private TextView tvKickboard;
    private TextView tvBicycle;
    private ImageView imageView;
    private TextView tvVacant;

    private ImageView backButton;

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


        // 주차장 데이터 가져오기
        String name = getIntent().getStringExtra("name");
        String address = getIntent().getStringExtra("address");
        String photo = getIntent().getStringExtra("photo");
        String kickboard = getIntent().getStringExtra("kickboard");
        String bicycle = getIntent().getStringExtra("bicycle");
        String number = getIntent().getStringExtra("number");

        tvName = findViewById(R.id.tvName);
        tvAddress = findViewById(R.id.tvAddress);
        imageView = findViewById(R.id.imageView);
        tvKickboard = findViewById(R.id.tvKickboard);
        tvBicycle = findViewById(R.id.tvBicycle);
        tvVacant = findViewById(R.id.tvVacant);

        tvName.setText(name);
        tvAddress.setText(address);
        tvKickboard.setText(kickboard);
        tvBicycle.setText(bicycle);


        // 주차 가능 자리 계산하기 (10대 주차 가능한걸로 가정)
        int number2 = Integer.parseInt(number.replaceAll("[^0-9]",""));
        String stVacant = Integer.toString(10 - number2);

        tvVacant.setText(stVacant+"대");


        // Glide로 이미지 표시하기
        // Log.v("image url", photo);
        Glide.with(this).load(photo).error(R.drawable.main_logo).into(imageView);


        // 뒤로가기 버튼 구현
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}

package com.example.project_nanlina.controller.parking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.project_nanlina.controller.FindRoad;
import com.example.project_nanlina.R;
import com.example.project_nanlina.controller.qrcode.PMIdInput;
import com.example.project_nanlina.controller.qrcode.QRCodeReader;
import com.example.project_nanlina.databinding.ParkingInfoBinding;

public class ParkingInfo extends AppCompatActivity {

    public static double pLatitude;
    public static double pLongitude;
    public static String id;

    private ParkingInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ParkingInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // qrcode 버튼 화면 이동
        binding.qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QRCodeReader.class);
                startActivity(intent);
            }
        });

        // 길찾기 버튼 화면 이동
        binding.findRoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FindRoad.class);
                startActivity(intent);
            }
        });

        // 주차장 데이터 가져오기
        String name = getIntent().getStringExtra("name");
        String address = getIntent().getStringExtra("address");
        String photo = getIntent().getStringExtra("photo");
        String kickboard = getIntent().getStringExtra("kickboard");
        String bicycle = getIntent().getStringExtra("bicycle");
        int number = Integer.parseInt(kickboard) + Integer.parseInt(bicycle);

        id = getIntent().getStringExtra("id");
        pLatitude = Double.parseDouble(getIntent().getStringExtra("latitude"));
        pLongitude = Double.parseDouble(getIntent().getStringExtra("longitude"));

        binding.tvName.setText(name);
        binding.tvAddress.setText(address);
        binding.tvKickboard.setText(kickboard + "대");
        binding.tvBicycle.setText(bicycle + "대");

        Intent intent = new Intent(getApplicationContext(), PMIdInput.class);
        intent.putExtra("name", name);

        // 주차 가능 자리 계산하기 (10대 주차 가능한걸로 가정)
//        int number2 = Integer.parseInt(number.replaceAll("[^0-9]",""));
        String stVacant = Integer.toString(10 - number);
        binding.tvVacant.setText(stVacant + "대");

        // Glide로 이미지 표시하기
//         Log.v("image url", photo);
        Glide.with(this).load(photo).error(R.drawable.main_logo).into(binding.imageView);


        // 뒤로가기 버튼 구현
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}

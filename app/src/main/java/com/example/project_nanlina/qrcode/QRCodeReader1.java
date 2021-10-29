package com.example.project_nanlina.qrcode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_nanlina.R;

public class QRCodeReader1 extends AppCompatActivity {

    private Button qrcode2;
    private Button input1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_reader1);

        qrcode2 = findViewById(R.id.qrcode2);
        input1 = findViewById(R.id.input1);

        // QR 코드 버튼 클릭시 화면 이동
        qrcode2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QRCodeReader2.class);
                startActivity(intent);
            }
        });

        // 직접 입력 버튼 클릭시 화면 이동
        input1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PMIdInput.class);
                startActivity(intent);
            }
        });
    }
}

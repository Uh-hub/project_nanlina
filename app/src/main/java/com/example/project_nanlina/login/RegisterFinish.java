package com.example.project_nanlina.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_nanlina.MainActivity;
import com.example.project_nanlina.R;

public class RegisterFinish extends AppCompatActivity {

    Button btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_finish);


        btnFinish = findViewById(R.id.btn_finish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityLogIn.class);
                startActivity(intent);
            }
        });
    }
}

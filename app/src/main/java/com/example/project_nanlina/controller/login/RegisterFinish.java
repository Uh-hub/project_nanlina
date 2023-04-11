package com.example.project_nanlina.controller.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_nanlina.databinding.RegisterFinishBinding;

public class RegisterFinish extends AppCompatActivity {

    private RegisterFinishBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RegisterFinishBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityLogIn.class);
                startActivity(intent);
            }
        });
    }
}

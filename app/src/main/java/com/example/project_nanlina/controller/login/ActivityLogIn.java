package com.example.project_nanlina.controller.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project_nanlina.R;
import com.example.project_nanlina.controller.MainActivity;
import com.example.project_nanlina.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityLogIn extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;      // 파이어베이스 인증
    private DatabaseReference mDatabaseRef;  // 실시간 데이터베이스

    private ActivityLoginBinding binding;   // 뷰 바인딩

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("project_NanlinA");

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로그인 요청
                String strEmail = binding.etEmail.getText().toString();
                String strPwd = binding.etPwd.getText().toString();

                mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(ActivityLogIn.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공(허용 화면으로 이동)
                            Intent intent = new Intent(ActivityLogIn.this, MainActivity.class);
                            startActivity(intent);
                            finish();   // 현재 액티비티 파괴
                        }
                        else {
                            Toast.makeText(ActivityLogIn.this, "로그인에 실패하셨습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 화면으로 이동
                Intent intent = new Intent(ActivityLogIn.this, ActivityRegister.class);
                startActivity(intent);
            }
        });
    }
}

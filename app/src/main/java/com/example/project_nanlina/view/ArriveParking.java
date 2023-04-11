package com.example.project_nanlina.view;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_nanlina.databinding.ArriveParkingBinding;

public class ArriveParking extends AppCompatActivity {

    // Frame 단위로 이미지를 바꿔서 그려주는 Drawable 객체
    AnimationDrawable ani1;
    AnimationDrawable ani2;
    AnimationDrawable ani3;

    private ArriveParkingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ArriveParkingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ImageView에 src속성으로 설정된 이미지를 Drawable객체로 얻어오기
        ani1 = (AnimationDrawable) binding.imageView1.getDrawable();
        ani2 = (AnimationDrawable) binding.imageView2.getDrawable();
        ani3 = (AnimationDrawable) binding.imageView3.getDrawable();

        TranslateAnimation anim = new TranslateAnimation
                (-500,   // fromXDelta
                        0,  // toXDelta
                        0,    // fromYDelta
                        0);// toYDelta
        anim.setDuration(4000);
        anim.setRepeatCount(Animation.INFINITE);
        binding.imageView4.startAnimation(anim);

        ani1.start();
        ani2.start();
        ani3.start();

        binding.btnFinish2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FinishActivity.class);
                startActivity(intent);
            }
        });
    }
}

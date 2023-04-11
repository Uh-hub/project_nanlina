package com.example.project_nanlina.view;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_nanlina.view.FinishActivity;
import com.example.project_nanlina.R;

public class ArriveParking extends AppCompatActivity {

    ImageView img1;
    ImageView img2;
    ImageView img3;
    ImageView img4;

    // Frame 단위로 이미지를 바꿔서 그려주는 Drawable 객체
    AnimationDrawable ani1;
    AnimationDrawable ani2;
    AnimationDrawable ani3;

    Button finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.arrive_parking);

        img1 = (ImageView) findViewById(R.id.imageView1);
        img2 = (ImageView) findViewById(R.id.imageView2);
        img3 = (ImageView) findViewById(R.id.imageView3);
        img4 = (ImageView) findViewById(R.id.imageView4);

        // ImageView에 src속성으로 설정된 이미지를 Drawable객체로 얻어오기
        ani1 = (AnimationDrawable) img1.getDrawable();
        ani2 = (AnimationDrawable) img2.getDrawable();
        ani3 = (AnimationDrawable) img3.getDrawable();

        TranslateAnimation anim = new TranslateAnimation
                (-500,   // fromXDelta
                        0,  // toXDelta
                        0,    // fromYDelta
                        0);// toYDelta
        anim.setDuration(4000);
        anim.setRepeatCount(Animation.INFINITE);
        img4.startAnimation(anim);

        ani1.start();
        ani2.start();
        ani3.start();


        finish = findViewById(R.id.btn_finish2);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FinishActivity.class);
                startActivity(intent);
            }
        });
    }
}

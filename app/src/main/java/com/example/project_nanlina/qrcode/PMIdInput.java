package com.example.project_nanlina.qrcode;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_nanlina.R;

public class PMIdInput extends AppCompatActivity {

    public static String useID;   // QR 코드에서 읽어온 PM ID!!!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pm_id_input);

        EditText editText = findViewById(R.id.inputPMID);
        Button button2 = findViewById(R.id.button2);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // PM ID를 입력하지 않았을때
                if(editText.getText().toString().length() == 0) {
                    Toast.makeText(PMIdInput.this, "ID를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else {
                    useID = editText.getText().toString();
                    ///////// 이용중 화면으로 이동!!!
               }
            }
        });
    }
}

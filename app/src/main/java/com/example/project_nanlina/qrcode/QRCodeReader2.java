package com.example.project_nanlina.qrcode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_nanlina.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class QRCodeReader2 extends AppCompatActivity {

    private Button buttonScan;
    private TextView textViewId, textViewResult;

    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_reader2);

        buttonScan = (Button) findViewById(R.id.button);
        textViewId = (TextView) findViewById(R.id.result);
        textViewResult = (TextView) findViewById(R.id.result2);

        qrScan = new IntentIntegrator(this);


        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan.setPrompt("Scanning...");
                qrScan.initiateScan();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            // QR 코드가 없으면
            if (result.getContents() == null) {
                // Toast.makeText(QRCodeReader2.this, "취소!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), QRCodeReader2.class);
                startActivity(intent);
            }
            // QR 코드가 있으면
            else {
                Toast.makeText(QRCodeReader2.this, "스캔완료!", Toast.LENGTH_SHORT).show();

                try {
                    // data를 json으로 변환
                    JSONObject obj = new JSONObject(result.getContents());
                    textViewId.setText(obj.getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    textViewResult.setText(result.getContents());
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

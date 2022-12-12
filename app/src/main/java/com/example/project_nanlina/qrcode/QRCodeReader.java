package com.example.project_nanlina.qrcode;

import static java.sql.DriverManager.println;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project_nanlina.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class QRCodeReader extends AppCompatActivity {

    private Button buttonScan;
    private Button input1;

    private IntentIntegrator qrScan;

    public static String useID;   // QR 코드에서 읽어온 PM ID!!!

    private static String IP_ADDRESS = "10.0.2.2";
    private static String TAG = "phptest";

    String pm_name;
    String pm_type;

    static RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_reader);

        buttonScan = (Button) findViewById(R.id.scan_button);
        input1 = findViewById(R.id.input1);

        qrScan = new IntentIntegrator(this);

        // QR 코드 버튼 클릭시 화면 이동
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan.setPrompt("Scanning...");
                qrScan.initiateScan();
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

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            // QR 코드가 없으면
            if (result.getContents() == null) {
                // Toast.makeText(QRCodeReader2.this, "취소!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), QRCodeReader.class);
                startActivity(intent);
            }
            // QR 코드가 있으면
            else {
                Toast.makeText(QRCodeReader.this, "스캔완료!", Toast.LENGTH_SHORT).show();

                try {
                    // data를 json으로 변환
                    JSONObject obj = new JSONObject(result.getContents());
                    useID = obj.getString("id");
                    //textViewId.setText(obj.getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    //textViewResult.setText(result.getContents());
                }

                ////// 스캔후 이용중 화면으로 이동하는 걸로 변경!!!

                if (useID.equals("Hv4tD64yX8")) {
                    pm_name = "지쿠터";
                    pm_type = "kickboard";
                }
                else if (useID.equals("MeVJcPG73z")) {
                    pm_name = "지쿠터";
                    pm_type = "kickboard";
                }
                else if (useID.equals("FL66gYnwaZ")) {
                    pm_name = "deer";
                    pm_type = "kickboard";
                }
                else if (useID.equals("DZ8GG9qqHb")) {
                    pm_name = "Beam";
                    pm_type = "kickboard";
                }
                else if (useID.equals("Z39gmtOAZ0")) {
                    pm_name = "타랑께";
                    pm_type = "bicycle";
                }

//                makeRequest();
                Intent intent2 = new Intent(getApplicationContext(), InUse.class);
                startActivity(intent2);
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void makeRequest() {
        String url = "http://127.0.0.1:8000/manager/rent/?pid=1&mid=1&latitude=1&longitude=1";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        println("응답 -> " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        println("에러 -> " + error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }
        };
        request.setShouldCache(false);
        requestQueue.add(request);
        println("요청 보냄");
    }
}

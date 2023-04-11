package com.example.project_nanlina.controller.qrcode;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project_nanlina.controller.parking.ParkingInfo;
import com.example.project_nanlina.databinding.QrCodeReaderBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class QRCodeReader extends AppCompatActivity {

    private IntentIntegrator qrScan;

    public static String useID;   // QR 코드에서 읽어온 PM ID!!!

    private static String IP_ADDRESS = "10.0.2.2";
    private static String TAG = "phptest";

    String pm_name;
    String pm_type;

    static RequestQueue requestQueue;
    private QrCodeReaderBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = QrCodeReaderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        qrScan = new IntentIntegrator(this);

        // QR 코드 버튼 클릭시 화면 이동
        binding.scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan.setPrompt("Scanning...");
                qrScan.initiateScan();
            }
        });

        // 직접 입력 버튼 클릭시 화면 이동
        binding.input1.setOnClickListener(new View.OnClickListener() {
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

                makeRequest();
                Intent intent2 = new Intent(getApplicationContext(), InUse.class);
                startActivity(intent2);
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void makeRequest() {
        String url = "http://127.0.0.1:8000/check/";

        // 왜 안되니..
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("error", error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("rid", useID);
                params.put("latitude", Double.toString(35.1629723));
                params.put("longitude", Double.toString(126.9186564));
                return params;
            }
        };
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);


        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("error", error.getMessage());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "text/plain");
                headers.put("X-CSRFToken", "ETKdheO9U73bBg7UH1bLOcKXCtMnleyK");
                return headers;
            }
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("pid", ParkingInfo.id);
                Log.v("test", ParkingInfo.id);
                params.put("kickboard", Integer.toString(-1));

                return params;
            }
        };
        putRequest.setShouldCache(false);
        requestQueue.add(putRequest);
        Log.v("test", "요청 보냄");
    }
}

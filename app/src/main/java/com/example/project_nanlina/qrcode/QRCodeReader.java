package com.example.project_nanlina.qrcode;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class QRCodeReader extends AppCompatActivity {

    private Button buttonScan;
    private Button input1;

    private IntentIntegrator qrScan;

    public static String useID;   // QR 코드에서 읽어온 PM ID!!!

    private static String IP_ADDRESS = "10.0.2.2";
    private static String TAG = "phptest";

    String pm_name;
    String pm_type;

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

                InsertData task = new InsertData();
                task.execute("http://" + IP_ADDRESS + "/insert.php", useID);
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(QRCodeReader.this, "Please Wait",
                    null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "POST response - " + result);
        }

        @Override
        protected String doInBackground(String... params) {
            String useID = (String)params[1];
            String name = getIntent().getStringExtra("name");

            String serverURL = (String)params[0];

            // 웹으로 전송되는 데이터
            String postParameters = "useID=" + useID + "&name=" + name + "&pm_name=" + pm_name + "&pm_type=" + pm_type;

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString();
            }
            catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }
        }
    }

}

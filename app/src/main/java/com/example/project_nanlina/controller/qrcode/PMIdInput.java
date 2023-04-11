package com.example.project_nanlina.controller.qrcode;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_nanlina.R;
import com.example.project_nanlina.databinding.ParkingInfoBinding;
import com.example.project_nanlina.databinding.PmIdInputBinding;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PMIdInput extends AppCompatActivity {

    public static String useID;   // QR 코드에서 읽어온 PM ID!!!
    private static String IP_ADDRESS = "10.0.2.2";
    private static String TAG = "phptest";

    String name;
    String pm_name;
    String pm_type;

    private PmIdInputBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = PmIdInputBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        name = getIntent().getStringExtra("name");
        if (name == null) {
            name = "산수 1동 제3주차장";
        }

        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // PM ID를 입력하지 않았을때
                if(binding.inputPMID.getText().toString().length() == 0) {
                    Toast.makeText(PMIdInput.this, "ID를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else {
                    useID = binding.inputPMID.getText().toString();
                    ///////// 이용중 화면으로 이동!!!

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
        });
    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(PMIdInput.this, "Please Wait",
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
            String serverURL = (String)params[0];

            // 웹으로 전송되는 데이터
            String postParameters = "&useID=" + useID + "&name=" + name + "&pm_name=" + pm_name + "&pm_type=" + pm_type;

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

package com.example.project_nanlina;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_nanlina.parking.ParkingInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Test2 extends AppCompatActivity {

    private static String IP_ADDRESS = "10.0.2.2";
    private static String TAG2 = "phptest";

    private ArrayList<PM> pmArrayList;
    private PMAdapter pmAdapter;
    private String mJsonString;
    private RecyclerView recyclerView;
    private TextView mTextViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test2);

        Button button1 = findViewById(R.id.button1);   // 전체
        Button button2 = findViewById(R.id.button2);   // 전동킥보드
        Button button3 = findViewById(R.id.button3);   // 전기자전거

        button1.setSelected(true);   // 전체 버튼 눌린 상태로 유지
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button1.setSelected(true);
                button2.setSelected(false);
                button3.setSelected(false);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button1.setSelected(false);
                button2.setSelected(true);
                button3.setSelected(false);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button1.setSelected(false);
                button2.setSelected(false);
                button3.setSelected(true);
            }
        });

        mTextViewResult = (TextView) findViewById(R.id.textView_main_result);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        mTextViewResult.setMovementMethod(new ScrollingMovementMethod());


        Test2.GetData task = new Test2.GetData();
        task.execute("http://" + IP_ADDRESS + "/getjson.php", "");
    }

    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Test2.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG2, "response - " + result);

            if (result == null) {
                mTextViewResult.setText(errorString);
            }
            else {
                mJsonString = result;
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = params[1];

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
                Log.d(TAG2, "response code - " + responseStatusCode);

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

                while((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();
            }
            catch (Exception e) {
                Log.d(TAG2, "GetData : Error", e);
                errorString = e.toString();

                return null;
            }
        }
    }

    private void showResult() {
        String TAG_JSON = "webnautes";
        String TAG_NAME = "name";
        String TAG_ADDRESS = "address";
        String TAG_IMAGE = "photo";
        String TAG_KICKBOARD = "kickboard";
        String TAG_BICYCLE = "bicycle";

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            pmAdapter = new PMAdapter();

            for (int i = 1; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);

                String name = item.getString(TAG_NAME);
                String address = item.getString(TAG_ADDRESS);
                String image = item.getString(TAG_IMAGE);
                String kickboard = item.getString(TAG_KICKBOARD);
                String bicycle = item.getString(TAG_BICYCLE);

                int number = Integer.parseInt(kickboard.replaceAll("[^0-9]",""))
                        + Integer.parseInt(bicycle.replaceAll("[^0-9]",""));
                String stNumber = Integer.toString(number);

                pmAdapter.addItem(new PM(name, address, image, kickboard+"대", bicycle.trim()+"대", stNumber+"대"));
            }

            recyclerView.setAdapter(pmAdapter);

            pmAdapter.setOnItemClickListener(new OnPMItemClickListener() {
                @Override
                public void onItemClick(PMAdapter.ViewHolder holder, View view, int position) {
                    PM item = pmAdapter.getItem(position);

                    Intent intent = new Intent(getApplicationContext(), ParkingInfo.class);
                    intent.putExtra("name", item.getName());
                    intent.putExtra("address", item.getAddress());
                    intent.putExtra("photo", item.getPhoto());
                    intent.putExtra("kickboard", item.getKickboard());
                    intent.putExtra("bicycle", item.getBicycle());
                    intent.putExtra("number", item.getNumber());

                    startActivity(intent);
//                    Toast.makeText(getApplicationContext(), "아이템 선택됨: " + item.getName(), Toast.LENGTH_LONG).show();
                }
            });
        }
        catch (JSONException e) {
            Log.d(TAG2, "showResult : ", e);
        }
    }
}

package com.example.project_nanlina;

import androidx.annotation.NonNull;
//import androidx.appcompat.app.ActionBar;
//import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
//import androidx.core.view.GravityCompat;
//import androidx.drawerlayout.widget.DrawerLayout;
//import androidx.fragment.app.Fragment;

import android.Manifest;
//import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
//import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_nanlina.login.ActivityLogIn;
import com.example.project_nanlina.parking.PMListAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private FirebaseAuth mFirebaseAuth;

    //????????? ?????? ??????
    private GoogleMap map;
    private Marker currentMarker = null;
    public Marker parkingMarker = null;


    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1???
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5???
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;


    //?????? ???????????? ?????? ????????? ????????? ??????
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    Location mCurrentLocation;
    LatLng currentPosition;


    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;

    private View mLayout; // Snackbar ???????????? ?????? View ??????


    // MySQL ??????
    private static String IP_ADDRESS = "10.0.2.2";
    private static String TAG2 = "phptest";

    private String mJsonString;


    // SQLlite ??????
    private RecyclerView recyclerView;
    private PMListAdapter pmAdapter;
    SQLiteDatabase database;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        setContentView(R.layout.activity_main);

        mLayout = findViewById(R.id.layout_main);

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


////////////////////////////////////////////////////////////////////////////////////////////////////
        // ????????? ??????
        mFirebaseAuth = FirebaseAuth.getInstance();

        ImageView btn_logout = findViewById(R.id.btn_logout);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ??????????????????
                mFirebaseAuth.signOut();
                Toast.makeText(MainActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, ActivityLogIn.class);
                startActivity(intent);
                finish();
            }

        });

        //?????? ??????
//         mFirebaseAuth.getCurrentUser().delete();


////////////////////////////////////////////////////////////////////////////////////////////
        // ?????? ?????? ????????? ??????
        recyclerView = findViewById(R.id.recyclerView);

        // ?????? ?????? ?????? (??????, ???????????????, ???????????????)
        Button button1 = findViewById(R.id.button1);   // ??????
        Button button2 = findViewById(R.id.button2);   // ???????????????
        Button button3 = findViewById(R.id.button3);   // ???????????????

        button1.setSelected(true);   // ?????? ?????? ?????? ????????? ??????
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


        ////// ????????? ?????? ??????????????????
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        openDB();


        // ??? ?????? ????????? ????????? ???????????? (select) - ????????? ??????, ??????, ??????, ??????, ????????? sqllite??? ????????????
        if (database != null) {
            String tableName = "pm_info";
            String query = "select id, name, address, latitude, longitude, photo from " + tableName;
            Cursor cursor = database.rawQuery(query, null);
            Log.v(TAG, "????????? ????????? ??? : " + cursor.getCount());

            pmAdapter = new PMListAdapter();

            //?????? ?????? ?????? ????????? ?????? array ??????
            ArrayList latitudeList = new ArrayList<Double>();
            ArrayList longitudeList = new ArrayList<Double>();


            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String address = cursor.getString(2);
                double latitude = cursor.getDouble(3);
                double longitude = cursor.getDouble(4);
                String photo = cursor.getString(5);


//                pmAdapter.addItem(new PMItem(name, address, photo));
//
//                pmAdapter.setOnItemClickListener(new OnPMItemClickListener() {
//                    @Override
//                    public void onItemClick(PMListAdapter.ViewHolder holder, View view, int position) {
//                        PMItem item = pmAdapter.getItem(position);
//
//                        Intent intent = new Intent(getApplicationContext(), ParkingInfo.class);
//                        intent.putExtra("name", item.getName());
//                        intent.putExtra("address", item.getAddress());
//                        intent.putExtra("photo", item.getPhoto());
////                        intent.putExtra("kickboard", item.getKickboard());
////                        intent.putExtra("bicycle", item.getBicycle());
////                        intent.putExtra("number", item.getNumber());
//
//                        startActivity(intent);
////                    Toast.makeText(getApplicationContext(), "????????? ?????????: " + item.getName(), Toast.LENGTH_LONG).show();
//                    }
//                });
            }
            cursor.close();
        }
        else {
            Log.e(TAG, "selectData() db??????.");
        }

        recyclerView.setAdapter(pmAdapter);


        MainActivity.GetData task = new MainActivity.GetData();
        task.execute("http://" + IP_ADDRESS + "/getjson.php", "");
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        Log.d(TAG, "onMapReady :");

        setDefaultLocation();

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            startLocationUpdates();

        } else {

            // ???????????? ????????? ????????? ??? ?????? ?????? ??????
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                Snackbar.make(mLayout, "??? ?????? ??????????????? ?????? ?????? ????????? ???????????????.",
                        Snackbar.LENGTH_INDEFINITE).setAction("??????", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        //?????? ???, ??? ??????
                        ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();


            } else {
                //???????????? ???????????? ????????? ??? ?????? ??????, ?????? ??????
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }


        // ??? ?????? ????????? ????????? ???????????? (select) - ????????? ??????, ??????, ??????, ??????, ????????? sqllite??? ????????????
        if (database != null) {
            String tableName = "pm_info";
            String query = "select id, name, address, latitude, longitude, photo from " + tableName;
            Cursor cursor = database.rawQuery(query, null);
            Log.v(TAG, "????????? ????????? ??? : " + cursor.getCount());

            pmAdapter = new PMListAdapter();


            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String address = cursor.getString(2);
                double latitude = cursor.getDouble(3);
                double longitude = cursor.getDouble(4);
                String photo = cursor.getString(5);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions
                        .position(new LatLng(latitude, longitude))
                        .title(name)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
                map.addMarker(markerOptions);


            }
        }
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                Log.d(TAG, "onMapClick :");
            }
        });
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);

                currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());


                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "??????:" + String.valueOf(location.getLatitude())
                        + " ??????:" + String.valueOf(location.getLongitude());


                Log.d(TAG, "onLocationResult : " + markerSnippet);


                //?????? ????????? ?????? ???????????? ??????
                setCurrentLocation(location, markerTitle, markerSnippet);

                mCurrentLocation = location;
            }


        }

    };


    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        } else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);


            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "startLocationUpdates : ????????? ???????????? ??????");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                map.setMyLocationEnabled(true);

        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        if (checkPermission()) {

            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (map != null)
                map.setMyLocationEnabled(true);

        }


    }


    @Override
    protected void onStop() {

        super.onStop();

        if (mFusedLocationClient != null) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }


    public String getCurrentAddress(LatLng latlng) {

        //???????????? GPS??? ????????? ??????
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //???????????? ?????? ?????? ???,
            Toast.makeText(this, "??????????????? ???????????? ???????????????", Toast.LENGTH_LONG).show();
            return "??????????????? ???????????? ???????????????";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "GPS ????????? ?????????????????????", Toast.LENGTH_LONG).show();
            return "GPS ????????? ?????????????????????";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "????????? ?????? ??? ????????????", Toast.LENGTH_LONG).show();
            return "????????? ?????? ??? ????????????";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {


        if (currentMarker != null) currentMarker.remove();


        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location));
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);


        currentMarker = map.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        map.moveCamera(cameraUpdate);

    }


    public void setDefaultLocation() {


        //????????? ??????, ??????
        LatLng DEFAULT_LOCATION = new LatLng(35.15506884797796, 126.83761778574868);
        String markerTitle = "???????????? ????????? ??? ??????";
        String markerSnippet = "?????? ???????????? GPS ?????? ?????? ???????????????";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = map.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        map.moveCamera(cameraUpdate);

    }


    //??????????????? ????????? ????????? ????????? ?????? ????????????
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        return false;

    }


    /*
     * ActivityCompat.requestPermissions??? ????????? ????????? ????????? ????????? ???????????? ??????????????????.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // ?????? ????????? PERMISSIONS_REQUEST_CODE ??????, ????????? ????????? ???????????? ??????????????????

            boolean check_result = true;


            // ?????? ???????????? ??????????????? ??????

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {

                // ???????????? ??????????????? ?????? ??????????????? ??????
                startLocationUpdates();
            } else {
                // ????????? ???????????? ????????? ?????? ????????? ??? ?????? ????????? ??????????????? ?????? ???????????????.2 ?????? ????????? ????????????.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {


                    // ???????????? ????????? ????????? ???????????? ?????? ?????? ???????????? ????????? ???????????? ?????? ????????? ??? ????????????.
                    Snackbar.make(mLayout, "???????????? ?????????????????????. ?????? ?????? ???????????? ???????????? ??????????????????. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("??????", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                } else {


                    // "?????? ?????? ??????"??? ???????????? ???????????? ????????? ????????? ???????????? ??????(??? ??????)?????? ???????????? ???????????? ?????? ????????? ??? ????????????.
                    Snackbar.make(mLayout, "???????????? ?????????????????????. ??????(??? ??????)?????? ???????????? ???????????? ?????????. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("??????", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();
                }
            }

        }
    }


    //??????????????? GPS ???????????? ?????? ????????????
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("?????? ????????? ????????????");
        builder.setMessage("?????? ???????????? ???????????? ?????? ???????????? ???????????????.\n"
                + "???????????? ?????????????????????????");
        builder.setCancelable(true);
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //???????????? GPS ?????? ???????????? ??????
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : GPS ????????? ?????????");


                        needRequest = true;

                        return;
                    }
                }

                break;
        }
    }


    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG2, "response - " + result);

            if (result == null) {
                Log.d("error", errorString);
            } else {
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
                } else {
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

                return sb.toString().trim();
            } catch (Exception e) {
                Log.d(TAG2, "GetData : Error", e);
                errorString = e.toString();

                return null;
            }
        }
    }

    private void showResult() {
        String TAG_JSON = "webnautes";  // ??????
//        String TAG_JSON = "noa_on_air";   // ??????
//        String TAG_NAME = "name";
//        String TAG_ADDRESS = "address";
//        String TAG_IMAGE = "photo";
        String TAG_KICKBOARD = "kickboard";
        String TAG_ID = "id";
//        String TAG_LATITUDE = "latitude";
//        String TAG_LONGITUDE = "longitude";
        String TAG_GCOOTER = "gcooter";
        String TAG_DEER = "deer";
        String TAG_BEAM = "beam";
        String TAG_TALANG = "talang";
        String TAG_BICYCLE = "bicycle";

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            pmAdapter = new PMListAdapter();

            for (int i = 1; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);

//                String name = item.getString(TAG_NAME);
//                String address = item.getString(TAG_ADDRESS);
//                String image = item.getString(TAG_IMAGE);
                String kickboard = item.getString(TAG_KICKBOARD);
                String bicycle = item.getString(TAG_BICYCLE);
//                double latitude = item.getDouble(TAG_LATITUDE);
//                double longitude = item.getDouble(TAG_LONGITUDE);
                String gcooter = item.getString(TAG_GCOOTER);
                String deer = item.getString(TAG_DEER);
                String beam = item.getString(TAG_BEAM);
                String talang = item.getString(TAG_TALANG);
                String id = item.getString(TAG_ID);



                int number = Integer.parseInt(kickboard.replaceAll("[^0-9]",""))
                        + Integer.parseInt(bicycle.replaceAll("[^0-9]",""));
                String stNumber = Integer.toString(number);

            }
            recyclerView.setAdapter(pmAdapter);
        } catch (JSONException e) {
            Log.d(TAG2, "showResult : ", e);
        }
    }

    public void openDB() {
        Log.v(TAG, "openDB() ??????");
        DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
        database = helper.getWritableDatabase();


        if (database != null) {
            Log.v(TAG, "DB ?????? ??????!");
        } else {
            Log.e(TAG, "DB ?????? ??????!");
        }
    }
}
//    pulic ArrayList<String> user_info(String user_id, String user_latitude, String user_longitude){
//
//}

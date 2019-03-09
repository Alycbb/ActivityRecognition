package com.wedp2.alybb.activityrecognition;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status>, OnMapReadyCallback {

    private ExampleDBHelper mydb;
    private static final String TAG = "MainActivity";
    private GoogleApiClient mGoogleApiClient;
    private TextView detectView;
    private String pre_A;
    private Date pre_T = null;
    private ImageView imgActivity;
    private ActivityDetectionBroadcastReceiver mBroadcastReceiver;

    //db
    public static final String INPUT_COLUMN_ID = "_id";
    public static final String INPUT_COLUMN_Time = "time";
    public static final String INPUT_COLUMN_Activity = "activity";

    //map
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mydb = new ExampleDBHelper(this);

        detectView = (TextView) findViewById(R.id.detect);
        imgActivity = findViewById(R.id.imageView);
        mBroadcastReceiver = new ActivityDetectionBroadcastReceiver();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();

//        detectView.setVisibility(View.VISIBLE);

//        mDetectedActivityTextView.setText(intent.getStringExtra("type"));
    }



    public void onConnected(Bundle bundle) {
        Intent intent = new Intent(this,ActivitiesIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, 3000, getActivityDetectionPendingIntent());
        Log.i(TAG,"Connected");
    }

    public void onConnectionSuspended(int i){
        Log.i(TAG,"Connection suspended");
        mGoogleApiClient.connect();
    }

    public void onConnectionFailed(ConnectionResult connectionResult){
        Log.i(TAG,"Connection failed.Error" + connectionResult.getErrorCode());
    }

    protected void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop(){
        super.onStop();
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
        mydb.delete();

    }

    public String getDetectedActivity(int type){
        Resources resources = this.getResources();
        switch (type){
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.walking);
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.bicycle);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown);
            default:
                return resources.getString(R.string.unidentifiable);
        }
    }

    public int getImag(int type){

        switch (type){
            case DetectedActivity.RUNNING:
                return R.drawable.running;
            case DetectedActivity.STILL:
                return R.drawable.still;
            case DetectedActivity.WALKING:
                return R.drawable.walking;
            case DetectedActivity.ON_FOOT:
                return R.drawable.walking;
            case DetectedActivity.IN_VEHICLE:
                return R.drawable.vehicle;
            case DetectedActivity.ON_BICYCLE:
                return R.drawable.bicycle;
            case DetectedActivity.TILTING:
                return R.drawable.tilting;
            case DetectedActivity.UNKNOWN:
                return R.drawable.wrong;
            default:
                return R.drawable.wrong;
        }
    }


//    private void handleUserActivity(int type) {
//        String label = getString(R.string.unidentifiable);
//        int icon = R.drawable.running;
//
//        switch (type) {
//            case DetectedActivity.RUNNING: {
//                label = getString(R.string.running);
//                icon = R.drawable.running;
//                break;
//            }
//            case DetectedActivity.STILL: {
//                label = getString(R.string.still);
//                icon = R.drawable.still;
//                break;
//            }
//            case DetectedActivity.WALKING: {
//                label = getString(R.string.walking);
//                icon = R.drawable.walking;
//                break;
//            }
//            case DetectedActivity.UNKNOWN: {
//                label = getString(R.string.unknown);
//                break;
//            }
//        }
//
//        Log.e(TAG, "User activity: " + label );
//
//            mDetectedActivityTextView.setText(label);
//            imgActivity.setImageResource(icon);
//
//    }



    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {

        public void onReceive (Context context, Intent intent){
            SimpleDateFormat sd  = new SimpleDateFormat("hh:mm:ss ");
            Date d1 = new Date(System.currentTimeMillis());
            long min,sec;
//            String currentTime = sd.format(d1);


            int type = intent.getIntExtra("type",-1);
            String activityString = "";
            activityString +=  "Activity: " + getDetectedActivity(type);
            detectView.setText(activityString);
            imgActivity.setImageResource(getImag(type));
            Cursor cursor = mydb.getAllRecord();
            if(cursor.getCount() > 0){
                cursor.move(cursor.getCount());
                String time = cursor.getString(cursor.getColumnIndex(INPUT_COLUMN_Time));
                String activity = cursor.getString(cursor.getColumnIndex(INPUT_COLUMN_Activity));
                ParsePosition pos = new ParsePosition(0);
                pre_T = sd.parse(time,pos);
                pre_A = activity;
                Log.v(TAG,"Last time:");
                Log.v(TAG,"startTime : " + time +" Activity: "+ activity);
//                Log.v(TAG,"preActivity = " +pre_A);
            }else if(cursor.getCount() == 0){
                Log.v(TAG,"NO RECORD BEFORE");
                mydb.insertRecord(sd.format(d1),getDetectedActivity(type));
                pre_T = d1;
                pre_A = getDetectedActivity(type);
            }



            if(pre_A.equals(getDetectedActivity(type))){
                Log.v(TAG,"Now: the same activity");
            }else {
                Date d2 = new Date(System.currentTimeMillis());
                min = calculateMin(pre_T,d2);
                sec = calculateSec(pre_T,d2);
                Log.v(TAG,"previous Activity is : "+pre_A );
                Log.v(TAG,"current Activity = " +getDetectedActivity(type));
                if(pre_A.equals("walking")){
                    Log.v(TAG,"ffufwegfweggukwguiw");
                    Toast.makeText(MainActivity.this,"you have just walked for "+min+"min"+sec+"sec",Toast.LENGTH_LONG).show();
                }else if(pre_A.equals("on foot")){
                    Log.v(TAG,"walk");
                    Toast.makeText(MainActivity.this,"you have just walked for "+min+"min"+sec+"sec",Toast.LENGTH_LONG).show();
                }else if(pre_A.equals("running")) {
                    Toast.makeText(MainActivity.this, "you have just ran for " + min + "min" + sec + "sec", Toast.LENGTH_LONG).show();
                }else if(pre_A.equals("still")) {
                    Toast.makeText(MainActivity.this, "you have just kept still for " + min + "min" + sec + "sec", Toast.LENGTH_LONG).show();
                }else if(pre_A.equals("in_vehicle")) {
                    Toast.makeText(MainActivity.this, "you have just in vehicle for " + min + "min" + sec + "sec", Toast.LENGTH_LONG).show();
                }else if(pre_A.equals("on_bicycle")) {
                    Toast.makeText(MainActivity.this, "you have just on bicycle for " + min + "min" + sec + "sec", Toast.LENGTH_LONG).show();
                }else if(pre_A.equals("tilting")) {
                    Toast.makeText(MainActivity.this, "you have just tilted for " + min + "min" + sec + "sec", Toast.LENGTH_LONG).show();
                } else if(pre_A.equals("unknown")) {
                    Toast.makeText(MainActivity.this, "unknown activity for " + min + "min" + sec + "sec", Toast.LENGTH_LONG).show();
                }
                boolean insert = mydb.insertRecord(sd.format(d1),getDetectedActivity(type));
                if(insert){
//                    Toast.makeText(MainActivity.this, ""+sd.format(d1)+activityString, Toast.LENGTH_SHORT).show();
                    Log.v(TAG,"Insert succeed: "+sd.format(d1)+getDetectedActivity(type));
                }else{
//                    Toast.makeText(MainActivity.this, "Insert failed", Toast.LENGTH_SHORT).show();
                    Log.v(TAG,"no Insert ");
                }
            }

            RecognizeStatus(type);
        }
    }



    public long calculateMin(Date dbData,Date d2){
        long duration = d2.getTime() - dbData.getTime();
        long d_min = duration / (60 * 1000) % 60;
        return d_min;
    }

    public long calculateSec(Date dbData,Date d2){
        long duration = d2.getTime() - dbData.getTime();
        long d_sec = duration / 1000 % 60;
        return d_sec;
    }

    public void RecognizeStatus(int type){

        if( type == DetectedActivity.WALKING || type == DetectedActivity.ON_FOOT){
            getLocationPermission();
            Log.v(TAG,"map show");
            Intent ms = new Intent(this,MusicService.class);
            startService(ms);
        }else{
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
            mapFragment.getView().setVisibility(View.INVISIBLE);
            Log.v(TAG,"no map");
            Intent ms = new Intent(this,MusicService.class);
            stopService(ms);
        }

    }

//    public void walkRecognition(int type){
//        if (type == DetectedActivity.WALKING){
//            Intent walk = new Intent(this,WalkActivity.class);
//            walk.putExtra("walk",type);
//            startActivity(walk);
//        }
//    }


//    public void requestActivityUpdates(View view){
//        if (!mGoogleApiClient.isConnected()) {
//            Toast.makeText(this, "GoogleApiClient not yet connected", Toast.LENGTH_SHORT).show();
//        } else {
//            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, 0, getActivityDetectionPendingIntent()).setResultCallback(this);
//        }
//    }
//
//    public void removeActivityUpdates(View view) {
//        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mGoogleApiClient, getActivityDetectionPendingIntent()).setResultCallback(this);
//    }

    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, ActivitiesIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void onResult(Status status){
        if(status.isSuccess()){
            Log.e(TAG,"Successfully added activity detection.");
        }else{
            Log.e(TAG,"Error:" +status.getStatusMessage());
        }
    }

    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(Constants.string_action));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }



    public void onMapReady(GoogleMap googleMap) {
//        Toast.makeText(this, "Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MainActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getView().setVisibility(View.VISIBLE);
        mapFragment.getMapAsync(MainActivity.this);
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

}





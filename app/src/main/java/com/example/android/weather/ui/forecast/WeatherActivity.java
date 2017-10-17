package com.example.android.weather.ui.forecast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.weather.R;
import com.example.android.weather.db.WeatherRepository;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WeatherActivity extends AppCompatActivity implements WeatherContract.View,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    SharedPreferences mSharedPreferences;
    private final String SAVED_DATA = "saved_data";
    private final String INIT_SELECTION = "init_selection";
    private final String DAILY_SELECTION = "daily_selection";

    public static final String NAME = "name";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String DISPLAY_LAT_LNG = "display_lat_lng";

    WeatherContract.Presenter mPresenter;
    WeatherDisplayAdapter mDailyAdapter;

    @BindView(R.id.recyclerview_weather) RecyclerView mRecyclerView;
    @BindView(R.id.location_name) TextView mLocationName;
    @BindView(R.id.spinner_number_days) Spinner mSpinnerDays;
    @BindView(R.id.btn_share) ImageButton mButtonShare;
    @BindView(R.id.btn_switch) Button mButtonSwitch;
    private TextToSpeech mTextToSpeech;

    private boolean mInitialized = false;
    private boolean mThisLocation = false;
    private boolean mLocationSet = false;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;

    private boolean mDaily = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mSharedPreferences = getBaseContext().getSharedPreferences(SAVED_DATA,Context.MODE_PRIVATE);
        mDaily = mSharedPreferences.getBoolean(DAILY_SELECTION,true);

        if(!mDaily){
            mButtonSwitch.setText(getResources().getString(R.string.show_daily));
        }else{
            mButtonSwitch.setText(getResources().getString(R.string.show_hourly));
        }

        mSpinnerDays.setEnabled(mDaily);


        mPresenter = new WeatherPresenter(this,new WeatherRepository(getBaseContext()));


        mButtonShare.setEnabled(false);

        setupSpinner();
        setTextToSpeech();
        setRecyclerView();

        Intent intent = getIntent();

        String name = intent.getStringExtra(NAME);
        double lat = intent.getDoubleExtra(LATITUDE,200);
        double lng = intent.getDoubleExtra(LONGITUDE,200);

        if(lat!=200 && lng!=200){
            mPresenter.downloadForecast(name,lat,lng,mDaily);

        }
        else {
            mThisLocation = true;
            setGoogleApiClient();
        }

    }

    private void setTextToSpeech(){
        mTextToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=TextToSpeech.ERROR){
                    mTextToSpeech.setLanguage(Locale.UK);
                }
            }
        });
    }

    private void setGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mThisLocation) mGoogleApiClient.connect();
    }

    private void setupSpinner(){

        int initSelection = mSharedPreferences.getInt(INIT_SELECTION,6);

        String[] day = {"1","2","3","4","5","6","7"};

        ArrayAdapter<String> adapterDays = new ArrayAdapter<>(
            this,R.layout.weather_days_spinner_item,day);
        mSpinnerDays.setAdapter(adapterDays);
        mSpinnerDays.setSelection(initSelection);
        mSpinnerDays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(mInitialized) {
                    mDailyAdapter.setSize(position + 1);
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putInt(INIT_SELECTION,position);
                    editor.commit();
                }

                mInitialized = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setRecyclerView(){
        mDailyAdapter = new WeatherDisplayAdapter(this,mPresenter,mTextToSpeech,true,0);
        mRecyclerView.setAdapter(mDailyAdapter);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        else
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }



    @Override
    public void displayDaily(int day) {
        int initSelection = mSharedPreferences.getInt(INIT_SELECTION,6);
        mDailyAdapter.setSize(initSelection+1,true);
        mButtonShare.setEnabled(true);
       // mLocationName.setText(mPresenter.getName());
    }

    @Override
    public void displayHourly(int hours) {

        mDailyAdapter.setSize(24,false);
        mButtonShare.setEnabled(true);
    }

    @Override
    public void setName(String name) {
        mLocationName.setText(name);
    }

    @Override
    public void error() {
        Snackbar.make(mRecyclerView, getResources().getString(R.string.error), Snackbar.LENGTH_INDEFINITE)
                .setAction(getResources().getString(R.string.retry),
                        new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                mPresenter.downloadForecast(mDaily);
                            }
                        }
                )
                .show();
    }

    @OnClick(R.id.btn_save_location)
    public void saveLocation(){
        FragmentManager fm = this.getSupportFragmentManager();
        LocationDialog shareDialog = new LocationDialog(this,mPresenter.getName(),mPresenter.getLatitude(),mPresenter.getLongitude());
        shareDialog.show(fm,"dialog_save");
    }

    @OnClick(R.id.btn_share)
    public void share(){
        FragmentManager fm = this.getSupportFragmentManager();
        ShareDialog shareDialog;
        if(mDaily) {
            int days = mSpinnerDays.getSelectedItemPosition() + 1;
            shareDialog = new ShareDialog(
                    mPresenter.getShareSubject(getBaseContext(), days),
                    mPresenter.getShareBodyDaily(getBaseContext(), days));
        }
        else{
            shareDialog = new ShareDialog(
                    mPresenter.getShareSubjectHourly(getBaseContext()),
                    mPresenter.getShareBodyHourly(getBaseContext()));
        }

        shareDialog.show(fm,"dialog_share");
    }

    @OnClick(R.id.btn_switch)
    public void switchTime(){
        if(mDaily){
            mDaily = false;
            mSpinnerDays.setEnabled(mDaily);
            mDailyAdapter.setSize(24,mDaily);
            mButtonSwitch.setText(getResources().getString(R.string.show_daily));
        }else {
            mDaily = true;
            int size = mSpinnerDays.getSelectedItemPosition();
            mDailyAdapter.setSize(size+1,mDaily);
            mSpinnerDays.setEnabled(mDaily);
            mButtonSwitch.setText(getResources().getString(R.string.show_hourly));
        }
    }

    public void saveLocation(String locationName){
        mPresenter.saveLocation(locationName);
    }

    public static Intent getIntent(Context context,String name, double latitude, double longitude){
        Intent intent = new Intent(context, WeatherActivity.class);
        intent.putExtra(NAME,name);
        intent.putExtra(LATITUDE,latitude);
        intent.putExtra(LONGITUDE,longitude);
        return intent;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            setLastLocation();
          //  onLocationChanged(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));

        }else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        }

    }

    private void setLocation(){
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            setLastLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        setLocation();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void setLastLocation(){
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(300);
                    if(ActivityCompat.checkSelfPermission(WeatherActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {

                        onLocationChanged(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
                    }
                } catch (InterruptedException e) {

                }
            }
        };
        thread.start();
    }

    @Override
    public synchronized void onLocationChanged(Location location) {
        if(!mLocationSet && location!=null) {
            mLocationSet = true;
            mPresenter.downloadForecast(DISPLAY_LAT_LNG, location.getLatitude(), location.getLongitude(),mDaily);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(DAILY_SELECTION,mDaily);
        editor.commit();

        if(mTextToSpeech!=null){
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }

        super.onPause();
    }

    @Override
    protected void onStop() {
        if(mGoogleApiClient!=null) mGoogleApiClient.disconnect();
        super.onStop();
    }
}

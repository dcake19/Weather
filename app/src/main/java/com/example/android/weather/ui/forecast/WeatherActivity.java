package com.example.android.weather.ui.forecast;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

    public static final String NAME = "name";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String DISPLAY_LAT_LNG = "display_lat_lng";

    WeatherContract.Presenter mPresenter;
    WeatherDisplayAdapter mDailyAdapter;

    @BindView(R.id.recyclerview_weather) RecyclerView mRecyclerView;
    @BindView(R.id.location_name) TextView mLocationName;
    @BindView(R.id.spinner_number_days) Spinner mSpinnerDays;

    private TextToSpeech mTextToSpeech;

    private boolean mInitialized = false;
    private boolean mThisLocation = false;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mPresenter = new WeatherPresenter(this,new WeatherRepository(getBaseContext()));

        setupSpinner();
        setTextToSpeech();
        setRecyclerView();


        Intent intent = getIntent();

        String name = intent.getStringExtra(NAME);
        double lat = intent.getDoubleExtra(LATITUDE,200);
        double lng = intent.getDoubleExtra(LONGITUDE,200);

        if(lat!=200 && lng!=200){
            mPresenter.downloadForecast(name,lat,lng);

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

    //    private void setupToolbar(){
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//    }

    private void setupSpinner(){
        String[] day = {"1","2","3","4","5","6","7"};

        ArrayAdapter<String> adapterDays = new ArrayAdapter<>(
            this,R.layout.weather_days_spinner_item,day);
        mSpinnerDays.setAdapter(adapterDays);
        mSpinnerDays.setSelection(6);
        mSpinnerDays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(mInitialized)
                    mDailyAdapter.setSize(position+1);

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
        GridLayoutManager glm = new GridLayoutManager(this,1);
        mRecyclerView.setLayoutManager(glm);
    }


    @Override
    public void displayDaily(int day) {
        mDailyAdapter.setSize(7);
        mLocationName.setText(mPresenter.getName());
    }

    @Override
    public void displayHourly(int hours) {

    }




    @OnClick(R.id.btn_save_location)
    public void saveLocation(){
        FragmentManager fm = this.getSupportFragmentManager();
        LocationDialog shareDialog = new LocationDialog(this,mPresenter.getLatitude(),mPresenter.getLongitude());
        shareDialog.show(fm,"dialog_save");
    }

    @OnClick(R.id.btn_share)
    public void share(){
        FragmentManager fm = this.getSupportFragmentManager();
        int days = mSpinnerDays.getSelectedItemPosition()+1;
        ShareDialog shareDialog = new ShareDialog(
                mPresenter.getShareSubject(getBaseContext(),days),
                mPresenter.getShareBodyDaily(getBaseContext(),days));
        shareDialog.show(fm,"dialog_share");
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
        mLocationRequest.setInterval(10);
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

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

    @Override
    public void onLocationChanged(Location location) {
        mPresenter.downloadForecast(DISPLAY_LAT_LNG,location.getLatitude(),location.getLongitude());
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onPause() {

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

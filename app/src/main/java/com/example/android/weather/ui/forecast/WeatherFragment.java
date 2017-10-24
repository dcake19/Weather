package com.example.android.weather.ui.forecast;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class WeatherFragment extends Fragment implements WeatherContract.View,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String NAME = "name";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    private final String SAVED_DATA = "saved_data";

    public static final String DISPLAY_LAT_LNG = "display_lat_lng";

    private WeatherContract.Presenter mPresenter;
    private WeatherDisplayAdapter mDailyAdapter;

    @BindView(R.id.recyclerview_weather)
    RecyclerView mRecyclerView;

    private TextToSpeech mTextToSpeech;

    private boolean mThisLocation = false;
    private boolean mLocationSet = false;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = new WeatherPresenter(this,new WeatherRepository(getActivity().getApplicationContext()),
                getActivity().getApplicationContext().getSharedPreferences(SAVED_DATA, Context.MODE_PRIVATE));


        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.weather_fragment, container, false);
        ButterKnife.bind(this,rootview);

        ((WeatherActivity)getActivity()).setToolbarDetails();

        setTextToSpeech();
        setRecyclerView();

        Intent intent = getActivity().getIntent();

        String name = intent.getStringExtra(NAME);
        double lat = intent.getDoubleExtra(LATITUDE,200);
        double lng = intent.getDoubleExtra(LONGITUDE,200);

        if(lat!=200 && lng!=200){
            mPresenter.downloadForecast(name,lat,lng, ((WeatherActivity)getActivity()).getDaily());

        }
        else if(mLocationSet){
            mPresenter.downloadForecast(((WeatherActivity)getActivity()).getDaily());
        }
        else {
            mThisLocation = true;
            setGoogleApiClient();
        }

        return rootview;
    }

    private void setRecyclerView(){
        mDailyAdapter = new WeatherDisplayAdapter(getActivity(),mPresenter,mTextToSpeech,true,0);
        mRecyclerView.setAdapter(mDailyAdapter);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        else
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    }


    private void setTextToSpeech(){
        mTextToSpeech = new TextToSpeech(getActivity().getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=TextToSpeech.ERROR){
                    mTextToSpeech.setLanguage(Locale.UK);
                }
            }
        });
    }

    private void setGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void setAdapterSize(int size){
        mDailyAdapter.setSize(size);
        mPresenter.saveSelection(size-1);
    }

    public boolean getInitialDaily(){
        return mPresenter.getInitialDaily();
    }

    public int getInitalSelection(){
        return mPresenter.getInitialSelection();
    }

    @Override
    public void displayDaily(int day) {
        int initSelection = mPresenter.getInitialSelection();
        mDailyAdapter.setSize(initSelection+1,true);
        ((WeatherActivity)getActivity()).enableShare();
    }

    @Override
    public void displayHourly(int hours) {
        mDailyAdapter.setSize(24,false);
        ((WeatherActivity)getActivity()).enableShare();
    }

    @Override
    public void setName(String name) {
        ((WeatherActivity)getActivity()).setLocationName(name);
    }

    @Override
    public void error() {
        Snackbar.make(mRecyclerView, getResources().getString(R.string.error), Snackbar.LENGTH_INDEFINITE)
                .setAction(getResources().getString(R.string.retry),
                        new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                mPresenter.downloadForecast(((WeatherActivity)getActivity()).getDaily());
                            }
                        }
                )
                .show();
    }


    public void saveLocation(){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        LocationDialog shareDialog = new LocationDialog(mPresenter,
                mPresenter.getName(),mPresenter.getLatitude(),mPresenter.getLongitude());
        shareDialog.show(fm,"dialog_save");
    }

    public void shareDaily(int days){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        ShareDialog shareDialog;

            shareDialog = new ShareDialog(
                    mPresenter.getShareSubject(getContext(), days),
                    mPresenter.getShareBodyDaily(getContext(), days));

        shareDialog.show(fm,"dialog_share");
    }

    public void shareHourly(){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        ShareDialog shareDialog;

        shareDialog = new ShareDialog(
                mPresenter.getShareSubjectHourly(getContext()),
                mPresenter.getShareBodyHourly(getContext()));

        shareDialog.show(fm,"dialog_share");
    }

    public void switchTime(int size ,boolean daily){

            mDailyAdapter.setSize(size,daily);

            mDailyAdapter.setSize(size,daily);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        if(ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            setLastLocation();
            //  onLocationChanged(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));

        }else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        }

    }

    private void setLocation(){
        if(ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
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
                    if(ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
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
            mPresenter.downloadForecast(DISPLAY_LAT_LNG,
                    location.getLatitude(), location.getLongitude(),
                    ((WeatherActivity)getActivity()).getDaily());
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if(mThisLocation) mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        mPresenter.saveDaily(((WeatherActivity)getActivity()).getDaily());

        if(mTextToSpeech!=null){
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }

        super.onPause();
    }

    @Override
    public void onStop() {
        if(mGoogleApiClient!=null) mGoogleApiClient.disconnect();
        super.onStop();
    }
}

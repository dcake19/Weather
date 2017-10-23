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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class WeatherActivity extends AppCompatActivity {

    private final String FRAGMENT = "Weather Fragment";
    private WeatherFragment mFragment;

    public static final String NAME = "name";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String DISPLAY_LAT_LNG = "display_lat_lng";

    private final String SAVE_DAILY = "save_daily";

    @BindView(R.id.location_name) TextView mLocationName;
    @BindView(R.id.spinner_number_days) Spinner mSpinnerDays;
    @BindView(R.id.btn_share) ImageButton mButtonShare;
    @BindView(R.id.btn_switch) Button mButtonSwitch;

    private boolean mInitialized = false;

    private boolean mDaily = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_activity);

        ButterKnife.bind(this);


        if(savedInstanceState!=null)
            mDaily = savedInstanceState.getBoolean(SAVE_DAILY,true);


        FragmentManager fm = getSupportFragmentManager();
        mFragment = (WeatherFragment) fm.findFragmentByTag(FRAGMENT);

        if(mFragment==null) {
            mFragment = new WeatherFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            fragmentTransaction.add(R.id.weather_content, mFragment, FRAGMENT);
            fragmentTransaction.commit();
        }

    }

    public void setToolbarDetails(){
        mDaily = mFragment.getInitialDaily();

        if(!mDaily){
            mButtonSwitch.setText(getResources().getString(R.string.show_daily));
        }else{
            mButtonSwitch.setText(getResources().getString(R.string.show_hourly));
        }

        mSpinnerDays.setEnabled(mDaily);

        mButtonShare.setEnabled(false);

        setupSpinner();
    }

    private void setupSpinner(){

        int initSelection =  mFragment.getInitalSelection();

        String[] day = {"1","2","3","4","5","6","7"};

        ArrayAdapter<String> adapterDays = new ArrayAdapter<>(
            this,R.layout.weather_days_spinner_item,day);
        mSpinnerDays.setAdapter(adapterDays);
        mSpinnerDays.setSelection(initSelection);
        mSpinnerDays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(mInitialized) {
                    if(mDaily)
                        mFragment.setAdapterSize(position+1);

                }
                mInitialized = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public boolean getDaily(){
        return mDaily;
    }

    public void setLocationName(String name){
        mLocationName.setText(name);
    }

    public void enableShare(){
        mButtonShare.setEnabled(true);
    }

    @OnClick(R.id.btn_save_location)
    public void saveLocation(){
        mFragment.saveLocation();
    }

    @OnClick(R.id.btn_share)
    public void share(){
        if(mDaily) {
            mFragment.shareDaily(mSpinnerDays.getSelectedItemPosition() + 1);
        }
        else{
            mFragment.shareHourly();
        }
    }

    @OnClick(R.id.btn_switch)
    public void switchTime(){
        if(mDaily){
            mDaily = false;
            mSpinnerDays.setEnabled(mDaily);
            mFragment.switchTime(24,mDaily);
            mButtonSwitch.setText(getResources().getString(R.string.show_daily));
        }else {
            mDaily = true;
            int size = mSpinnerDays.getSelectedItemPosition();
            mFragment.switchTime(size+1,mDaily);
            mSpinnerDays.setEnabled(mDaily);
            mButtonSwitch.setText(getResources().getString(R.string.show_hourly));
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVE_DAILY,mDaily);
    }

    public static Intent getIntent(Context context, String name, double latitude, double longitude){
        Intent intent = new Intent(context, WeatherActivity.class);
        intent.putExtra(NAME,name);
        intent.putExtra(LATITUDE,latitude);
        intent.putExtra(LONGITUDE,longitude);
        return intent;
    }



}

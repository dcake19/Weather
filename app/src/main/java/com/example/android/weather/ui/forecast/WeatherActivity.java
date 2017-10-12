package com.example.android.weather.ui.forecast;

import android.content.Context;
import android.content.Intent;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WeatherActivity extends AppCompatActivity implements WeatherContract.View {

    public static final String NAME = "name";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String DISPLAY_LAT_LNG = "display_lat_lng";

    WeatherContract.Presenter mPresenter;
    WeatherDisplayAdapter mDailyAdapter;

    @BindView(R.id.recyclerview_weather) RecyclerView mRecyclerView;
    @BindView(R.id.location_name) TextView mLocationName;
    @BindView(R.id.spinner_number_days) Spinner mSpinnerDays;

    private boolean mInitialized = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mPresenter = new WeatherPresenter(this,new WeatherRepository(getBaseContext()));

        setupSpinner();
        setRecyclerView();

        Intent intent = getIntent();

        String name = intent.getStringExtra(NAME);
        double lat = intent.getDoubleExtra(LATITUDE,200);
        double lng = intent.getDoubleExtra(LONGITUDE,200);



        if(lat==200 || lng==200){
            mPresenter.downloadForecast(DISPLAY_LAT_LNG,51.5074,0.1278);
        }
        else {
            mPresenter.downloadForecast(name,lat,lng);
        }





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
        mDailyAdapter = new WeatherDisplayAdapter(this,mPresenter,true,0);
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

}

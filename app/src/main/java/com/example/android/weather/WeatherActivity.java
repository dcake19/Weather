package com.example.android.weather;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WeatherActivity extends AppCompatActivity implements WeatherContract.View{

    WeatherContract.Presenter mPresenter;
    WeatherDisplayAdapter mDailyAdapter;

    @BindView(R.id.recyclerview_weather) RecyclerView mRecyclerView;
    @BindView(R.id.spinner_number_days) Spinner mSpinnerDays;

    private boolean mInitialized = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mPresenter = new WeatherPresenter(this);
        mPresenter.downloadForecast();

        //setupToolbar();
        setupSpinner();
       setRecyclerView();

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

    }

}

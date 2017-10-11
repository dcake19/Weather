package com.example.android.weather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WeatherActivity extends AppCompatActivity implements WeatherContract.View{

    WeatherContract.Presenter mPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPresenter = new WeatherPresenter();
        mPresenter.downloadForecast();
    }





}

package com.example.android.weather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherActivity extends AppCompatActivity implements WeatherContract.View{

    WeatherContract.Presenter mPresenter;
    WeatherDisplayAdapter mDailyAdapter;

    @BindView(R.id.recyclerview_weather) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mPresenter = new WeatherPresenter(this);
        mPresenter.downloadForecast();

       setRecyclerView();
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
}

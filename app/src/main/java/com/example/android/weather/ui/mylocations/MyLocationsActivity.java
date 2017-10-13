package com.example.android.weather.ui.mylocations;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.weather.R;
import com.example.android.weather.db.WeatherRepository;
import com.example.android.weather.ui.forecast.WeatherDisplayAdapter;
import com.example.android.weather.ui.mylocations.MyLocationsContract;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MyLocationsActivity extends AppCompatActivity  implements MyLocationsContract.View{

    MyLocationsContract.Presenter mPresenter;
    MyLocationsAdapter mAdapter;

    @BindView(R.id.recyclerview_locations)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_locations_activity);

        ButterKnife.bind(this);

        mPresenter = new MyLocationsPresenter(this,new WeatherRepository(getBaseContext()));
        setRecyclerView();

        mPresenter.getLocations();
    }


    private void setRecyclerView(){
        mAdapter = new MyLocationsAdapter(this,getBaseContext(),mPresenter);
        mRecyclerView.setAdapter(mAdapter);
        GridLayoutManager glm = new GridLayoutManager(this,1);
        mRecyclerView.setLayoutManager(glm);
    }


    @Override
    public void displayLocations(int size) {
        mAdapter.setSize(size);
    }
}

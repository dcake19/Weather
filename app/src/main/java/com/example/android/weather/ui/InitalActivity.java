package com.example.android.weather.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.android.weather.R;
import com.example.android.weather.db.WeatherRepository;
import com.example.android.weather.ui.forecast.WeatherActivity;
import com.example.android.weather.ui.map.MapActivity;
import com.example.android.weather.ui.mylocations.MyLocationsActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class InitalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inital_activity);
        ButterKnife.bind(this);

        //loadInitialSetup();
    }

    @OnClick({R.id.btn_forecast_here,R.id.btn_my_locations,R.id.btn_show_on_map})
    public void startNewActivity(View view){
        if(view.getId()==R.id.btn_forecast_here){
            Intent intent = new Intent(getBaseContext(), WeatherActivity.class);
            startActivity(intent);
        }
        else if(view.getId()==R.id.btn_my_locations){
            Intent intent = new Intent(getBaseContext(),MyLocationsActivity.class);
            startActivity(intent);
        }
        else if(view.getId()==R.id.btn_show_on_map){
            Intent intent = new Intent(getBaseContext(),MapActivity.class);
            startActivity(intent);
        }
    }


    private void loadInitialSetup(){
        WeatherRepository repository = new WeatherRepository(getBaseContext());
        repository.insert("London",51.5074,-0.1278);
        repository.insert("Cambridge",52.2053,0.1218);
        repository.insert("Leeds",53.8008,-1.5491);
        repository.insert("Birmingham",52.4862,-1.8904);
        repository.insert("Manchester",53.4808,-2.2426);
        repository.insert("Glasgow",55.8642,-4.2518);
        repository.insert("Bristol",51.4545,-2.5879);
    }
}

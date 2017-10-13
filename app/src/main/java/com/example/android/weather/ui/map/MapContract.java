package com.example.android.weather.ui.map;


import android.content.Context;
import android.content.Intent;

public class MapContract {

    interface View{
        void locationsReady(int size);
    }

    interface Presenter{
        void getLocations();
        double getLatitude(int position);
        double getLongitude(int position);
        Intent getIntentForWeatherActivity(Context context,int position);
    }
}

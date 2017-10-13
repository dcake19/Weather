package com.example.android.weather.ui.map;


import android.content.Context;
import android.content.Intent;

public class MapContract {

    interface View{
        void locationsReady(int size);
        void searchComplete(double lat,double lng);
    }

    interface Presenter{
        void getLocations();
        double getLatitude(int position);
        double getLongitude(int position);
        Intent getIntentForWeatherActivity(Context context,int position);
        void searchForLocation(String location);

    }
}

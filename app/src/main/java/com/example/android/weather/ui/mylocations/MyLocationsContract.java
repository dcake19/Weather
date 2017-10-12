package com.example.android.weather.ui.mylocations;


import android.content.Context;
import android.content.Intent;

public class MyLocationsContract {

    interface View{
        void displayLocations(int size);
    }

    interface Presenter{
        void getLocations();
        String getName(int position);
        String getLatLong(int position);
        boolean getDisplayed(int position);
        void removeLocation(int position);
        void changedDisplayed(int position,boolean display);
        Intent getIntentForWeatherActivity(Context context,int position);
    }
}

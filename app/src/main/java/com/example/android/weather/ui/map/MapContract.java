package com.example.android.weather.ui.map;



public class MapContract {

    interface View{
        void locationsReady(int size);
    }

    interface Presenter{
        void getLocations();
        double getLatitude(int position);
        double getLongitude(int position);
        double getCenterLatitude();
        double getCenterLongitude();
    }
}

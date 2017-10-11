package com.example.android.weather;



public class WeatherContract {

    interface View{

    }

    interface Presenter{
        void downloadForecast();
    }
}

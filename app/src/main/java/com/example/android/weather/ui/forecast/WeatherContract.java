package com.example.android.weather.ui.forecast;


import android.content.Context;

public class WeatherContract {

    interface View{
        void displayDaily(int day);
        void displayHourly(int hours);
    }

    interface Presenter{
        void downloadForecast();
        String getDate(int position);
        String getSummaryDaily(int position);
        String getHighTemp(Context context,int position);
        String getLowTemp(Context context,int position);
        String getWindSpeedDaily(Context context,int position);
        String getHumidityDaily(Context context,int position);
        String getPrecipDaily(Context context,int position);
        int getIconDaily(Context context,int position);
        String getLatLong();
        String getLatitude();
        String getLongitude();
        void saveLocation(String locationName);
        String getShareSubject(Context context,int days);
        String getShareBodyDaily(Context context,int days);
    }
}
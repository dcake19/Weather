package com.example.android.weather.ui.forecast;


import android.content.Context;

public class WeatherContract {

    interface View{
        void displayDaily(int day);
        void displayHourly(int hours);
        void setName(String name);
        void error();
    }

    interface Presenter{
        void downloadForecast();
        void downloadForecast(String name,double latitude,double longitude);
        String getName();
        String getDate(int position);
        String getSummaryDaily(int position);
        String getHighTemp(Context context,int position);
        String getLowTemp(Context context,int position);
        String getWindSpeedDaily(Context context,int position);
        String getPrecipDaily(Context context,int position);
        int getIconDaily(Context context,int position);
        String getLatLong();
        String getLatitude();
        String getLongitude();
        void saveLocation(String locationName);
        String getShareSubject(Context context,int days);
        String getShareBodyDaily(Context context,int days);
        String getWeatherSpeak(int position);
    }
}

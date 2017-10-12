package com.example.android.weather;


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
    }
}

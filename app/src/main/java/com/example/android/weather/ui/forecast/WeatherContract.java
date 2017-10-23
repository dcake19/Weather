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
        void downloadForecast(boolean daily);
        void downloadForecast(String name,double latitude,double longitude,boolean daily);
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

        String getWeatherSpeakDaily(int position);
        String getTime(int position);
        String getTempHourly(int position);
        String getWindSpeedHourly(Context context,int position);
        String getPrecipHourly(Context context,int position);
        int getIconHourly(Context context,int position);
        String getWeatherSpeakHourly(int position);
        String getShareSubjectHourly(Context context);
        String getShareBodyHourly(Context context);

        boolean getInitialDaily();
        int getInitialSelection();
        void saveDaily(boolean daily);
        void saveSelection(int selection);

        void onPause();

    }
}

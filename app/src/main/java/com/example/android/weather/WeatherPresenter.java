package com.example.android.weather;


import android.content.Context;
import android.util.Log;

import com.example.android.weather.rest.ApiService;
import com.example.android.weather.rest.ApiUtils;
import com.example.android.weather.rest.model.Daily;
import com.example.android.weather.rest.model.Hourly;
import com.example.android.weather.rest.model.WeatherForecast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherPresenter implements WeatherContract.Presenter {

    private WeatherContract.View mView;
    private ApiService mApiService;
    private Daily mDaily;
    private Hourly mHourly;
    private boolean mShowDaily = true;
    private String mStringLatitude;
    private String mStringLongitude;

    public WeatherPresenter( WeatherContract.View view) {

        mView = view;
        mApiService = ApiUtils.getApiService();
        mStringLatitude = "52.2053";
        mStringLongitude = "0.1218";
    }



    @Override
    public void downloadForecast() {
//        mApiService.getForecast().enqueue(new Callback<WeatherForecast>() {
//            @Override
//            public void onResponse(Call<WeatherForecast> call, Response<WeatherForecast> response) {
//                WeatherForecast wf = response.body();
//
//                Log.d("AnswersPresenter", "posts loaded from API");
//            }
//
//            @Override
//            public void onFailure(Call<WeatherForecast> call, Throwable t) {
//                Log.d("AnswersPresenter", "posts loaded from API");
//            }
//        });



        mApiService.getForecast(getLatLong()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WeatherForecast>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(WeatherForecast weatherForecast) {

                        mDaily = weatherForecast.getDaily();
                        mHourly = weatherForecast.getHourly();

                        if(mShowDaily)
                            mView.displayDaily(mDaily.getData().size());
                        else
                            mView.displayHourly(mHourly.getData().size());

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v("","");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public String getDate(int position) {
        int time = mDaily.getData().get(position+1).getTime();
        long javaTime = time;
        javaTime *= 1000;
        Date date = new Date(javaTime);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM");
        return sdf.format(date);
    }

    @Override
    public String getSummaryDaily(int position) {
        return mDaily.getData().get(position+1).getSummary();
    }

    @Override
    public String getHighTemp(Context context, int position) {
        return context.getString(R.string.high) + " " + mDaily.getData().get(position+1).getTemperatureHigh() + "\u2103";
    }

    @Override
    public String getLowTemp(Context context,int position) {
        return context.getString(R.string.low) + " " + mDaily.getData().get(position+1).getTemperatureLow()  + "\u2103";
    }

    @Override
    public String getWindSpeedDaily(Context context,int position) {
        return context.getString(R.string.wind_speed)
                + " " + mDaily.getData().get(position+1).getWindSpeed()
                + " " + context.getString(R.string.wind_speed_units) ;
    }

    @Override
    public String getHumidityDaily(Context context,int position) {
        return context.getString(R.string.humidity)
                + " " + mDaily.getData().get(position+1).getHumidity()
                + "\u0025";
    }

    @Override
    public String getPrecipDaily(Context context,int position) {
        String chance = Double.toString(mDaily.getData().get(position+1).getPrecipProbability()*100);
        return chance + "\u0025"
                + " " +context.getString(R.string.chance)
                + " " + mDaily.getData().get(position+1).getPrecipType();
    }

    private String getHumidityDailyEmail(Context context,int position) {
        return context.getString(R.string.humidity)
                + " " + mDaily.getData().get(position+1).getHumidity()
                + " pct.";
    }

    private String getPrecipDailyEmail(Context context,int position) {
        String chance = Double.toString(mDaily.getData().get(position+1).getPrecipProbability()*100);
        return chance + " pct."
                + " " +context.getString(R.string.chance)
                + " " + mDaily.getData().get(position+1).getPrecipType();
    }

    @Override
    public int getIconDaily(Context context, int position) {
        String icon =  mDaily.getData().get(position+1).getIcon();
        return weatherIcon(icon);
    }

    @Override
    public String getLatLong() {
        return mStringLatitude + "," + mStringLongitude;
    }

    @Override
    public String getLatitude() {
        return mStringLatitude;
    }

    @Override
    public String getLongitude() {
        return mStringLongitude;
    }

    @Override
    public void saveLocation(String locationName) {

    }

    @Override
    public String getShareSubject(Context context, int days) {
        return context.getString(R.string.weather_for_next) + " " + days + " " + context.getString(R.string.days);
    }


    @Override
    public String getShareBodyDaily(Context context,int days) {
        StringBuilder shareBody = new StringBuilder();

        for(int i=0;i<days;i++) {
            shareBody.append(getDate(i)+"\n");
            shareBody.append(getSummaryDaily(i)+"\n");
            shareBody.append(getHighTemp(context,i)+"\n");
            shareBody.append(getLowTemp(context,i)+"\n");
            shareBody.append(getWindSpeedDaily(context,i)+"\n");
            shareBody.append(getHumidityDailyEmail(context,i)+"\n");
            shareBody.append(getPrecipDailyEmail(context,i)+"\n");
            shareBody.append("\n");
        }

        return shareBody.toString();
    }

    private int weatherIcon(String icon){
        if(icon.equals("clear-day"))
            return R.drawable.art_clear_day;
        else if(icon.equals("clear-night"))
            return R.drawable.art_clear_night;
        else if(icon.equals("rain"))
            return R.drawable.art_rain;
        else if(icon.equals("snow"))
            return R.drawable.art_snow;
        else if(icon.equals("sleet"))
            return R.drawable.art_sleet;
        else if(icon.equals("wind"))
            return R.drawable.art_wind;
        else if(icon.equals("fog"))
            return R.drawable.art_fog;
        else if(icon.equals("cloudy"))
            return R.drawable.art_cloudy;
        else if(icon.equals("partly-cloudy-day"))
            return R.drawable.art_partly_cloudy_day;
        return R.drawable.art_partly_cloudy_night;
    }



}

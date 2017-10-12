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
    private ArrayList<String> mDateStrings = new ArrayList<>(7);

    public WeatherPresenter( WeatherContract.View view) {

        mView = view;
        mApiService = ApiUtils.getApiService();
        setDateStrings();
    }

    private void setDateStrings(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM");
        long time = date.getTime();

        mDateStrings.add(sdf.format(date));
        mDateStrings.add(sdf.format(date));
        mDateStrings.add(sdf.format(date));
        mDateStrings.add(sdf.format(date));
        mDateStrings.add(sdf.format(date));
        mDateStrings.add(sdf.format(date));
        mDateStrings.add(sdf.format(date));
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



        mApiService.getForecast("52.2053,0.1218").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
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
        int time = mDaily.getData().get(position).getTime();
        long javaTime = time;
        javaTime *= 1000;
        Date date = new Date(javaTime);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM");
        return sdf.format(date);
       // return mDateStrings.get(position);
    }

    @Override
    public String getSummaryDaily(int position) {
        return mDaily.getData().get(position).getSummary();
    }

    @Override
    public String getHighTemp(Context context, int position) {
        return context.getString(R.string.high) + " " + mDaily.getData().get(position).getTemperatureHigh();
    }

    @Override
    public String getLowTemp(Context context,int position) {
        return context.getString(R.string.low) + " " + mDaily.getData().get(position).getTemperatureLow();
    }

    @Override
    public String getWindSpeedDaily(Context context,int position) {
        return context.getString(R.string.wind_speed)
                + " " + mDaily.getData().get(position).getWindSpeed()
                + " " + context.getString(R.string.wind_speed_units) ;
    }

    @Override
    public String getHumidityDaily(Context context,int position) {
        return context.getString(R.string.humidity)
                + " " + mDaily.getData().get(position).getHumidity()
                + "%";
    }

    @Override
    public String getPrecipDaily(Context context,int position) {
        return mDaily.getData().get(position).getPrecipProbability() + "%"
                + " " +context.getString(R.string.chance)
                + " " + mDaily.getData().get(position).getPrecipType();
    }

    @Override
    public int getIconDaily(Context context, int position) {
        String icon =  mDaily.getData().get(position).getIcon();
        return weatherIcon(icon);
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

package com.example.android.weather;


import android.util.Log;

import com.example.android.weather.rest.ApiService;
import com.example.android.weather.rest.ApiUtils;
import com.example.android.weather.rest.model.Daily;
import com.example.android.weather.rest.model.WeatherForecast;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherPresenter implements WeatherContract.Presenter {

    ApiService mApiService;

    public WeatherPresenter() {

        mApiService = ApiUtils.getApiService();

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
                        Daily daily = weatherForecast.getDaily();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}

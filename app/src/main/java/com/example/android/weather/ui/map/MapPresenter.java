package com.example.android.weather.ui.map;


import android.content.Context;
import android.content.Intent;

import com.example.android.weather.db.Location;
import com.example.android.weather.db.WeatherRepository;
import com.example.android.weather.ui.forecast.WeatherActivity;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MapPresenter implements MapContract.Presenter{

    MapContract.View mView;
    WeatherRepository mRepository;
    private ArrayList<Location> mLocations;

    public MapPresenter( MapContract.View view,WeatherRepository repository) {
        mRepository = repository;
        mView = view;
    }

    @Override
    public void getLocations() {
        Observable<ArrayList<Location>> observable = Observable.create(new ObservableOnSubscribe<ArrayList<Location>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<Location>> e) throws Exception {
                e.onNext(mRepository.getLoactionsForMap());
            }
        });

        Consumer<ArrayList<Location>> consumer = new Consumer<ArrayList<Location>>() {
            @Override
            public void accept(@NonNull ArrayList<Location> locations) throws Exception {
                mLocations = locations;
                mView.locationsReady(mLocations.size());
            }
        };

        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(consumer);
    }

    @Override
    public double getLatitude(int position) {
        return mLocations.get(position).latitude;
    }

    @Override
    public double getLongitude(int position) {
        return mLocations.get(position).longitude;
    }

    @Override
    public Intent getIntentForWeatherActivity(Context context, int position) {
        return WeatherActivity.getIntent(context,
                mLocations.get(position).name,
                mLocations.get(position).latitude,
                mLocations.get(position).longitude);
    }
}

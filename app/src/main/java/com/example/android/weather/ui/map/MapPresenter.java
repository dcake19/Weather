package com.example.android.weather.ui.map;


import com.example.android.weather.db.Location;
import com.example.android.weather.db.WeatherRepository;

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
    public double getCenterLongitude() {
        double largest = mLocations.get(0).longitude;
        double smallest = mLocations.get(0).longitude;
        for(int i=1;i<mLocations.size();i++){
            if (mLocations.get(i).longitude>largest) largest = mLocations.get(i).longitude;
            if (mLocations.get(i).longitude<smallest) smallest = mLocations.get(i).longitude;
        }
        return (largest+smallest)/2;
    }

    @Override
    public double getCenterLatitude() {
        double largest = mLocations.get(0).latitude;
        double smallest = mLocations.get(0).latitude;
        for(int i=1;i<mLocations.size();i++){
            if (mLocations.get(i).latitude>largest) largest = mLocations.get(i).latitude;
            if (mLocations.get(i).latitude<smallest) smallest = mLocations.get(i).latitude;
        }
        return (largest+smallest)/2;
    }
}

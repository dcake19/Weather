package com.example.android.weather.ui.mylocations;


import android.content.Context;
import android.content.Intent;

import com.example.android.weather.db.Location;
import com.example.android.weather.db.WeatherRepository;
import com.example.android.weather.rest.ApiService;
import com.example.android.weather.rest.ApiUtils;
import com.example.android.weather.ui.forecast.WeatherActivity;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MyLocationsPresenter implements MyLocationsContract.Presenter{

    private MyLocationsContract.View mView;
    private WeatherRepository mRepository;
    private ArrayList<Location> mLocations;
    private boolean mSubscribed,mComplete;

    public MyLocationsPresenter(MyLocationsContract.View view, WeatherRepository repository) {
        mView = view;
        mRepository = repository;
    }

    @Override
    public void getLocations() {
        if(mComplete){
            mView.displayLocations(mLocations.size());
        }
        else if(!mSubscribed) {
            Observable<ArrayList<Location>> observable = Observable.create(new ObservableOnSubscribe<ArrayList<Location>>() {
                @Override
                public void subscribe(ObservableEmitter<ArrayList<Location>> e) throws Exception {
                    mSubscribed = true;
                    e.onNext(mRepository.getLoactions());
                }
            });

            Consumer<ArrayList<Location>> consumer = new Consumer<ArrayList<Location>>() {
                @Override
                public void accept(@NonNull ArrayList<Location> locations) throws Exception {
                    mLocations = locations;
                    mView.displayLocations(mLocations.size());
                    mComplete = true;
                }
            };

            observable.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(consumer);
        }
    }

    @Override
    public String getName(int position) {
        String name = mLocations.get(position).name;
        return name!=null ? name : "";
    }

    @Override
    public String getLatLong(int position) {
        double lat = mLocations.get(position).latitude;
        String sLat = String.valueOf(lat);

        double lng = mLocations.get(position).longitude;
        String sLng = String.valueOf(lng);

        return sLat + ", " + sLng;
    }

    @Override
    public boolean getDisplayed(int position) {
        return mLocations.get(position).display;
    }

    @Override
    public void removeLocation(int position) {
        final int id = mLocations.get(position).id;
        mLocations.remove(position);

        Thread thread = new Thread() {
            @Override
            public void run() {
             mRepository.delete(id);
            }
        };
        thread.start();
    }

    @Override
    public void changedDisplayed(final int position,final boolean display) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                mRepository.changeDisplay(mLocations.get(position).id,display);
            }
        };
        thread.start();
    }

    @Override
    public Intent getIntentForWeatherActivity(Context context, int position) {
        return WeatherActivity.getIntent(context,
                mLocations.get(position).name,
                mLocations.get(position).latitude,
                mLocations.get(position).longitude);
    }
}

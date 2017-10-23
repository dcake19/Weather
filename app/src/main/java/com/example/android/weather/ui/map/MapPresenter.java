package com.example.android.weather.ui.map;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.android.weather.BuildConfig;
import com.example.android.weather.db.Location;
import com.example.android.weather.db.WeatherRepository;
import com.example.android.weather.rest.ApiServiceLocation;
import com.example.android.weather.rest.ApiUtils;
import com.example.android.weather.rest.citysearch.AddressComponent;
import com.example.android.weather.rest.citysearch.CitySearchResults;
import com.example.android.weather.ui.forecast.WeatherActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MapPresenter implements MapContract.Presenter{

    private MapContract.View mView;
    private WeatherRepository mRepository;
    private ApiServiceLocation mApiServiceLocation;
    private ArrayList<Location> mLocations;
    private String mLastSearchedName = "";
    private double mLastSearchedLat;
    private double mLastSearchedLng;
    private boolean mSubscribed,mComplete;

    public MapPresenter( MapContract.View view,WeatherRepository repository) {
        mRepository = repository;
        mView = view;
        mApiServiceLocation = ApiUtils.getApiServiceLocation();
    }

    @Override
    public void getLocations() {
        if(mComplete){
            mView.locationsReady(mLocations.size());
        }
        else if(!mSubscribed) {
            Observable<ArrayList<Location>> observable = Observable.create(new ObservableOnSubscribe<ArrayList<Location>>() {
                @Override
                public void subscribe(ObservableEmitter<ArrayList<Location>> e) throws Exception {
                    mSubscribed = true;
                    e.onNext(mRepository.getLoactionsForMap());
                }
            });

            Consumer<ArrayList<Location>> consumer = new Consumer<ArrayList<Location>>() {
                @Override
                public void accept(@NonNull ArrayList<Location> locations) throws Exception {
                    mLocations = locations;
                    mView.locationsReady(mLocations.size());
                    mComplete = true;
                }
            };

            observable.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(consumer);
        }
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

    @Override
    public void searchForLocation(String location) {

        mApiServiceLocation.getLocation(location, BuildConfig.GEOCODING_API_KEY).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CitySearchResults>(){
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CitySearchResults citySearchResults) {
                        if(citySearchResults.getResults().size() >= 1) {
                            List<AddressComponent> addressComponent = citySearchResults.getResults().get(0).getAddressComponents();

                            boolean nameFound = false;
                            int i=0;
                            while(!nameFound && i<addressComponent.size()){
                                List<String> types = addressComponent.get(i).getTypes();
                                for(int j=0;j<types.size();j++){
                                    if(types.get(j).equals("locality") || types.get(j).equals("political") || types.get(j).equals("postal_town")) {
                                        mLastSearchedName = addressComponent.get(i).getShortName();
                                        nameFound = true;
                                        break;
                                    }
                                }
                                i++;
                            }
                        }
                        mLastSearchedLat = citySearchResults.getResults().get(0).getGeometry().getLocation().getLat();
                        mLastSearchedLng = citySearchResults.getResults().get(0).getGeometry().getLocation().getLng();
                        mView.searchComplete(mLastSearchedName,mLastSearchedLat,mLastSearchedLng);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.error();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public void saveSearchedTerm(final String name,final double lat,final double lng) {
        mLocations.add(new Location(0,name,lat,lng,1));

        Thread thread = new Thread() {
            @Override
            public void run() {
                mRepository.insert(name,lat,lng);
            }
        };
        thread.start();
    }


    @Override
    public String getLastSearchedName() {
        return mLastSearchedName;
    }

    @Override
    public double getLastSearchedLat() {
        return mLastSearchedLat;
    }

    @Override
    public double getLastSearchedLng() {
        return mLastSearchedLng;
    }
}

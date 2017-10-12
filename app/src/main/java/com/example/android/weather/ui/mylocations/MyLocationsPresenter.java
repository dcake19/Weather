package com.example.android.weather.ui.mylocations;


import com.example.android.weather.db.Location;
import com.example.android.weather.db.WeatherRepository;
import com.example.android.weather.rest.ApiService;
import com.example.android.weather.rest.ApiUtils;

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

    public MyLocationsPresenter(MyLocationsContract.View view, WeatherRepository repository) {
        mView = view;
        mRepository = repository;

//       mRepository.insert("Cambridge",52.2053,0.1218);
//        mRepository.insert("Leeds",53.8008,1.5491);
//        mRepository.insert("Birmingham",52.4862, 1.8904);
//        mRepository.insert("Manchester",53.4808, 2.2426);
//        mRepository.insert("Glasgow",55.8642, 4.2518);
//        mRepository.insert("Bristol",51.4545, 2.5879);

    }

    @Override
    public void getLocations() {
        Observable<ArrayList<Location>> observable = Observable.create(new ObservableOnSubscribe<ArrayList<Location>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<Location>> e) throws Exception {
                e.onNext(mRepository.getLoactions());
            }
        });

        Consumer<ArrayList<Location>> consumer = new Consumer<ArrayList<Location>>() {
            @Override
            public void accept(@NonNull ArrayList<Location> locations) throws Exception {
                mLocations = locations;
                mView.displayLocations(mLocations.size());
            }
        };

        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(consumer);
    }

    @Override
    public String getName(int position) {
        String name = mLocations.get(position).name;
        return name!=null ? name : "";
    }

    @Override
    public String getLatLong(int position) {
        return mLocations.get(position).latitude + ", " + mLocations.get(position).longitude;
    }

    @Override
    public boolean getDisplayed(int position) {
        return mLocations.get(position).display;
    }

    @Override
    public void removeLocation(int position) {
        int id = mLocations.get(position).id;
        mLocations.remove(position);
        mRepository.delete(id);
    }

    @Override
    public void changedDisplayed(int position,boolean display) {
        mRepository.changeDisplay(mLocations.get(position).id,display);
    }
}

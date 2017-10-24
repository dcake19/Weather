package com.example.android.weather.ui.forecast;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.android.weather.BuildConfig;
import com.example.android.weather.R;
import com.example.android.weather.db.WeatherRepository;
import com.example.android.weather.rest.ApiServiceLocation;
import com.example.android.weather.rest.citysearch.AddressComponent;
import com.example.android.weather.rest.citysearch.CitySearchResults;
import com.example.android.weather.rest.model.Datum_;
import com.example.android.weather.rest.model.Datum__;
import com.example.android.weather.ui.forecast.WeatherContract;
import com.example.android.weather.rest.ApiService;
import com.example.android.weather.rest.ApiUtils;
import com.example.android.weather.rest.model.Daily;
import com.example.android.weather.rest.model.Hourly;
import com.example.android.weather.rest.model.WeatherForecast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class WeatherPresenter implements WeatherContract.Presenter {

    private WeatherContract.View mView;
    private ApiService mApiService;
    private ApiServiceLocation mApiServiceLocation;
    private WeatherRepository mRepository;
    private SharedPreferences mSharedPreferences;
    private Daily mDaily;
    private Hourly mHourly;
    private String mStringLatitude;
    private String mStringLongitude;
    private Double mLatitude;
    private Double mLongitude;
    private String mName;
    private final String INIT_SELECTION = "init_selection";
    private final String DAILY_SELECTION = "daily_selection";

    private boolean mLocationSubscribed,mForecastSubscribed,mLocationComplete,mForecastComplete;

    public WeatherPresenter(WeatherContract.View view, WeatherRepository repository, SharedPreferences sharedPreferences) {
        mView = view;
        mRepository = repository;
        mApiService = ApiUtils.getApiService();
        mApiServiceLocation = ApiUtils.getApiServiceLocation();
        mSharedPreferences = sharedPreferences;
    }

    @Override
    public void downloadForecast(boolean daily) {
        downloadForecast(mName,mLatitude,mLongitude,daily);
    }

    @Override
    public void downloadForecast(String name,double latitude,double longitude,final boolean daily) {

        mLatitude = latitude;
        mLongitude = longitude;

        mStringLatitude = String.valueOf(latitude);
        mStringLongitude = String.valueOf(longitude);

        if(name==null || name.equals("")) name = WeatherActivity.DISPLAY_LAT_LNG;
        if(name.equals(WeatherActivity.DISPLAY_LAT_LNG)){
            mName = WeatherActivity.DISPLAY_LAT_LNG;

            if(mLocationSubscribed && mLocationComplete)
                mView.setName(mName);
            else if (!mLocationSubscribed)
                subscribeLocationName();
        }else{
            mName = name;
            mView.setName(mName);
        }

        if (mForecastSubscribed && mForecastComplete) {
            displayForecast(daily);
        }
        else if (!mForecastSubscribed) {
            subscribeForecast(daily);
        }
    }

    private void subscribeLocationName(){
        mApiServiceLocation.getLocation(getLatLong(), BuildConfig.GEOCODING_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<CitySearchResults, String>() {
                    @Override
                    public String apply(CitySearchResults citySearchResults) throws Exception {
                        String name = "";
                        if(citySearchResults.getResults().size() >= 1) {
                            List<AddressComponent> addressComponent = citySearchResults.getResults().get(0).getAddressComponents();

                            boolean nameFound = false;
                            int i=0;
                            while(!nameFound && i<addressComponent.size()){
                                List<String> types = addressComponent.get(i).getTypes();
                                for(int j=0;j<types.size();j++){
                                    if(types.get(j).equals("locality") || types.get(j).equals("political") || types.get(j).equals("postal_town")) {
                                        name = addressComponent.get(i).getShortName();
                                        nameFound = true;
                                        break;
                                    }
                                }
                                i++;
                            }
                        }

                       return name;
                    }
                })
                .subscribe(new Observer<String>(){
                    @Override
                    public void onSubscribe(Disposable d) {
                        mLocationSubscribed = true;
                    }

                    @Override
                    public void onNext(String locationName) {

                        mName = locationName;
                        mView.setName(mName);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mLocationSubscribed = false;
                        mName = "";
                        mView.setName(getLatLong());
                    }

                    @Override
                    public void onComplete() {
                        mLocationComplete = true;
                    }
                });
    }

    private void subscribeForecast(final boolean daily){
        mApiService.getForecast(getLatLong()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WeatherForecast>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mForecastSubscribed = true;
                    }

                    @Override
                    public void onNext(WeatherForecast weatherForecast) {
                        mDaily = weatherForecast.getDaily();
                        mHourly = weatherForecast.getHourly();

                        displayForecast(daily);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mForecastSubscribed = false;
                        mView.error();
                    }

                    @Override
                    public void onComplete() {
                        mForecastComplete = true;
                    }
                });
    }

    private void displayForecast(boolean daily){
        if (daily)
            mView.displayDaily(mDaily.getData().size());
        else
            mView.displayHourly(mHourly.getData().size());
    }

    @Override
    public String getName() {
        return mName;
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
        String temp = formatDoubleAsString(0,mDaily.getData().get(position+1).getTemperatureHigh());
        return temp + "\u2103";
    }

    @Override
    public String getLowTemp(Context context,int position) {
        String temp = formatDoubleAsString(0,mDaily.getData().get(position+1).getTemperatureLow());
        return temp + "\u2103";
    }

    @Override
    public String getWindSpeedDaily(Context context,int position) {
        String windSpeed = formatDoubleAsString(1, mDaily.getData().get(position+1).getWindSpeed());
        return  windSpeed + " " + context.getString(R.string.wind_speed_units) ;
    }

    @Override
    public String getPrecipDaily(Context context,int position) {
        String chance = formatDoubleAsString(0,mDaily.getData().get(position+1).getPrecipProbability()*100);

        String precipType = mDaily.getData().get(position+1).getPrecipType();
        if(precipType == null) precipType = "rain";
        return chance + "\u0025"
                + " " +context.getString(R.string.chance)
                + " " + precipType;
    }


    private String formatDoubleAsString(int decimalsPlaces,Double value){
        if (decimalsPlaces>0) decimalsPlaces++;
        String string = Double.toString(value);
        if(value>=10) {
            if(string.length()>=2+decimalsPlaces)
                string = string.substring(0, 2 + decimalsPlaces);
        }
        else
        if(string.length()>=1+decimalsPlaces)
            string = string.substring(0,1+decimalsPlaces);

        return string;
    }

    private String getPrecipDailyEmail(Context context,int position) {
        String chance = formatDoubleAsString(0,mDaily.getData().get(position+1).getPrecipProbability()*100);
        String precipType = mDaily.getData().get(position+1).getPrecipType();
        if(precipType == null) precipType = "rain";

        return chance + " pct."
                + " " +context.getString(R.string.chance)
                + " " + precipType;
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
    public void saveLocation(final String locationName) {

        Thread thread = new Thread() {
            @Override
            public void run() {
                mRepository.insert(locationName,mLatitude,mLongitude);
            }
        };
        thread.start();

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
            shareBody.append(getPrecipDailyEmail(context,i)+"\n");
            shareBody.append("\n");
        }

        return shareBody.toString();
    }

    @Override
    public String getWeatherSpeakDaily(int position) {
        Datum__ data = mDaily.getData().get(position+1);
        Double precipChance = (data.getPrecipProbability()*100);
        String precipType = data.getPrecipType();
        if(precipType==null) precipType = "rain";

        return data.getSummary() + "The high temperature for the day is " + data.getTemperatureHigh().intValue() + " degrees celcius." +
                "The low temperature for the day is " + data.getTemperatureLow().intValue() + " degrees celcius." +
                "The wind speed is " + formatDoubleAsString(1, data.getWindSpeed()) + " meters per second." +
                "There is a " + precipChance.intValue() + "percent chance of " + precipType;
    }




    @Override
    public String getTime(int position) {
        int time = mHourly.getData().get(position).getTime();
        long javaTime = time;
        javaTime *= 1000;
        Date date = new Date(javaTime);
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        String hour = sdf.format(date);
        int h = Integer.valueOf(hour);
        String display;
        if(h==12)
            display = "12 pm";
        else if(h==0)
            display = "12 am";
        else if(h<12)
            display = h + " am";
        else
            display = (h-12) + " pm";
        return display;
    }

    @Override
    public String getTempHourly(int position) {
        return formatDoubleAsString(0,mHourly.getData().get(position).getTemperature()) +  "\u2103";
    }

    @Override
    public String getWindSpeedHourly(Context context, int position) {
        String windSpeed = formatDoubleAsString(1, mHourly.getData().get(position).getWindSpeed());
        return  windSpeed + " " + context.getString(R.string.wind_speed_units) ;
    }

    @Override
    public String getPrecipHourly(Context context, int position) {
        String chance = formatDoubleAsString(0,mHourly.getData().get(position).getPrecipProbability()*100);

        String precipType = mHourly.getData().get(position).getPrecipType();
        if(precipType == null) precipType = "rain";
        return chance + "\u0025"
                + " " +context.getString(R.string.chance)
                + " " + precipType;
    }

    @Override
    public int getIconHourly(Context context, int position) {
        String icon =  mHourly.getData().get(position).getIcon();
        return weatherIcon(icon);
    }

    @Override
    public String getWeatherSpeakHourly(int position) {
        Datum_ data = mHourly.getData().get(position);
        Double precipChance = (data.getPrecipProbability()*100);
        String precipType = data.getPrecipType();
        if(precipType==null) precipType = "rain";

        return data.getSummary() + ". The temperature will be " + data.getTemperature().intValue() + " degrees celcius." +
                "The wind speed will be " + formatDoubleAsString(1, data.getWindSpeed()) + " meters per second." +
                "There is a " + precipChance.intValue() + "percent chance of " + precipType;
    }

    @Override
    public String getShareSubjectHourly(Context context) {
        return context.getString(R.string.weather_for_next) + " 24 hours.";
    }

    @Override
    public String getShareBodyHourly(Context context) {
        StringBuilder shareBody = new StringBuilder();

        for(int i=0;i<24;i++) {
            shareBody.append(getTime(i)+"\n");
            shareBody.append(getSummaryHourly(i)+"\n");
            shareBody.append("Temperature: " + getTempHourly(i)+"\n");
            shareBody.append("Wind Speed: " + getWindSpeedHourly(context,i)+"\n");
            shareBody.append(getPrecipHourlyEmail(context,i)+"\n");
            shareBody.append("\n");
        }

        return shareBody.toString();
    }

    @Override
    public boolean getInitialDaily() {
        return mSharedPreferences.getBoolean(DAILY_SELECTION,true);
    }

    @Override
    public int getInitialSelection() {
        return mSharedPreferences.getInt(INIT_SELECTION,6);
    }

    @Override
    public void saveDaily(boolean daily) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(DAILY_SELECTION,daily);
        editor.commit();
    }

    @Override
    public void saveSelection(int selection) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(INIT_SELECTION,selection);
        editor.commit();
    }

    private String getSummaryHourly(int position){
        return mHourly.getData().get(position).getSummary();
    }

    private String getPrecipHourlyEmail(Context context,int position) {
        String chance = formatDoubleAsString(0,mHourly.getData().get(position).getPrecipProbability()*100);
        String precipType = mHourly.getData().get(position).getPrecipType();
        if(precipType == null) precipType = "rain";

        return chance + " pct."
                + " " + context.getString(R.string.chance)
                + " " + precipType;
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
        else if(icon.equals("partly-cloudy-night"))
            return R.drawable.art_partly_cloudy_night;
        return R.drawable.art_partly_cloudy_day;
    }



}

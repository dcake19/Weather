package com.example.android.weather.ui.forecast;


import com.example.android.weather.db.WeatherRepository;

public class WeatherPresenterClient {

    private static WeatherPresenter presenter = null;

   public static WeatherPresenter getPresenter
           (boolean getNew, WeatherContract.View view, WeatherRepository repository){
       if(presenter == null || getNew){
           return new WeatherPresenter(view, repository);
       }
       else{
           presenter.setViewAndRepository(view, repository);
           return presenter;
       }

   }

}

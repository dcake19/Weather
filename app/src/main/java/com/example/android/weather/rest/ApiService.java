package com.example.android.weather.rest;


import com.example.android.weather.rest.model.WeatherForecast;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("/forecast/53755ef8d246f8c0be06fe90f87ec742/52.2053,0.1218/")
    Observable<WeatherForecast> getForecast();

    @GET("/forecast/53755ef8d246f8c0be06fe90f87ec742/{latlong}")
    Observable<WeatherForecast> getForecast(
            @Path("latlong") String latlong);


//    @GET("/forecast/53755ef8d246f8c0be06fe90f87ec742/52.2053,0.1218/")
//    Call<WeatherForecast> getForecast();

}

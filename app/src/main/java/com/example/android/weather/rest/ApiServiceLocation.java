package com.example.android.weather.rest;


import com.example.android.weather.rest.citysearch.CitySearchResults;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiServiceLocation {

    @GET("/maps/api/geocode/json?address=London&key=AIzaSyDWAeoc_1ZqPvlzNCbMkw5TiEB-uLc-YVY")
    Observable<CitySearchResults> getLocation();

    @GET("/maps/api/geocode/json")
    Observable<CitySearchResults> getLocation(
            @Query("address") String location,
            @Query("key") String apiKeyLocation);

}

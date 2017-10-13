package com.example.android.weather.rest;


import android.animation.AnimatorSet;

public class ApiUtils {
    private ApiUtils() {}

    public static ApiService getApiService() {

        return RetrofitClient.getClient().create(ApiService.class);
    }

    public static ApiServiceLocation getApiServiceLocation() {

        return RetrofitClient.getClientLocation().create(ApiServiceLocation.class);
    }


}

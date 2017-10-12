package com.example.android.weather.db;

public class Location {

    public String name;
    public double latitude;
    public double longitude;
    public boolean display;

    public Location(String name, double latitude, double longitude, int display) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.display = display==1;
    }
}

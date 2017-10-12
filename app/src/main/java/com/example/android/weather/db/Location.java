package com.example.android.weather.db;

public class Location {
    public int id;
    public String name;
    public double latitude;
    public double longitude;
    public boolean display;

    public Location(int id,String name, double latitude, double longitude, int display) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.display = display==1;
    }
}

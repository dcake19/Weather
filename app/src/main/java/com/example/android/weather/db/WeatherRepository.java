package com.example.android.weather.db;

import com.example.android.weather.db.WeatherDbContract.*;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;

public class WeatherRepository {

    private Context mContext;


    public WeatherRepository(Context context) {
        mContext = context;
    }

    public void insert(String name,double latitude,double longitude){
        ContentValues values = new ContentValues();
        values.put(LocationsEntry.COLUMN_NAME,name);
        values.put(LocationsEntry.COLUMN_LATITUDE,latitude);
        values.put(LocationsEntry.COLUMN_LONGITUDE,longitude);

        mContext.getContentResolver().insert(LocationsEntry.CONTENT_URI,values);
    }

    public ArrayList<Location> getLoactions(){

        Cursor cursor = mContext.getContentResolver().query(LocationsEntry.CONTENT_URI,null,null,null,null);

        ArrayList<Location> locations = new ArrayList<>();

        while (cursor.moveToNext()){
            locations.add(new Location(
                    cursor.getInt(cursor.getColumnIndex(LocationsEntry._ID)),
                    cursor.getString(cursor.getColumnIndex(LocationsEntry.COLUMN_NAME)),
                    cursor.getDouble(cursor.getColumnIndex(LocationsEntry.COLUMN_LATITUDE)),
                    cursor.getDouble(cursor.getColumnIndex(LocationsEntry.COLUMN_LONGITUDE)),
                    cursor.getInt(cursor.getColumnIndex(LocationsEntry.COLUMN_DISPLAY_ON_MAP))));
        }

        cursor.close();

        return locations;
    }

    public ArrayList<Location> getLoactionsForMap(){

        String[] selectionArgs = {"1"};
        String where = LocationsEntry.COLUMN_DISPLAY_ON_MAP + "=?";

        Cursor cursor = mContext.getContentResolver().query(LocationsEntry.CONTENT_URI,null,where,selectionArgs,null);

        ArrayList<Location> locations = new ArrayList<>();

        while (cursor.moveToNext()){
            locations.add(new Location(
                    cursor.getInt(cursor.getColumnIndex(LocationsEntry._ID)),
                    cursor.getString(cursor.getColumnIndex(LocationsEntry.COLUMN_NAME)),
                    cursor.getDouble(cursor.getColumnIndex(LocationsEntry.COLUMN_LATITUDE)),
                    cursor.getDouble(cursor.getColumnIndex(LocationsEntry.COLUMN_LONGITUDE)),
                    cursor.getInt(cursor.getColumnIndex(LocationsEntry.COLUMN_DISPLAY_ON_MAP))));
        }

        cursor.close();

        return locations;
    }

    public void delete(int id){
        String[] selectionArgs = {String.valueOf(id)};
        String where = LocationsEntry._ID + "=?";
        mContext.getContentResolver().delete(LocationsEntry.CONTENT_URI,where,selectionArgs);
    }

    public void changeDisplay(int id,boolean display){
        int intDisplay = display ? 1 : 0;

        ContentValues values = new ContentValues();
        values.put(LocationsEntry.COLUMN_DISPLAY_ON_MAP,intDisplay);
        String[] selectionArgs = {String.valueOf(id)};
        String where = LocationsEntry._ID + "=?";

        mContext.getContentResolver().update(LocationsEntry.CONTENT_URI,values,where,selectionArgs);
    }


}

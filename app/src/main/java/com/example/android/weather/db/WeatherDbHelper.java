package com.example.android.weather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.weather.db.WeatherDbContract.*;

public class WeatherDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "weather.db";
    private static final int DATABASE_VERSION = 1;


    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_LOCATIONS_TABLE = "CREATE TABLE " + LocationsEntry.TABLE_NAME + "("
                + LocationsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + LocationsEntry.COLUMN_NAME + " TEXT, "
                + LocationsEntry.COLUMN_LATITUDE + " REAL NOT NULL, "
                + LocationsEntry.COLUMN_LONGITUDE + " REAL NOT NULL, "
                + LocationsEntry.COLUMN_DISPLAY_ON_MAP + " INTEGER NOT NULL DEFAULT 1);";

        db.execSQL(SQL_CREATE_LOCATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

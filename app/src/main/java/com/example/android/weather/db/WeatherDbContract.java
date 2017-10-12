package com.example.android.weather.db;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class WeatherDbContract {

    private WeatherDbContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.android.weather";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_LOCATION = "location";

    public static final class LocationsEntry implements BaseColumns{
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_LOCATION);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String TABLE_NAME = "locations";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DISPLAY_ON_MAP = "display";
    }

}

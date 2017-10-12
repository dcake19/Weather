package com.example.android.weather.db;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import com.example.android.weather.db.WeatherDbContract.*;

public class WeatherProvider extends ContentProvider {

    private WeatherDbHelper mDbHelper;

    private static final int MY_LOCATIONS = 100;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(WeatherDbContract.CONTENT_AUTHORITY,WeatherDbContract.PATH_LOCATION,MY_LOCATIONS);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new WeatherDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] columns, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match){
            case MY_LOCATIONS:
                cursor = database.query(LocationsEntry.TABLE_NAME,columns,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case MY_LOCATIONS:
                return LocationsEntry.CONTENT_LIST_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);

        if(match==MY_LOCATIONS){
            SQLiteDatabase database = mDbHelper.getWritableDatabase();
            long id = database.insert(LocationsEntry.TABLE_NAME,null,values);
            return ContentUris.withAppendedId(uri,id);
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}

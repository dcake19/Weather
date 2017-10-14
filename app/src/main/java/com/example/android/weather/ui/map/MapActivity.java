package com.example.android.weather.ui.map;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.android.weather.R;
import com.example.android.weather.db.WeatherRepository;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        MapContract.View{

    private MapContract.Presenter mPresenter;

    private GoogleMap mMap;
    private boolean mReady=false;
    private ArrayList<MarkerOptions> mMarkers;

    @BindView(R.id.layout_map) LinearLayout mLayout;
    @BindView(R.id.txt_search) EditText mSearchTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        ButterKnife.bind(this);

        mPresenter = new MapPresenter(this,new WeatherRepository(this));
        mMarkers = new ArrayList<>();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @OnClick({R.id.btn_map,R.id.btn_terrain,R.id.btn_satellite})
    public void changeMap(View view){
        if(view.getId()==R.id.btn_map){
            if(mReady)
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        else if(view.getId()==R.id.btn_terrain){
            if(mReady)
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }
        else if(view.getId()==R.id.btn_satellite){
            if(mReady)
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
    }

    @OnClick(R.id.btn_search)
    public void search(){
        mPresenter.searchForLocation(mSearchTerm.getText().toString());
    }

    @OnClick(R.id.fab_speak)
    public void speak(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        try{
            startActivityForResult(intent,200);
        }catch (ActivityNotFoundException e){

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==200){
            if(resultCode==RESULT_OK && data != null){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                mSearchTerm.setText(result.get(0));
                search();
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mReady = true;
        mPresenter.getLocations();

    }

    @Override
    public void locationsReady(int size) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for(int i=0;i<size;i++){
            mMarkers.add(new MarkerOptions()
                    .position(new LatLng(mPresenter.getLatitude(i),mPresenter.getLongitude(i))));
            mMarkers.get(i).title(Integer.toString(i));
            mMap.addMarker(mMarkers.get(i));
            builder.include(mMarkers.get(i).getPosition());
        }

        mMap.setOnMarkerClickListener(this);
        if(mMarkers.size()>0) {
            int padding = 120; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(builder.build(), padding);
            mMap.moveCamera(cu);
        }

    }

    @Override
    public void searchComplete(final String name, final double lat, final double lng) {
        CameraPosition target = CameraPosition.builder().target(new LatLng(lat,lng)).zoom(10).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(target));

        Snackbar.make(mLayout, getResources().getString(R.string.add_to_my_locations), Snackbar.LENGTH_INDEFINITE)
                .setAction(getResources().getString(android.R.string.yes),
                        new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                mPresenter.saveSearchedTerm(name,lat,lng);
                                mMarkers.add(new MarkerOptions().position(new LatLng(lat,lng)));
                                int last = mMarkers.size()-1;
                                mMarkers.get(last).title(Integer.toString(last));
                                mMap.addMarker(mMarkers.get(last));
                            }
                        }
                )
                .show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        int position = Integer.valueOf(marker.getTitle());
        Intent intent = mPresenter.getIntentForWeatherActivity(getBaseContext(),position);
        startActivity(intent);

        return true;
    }


}

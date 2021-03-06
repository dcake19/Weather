package com.example.android.weather.ui.map;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LocationsMapFragment extends Fragment implements MapContract.View,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener{

    private final String SAVE_MAP_TYPE = "map_type";
    private int mMapType = GoogleMap.MAP_TYPE_NORMAL;

    private final String SAVE_CAMERA_POSITION = "camera_position";
    private CameraPosition mCameraPosition;

    private final String SAVE_LOCATION_SNACKBAR = "location_snackbar";

    private MapContract.Presenter mPresenter;

    private GoogleMap mMap;
    private boolean mReady=false;
    private ArrayList<MarkerOptions> mMarkers;

    @BindView(R.id.layout_map)
    LinearLayout mLayout;
    @BindView(R.id.txt_search)
    EditText mSearchTerm;

    private Snackbar mLocationSnackbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = new MapPresenter(this,new WeatherRepository(getActivity().getApplicationContext()));
        mMarkers = new ArrayList<>();

        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.map_fragment, container, false);
        ButterKnife.bind(this,rootview);

        if(savedInstanceState!=null) {
            mMapType = savedInstanceState.getInt(SAVE_MAP_TYPE, GoogleMap.MAP_TYPE_NORMAL);
            mCameraPosition = savedInstanceState.getParcelable(SAVE_CAMERA_POSITION);
            if(savedInstanceState.getBoolean(SAVE_LOCATION_SNACKBAR,false))
                showLocationSnackbar(mPresenter.getLastSearchedName(),
                        mPresenter.getLastSearchedLat(),
                        mPresenter.getLastSearchedLng());
        }

        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        return rootview;
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
        ((MapActivity)getActivity()).speak();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(mMapType);
        mReady = true;
        mPresenter.getLocations();
    }

    @Override
    public void locationsReady(int size) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if(mMarkers.size()==0) {
            for (int i = 0; i < size; i++) {
                mMarkers.add(new MarkerOptions()
                        .position(new LatLng(mPresenter.getLatitude(i), mPresenter.getLongitude(i))));
                mMarkers.get(i).title(Integer.toString(i));
                builder.include(mMarkers.get(i).getPosition());
            }
        }

        for (int i = 0; i < size; i++) {
            mMap.addMarker(mMarkers.get(i));
        }

        mMap.setOnMarkerClickListener(this);

        if(mCameraPosition != null){
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        }
        else if(mMarkers.size()>0) {
            int padding = 120; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(builder.build(), padding);
            mMap.moveCamera(cu);
        }

    }

    @Override
    public void searchComplete(final String name, final double lat, final double lng) {
        CameraPosition target = CameraPosition.builder().target(new LatLng(lat,lng)).zoom(10).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(target));

        showLocationSnackbar(name, lat, lng);
    }

    private void showLocationSnackbar(final String name, final double lat, final double lng){
        mLocationSnackbar = Snackbar.make(mLayout, getResources().getString(R.string.add_to_my_locations), Snackbar.LENGTH_INDEFINITE)
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
                );

        mLocationSnackbar.show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        int position = Integer.valueOf(marker.getTitle());
        Intent intent = mPresenter.getIntentForWeatherActivity(getContext(),position);
        startActivity(intent);

        return true;
    }

    @Override
    public void error() {
        Snackbar.make(mLayout, getResources().getString(R.string.error), Snackbar.LENGTH_INDEFINITE)
                .setAction(getResources().getString(R.string.retry),
                        new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                mPresenter.searchForLocation(mSearchTerm.getText().toString());
                            }
                        }
                )
                .show();
    }

    public void search(String searchTerm){
        mSearchTerm.setText(searchTerm);
        search();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_MAP_TYPE,mMap.getMapType());
        outState.putParcelable(SAVE_CAMERA_POSITION,mMap.getCameraPosition());
        if(mLocationSnackbar!=null)
            outState.putBoolean(SAVE_LOCATION_SNACKBAR,mLocationSnackbar.isShown());
        else
            outState.putBoolean(SAVE_LOCATION_SNACKBAR,false);
    }
}

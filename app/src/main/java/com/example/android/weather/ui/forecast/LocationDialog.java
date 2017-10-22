package com.example.android.weather.ui.forecast;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.weather.R;

@SuppressLint("ValidFragment")
public class LocationDialog extends DialogFragment implements View.OnClickListener{

   // WeatherActivity mWeatherActivity;
    WeatherContract.Presenter mPresenter;
    private String mLatitude;
    private String mLongitude;
    private EditText mEditText;
    private String mName;

//    public LocationDialog(WeatherActivity activity,String name,String latitude, String longitude) {
//        mWeatherActivity = activity;
//        mName = name;
//        mLatitude = latitude;
//        mLongitude = longitude;
//    }

    public LocationDialog(WeatherContract.Presenter presenter,String name,String latitude, String longitude) {
        mPresenter = presenter;
        mName = name;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview =  inflater.inflate(R.layout.weather_location_dialog, container, false);

        mEditText = (EditText) rootview.findViewById(R.id.edittext_location_name);
        mEditText.setText(mName);
        TextView latitude = (TextView) rootview.findViewById(R.id.textview_latitude);
        latitude.setText(mLatitude);
        TextView longitude = (TextView) rootview.findViewById(R.id.textview_longitude);
        longitude.setText(mLongitude);
        Button save = (Button) rootview.findViewById(R.id.btn_save);
        save.setOnClickListener(this);

        Button dismiss = (Button) rootview.findViewById(R.id.btn_dismiss);
        dismiss.setOnClickListener(this);

        return rootview;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_save:
                //mWeatherActivity.saveLocation(mEditText.getText().toString());
                mPresenter.saveLocation(mEditText.getText().toString());
                dismiss();
                break;
            case  R.id.btn_dismiss:
                dismiss();
                break;
        }
    }
}

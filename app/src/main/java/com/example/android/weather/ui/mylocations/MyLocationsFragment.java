package com.example.android.weather.ui.mylocations;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.weather.R;
import com.example.android.weather.db.WeatherRepository;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MyLocationsFragment extends Fragment implements MyLocationsContract.View{

    MyLocationsContract.Presenter mPresenter;
    MyLocationsAdapter mAdapter;

    @BindView(R.id.recyclerview_locations)
    RecyclerView mRecyclerView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = new MyLocationsPresenter(this,new WeatherRepository(getContext()));

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.my_locations_fragment, container, false);
        ButterKnife.bind(this,rootview);

        setRecyclerView();

        mPresenter.getLocations();

        setRetainInstance(true);

        return rootview;
    }

    private void setRecyclerView(){
        mAdapter = new MyLocationsAdapter((MyLocationsActivity)getActivity(),getContext(),mPresenter);
        mRecyclerView.setAdapter(mAdapter);
        GridLayoutManager glm = new GridLayoutManager(getContext(),1);
        mRecyclerView.setLayoutManager(glm);
    }


    @Override
    public void displayLocations(int size) {
        mAdapter.setSize(size);
    }

}

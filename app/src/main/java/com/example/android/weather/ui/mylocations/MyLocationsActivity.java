package com.example.android.weather.ui.mylocations;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.weather.R;

public class MyLocationsActivity extends AppCompatActivity {

//    MyLocationsContract.Presenter mPresenter;
//    MyLocationsAdapter mAdapter;
//
//    @BindView(R.id.recyclerview_locations)
//    RecyclerView mRecyclerView;

    private final String FRAGMENT = "My Locations Fragment";
    private MyLocationsFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_locations_activity);

        getSupportActionBar().setTitle(getResources().getString(R.string.my_locations));

        FragmentManager fm = getSupportFragmentManager();
        mFragment = (MyLocationsFragment) fm.findFragmentByTag(FRAGMENT);

        if(mFragment==null) {
            mFragment = new MyLocationsFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            fragmentTransaction.add(R.id.my_locations_content, mFragment, FRAGMENT);
            fragmentTransaction.commit();
        }

      //  ButterKnife.bind(this);

     //   mPresenter = new MyLocationsPresenter(this,new WeatherRepository(getBaseContext()));
   //     setRecyclerView();

      //  mPresenter.getLocations();
    }

//
//    private void setRecyclerView(){
//        mAdapter = new MyLocationsAdapter(this,getBaseContext(),mPresenter);
//        mRecyclerView.setAdapter(mAdapter);
//        GridLayoutManager glm = new GridLayoutManager(this,1);
//        mRecyclerView.setLayoutManager(glm);
//    }
//
//
//    @Override
//    public void displayLocations(int size) {
//        mAdapter.setSize(size);
//    }
}

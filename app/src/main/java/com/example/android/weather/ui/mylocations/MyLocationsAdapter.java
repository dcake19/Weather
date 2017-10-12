package com.example.android.weather.ui.mylocations;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.weather.R;
import com.example.android.weather.ui.forecast.WeatherDisplayAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyLocationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    MyLocationsActivity mActivity;
    private Context mContext;
    private int mSize = 0;
    private MyLocationsContract.Presenter mPresenter;

    public MyLocationsAdapter(MyLocationsActivity activity,Context context,MyLocationsContract.Presenter presenter) {
        mActivity = activity;
        mContext = context;
        mPresenter = presenter;
    }

    public void setSize(int size){
        mSize = size;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_locations_list_item,parent,false);
        return new MyLocationsAdapter.ViewHolderLocations(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolderLocations holderLocations = (ViewHolderLocations) holder;
        holderLocations.locationName.setText(mPresenter.getName(position));
        holderLocations.locationCoords.setText(mPresenter.getLatLong(position));
        holderLocations.displayed.setChecked(mPresenter.getDisplayed(position));
    }

    @Override
    public int getItemCount() {
        return mSize;
    }

    class ViewHolderLocations extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.layout_location) LinearLayout layout;
        @BindView(R.id.location_name) TextView locationName;
        @BindView(R.id.location_coords) TextView locationCoords;
        @BindView(R.id.checkbox_displayed) CheckBox displayed;
        @BindView(R.id.btn_delete) ImageButton delete;

        public ViewHolderLocations(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            layout.setOnClickListener(this);
            delete.setOnClickListener(this);
            displayed.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view.getId() == delete.getId()) {
                new AlertDialog.Builder(mActivity)
                        .setTitle(R.string.are_you_sure)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mPresenter.removeLocation(getAdapterPosition());
                                int adapterPosition = getAdapterPosition();
                                notifyItemRemoved(adapterPosition);
                                mSize--;
                                notifyItemRangeChanged(adapterPosition, mSize);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }else if(view.getId() == displayed.getId()){
                mPresenter.changedDisplayed(getAdapterPosition(),displayed.isChecked());
            }else{
                mContext.startActivity(mPresenter.getIntentForWeatherActivity(mContext,getAdapterPosition()));
            }

        }
    }

}

package com.example.android.weather;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherDisplayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private boolean mDaily = true;
    private WeatherContract.Presenter mPresenter;
    private int mSize = 0;

    public WeatherDisplayAdapter(Context context, WeatherContract.Presenter presenter, boolean daily,int size) {
        mContext = context;
        mPresenter = presenter;
        mDaily = daily;
        mSize = size;
    }

    public void setSize(int size){
        mSize = size;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weather_list_item_daily,parent,false);
        return new WeatherDisplayAdapter.ViewHolderWeatherDaily(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(mDaily){
            ViewHolderWeatherDaily holderDaily = (ViewHolderWeatherDaily) holder;
            holderDaily.icon.setImageResource(mPresenter.getIconDaily(mContext,position));
            holderDaily.date.setText(mPresenter.getDate(position));
            holderDaily.summary.setText(mPresenter.getSummaryDaily(position));
            holderDaily.highTemp.setText(mPresenter.getHighTemp(mContext,position));
            holderDaily.lowTemp.setText(mPresenter.getLowTemp(mContext,position));
            holderDaily.windSpeed.setText(mPresenter.getWindSpeedDaily(mContext,position));
            holderDaily.humidity.setText(mPresenter.getHumidityDaily(mContext,position));
            holderDaily.precip.setText(mPresenter.getPrecipDaily(mContext,position));

        }
    }

    @Override
    public int getItemCount() {
        return mSize;
    }

    class ViewHolderWeatherDaily extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.weather_icon) ImageView icon;
        @BindView(R.id.date) TextView date;
        @BindView(R.id.summary) TextView summary;
        @BindView(R.id.high_temp) TextView highTemp;
        @BindView(R.id.low_temp) TextView lowTemp;
        @BindView(R.id.wind_speed) TextView windSpeed;
        @BindView(R.id.humidity) TextView humidity;
        @BindView(R.id.precip) TextView precip;

        public ViewHolderWeatherDaily(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        @Override
        public void onClick(View v) {

        }
    }
}

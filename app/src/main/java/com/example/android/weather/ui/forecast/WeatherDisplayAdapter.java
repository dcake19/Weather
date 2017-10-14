package com.example.android.weather.ui.forecast;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.weather.R;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherDisplayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private boolean mDaily = true;
    private WeatherContract.Presenter mPresenter;
    private int mSize = 0;
    private TextToSpeech mTextToSpeech;

    public WeatherDisplayAdapter(Context context, WeatherContract.Presenter presenter, TextToSpeech textToSpeech,boolean daily,int size) {
        mContext = context;
        mPresenter = presenter;
        mDaily = daily;
        mSize = size;
        mTextToSpeech = textToSpeech;
    }

    public void setSize(int size){
        mSize = size;
        notifyDataSetChanged();
    }

    public void setSize(int size,boolean daily){
        mSize = size;
        mDaily = daily;
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
        ViewHolderWeatherDaily holderDaily = (ViewHolderWeatherDaily) holder;
        if(mDaily){
            holderDaily.icon.setImageResource(mPresenter.getIconDaily(mContext,position));
            holderDaily.date.setText(mPresenter.getDate(position));
            holderDaily.highTemp.setText(mPresenter.getHighTemp(mContext,position));
            holderDaily.lowTemp.setText(mPresenter.getLowTemp(mContext,position));
            holderDaily.windSpeed.setText(mPresenter.getWindSpeedDaily(mContext,position));
            holderDaily.precip.setText(mPresenter.getPrecipDaily(mContext,position));
        }else{
            holderDaily.icon.setImageResource(mPresenter.getIconHourly(mContext,position));
            holderDaily.date.setText(mPresenter.getTime(position));
            holderDaily.highTemp.setText(mPresenter.getTempHourly(position));
            holderDaily.lowTemp.setText("");
            holderDaily.windSpeed.setText(mPresenter.getWindSpeedHourly(mContext,position));
            holderDaily.precip.setText(mPresenter.getPrecipHourly(mContext,position));
        }

    }

    @Override
    public int getItemCount() {
        return mSize;
    }

    class ViewHolderWeatherDaily extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.layout_weather) LinearLayout mLayout;
        @BindView(R.id.weather_icon) ImageView icon;
        @BindView(R.id.date) TextView date;
        @BindView(R.id.high_temp) TextView highTemp;
        @BindView(R.id.low_temp) TextView lowTemp;
        @BindView(R.id.wind_speed) TextView windSpeed;
        @BindView(R.id.precip) TextView precip;

        public ViewHolderWeatherDaily(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            mLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mDaily)
                mTextToSpeech.speak(mPresenter.getWeatherSpeakDaily(getAdapterPosition()),TextToSpeech.QUEUE_FLUSH,null);
            else
                mTextToSpeech.speak(mPresenter.getWeatherSpeakHourly(getAdapterPosition()),TextToSpeech.QUEUE_FLUSH,null);
        }
    }
}

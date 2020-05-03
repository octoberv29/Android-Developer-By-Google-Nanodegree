package com.example.android.sunshine;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private String[] mWeatherData;

    private final ForecastAdapterOnClickHandler mClickHandler;

    // The interface that receives onClick messages.
    public interface ForecastAdapterOnClickHandler {
        void onClick(String weatherForDay);
    }

    // Constructor for ForecastAdapter. It accepts a specification for the ForecastAdapterOnClickHandler.
    public ForecastAdapter(ForecastAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    // onCreateViewHolder() gets called when each new ViewHolder is created. This happens when the RecyclerView
    // is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Usually involves inflating a layout from XML and returning the holder.
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.forecast_list_item, viewGroup, false);
        return new ForecastAdapterViewHolder(view);
    }

    // OnBindViewHolder() is called by the RecyclerView to display the data at the specified position.
    // In this method, we update the contents of the ViewHolder to display the weather details for this particular position.
    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {
        String weatherForThisDay = mWeatherData[position];
        forecastAdapterViewHolder.mWeatherTextView.setText(weatherForThisDay);
    }

    // getItemCount() returns the number of items to display.
    // It is used behind the scenes to help layout our Views and for animations.
    @Override
    public int getItemCount() {
        if (null == mWeatherData) return 0;
        return mWeatherData.length;
    }

    // This method is used to set the weather forecast on a ForecastAdapter if we've already created one.
    // This is handy when we get new data from the web but don't want to create a new ForecastAdapter to display it.
    public void setWeatherData(String[] weatherData) {
        mWeatherData = weatherData;
        notifyDataSetChanged();
    }

    // Provide a direct reference to each of the views within a data item
    // Cache children views of a forecast list item.
    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mWeatherTextView;

        public ForecastAdapterViewHolder(View view) {
            super(view);
            mWeatherTextView = (TextView) view.findViewById(R.id.tv_weather_data);
            // Call setOnClickListener on the view passed into the constructor (use 'this' as the OnClickListener)
            view.setOnClickListener(this);
        }

        // Override onClick, passing the clicked day's data to mClickHandler via its onClick method
        // This gets called by the child views during a click.
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String weatherForDay = mWeatherData[adapterPosition];
            mClickHandler.onClick(weatherForDay);
        }
    }
}

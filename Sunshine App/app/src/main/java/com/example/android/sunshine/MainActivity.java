/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.data.WeatherContract.WeatherEntry;
import com.example.android.sunshine.utilities.FakeDataUtils;


public class MainActivity extends AppCompatActivity implements
        ForecastAdapter.ForecastAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();

    // The columns of data that we are interested in displaying within our MainActivity's list of weather data.
    public static final String[] MAIN_FORECAST_PROJECTION = {
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_WEATHER_ID,
    };
    // The indices of the values in the array of Strings above.
    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_CONDITION_ID = 3;

    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;
    private ProgressBar mLoadingIndicator;

    private int mPosition = RecyclerView.NO_POSITION;

    private static final int ID_FORECAST_LOADER = 44;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        getSupportActionBar().setElevation(0f);

        FakeDataUtils.insertFakeData(this);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true); // use this setting to improve performance if you know that changes in content do not change the child layout size in the RecyclerView

        mForecastAdapter = new ForecastAdapter(this, this);
        mRecyclerView.setAdapter(mForecastAdapter);

        showLoading();

        getSupportLoaderManager().initLoader(ID_FORECAST_LOADER, null, MainActivity.this);
    }

    // Handle RecyclerView item clicks
    @Override
    public void onClick(long date) {
        Intent weatherDetailIntent = new Intent(MainActivity.this, DetailActivity.class);
        Uri uriForDateClicked = WeatherEntry.buildWeatherUriWithDate(date);
        weatherDetailIntent.setData(uriForDateClicked);
        startActivity(weatherDetailIntent);
    }

    // Hide the loading indicator and show the data
    private void showWeatherDataView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE); // hide the loading indicator data
        mRecyclerView.setVisibility(View.VISIBLE); // make sure the weather data is visible
    }

    // Hide the data and show the loading indicator
    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    /* --- Cursor Loader Callbacks --- */

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, final Bundle loaderArgs) {

//        return new AsyncTaskLoader<String[]>(this) {
//
//            // This String array will hold and help cache our weather data
//            String[] mWeatherData = null;
//
//            @Override
//            protected void onStartLoading() {
//                if (mWeatherData != null) {
//                    deliverResult(mWeatherData);
//                } else {
//                    mLoadingIndicator.setVisibility(View.VISIBLE);
//                    forceLoad();
//                }
//            }
//
//            @Override
//            public String[] loadInBackground() {
//
//                String locationQuery = SunshinePreferences
//                        .getPreferredWeatherLocation(MainActivity.this);
//
//                URL weatherRequestUrl = NetworkUtils.buildUrl(locationQuery);
//
//                try {
//                    String jsonWeatherResponse = NetworkUtils
//                            .getResponseFromHttpUrl(weatherRequestUrl);
//
//                    String[] simpleJsonWeatherData = OpenWeatherJsonUtils
//                            .getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);
//
//                    return simpleJsonWeatherData;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return null;
//                }
//            }
//
//            // Sends the result of the load to the registered listener.
//            public void deliverResult(String[] data) {
//                mWeatherData = data;
//                super.deliverResult(data);
//            }
//        };

        switch (loaderId) {

            case ID_FORECAST_LOADER:
                return new CursorLoader(this,
                        WeatherEntry.CONTENT_URI,  /* URI for all rows of weather data in our weather table */
                        MAIN_FORECAST_PROJECTION,
                        WeatherEntry.getSqlSelectForTodayOnwards(), /* Selection: all weather data from today onwards */
                        null,
                        WeatherEntry.COLUMN_DATE + " ASC"
                );

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mForecastAdapter.swapCursor(cursor);
        if (mPosition == RecyclerView.NO_POSITION) {
            mPosition = 0;
        }
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (cursor.getCount() != 0) {
            showWeatherDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Since this Loader's data is now invalid, we need to clear the Adapter that is displaying the data.
        mForecastAdapter.swapCursor(null);
    }

    /* --- Action Bar methods --- */

    private void openPreferredLocationInMap() {
        double[] coordinates = SunshinePreferences.getLocationCoordinates(this);
        String posLat = Double.toString(coordinates[0]);
        String posLong = Double.toString(coordinates[1]);
        Uri geoLocation = Uri.parse("geo:" + posLat + "," + posLong);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_map) {
            openPreferredLocationInMap();
            return true;
        }

        if (id == R.id.action_settings) {
            Intent intentToOpenSettings = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intentToOpenSettings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
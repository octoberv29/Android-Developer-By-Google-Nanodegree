package com.example.android.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.sunshine.data.WeatherContract.WeatherEntry;
import com.example.android.sunshine.databinding.ActivityDetailBinding;
import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    // The columns of data that we are interested in displaying within our DetailActivity's weather display.
    public static final String[] WEATHER_DETAIL_PROJECTION = {
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_DEGREES,
            WeatherEntry.COLUMN_WEATHER_ID
    };
    // The indices of the values in the array of Strings above
    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_HUMIDITY = 3;
    public static final int INDEX_WEATHER_PRESSURE = 4;
    public static final int INDEX_WEATHER_WIND_SPEED = 5;
    public static final int INDEX_WEATHER_DEGREES = 6;
    public static final int INDEX_WEATHER_CONDITION_ID = 7;

    private static final int ID_DETAIL_LOADER = 353;

    private String mForecastSummary;

    // The URI that is used to access the chosen day's weather details */
    private Uri mUri;

    private ActivityDetailBinding mDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        mUri = getIntent().getData();
        if (mUri == null) throw new NullPointerException("URI for DetailActivity cannot be null");

        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mForecastSummary + FORECAST_SHARE_HASHTAG)
                .getIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return shareIntent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            Intent shareIntent = createShareForecastIntent();
            startActivity(shareIntent);
            return true;
        }

        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle bundle) {
        switch (loaderId) {

            case ID_DETAIL_LOADER:

                return new CursorLoader(
                        this,
                        mUri,
                        WEATHER_DETAIL_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        boolean cursorHasValidData = false;
        if (cursor != null && cursor.moveToFirst()) {
            cursorHasValidData = true;
        }
        if (!cursorHasValidData) return;

        // icon
        int weatherId = cursor.getInt(INDEX_WEATHER_CONDITION_ID);
        int weatherImageId = SunshineWeatherUtils.getLargeArtResourceIdForWeatherCondition(weatherId);
        mDetailBinding.primaryInfo.weatherIcon.setImageResource(weatherImageId);

        // date
        long localDateMidnightGmt = cursor.getLong(INDEX_WEATHER_DATE);
        String dateText = SunshineDateUtils.getFriendlyDateString(this, localDateMidnightGmt, true);
        mDetailBinding.primaryInfo.date.setText(dateText);

        // description
        String description = SunshineWeatherUtils.getStringForWeatherCondition(this, weatherId);
        String descriptionA11y = getString(R.string.a11y_forecast, description);
        mDetailBinding.primaryInfo.weatherDescription.setText(description);
        mDetailBinding.primaryInfo.weatherDescription.setContentDescription(descriptionA11y);

        // high temperature
        double highInCelsius = cursor.getDouble(INDEX_WEATHER_MAX_TEMP);
        String highString = SunshineWeatherUtils.formatTemperature(this, highInCelsius);
        String highA11y = getString(R.string.a11y_high_temp, highString);
        mDetailBinding.primaryInfo.highTemperature.setText(highString);
        mDetailBinding.primaryInfo.highTemperature.setContentDescription(highA11y);;

        // low temperature
        double lowInCelsius = cursor.getDouble(INDEX_WEATHER_MIN_TEMP);
        String lowString = SunshineWeatherUtils.formatTemperature(this, lowInCelsius);
        String lowA11y = getString(R.string.a11y_low_temp, lowString);
        mDetailBinding.primaryInfo.lowTemperature.setText(lowString);
        mDetailBinding.primaryInfo.lowTemperature.setContentDescription(lowA11y);

        // humidity
        float humidity = cursor.getFloat(INDEX_WEATHER_HUMIDITY);
        String humidityString = getString(R.string.format_humidity, humidity);
        String humidityA11y = getString(R.string.a11y_humidity, humidityString);
        mDetailBinding.extraDetails.humidity.setText(humidityString);
        mDetailBinding.extraDetails.humidity.setContentDescription(humidityA11y);

        // wind speed and direction
        float windSpeed = cursor.getFloat(INDEX_WEATHER_WIND_SPEED);
        float windDirection = cursor.getFloat(INDEX_WEATHER_DEGREES);
        String windString = SunshineWeatherUtils.getFormattedWind(this, windSpeed, windDirection);
        String windA11y = getString(R.string.a11y_wind, windString);
        mDetailBinding.extraDetails.windMeasurement.setText(windString);
        mDetailBinding.extraDetails.windMeasurement.setContentDescription(windA11y);
        mDetailBinding.extraDetails.windLabel.setContentDescription(windA11y);

        // pressure
        float pressure = cursor.getFloat(INDEX_WEATHER_PRESSURE);
        String pressureString = getString(R.string.format_pressure, pressure);
        String pressureA11y = getString(R.string.a11y_pressure, pressureString);
        mDetailBinding.extraDetails.pressure.setText(pressureString);
        mDetailBinding.extraDetails.pressure.setContentDescription(pressureA11y);
        mDetailBinding.extraDetails.pressureLabel.setContentDescription(pressureA11y);

        // store the forecast summary String in the forecast summary field to share later
        mForecastSummary = String.format("%s - %s - %s/%s", dateText, description, highString, lowString);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) { }
}

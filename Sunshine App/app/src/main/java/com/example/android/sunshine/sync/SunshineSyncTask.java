package com.example.android.sunshine.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.text.format.DateUtils;

import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.NotificationUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;


public class SunshineSyncTask {


    synchronized public static void syncWeather(Context context) {

        try {
            // get URL, then retrieve the JSON, then parse this JSON into a list of weather values
            URL weatherRequestUrl = NetworkUtils.getUrl(context);
            String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);
            ContentValues[] weatherValues = OpenWeatherJsonUtils
                    .getWeatherContentValuesFromJson(context, jsonWeatherResponse);

            if (weatherValues != null && weatherValues.length != 0) {

                ContentResolver sunshineContentResolver = context.getContentResolver();

                // delete old weather data because we don't need to keep multiple days' data
                sunshineContentResolver.delete(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        null,
                        null
                );

                // insert our new weather data into Sunshine's ContentProvider
                sunshineContentResolver.bulkInsert(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        weatherValues
                );


                // check if notifications are enabled and when was the last one
                boolean notificationsEnabled = SunshinePreferences.areNotificationsEnabled(context);
                long timeSinceLastNotification = SunshinePreferences.getEllapsedTimeSinceLastNotification(context);

                // if more than a day have passed and notifications are enabled, notify the user
                if (notificationsEnabled && timeSinceLastNotification >= DateUtils.DAY_IN_MILLIS) {
                    NotificationUtils.notifyUserOfNewWeather(context);
                }
            }

        } catch (Exception e) {
            /* Server probably invalid */
            e.printStackTrace();
        }
    }

}

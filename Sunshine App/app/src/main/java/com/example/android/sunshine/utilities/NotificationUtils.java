package com.example.android.sunshine.utilities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import com.example.android.sunshine.DetailActivity;
import com.example.android.sunshine.R;
import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.data.WeatherContract;

public class NotificationUtils {

    //  The columns of data that we are interested in displaying within our notification to let
    //  the user know there is new weather data available.
    public static final String[] WEATHER_NOTIFICATION_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
    };

    // the indices of the values in the array of Strings above
    public static final int INDEX_WEATHER_ID = 0;
    public static final int INDEX_MAX_TEMP = 1;
    public static final int INDEX_MIN_TEMP = 2;


    private static final int WEATHER_NOTIFICATION_ID = 3004;

    // Constructs and displays a notification for the newly updated weather for today.
    public static void notifyUserOfNewWeather(Context context) {

        /* Build the URI for today's weather in order to show up to date data in notification */
        Uri todaysWeatherUri = WeatherContract.WeatherEntry
                .buildWeatherUriWithDate(SunshineDateUtils.normalizeDate(System.currentTimeMillis()));

        /*
         * The MAIN_FORECAST_PROJECTION array passed in as the second parameter is defined in our WeatherContract
         * class and is used to limit the columns returned in our cursor.
         */
        Cursor todayWeatherCursor = context.getContentResolver().query(
                todaysWeatherUri,
                WEATHER_NOTIFICATION_PROJECTION,
                null,
                null,
                null
        );

        /*
         * If todayWeatherCursor is empty, moveToFirst will return false. If our cursor is not
         * empty, we want to show the notification.
         */
        if (todayWeatherCursor.moveToFirst()) {

            /* Weather ID as returned by API, used to identify the icon to be used */
            int weatherId = todayWeatherCursor.getInt(INDEX_WEATHER_ID);
            double high = todayWeatherCursor.getDouble(INDEX_MAX_TEMP);
            double low = todayWeatherCursor.getDouble(INDEX_MIN_TEMP);

            Resources resources = context.getResources();
            int largeArtResourceId = SunshineWeatherUtils
                    .getLargeArtResourceIdForWeatherCondition(weatherId);

            Bitmap largeIcon = BitmapFactory.decodeResource(
                    resources,
                    largeArtResourceId);

            String notificationTitle = context.getString(R.string.app_name);

            String notificationText = getNotificationText(context, weatherId, high, low);

            /* getSmallArtResourceIdForWeatherCondition returns the proper art to show given an ID */
            int smallArtResourceId = SunshineWeatherUtils
                    .getSmallArtResourceIdForWeatherCondition(weatherId);


            // use NotificationCompat.Builder to begin building the notification
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setColor(ContextCompat.getColor(context,R.color.colorPrimary))
                    .setSmallIcon(smallArtResourceId)
                    .setLargeIcon(largeIcon)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationText)
                    .setAutoCancel(true);

            // create an Intent with the proper URI to start the DetailActivity
            /*
             * This Intent will be triggered when the user clicks the notification. In our case,
             * we want to open Sunshine to the DetailActivity to display the newly updated weather.
             */
            Intent detailIntentForToday = new Intent(context, DetailActivity.class);
            detailIntentForToday.setData(todaysWeatherUri);

            // use TaskStackBuilder to create the proper PendingIntent
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
            taskStackBuilder.addNextIntentWithParentStack(detailIntentForToday);
            PendingIntent resultPendingIntent = taskStackBuilder
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            // set the content Intent of the NotificationBuilder
            notificationBuilder.setContentIntent(resultPendingIntent);

            // get a reference to the NotificationManager
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            // notify the user with the ID WEATHER_NOTIFICATION_ID
            /* WEATHER_NOTIFICATION_ID allows you to update or cancel the notification later on */
            notificationManager.notify(WEATHER_NOTIFICATION_ID, notificationBuilder.build());

            // save the time at which the notification occurred using SunshinePreferences
            /*
             * Since we just showed a notification, save the current time. That way, we can check
             * next time the weather is refreshed if we should show another notification.
             */
            SunshinePreferences.saveLastNotificationTime(context, System.currentTimeMillis());
        }

        /* Always close your cursor when you're done with it to avoid wasting resources. */
        todayWeatherCursor.close();
    }


    private static String getNotificationText(Context context, int weatherId, double high, double low) {

        String shortDescription = SunshineWeatherUtils.getStringForWeatherCondition(context, weatherId);

        String notificationFormat = context.getString(R.string.format_notification);

        // using String's format method, we create the forecast summary
        String notificationText = String.format(
                notificationFormat,
                shortDescription,
                SunshineWeatherUtils.formatTemperature(context, high),
                SunshineWeatherUtils.formatTemperature(context, low)
        );

        return notificationText;
    }
}

# Sunshine App

This is an application I developed while doing Developing Android Apps course
as a part of Android Developer Nanodegree by Google.

1. Create Project Sunshine:
    * Application shows some dummy data on the screen.

2. Connect to the Internet:
    * Construct a weather URL and fetch the data from the web using HttpsURLConnection.  
        The result of HTTPS request will be a JSON; therefore, parse JSON.  
        To prevent waiting for this long-running task (fetching data) on main thread  
        use AsyncTask, which will run this task on a background thread.  
        Execute AsyncTask inside onCreate() to fetch the data when the app is launched.  
        Because AsyncTask does not load up data automatically when it changes,  
        add a way to update the result on the screen.  
    &nbsp;
    
    1. Added Internet Permission to communicate with the server.
    2. Created NetworkUtils class, where:
        1. Implemented buildUrl() to build the URL from the StringURL and additional query params.
        2. Implemented getResponseFromHttpUrl() that takes URL and use it to fetch the data.
        3. No need to do the parsing, because it was already implemented inside OpenWeatherJsonUtils class.
    3. Created AsyncTask to run fetching data task on the background thread:
        1. Implemented doInBackground(). It creates a valid URL using buildUrl() and passed params,
            then, passes it to getResponseFromHttpUrl() to fetch the data. It returns JSON String, which
            then it passes to getSimpleWeatherStringsFromJson() and obtains String[] of weather data.
        2. Implemented onPostExecute() to show the result of doInBackground() on a screen.
        3. Executed AsyncTask inside onCreate() of the MainActivity to show the result of a query
            after launching the application.
        4. Added a menu item to execute AsyncTask every time user wants to refresh the weather data.
    4. Added visual polish:
        1. Added error message and loading indicator to the layout.
        2. Override onPreExecute() inside AsyncTask.
        
3. RecyclerView
    * Replace a TextView with weather data by a RecyclerView.  
        Populate this RecyclerView using an Adapter. It creates views for items, and replaces  
        the content of some of the views with new data items when the original item is no longer visible.  
        Use ViewHolder to cache the items. Then, handle item click events.  
    &nbsp;
    
    1. Added a RecyclerView dependency to the app gradle.
    2. Replaced a TextView by a RecyclerView in activity layout and created a separate list item layout.
    3. Implemented a RecyclerView.Adapter
        1. Created an inner class that extends from RecyclerView.ViewHolder,  
            where I provided a direct reference to each of the views of a single item in a list.
        2. Created a constructor for a RecyclerView.Adapter.
        3. Overrode onCreateViewHolder() that creates new views.
        4. Overrode onBindViewHolder() that replaces the content of a specific view.
        5. Overrode getItemCount() that returns the size of the dataset. 
    4. Bound the adapter to the data source to populate the RecyclerView using LayoutManager in the activity.
        1. Obtained a handle to the RecyclerView object.
        2. Connected it to a layout manager.
        3. Attached an adapter for the data to be displayed.
    5. Handled item clicks using a listener interface inside RecyclerView.Adapter
        1. Created an interface with onClick() method.
        2. Implemented this interface inside activity.
        3. Changed adapter's constructor to accept an object of this interface as a parameter.
        4. ViewHolder now implements View.OnClickListener interface to pass the information from an  
            item with specific position back to activity.
            
4. Intens
    * Create a new DetailActivity and declare it as a child of a MainActivity.  
        Use explicit intent to redirect the user to a DetailActivity.  
        Use implicit intent to open a map.  
        Use ShareCompat Intent builder to create a Forecast intent for sharing with other apps.  
    &nbsp;
        
    1. Created a new DetailActivity and declared it in the manifest as a child of the MainActivity.
    2. Redirected the user to a newly created activity using intent when the user clicks on an item in the list.
    3. Changed the menu inside the MainActivity to show the map location. Created an intent to do that.
    4. Create a menu for detail activity with a share button and used ShareCompat Intent builder to create  
        a Forecast intent for sharing with other apps.
        
5. Lifecycle
    * Replace AsyncTask by AsyncTaskLoader.  
        AsyncTaskLoader prevents duplication of background threads and eliminates duplication of zombie activities  
        during screen rotation and similar Activity configuration changes, because it caches data and  
        behaves within the lifecycles of Fragments and Activities.  
    &nbsp;
           
    1. Implemented LoaderManager.LoaderCallbacks inside the MainActivity.    
    2. Created a unique Loader ID and filled-in loader callbacks:
        1. Within onCreateLoader, returned a new AsyncTaskLoader that looks a lot like the existing FetchWeatherTask.  
            Inside AsyncTask:
            1. Cached the weather data in a member variable and delivered it in onStartLoading().
            2. LoadInBackground() - the same as doInBackground() method of AsyncTask, i.e. do the background task (fetch the data from the server).
        2. Updated the UI inside onLoadFinished() - the same as onPostExecute() of FetchWeatherTask.
        3. Implemented onLoaderReset(), because it's required, but left it blank.
    3. Refactored the code:
        1. Refactored refresh functionality to work with AsyncTaskLoader.
        2. Replaced calls to an AsyncTask by initialising Loader on Loader Manager.
        3. Deleted previous AsyncTask (FetchWeatherTask).
        
6. Preferences
    * Add SharedPreference for units (metric, imperial) and location so that the user can change them.
    &nbsp;
    
    1. Created SettingsActivity:
        1. Created SettingsActivity (+xml), enabled back arrow functionality.
        2. Added a settings option to the main and detail menus, 
        3. Enabled redirecting from settings action to a Settings Activity.
    2. Created SettingsFragment:
        1. Added a library dependency.
        2. Created an xml resource directory with an xml file PreferenceScreen that has  
            an EditTextPreference and ListPreference.
        3. Created SettingsFragment that extends PreferenceFragmentCompat, and  
            inside onCreatePreferences() use the addPreferencesFromResource() method to inflate that xml file.
        4. Added a theme for the preferences.    
        5. Set the root layout of activity_settings to the newly created SettingsFragment.
        6. Set the preference summary on each preference that isn't a CheckBoxPreference  
            to show the current state of each preference (inside the SettingsFragment).
        7. Registered and unregistered the OnSharedPreferenceChangeListener.
    3. Updated the UI according to preference file (inside the MainActivity):
        1. Implemented OnSharedPreferenceChangeListener to triger and apply all changes happened to the preference file.      
        2. Added all of the required changes to the SunshinePreferences class to get the location and units.
        
7. Database Creation
    * Save data using SQLite database to cache weather forecast on a phone. 
        For that, define a schema and contract. Also, create a database using an SQLiteOpenHelper.
    &nbsp;

    1. Defined a schema and contract:
        1. Defined WeatherContract class.
        2. Created an inner class WeatherEntry that implements the BaseColumns interface (one class per one table).
        3. Inside created static final members for the table name and each of the db columns.
    2. Created a database using an SQLiteOpenHelper:
        1. Created a subclass WeatherDbHelper that extends from SQLiteOpenHelper. 
        2. Defined constants for database name and version.
        3. Created a constructor that takes a context and calls the parent constructor.
        4. Overrode onCreate() and onUpgrade() methods to create/update db.
        
8. Content Providers
    * Add Content Provider to access the cached data stored in a local database.  
        Replace AsyncTaskLoader by Cursor Loader inside MainActivity to take the data from  
        a local database through Content Provider's methods using ContentResolver.  
        For now, use fake data instead of downloading it from the web.  
        Update MainActivity and ForecastAdapter to use cursor and delete OnSharedPreferenceChangeListener.
        Update DetailActivity to show details in separate TextViews instead of one summary.
    &nbsp;
    
    1. Content Provider and Cursor Loader:    
        1. Created a WeatherProvider class by subclassing a ContentProvider and added it to the Manifest file.
        2. Added URIs to the Contract class, which apps/components will use to contact the content provider.
        3. Added UriMatcher to the ContentProvider to match given URIs to integer codes while doing operations on db.
        4. Overrode ContentProvider methods: bulkInsert(), insert(), query(), update(), delete().
        5. Added ContentResolver to interact with the ContentProvider instead of directly interacting with a db.
    2. MainActivity:    
        1. Updated ForecastAdapter to use cursor instead of String data.
        2. Replaced AsyncTaskLoader by CursorLoader in MainActivity.
        3. Eliminated all of the code of OnSharedPreferenceChangeListener in MainActivity.
    3. DetailActivity:
        1. Updated activity_detail.xml to display all of the data in a separate TextViews.
        2. Modified DetailActivity to show all of the data and added CursorLoader to show this data.
        3. Changed Item Click Listener Interface inside ForecastAdapter to accept long date.
        4. Changed implementation of this interface inside MainActivity so that now it sends Uri of  
            a specific weather to a DetailActivity when click event occurs.
            
9. Background Tasks
    * The goal is to always get fresh data and display a notification everyday when the weather updates.  
        To do this, start by setting Sunshine app with a background synchronization IntentService. It will  
        update database in the background. Then, optimize synchronization process (synchronize only if the  
        database was never synchronized before). Then, use FirebaseJobDispatcher to run synchronization every 3-4 hours.  
        And display notification with the updated weather status every 24 hours.  
    &nbsp;
            
    1. Created SunshineSyncTask with just one method syncWeather():
        1. Put the previous loading weather data logic from AsyncTaskLoader in this method.  
            (It performs the network request for updated weather, parses the JSON from that request,  
              and inserts the new weather information into our ContentProvider)
        2. If the results are valid, it deletes the old weather data and inserts the new data.
    2. Create and Register SunshineSyncIntentService. 
        It will handle backgrounding our syncTask. IntentServices are perfect for  
        one off tasks that need to be handled in the background, so we’ll create one here.  
        1. Created SunshineSyncIntentService class and set it to extend IntentService.
        2. Declared this service in Manifest.
        3. Created a constructor that calls super and passes the name of this class as a string.
        4. Overrode onHandleIntent to call SunshineSyncTask.syncWeather() to perform the background task.
    3. Create SunshineSyncUtils. Inside created a startImmediateSync() that will start the IntentService   
         and force an immediate synchronization when called. It basically get everything wired up.
    4. Optimizing the synchronization process. It’s best practice to not initialize things more than once,   
        so for that, we will make sure that startImmediateSync will only get called once when the app starts   
        and only if the database was empty. inside SunshineSyncUtils class:
        1. Created a boolean flag called sInitialized. This will be mainly used as a safeguard   
            to prevent calling the synchronize method more than once.
        2. Created initialize() that will use that boolean to guarantee that startImmediateSync()  
            is called only when necessary.
        3. If flag isn’t set to true, we want to check to see if our ContentProvider is empty,  
            in case for example the app was just freshly installed and had no data stored yet.  
            To do this, run a query and get the result count, but do so on a background thread  
            using an AsyncTask.
        4.  If the ContentProvider is in fact empty, go ahead and call startImmediateSync.
    5. Syncing on demand is great, but don’t we want to continuously update the data for our users,   
        even when the app isn’t in the foreground? Create FirebaseJobDispatcher:
        1. Added the FirebaseJobDispatcher dependency.
        2. Created SunshineFirebaseJobService that extends jobdispatcher.JobService.
        3. Within the Service, overrode onStartJob() and call to our SunshineSyncTask.syncWeather() in the background.
        4. Once the syncWeather method finishes, called jobFinished, passing the JobParameters argument  
            from onStartJob as well as a false value to signify that we don’t have any more work to do.
        5. To clean up any mess that may be caused by the framework cancelling our jobs,  
            overrode onStopJob, and stop our background thread that was started in onStartJob. 
        6. Returned true to tell the system, “Yes please, we’d like to be rescheduled to finish that  
            work that we were doing when you so rudely interrupted us.”
        7. Declared newly created Service in the Manifest.
        8. Modified SunshineSyncUtils:
            1. Added constant values to represent how frequently, and with what timeframe,   
                we will perform our weather synchronization.
            2. Created scheduleFirebaseJobDispatcherSync() that builds and dispatchers our Job,  
                and then call that method from the initialize method (only if the method hasn’t  
                been previously initialized).
    6. Notifications:
        1. Created a constant int identifier for our notification.  
            This can be used later to access the notification.
        2. Created an Intent with the proper Uri to start the DetailActivity.
        3. We want to navigate back to the MainActivity from the DetailActivity if the user   
            clicks the Notification and then clicks back, so use TaskStackBuilder for that.
        4. Assigned that intent to the NotificationBuilder object so that when the user clicks   
            the notification, it is fired off.
        5. In order to notify the user, we need a reference to the NotificationManager,  
            so use getSystemService to do so.
        6. Now that everything is ready, notify the user and also save the time at which we showed   
            this notification. Notifications are totally super awesome, but we don’t want to annoy  
            our users with too many of them.
        7. Created bools.xml under res/values and within it, create a boolean value set to true. 
        8. Should we notify the user when we sync the data? Within SunshineSyncTask, first check to   
            see if notifications are enabled at all. If they are, we’ll also need to check to see when   
            the last time we notified the user was. If it was less than a day ago, it’s better that  
            we hold off, and just keep our user happy that her weather data is up to date and ready  
            to be displayed as soon as she wants it!
            
10. Completing the UI
    
    1. Updated the forecast_list_item layout and the ForecastAdapterViewHolder that populates these item views.
    2. Added a new layout for Today item in the list and updated the ForecastAdapter to switch between old and new.
    3. Enabled and added Data Binding to DetailActivity, added accessibility elements, such as setContentDescription.
    
11. Polishing the UI

    1. Added new Colors, Fonts, and Dimensions.
    2. Created and applied Styles.
    3. Added Touch Selectors.
    4. Added Resource Qualifiers.
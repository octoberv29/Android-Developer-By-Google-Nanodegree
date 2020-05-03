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
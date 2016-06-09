package com.tezewike.er.movie.data;


import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.tezewike.er.BuildConfig;
import com.tezewike.er.utils.AppNetwork;
import com.tezewike.er.utils.CustomJsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Tobe on 6/8/2016.
 */
public class MovieLoader extends AsyncTaskLoader<Cursor> {
    String LOG_TAG = MovieLoader.class.getSimpleName();
    String TAB_TAG;

    Context context;
    Cursor mCursor;

    String param;
    String RECENT_PARAM = "recent";
    String POPULAR_PARAM = "popular";

    MovieDbHelper movieSQLiteDb;
    AppNetwork appNetwork;
    CustomJsonUtils customJsonUtils;

    ProgressDialog progressDialog;
    Snackbar snackbar;

    public MovieLoader(Context context, String parameter) {
        super(context);
        this.context = context;
        this.param = parameter;

        this.TAB_TAG = param + " tab: ";
        this.movieSQLiteDb = new MovieDbHelper(context);
        this.appNetwork = new AppNetwork(context);
        this.customJsonUtils = new CustomJsonUtils(context);
//        this.progressDialog = new ProgressDialog(context);
//        this.snackbar = Snackbar.make(context.getWindow().getDecorView().getRootView(),
//                "Connection not active...", Snackbar.LENGTH_LONG);
    }

    @Override
    public Cursor loadInBackground() {
        if (getData()) {
            mCursor = movieSQLiteDb.getInformation(param, null);
            return mCursor;
        } else {
            return null;
        }
    }

/*    @Override
    protected void onPreExecute() {
        if (!isAppConnected()) {
            snackbar.show();
            cancel(true);
        } else {
            progressDialog.setMessage("Loading Movie List");
            progressDialog.setCancelable(false);
            progressDialog.setInverseBackgroundForced(false);
            progressDialog.show();
        }
    }

    @Override
    protected Boolean doInBackground(String... params) {
        Log.v(LOG_TAG, TAB_TAG + "doInBackground: Running...");

        if (isOldInformation()) {
            // If the information isn't new, end the process
            Log.v(LOG_TAG, TAB_TAG + "Previous data found.");
            return true;
        } else {
            Log.v(LOG_TAG, TAB_TAG + "Either old, or no, previous data found.");
        }

        // If the above failed, try to obtain data from the web
        URL jsonURL;
        if (params[0].equals(RECENT_PARAM)) {
            jsonURL = getRecentMoviesURL();
        } else if (params[0].equals(POPULAR_PARAM)) {
            jsonURL = getPopularMoviesURL();
        } else {
            return false;
        }

        String data = customJsonUtils.getJsonString(jsonURL);
        try {
            return parseMovieJson(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // If all the above methods failed, return false
        return false;
    }

    @Override
    protected void onPostExecute(Boolean results) {
        Log.v(LOG_TAG, TAB_TAG + "onPostExecute: Finishing tasks...");

        if (results != null) {
            mCursor = movieSQLiteDb.getInformation(param, null);
            movieSQLiteDb.close();
        }

        // End the Loading dialogLoad box
        progressDialog.dismiss();

    }
*/
    private String[] getTimes(int wks) {

        Calendar time = GregorianCalendar.getInstance();
        Calendar past = GregorianCalendar.getInstance();
        past.add(Calendar.DAY_OF_YEAR, -7 * wks);   // Set to (int wks) week(s) before

        int month = time.get(Calendar.MONTH) + 1;
        int date = time.get(Calendar.DATE);
        int year = time.get(Calendar.YEAR);

        int p_month = past.get(Calendar.MONTH) + 1;
        int p_date = past.get(Calendar.DATE);
        int p_year = past.get(Calendar.YEAR);

        String timeNow = year + "-" + month + "-" + date;
        String timePast = p_year + "-" + p_month + "-" + p_date;

        return new String[] {timePast, timeNow} ;
    }

    private URL getRecentMoviesURL() {

        // Set queries
        final String BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
        final String FROM_PARAM = "primary_release_date.gte";
        final String TO_PARAM = "primary_release_date.lte";
        final String SORT_PARAM = "sort_by";
        final String API_PARAM = "api_key";


        int weeks = 2;
        // Get Date parameters
        String[] dates = getTimes(weeks);
        // Get apiK
        String apiKey = BuildConfig.TMDB_API_KEY;
        String popularity = "vote_average";

        // Build the url
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(FROM_PARAM, dates[0])
                .appendQueryParameter(TO_PARAM, dates[1])
                .appendQueryParameter(SORT_PARAM, popularity)
                .appendQueryParameter(API_PARAM, apiKey)
                .build();

        // Attempt to create url
        URL url;
        try {
            url = new URL(uri.buildUpon().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        return url;
    }

    private URL getPopularMoviesURL() {

        // Set queries
        final String BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
        final String FROM_PARAM = "primary_release_date.gte";
        final String TO_PARAM = "primary_release_date.lte";
        final String SORT_PARAM = "sort_by";
        final String API_PARAM = "api_key";


        int weeks = 10;
        // Get Date parameters
        String[] dates = getTimes(weeks);
        // Get apiK
        String apiKey = BuildConfig.TMDB_API_KEY;
        String popularity = "popularity.desc";

        // Build the url
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(FROM_PARAM, dates[0])
                .appendQueryParameter(TO_PARAM, dates[1])
                .appendQueryParameter(SORT_PARAM, popularity)
                .appendQueryParameter(API_PARAM, apiKey)
                .build();

        // Attempt to create url
        URL url;
        try {
            url = new URL(uri.buildUpon().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        return url;
    }

    private String getImageURL(String urlPath, boolean poster) {
        String base = "http://image.tmdb.org/t/p/w";
        String width;

        if (urlPath.equals("null")) {
            return urlPath;
        }

        if (poster) {
            width = "185";
        } else {
            // Used for the backdrop
            width = "500";
        }

        return base + width + urlPath;
    }

    private boolean parseMovieJson(String jsonData) throws JSONException {

        if (jsonData == null) {
            return false;
        }

        JSONObject data = new JSONObject(jsonData);

        // Names of key Json elements that can be extracted
        final String LIST = "results";
        final String TITLE = "original_title";
        final String IMAGE = "poster_path";
        final String BACKDROP = "backdrop_path";
        final String RELEASE = "release_date";
        final String DESCRIPTION = "overview";
        final String VOTE = "vote_average";

        // Each value in movies[i] corresponds to one movie
        JSONArray moviesJSON = data.getJSONArray(LIST);

        for (int i = 0; i < moviesJSON.length(); i++) {
            JSONObject movie = moviesJSON.getJSONObject(i);

            movieSQLiteDb.putInformation(param, i,
                    movie.getString(TITLE),
                    movie.getString(VOTE),
                    movie.getString(RELEASE),
                    getImageURL(movie.getString(IMAGE), true),
                    getImageURL(movie.getString(BACKDROP), false),
                    movie.getString(DESCRIPTION)
            );

        }

        Log.e(LOG_TAG, TAB_TAG + "Data inserted into table.");

        return true;
    }

    private boolean isOldInformation() {
        try {
            return movieSQLiteDb.compareDateInformation(param);
        } catch (SQLiteException e) {
            Log.v(LOG_TAG, "No table(s) currently exist.");
            return false;
        }
    }

    private boolean isAppConnected() {
        return appNetwork.isNetworkAvailable();
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public boolean getData() {
        Log.v(LOG_TAG, TAB_TAG + "doInBackground: Running...");

        if (isOldInformation()) {
            // If the information isn't new, end the process
            Log.v(LOG_TAG, TAB_TAG + "Previous data found.");
            return true;
        } else {
            Log.v(LOG_TAG, TAB_TAG + "Either old, or no, previous data found.");
        }

        // If the above failed, try to obtain data from the web
        URL jsonURL;
        if (param.equals(RECENT_PARAM)) {
            jsonURL = getRecentMoviesURL();
        } else if (param.equals(POPULAR_PARAM)) {
            jsonURL = getPopularMoviesURL();
        } else {
            return false;
        }

        String data = customJsonUtils.getJsonString(jsonURL);
        try {
            return parseMovieJson(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // If all the above methods failed, return false
        return false;
    }

}
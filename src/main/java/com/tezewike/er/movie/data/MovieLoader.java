package com.tezewike.er.movie.data;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.tezewike.er.BuildConfig;
import com.tezewike.er.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;


public class MovieLoader extends AsyncTaskLoader<Cursor> {
    String LOG_TAG = MovieLoader.class.getSimpleName();
    String TAB_TAG;

    Context context;
    Utilities utilities;
    MovieDbHelper movieSQLiteDb;

    int id;
    String param, currentTab;
    String RECENT_PARAM = "recent";
    String POPULAR_PARAM = "popular";
    String DETAIL_PARAM = "detail";

    Snackbar snackbar;

    public MovieLoader(Context context, String parameter) {
        super(context);
        this.context = context;
        this.param = parameter;

        this.TAB_TAG = param + " tab: ";
        this.movieSQLiteDb = new MovieDbHelper(context);
        this.utilities = new Utilities(context);
    }

    public MovieLoader(Context context, String parameter, String tab, int id) {
        super(context);
        this.context = context;
        this.param = parameter;
        this.currentTab = tab;
        this.id = id;

        this.TAB_TAG = param + " tab: ";
        this.movieSQLiteDb = new MovieDbHelper(context);
        this.utilities = new Utilities(context);
    }

    @Override
    public Cursor loadInBackground() {
        Log.v(LOG_TAG, TAB_TAG + "loadInBackground: Running...");

        if (!isAppConnected()) {
            snackbar = Snackbar.make(((Activity) context).getWindow().getDecorView().getRootView(),
                    "Not connected...", Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
            return null;
        }

        if (param.equals(DETAIL_PARAM)) {
            return getMovieIDtCursorData();
        } else if (param.equals(RECENT_PARAM)) {
            return getRecentCursorData();
        } else if (param.equals(POPULAR_PARAM)) {
            return getPopularCursorData();
        } else {
            return null;
        }

    }

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

    private URL getMovieURLFromID(int id) {
        final String BASE_URL = "https://api.themoviedb.org/3/movie/" + id + "?";
        final String API_PARAM = "api_key";
        String apiKey = BuildConfig.TMDB_API_KEY;

        // Build the url
        Uri uri = Uri.parse(BASE_URL).buildUpon()
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

    private Cursor parseDiscoverMovieJson(String jsonData) throws JSONException {

        if (jsonData == null) {
            return null;
        }

        JSONObject data = new JSONObject(jsonData);

        // Names of key Json elements that can be extracted
        final String LIST = "results";
        final String ID = "id";
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

            movieSQLiteDb.putInformation(param,
                    Integer.parseInt(movie.getString(ID)),
                    movie.getString(TITLE),
                    movie.getString(VOTE),
                    movie.getString(RELEASE),
                    getImageURL(movie.getString(IMAGE), true),
                    getImageURL(movie.getString(BACKDROP), false),
                    movie.getString(DESCRIPTION)
            );

        }

        Log.e(LOG_TAG, TAB_TAG + "Data inserted into table.");
        return movieSQLiteDb.getInformation(param);
    }

    private Cursor parseMovieFromID(String jsonData) throws JSONException {

        if (jsonData == null) {
            return null;
        }

        JSONObject movie = new JSONObject(jsonData);

        // Names of key Json elements that can be extracted
        final String ADULT = "adult";
        final String GENRE = "genres";
        final String GENRE_NAME = "name";
        final String STATUS = "status";
        final String RUNTIME = "runtime";
        final String TAGLINE = "tagline";

        boolean adult = Boolean.parseBoolean(movie.getString(ADULT));
        JSONArray genres = movie.getJSONArray(GENRE);
        List<String> genre_names =  new ArrayList<>();
        for (int i = 0; i < genres.length(); i++) {
            genre_names.add(genres.getJSONObject(i).getString(GENRE_NAME));
        }
        String status = movie.getString(STATUS);
        String run = movie.getString(RUNTIME);
        String tag = movie.getString(TAGLINE);
        movieSQLiteDb.putInformation(currentTab, id, adult, genre_names.get(0), status,
                run, tag);

        return movieSQLiteDb.getInformation(currentTab, id);
    }

    private boolean isOldInformation() {
        if (param.equals(RECENT_PARAM) || param.equals(POPULAR_PARAM)) {
            try {
                return movieSQLiteDb.compareDateInformation(param);
            } catch (SQLiteException e) {
                Log.v(LOG_TAG, "No table(s) currently exist.");
                return false;
            }
        } else if (param.equals(DETAIL_PARAM)) {
            return movieSQLiteDb.isDetailAvailable(currentTab, id);
        }

        return false;
    }

    private boolean isAppConnected() {
        return utilities.isNetworkAvailable();
    }

    private Cursor getRecentCursorData() {
        if (isOldInformation()) {
            // If the information isn't new, end the process
            Log.v(LOG_TAG, TAB_TAG + "Previous data found.");
            return movieSQLiteDb.getInformation(param);
        } else {
            Log.v(LOG_TAG, TAB_TAG + "Either old, or no, previous data found.");
        }

        // If the above failed, try to obtain data from the web
        URL jsonURL = getRecentMoviesURL();
        String data = utilities.getJsonString(jsonURL);
        try {
            return parseDiscoverMovieJson(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // If all the above methods failed, return null
        return null;
    }

    private Cursor getPopularCursorData() {
        if (isOldInformation()) {
            // If the information isn't new, end the process
            Log.v(LOG_TAG, TAB_TAG + "Previous data found.");
            return movieSQLiteDb.getInformation(param);
        } else {
            Log.v(LOG_TAG, TAB_TAG + "Either old, or no, previous data found.");
        }

        // If the above failed, try to obtain data from the web
        URL jsonURL = getPopularMoviesURL();
        String data = utilities.getJsonString(jsonURL);
        try {
            return parseDiscoverMovieJson(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // If all the above methods failed, return null
        return null;
    }

    private Cursor getMovieIDtCursorData() {
        if (isOldInformation()) {
            // If the information isn't new, end the process
            Log.v(LOG_TAG, TAB_TAG + "Previous data found.");
            return movieSQLiteDb.getInformation(currentTab, id);
        } else {
            Log.v(LOG_TAG, TAB_TAG + "Either old, or no, previous data found.");
        }

        // If the above failed, try to obtain data from the web
        URL jsonURL = getMovieURLFromID(id);
        String data = utilities.getJsonString(jsonURL);
        try {
            return parseMovieFromID(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // If all the above methods failed, return null
        return null;
    }

}
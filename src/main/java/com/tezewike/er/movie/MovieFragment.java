package com.tezewike.er.movie;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tezewike.er.BuildConfig;
import com.tezewike.er.R;
import com.tezewike.er.utils.CustomJsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment {
    private String LOG_TAG = MovieFragment.class.getSimpleName();

    private OnMovieSelectedListener itemListener;
    private Cursor mCursor;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter movieAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String param;

    // Container Activity must implement this interface
    public interface OnMovieSelectedListener {
        void onMovieSelected(String cursorId, String param);
    }

    public MovieFragment() {
        // Required empty public constructor
    }

    public static MovieFragment newInstance(String execute_param) {
        MovieFragment mFragment = new MovieFragment();
        Bundle bundle = new Bundle();
        bundle.putString("param", execute_param);
        mFragment.setArguments(bundle);

        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the movie_titles and posters
        setHasOptionsMenu(true);

        // Get parameters for ASyncTask from bundle
        param = getArguments().getString("param");

        if (param != null) {
            if (param.equals("recent")) {
                // use a grid layout manager
                int columns = 2;
                mLayoutManager = new GridLayoutManager(getActivity(), columns);
            } else if (param.equals("popular")) {
                // use a linear layout manager
                mLayoutManager = new LinearLayoutManager(getActivity());
            }

            updateMovies(param);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_refresh) {
            updateMovies(param);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        // Create a GridView
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.movie_recyclerview);

        // The LayoutManager is set in OnCreate
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        if (mCursor != null) {
            movieAdapter = new MovieAdapter(getActivity(), mCursor, param);
            mRecyclerView.setAdapter(movieAdapter);
        }

        Button button = (Button) rootView.findViewById(R.id.shuffle_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemListener.onMovieSelected(null, param);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void updateMovies(String param) {
        // TODO ~ Add preference parameters here
        FetchMovieData fetchMovieData = new FetchMovieData();
        fetchMovieData.execute(param);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            itemListener = (OnMovieSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMovieSelectedListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        itemListener = null;
    }

    private class FetchMovieData extends AsyncTask<String, Void, Boolean> {
        String LOG_TAG = FetchMovieData.class.getSimpleName();
        String TAB_TAG = param + " tab: ";

        MovieDbHelper movieSQLDb = new MovieDbHelper(getActivity());
        CustomJsonUtils customJsonUtils = new CustomJsonUtils(getActivity());
        ProgressDialog dialogLoad = new ProgressDialog(getActivity());

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
            int len = moviesJSON.length();

            // Place data into arrays
            String[] titles = new String[len];
            String[] posters = new String[len];
            String[] backdrops = new String[len];
            String[] descriptions = new String[len];
            String[] release = new String[len];
            String[] vote = new String[len];

            for (int i = 0; i < len; i++) {
                JSONObject movie = moviesJSON.getJSONObject(i);
                titles[i] = movie.getString(TITLE);
                posters[i] = getImageURL(movie.getString(IMAGE), true);
                backdrops[i] = getImageURL(movie.getString(BACKDROP), false);
                descriptions[i] = movie.getString(DESCRIPTION);
                release[i] = movie.getString(RELEASE);
                vote[i] = movie.getString(VOTE);

                movieSQLDb.putInformation(param, i, titles[i],
                        vote[i], release[i], posters[i], backdrops[i], descriptions[i]);
            }

            Log.e(LOG_TAG, TAB_TAG + "Data inserted into table.");

            return true;
        }

        private boolean isOldInformation() {
            try {
                return movieSQLDb.compareDateInformation(param);
            } catch (SQLiteException e) {
                Log.v(LOG_TAG, "No table(s) currently exist.");
                return false;
            }
        }

        @Override
        protected void onPreExecute() {
            dialogLoad.setMessage("Loading Movie List");
            dialogLoad.setCancelable(false);
            dialogLoad.setInverseBackgroundForced(false);
            dialogLoad.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Log.v(LOG_TAG, TAB_TAG + "doInBackground: Running...");

            String RECENT_PARAM = "recent";
            String POPULAR_PARAM = "popular";

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
                mCursor = movieSQLDb.getInformation(param, null);
                movieAdapter = new MovieAdapter(getActivity(), mCursor, param);
                mRecyclerView.setAdapter(movieAdapter);
                movieSQLDb.close();
            }

            // End the Loading dialogLoad box
            dialogLoad.dismiss();
        }

    }

    class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
        protected Context mContext;
        protected String parameter;
        protected List<MovieData> mMovies = new ArrayList<>();

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public TextView movieTitle;
            public ImageView posterImage;
            public TextView releaseDate;
            public TextView descriptText;

            public ViewHolder(View itemView) {
                super(itemView);

                if (parameter.equals("recent")) {
                    posterImage = (ImageView) itemView.findViewById(R.id.movie_layout_poster_rec);
                    movieTitle = (TextView) itemView.findViewById(R.id.movie_layout_title_rec);
                } else if (parameter.equals("popular")) {
                    posterImage = (ImageView) itemView.findViewById(R.id.movie_layout_poster_pop);
                    movieTitle = (TextView) itemView.findViewById(R.id.movie_layout_title_pop);
                    descriptText = (TextView) itemView.findViewById(R.id.movie_layout_desc_pop);
                    releaseDate = (TextView) itemView.findViewById(R.id.movie_layout_release_pop);
                }

                posterImage.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getPosition();
                itemListener.onMovieSelected(""+position, param);
            }

        }

        public MovieAdapter(Context c, Cursor movieCursor, String param) {
            this.mContext = c;
            this.mMovies = getMovieData(movieCursor);
            this.parameter = param;
        }

        @Override
        public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int view) {
            View convertView;
            if (parameter.equals("recent")) {
                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_movie_recent, parent, false);
            } else if (parameter.equals("popular")) {
                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_movie_popular, parent, false);
            } else {
                Log.e("MovieAdapter", "Parameter not found. Returning null ViewHolder...");
                return null;
            }

            return new ViewHolder(convertView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            MovieData movie = mMovies.get(position);
            String url = movie.poster;

            holder.movieTitle.setText(movie.title);
            Picasso.with(mContext).load(url)
                    .placeholder(R.drawable.placeholder_poster)
                    .error(R.drawable.placeholder_poster)
                    .into(holder.posterImage);

            if (parameter.equals("popular")) {
                holder.releaseDate.setText(movie.release);
                holder.descriptText.setText(movie.description);
            }

        }

        @Override
        public int getItemCount() {
            return mMovies.size();
        }

        private List<MovieData> getMovieData(Cursor cursor) {
            List<MovieData> list = new ArrayList<>();

            int col_title = cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_NAME);
            int col_poster = cursor.getColumnIndex(MovieContract.MovieEntry.POSTER_URL);
            int col_backdrop = cursor.getColumnIndex(MovieContract.MovieEntry.BACKDROP_URL);
            int col_descript = cursor.getColumnIndex(MovieContract.MovieEntry.DESCRIPTION);
            int col_vote = cursor.getColumnIndex(MovieContract.MovieEntry.VOTE_AVERAGE);
            int col_release = cursor.getColumnIndex(MovieContract.MovieEntry.RELEASE_DATE);

            cursor.moveToFirst();
            do {
                list.add( new MovieData(
                        cursor.getString(col_title),
                        cursor.getString(col_poster),
                        cursor.getString(col_backdrop),
                        cursor.getString(col_descript),
                        cursor.getString(col_release),
                        cursor.getString(col_vote)
                    )
                );

            } while (cursor.moveToNext());

            cursor.close();

            return list;
        }

    }

}

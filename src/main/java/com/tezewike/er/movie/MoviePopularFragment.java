package com.tezewike.er.movie;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tezewike.er.R;
import com.tezewike.er.movie.data.MovieContract;
import com.tezewike.er.movie.data.MovieData;
import com.tezewike.er.movie.data.MovieLoader;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoviePopularFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MoviePopularFragment.class.getSimpleName();
    private final String TAB = "popular";
    private final int LOADER_ID = 2;

    private OnMovieSelectedListener itemListener;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter movieAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public MoviePopularFragment() {
        // Required empty public constructor
    }

    public static MoviePopularFragment newInstances() {

        return new MoviePopularFragment();
    }

    // Container Activity must implement this interface
    public interface OnMovieSelectedListener {
        void onMovieSelected(String cursorId, String param);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MovieLoader(getContext(), TAB);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieAdapter = new PopularMovieAdapter(getActivity(), data);
        mRecyclerView.setAdapter(movieAdapter);
        Log.v(LOG_TAG, "onLoadFinished...");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "onLoaderReset called...");
        mRecyclerView.setAdapter(null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the movie_titles and posters
        setHasOptionsMenu(true);

        setRetainInstance(true);
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
            updateMovies(TAB);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        // Initialize recyclerView
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.movie_recyclerView);

        // Create a linear layout
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        movieAdapter = new PopularMovieAdapter(getActivity(), null);
        mRecyclerView.setAdapter(movieAdapter);

        // Must use forceLoad() when using support libraries
        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this).forceLoad();

/*      Button button = (Button) rootView.findViewById(R.id.shuffle_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemListener.onMovieSelected(null, param);
            }
        });
*/
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void updateMovies(String param) {
        // TODO ~ Add preference parameters here
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

    class PopularMovieAdapter extends RecyclerView.Adapter<PopularMovieAdapter.ViewHolder> {
        protected Context mContext;
        protected List<MovieData> mMovies = new ArrayList<>();

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public TextView movieTitle;
            public ImageView posterImage;
            public TextView releaseDate;
            public TextView descriptText;

            public ViewHolder(View itemView) {
                super(itemView);
                posterImage = (ImageView) itemView.findViewById(R.id.movie_layout_poster_pop);
                movieTitle = (TextView) itemView.findViewById(R.id.movie_layout_title_pop);
                descriptText = (TextView) itemView.findViewById(R.id.movie_layout_desc_pop);
                releaseDate = (TextView) itemView.findViewById(R.id.movie_layout_release_pop);

                posterImage.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getPosition();
                itemListener.onMovieSelected(""+position, TAB);
            }

        }

        public PopularMovieAdapter(Context c, Cursor movieCursor) {
            this.mContext = c;
            if (movieCursor != null) {
                this.mMovies = getMovieData(movieCursor);
            }
        }

        @Override
        public PopularMovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int view) {

            View convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_movie_popular, parent, false);

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

            holder.releaseDate.setText(movie.release);
            holder.descriptText.setText(movie.description);

        }

        @Override
        public int getItemCount() {
            return mMovies.size();
        }

        private List<MovieData> getMovieData(Cursor cursor) {

            List<MovieData> list = new ArrayList<>();
            int i = 0;

            try {
                cursor.moveToFirst();
                do {
                    list.add( new MovieData(
                            cursor.getString(MovieContract.MovieEntry.INT_MOVIE_NAME),
                            cursor.getString(MovieContract.MovieEntry.INT_POSTER_URL),
                            cursor.getString(MovieContract.MovieEntry.INT_BACKDROP_URL),
                            cursor.getString(MovieContract.MovieEntry.INT_DESCRIPTION),
                            cursor.getString(MovieContract.MovieEntry.INT_RELEASE_DATE),
                            cursor.getString(MovieContract.MovieEntry.INT_VOTE_AVERAGE)
                        )
                    );
                    i++;
                } while (cursor.moveToNext());

            } catch (NullPointerException npe) {
                Log.e(LOG_TAG, "NullPointerException while generating "+ i +" items");
                return list;
            } catch (CursorIndexOutOfBoundsException e) {
                Log.e(LOG_TAG, "CursorIndexOutOfBoundsException while generating "+ i +" items");
                cursor.close();
                return list;
            }

            Log.v(LOG_TAG, i + " items generated from cursor.");
            return list;
        }

    }

}

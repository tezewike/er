package com.tezewike.er.movie;


import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.os.Handler;
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
import com.tezewike.er.movie.data.MovieLoader;
import com.tezewike.er.utils.Utilities;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoviePopularFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MoviePopularFragment.class.getSimpleName();
    private final String TAB = "popular";

    private Integer lastPosition;
    private OnMovieSelectedListener itemListener;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter movieAdapter;

    public MoviePopularFragment() {
        // Required empty public constructor
    }

    // Container Activity must implement this interface
    public interface OnMovieSelectedListener {
        void onMovieSelected(Integer cursorId, String param);
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
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        movieAdapter = new PopularMovieAdapter(getActivity(), null);
        mRecyclerView.setAdapter(movieAdapter);

        // Must use forceLoad() when using support libraries
        int LOADER_ID = 2;
        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this).forceLoad();

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            outState.putInt("popular_last_item", lastPosition);
        } catch (NullPointerException e) {
            // do nothing
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            lastPosition = savedInstanceState.getInt("popular_last_item");
        } catch (NullPointerException e) {
            // do nothing
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        if (lastPosition != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView.scrollToPosition(lastPosition);
                }
            }, 100);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            itemListener = (OnMovieSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
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
        protected List<String> ids, titles, posters, releases, descriptions;

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
                lastPosition = getLayoutPosition();

                int itemId = Integer.parseInt(ids.get(lastPosition));
                Log.v(LOG_TAG, "id: " + itemId + " title: " + titles.get(lastPosition));
                itemListener.onMovieSelected(itemId, TAB);
            }

        }

        public PopularMovieAdapter(Context c, Cursor movieCursor) {
            this.mContext = c;
            clearLists();
            if (movieCursor != null) {
                addMovieDataToLists(movieCursor);
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
            String url = posters.get(position);

            holder.movieTitle.setText(titles.get(position));
            Picasso.with(mContext).load(url)
                    .placeholder(R.drawable.placeholder_poster)
                    .error(R.drawable.placeholder_poster)
                    .into(holder.posterImage);

            holder.releaseDate.setText(releases.get(position));
            holder.descriptText.setText(descriptions.get(position));

        }

        @Override
        public int getItemCount() {
            return titles.size();
        }

        private void addMovieDataToLists(Cursor cursor) {

            try {
                cursor.moveToFirst();
                do {
                    ids.add(cursor.getString(MovieContract.MovieEntry.INT_ID));
                    titles.add(cursor.getString(MovieContract.MovieEntry.INT_MOVIE_NAME));
                    posters.add(cursor.getString(MovieContract.MovieEntry.INT_POSTER_URL));
                    releases.add(Utilities.dateFormatter(
                            cursor.getString(MovieContract.MovieEntry.INT_RELEASE_DATE)));
                    descriptions.add(cursor.getString(MovieContract.MovieEntry.INT_DESCRIPTION));
                } while (cursor.moveToNext());
            } catch (NullPointerException npe) {
                Log.e(LOG_TAG, "NullPointerException while generating items");
            } catch (CursorIndexOutOfBoundsException e) {
                Log.e(LOG_TAG, "CursorIndexOutOfBoundsException while generating items");
                cursor.close();
            }

        }

        private void clearLists() {
            ids = new ArrayList<>();
            titles = new ArrayList<>();
            posters = new ArrayList<>();
            releases = new ArrayList<>();
            descriptions = new ArrayList<>();
        }

    }

}

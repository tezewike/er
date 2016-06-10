package com.tezewike.er.movie;


import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
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

import java.util.ArrayList;
import java.util.List;


public class MovieRecentFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private String LOG_TAG = MovieRecentFragment.class.getSimpleName();
    private final String TAB = "recent";

    private Integer lastPosition;
    private OnMovieSelectedListener itemListener;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter movieAdapter;

    public MovieRecentFragment() {
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
        movieAdapter = new RecentMovieAdapter(getActivity(), data);
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

        // Init recyclerView
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.movie_recyclerView);

        // Create a grid layout
        int columns = 2;
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), columns);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        movieAdapter = new RecentMovieAdapter(getActivity(), null);
        mRecyclerView.setAdapter(movieAdapter);

        // Must use forceLoad() when using support libraries
        int LOADER_ID = 1;
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

    class RecentMovieAdapter extends RecyclerView.Adapter<RecentMovieAdapter.ViewHolder> {
        protected Context mContext;
        protected List<String> ids, titles, posters;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public TextView movieTitle;
            public ImageView posterImage;

            public ViewHolder(View itemView) {
                super(itemView);

                posterImage = (ImageView) itemView.findViewById(R.id.movie_layout_poster_rec);
                movieTitle = (TextView) itemView.findViewById(R.id.movie_layout_title_rec);

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

        public RecentMovieAdapter(Context c, Cursor movieCursor) {
            this.mContext = c;
            clearLists();
            if (movieCursor != null) {
                addMovieDataToLists(movieCursor);
            }
        }

        @Override
        public RecentMovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int view) {

            View convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_movie_recent, parent, false);

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
        }

    }

}

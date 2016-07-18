package com.tezewike.er.movie;


import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tezewike.er.R;
import com.tezewike.er.movie.data.MovieContract;
import com.tezewike.er.movie.data.MovieDbHelper;
import com.tezewike.er.movie.data.MovieLoader;
import com.tezewike.er.utils.Utilities;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the listener interface
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private final String parameter = "detail";
    private final String POPULAR = "popular";
    private final String RECENT  = "recent";
    private String TAB;

    private Cursor mCursor;
    private DetailViewChanger mChanger;
    private View rootView;
    private MovieDbHelper movieSQLDb;
    private int movieId;

    public MovieDetailFragment() {
        // Required empty public Constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Bundle data = getArguments();
            movieId = data.getInt("movie");
            TAB = data.getString("param");
            movieSQLDb = new MovieDbHelper(getActivity());
            mCursor = movieSQLDb.getInformation(TAB, movieId);
        } catch (NullPointerException e) {
            Log.v(LOG_TAG, "No argument passed.");
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MovieLoader(getContext(), parameter, TAB, movieId);
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        Log.v(LOG_TAG, "onLoadFinished...");
        mChanger = new DetailViewChanger(rootView, mCursor);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        Log.v(LOG_TAG, "onLoaderReset called...");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        mChanger = new DetailViewChanger(rootView, mCursor);
        mChanger.updateView();

        if (TAB != null && movieId != 0) {
            getActivity().getSupportLoaderManager().initLoader(12, null, this).forceLoad();
        }

        return rootView;
    }

    private class DetailViewChanger {
        ViewHolder viewHolder;
        String mTitle, mVote, mRelease, mPoster, mBackdrop, mDescription, mGenre, mStatus,
                mRuntime, mTagline;

        public class ViewHolder {
            TextView title, vote, release, description, genre, status, runtime, tagline;
            final ImageView backdrop, poster;
            final CardView card;

            public ViewHolder(View v) {
                card = (CardView) v.findViewById(R.id.detail_cardView);

                title = (TextView) v.findViewById(R.id.movie_detail_title);
                description = (TextView) v.findViewById(R.id.movie_detail_descript);
                backdrop = (ImageView) v.findViewById(R.id.movie_detail_backdrop);
                poster = (ImageView) v.findViewById(R.id.movie_detail_poster);

                tagline = (TextView) v.findViewById(R.id.movie_detail_tagline);
                vote = (TextView) v.findViewById(R.id.movie_detail_vote);
                release = (TextView) v.findViewById(R.id.movie_detail_release);
            //    genre = (TextView) v.findViewById(R.id.movie_detail_genre);
                runtime = (TextView) v.findViewById(R.id.movie_detail_runtime);
            }
        }

        public DetailViewChanger(View view, Cursor cursor) {
            this.viewHolder = new ViewHolder(view);

            if (cursor != null) {
                viewHolder.card.setVisibility(View.VISIBLE);
                addMovieDataToVariables(cursor);
            } else {
                viewHolder.card.setVisibility(View.GONE);
            }
        }

        public void updateView() {
            Picasso.with(getContext()).load(mBackdrop)
                    .into(viewHolder.backdrop);

            Picasso.with(getContext()).load(mPoster).placeholder(R.drawable.placeholder_poster)
                    .into(viewHolder.poster);

            viewHolder.title.setText(mTitle);
            viewHolder.release.setText(mRelease);
            viewHolder.tagline.setText(mTagline);
            viewHolder.runtime.setText(mRuntime);
//            viewHolder.genre.setText(mGenre);
            viewHolder.vote.setText(mVote);
            viewHolder.description.setText(mDescription);

        }

        private void addMovieDataToVariables(Cursor cursor) {

            try {
                cursor.moveToFirst();
                mTitle = cursor.getString(MovieContract.MovieEntry.INT_MOVIE_NAME);
                mVote = cursor.getString(MovieContract.MovieEntry.INT_VOTE_AVERAGE);
                mRelease = Utilities.dateFormatter(
                        cursor.getString(MovieContract.MovieEntry.INT_RELEASE_DATE));
                mPoster = cursor.getString(MovieContract.MovieEntry.INT_POSTER_URL);
                mBackdrop = cursor.getString(MovieContract.MovieEntry.INT_BACKDROP_URL);
                mDescription = cursor.getString(MovieContract.MovieEntry.INT_DESCRIPTION);
//                mGenre = cursor.getString(MovieContract.MovieEntry.INT_GENRE);
                mStatus = cursor.getString(MovieContract.MovieEntry.INT_STATUS);
                mRuntime = Utilities.minutesConvert(
                        cursor.getString(MovieContract.MovieEntry.INT_RUNTIME));
                mTagline = cursor.getString(MovieContract.MovieEntry.INT_TAGLINE);

            } catch (NullPointerException npe) {
                Log.e(LOG_TAG, "NullPointerException while generating items");
            } catch (CursorIndexOutOfBoundsException e) {
                Log.e(LOG_TAG, "CursorIndexOutOfBoundsException while generating items");
                cursor.close();
            }
        }

    }

}

package com.tezewike.er.movie;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tezewike.er.R;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 */
public class MovieDetailFragment extends Fragment {
    Cursor mCursor;
    MovieDbHelper movieSQLDb;
    MovieData movie;

    public MovieDetailFragment() {
        // Required empty public Constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle data = getArguments();
        String id = data.getString("movie");
        String parameter = data.getString("param");

        movieSQLDb = new MovieDbHelper(getActivity());

        mCursor = movieSQLDb.getInformation(parameter, id);
        movie = getMovieData(mCursor).get(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        ViewHolder holder = new ViewHolder(rootView);

        String backdropUrl = movie.backdrop;
        String posterUrl = movie.poster;

        if (backdropUrl == null) {
            // Todo ~ add placeholder
        } else {
            Picasso.with(getActivity()).load(movie.backdrop).into(holder.backdrop);
        }
        if (posterUrl == null) {
            // Todo ~ add placeholder
        } else {
            Picasso.with(getActivity()).load(movie.poster).into(holder.poster);
        }

        holder.description.setText(movie.description);
        holder.title.setText(movie.title);

        return rootView;
    }

    private class ViewHolder {
        final TextView title;
        final TextView description;
        final ImageView backdrop;
        final ImageView poster;

        public ViewHolder(View v) {
            title = (TextView) v.findViewById(R.id.movie_detail_title);
            description = (TextView) v.findViewById(R.id.movie_detail_descript);
            backdrop = (ImageView) v.findViewById(R.id.movie_detail_backdrop);
            poster = (ImageView) v.findViewById(R.id.movie_detail_poster);
        }
    }

    private List<MovieData> getMovieData(Cursor cursor) {
        List<MovieData> list = new ArrayList<>();
        cursor.moveToFirst();
        do {
            list.add( new MovieData(
                    cursor.getString(1),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(3),
                    cursor.getString(2)));
        } while (cursor.moveToNext());

        cursor.close();

        return list;
    }

}

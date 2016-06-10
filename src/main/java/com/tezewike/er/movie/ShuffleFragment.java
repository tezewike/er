package com.tezewike.er.movie;


import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tezewike.er.R;
import com.tezewike.er.movie.data.MovieData;
import com.tezewike.er.movie.data.MovieDbHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShuffleFragment extends Fragment {
    MovieRecentFragment.OnMovieSelectedListener itemListener;
    Cursor mCursor;
    MovieDbHelper movieSQLDb = new MovieDbHelper(getActivity());
    List<MovieData> movies;
    String parameter;


    public ShuffleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getArguments();
        // Get movie data from bundle
        int id = Integer.parseInt(data.getString("movie"));
        parameter = data.getString("param");

        mCursor = movieSQLDb.getInformation(parameter, id);
        movies = getMovieData(mCursor);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_shuffle, container, false);

        if (movies == null) {
            return rootView;
        }

        // Create a holder for our views
        final ViewHolder holder = new ViewHolder(rootView);

        final int[] position = {-1};
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovieData[] selectedMovie = new MovieData[1];
                selectedMovie[0] = movies.get(position[0]);
                itemListener.onMovieSelected(position[0], parameter);
            }
        });

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            boolean visibility = false;
            long initial = 50;
            @Override
            public void run() {
                if (initial < 750) {
                    position[0] = randInt(movies.size());
                    holder.title.setText(movies.get(position[0]).title);

                    initial = (long) (initial * 1.15);
                } else {
                    while (!visibility) {
                        holder.image.setVisibility(View.VISIBLE);
                        Picasso.with(getActivity())
                                .load(movies.get(position[0]).poster)
                                .into(holder.image);
                        visibility = true;
                    }
                }

                handler.postDelayed(this, initial);
            }
        };

        handler.postDelayed(runnable, 1000);

        return rootView;
    }

    private class ViewHolder {
        final TextView title;
        final ImageView image;

        public ViewHolder(View view) {
            title = (TextView) view.findViewById(R.id.shuffle_text);
            image = (ImageView) view.findViewById(R.id.shuffle_image);
        }
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            itemListener = (MovieRecentFragment.OnMovieSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement MovieRecentFragment.OnOptionSelectedListener");
        }

    }

    public static int randInt(int max) {
        Random rand = new Random();
        return rand.nextInt(max);
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

        cursor.close();;

        return list;
    }

}

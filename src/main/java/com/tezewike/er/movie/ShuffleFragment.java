package com.tezewike.er.movie;


import com.squareup.picasso.Picasso;
import com.tezewike.er.*;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShuffleFragment extends Fragment {
    MovieFragment.OnMovieSelectedListener itemListener;
    MovieData[] movies;

    public ShuffleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getArguments();
        // Get movie data from bundle
        movies = (MovieData[]) data.getParcelableArray("movies");
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
                selectedMovie[0] = movies[position[0]];
                itemListener.onMovieSelected(selectedMovie, true);
            }
        });

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            boolean visibility = false;
            long initial = 50;
            @Override
            public void run() {
                if (initial < 750) {
                    position[0] = randInt(movies.length);
                    holder.title.setText(movies[position[0]].title);

                    initial = (long) (initial * 1.15);
                } else {
                    while (!visibility) {
                        holder.image.setVisibility(View.VISIBLE);
                        Picasso.with(getActivity())
                                .load(movies[position[0]].poster)
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
            itemListener = (MovieFragment.OnMovieSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement MovieFragment.OnOptionSelectedListener");
        }

    }

    public static int randInt(int max) {
        Random rand = new Random();
        return rand.nextInt(max);
    }

}

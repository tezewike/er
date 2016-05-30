package com.tezewike.er.movie;


import com.tezewike.er.*;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 */
public class MovieDetailFragment extends Fragment {
    MovieData movie;

    public MovieDetailFragment() {
        // Required empty public Constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle data = getArguments();
        // Get movie data from bundle
        movie = (MovieData) data.getParcelable("movie");
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

}

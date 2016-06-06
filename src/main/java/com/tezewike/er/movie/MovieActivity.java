package com.tezewike.er.movie;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.tezewike.er.R;
import com.tezewike.er.utils.ViewPagerAdapter;

public class MovieActivity extends AppCompatActivity
        implements MovieFragment.OnMovieSelectedListener {

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        toolbar = (Toolbar) findViewById(R.id.movie_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Home");

        tabLayout = (TabLayout) findViewById(R.id.movie_tablayout);
        viewPager = (ViewPager) findViewById(R.id.movie_viewpager);

        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mAdapter.addFragment(MovieFragment.newInstance("recent"), "New Releases");
        mAdapter.addFragment(MovieFragment.newInstance("popular"), "Popular Now");

        viewPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public void onMovieSelected(String id, String param) {
        Bundle bundle = new Bundle();
        bundle.putString("param", param);

        if (id != null) {
            bundle.putString("movie", id);
            Fragment detailFragment = new MovieDetailFragment();
            detailFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, detailFragment)
                    .addToBackStack("frag1")
                    .commit();
        } else {
            bundle.putString("movies", null);
            Fragment shuffleFragment = new ShuffleFragment();
            shuffleFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, shuffleFragment)
                    .addToBackStack("frag1")
                    .commit();
        }


    }

}

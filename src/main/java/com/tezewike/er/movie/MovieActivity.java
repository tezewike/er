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
        implements MovieRecentFragment.OnMovieSelectedListener,
                   MoviePopularFragment.OnMovieSelectedListener {

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter mAdapter;
    boolean twoPaneActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        toolbar = (Toolbar) findViewById(R.id.movie_toolbar);
        setSupportActionBar(toolbar);

        tabLayout = (TabLayout) findViewById(R.id.movie_tablayout);
        viewPager = (ViewPager) findViewById(R.id.movie_viewpager);

        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mAdapter.addFragment(new MovieRecentFragment(), "New Releases");
        mAdapter.addFragment(new MoviePopularFragment(), "Popular Now");
        viewPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(viewPager);

        if (findViewById(R.id.movie_detail_container) != null) {
            twoPaneActivity = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailFragment())
                        .addToBackStack("frag1")
                        .commit();
            }

        } else {
            twoPaneActivity = false;
        }

    }

    @Override
    public void onMovieSelected(Integer id, String param) {
        Bundle bundle = new Bundle();
        bundle.putString("param", param);
        int layout;

        if (id != null) {
            bundle.putInt("movie", id);
            Fragment detailFragment = new MovieDetailFragment();
            detailFragment.setArguments(bundle);

            if (twoPaneActivity) {
                layout = R.id.movie_detail_container;
            } else {
                layout = android.R.id.content;
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(layout, detailFragment)
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

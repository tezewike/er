package com.tezewike.er;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.tezewike.er.movie.MovieActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, MovieActivity.class);
        startActivity(intent);

        // Destroy the activity
        finish();
    }

}

/**
 * Implement OnOptionSelectedListener if using this segment
 *
    @Override
    public void onOptionSelected(int selection) {
        if (selection == 0) {
            Intent intent = new Intent(this, MovieActivity.class);
            startActivity(intent);
        } else if (selection == 1) {
            Intent intent = new Intent(this, SteamActivity.class);
            startActivity(intent);
        }
    }
**/

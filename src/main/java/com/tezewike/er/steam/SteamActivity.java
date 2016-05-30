package com.tezewike.er.steam;

import android.os.Bundle;

import android.app.Activity;


import com.tezewike.er.R;

public class SteamActivity extends Activity
    implements SteamFragment.OnGameSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steam);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SteamFragment())
                    .commit();
        }

    }

    @Override
    public void onGameSelected(SteamData[] steamData, boolean one) {

    }
}

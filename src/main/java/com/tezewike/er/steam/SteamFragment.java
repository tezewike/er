package com.tezewike.er.steam;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tezewike.er.BuildConfig;
import com.tezewike.er.R;
import com.tezewike.er.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class SteamFragment extends Fragment {

    OnGameSelectedListener itemListener;
    SteamData[] games;
    List<String> titles = new ArrayList<>();
    List<String> images = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter steamAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    // Container Activity must implement this interface
    public interface OnGameSelectedListener {
        public void onGameSelected(SteamData[] steamData, boolean one);
    }

    public SteamFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the movie_titles and posters
        updateSteamList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_steam, container, false);

        // Create a DrigView
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.steam_gridView);

        int columns = 2;

        mLayoutManager = new GridLayoutManager(getActivity(), columns);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        steamAdapter = new SteamAdapter(getActivity(), titles, images);
        mRecyclerView.setAdapter(steamAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void updateSteamList() {
        // TODO ~ Add preference parameters here
        FetchSteamData fetchSteamData = new FetchSteamData();
        fetchSteamData.execute();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            itemListener = (OnGameSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnGameSelectedListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        itemListener = null;
    }

    public class FetchSteamData extends AsyncTask<Void, Void, SteamData[]> {

        ProgressDialog dialog = new ProgressDialog(getActivity());
        String LOG_TAG = FetchSteamData.class.getSimpleName();

        Utilities utilities = new Utilities(getActivity());
        String steam_data_file = "steam_data.txt";

        private URL getSteamURL(String steamId) {

            // Set queries
            final String BASE_URL = "http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?";
            final String FORMAT_PARAM = "format";
            final String SID_PARAM = "steamid";
            final String API_PARAM = "key";
            final String INFO_PARAM = "include_appinfo"; // Value 1 means true, 0 means false

            // Get apiK
            String apiKey = BuildConfig.STEAM_API_KEY;

            // Build the url
            Uri uri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(API_PARAM, apiKey)
                    .appendQueryParameter(SID_PARAM, "76561197960434622")
                    .appendQueryParameter(FORMAT_PARAM, "json")
                    .appendQueryParameter(INFO_PARAM, "1")
                    .build();

            // Attempt to create url
            URL url;
            try {
                url = new URL(uri.buildUpon().toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            return url;
        }

        private String getImageURL(String urlPath, String appid) {

            String base = "http://media.steampowered.com/steamcommunity/public/images/apps/";

            if (urlPath == null) {
                return null;
            }

            return base + appid +"/" + urlPath + ".jpg";
        }

        private SteamData[] parseSteamJson(String jsonData, boolean fromFile)
                throws JSONException {

            if (!fromFile) {
                // If this data wasn't from the file, write to the file.
                utilities.writeToFile(steam_data_file, jsonData);
            } else {
                Log.v(LOG_TAG, "Data found. Attempting to parse json data.");
            }

            // Names of key Json elements that can be extracted
            final String LIST = "response";
            final String GAMES = "games";
            final String APPID = "appid";
            final String TITLE = "name";
            final String PLAYTIME = "playtime_forever";
            final String IMAGE = "img_logo_url";

            JSONObject data = new JSONObject(jsonData);
            JSONObject response = data.getJSONObject(LIST);

            // Each value in games[i] corresponds to one movie
            JSONArray gamesJSON = response.getJSONArray(GAMES);
            int len = gamesJSON.length();

            if (len > 30 ) {
                len = 30;
            }

            // Place data into arrays
            games = new SteamData[len];

            String[] titles = new String[len];
            String[] appids = new String[len];
            String[] images = new String[len];

            for (int i = 0; i < len; i++) {
                JSONObject game = gamesJSON.getJSONObject(i);

                if (!game.get(PLAYTIME).equals("0")) {
                    titles[i] = game.getString(TITLE);
                    appids[i] = game.getString(APPID);
                    images[i] = getImageURL(game.getString(IMAGE), appids[i]);

                    games[i] = new SteamData(titles[i], appids[i], images[i]);
                }
            }

            return games;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Downloading Steam Game List");
            dialog.setCancelable(false);
            dialog.setInverseBackgroundForced(false);
            dialog.show();
        }

        @Override
        protected SteamData[] doInBackground(Void... params) {
            Log.v("doInBackgroundSteam","ran");
            URL jsonURL;

            String data = utilities.readFromFile(steam_data_file);

            if (!data.equals("")) {
                try {
                    return parseSteamJson(data, true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // If the above failed, try to obtain data from the web
            jsonURL = getSteamURL(null);
            data = utilities.getJsonString(jsonURL);
            try {
                return parseSteamJson(data, false);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // If all the above methods failed, return null
            return null;
        }

        @Override
        protected void onPostExecute(SteamData[] results) {
            if (results != null) {
                titles.clear();
                images.clear();
                for (int i = 0; i < games.length; i++) {
                    titles.add(games[i].title);
                    images.add(games[i].image);
                }
                steamAdapter = new SteamAdapter(getActivity(), titles, images);
                mRecyclerView.setAdapter(steamAdapter);
            }

            // End the Loading dialog box
            dialog.dismiss();
        }

    }

    class SteamAdapter extends RecyclerView.Adapter<SteamAdapter.ViewHolder> {
        protected Context mContext;
        protected List<String> mUrls;
        protected List<String> mTitles;

        public class ViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener {

            public ImageView imageView;

            public ViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.steam_layout_image);
                imageView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getPosition();

                SteamData[] selectedGame = new SteamData[1];
                selectedGame[0] = games[position];
                itemListener.onGameSelected(selectedGame, true);
            }

        }

        public SteamAdapter(Context c, List<String> titles, List<String> urls) {
            this.mContext = c;
            this.mTitles = titles;
            this.mUrls = urls;
        }

        @Override
        public SteamAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int view) {
            View convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_steam_frag, parent, false);

            ViewHolder viewHolder = new ViewHolder(convertView);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // Set the image using Picasso to load in image
            String url = mUrls.get(position);
            if  ((url != null && !url.equals(""))) {
                // Set the title
                Picasso.with(mContext).load(mUrls.get(position))
                        .into(holder.imageView);
            }

        }

        @Override
        public int getItemCount() {
            return mTitles.size();
        }

    }

}

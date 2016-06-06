package com.tezewike.er.movie;


import android.provider.BaseColumns;


public class MovieContract {

    public static final String DATABASE_NAME = "movie.db";

    public static abstract class MovieEntry implements BaseColumns {

        // Table Names
        public static final String TABLE_NAME_RECENT = "recent";
        public static final String TABLE_NAME_POPULAR = "popular";

        // Parameter names
        public static final String ID = "id";
        public static final String MOVIE_NAME = "title";
        public static final String VOTE_AVERAGE = "vote";
        public static final String RELEASE_DATE = "release";
        public static final String POSTER_URL = "poster";
        public static final String BACKDROP_URL = "backdrop";
        public static final String DESCRIPTION = "description";

    }

}

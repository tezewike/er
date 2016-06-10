package com.tezewike.er.movie.data;


import android.provider.BaseColumns;


public class MovieContract {

    public static final String DATABASE_NAME = "movie.db";

    public static abstract class MovieEntry implements BaseColumns {;

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
        public static final String ADULT_RATED = "adult ";
        public static final String GENRE = "genre";
        public static final String STATUS = "status";
        public static final String RUNTIME = "runtime";
        public static final String TAGLINE = "tagline";


        public static int INT_ID = 0;
        public static int INT_MOVIE_NAME = 1;
        public static int INT_VOTE_AVERAGE = 2;
        public static int INT_RELEASE_DATE = 3;
        public static int INT_POSTER_URL = 4;
        public static int INT_BACKDROP_URL = 5;
        public static int INT_DESCRIPTION = 6;
        public static int INT_ADULT_RATED = 7;
        public static int INT_GENRE = 8;
        public static int INT_STATUS = 9;
        public static int INT_RUNTIME = 10;
        public static int INT_TAGLINE = 11;

    }

}

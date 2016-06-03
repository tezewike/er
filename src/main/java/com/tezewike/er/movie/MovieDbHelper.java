package com.tezewike.er.movie;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tezewike.er.movie.MovieTable.MovieEntry;

/**
 * Created by Tobe on 6/2/2016.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private final String LOG_TAG = MovieDbHelper.this.getDatabaseName();
    private final String POPULAR = "popular";
    private final String RECENT  = "recent";

    public static final int database_version = 1;

    public MovieDbHelper(Context context) {
        super(context, MovieTable.DATABASE_NAME, null, database_version);
        Log.v(LOG_TAG, "Database Created.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_RECENT_MOVIE_TABLE =
                "CREATE TABLE " + MovieEntry.TABLE_NAME_RECENT + " (" +
                        MovieEntry.ID           + " INTEGER NOT NULL, " +
                        MovieEntry.MOVIE_NAME   + " TEXT NOT NULL, " +
                        MovieEntry.VOTE_AVERAGE + " TEXT NOT NULL, " +
                        MovieEntry.RELEASE_DATE + " TEXT NOT NULL, " +
                        MovieEntry.POSTER_URL   + " TEXT NOT NULL, " +
                        MovieEntry.BACKDROP_URL + " TEXT NOT NULL, " +
                        MovieEntry.DESCRIPTION  + " TEXT NOT NULL " +
                        " );";

        final String SQL_CREATE_POPULAR_MOVIE_TABLE =
                "CREATE TABLE " + MovieEntry.TABLE_NAME_POPULAR + " (" +
                        MovieEntry.ID           + " INTEGER NOT NULL, " +
                        MovieEntry.MOVIE_NAME   + " TEXT NOT NULL, " +
                        MovieEntry.VOTE_AVERAGE + " TEXT NOT NULL, " +
                        MovieEntry.RELEASE_DATE + " TEXT NOT NULL, " +
                        MovieEntry.POSTER_URL   + " TEXT NOT NULL, " +
                        MovieEntry.BACKDROP_URL + " TEXT NOT NULL, " +
                        MovieEntry.DESCRIPTION  + " TEXT NOT NULL " +
                        " );";

        db.execSQL(SQL_CREATE_RECENT_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_POPULAR_MOVIE_TABLE);

        Log.v(LOG_TAG, "Tables Created.");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME_RECENT);
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME_POPULAR);
        onCreate(db);
    }

    public void putInformation(MovieDbHelper db, String param, int id, String name,
                               String vote, String release, String poster, String backdrop, String description) {
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(MovieEntry.ID, id);
        contentValues.put(MovieEntry.MOVIE_NAME, name);
        contentValues.put(MovieEntry.VOTE_AVERAGE, vote);
        contentValues.put(MovieEntry.RELEASE_DATE, release);
        contentValues.put(MovieEntry.POSTER_URL, poster);
        contentValues.put(MovieEntry.BACKDROP_URL, backdrop);
        contentValues.put(MovieEntry.DESCRIPTION, description);

        if (param.equals(RECENT)) {
            sqLiteDatabase.insert(MovieEntry.TABLE_NAME_RECENT, null, contentValues);
            Log.v(LOG_TAG, id + " Info inserted into " + MovieEntry.TABLE_NAME_RECENT);
        } else if (param.equals(POPULAR)) {
            sqLiteDatabase.insert(MovieEntry.TABLE_NAME_POPULAR, null, contentValues);
            Log.v(LOG_TAG, id + " Info inserted into " + MovieEntry.TABLE_NAME_POPULAR);
        } else {
            Log.e(LOG_TAG, "Parameter \"" + param + "\" is not valid. Data insertion failed.");
        }


    }

    public Cursor getInformation(MovieDbHelper db, String param, String id) {
        SQLiteDatabase sqLiteDatabase = db.getReadableDatabase();
        String[] columns = {MovieEntry.ID, MovieEntry.MOVIE_NAME, MovieEntry.VOTE_AVERAGE,
                MovieEntry.RELEASE_DATE, MovieEntry.POSTER_URL, MovieEntry.BACKDROP_URL,
                MovieEntry.DESCRIPTION};
        String table;

        if (param.equals(RECENT)) {
            table = MovieEntry.TABLE_NAME_RECENT;
        } else if (param.equals(POPULAR)) {
            table = MovieEntry.TABLE_NAME_POPULAR;
        } else {
            Log.e(LOG_TAG, "Table with parameter \"" + param + "\" does not exist.");
            return null;
        }

        if (id != null) {
            id = MovieEntry.ID + " = " + id;
        }

        Cursor cursor = sqLiteDatabase.query(table, // Which table to read?
                columns,  // columns - Which columns in specified table
                id,     // selection - aka WHERE. Which rows to return. Null returns all rows
                null,     // selectionArgs
                null,     // groupBy - aka GROUP BY.
                null,     // having
                null);    // orderBY - aka ORDER BY.

        return cursor;
    }

}

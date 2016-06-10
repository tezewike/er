package com.tezewike.er.movie.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tezewike.er.movie.data.MovieContract.MovieEntry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MovieDbHelper extends SQLiteOpenHelper {

    private final String LOG_TAG = MovieDbHelper.this.getDatabaseName();
    private final String POPULAR = "popular";
    private final String RECENT  = "recent";
    private final String CREATION_DATE = "created_at";

    public static final int database_version = 1;

    public MovieDbHelper(Context context) {
        super(context, MovieContract.DATABASE_NAME, null, database_version);
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
                        MovieEntry.DESCRIPTION  + " TEXT NOT NULL, " +
                        MovieEntry.ADULT_RATED  + " BOOLEAN, " +
                        MovieEntry.GENRE        + " TEXT, " +
                        MovieEntry.STATUS       + " TEXT, " +
                        MovieEntry.RUNTIME      + " TEXT, " +
                        MovieEntry.TAGLINE      + " TEXT, " +
                        CREATION_DATE + " TEXT NOT NULL " +
                        " );";

        final String SQL_CREATE_POPULAR_MOVIE_TABLE =
                "CREATE TABLE " + MovieEntry.TABLE_NAME_POPULAR + " (" +
                        MovieEntry.ID           + " INTEGER NOT NULL, " +
                        MovieEntry.MOVIE_NAME   + " TEXT NOT NULL, " +
                        MovieEntry.VOTE_AVERAGE + " TEXT NOT NULL, " +
                        MovieEntry.RELEASE_DATE + " TEXT NOT NULL, " +
                        MovieEntry.POSTER_URL   + " TEXT NOT NULL, " +
                        MovieEntry.BACKDROP_URL + " TEXT NOT NULL, " +
                        MovieEntry.DESCRIPTION  + " TEXT NOT NULL, " +
                        MovieEntry.ADULT_RATED  + " BOOLEAN, " +
                        MovieEntry.GENRE        + " TEXT, " +
                        MovieEntry.STATUS       + " TEXT, " +
                        MovieEntry.RUNTIME      + " TEXT, " +
                        MovieEntry.TAGLINE      + " TEXT, " +
                                  CREATION_DATE + " TEXT NOT NULL " +
                        " );";

        db.execSQL(SQL_CREATE_RECENT_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_POPULAR_MOVIE_TABLE);
        Log.v(LOG_TAG, "Tables Created.");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME_RECENT);
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME_POPULAR);
        Log.v(LOG_TAG, "onUpgrade: Tables dropped. Recreating Tables...");
        onCreate(db);
    }

    public void deleteTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME_RECENT);
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME_POPULAR);
        Log.v(LOG_TAG, "deleteTables: Tables dropped.");
        onCreate(db);
    }

    public void putInformation(String param, int id, String name,
                               String vote, String release, String poster, String backdrop,
                               String description) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        long insertId;
        String table;
        if (param.equals(RECENT)) {
            table = MovieEntry.TABLE_NAME_RECENT;
        } else if (param.equals(POPULAR)) {
            table = MovieEntry.TABLE_NAME_POPULAR;
        } else {
            Log.e(LOG_TAG, "Table with parameter \"" + param + "\" does not exist.");
            return;
        }

        contentValues.put(MovieEntry.ID, id);
        contentValues.put(MovieEntry.MOVIE_NAME, name);
        contentValues.put(MovieEntry.VOTE_AVERAGE, vote);
        contentValues.put(MovieEntry.RELEASE_DATE, release);
        contentValues.put(MovieEntry.POSTER_URL, poster);
        contentValues.put(MovieEntry.BACKDROP_URL, backdrop);
        contentValues.put(MovieEntry.DESCRIPTION, description);
        contentValues.put(CREATION_DATE, getCurrentDate());

        insertId = sqLiteDatabase.insert(table, null, contentValues);

    }

    public void putInformation(String param, int id, boolean adult, String genre, String status, String runtime,
                               String tagline) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        long updateId;
        String table;

        if (param.equals(RECENT)) {
            table = MovieEntry.TABLE_NAME_RECENT;
        } else if (param.equals(POPULAR)) {
            table = MovieEntry.TABLE_NAME_POPULAR;
        } else {
            Log.e(LOG_TAG, "Table with parameter \"" + param + "\" does not exist.");
            return;
        }

        contentValues.put(MovieEntry.ADULT_RATED, adult);
        contentValues.put(MovieEntry.GENRE, genre);
        contentValues.put(MovieEntry.STATUS, status);
        contentValues.put(MovieEntry.RUNTIME, runtime);
        contentValues.put(MovieEntry.TAGLINE, tagline);

        updateId = sqLiteDatabase.update(table, contentValues,
                MovieEntry.ID + "=" + id, null);
    }

    public Cursor getInformation(String param) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
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

        Cursor cursor = sqLiteDatabase.query(table, // Which table to read?
                columns,  // columns - Which columns in specified table
                null,       // selection - aka WHERE. Which rows to return. Null returns all rows
                null,     // selectionArgs
                null,     // groupBy - aka GROUP BY.
                null,     // having
                null);    // orderBY - aka ORDER BY.

        return cursor;
    }

    public Cursor getInformation(String param, int id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String[] columns = {MovieEntry.ID, MovieEntry.MOVIE_NAME, MovieEntry.VOTE_AVERAGE,
                MovieEntry.RELEASE_DATE, MovieEntry.POSTER_URL, MovieEntry.BACKDROP_URL,
                MovieEntry.DESCRIPTION, MovieEntry.ADULT_RATED, MovieEntry.GENRE, MovieEntry.STATUS,
                MovieEntry.RUNTIME, MovieEntry.TAGLINE};
        String table;

        if (param.equals(RECENT)) {
            table = MovieEntry.TABLE_NAME_RECENT;
        } else if (param.equals(POPULAR)) {
            table = MovieEntry.TABLE_NAME_POPULAR;
        } else {
            Log.e(LOG_TAG, "Table with parameter \"" + param + "\" does not exist.");
            return null;
        }

        Cursor cursor = sqLiteDatabase.query(table, // Which table to read?
                columns,                    // columns - Which columns in specified table
                MovieEntry.ID + " = " + id, // selection - aka WHERE. Which rows to return. Null returns all rows
                null,                       // selectionArgs
                null,                       // groupBy - aka GROUP BY.
                null,                       // having
                null);                      // orderBY - aka ORDER BY.

        return cursor;
    }

    public boolean compareDateInformation(String param) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String[] columns = {CREATION_DATE};
        String table;

        if (param.equals(RECENT)) {
            table = MovieEntry.TABLE_NAME_RECENT;
        } else if (param.equals(POPULAR)) {
            table = MovieEntry.TABLE_NAME_POPULAR;
        } else {
            return false;
        }

        try {
            Cursor cursor = sqLiteDatabase.query(true, table, columns, null,
                    null, null, null, null, "1");
            cursor.moveToFirst();
            String oldDate = cursor.getString(cursor.getColumnIndex(CREATION_DATE));
            String currentDate = this.getCurrentDate();
            cursor.close();
            if (!oldDate.equals(currentDate)) {
                this.deleteTables();
            }
            return oldDate.equals(currentDate);
        } catch (CursorIndexOutOfBoundsException e) {
            Log.v(LOG_TAG, "No data in table exists.");
        }

        return false;
    }

    public boolean isDetailAvailable(String param, int id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String[] columns = {MovieEntry.GENRE};
        String table;

        if (param.equals(RECENT)) {
            table = MovieEntry.TABLE_NAME_RECENT;
        } else if (param.equals(POPULAR)) {
            table = MovieEntry.TABLE_NAME_POPULAR;
        } else {
            return false;
        }

        // Only one column is checked for an entry
        Cursor cursor = sqLiteDatabase.query(true, table, columns, null,
                null, null, null, null, "1");

        try {
            cursor.getString(cursor.getColumnIndex(MovieEntry.GENRE));
            cursor.close();
            return true;
        } catch (CursorIndexOutOfBoundsException e) {
            return false;
        }

    }

    public String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }

}

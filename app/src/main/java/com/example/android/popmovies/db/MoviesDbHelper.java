package com.example.android.popmovies.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by arifinbardansyah on 7/28/17.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movies.db";

    public static final int DATABASE_VERSION = 1;

    public MoviesDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " +
                FavoritesContract.FavoritesEntry.FAVORITES + " (" +
                FavoritesContract.FavoritesEntry._ID + " INTEGER NOT NULL," +
                FavoritesContract.FavoritesEntry.VOTE_COUNT + " INTEGER," +
                FavoritesContract.FavoritesEntry.VIDEO + " INTEGER," +
                FavoritesContract.FavoritesEntry.VOTE_AVERAGE + " DOUBLE," +
                FavoritesContract.FavoritesEntry.TITLE + " TEXT," +
                FavoritesContract.FavoritesEntry.POPULARITY + " DOUBLE," +
                FavoritesContract.FavoritesEntry.POSTER_PATH + " TEXT," +
                FavoritesContract.FavoritesEntry.ORIGINAL_LANGUAGE + " TEXT," +
                FavoritesContract.FavoritesEntry.ORIGINAL_TITLE + " TEXT," +
                FavoritesContract.FavoritesEntry.BACKDROP_PATH + " TEXT," +
                FavoritesContract.FavoritesEntry.ADULT + " INTEGER," +
                FavoritesContract.FavoritesEntry.OVERVIEW + " TEXT," +
                FavoritesContract.FavoritesEntry.RELEASE_DATE + " TEXT" +
//                FavoritesContract.FavoritesEntry.GENRE_IDS + " TEXT" +
                ");";
        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS"+ FavoritesContract.FavoritesEntry.FAVORITES);
        onCreate(db);
    }
}

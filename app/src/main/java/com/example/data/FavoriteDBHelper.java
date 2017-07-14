package com.example.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Tayyab on 7/1/2017.
 */

class FavoriteDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favoritelist.db";
    private static final int DATABASE_VERSION = 1;

    public FavoriteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_FAVORITE_CREATE_TABLE = "Create Table "+
                FavoriteContract.FavoriteEntry.TABLE_NAME + "("+
                FavoriteContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoriteContract.FavoriteEntry.MOVIE_ID + " INTEGER NOT NULL,"+
                FavoriteContract.FavoriteEntry.MOVIE_POSTER_PATH + " TEXT NOT NULL,"+
                FavoriteContract.FavoriteEntry.MOVIE_TITLE + " TEXT NOT NULL,"+
                FavoriteContract.FavoriteEntry.MOVIE_OVERVIEW + " TEXT NOT NULL,"+
                FavoriteContract.FavoriteEntry.MOVIE_VOTE_AVERAGE + " TEXT NOT NULL,"+
                FavoriteContract.FavoriteEntry.MOVIE_RELEASE_DATE + " TEXT NOT NULL"+
                ");";
        db.execSQL(SQL_FAVORITE_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ FavoriteContract.FavoriteEntry.TABLE_NAME);
        onCreate(db);
    }
}

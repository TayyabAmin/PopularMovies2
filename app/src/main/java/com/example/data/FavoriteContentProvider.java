package com.example.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Tayyab on 7/7/2017.
 */

public class FavoriteContentProvider extends ContentProvider {

    private static final int FAVORITES = 100;
    private static final int FAVORITE_WITH_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavoriteContract.AUTHORITY, FavoriteContract.PATH_FAVORITE, FAVORITES);
        uriMatcher.addURI(FavoriteContract.AUTHORITY, FavoriteContract.PATH_FAVORITE, FAVORITE_WITH_ID);
        return uriMatcher;
    }

    private FavoriteDBHelper mFavoriteDBHelper;
    @Override
    public boolean onCreate() {

        Context context = getContext();
        mFavoriteDBHelper = new FavoriteDBHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mFavoriteDBHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match) {
            case FAVORITE_WITH_ID:
                retCursor = db.query(FavoriteContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVORITES:
                retCursor = db.query(FavoriteContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: "+uri);
        }
        if (getContext().getContentResolver() == null) {
            return null;
        }else {
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
            return retCursor;
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mFavoriteDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case FAVORITE_WITH_ID:
                long id = db.insert(FavoriteContract.FavoriteEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavoriteContract.FavoriteEntry.CONTENT_URI, id);
                }else {
                    throw new android.database.SQLException("Failed to insert row into: "+uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: "+uri);
        }
        if (getContext().getContentResolver() == null) {
            return null;
        }else {
            getContext().getContentResolver().notifyChange(uri, null);
            return returnUri;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        /*final SQLiteDatabase db = mFavoriteDBHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int favoriteDeleted;
        switch (match) {
            case FAVORITE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                favoriteDeleted = db.delete(FavoriteContract.FavoriteEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        if (favoriteDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return favoriteDeleted;*/
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}

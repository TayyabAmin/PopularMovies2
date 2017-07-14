package com.example.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Tayyab on 7/1/2017.
 */

public class FavoriteContract {

    public static final String AUTHORITY = "com.example.popularmoviesstage2";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVORITE = "favoritelist";

    public static final class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        public static final String TABLE_NAME = "favoritelist";
        public static final String MOVIE_ID = "movie_id";
        public static final String MOVIE_POSTER_PATH = "movie_poster_path";
        public static final String MOVIE_TITLE = "movie_title";
        public static final String MOVIE_OVERVIEW = "movie_overview";
        public static final String MOVIE_VOTE_AVERAGE = "movie_vote_average";
        public static final String MOVIE_RELEASE_DATE = "movie_release_date";
    }
}

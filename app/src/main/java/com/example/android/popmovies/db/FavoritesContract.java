package com.example.android.popmovies.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by arifinbardansyah on 7/28/17.
 */

public class FavoritesContract {

    public static final String AUTHORITY = "com.example.android.popmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVORITES = "favorites";

    public static class FavoritesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String FAVORITES = "favorites";
        public static final String VOTE_COUNT = "vote_count";
        public static final String VIDEO = "video";
        public static final String VOTE_AVERAGE = "vote_average";
        public static final String TITLE = "title";
        public static final String POPULARITY = "popularity";
        public static final String POSTER_PATH = "poster_path";
        public static final String ORIGINAL_LANGUAGE = "original_language";
        public static final String ORIGINAL_TITLE = "original_title";
        public static final String BACKDROP_PATH = "backdrop_path";
        public static final String ADULT = "adult";
        public static final String OVERVIEW = "overview";
        public static final String RELEASE_DATE = "release_date";
//        public static final String GENRE_IDS = "genre_ids";
    }
}

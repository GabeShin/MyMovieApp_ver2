package com.example.android.movieratingapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Gabe on 2016-11-03.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.movieratingapp";
    public static final Uri BASE_AUTHORITY = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movies";

    public static final class MovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_AUTHORITY.buildUpon().appendPath(PATH_MOVIE).build();

        // CURSOR_DIR_BASE_TYPE/com.example.android.movieratingapp/movies
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        // CURSOR_ITEM_BASE_TYPE/com.example.android.movieratingapp/movies
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        /*
        Table name
        */
        public static final String TABLE_NAME = "movies";

        // Columns for Poster Activity
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_RATING = "rating";

        // Columns for Movie Details
        public static final String COLUMN_TAGLINE = "tagline";
        public static final String COLUMN_GENRE = "genre";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_BACKGROUND_PATH = "background_path";

        // Columns for Movie Casts
        public static final String COLUMN_CAST_IMAGE = "cast_images";
        public static final String COLUMN_CAST_NAME = "cast_name";
        public static final String COLUMN_CAST_ROLE = "cast_role";

        // Columns for Movie Images
        public static final String COLUMN_MOVIE_GALLARY = "movie_gallery";

        // Columns for Movie Videos
        public static final String COLUMN_MOVIE_TRAILER = "movie_trailer";

        // Columns for Related Movies
        public static final String COLUMN_RELATED_MOVIE_TITLES = "related_movie_titles";
        public static final String COLUMN_RELATED_MOVIE_IMAGES = "related_movie_images";
        public static final String COLUMN_RELATED_MOVIE_ID = "related_movie_id";
        public static final String COLUMN_RELATED_MOVIE_RATING = "related_movie_rating";
    }
}

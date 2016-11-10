package com.example.android.movieratingapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.android.movieratingapp.data.MovieContract.MovieEntry;

/**
 * Created by Gabe on 2016-11-03.
 */
public class MovieProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private MovieDbHelper mOpenHelper;


    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static final int MOVIE = 100;
    static final int MOVIE_ID = 101;

    static {
        // "content://com.example.android.movieratingapp/movie"
        // This URI is used to provide access to MULTIPLE rows of the table
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE, MOVIE);

        // "content://com.example.android.movieratingapp/movie/#"
        // This URI is used to provide access to ONE single row of the table.
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/#", MOVIE_ID);

        final String authority = MovieContract.CONTENT_AUTHORITY;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                cursor = db.query(MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MOVIE_ID:
                selection = MovieEntry.COLUMN_MOVIE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
        // notify change in Uri
        getContext().getContentResolver().notifyChange(uri, null);
        return cursor;
    }

    @Override
    public String getType (Uri uri){
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                return MovieEntry.CONTENT_TYPE;
            case MOVIE_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues cv) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case MOVIE:
                long id = db.insertWithOnConflict(MovieEntry.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
                returnUri = ContentUris.withAppendedId(uri, id);
                break;
            default:
                throw new IllegalArgumentException("Insertion is not supported for the URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        // Exclude one's already existing in the data
                        long _id = db.insertWithOnConflict(MovieEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_IGNORE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int deletedRows = 0;
        int match = sUriMatcher.match(uri);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (match){
            case MOVIE:
                deletedRows = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_ID:
                selection = MovieEntry.COLUMN_MOVIE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                deletedRows = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Delete is not supported for the URI: " + uri);
        }

        if (deletedRows != 0){
            // notify change in Uri
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deletedRows;
    }

    @Override
    public int update(Uri uri, ContentValues cv, String selection, String[] selectionArgs) {
        if (cv.size() == 0){
            return 0;
        }
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        return db.update(MovieEntry.TABLE_NAME, cv, selection, selectionArgs);
    }

}

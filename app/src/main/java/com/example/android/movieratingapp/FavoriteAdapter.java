package com.example.android.movieratingapp;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movieratingapp.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by Gabe on 2016-11-07.
 */
public class FavoriteAdapter extends CursorAdapter {

    public FavoriteAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.poster_item, viewGroup, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor data) {
        int id = data.getInt(data.getColumnIndexOrThrow(MovieContract.MovieEntry._ID));

         // format title
        TextView tvTitle = (TextView) view.findViewById(R.id.poster_title);
        String title = data.getString(data.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_TITLE));
        tvTitle.setText(title);

        // format rating
        TextView tvRating = (TextView) view.findViewById(R.id.poster_rating);
        double currentRating = data.getDouble(data.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_RATING));
        tvRating.setText(String.valueOf(currentRating));

        // format poster images using Picasso extension
        // compile 'com.squareup.picasso:picasso:2.5.2'
        ImageView imagePoster = (ImageView) view.findViewById(R.id.poster_image);
        String path = data.getString(data.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
        Picasso.with(context).load(path).into(imagePoster);
    }
}

package com.example.android.movieratingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movieratingapp.custom_class.Poster;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Gabe on 2016-10-24.
 */
public class PosterAdapter extends ArrayAdapter<Poster> {

    private static final String LOG_TAG = PosterAdapter.class.getSimpleName();

    public PosterAdapter(Context context, ArrayList<Poster> resource) {
        super(context, 0, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridItemView = convertView;

        if (gridItemView == null){
            gridItemView = LayoutInflater.from(getContext()).inflate(R.layout.poster_item, parent, false);
        }

        Poster current = getItem(position);

        // format title
        TextView title = (TextView) gridItemView.findViewById(R.id.poster_title);
        String currentTitle = current.getTitle();
        title.setText(currentTitle);

        // format rating
        TextView rating = (TextView) gridItemView.findViewById(R.id.poster_rating);
        double currentRating = current.getRating();
        rating.setText(String.valueOf(currentRating));

        // format poster images using Picasso extension
        // compile 'com.squareup.picasso:picasso:2.5.2'
        ImageView poster = (ImageView) gridItemView.findViewById(R.id.poster_image);
        String path = "http://image.tmdb.org/t/p/w185";
        String thumbnailPath = current.getThumbnail();
        path += thumbnailPath;
        Picasso.with(getContext()).load(path).into(poster);

        return gridItemView;
    }
}

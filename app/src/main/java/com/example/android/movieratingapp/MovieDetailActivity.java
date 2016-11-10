package com.example.android.movieratingapp;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.android.movieratingapp.data.MovieContract;

/**
 * Created by Gabe on 2016-10-24.
 */
public class MovieDetailActivity extends AppCompatActivity implements MovieDetailFragment.Callback{

    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (savedInstanceState == null){
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Bundle arguments = new Bundle();
            arguments.putInt(MovieDetailFragment.MOVIE_ID, getIntent().getExtras().getInt(MovieDetailFragment.MOVIE_ID));

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction().add(R.id.frame_movie_overview, fragment).commit();
        }
    }

    @Override
    public void onItemSelected(int movieId) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MovieDetailFragment.MOVIE_ID, movieId);
        startActivity(intent);
    }
}

package com.example.android.movieratingapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class PosterActivity extends AppCompatActivity
        implements FavoriteFragment.Callback, PosterFragment.Callback, MovieDetailFragment.Callback{

    public static final String LOG_TAG = PosterActivity.class.getSimpleName();

    private boolean mTwoPane;

    private static final String DETAILFRAGMENT_TAG = "DFTAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null) {
            // if layout-sw600dp is used, there will be movie_detail_container
            mTwoPane = true;

            // in Two Pane mode, show the detail view
            if (savedInstanceState == null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.movie_lists_container, new PosterFragment());
                transaction.commit();
            }
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.movie_lists_container, new PosterFragment()).commit();
        }
    }

    @Override
    public void onItemSelected(int movieId) {
        if (mTwoPane){
            // if layout is two-pane mode, show the detail view in this activity,
            // without starting MovieDetailActivity
            Bundle args = new Bundle();
            args.putInt(MovieDetailFragment.MOVIE_ID, movieId);

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(MovieDetailFragment.MOVIE_ID, movieId);
            startActivity(intent);
        }
    }

    @Override
    public void onReplaceFragment(int action) {
        switch (action){
            case R.id.action_favorite:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_lists_container, new FavoriteFragment())
                        .commit();
                break;
            case R.id.action_list:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_lists_container, new PosterFragment())
                        .commit();
        }
    }

    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }
}

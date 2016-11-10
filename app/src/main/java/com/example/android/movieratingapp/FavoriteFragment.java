package com.example.android.movieratingapp;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.android.movieratingapp.data.MovieContract;

public class FavoriteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    FavoriteAdapter mAdapter;
    TextView mPosterTitle;
    private static final int FAVORITE_LOADER = 100;

    public interface Callback{
        //when an item has been selected
        void onItemSelected(int movieId);
        void onReplaceFragment(int action);
        void setActionBarTitle(String title);
    }

    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        mPosterTitle = (TextView) view.findViewById(R.id.poster_title);

        ((Callback) getActivity()).setActionBarTitle("Your Favorites");

        // finding grid view to populate
        GridView gridView = (GridView) view.findViewById(R.id.favorite_list);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                if (cursor != null){
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
                    // Create a intent to go to MovieDetailActivity
                    ((Callback) getActivity()).onItemSelected(id);
                }
            }
        });

        // set up the adapter to the grid view
        mAdapter = new FavoriteAdapter(getActivity(), null);
        gridView.setAdapter(mAdapter);        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FAVORITE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                MovieContract.MovieEntry._ID,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                MovieContract.MovieEntry.COLUMN_TITLE,
                MovieContract.MovieEntry.COLUMN_RATING,
                MovieContract.MovieEntry.COLUMN_POSTER_PATH
        };

        CursorLoader cursorLoader = new CursorLoader(getActivity(), MovieContract.MovieEntry.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.favorite_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_list:
                ((Callback) getActivity()).onReplaceFragment(R.id.action_list);
        }
        return true;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}

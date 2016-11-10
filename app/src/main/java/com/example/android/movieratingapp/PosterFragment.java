package com.example.android.movieratingapp;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.example.android.movieratingapp.custom_class.Poster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabe on 2016-11-06.
 */
public class PosterFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Poster>>{

    public static final String LOG_TAG = PosterFragment.class.getSimpleName();

    /*
    DISCOVER REQUEST is used for:
    sort by average rating, genre... etc
     */
    private static final String POSTER_DISCOVER_REQUEST_URL = "https://api.themoviedb.org/3/discover/movie?";
    /*
    SEARCH REQUEST is used for:
    searching for movie titles
     */
    private static final String POSTER_SEARCH_REQUEST_URL = "https://api.themoviedb.org/3/search/movie?";

    /*
    DECLARE VARIABLES:
    Constant Value for loader ID
    Adapter for Arraylist<Poster>
     */
    private static final int POSTER_LOADER_ID = 100;
    private static final int SEARCH_LOADER_ID = 200;
    private PosterAdapter mAdapter;
    private String mQuery = "";
    private String mSortBy = "";
    /*
    DECLARE VIEWS THAT NEED ON CLICK LISTENERS
    & Views to extract
     */
    private LinearLayout advancedSearchLayout;
    private ImageView expandButton;
    private ImageView collapseButton;
    private ImageView searchButton;
    private EditText searchKeyWord;
    private RadioButton sortByPopularity;
    private RadioButton sortByNewest;
    private RadioButton sortByRating;

    public interface Callback{
        void onItemSelected(int movieId);
        void onReplaceFragment(int action);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_poster, container, false);

        // set on Click Listener on Search Button
        searchButton = (ImageView) view.findViewById(R.id.poster_search_button);
        searchKeyWord = (EditText) view.findViewById(R.id.poster_search_keyword);
        sortByPopularity = (RadioButton) view.findViewById(R.id.poster_sort_by_popularity);
        sortByNewest = (RadioButton) view.findViewById(R.id.poster_sort_by_newest);
        sortByRating = (RadioButton) view.findViewById(R.id.poster_sort_by_rating);

        // finding grid view to populate
        GridView gridView = (GridView) view.findViewById(R.id.grid_view);

        // set up the adapter to the grid view
        mAdapter = new PosterAdapter(getActivity(), new ArrayList<Poster>());
        gridView.setAdapter(mAdapter);

        // start off loader IF there is internet connection
        // otherwise, set empty view
        if (isNetworkAvailable(getActivity())) {
            // if there is already loader initated,
            // - for example, onCreate could be re-triggered by orientation change -
            // don't init the loader.
            LoaderManager loaderManager = getLoaderManager();
            if (loaderManager.getLoader(POSTER_LOADER_ID) == null) {
                loaderManager.initLoader(POSTER_LOADER_ID, null, this);
            }
        }

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchKeyWord.getText().toString().trim().isEmpty()){
                    // if mQuery is null and search button is clicked,
                    // user is not looking for specific movie.
                    // Call for POSTER loader.
                    if (sortByPopularity.isChecked()){
                        mSortBy = "popularity.desc";
                    } else if (sortByNewest.isChecked()){
                        mSortBy = "released_date.desc";
                    } else if (sortByRating.isChecked()){
                        mSortBy = "vote_average.desc";
                    }

                    LoaderManager loaderManager = getLoaderManager();
                    loaderManager.restartLoader(POSTER_LOADER_ID, null, PosterFragment.this);
                } else if (mQuery != null){
                    // if mQuery is null and search button is clicked,
                    // user is looking for specific movie.
                    // Call for SEARCH loader.
                    mQuery = searchKeyWord.getText().toString().trim();
                    if (sortByPopularity.isChecked()){
                        mSortBy = "popularity.desc";
                    } else if (sortByNewest.isChecked()){
                        mSortBy = "released_date.desc";
                    } else if (sortByRating.isChecked()){
                        mSortBy = "vote_average.desc";
                    }

                    LoaderManager loaderManager = getLoaderManager();
                    loaderManager.restartLoader(SEARCH_LOADER_ID, null, PosterFragment.this);
                }
            }
        });

        // set on Item Clicker for each items in gridview.
        // get Movie ID and pass to the new activity with intent
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // find the view that was clicked
                Poster current = mAdapter.getItem(i);
                // get Movie ID
                int id = current.getMovieId();

                // Create a intent to go to MovieDetailActivity
                ((Callback) getActivity()).onItemSelected(id);
            }
        });

        // set expand and collapse method on advanced search function.
        advancedSearchLayout = (LinearLayout) view.findViewById(R.id.advanced_search_view);
        expandButton = (ImageView) view.findViewById(R.id.expand_searchview_button);
        collapseButton = (ImageView) view.findViewById(R.id.collapse_searchview_button);

        // set expand and collapse method on advanced search function.
        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expand(advancedSearchLayout);
                expandButton.setVisibility(View.GONE);
                collapseButton.setVisibility(View.VISIBLE);
            }
        });
        collapseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapse(advancedSearchLayout);
                expandButton.setVisibility(View.VISIBLE);
                collapseButton.setVisibility(View.GONE);
            }
        });

        return view;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.poster_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_favorite:
                ((Callback) getActivity()).onReplaceFragment(R.id.action_favorite);
        }
        return true;
    }

    @Override
    public Loader<List<Poster>> onCreateLoader(int id, Bundle args) {
        Uri baseUri;

        if (id == POSTER_LOADER_ID) {
            // use Uri Builder to build POSTER_DISCOVER_REQUEST_URL
            baseUri = Uri.parse(POSTER_DISCOVER_REQUEST_URL);

            Uri.Builder uriBuilder = baseUri.buildUpon();
            uriBuilder.appendQueryParameter("sort_by", mSortBy.toString());
            uriBuilder.appendQueryParameter("api_key", "a72d31708653727455966e47468c9bf0");

            Log.v(LOG_TAG, "Request URL is " + uriBuilder.toString());

            PosterLoader loader = new PosterLoader(getActivity(), uriBuilder.toString());

            return loader;
        } else {
            // use Uri Builder to build POSTER_SEARCH_REQUEST_URL
            baseUri = Uri.parse(POSTER_SEARCH_REQUEST_URL);

            Uri.Builder uriBuilder = baseUri.buildUpon();
            uriBuilder.appendQueryParameter("query", mQuery.toString());
            uriBuilder.appendQueryParameter("sort_by", mSortBy.toString());
            uriBuilder.appendQueryParameter("api_key", "a72d31708653727455966e47468c9bf0");

            Log.v(LOG_TAG, "Request URL is " + uriBuilder.toString());

            PosterLoader loader = new PosterLoader(getActivity(), uriBuilder.toString());

            return loader;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<Poster>> loader, List<Poster> data) {
        mAdapter.clear();
        if (data == null || data.isEmpty()) {
            Log.v(LOG_TAG, "make an empty view?");
        } else {
            mAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Poster>> loader) {

    }

    /*
        Check if Network is Available.
        Internet permission is required.
         */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /*
    Expand and Collapse Search Bar
     */
    public static void expand(final View v){
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1 ? ViewGroup.LayoutParams.WRAP_CONTENT:
                        (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // speed is 1 dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v){
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        // speed 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }
}

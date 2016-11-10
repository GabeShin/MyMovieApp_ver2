package com.example.android.movieratingapp;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.android.movieratingapp.custom_class.Poster;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by Gabe on 2016-10-24.
 */
public class PosterLoader extends AsyncTaskLoader<List<Poster>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = PosterLoader.class.getName();

    /**
     * Query URL
     */
    private String mUrl;

    public PosterLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Poster> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list.
        List<Poster> posters = fetchPosterData(mUrl);
        return posters;
    }

    public static List<Poster> fetchPosterData(String requestUrl) {

        // Create URL object
        URL url = Utilities.createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = Utilities.makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<Poster> posters = Utilities.extractPosterFeatureFromJson(jsonResponse);

        // Return the {@link Event}
        return posters;
    }
}

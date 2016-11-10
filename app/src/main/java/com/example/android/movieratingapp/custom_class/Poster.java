package com.example.android.movieratingapp.custom_class;

import java.net.URL;

/**
 * Created by Gabe on 2016-10-24.
 */
public class Poster {

    String mTitle;
    String mThumbnail;
    double mRating;
    int mMovieId;

    public Poster(String title, String thumbnail, double rating, int ID){
        mTitle = title;
        mThumbnail = thumbnail;
        mRating = rating;
        mMovieId = ID;
    }

    public String getTitle() {
        return mTitle;
    }

    public double getRating() {
        return mRating;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public int getMovieId() {
        return mMovieId;
    }
}

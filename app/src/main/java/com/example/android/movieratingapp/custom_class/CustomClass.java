package com.example.android.movieratingapp.custom_class;

import java.util.ArrayList;

/**
 * Created by Gabe on 2016-11-07.
 */
public class CustomClass {


    // This constructor is used for Movie Cast
    private String mName;
    private String mImagePath;
    private String mRole;

    public CustomClass(String name, String imagePath, String role){
        mName = name;
        mImagePath = imagePath;
        mRole = role;
    }
    public String getCastName() {
        return mName;
    }
    public String getImagePath() {
        return mImagePath;
    }
    public String getRole() {
        return mRole;
    }


    // This constructor is used for Videos
    private String mVideoName;
    private String mKey;

    public CustomClass(String videoName, String key){
        mVideoName = videoName;
        mKey = key;
    }
    public String getPrimaryString() {
        return mVideoName;
    }
    public String getSecondaryString() {
        return mKey;
    }


    // This constructor is used for Movie Detail
    String mTitle;
    String mTagline;
    String mPosterUrl;
    ArrayList<String> mGenre;
    String mReleasedDate;
    Double mRating;
    String mSynopsis;
    String mBackdropPath;

    public CustomClass(String title, String tagline, String posterUrl, ArrayList<String> genre,
                        String releasedDate, Double rating, String synopsis, String backdropUrl){
        mTitle = title;
        mTagline = tagline;
        mPosterUrl = posterUrl;
        mGenre = genre;
        mReleasedDate = releasedDate;
        mRating = rating;
        mSynopsis = synopsis;
        mBackdropPath = backdropUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getTagline() {
        return mTagline;
    }

    public String getPosterUrl() {
        return mPosterUrl;
    }

    public ArrayList<String> getGenre() {
        return mGenre;
    }

    public String getReleasedDate() {
        return mReleasedDate;
    }

    public Double getRating() {
        return mRating;
    }

    public String getSynopsis() {
        return mSynopsis;
    }

    public String getBackdropPath() {
        return mBackdropPath;
    }


    // This constructor is used for Image Gallery
    private String mGalleryPath;

    public CustomClass(String path){
        mGalleryPath = path;
    }

    public String getGalleryPath() {
        return mGalleryPath;
    }

    // This constructor is used for Similar Movies
    private String mMovieTitle;
    private int mMovieId;

    public CustomClass(String movieTitle, int id){
        mMovieTitle = movieTitle;
        mMovieId = id;
    }

    public int getMovieId() {
        return mMovieId;
    }

    public String getMoviePosterPath() {
        return mMovieTitle;
    }
}

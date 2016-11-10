package com.example.android.movieratingapp;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movieratingapp.custom_class.CustomClass;
import com.example.android.movieratingapp.data.MovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<CustomClass>> {

    public interface Callback{
        void onItemSelected(int movieId);
    }

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    /*
    MOVIE REQUEST is used for:
    getting detailed information about a movie. Used with movie Id.
    */
    private static final String MOVIE_REQUEST_URL = "https://api.themoviedb.org/3/movie/";

    static final String MOVIE_ID = "URI";

    /*
    DECLARE VARIABLES:
    Constant Value for loader ID
    Passed Movie Id from Intent
     */
    private static final int MOVIE_LOADER_ID = 200;
    private static final int CAST_LOADER_ID = 300;
    private static final int GALLERY_LOADER_ID = 400;
    private static final int SIMILAR_LOADER_ID = 500;
    private static final int VIDEO_LOADER_ID = 600;
    private static final int REVIEW_LOADER_ID = 700;

    private static int mMovieId;
    static Uri contentUri;
    /*
    DECLARE VIEWS:
     */
    TextView mTitle;
    TextView mTagline;
    TextView mGenres;
    TextView mReleasedDate;
    TextView mRating;
    TextView mSynopsis;
    ImageView mPosterImage;
    ImageView mFavorite;
    ImageView mBackground;


    RecyclerView mCastHorizontalView;
    CastAdapter mCastAdapter;

    RecyclerView mGalleryHorizontalView;
    GalleryAdapter mGalleryAdapter;

    RecyclerView mSimilarHorizontalView;
    SimilarAdapter mSimilarAdapter;

    ListView mVideoListView;
    VideoAdapter mVideoAdapter;

    ListView mReviewListView;
    ReviewAdapter mReviewAdapter;

    /*
    VARIABLES FOR ADDING TO DATABASE
     */
    private String mTitleStr;
    private String mRatingStr;
    private String mPosterPath;

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();

        if (arguments != null) {
            mMovieId = arguments.getInt(MOVIE_ID);
            contentUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, mMovieId);
        }


        Log.v(LOG_TAG, "Movie Id is " + mMovieId);

        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        // find views and assign them
        mTitle = (TextView) view.findViewById(R.id.movie_title);
        mTagline = (TextView) view.findViewById(R.id.movie_title_tag);
        mGenres = (TextView) view.findViewById(R.id.movie_genre);
        mReleasedDate = (TextView) view.findViewById(R.id.released_date);
        mRating = (TextView) view.findViewById(R.id.movie_rating);
        mSynopsis = (TextView) view.findViewById(R.id.movie_synopsis);
        mPosterImage = (ImageView) view.findViewById(R.id.movie_poster);
        mFavorite = (ImageView) view.findViewById(R.id.movie_favorite);

        mBackground = (ImageView) getActivity().findViewById(R.id.background_large_image);

        // If pressed Favorite button, add the following movie to the database.
        mFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // the movie is not in the database
                if (!isMovieAlreadyInDatabase()) {
                    // Change the image
                    mFavorite.setImageResource(R.drawable.ic_favorite_white_24dp);
                    // Add to the database
                    ContentValues cv = new ContentValues();
                    cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mMovieId);
                    cv.put(MovieContract.MovieEntry.COLUMN_TITLE, mTitleStr);
                    cv.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, mPosterPath);
                    cv.put(MovieContract.MovieEntry.COLUMN_RATING, mRatingStr);

                    Uri uri = getActivity().getContentResolver()
                            .insert(MovieContract.MovieEntry.CONTENT_URI, cv);
                    Toast.makeText(getActivity(), getString(R.string.added_to_favorite), Toast.LENGTH_SHORT).show();
                }
                // If mFavorite IS clicked, then it is in the database
                else {
                    // Change the Image
                    mFavorite.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                    // Delete from the database
                    getActivity().getContentResolver().delete(contentUri, null, null);
                    Toast.makeText(getActivity(), getString(R.string.remove_from_favorite), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mCastHorizontalView = (RecyclerView) view.findViewById(R.id.cast_horizontal_view);
        LinearLayoutManager castHorizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mCastHorizontalView.setLayoutManager(castHorizontalLayoutManager);

        mGalleryHorizontalView = (RecyclerView) view.findViewById(R.id.gallery_horizontal_view);
        LinearLayoutManager galleryHorizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mGalleryHorizontalView.setLayoutManager(galleryHorizontalLayoutManager);

        mSimilarHorizontalView = (RecyclerView) view.findViewById(R.id.similar_horizontal_view);
        LinearLayoutManager similarHorizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mSimilarHorizontalView.setLayoutManager(similarHorizontalLayoutManager);

        mVideoListView = (ListView) view.findViewById(R.id.video_list_view);
        mVideoAdapter = new VideoAdapter(getActivity(), new ArrayList<CustomClass>());
        mVideoListView.setAdapter(mVideoAdapter);

        mReviewListView = (ListView) view.findViewById(R.id.review_list_view);
        mReviewAdapter = new ReviewAdapter(getActivity(), new ArrayList<CustomClass>());
        mReviewListView.setAdapter(mReviewAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isNetworkAvailable(getActivity())){
            getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
            getLoaderManager().initLoader(CAST_LOADER_ID, null, this);
            getLoaderManager().initLoader(GALLERY_LOADER_ID, null, this);
            getLoaderManager().initLoader(SIMILAR_LOADER_ID, null, this);
            getLoaderManager().initLoader(VIDEO_LOADER_ID, null, this);
            getLoaderManager().initLoader(REVIEW_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<List<CustomClass>> onCreateLoader(int id, Bundle args) {

        String uri;
        Uri baseUri;
        Uri.Builder uriBuilder;

        switch (id){
            case MOVIE_LOADER_ID:
                uri = MOVIE_REQUEST_URL + String.valueOf(mMovieId);

                baseUri = Uri.parse(uri);
                uriBuilder = baseUri.buildUpon();
                uriBuilder.appendQueryParameter("api_key", "a72d31708653727455966e47468c9bf0");

                Log.v(LOG_TAG, "Request URL is " + uriBuilder);
                return new MovieDetailLoader(getActivity(), uriBuilder.toString());

            case CAST_LOADER_ID:
                uri = MOVIE_REQUEST_URL + String.valueOf(mMovieId) + "/casts";

                baseUri = Uri.parse(uri);
                uriBuilder = baseUri.buildUpon();
                uriBuilder.appendQueryParameter("api_key", "a72d31708653727455966e47468c9bf0");

                Log.v(LOG_TAG, "Request URL is " + uriBuilder);
                return new MovieCastLoader(getActivity(), uriBuilder.toString());

            case GALLERY_LOADER_ID:
                uri = MOVIE_REQUEST_URL + String.valueOf(mMovieId) + "/images";

                baseUri = Uri.parse(uri);
                uriBuilder = baseUri.buildUpon();
                uriBuilder.appendQueryParameter("api_key", "a72d31708653727455966e47468c9bf0");

                Log.v(LOG_TAG, "Request URL is " + uriBuilder);
                return new GalleryLoader(getActivity(), uriBuilder.toString());

            case SIMILAR_LOADER_ID:
                uri = MOVIE_REQUEST_URL + String.valueOf(mMovieId) + "/similar";

                baseUri = Uri.parse(uri);
                uriBuilder = baseUri.buildUpon();
                uriBuilder.appendQueryParameter("api_key", "a72d31708653727455966e47468c9bf0");

                Log.v(LOG_TAG, "Request URL is " + uriBuilder);
                return new SimilarLoader(getActivity(), uriBuilder.toString());
            case VIDEO_LOADER_ID:
                uri = MOVIE_REQUEST_URL + String.valueOf(mMovieId) + "/videos";

                baseUri = Uri.parse(uri);
                uriBuilder = baseUri.buildUpon();
                uriBuilder.appendQueryParameter("api_key", "a72d31708653727455966e47468c9bf0");

                Log.v(LOG_TAG, "Request URL is " + uriBuilder);
                return new VideoLoader(getActivity(), uriBuilder.toString());
            case REVIEW_LOADER_ID:
                uri = MOVIE_REQUEST_URL + String.valueOf(mMovieId) + "/reviews";

                baseUri = Uri.parse(uri);
                uriBuilder = baseUri.buildUpon();
                uriBuilder.appendQueryParameter("api_key", "a72d31708653727455966e47468c9bf0");

                Log.v(LOG_TAG, "Request URL is " + uriBuilder);
                return new ReviewLoader(getActivity(), uriBuilder.toString());
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<CustomClass>> loader, List<CustomClass> data) {

        switch (loader.getId()){
            case MOVIE_LOADER_ID:
                CustomClass current = data.get(0);
                // extracted data from data and populate the views
                mTitleStr = current.getTitle();
                mTitle.setText(mTitleStr);
                mTagline.setText(current.getTagline());
                mReleasedDate.setText(current.getReleasedDate());
                mRatingStr = current.getRating().toString();
                mRating.setText((mRatingStr));
                mSynopsis.setText(current.getSynopsis());
                // Poster image
                mPosterPath = "http://image.tmdb.org/t/p/w185";
                String thumbnailPath = current.getPosterUrl();
                mPosterPath += thumbnailPath;
                Picasso.with(getActivity()).load(mPosterPath).into(mPosterImage);
                // Genre
                ArrayList<String> genre = current.getGenre();
                int length = genre.size();
                String genreInContext = genre.get(0);
                for (int i = 1; i < length; i++){
                    genreInContext+= "/ " + genre.get(i);
                }
                mGenres.setText(genreInContext);
                // Background image
                String backgroundPath = current.getBackdropPath();
                String bgPath = "http://image.tmdb.org/t/p/w780" + backgroundPath;
                Picasso.with(getActivity()).load(bgPath).into(mBackground);
                // Favorite Button
                if (isMovieAlreadyInDatabase()){
                    // movie is in the favorite database - change the image
                    mFavorite.setImageResource(R.drawable.ic_favorite_white_24dp);
                }
                break;

            case CAST_LOADER_ID:
                mCastAdapter = new CastAdapter(data);
                mCastHorizontalView.setAdapter(mCastAdapter);
                break;

            case GALLERY_LOADER_ID:
                mGalleryAdapter = new GalleryAdapter(data);
                mGalleryHorizontalView.setAdapter(mGalleryAdapter);
                break;

            case SIMILAR_LOADER_ID:
                mSimilarAdapter = new SimilarAdapter(data);
                mSimilarHorizontalView.setAdapter(mSimilarAdapter);
                break;

            case VIDEO_LOADER_ID:
                mVideoAdapter.clear();
                if (data == null || data.isEmpty()) {
                    Log.v(LOG_TAG, "make an empty view?");
                } else {
                    mVideoAdapter.addAll(data);
                    int num = data.size();
                    float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            51 * num, getResources().getDisplayMetrics());
                    ViewGroup.LayoutParams params = mVideoListView.getLayoutParams();
                    params.height = (int) pixels;
                    mVideoListView.setLayoutParams(params);
                }
                break;

            case REVIEW_LOADER_ID:
                mReviewAdapter.clear();
                if (data == null || data.isEmpty()) {
                    Log.v(LOG_TAG, "make an empty view?");
                } else {
                    mReviewAdapter.addAll(data);
                    int num = data.size();
                    float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            102 * num, getResources().getDisplayMetrics());
                    ViewGroup.LayoutParams params = mReviewListView.getLayoutParams();
                    params.height = (int) pixels;
                    mReviewListView.setLayoutParams(params);
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<List<CustomClass>> loader) {
        switch (loader.getId()){
            case VIDEO_LOADER_ID:
                mVideoAdapter.clear();
                break;

            case REVIEW_LOADER_ID:
                mReviewAdapter.clear();
                break;
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public boolean isMovieAlreadyInDatabase(){
        String[] projection = {MovieContract.MovieEntry.COLUMN_MOVIE_ID};

        Cursor cursor = getActivity().getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor.moveToFirst()){
            Log.v("isMovieInDatabase", "Data exists, return false");
            return true;
        } else {
            Log.v("isMovieInDatabase", "Data didn't exist, return true");
            return false;
        }
    }

    private static class MovieDetailLoader extends AsyncTaskLoader<List<CustomClass>>{
        /**
         * Tag for log messages
         */
        private static final String LOG_TAG = MovieDetailLoader.class.getName();

        /**
         * Query URL
         */
        private String mUrl;

        public MovieDetailLoader(Context context, String url) {
            super(context);
            mUrl = url;
        }

        @Override
        public List<CustomClass> loadInBackground() {
            if (mUrl == null) {
                return null;
            }
            // Perform the network request, parse the response, and extract a list.
            List<CustomClass> details = fetchMovieData(mUrl);
            return details;
        }

        private static List<CustomClass> fetchMovieData(String requestUrl) {
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
            List<CustomClass> movie = extractMovieFeatureFromJson(jsonResponse);

            // Return the {@link Event}
            return movie;
        }

        private static List<CustomClass> extractMovieFeatureFromJson(String JsonResponse) {

            // Create an empty List that we can start adding news to
            List<CustomClass> movie = new ArrayList<>();
            // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
            // is formatted, a JSONException exception object will be thrown.
            // Catch the exception so the app doesn't crash, and print the error message to the logs.
            try {
                // get root JSONObject
                JSONObject root = new JSONObject(JsonResponse);

                String title = "";
                if (root.has("title")){
                    title = root.getString("title");
                } else if (root.has("original_title")){
                    title = root.getString("original_title");
                }

                String tagline = "";
                if (root.has("tagline")){
                    tagline = root.getString("tagline");
                }

                ArrayList<String> genre = new ArrayList<>();
                if (root.has("genres")){
                    JSONArray jsonArray = root.getJSONArray("genres");
                    int length = jsonArray.length();
                    for (int i = 0; i < length; i++){
                        // get i-th result
                        JSONObject current = jsonArray.getJSONObject(i);
                        genre.add(current.getString("name"));
                    }
                }

                String imagePath = "";
                if (root.has("poster_path")){
                    imagePath = root.getString("poster_path");
                }

                String releaseDate = "Unknown";
                if (root.has("release_date") && !root.isNull("release_date")){
                    releaseDate = root.getString("release_date");
                }

                String synopsis = "No synopsis available";
                if (root.has("overview") && !root.isNull("overview")){
                    synopsis = root.getString("overview");
                }

                double rating = 0;
                if (root.has("vote_average") && !root.isNull("vote_average")){
                    rating = root.getDouble("vote_average");
                }

                String backdropPath = "";
                if (root.has("belongs_to_collection") && !root.isNull("belong_to_collection")){
                    JSONObject collection = root.getJSONObject("belongs_to_collection");
                    backdropPath = collection.getString("backdrop_path");
                } else {
                    backdropPath = root.getString("backdrop_path");
                }

                movie.add(new CustomClass(title, tagline, imagePath, genre, releaseDate, rating, synopsis, backdropPath));
                // Parse the response given by the SAMPLE_JSON_RESPONSE string and
                // build up a list of Earthquake objects with the corresponding data.
            } catch (JSONException e) {
                // If an error is thrown when executing any of the above statements in the "try" block,
                // catch the exception here, so the app doesn't crash. Print a log message
                // with the message from the exception.
                Log.e(LOG_TAG, "Problem parsing the news JSON results", e);
            }

            // Return the list of posters
            return movie;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }

    // Movie Cast Loader + Adapter
    private static class MovieCastLoader extends AsyncTaskLoader<List<CustomClass>> {
        /**
         * Tag for log messages
         */
        private static final String LOG_TAG = MovieCastLoader.class.getName();

        /**
         * Query URL
         */
        private String mUrl;

        public MovieCastLoader(Context context, String url) {
            super(context);
            mUrl = url;
            Log.v(LOG_TAG, "Url is " + mUrl);
        }

        @Override
        public List<CustomClass> loadInBackground() {
            if (mUrl == null) {
                return null;
            }
            // Perform the network request, parse the response, and extract a list.
            List<CustomClass> casts = fetchCastData(mUrl);
            return casts;
        }

        private static List<CustomClass> fetchCastData(String requestUrl) {

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
            List<CustomClass> casts = extractCastFeatureFromJson(jsonResponse);

            // Return the {@link Event}
            return casts;
        }

        private static ArrayList<CustomClass> extractCastFeatureFromJson(String JsonResponse) {

            // Create an empty List that we can start adding news to
            ArrayList<CustomClass> castList = new ArrayList<>();
            // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
            // is formatted, a JSONException exception object will be thrown.
            // Catch the exception so the app doesn't crash, and print the error message to the logs.
            try {
                // get root JSONObject
                JSONObject root = new JSONObject(JsonResponse);

                // get Json Array 'cast'
                JSONArray cast = root.getJSONArray("cast");
                int ln = cast.length();

                for (int i = 0; i < ln; i++){
                    JSONObject currentCast = cast.getJSONObject(i);
                    String name = currentCast.getString("name");
                    String role = currentCast.getString("character");
                    String profile_path = currentCast.getString("profile_path");
                    if (name != null && role != null && profile_path != null && !profile_path.equals("null")){
                        castList.add(new CustomClass(name, profile_path, role));
                    }
                }
            } catch (JSONException e) {
                // If an error is thrown when executing any of the above statements in the "try" block,
                // catch the exception here, so the app doesn't crash. Print a log message
                // with the message from the exception.
                Log.e(LOG_TAG, "Problem parsing the news JSON results", e);
            }

            // Return the list of casts
            return castList;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }

    private class CastAdapter extends RecyclerView.Adapter<CastAdapter.MyViewHolder> {

        private List<CustomClass> casts;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView textViewName;
            public TextView textViewRole;
            public ImageView imageViewCast;

            public MyViewHolder(View view) {
                super(view);
                textViewName = (TextView) view.findViewById(R.id.cast_name);
                textViewRole = (TextView) view.findViewById(R.id.cast_role);
                imageViewCast = (ImageView) view.findViewById(R.id.cast_image);
            }
        }


        public CastAdapter(List<CustomClass> castsInput) {
            casts = castsInput;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_item_view, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            CustomClass current = casts.get(position);
            holder.textViewName.setText(current.getCastName());
            holder.textViewName.setVisibility(View.VISIBLE);
            holder.textViewRole.setText(current.getRole());
            holder.textViewRole.setVisibility(View.VISIBLE);
            String path = "http://image.tmdb.org/t/p/w342" + current.getImagePath();
            Picasso.with(getActivity()).load(path).transform(new CircleTransformation()).into(holder.imageViewCast);
        }

        @Override
        public int getItemCount() {
            return casts.size();
        }
    }

    // Image Gallery Loader + Adapter
    private static class GalleryLoader extends AsyncTaskLoader<List<CustomClass>> {
        /**
         * Tag for log messages
         */
        private static final String LOG_TAG = GalleryLoader.class.getName();
        /**
         * Query URL
         */
        private String mUrl;

        public GalleryLoader(Context context, String url) {
            super(context);
            mUrl = url;
            Log.v(LOG_TAG, "Url is " + mUrl);
        }

        @Override
        public List<CustomClass> loadInBackground() {
            if (mUrl == null) {
                return null;
            }
            // Perform the network request, parse the response, and extract a list.
            List<CustomClass> gallery = fetchCastData(mUrl);
            return gallery;
        }

        private static List<CustomClass> fetchCastData(String requestUrl) {

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
            List<CustomClass> galleryPaths = extractCastFeatureFromJson(jsonResponse);

            // Return the {@link Event}
            return galleryPaths;
        }

        private static ArrayList<CustomClass> extractCastFeatureFromJson(String JsonResponse) {

            // Create an empty List that we can start adding news to
            ArrayList<CustomClass> galleryPaths = new ArrayList<>();

            try {
                // get root JSONObject
                JSONObject root = new JSONObject(JsonResponse);

                // get Json Array 'backdrops'
                if (!root.has("backdrops")){
                    return null;
                }

                JSONArray backdrops = root.getJSONArray("backdrops");
                int ln = backdrops.length();

                for (int i = 0; i < ln; i++){
                    JSONObject currentBackdrop = backdrops.getJSONObject(i);
                    String path = currentBackdrop.getString("file_path");

                    galleryPaths.add(new CustomClass(path));
                }
            } catch (JSONException e) {
                // If an error is thrown when executing any of the above statements in the "try" block,
                // catch the exception here, so the app doesn't crash. Print a log message
                // with the message from the exception.
                Log.e(LOG_TAG, "Problem parsing the news JSON results", e);
            }

            // Return the list of casts
            return galleryPaths;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }

    private class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {

        private List<CustomClass> paths;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageViewCast;

            public MyViewHolder(View view) {
                super(view);
                imageViewCast = (ImageView) view.findViewById(R.id.cast_image);
            }
        }

        public GalleryAdapter(List<CustomClass> pathInputs) {
            paths = pathInputs;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_item_view, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            String appendPath = paths.get(position).getGalleryPath();
            String path = "http://image.tmdb.org/t/p/w500" + appendPath;
            Picasso.with(getActivity()).load(path).into(holder.imageViewCast);
        }

        @Override
        public int getItemCount() {
            return paths.size();
        }
    }

    // Similar Movie Loader + Adapter
    private static class SimilarLoader extends AsyncTaskLoader<List<CustomClass>> {
        /**
         * Tag for log messages
         */
        private static final String LOG_TAG = SimilarLoader.class.getName();
        /**
         * Query URL
         */
        private String mUrl;

        public SimilarLoader(Context context, String url) {
            super(context);
            mUrl = url;
            Log.v(LOG_TAG, "Url is " + mUrl);
        }

        @Override
        public List<CustomClass> loadInBackground() {
            if (mUrl == null) {
                return null;
            }
            // Perform the network request, parse the response, and extract a list.
            List<CustomClass> similar = fetchCastData(mUrl);
            return similar;
        }

        private static List<CustomClass> fetchCastData(String requestUrl) {

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
            List<CustomClass> similarPosters = extractCastFeatureFromJson(jsonResponse);

            // Return the {@link Event}
            return similarPosters;
        }

        private static ArrayList<CustomClass> extractCastFeatureFromJson(String JsonResponse) {

            // Create an empty List that we can start adding news to
            ArrayList<CustomClass> similarPosters = new ArrayList<>();

            // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
            // is formatted, a JSONException exception object will be thrown.
            // Catch the exception so the app doesn't crash, and print the error message to the logs.
            try {
                // get root JSONObject
                JSONObject root = new JSONObject(JsonResponse);

                // get Json Array 'backdrops'

                if (!root.has("results")){
                    return null;
                }

                JSONArray results = root.getJSONArray("results");
                int ln = results.length();

                for (int i = 0; i < ln; i++){
                    JSONObject current = results.getJSONObject(i);
                    String path = current.getString("poster_path");
                    int id = current.getInt("id");

                    similarPosters.add(new CustomClass(path, id));
                }
            } catch (JSONException e) {
                // If an error is thrown when executing any of the above statements in the "try" block,
                // catch the exception here, so the app doesn't crash. Print a log message
                // with the message from the exception.
                Log.e(LOG_TAG, "Problem parsing the news JSON results", e);
            }

            // Return the list of casts
            return similarPosters;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }

    public class SimilarAdapter extends RecyclerView.Adapter<SimilarAdapter.MyViewHolder> {

        private List<CustomClass> paths;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageViewCast;

            public MyViewHolder(View view) {
                super(view);
                imageViewCast = (ImageView) view.findViewById(R.id.cast_image);
            }
        }

        public SimilarAdapter(List<CustomClass> pathInputs) {
            paths = pathInputs;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_item_view, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            CustomClass current = paths.get(position);
            String path = "http://image.tmdb.org/t/p/w342" + current.getMoviePosterPath();
            final int id = current.getMovieId();
            Picasso.with(getActivity()).load(path).into(holder.imageViewCast);

            holder.imageViewCast.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((Callback)getActivity()).onItemSelected(id);
                }
            });
        }

        @Override
        public int getItemCount() {
            return paths.size();
        }
    }

    // Video Loader + Adapter
    private static class VideoLoader extends AsyncTaskLoader<List<CustomClass>> {
        /**
         * Tag for log messages
         */
        private static final String LOG_TAG = VideoLoader.class.getName();
        /**
         * Query URL
         */
        private String mUrl;

        public VideoLoader(Context context, String url) {
            super(context);
            mUrl = url;
            Log.v(LOG_TAG, "Url is " + mUrl);
        }

        @Override
        public List<CustomClass> loadInBackground() {
            if (mUrl == null) {
                return null;
            }
            // Perform the network request, parse the response, and extract a list.
            List<CustomClass> videos = fetchCastData(mUrl);
            return videos;
        }

        private static List<CustomClass> fetchCastData(String requestUrl) {

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
            List<CustomClass> videos = extractCastFeatureFromJson(jsonResponse);

            // Return the {@link Event}
            return videos;
        }

        private static ArrayList<CustomClass> extractCastFeatureFromJson(String JsonResponse) {

            // Create an empty List that we can start adding news to
            ArrayList<CustomClass> videos = new ArrayList<>();

            try {
                // get root JSONObject
                JSONObject root = new JSONObject(JsonResponse);

                // get Json Array 'results'
                if (!root.has("results")){
                    return null;
                }
                JSONArray results = root.getJSONArray("results");
                int ln = results.length();

                for (int i = 0; i < ln; i++){
                    JSONObject current = results.getJSONObject(i);
                    String videoName = current.getString("name");
                    String key = current.getString("key");

                    videos.add(new CustomClass(videoName, key));
                }
            } catch (JSONException e) {
                // If an error is thrown when executing any of the above statements in the "try" block,
                // catch the exception here, so the app doesn't crash. Print a log message
                // with the message from the exception.
                Log.e(LOG_TAG, "Problem parsing the news JSON results", e);
            }
            // Return the list of videos
            return videos;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }

    public class VideoAdapter extends ArrayAdapter<CustomClass> {

        public VideoAdapter(Context context, ArrayList<CustomClass> resource) {
            super(context, 0, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View gridItemView = convertView;

            if (gridItemView == null){
                gridItemView = LayoutInflater.from(getContext()).inflate(R.layout.video_listview_item, parent, false);
            }
            final CustomClass current = getItem(position);

            final String path = "https://www.youtube.com/watch?v=" + current.getSecondaryString();

            TextView videoName = (TextView) gridItemView.findViewById(R.id.video_title);
            String name = current.getPrimaryString();
            videoName.setText(name);
            videoName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
                    startActivity(intent);
                }
            });
            return gridItemView;
        }
    }

    // Review Loader + Adapter
    public class ReviewAdapter extends ArrayAdapter<CustomClass> {

        public ReviewAdapter(Context context, ArrayList<CustomClass> resource) {
            super(context, 0, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View listViewItem = convertView;

            if (listViewItem == null){
                listViewItem = LayoutInflater.from(getContext()).inflate(R.layout.review_listview_item, parent, false);
            }
            final CustomClass current = getItem(position);

            String userId = current.getPrimaryString();
            String reviewText = current.getSecondaryString();

            TextView tvUser = (TextView) listViewItem.findViewById(R.id.review_id);
            tvUser.setText("Review by: " + userId);

            TextView tvReview = (TextView) listViewItem.findViewById(R.id.review_text);
            tvReview.setText(reviewText);

            tvReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(),"get the review", Toast.LENGTH_SHORT).show();
                }
            });
            return listViewItem;
        }
    }

    private static class ReviewLoader extends AsyncTaskLoader<List<CustomClass>> {
        /**
         * Tag for log messages
         */
        private static final String LOG_TAG = ReviewLoader.class.getName();
        /**
         * Query URL
         */
        private String mUrl;

        public ReviewLoader(Context context, String url) {
            super(context);
            mUrl = url;
            Log.v(LOG_TAG, "Url is " + mUrl);
        }

        @Override
        public List<CustomClass> loadInBackground() {
            if (mUrl == null) {
                return null;
            }
            // Perform the network request, parse the response, and extract a list.
            List<CustomClass> reviews = fetchCastData(mUrl);
            return reviews;
        }

        private static List<CustomClass> fetchCastData(String requestUrl) {

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
            List<CustomClass> reviews = extractCastFeatureFromJson(jsonResponse);

            // Return the {@link Event}
            return reviews;
        }

        private static ArrayList<CustomClass> extractCastFeatureFromJson(String JsonResponse) {

            // Create an empty List that we can start adding news to
            ArrayList<CustomClass> reviews = new ArrayList<>();

            try {
                // get root JSONObject
                JSONObject root = new JSONObject(JsonResponse);

                // get Json Array 'results'
                if (!root.has("results")){
                    return null;
                }
                JSONArray results = root.getJSONArray("results");
                int ln = results.length();

                for (int i = 0; i < ln; i++){
                    JSONObject current = results.getJSONObject(i);
                    String author = current.getString("author");
                    String content = current.getString("content");

                    reviews.add(new CustomClass(author, content));
                }
            } catch (JSONException e) {
                // If an error is thrown when executing any of the above statements in the "try" block,
                // catch the exception here, so the app doesn't crash. Print a log message
                // with the message from the exception.
                Log.e(LOG_TAG, "Problem parsing the news JSON results", e);
            }
            // Return the list of videos
            return reviews;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }

}

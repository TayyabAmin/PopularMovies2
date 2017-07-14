package com.example.popularmoviesstage2;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.adapter.ReviewsAdapter;
import com.example.adapter.TrailersAdapter;
import com.example.data.FavoriteContract;
import com.example.model.ReviewData;
import com.example.model.TrailerData;
import com.example.popularmoviesstage2.databinding.ActivityDetailMovieBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Tayyab on 6/5/2017.
 */

public class DetailActivity extends AppCompatActivity implements TrailersAdapter.ListItemClickListener{
    private ActivityDetailMovieBinding mBinding;

    private int movieId;
    private String moviePosterPath;
    private String movieTitle;
    private String movieOverview;
    private String movieVotingAverage;
    private String movieReleaseDate;
    private ArrayList<TrailerData> trailerList;
    private ArrayList<ReviewData> reviewList;
    private JSONObject jsonObj;
//    private SQLiteDatabase mDb;
//    private FavoriteDBHelper dbHelper;
    private final String CALLBACK_TRAILER = "trailer_callback";
    private final String CALLBACK_REVIEW = "review_callback";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail_movie);
//        dbHelper = new FavoriteDBHelper(this);

        Bundle bundle = getIntent().getExtras();

        movieId = bundle.getInt("id");
        moviePosterPath = bundle.getString("poster_path");
        movieTitle = bundle.getString("title");
        movieOverview = bundle.getString("overview");
        movieVotingAverage = bundle.getString("rating");
        movieReleaseDate = bundle.getString("release_date");

        mBinding.movieTitle.setText(movieTitle);
        String ratingText = movieVotingAverage+"/10";
        mBinding.movieRating.setText(ratingText);
        mBinding.movieYear.setText(getYear(movieReleaseDate));
        mBinding.movieOverview.setText(movieOverview);
        if (checkMovieExist(movieId)) {
            mBinding.movieFav.setText(getResources().getString(R.string.mark_as_fav_2));
            mBinding.movieFav.setBackgroundColor(ContextCompat.getColor(this, R.color.yellowGold));
        }else {
            mBinding.movieFav.setText(getResources().getString(R.string.mark_as_fav));
        }


        final String URL = "http://image.tmdb.org/t/p/w185";
        String path = URL + bundle.getString("poster_path");

        Picasso.with(DetailActivity.this).load(path).into(mBinding.moviePoster);

        String detailPath = "https://api.themoviedb.org/3/movie/";
        String completeTrailerPath = detailPath+movieId+"/videos?api_key="+MainActivity.apiKey;
        String completeReviewPath = detailPath+movieId+"/reviews?api_key="+MainActivity.apiKey;

        if (savedInstanceState != null) {
            reviewList = savedInstanceState.getParcelableArrayList(CALLBACK_REVIEW);
            trailerList = savedInstanceState.getParcelableArrayList(CALLBACK_TRAILER);
            addRecyclerViewAdapter();
            addReviewRecyclerViewAdapter();
        }else {
            fetchMovieTrailers(completeTrailerPath);
            fetchMovieReviews(completeReviewPath);
        }
    }

    private String getYear(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd", Locale.US);
            Date date = sdf.parse(dateString);

            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy", Locale.US);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(CALLBACK_TRAILER, trailerList);
        outState.putParcelableArrayList(CALLBACK_REVIEW, reviewList);
        super.onSaveInstanceState(outState);
    }

    private void fetchMovieTrailers(String moviesPath) {
        // Check if Internet is working
        if (!isNetworkAvailable(this)) {
            // Show a message to the user to check his Internet
            Toast.makeText(this, "Please check your Internet connection", Toast.LENGTH_SHORT).show();
        } else {
            trailerList = new ArrayList<>();
            // make HTTP request to retrieve the trailers
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    moviesPath, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        int length = response.getJSONArray("results").length();
                        for (int i=0; i<length; i++) {
                            jsonObj = (JSONObject) response.getJSONArray("results").get(i);

                            TrailerData trailerData = new TrailerData();
                            trailerData.setId(jsonObj.getString("id"));
                            trailerData.setKey(jsonObj.getString("key"));
                            trailerData.setName(jsonObj.getString("name"));
                            trailerList.add(trailerData);
                        }
                        addRecyclerViewAdapter();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("tag", "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), "Error while loading ... ", Toast.LENGTH_SHORT).show();
                }
            });
            // Adding request to request queue
            AppController.getInstance(this).addToRequestQueue(jsonObjReq);
        }
    }

    private void addRecyclerViewAdapter () {
        LinearLayoutManager trailerLayoutManager;
        trailerLayoutManager = new LinearLayoutManager(this);
        mBinding.detailRecyclerView.setLayoutManager(trailerLayoutManager);
        mBinding.detailRecyclerView.setHasFixedSize(true);
        TrailersAdapter adapter = new TrailersAdapter(DetailActivity.this, trailerList, this);
        mBinding.detailRecyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mBinding.detailRecyclerView.getContext(),
                trailerLayoutManager.getOrientation());
        mBinding.detailRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    ////////////////////check internet connection
    private boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));

        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onListItemClicked(int checkedItemIndex) {
        watchYoutubeVideo(trailerList.get(checkedItemIndex).getKey());
    }

    private void watchYoutubeVideo(String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    private void fetchMovieReviews(String moviesPath) {
        // Check if Internet is working
        if (!isNetworkAvailable(this)) {
            // Show a message to the user to check his Internet
            Toast.makeText(this, "Please check your Internet connection", Toast.LENGTH_SHORT).show();
        } else {
            reviewList = new ArrayList<>();
            // make HTTP request to retrieve the trailers
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    moviesPath, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        int length = response.getJSONArray("results").length();
                        for (int i=0; i<length; i++) {
                            jsonObj = (JSONObject) response.getJSONArray("results").get(i);

                            ReviewData reviewData = new ReviewData();
                            reviewData.setId(jsonObj.getString("id"));
                            reviewData.setAuthor(jsonObj.getString("author"));
                            reviewData.setContent(jsonObj.getString("content"));
//                            reviewData.setContent(jsonObj.getString("url"));
                            reviewList.add(reviewData);
                        }
                        addReviewRecyclerViewAdapter();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("tag", "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), "Error while loading ... ", Toast.LENGTH_SHORT).show();
                }
            });
            // Adding request to request queue
            AppController.getInstance(this).addToRequestQueue(jsonObjReq);
        }
    }

    private void addReviewRecyclerViewAdapter () {
        LinearLayoutManager reviewLayoutManager;
        reviewLayoutManager = new LinearLayoutManager(this);
        mBinding.reviewsRecyclerView.setLayoutManager(reviewLayoutManager);
        mBinding.reviewsRecyclerView.setHasFixedSize(true);
        ReviewsAdapter adapter = new ReviewsAdapter(DetailActivity.this, reviewList);
        mBinding.reviewsRecyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mBinding.reviewsRecyclerView.getContext(),
                reviewLayoutManager.getOrientation());
        mBinding.reviewsRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    /*public boolean checkMovieExist (int movieId) {
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Integer> favLoader = loaderManager.getLoader(LOADER_ID);
        Bundle bundle = new Bundle();
        if (favLoader == null) {
            bundle.putInt("id", movieId);
            loaderManager.initLoader(LOADER_ID, bundle, this);
        }else {
            bundle.putInt("id", movieId);
            loaderManager.restartLoader(LOADER_ID, bundle, this);
        }
    }*/

    private boolean checkMovieExist(int movieId) {
        try {
            String selection = "movie_id=?";
            String[] selectionArgs = new String[]{String.valueOf(movieId)};
            Cursor cursor = getContentResolver().query(FavoriteContract.FavoriteEntry.CONTENT_URI,
                    null,
                    selection,
                    selectionArgs,
                    null);

            if (cursor.getCount() <= 0) {
                cursor.close();
                return false;
            }
            cursor.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
        /*String[] column = {FavoriteContract.FavoriteEntry._ID};
        String selection = FavoriteContract.FavoriteEntry.MOVIE_ID+"=?";
        String[] selectionArgs = new String[]{String.valueOf(movieId)};
        Cursor cursor = mDb.query(
                FavoriteContract.FavoriteEntry.TABLE_NAME,
                column,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.getCount() <=0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;*/


    /*public long addFavorite(int movieId) {
        mDb = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoriteContract.FavoriteEntry.MOVIE_ID, movieId);
        contentValues.put(FavoriteContract.FavoriteEntry.MOVIE_POSTER_PATH, moviePosterPath);
        contentValues.put(FavoriteContract.FavoriteEntry.MOVIE_TITLE, movieTitle);
        contentValues.put(FavoriteContract.FavoriteEntry.MOVIE_OVERVIEW, movieOverview);
        contentValues.put(FavoriteContract.FavoriteEntry.MOVIE_VOTE_AVERAGE, movieVotingAverage);
        contentValues.put(FavoriteContract.FavoriteEntry.MOVIE_RELEASE_DATE, movieReleaseDate);
        return mDb.insert(FavoriteContract.FavoriteEntry.TABLE_NAME, null, contentValues);
    }*/

    private Uri addFavorite(int movieId) {

        Uri uri = null;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(FavoriteContract.FavoriteEntry.MOVIE_ID, movieId);
            contentValues.put(FavoriteContract.FavoriteEntry.MOVIE_POSTER_PATH, moviePosterPath);
            contentValues.put(FavoriteContract.FavoriteEntry.MOVIE_TITLE, movieTitle);
            contentValues.put(FavoriteContract.FavoriteEntry.MOVIE_OVERVIEW, movieOverview);
            contentValues.put(FavoriteContract.FavoriteEntry.MOVIE_VOTE_AVERAGE, movieVotingAverage);
            contentValues.put(FavoriteContract.FavoriteEntry.MOVIE_RELEASE_DATE, movieReleaseDate);

            uri = getContentResolver().insert(FavoriteContract.FavoriteEntry.CONTENT_URI, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Excp", e.getMessage());
        }
        return uri;
    }

    public void clickFavorite(View view) {
        if (!checkMovieExist(movieId)) {
            Uri uri = addFavorite(movieId);
            if (uri != null) {
                mBinding.movieFav.setText(getResources().getString(R.string.mark_as_fav_2));
                mBinding.movieFav.setBackgroundColor(ContextCompat.getColor(this, R.color.yellowGold));
            }
        }
    }
}

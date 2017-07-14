package com.example.popularmoviesstage2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.adapter.MoviesAdapter;
import com.example.data.FavoriteContract;
import com.example.model.MovieData;
import com.example.popularmoviesstage2.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ActivityMainBinding mainBinding;

    static final String apiKey = ""; // Insert  your api key here
    private final String popularMoviePath = "https://api.themoviedb.org/3/movie/popular?api_key="+apiKey;
    private final String topRatedMoviePath = "https://api.themoviedb.org/3/movie/top_rated?api_key="+apiKey;
    private ProgressDialog pDialog;
    private JSONObject jsonObj;
    private ArrayList<MovieData> arrayList;
    private int isPopularMovieSelected = 1;
    private static final String LIFECYCLE_CALLBACKS_KEY = "callbacks";
    private static final int LOADER_ID = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mainBinding.gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);

                intent.putExtra("id", arrayList.get(position).getMovieId());
                intent.putExtra("title", arrayList.get(position).getMovieTitle());
                intent.putExtra("poster_path", arrayList.get(position).getPath());
                intent.putExtra("rating", arrayList.get(position).getMovieRating());
                intent.putExtra("release_date", arrayList.get(position).getReleaseDate());
                intent.putExtra("overview", arrayList.get(position).getMovieOverview());

                startActivity(intent);
            }
        });

        if (savedInstanceState != null) {
//            Toast.makeText(this, "onSaveInstance != null", Toast.LENGTH_SHORT).show();
            arrayList = savedInstanceState.getParcelableArrayList(LIFECYCLE_CALLBACKS_KEY);
            if (arrayList != null) {
                MoviesAdapter adapter = new MoviesAdapter(MainActivity.this, R.layout.grid_view, arrayList);
                mainBinding.gridview.setAdapter(adapter);
            }
        }else {
//            Toast.makeText(this, "FETCH MOVIES", Toast.LENGTH_SHORT).show();
            fetchMovies(popularMoviePath);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(LIFECYCLE_CALLBACKS_KEY, arrayList);
//        Toast.makeText(this, "onSaveInstance", Toast.LENGTH_SHORT).show();
        super.onSaveInstanceState(outState);

    }

    private void fetchMovies(String moviesPath) {
        // prepare the loading Dialog
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait while retrieving the data ...");
        pDialog.setCancelable(false);

        arrayList = new ArrayList<MovieData>();

        // Check if Internet is working
        if (!isNetworkAvailable(this)) {
            // Show a message to the user to check his Internet
            Toast.makeText(this, "Please check your Internet connection", Toast.LENGTH_SHORT).show();
        } else {
            pDialog.show();

            // make HTTP request to retrieve the weather
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    moviesPath, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        int length = response.getJSONArray("results").length();
                        for (int i=0; i<length; i++) {
                            jsonObj = (JSONObject) response.getJSONArray("results").get(i);
//                            Log.d("TAG", "onResponse: "+jsonObj);
                            int id = jsonObj.getInt("id");
                            String poster = jsonObj.getString("poster_path");
                            String title = jsonObj.getString("original_title");
                            String overview = jsonObj.getString("overview");
                            String rating = jsonObj.getString("vote_average");
                            String date = jsonObj.getString("release_date");

                            addValues(id, poster, title, overview, rating, date, arrayList);
                        }

                        pDialog.dismiss();
                        MoviesAdapter adapter = new MoviesAdapter(MainActivity.this, R.layout.grid_view, arrayList);
                        mainBinding.gridview.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("tag", "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), "Error while loading ... ", Toast.LENGTH_SHORT).show();
                    // hide the progress dialog
                    pDialog.dismiss();
                }
            });
            // Adding request to request queue
            AppController.getInstance(this).addToRequestQueue(jsonObjReq);
        }
    }

    private void addValues(int id, String poster, String title, String overview, String rating, String date, ArrayList<MovieData> list) {
        MovieData movieData = new MovieData();

        movieData.setMovieId(id);
        movieData.setPath(poster);
        movieData.setMovieTitle(title);
        movieData.setMovieOverview(overview);
        movieData.setMovieRating(rating);
        movieData.setReleaseDate(date);
        list.add(movieData);
    }

    ////////////////////check internet connection
    private boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private void fetchFavoriteList(Cursor mCursor) {

        arrayList = new ArrayList<MovieData>();
        if (mCursor.moveToFirst()) {
            do {
                int id = mCursor.getInt(1);
                String poster = mCursor.getString(2);
                String title = mCursor.getString(3);
                String overview = mCursor.getString(4);
                String rating = mCursor.getString(5);
                String date = mCursor.getString(6);

                addValues(id, poster, title, overview, rating, date, arrayList);
            } while (mCursor.moveToNext());
        }else {
            mainBinding.messageview.setVisibility(View.VISIBLE);
        }
        if (!mCursor.isClosed()) {
            mCursor.close();
        }

        MoviesAdapter adapter = new MoviesAdapter(MainActivity.this, R.layout.grid_view, arrayList);
        mainBinding.gridview.setAdapter(adapter);
    }

    /*public Cursor fetchFavoriteMovies() {
        FavoriteDBHelper dbHelper = new FavoriteDBHelper(this);
        SQLiteDatabase mDb;
        mDb = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM "+ FavoriteContract.FavoriteEntry.TABLE_NAME;
        Cursor cursor = mDb.rawQuery(query, null);
        return cursor;
    }*/

    private void fetchFavoriteMovies() {
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Integer> favLoader = loaderManager.getLoader(LOADER_ID);
        if (favLoader == null) {
            loaderManager.initLoader(LOADER_ID, null, this);
        }else {
            loaderManager.restartLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_setting) {

            View menuItemView = findViewById(R.id.action_setting);
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, menuItemView);
            popupMenu.getMenuInflater().inflate(R.menu.pop_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    switch (id) {
                        case R.id.popular:
                            if (isPopularMovieSelected != 1) {
                                isPopularMovieSelected = 1;
                                fetchMovies(popularMoviePath);
                            }
                            break;
                        case R.id.rating:
                            if (isPopularMovieSelected != 2) {
                                isPopularMovieSelected = 2;
                                fetchMovies(topRatedMoviePath);
                            }
                            break;
                        case R.id.favorite:
                            if (isPopularMovieSelected != 3) {
                                isPopularMovieSelected = 3;
                                fetchFavoriteMovies();
                            }
                            break;
                    }

                    return true;
                }
            });

            popupMenu.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(MainActivity.this,
                FavoriteContract.FavoriteEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        fetchFavoriteList(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

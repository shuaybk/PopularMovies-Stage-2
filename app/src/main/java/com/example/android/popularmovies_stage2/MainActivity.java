package com.example.android.popularmovies_stage2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies_stage2.DatabaseUtils.AppDatabase;
import com.example.android.popularmovies_stage2.DatabaseUtils.FavouriteMovie;
import com.example.android.popularmovies_stage2.utilities.JsonUtils;
import com.example.android.popularmovies_stage2.utilities.NetworkUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


////// TO DO
//Fix the observer to stop creating a new one every time we sort by favourite.
//It should probably be created in onCreate once


public class MainActivity extends AppCompatActivity {

    public static final String API_KEY = "";  //themoviedb.org API Key, replace with your own to make this app work
    public static final int IMAGE_WIDTH = 342; //Choices are 92, 154, 185, 342, 500, 780
    public static final String SORT_TYPE_POPULAR = "POPULAR_SORT";
    public static final String SORT_TYPE_RATING = "RATING_SORT";
    public static final String SORT_TYPE_FAVOURITE = "FAVOURITE_SORT";
    public static final String BUNDLE_SORT_KEY = "SORT TYPE";

    private ArrayList<Movie> movieList;

    private RecyclerView mRecyclerViewMovies;

    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessage;

    private String currentSort;
    private LiveData<List<FavouriteMovie>> favouriteMoviesLD;

    AppDatabase mDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDb = AppDatabase.getInstance(this);

        movieList = new ArrayList<Movie>();

        mRecyclerViewMovies = (RecyclerView) findViewById(R.id.recyclerview_movies);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorMessage = (TextView) findViewById(R.id.tv_error_message);

        //get current sort first
        if (savedInstanceState != null) {
            currentSort = savedInstanceState.getString(BUNDLE_SORT_KEY);
        } else {
            currentSort = SORT_TYPE_POPULAR;
        }
        startFavouriteMoviesObserver();
        if (!currentSort.equals(SORT_TYPE_FAVOURITE)) {
            refreshList();
        }

        setTitle("PopularMovies");

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(BUNDLE_SORT_KEY, currentSort);
        super.onSaveInstanceState(outState);
    }

    //Fetch data online and refresh the list if there is an internet connection, otherwise display error
    private void refreshList() {
        if (isConnectedToInternet()) {
            URL url = NetworkUtils.getMovieURL(currentSort);
            new MovieQueryTask().execute(url);
        } else {
            mRecyclerViewMovies.setVisibility(View.INVISIBLE);
            mErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_sort_popular:
                Toast.makeText(this, "Sorting by popularity", Toast.LENGTH_LONG).show();
                currentSort = SORT_TYPE_POPULAR;
                refreshList();
                return true;
            case R.id.action_sort_rating:
                Toast.makeText(this, "Sorting by top rated", Toast.LENGTH_LONG).show();
                currentSort = SORT_TYPE_RATING;
                refreshList();
                return true;
            case R.id.action_sort_fav:
                Toast.makeText(this, "Showing favourites", Toast.LENGTH_LONG).show();
                currentSort = SORT_TYPE_FAVOURITE;
                showFavourites();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Called when we receive movie data, either from TMDB or from our own DB
    //Sets the appropriate data depending what kind of data was passed to it
    public void setMovieData(String json, List<FavouriteMovie> favouriteMovies) {
        //For either type of data, first clear the movie list then populate it again
        //with new results, then reinitialize the RecyclerView
        if (json != null) {

            try {
                JSONObject jsonData = new JSONObject(json);
                JSONArray jsonMovieList = jsonData.getJSONArray("results");
                movieList.clear();

                for (int i = 0; i < jsonMovieList.length(); i++) {
                    movieList.add(JsonUtils.parseMovieJson(jsonMovieList.getJSONObject(i)));
                }

                initRecyclerViewMovies();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (favouriteMovies != null) {
            movieList.clear();

            for (FavouriteMovie favouriteMovie: favouriteMovies) {
                movieList.add(favouriteMovie.toMovie());
            }
            initRecyclerViewMovies();
        }
    }

    public void initRecyclerViewMovies() {
        MovieAdapter adapter = new MovieAdapter(this, movieList);
        mRecyclerViewMovies.setAdapter(adapter);
        int numColumns = getNumColumns();
        mRecyclerViewMovies.setLayoutManager(new GridLayoutManager(this, numColumns));
    }

    //Calculate the number of columns required for the grid layout based on screen width and image width
    public int getNumColumns() {
        int imageWidth = IMAGE_WIDTH;
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        return screenWidth/imageWidth;
    }

    public boolean isConnectedToInternet() {
        ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null) {
            return false;
        }
        return true;
    }

    //Queries the database for all our favourites and lists them
    public void showFavourites() {
        //Start observer to load favourite movies as a ONE TIME load
        //We don't want to keep this observer otherwise we are creating a new observer everytime user
        //sorts by favourites.  So remove the observer in the onChanged method
        //We already have a different observer for listening to updates started in onCreate
        favouriteMoviesLD.observe(this, new Observer<List<FavouriteMovie>>() {
            @Override
            public void onChanged(@Nullable List<FavouriteMovie> favMovies) {
                favouriteMoviesLD.removeObserver(this);
                mRecyclerViewMovies.setVisibility(View.VISIBLE);
                mErrorMessage.setVisibility(View.INVISIBLE);
                setMovieData(null, favMovies);
            }
        });
    }

    //Helper method to start the observer
    private void startFavouriteMoviesObserver() {
        favouriteMoviesLD = mDb.favouriteMovieDao().loadAllFavMovies();
        //Start observer to listen for all updates throughout activity life
        favouriteMoviesLD.observe(this, new Observer<List<FavouriteMovie>>() {
            @Override
            public void onChanged(@Nullable List<FavouriteMovie> favMovies) {
                if (currentSort.equals(SORT_TYPE_FAVOURITE)) {
                    setMovieData(null, favMovies);
                }
            }
        });
    }


    public class MovieQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
            mRecyclerViewMovies.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String queryResults = null;
            try {
                queryResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return queryResults;
        }

        @Override
        protected void onPostExecute(String queryResults) {
            if (queryResults == null) {
                mErrorMessage.setVisibility(View.VISIBLE);
                mRecyclerViewMovies.setVisibility(View.INVISIBLE);
            } else {
                mErrorMessage.setVisibility(View.INVISIBLE);
                mRecyclerViewMovies.setVisibility(View.VISIBLE);
                setMovieData(queryResults, null);
            }
            mLoadingIndicator.setVisibility(View.INVISIBLE);
        }
    }
}
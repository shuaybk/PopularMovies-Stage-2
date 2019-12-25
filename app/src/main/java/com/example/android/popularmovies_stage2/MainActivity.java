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

public class MainActivity extends AppCompatActivity {

    public static final String API_KEY = "";  //themoviedb.org API Key, replace with your own to make this app work
    public static final int IMAGE_WIDTH = 342; //Choices are 92, 154, 185, 342, 500, 780
    public static final String SORT_TYPE_POPULAR = "POPULAR_SORT";
    public static final String SORT_TYPE_RATING = "RATING_SORT";
    public static final String SORT_TYPE_FAVOURITE = "FAVOURITE_SORT";

    private ArrayList<Movie> movieList;

    private RecyclerView mRecyclerViewMovies;

    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessage;

    private String currentSort;

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

        currentSort = SORT_TYPE_POPULAR;
        refreshList(1);

        setTitle("PopularMovies");

    }

    //Fetch data online and refresh the list if there is an internet connection, otherwise display error
    private void refreshList(int sortBy) {
        if (isConnectedToInternet()) {
            URL url = NetworkUtils.getURL(sortBy);
            new MovieQueryTask().execute(url);
        } else {
            mRecyclerViewMovies.setVisibility(View.INVISIBLE);
            mErrorMessage.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Error: No Internet Connection!", Toast.LENGTH_LONG).show();
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
                refreshList(1);
                return true;
            case R.id.action_sort_rating:
                Toast.makeText(this, "Sorting by top rated", Toast.LENGTH_LONG).show();
                currentSort = SORT_TYPE_RATING;
                refreshList(2);
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
        LiveData<List<FavouriteMovie>> favouriteMoviesLD = mDb.favouriteMovieDao().loadAllFavMovies();
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


    /// TEST CODE FOR DB ///
    /*
    @Override
    protected void onResume() {
        super.onResume();
        testDb();
    }

    public void testDb() {
        List<FavouriteMovie> favouriteMovies = mDb.favouriteMovieDao().loadAllFavMovies();
        System.out.println("HEEEEEEEEEEEEEEEEEEYYYYYYYYYYYYYY LISTEN UP");
        for (FavouriteMovie movie: favouriteMovies) {
            System.out.println(movie.getId() + ", " + movie.getTitle());
        }
    }
    */
}
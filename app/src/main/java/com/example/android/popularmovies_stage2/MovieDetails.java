package com.example.android.popularmovies_stage2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies_stage2.DatabaseUtils.AppDatabase;
import com.example.android.popularmovies_stage2.DatabaseUtils.FavouriteMovie;
import com.squareup.picasso.Picasso;

public class MovieDetails extends AppCompatActivity {

    Movie movie;
    ImageView mFullPoster;
    TextView mDescription;
    TextView mRating;
    TextView mRelease;
    Menu menu;
    boolean isFavourite;


    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        mDb = AppDatabase.getInstance(getApplicationContext());

        movie = null;
        mFullPoster = (ImageView) findViewById(R.id.im_full_poster);
        mDescription = (TextView) findViewById(R.id.tv_description);
        mRating = (TextView) findViewById(R.id.tv_rating);
        mRelease = (TextView) findViewById(R.id.tv_release);

        Intent parentIntent = getIntent();
        if (parentIntent.hasExtra(Intent.EXTRA_COMPONENT_NAME)) {
            movie = (Movie)parentIntent.getSerializableExtra(Intent.EXTRA_COMPONENT_NAME);
        }
        Picasso.get().load(movie.getPoster()).into(mFullPoster);
        setTitle(movie.getTitle());
        mDescription.setText(movie.getDescription());
        mRating.setText("Avg User Rating: " + movie.getRating() + "%");
        mRelease.setText("Release Date: " + movie.getReleaseDate());
    }


    ///////////////////////////
    ////  MENU STUFF HERE  ////
    ///////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        this.menu = menu;
        startFavouriteMovieObserver();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_favourite) {
            //Toggle from favourites list
            toggleFavourite();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //Helper method to toggle this movie as favourited/unfavourited
    //If it was a favourite before, delete it from favourites list now (to unfavourite)
    //otherwise add it to favourites list
    private void toggleFavourite() {

        if (movie != null) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    FavouriteMovie favouriteMovie = movie.toFavouriteMovie();
                    if (isFavourite) {
                        mDb.favouriteMovieDao().deleteFavMovie(favouriteMovie);
                    } else {
                        mDb.favouriteMovieDao().insertFavMovie(favouriteMovie);
                    }
                }
            });
        }
    }

    //Helper method to start the observer to listen for changes to this movie being
    //added/removed from favourites (and refreshing the state of the star button to on/off)
    private void startFavouriteMovieObserver() {

        final LiveData<FavouriteMovie> favouriteMovieLD = mDb.favouriteMovieDao().getFavMovieById2(movie.getId());
        favouriteMovieLD.observe(this, new Observer<FavouriteMovie>() {
            @Override
            public void onChanged(FavouriteMovie favouriteMovie) {
                MenuItem favButton = menu.findItem(R.id.action_favourite);

                if (favouriteMovie != null) {
                    isFavourite = true;
                    favButton.setIcon(android.R.drawable.btn_star_big_on);
                } else {
                    isFavourite = false;
                    favButton.setIcon(android.R.drawable.btn_star_big_off);
                }
            }
        });
    }
}

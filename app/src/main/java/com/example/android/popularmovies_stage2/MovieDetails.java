package com.example.android.popularmovies_stage2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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


        if (isFavourite()) {
            Toast.makeText(this, "Omg i love this movie!!!", Toast.LENGTH_SHORT).show();
        }
    }


    ///////////////////////////
    ////  MENU STUFF HERE  ////
    ///////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        this.menu = menu;
        setIcon();
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
    private void toggleFavourite() {
        if (movie != null) {
            FavouriteMovie favouriteMovie = new FavouriteMovie(movie.getId(), movie.getTitle(), movie.getPoster(),
                    movie.getDescription(), movie.getRating(), movie.getReleaseDate());

            if (isFavourite()) {
                //Remove from favourites
                mDb.favouriteMovieDao().deleteFavMovie(favouriteMovie);
            } else {
                //Add to favourites
                mDb.favouriteMovieDao().insertFavMovie(favouriteMovie);
            }
            setIcon();

            Toast.makeText(this, "id = " + movie.getId(), Toast.LENGTH_SHORT).show();
        }

    }

    //Helper method to set the favourite icon to on/off depending on current state
    private void setIcon() {
        if (movie != null) {
            MenuItem favButton = menu.findItem(R.id.action_favourite);
            if (isFavourite()) {
                favButton.setIcon(android.R.drawable.btn_star_big_on);
            } else {
                favButton.setIcon(android.R.drawable.btn_star_big_off);
            }
        }
    }

    //Helper method to determine if a movie is already a favourite
    private boolean isFavourite() {
        FavouriteMovie favouriteMovie = mDb.favouriteMovieDao().getFavMovieById(movie.getId());

        if (favouriteMovie != null) {
            return true;
        }
        return false;
    }
}

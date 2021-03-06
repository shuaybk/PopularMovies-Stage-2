package com.example.android.popularmovies_stage2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.content.Intent;


import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies_stage2.DatabaseUtils.AppDatabase;
import com.example.android.popularmovies_stage2.DatabaseUtils.FavouriteMovie;
import com.example.android.popularmovies_stage2.utilities.JsonUtils;
import com.example.android.popularmovies_stage2.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MovieDetails extends AppCompatActivity {

    private static final int BUTTON_TRAILER_FIRST_ID = 1000;
    Movie movie;
    ImageView mFullPoster;
    TextView mDescription;
    TextView mRating;
    TextView mRelease;
    Menu menu;
    ArrayList<Button> trailerButtons;
    LinearLayout trailerLayout;
    LinearLayout reviewLayout;
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

        trailerButtons = new ArrayList<Button>();
        setTrailersAndReviews();

        Picasso.get().load(movie.getPoster()).into(mFullPoster);
        setTitle(movie.getTitle());
        mDescription.setText(movie.getDescription());
        mRating.setText("Avg User Rating: " + movie.getRating() + "%");
        mRelease.setText("Release Date: " + movie.getReleaseDate());
    }

    private void setTrailersAndReviews() {
        URL[] url = new URL[2];
        url[0] = NetworkUtils.getMdbTrailerURL(movie.getId());
        url[1] = NetworkUtils.getReviewsURL(movie.getId());
        new AdditionalMovieDetailsQueryTask().execute(url);
    }

    private void populateTrailerViews(String trailerJson) {
        movie.setTrailerUris(JsonUtils.parseTrailerJson(trailerJson));
        int numTrailers = movie.getTrailerUris().size();
        trailerLayout = (LinearLayout) findViewById(R.id.trailer_fragment_id);

        //Create the buttons for viewing the trailers
        for (int i = 0; i < numTrailers; i++) {
            final int buttonId = BUTTON_TRAILER_FIRST_ID+i;
            Button newButton = new Button(this);
            newButton.setId(buttonId);
            newButton.setText("Play Trailer " + (i+1));
            newButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            newButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int trailerIndex = buttonId - BUTTON_TRAILER_FIRST_ID;
                    launchTrailerIntent(trailerIndex);
                }
            });

            trailerButtons.add(newButton);
            trailerLayout.addView(newButton);
        }
    }

    private void populateReviewViews(String reviewsJson) {
        movie.setReviews(JsonUtils.parseReviewsJson(reviewsJson));
        int numReviews = movie.getReviews().size();

        if (numReviews > 0) {
            reviewLayout = (LinearLayout) findViewById(R.id.review_fragment_id);
            int oneDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());

            LinearLayout.LayoutParams authParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    oneDip);
            authParams.setMargins(10 * oneDip, 10 * oneDip, 10 * oneDip, 0);
            contentParams.setMargins(10 * oneDip, 10 * oneDip, 10 * oneDip, 0);
            dividerParams.setMargins(10 * oneDip, 10 * oneDip, 10 * oneDip, 0);


            //Create text views for each review
            for (int i = 0; i < numReviews; i++) {
                TextView tvAuth = new TextView(this);
                TextView tvContent = new TextView(this);
                tvAuth.setLayoutParams(authParams);
                tvContent.setLayoutParams(contentParams);

                //Create a divider line above this review if it's not the first review
                if (i != 0) {
                    View divider = new View(this);
                    divider.setLayoutParams(dividerParams);
                    divider.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                    reviewLayout.addView(divider);
                }

                tvAuth.setText("User: " + movie.getReviews().get(i)[0]);
                tvAuth.setTypeface(null, Typeface.BOLD);
                tvContent.setText(movie.getReviews().get(i)[1]);

                reviewLayout.addView(tvAuth);
                reviewLayout.addView(tvContent);
            }

            View reviewDivider1 = (View) findViewById(R.id.review_divider1_id);
            View reviewDivider2 = (View) findViewById(R.id.review_divider2_id);
            TextView reviewLabel = (TextView) findViewById(R.id.label_reviews_id);
            reviewDivider1.setVisibility(View.VISIBLE);
            reviewDivider2.setVisibility(View.VISIBLE);
            reviewLabel.setVisibility(View.VISIBLE);
        }
    }

    private void launchTrailerIntent(int trailerIndex) {
        Uri trailerUri = movie.getTrailerUris().get(trailerIndex);
        Intent intent = new Intent(Intent.ACTION_VIEW, trailerUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
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


    public class AdditionalMovieDetailsQueryTask extends AsyncTask<URL, Void, String[]> {

        @Override
        protected String[] doInBackground(URL... params) {
            URL trailersUrl = params[0];
            URL reviewsUrl = params[1];
            String[] queryResults = new String[2];
            try {
                queryResults[0] = NetworkUtils.getResponseFromHttpUrl(trailersUrl);
                queryResults[1] = NetworkUtils.getResponseFromHttpUrl(reviewsUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return queryResults;
        }

        @Override
        protected void onPostExecute(String[] queryResults) {
            if (queryResults[0] == null || queryResults[1] == null) {

            } else {
                populateTrailerViews(queryResults[0]);
                populateReviewViews(queryResults[1]);
            }
        }
    }

}

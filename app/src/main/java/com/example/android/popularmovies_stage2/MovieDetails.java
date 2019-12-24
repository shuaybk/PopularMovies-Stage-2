package com.example.android.popularmovies_stage2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetails extends AppCompatActivity {

    Movie movie;
    ImageView mFullPoster;
    TextView mDescription;
    TextView mRating;
    TextView mRelease;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

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
}

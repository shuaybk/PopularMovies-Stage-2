package com.example.android.popularmovies_stage2;

import android.net.Uri;

import com.example.android.popularmovies_stage2.DatabaseUtils.FavouriteMovie;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

public class Movie implements Serializable {


    private int id;
    private String title;
    private String poster;
    private String description;
    private int rating;  // store as x10 to make it a percentage
    private String releaseDate;
    private ArrayList<Uri> trailerUris;

    public Movie() {

    }

    public Movie(int mId, String mTitle, String mPoster, String mDescription, int mRating, String mReleaseDate, ArrayList<Uri> mTrailerUris) {
        this.id = mId;
        this.title = mTitle;
        this.poster = mPoster;
        this.description = mDescription;
        this.rating = mRating;
        this.releaseDate = mReleaseDate;
        this.trailerUris = mTrailerUris;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public ArrayList<Uri> getTrailerUris() {
        return trailerUris;
    }

    public void setTrailerUris(ArrayList<Uri> trailerUris) {
        this.trailerUris = trailerUris;
    }

    public FavouriteMovie toFavouriteMovie() {
        return new FavouriteMovie(this.id,
                this.title, this.poster, this.description, this.rating, this.releaseDate);
    }


}

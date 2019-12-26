package com.example.android.popularmovies_stage2;

import com.example.android.popularmovies_stage2.DatabaseUtils.FavouriteMovie;

import java.io.Serializable;
import java.util.ArrayList;

public class Movie implements Serializable {


    private int id;
    private String title;
    private String poster;
    private String description;
    private int rating;  // store as x10 to make it a percentage
    private String releaseDate;
    private ArrayList<String> trailerKeys;

    public Movie() {

    }

    public Movie(int mId, String mTitle, String mPoster, String mDescription, int mRating, String mReleaseDate, ArrayList<String> mTrailerKeys) {
        this.id = mId;
        this.title = mTitle;
        this.poster = mPoster;
        this.description = mDescription;
        this.rating = mRating;
        this.releaseDate = mReleaseDate;
        this.trailerKeys = mTrailerKeys;
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

    public ArrayList<String> getTrailerKeys() {
        return trailerKeys;
    }

    public void setTrailerKeys(ArrayList<String> trailerKeys) {
        this.trailerKeys = trailerKeys;
    }

    public FavouriteMovie toFavouriteMovie() {
        return new FavouriteMovie(this.id,
                this.title, this.poster, this.description, this.rating, this.releaseDate);
    }


}

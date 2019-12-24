package com.example.android.popularmovies_stage2;

import java.io.Serializable;

public class Movie implements Serializable {



    private String title;
    private String poster;
    private String description;
    private int rating;  // store as x10 to make it a percentage
    private String releaseDate;

    public Movie() {

    }

    public Movie(String mTitle, String mPoster, String mDescription, int mRating, String mReleaseDate) {
        this.title = mTitle;
        this.poster = mPoster;
        this.description = mDescription;
        this.rating = mRating;
        this.releaseDate = mReleaseDate;
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


}

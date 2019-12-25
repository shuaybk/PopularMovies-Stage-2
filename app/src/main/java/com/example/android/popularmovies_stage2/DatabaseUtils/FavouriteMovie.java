package com.example.android.popularmovies_stage2.DatabaseUtils;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "favouriteMovie")
public class FavouriteMovie {

    @PrimaryKey
    private int id;
    private String title;
    private String poster;
    private String description;
    private int rating;  // store as x10 to make it a percentage
    private String releaseDate;


    public FavouriteMovie(int id, String title, String poster, String description, int rating, String releaseDate) {
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.description = description;
        this.rating = rating;
        this.releaseDate = releaseDate;
    }

    ////////////////////////////////////
    ////  GETTERS AND SETTERS HERE  ////
    ////////////////////////////////////
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

}

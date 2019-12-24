package com.example.android.popularmovies_stage2.utilities;

import com.example.android.popularmovies_stage2.MainActivity;
import com.example.android.popularmovies_stage2.Movie;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    public static final String IMAGE_SIZE = "w" + MainActivity.IMAGE_WIDTH;

    public static Movie parseMovieJson (JSONObject json) {
        Movie movie = null;
        try {
            String title = json.getString("original_title");
            String poster = BASE_IMAGE_URL + IMAGE_SIZE + json.getString("poster_path");
            String description = json.getString("overview");
            int rating = (int)(json.getDouble("vote_average")*10);
            String releaseDate = json.getString("release_date");

            movie = new Movie(title, poster, description, rating, releaseDate);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return movie;
    }
}

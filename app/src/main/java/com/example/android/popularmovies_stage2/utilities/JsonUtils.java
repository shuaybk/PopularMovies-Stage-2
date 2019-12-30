package com.example.android.popularmovies_stage2.utilities;

import android.net.Uri;

import com.example.android.popularmovies_stage2.MainActivity;
import com.example.android.popularmovies_stage2.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class JsonUtils {

    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    public static final String IMAGE_SIZE = "w" + MainActivity.IMAGE_WIDTH;
    public static final String TRAILER_TYPE_KEY = "type";
    public static final String TRAILER_SITE_KEY = "site";
    public static final String TRAILER_KEY_KEY = "key";
    public static final int MAX_NUMBER_TRAILERS = 3;


    public static Movie parseMovieJson (JSONObject json) {
        Movie movie = null;
        try {
            int id = json.getInt("id");
            String title = json.getString("original_title");
            String poster = BASE_IMAGE_URL + IMAGE_SIZE + json.getString("poster_path");
            String description = json.getString("overview");
            int rating = (int)(json.getDouble("vote_average")*10);
            String releaseDate = json.getString("release_date");

            movie = new Movie(id, title, poster, description, rating, releaseDate, null);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return movie;
    }

    public static ArrayList<Uri> parseTrailerJson (String trailerJson) {
        ArrayList<Uri> trailerUris = new ArrayList<Uri>();

        if (trailerJson != null) {
            try {
                JSONObject jsonData = new JSONObject(trailerJson);
                JSONArray jsonTrailerList = jsonData.getJSONArray("results");

                for (int i = 0; i < jsonTrailerList.length(); i++) {
                    if (trailerUris.size() < MAX_NUMBER_TRAILERS) {
                        JSONObject currTrailer = jsonTrailerList.getJSONObject(i);
                        if (currTrailer.getString(TRAILER_TYPE_KEY).equals("Trailer") && currTrailer.getString(TRAILER_SITE_KEY).equals("YouTube")) {
                            Uri uri = NetworkUtils.getYoutubeTrailerUri(currTrailer.getString((TRAILER_KEY_KEY)));
                            trailerUris.add(uri);
                        }
                    } else {
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return trailerUris;
    }
}

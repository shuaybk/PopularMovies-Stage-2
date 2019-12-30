package com.example.android.popularmovies_stage2.utilities;

import android.net.Uri;

import com.example.android.popularmovies_stage2.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    final static String MOVIEDB_BASE_URL = "https://api.themoviedb.org/3/movie/";
    final static String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";
    final static String MOVIEDB_URL_POPULAR = "popular";
    final static String MOVIEDB_URL_RATING = "top_rated";
    final static String MOVIEDB_URL_VIDEO = "/videos";
    final static String MOVIEDB_URL_REVIEW = "/reviews";
    final static String PARAM_API_KEY = "api_key";
    final static String PARAM_LANGUAGE = "language";
    final static String PARAM_PAGES = "page";

    final static String lang = "en-US";
    final static String pagesToDisplay = "1";


    public static URL getMovieURL(String sortBy) {

        String base_url = MOVIEDB_BASE_URL;
        if (sortBy.equals(MainActivity.SORT_TYPE_POPULAR)) {
            base_url += MOVIEDB_URL_POPULAR;
        } else if (sortBy.equals(MainActivity.SORT_TYPE_RATING)) {
            base_url += MOVIEDB_URL_RATING;
        }

        Uri builtUri = Uri.parse(base_url).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, MainActivity.API_KEY)
                .appendQueryParameter(PARAM_LANGUAGE, lang)
                .appendQueryParameter(PARAM_PAGES, pagesToDisplay)
                .build();

        return getUrl(builtUri);
    }

    //The id is the movie ID we want the trailer for
    public static URL getMdbTrailerURL(int id) {
        String base_url = MOVIEDB_BASE_URL + id + MOVIEDB_URL_VIDEO;

        Uri builtUri = Uri.parse(base_url).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, MainActivity.API_KEY)
                .appendQueryParameter(PARAM_LANGUAGE, lang)
                .build();

        return getUrl(builtUri);
    }

    public static URL getReviewsURL(int id) {
        String base_url = MOVIEDB_BASE_URL + id + MOVIEDB_URL_REVIEW;

        Uri builtUri = Uri.parse(base_url).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, MainActivity.API_KEY)
                .appendQueryParameter(PARAM_LANGUAGE, lang)
                .appendQueryParameter(PARAM_PAGES, pagesToDisplay)
                .build();

        return getUrl(builtUri);
    }

    public static Uri getYoutubeTrailerUri(String id) {
        String base_url = YOUTUBE_BASE_URL + id;

        Uri builtUri = Uri.parse(base_url).buildUpon().build();

        return builtUri;
    }

    private static URL getUrl(Uri builtUri) {
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println("HTTP CONNNECTION ERRRRRRRRRRRRRRRRROR");
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return null;
    }
}

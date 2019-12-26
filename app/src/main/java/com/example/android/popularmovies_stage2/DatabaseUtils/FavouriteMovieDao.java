package com.example.android.popularmovies_stage2.DatabaseUtils;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.android.popularmovies_stage2.Movie;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface FavouriteMovieDao {

    @Query("SELECT * FROM favouriteMovie")
    LiveData<List<FavouriteMovie>> loadAllFavMovies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavMovie(FavouriteMovie favouriteMovie);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFavMovie(FavouriteMovie favouriteMovie);

    @Delete
    void deleteFavMovie(FavouriteMovie favouriteMovie);

    @Query("SELECT * FROM favouriteMovie WHERE id = :id")
    FavouriteMovie getFavMovieById(int id);

    @Query("SELECT * FROM favouriteMovie WHERE id = :id")
    LiveData<FavouriteMovie> getFavMovieById2(int id);

}

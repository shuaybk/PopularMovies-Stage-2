package com.example.android.popularmovies_stage2.DatabaseUtils;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {FavouriteMovie.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "popularmovies";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        ///REMOVE THIS, ITS FOR TESTING ONLY
                        //MAKE THE DATABASE QUERIES ON A SEPARATE THREAD FOR FINAL VERSION
                        .allowMainThreadQueries()
                        .build();
            }
        }
        return sInstance;
    }

    public abstract FavouriteMovieDao favouriteMovieDao();

}

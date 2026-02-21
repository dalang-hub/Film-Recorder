package com.ZhouJason.filmrecorder;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Shot.class, FilmRoll.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ShotDao shotDao();      // 现在编译器能找到 ShotDao 了
    public abstract FilmRollDao filmRollDao(); // 现在也能找到 FilmRollDao 了

    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "film_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}

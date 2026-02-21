package com.ZhouJason.filmrecorder;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface FilmRollDao {
    @Insert
    void insert(FilmRoll roll);

    @Query("SELECT * FROM film_rolls")
    List<FilmRoll> getAllRolls();

    @Query("SELECT * FROM film_rolls ORDER BY lastModifiedTime DESC")
    List<FilmRoll> getAllRollsOrderByLastModifiedDesc();

    @Query("SELECT * FROM film_rolls ORDER BY lastModifiedTime ASC")
    List<FilmRoll> getAllRollsOrderByLastModifiedAsc();

    @Query("UPDATE film_rolls SET shotCount = :count WHERE name = :rollName")
    void updateShotCount(String rollName, int count);

    @Query("UPDATE film_rolls SET lastModifiedTime = :time WHERE name = :rollName")
    void updateLastModifiedTime(String rollName, long time);

    @Query("SELECT * FROM shots WHERE rollName = :rollName ORDER BY timestamp DESC LIMIT 1")
    Shot getLatestShot(String rollName);

    @Delete
    void delete(FilmRoll roll);
}

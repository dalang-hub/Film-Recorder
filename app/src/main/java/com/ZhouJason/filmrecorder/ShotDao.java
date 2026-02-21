package com.ZhouJason.filmrecorder;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface ShotDao {
    @Insert
    void insert(Shot shot);

    @Query("SELECT * FROM shots WHERE rollName = :rollName")
    List<Shot> getShotsByRoll(String rollName);

    @Delete
    void delete(Shot shot);

    @Query("DELETE FROM shots WHERE rollName = :rollName")
    void deleteShotsByRoll(String rollName);
    
    @Update
    void update(Shot shot);
}

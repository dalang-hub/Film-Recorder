package com.ZhouJason.filmrecorder;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "film_rolls")
public class FilmRoll {
    @PrimaryKey
    @NonNull
    public String name;
    public int iso;
    public int shotCount;
    public long createTime;
    public long lastModifiedTime;

    public FilmRoll(@NonNull String name, int iso) {
        this.name = name;
        this.iso = iso;
        this.shotCount = 0;
        this.createTime = System.currentTimeMillis();
        this.lastModifiedTime = this.createTime;
    }
}
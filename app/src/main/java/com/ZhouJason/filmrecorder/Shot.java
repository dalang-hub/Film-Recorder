package com.ZhouJason.filmrecorder;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shots")
public class Shot {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String rollName;
    public String imagePath;
    public String aperture;
    public String shutter;
    public String note;

    // --- 新增这两个字段 ---
    public double latitude;  // 纬度
    public double longitude; // 经度

    // --- 新增时间戳字段 ---
    public long timestamp; // 拍摄时间（毫秒级时间戳）

    public Shot(String rollName, String imagePath, String aperture, String shutter, String note, double latitude, double longitude) {
        this.rollName = rollName;
        this.imagePath = imagePath;
        this.aperture = aperture;
        this.shutter = shutter;
        this.note = note;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = System.currentTimeMillis(); // 自动记录当前时间
    }
}
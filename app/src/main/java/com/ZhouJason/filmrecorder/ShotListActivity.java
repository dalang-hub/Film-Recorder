package com.ZhouJason.filmrecorder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShotListActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String currentRollName;
    private Bitmap tempBitmap;

    private RecyclerView rvShots;
    private ShotAdapter shotAdapter;
    private List<Shot> shotList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shot_list);

        currentRollName = getIntent().getStringExtra("rollName");
        TextView title = findViewById(R.id.txtCurrentRollName);
        TextView createTime = findViewById(R.id.txtCreateTime);
        if (currentRollName != null) {
            title.setText("当前胶卷：" + currentRollName);
            // 加载创建时间
            new Thread(() -> {
                FilmRoll roll = AppDatabase.getInstance(this).filmRollDao().getAllRolls().stream()
                    .filter(r -> r.name.equals(currentRollName))
                    .findFirst()
                    .orElse(null);
                if (roll != null) {
                    String timeStr = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
                        .format(new java.util.Date(roll.createTime));
                    runOnUiThread(() -> {
                        createTime.setText("创建时间：" + timeStr);
                    });
                }
            }).start();
        }

        rvShots = findViewById(R.id.rvShots);
        rvShots.setLayoutManager(new LinearLayoutManager(this));

        // 绑定删除和编辑逻辑
        shotAdapter = new ShotAdapter(shotList, this::showDeleteConfirmDialog, this::showEditDialog);
        rvShots.setAdapter(shotAdapter);

        findViewById(R.id.btnTakeShot).setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        });

        loadShotsFromDatabase();
    }

    private void showDeleteConfirmDialog(Shot shot) {
        new AlertDialog.Builder(this)
                .setTitle("删除记录")
                .setMessage("确定删除吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    new Thread(() -> {
                        AppDatabase db = AppDatabase.getInstance(this);
                        if (shot.imagePath != null) new File(shot.imagePath).delete();
                        db.shotDao().delete(shot);
                        // 更新胶卷的 shotCount 和 lastModifiedTime
                        List<Shot> shots = db.shotDao().getShotsByRoll(currentRollName);
                        db.filmRollDao().updateShotCount(currentRollName, shots.size());
                        db.filmRollDao().updateLastModifiedTime(currentRollName, System.currentTimeMillis());
                        runOnUiThread(() -> {
                            Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show();
                            loadShotsFromDatabase();
                        });
                    }).start();
                })
                .setNegativeButton("取消", null).show();
    }
    
    private void showEditDialog(Shot shot) {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_add_shot, null);
        EditText editAp = v.findViewById(R.id.editShotAperture);
        EditText editSh = v.findViewById(R.id.editShotShutter);
        EditText editNt = v.findViewById(R.id.editShotNote);
        ImageView imgPreview = v.findViewById(R.id.imgShotPreview);
        
        // 显示照片
        if (shot.imagePath != null && !shot.imagePath.isEmpty()) {
            imgPreview.setVisibility(View.VISIBLE);
            imgPreview.setImageBitmap(android.graphics.BitmapFactory.decodeFile(shot.imagePath));
        }
        
        // 预填现有值
        String existingAperture = shot.aperture != null ? shot.aperture.replace("f/", "") : "";
        editAp.setText(existingAperture);
        editSh.setText(shot.shutter != null ? shot.shutter : "");
        editNt.setText(shot.note != null ? shot.note : "");
        
        new AlertDialog.Builder(this)
                .setTitle("编辑参数")
                .setView(v)
                .setPositiveButton("保存", (dialog, which) -> {
                    String aperture = editAp.getText().toString();
                    String shutter = editSh.getText().toString();
                    String note = editNt.getText().toString();
                    
                    new Thread(() -> {
                        // 更新数据库
                        Shot updatedShot = new Shot(
                            shot.rollName,
                            shot.imagePath,
                            aperture,
                            shutter,
                            note,
                            shot.latitude,
                            shot.longitude
                        );
                        updatedShot.id = shot.id; // 保持ID不变
                        updatedShot.timestamp = shot.timestamp; // 保持原时间戳
                        
                        AppDatabase.getInstance(this).shotDao().update(updatedShot);
                        
                        // 更新胶卷的 lastModifiedTime
                        AppDatabase.getInstance(this).filmRollDao().updateLastModifiedTime(currentRollName, System.currentTimeMillis());
                        
                        runOnUiThread(() -> {
                            Toast.makeText(this, "已更新", Toast.LENGTH_SHORT).show();
                            loadShotsFromDatabase();
                        });
                    }).start();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            tempBitmap = (Bitmap) data.getExtras().get("data");
            showDetailsDialog();
        }
    }

    @SuppressWarnings("MissingPermission") // 强行关闭 Lint 权限检查报错
    private void showDetailsDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_add_shot, null);
        EditText editAp = v.findViewById(R.id.editShotAperture);
        EditText editSh = v.findViewById(R.id.editShotShutter);
        EditText editNt = v.findViewById(R.id.editShotNote);

        new AlertDialog.Builder(this)
                .setTitle("填写参数").setView(v)
                .setPositiveButton("保存", (dialog, which) -> {
                    final String path = saveImageToInternalStorage(tempBitmap);
                    double lat = 0, lon = 0;
                    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Location loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (loc != null) { lat = loc.getLatitude(); lon = loc.getLongitude(); }
                    }

                    final double fLat = lat, fLon = lon;
                    final String aperture = editAp.getText().toString();
                    final String shutter = editSh.getText().toString();
                    final String note = editNt.getText().toString();
                    new Thread(() -> {
                        AppDatabase db = AppDatabase.getInstance(this);
                        Shot newShot = new Shot(
                                currentRollName,
                                path,
                                aperture,
                                shutter,
                                note,
                                fLat,
                                fLon
                        );
                    db.shotDao().insert(newShot);
                    
                    // 更新胶卷的 shotCount 和 lastModifiedTime
                    List<Shot> shots = db.shotDao().getShotsByRoll(currentRollName);
                    db.filmRollDao().updateShotCount(currentRollName, shots.size());
                    db.filmRollDao().updateLastModifiedTime(currentRollName, System.currentTimeMillis());
                        
                        runOnUiThread(() -> {
                            Toast.makeText(this, "记录成功！", Toast.LENGTH_SHORT).show();
                            loadShotsFromDatabase();
                        });
                    }).start();
                }).show();
    }

    private void loadShotsFromDatabase() {
        new Thread(() -> {
            List<Shot> saved = AppDatabase.getInstance(this).shotDao().getShotsByRoll(currentRollName);
            runOnUiThread(() -> {
                shotList.clear(); shotList.addAll(saved);
                shotAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    private String saveImageToInternalStorage(Bitmap bitmap) {
        String name = UUID.randomUUID().toString() + ".jpg";
        File file = new File(getFilesDir(), name);
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            return file.getAbsolutePath();
        } catch (Exception e) { return ""; }
    }
}

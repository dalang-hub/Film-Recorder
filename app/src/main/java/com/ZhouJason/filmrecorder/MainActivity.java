package com.ZhouJason.filmrecorder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvFilmRolls;
    private Button btnAddRoll;
    private Button btnSort;
    private List<FilmRoll> rollList = new ArrayList<>(); // 确保这里初始化了
    private FilmRollAdapter adapter;
    private boolean isSortAscending = false; // 默认从旧到新（升序）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvFilmRolls = findViewById(R.id.rvFilmRolls);
        btnAddRoll = findViewById(R.id.btnAddRoll);
        btnSort = findViewById(R.id.btnSort);

        rvFilmRolls.setLayoutManager(new LinearLayoutManager(this));

        // 适配器逻辑：点击进入，点击垃圾桶删除
        adapter = new FilmRollAdapter(rollList, new FilmRollAdapter.OnRollClickListener() {
            @Override
            public void onRollClick(FilmRoll roll) {
                Intent intent = new Intent(MainActivity.this, ShotListActivity.class);
                intent.putExtra("rollName", roll.name);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(FilmRoll roll) {
                showDeleteConfirmDialog(roll);
            }
        });
        rvFilmRolls.setAdapter(adapter);

        btnAddRoll.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddRollActivity.class);
            startActivityForResult(intent, 100);
        });

        btnSort.setOnClickListener(v -> {
            isSortAscending = !isSortAscending;
            btnSort.setText(isSortAscending ? "时间↓" : "时间↑");
            loadRollsFromDatabase();
        });

        loadRollsFromDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 返回MainActivity时刷新数据，更新shotCount
        loadRollsFromDatabase();
    }

    // MainActivity.java 里的关键删除方法
    private void showDeleteConfirmDialog(FilmRoll roll) {
        new AlertDialog.Builder(this)
                .setTitle("删除胶卷")
                .setMessage("确定删除 \"" + roll.name + "\" 吗？")
                .setPositiveButton("彻底删除", (dialog, which) -> {
                    new Thread(() -> {
                        AppDatabase db = AppDatabase.getInstance(this);
                        
                        // 1. 先查询该卷的所有照片，删除本地图片文件
                        List<Shot> shots = db.shotDao().getShotsByRoll(roll.name);
                        for (Shot shot : shots) {
                            if (shot.imagePath != null && !shot.imagePath.isEmpty()) {
                                new File(shot.imagePath).delete();
                            }
                        }
                        
                        // 2. 删除所有照片记录
                        db.shotDao().deleteShotsByRoll(roll.name);
                        
                        // 3. 删除胶卷记录
                        db.filmRollDao().delete(roll);
                        
                        // 4. 重新加载 UI
                        runOnUiThread(this::loadRollsFromDatabase);
                    }).start();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void loadRollsFromDatabase() {
        new Thread(() -> {
            List<FilmRoll> saved;
            if (isSortAscending) {
                saved = AppDatabase.getInstance(this).filmRollDao().getAllRollsOrderByLastModifiedAsc();
            } else {
                saved = AppDatabase.getInstance(this).filmRollDao().getAllRollsOrderByLastModifiedDesc();
            }
            runOnUiThread(() -> {
                rollList.clear();
                rollList.addAll(saved);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            loadRollsFromDatabase();
        }
    }
}

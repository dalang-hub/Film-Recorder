package com.ZhouJason.filmrecorder;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddRollActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_roll);

        EditText nameInput = findViewById(R.id.editNewRollName);
        EditText isoInput = findViewById(R.id.editNewRollIso);
        Button btn = findViewById(R.id.btnConfirmAdd);

        btn.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String isoStr = isoInput.getText().toString().trim();

            if (name.isEmpty() || isoStr.isEmpty()) {
                Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int iso = Integer.parseInt(isoStr);
                if (iso < 1 || iso > 10000) {
                    Toast.makeText(this, "ISO值必须在1-10000之间", Toast.LENGTH_SHORT).show();
                    return;
                }

                FilmRoll newRoll = new FilmRoll(name, iso);

                // 直接存入数据库
                new Thread(() -> {
                    AppDatabase.getInstance(this).filmRollDao().insert(newRoll);
                    runOnUiThread(() -> {
                        setResult(RESULT_OK);
                        finish(); // 存完直接关掉
                    });
                }).start();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "ISO值必须是有效的数字", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
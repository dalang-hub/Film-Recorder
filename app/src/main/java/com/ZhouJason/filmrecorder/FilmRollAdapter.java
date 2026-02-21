package com.ZhouJason.filmrecorder;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Random;

public class FilmRollAdapter extends RecyclerView.Adapter<FilmRollAdapter.ViewHolder> {
    private List<FilmRoll> mData;
    private OnRollClickListener clickListener;
    private AppDatabase db;
    private Handler mainHandler;
    private Random random;
    
    private static final String[] EMOJI_LIST = {"📷", "🎞️", "📸", "🎬", "🖼️", "🎨", "🌈", "✨", "🌟", "💫"};

    public interface OnRollClickListener {
        void onRollClick(FilmRoll roll);
        void onDeleteClick(FilmRoll roll);
    }

    public FilmRollAdapter(List<FilmRoll> data, OnRollClickListener listener) {
        this.mData = data;
        this.clickListener = listener;
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.random = new Random();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName, txtRollInfo, txtLastModifiedTime;
        public ImageView btnDelete, imgPreview;
        public ViewHolder(View v) {
            super(v);
            txtName = v.findViewById(R.id.txtRollName);
            txtLastModifiedTime = v.findViewById(R.id.txtLastModifiedTime);
            txtRollInfo = v.findViewById(R.id.txtRollInfo);
            btnDelete = v.findViewById(R.id.btnDeleteRoll);
            imgPreview = v.findViewById(R.id.imgRollPreview);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_film_roll, parent, false);
        if (db == null) {
            db = AppDatabase.getInstance(parent.getContext());
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FilmRoll roll = mData.get(position);
        holder.txtName.setText(roll.name);

        // 显示最新修改时间
        String timeStr = new java.text.SimpleDateFormat("MM-dd HH:mm", java.util.Locale.getDefault())
            .format(new java.util.Date(roll.lastModifiedTime));
        holder.txtLastModifiedTime.setText(timeStr);

        holder.txtRollInfo.setText("ISO " + roll.iso + " | 已拍 " + roll.shotCount + " 张");

        // 加载预览图或emoji
        loadPreviewImage(holder.imgPreview, roll.name, roll.shotCount);

        // 点击整行进入照片列表
        holder.itemView.setOnClickListener(v -> clickListener.onRollClick(roll));

        // 点击垃圾桶触发删除
        holder.btnDelete.setOnClickListener(v -> clickListener.onDeleteClick(roll));
    }

    private void loadPreviewImage(ImageView imageView, String rollName, int shotCount) {
        if (shotCount == 0) {
            // 空卷，显示随机emoji
            String emoji = EMOJI_LIST[random.nextInt(EMOJI_LIST.length)];
            Bitmap emojiBitmap = createEmojiBitmap(emoji, 200, 200);
            imageView.setImageBitmap(emojiBitmap);
        } else {
            // 在后台线程查询最新照片
            new Thread(() -> {
                Shot latestShot = db.filmRollDao().getLatestShot(rollName);
                if (latestShot != null && latestShot.imagePath != null) {
                    mainHandler.post(() -> {
                        imageView.setImageBitmap(android.graphics.BitmapFactory.decodeFile(latestShot.imagePath));
                    });
                } else {
                    // 没有照片，显示emoji
                    String emoji = EMOJI_LIST[random.nextInt(EMOJI_LIST.length)];
                    Bitmap emojiBitmap = createEmojiBitmap(emoji, 200, 200);
                    mainHandler.post(() -> {
                        imageView.setImageBitmap(emojiBitmap);
                    });
                }
            }).start();
        }
    }

    private Bitmap createEmojiBitmap(String emoji, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.parseColor("#EEEEEE"));
        
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(Math.min(width, height) * 0.6f);
        paint.setTextAlign(Paint.Align.CENTER);
        
        Rect bounds = new Rect();
        paint.getTextBounds(emoji, 0, emoji.length(), bounds);
        
        float x = width / 2f;
        float y = height / 2f + bounds.height() / 2f - bounds.bottom;
        canvas.drawText(emoji, x, y, paint);
        
        return bitmap;
    }

    @Override
    public int getItemCount() { return mData.size(); }
}

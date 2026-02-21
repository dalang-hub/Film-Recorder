package com.ZhouJason.filmrecorder;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ShotAdapter extends RecyclerView.Adapter<ShotAdapter.ViewHolder> {
    private List<Shot> mData;
    private OnDeleteClickListener deleteListener;
    private OnItemClickListener itemClickListener;
    private SimpleDateFormat dateFormat;

    public interface OnDeleteClickListener {
        void onDeleteClick(Shot shot);
    }
    
    public interface OnItemClickListener {
        void onItemClick(Shot shot);
    }

    public ShotAdapter(List<Shot> data, OnDeleteClickListener deleteListener, OnItemClickListener itemClickListener) {
        this.mData = data;
        this.deleteListener = deleteListener;
        this.itemClickListener = itemClickListener;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgShot, btnDelete;
        public TextView txtParams, txtNote, txtTime;
        public View itemView;
        public ViewHolder(View v) {
            super(v);
            itemView = v;
            imgShot = v.findViewById(R.id.imgShotPreview);
            txtParams = v.findViewById(R.id.txtShotParams);
            txtTime = v.findViewById(R.id.txtShotTime);
            txtNote = v.findViewById(R.id.txtShotNote);
            btnDelete = v.findViewById(R.id.btnDeleteShot);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shot, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Shot shot = mData.get(position);
        
        // 显示光圈（带f/前缀）和快门
        String apertureText = shot.aperture != null && !shot.aperture.isEmpty() ? "f/" + shot.aperture : "";
        String shutterText = shot.shutter != null && !shot.shutter.isEmpty() ? shot.shutter : "";
        
        if (!apertureText.isEmpty() && !shutterText.isEmpty()) {
            holder.txtParams.setText(apertureText + " | " + shutterText);
        } else if (!apertureText.isEmpty()) {
            holder.txtParams.setText(apertureText);
        } else if (!shutterText.isEmpty()) {
            holder.txtParams.setText(shutterText);
        } else {
            holder.txtParams.setText("");
        }
        
        holder.txtNote.setText(shot.note);
        
        // 显示拍摄时间
        if (shot.timestamp > 0) {
            holder.txtTime.setText(dateFormat.format(new Date(shot.timestamp)));
            holder.txtTime.setVisibility(View.VISIBLE);
        } else {
            holder.txtTime.setVisibility(View.GONE);
        }

        if (shot.imagePath != null) {
            holder.imgShot.setImageBitmap(BitmapFactory.decodeFile(shot.imagePath));
        }

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onDeleteClick(shot);
        });
        
        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) itemClickListener.onItemClick(shot);
        });
    }

    @Override
    public int getItemCount() { return mData.size(); }
}
package com.ZhouJason.filmrecorder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import java.util.*;
import java.io.File;

public class MapActivity extends AppCompatActivity {
    
    private MapView mapView;
    private TextView txtFilterLabel;
    private String currentFilter = "全部";
    private List<FilmRoll> allRolls = new ArrayList<>();
    private List<Marker> allMarkers = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_map);
        
        setupToolbar();
        setupMapView();
        loadFilmRolls();
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("照片地图");
        }
        
        txtFilterLabel = findViewById(R.id.txtFilterLabel);
        txtFilterLabel.setOnClickListener(v -> showFilterMenu());
    }
    
    private void setupMapView() {
        mapView = findViewById(R.id.mapView);
        mapView.setUseDataConnection(true);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(12.0);
        mapView.setMinZoomLevel(3.0);
        mapView.setMaxZoomLevel(19.0);
        
        findViewById(R.id.btnZoomIn).setOnClickListener(v -> {
            mapView.getController().zoomIn();
        });
        
        findViewById(R.id.btnZoomOut).setOnClickListener(v -> {
            mapView.getController().zoomOut();
        });
    }
    
    private void loadFilmRolls() {
        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(this);
                allRolls = db.filmRollDao().getAllRollsOrderByLastModifiedDesc();
                runOnUiThread(() -> loadPhotoLocations());
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "加载胶片卷失败", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
    
    private void loadPhotoLocations() {
        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(this);
                List<Shot> filteredShots = new ArrayList<>();
                
                List<Shot> finalShots;
                if (currentFilter.equals("全部")) {
                    for (FilmRoll roll : allRolls) {
                        List<Shot> shots = db.shotDao().getShotsByRoll(roll.name);
                        filteredShots.addAll(shots);
                    }
                    finalShots = filteredShots;
                } else {
                    finalShots = db.shotDao().getShotsByRoll(currentFilter);
                }
                
                runOnUiThread(() -> refreshMapMarkers(finalShots));
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "加载照片位置失败", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
    
    private void refreshMapMarkers(List<Shot> shots) {
        mapView.getOverlays().removeAll(allMarkers);
        allMarkers.clear();
        
        if (shots.isEmpty()) {
            Toast.makeText(this, "暂无照片位置信息", Toast.LENGTH_SHORT).show();
            return;
        }
        
        List<GeoPoint> validPoints = new ArrayList<>();
        
        for (Shot shot : shots) {
            if (shot.latitude != 0 && shot.longitude != 0) {
                GeoPoint point = new GeoPoint(shot.latitude, shot.longitude);
                validPoints.add(point);
                
                Marker marker = new Marker(mapView);
                marker.setPosition(point);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                marker.setInfoWindow(null);
                
                String title = shot.rollName;
                String snippet = String.format("光圈: %s | 快门: %s%n%s",
                    shot.aperture != null ? shot.aperture : "未设置",
                    shot.shutter != null ? shot.shutter : "未设置",
                    shot.note != null && !shot.note.isEmpty() ? shot.note : "无备注");
                
                marker.setTitle(title);
                marker.setSnippet(snippet);
                
                if (shot.imagePath != null && !shot.imagePath.isEmpty()) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeFile(shot.imagePath);
                        if (bitmap != null) {
                            Bitmap photoWithPin = createPinDrawable(bitmap);
                            marker.setIcon(new BitmapDrawable(getResources(), photoWithPin));
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        } else {
                            marker.setIcon(getDefaultDrawable());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        marker.setIcon(getDefaultDrawable());
                    }
                } else {
                    marker.setIcon(getDefaultDrawable());
                }
                
                mapView.getOverlays().add(marker);
                allMarkers.add(marker);
            }
        }
        
        if (!validPoints.isEmpty()) {
            double centerLat = 0, centerLon = 0;
            for (GeoPoint point : validPoints) {
                centerLat += point.getLatitude();
                centerLon += point.getLongitude();
            }
            centerLat /= validPoints.size();
            centerLon /= validPoints.size();
            
            mapView.getController().animateTo(new GeoPoint(centerLat, centerLon));
        }
        
        mapView.invalidate();
    }
    
    private void showFilterMenu() {
        PopupMenu popupMenu = new PopupMenu(this, txtFilterLabel);
        
        popupMenu.getMenu().add(0, 0, 0, "全部");
        
        for (int i = 0; i < allRolls.size(); i++) {
            popupMenu.getMenu().add(0, i + 1, 0, allRolls.get(i).name);
        }
        
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            String selectedFilter;
            
            if (itemId == 0) {
                selectedFilter = "全部";
            } else {
                selectedFilter = allRolls.get(itemId - 1).name;
            }
            
            currentFilter = selectedFilter;
            txtFilterLabel.setText(currentFilter + " 〱");
            
            loadPhotoLocations();
            
            return true;
        });
        
        popupMenu.show();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private Bitmap createPinDrawable(Bitmap photoBitmap) {
        int size = 110;
        int circleSize = 80;
        int borderWidth = 3;
        
        Bitmap result = Bitmap.createBitmap(size, size + 25, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        
        int centerX = size / 2;
        int centerY = circleSize / 2 + 10;
        float radius = circleSize / 2;
        
        paint.setColor(0xFFFFFFFF);
        canvas.drawCircle(centerX, centerY, radius + borderWidth, paint);
        
        Bitmap scaledPhoto = Bitmap.createScaledBitmap(photoBitmap, circleSize - borderWidth * 2, circleSize - borderWidth * 2, true);
        
        paint.setColor(0xFF000000);
        canvas.drawCircle(centerX, centerY, radius - borderWidth, paint);
        
        BitmapShader shader = new BitmapShader(scaledPhoto, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        canvas.drawCircle(centerX, centerY, radius - borderWidth * 2, paint);
        paint.setShader(null);
        
        Path path = new Path();
        path.moveTo(centerX - 15, centerY + radius - borderWidth);
        path.lineTo(centerX, centerY + radius - borderWidth + 20);
        path.lineTo(centerX + 15, centerY + radius - borderWidth);
        path.close();
        
        paint.setColor(0xFFFFFFFF);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, paint);
        
        paint.setColor(0xFFE0E0E0);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderWidth);
        canvas.drawCircle(centerX, centerY, radius, paint);
        
        if (photoBitmap != scaledPhoto) {
            scaledPhoto.recycle();
        }
        
        return result;
    }
    
    private Drawable getDefaultDrawable() {
        Bitmap bitmap = Bitmap.createBitmap(110, 135, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        
        int centerX = 55;
        int centerY = 50;
        float radius = 40;
        int borderWidth = 3;
        
        paint.setColor(0xFFFFFFFF);
        canvas.drawCircle(centerX, centerY, radius, paint);
        
        paint.setColor(0xFF3D5AFE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(centerX, centerY, radius - borderWidth, paint);
        
        paint.setColor(0xFFFFFFFF);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(40);
        canvas.drawText("📷", centerX, centerY + 14, paint);
        
        Path path = new Path();
        path.moveTo(centerX - 15, centerY + radius - borderWidth);
        path.lineTo(centerX, centerY + radius - borderWidth + 20);
        path.lineTo(centerX + 15, centerY + radius - borderWidth);
        path.close();
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, paint);
        
        return new BitmapDrawable(getResources(), bitmap);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDetach();
    }
}
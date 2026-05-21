package com.diploma.aerodent.ui.photos;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.Photo;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

public class FullScreenPhotoActivity extends AppCompatActivity {

    public static final String EXTRA_PHOTO_ID = "extra_photo_id";
    private int photoId;
    private Photo currentPhoto;
    private PhotoViewModel photoViewModel;

    private PhotoView photoView;
    private LinearLayout uiContainer;
    private boolean isUiVisible = true;
    private WindowInsetsControllerCompat windowInsetsController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_photo);

        if (getIntent() != null && getIntent().hasExtra(EXTRA_PHOTO_ID)) {
            photoId = getIntent().getIntExtra(EXTRA_PHOTO_ID, -1);
        } else {
            finish();
            return;
        }

        photoViewModel = new ViewModelProvider(this).get(PhotoViewModel.class);
        photoView = findViewById(R.id.image_full_screen);
        uiContainer = findViewById(R.id.ui_container);

        windowInsetsController = ViewCompat.getWindowInsetsController(getWindow().getDecorView());

        ViewCompat.setOnApplyWindowInsetsListener(uiContainer, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        photoViewModel.getPhotoById(photoId).observe(this, photo -> {
            if (photo != null) {
                this.currentPhoto = photo;
                if (photo.getFilePath() != null) {
                    Glide.with(this)
                            .load(new File(photo.getFilePath()))
                            .into(photoView);
                }
            } else {
                Toast.makeText(this, R.string.photo_deleted_success, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        findViewById(R.id.btn_close).setOnClickListener(v -> finish());
        findViewById(R.id.btn_delete_photo).setOnClickListener(v -> showDeleteConfirmation());

        photoView.setOnClickListener(v -> toggleUi());
    }

    private void toggleUi() {
        if (isUiVisible) {
            hideUi();
        } else {
            showUi();
        }
    }

    private void hideUi() {
        isUiVisible = false;
        uiContainer.animate().alpha(0f).setDuration(200).withEndAction(() -> uiContainer.setVisibility(View.GONE));
        if (windowInsetsController != null) {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
            windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }

    private void showUi() {
        isUiVisible = true;
        uiContainer.setVisibility(View.VISIBLE);
        uiContainer.animate().alpha(1f).setDuration(200);
        if (windowInsetsController != null) {
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars());
        }
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete)
                .setMessage(R.string.photo_delete_confirmation)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    if (currentPhoto != null) {
                        photoViewModel.deletePhoto(currentPhoto);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}

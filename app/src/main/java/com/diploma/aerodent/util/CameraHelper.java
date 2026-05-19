package com.diploma.aerodent.util;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.diploma.aerodent.R;
import com.diploma.aerodent.ui.photos.PhotoViewModel;

import java.io.File;
import java.io.IOException;


// Handles photo capture and permissions

public class CameraHelper {
    private final Fragment fragment;
    private final androidx.appcompat.app.AppCompatActivity activity;
    private final PhotoViewModel photoViewModel;
    private final ActivityResultLauncher<String> permissionLauncher;
    private final ActivityResultLauncher<Uri> cameraLauncher;

    private String currentPhotoPath;
    private int patientId;
    private Integer appointmentId;
    private OnPhotoSavedListener listener;
    private boolean showSuccessToast = false;

    public interface OnPhotoSavedListener {
        void onPhotoSaved(String path);
    }

    public CameraHelper(@NonNull Fragment fragment, @NonNull PhotoViewModel photoViewModel) {
        this.fragment = fragment;
        this.activity = null;
        this.photoViewModel = photoViewModel;

        this.permissionLauncher = fragment.registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted -> handlePermissionResult(isGranted, fragment.requireContext()));

        this.cameraLauncher = fragment.registerForActivityResult(new ActivityResultContracts.TakePicture(),
                success -> handleCameraResult(success, fragment.requireContext()));
    }

    public CameraHelper(@NonNull androidx.appcompat.app.AppCompatActivity activity, @NonNull PhotoViewModel photoViewModel) {
        this.fragment = null;
        this.activity = activity;
        this.photoViewModel = photoViewModel;

        this.permissionLauncher = activity.registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted -> handlePermissionResult(isGranted, activity));

        this.cameraLauncher = activity.registerForActivityResult(new ActivityResultContracts.TakePicture(),
                success -> handleCameraResult(success, activity));
    }

    private void handlePermissionResult(boolean isGranted, android.content.Context context) {
        if (isGranted) {
            launchCameraInternal(context);
        } else {
            Toast.makeText(context, R.string.camera_permission_required, Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCameraResult(boolean success, android.content.Context context) {
        if (success && currentPhotoPath != null) {
            photoViewModel.savePhoto(patientId, appointmentId, currentPhotoPath);
            if (showSuccessToast) {
                Toast.makeText(context, R.string.photo_saved_success, Toast.LENGTH_SHORT).show();
            }
            if (listener != null) {
                listener.onPhotoSaved(currentPhotoPath);
            }
        } else if (currentPhotoPath != null) {
            File file = new File(currentPhotoPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public void setShowSuccessToast(boolean showSuccessToast) {
        this.showSuccessToast = showSuccessToast;
    }

    private android.content.Context getContext() {
        return fragment != null ? fragment.requireContext() : activity;
    }

    public void takePhoto(int patientId, @Nullable Integer appointmentId, @Nullable OnPhotoSavedListener listener) {
        this.patientId = patientId;
        this.appointmentId = appointmentId;
        this.listener = listener;

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCameraInternal(getContext());
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCameraInternal(android.content.Context context) {
        try {
            File photoFile = photoViewModel.createImageFile(context, patientId);
            if (photoFile != null) {
                currentPhotoPath = photoFile.getAbsolutePath();
                Uri photoUri = FileProvider.getUriForFile(context,
                        context.getPackageName() + ".fileprovider", photoFile);
                cameraLauncher.launch(photoUri);
            }
        } catch (IOException ex) {
            Toast.makeText(context, R.string.photo_save_error, Toast.LENGTH_SHORT).show();
        }
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("camera_helper_photo_path", currentPhotoPath);
        outState.putInt("camera_helper_patient_id", patientId);
        if (appointmentId != null) {
            outState.putInt("camera_helper_appointment_id", appointmentId);
        }
    }

    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentPhotoPath = savedInstanceState.getString("camera_helper_photo_path");
            patientId = savedInstanceState.getInt("camera_helper_patient_id");
            if (savedInstanceState.containsKey("camera_helper_appointment_id")) {
                appointmentId = savedInstanceState.getInt("camera_helper_appointment_id");
            }
        }
    }
}

package com.diploma.aerodent.ui.photos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.Photo;

import java.io.File;

public class FullScreenPhotoDialogFragment extends DialogFragment {

    private static final String ARG_PHOTO_ID = "photo_id";
    private int photoId;
    private Photo currentPhoto;
    private PhotoViewModel photoViewModel;
    private ImageView imageView;

    public static FullScreenPhotoDialogFragment newInstance(int photoId) {
        FullScreenPhotoDialogFragment fragment = new FullScreenPhotoDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PHOTO_ID, photoId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        if (getArguments() != null) {
            photoId = getArguments().getInt(ARG_PHOTO_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_full_screen_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        photoViewModel = new ViewModelProvider(requireActivity()).get(PhotoViewModel.class);

        imageView = view.findViewById(R.id.image_full_screen);

        photoViewModel.getPhotoById(photoId).observe(getViewLifecycleOwner(), photo -> {
            if (photo != null) {
                this.currentPhoto = photo;
                if (photo.getFilePath() != null) {
                    Glide.with(this)
                            .load(new File(photo.getFilePath()))
                            .into(imageView);
                }
            }
        });

        view.findViewById(R.id.btn_close).setOnClickListener(v -> dismiss());

        view.findViewById(R.id.btn_delete_photo).setOnClickListener(v -> showDeleteConfirmation());
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete)
                .setMessage(R.string.photo_delete_confirmation)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    if (currentPhoto != null) {
                        photoViewModel.deletePhoto(currentPhoto);
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        imageView = null;
    }
}

package com.diploma.aerodent.ui.photos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.Photo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    public interface OnPhotoClickListener {
        void onPhotoClick(Photo photo);
    }

    private List<Photo> photos = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private OnPhotoClickListener listener;

    public void setOnPhotoClickListener(OnPhotoClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo_gallery, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Photo photo = photos.get(position);
        
        holder.textDate.setText(photo.getTakenAt() != null ? dateFormat.format(photo.getTakenAt()) : "");
        
        if (photo.getFilePath() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(new File(photo.getFilePath()))
                    .centerCrop()
                    .placeholder(R.drawable.ic_camera)
                    .into(holder.imagePhoto);
        } else {
            Glide.with(holder.itemView.getContext()).clear(holder.imagePhoto);
            holder.imagePhoto.setImageResource(R.drawable.ic_camera);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPhotoClick(photo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagePhoto;
        TextView textDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagePhoto = itemView.findViewById(R.id.image_photo);
            textDate = itemView.findViewById(R.id.text_photo_date);
        }
    }
}

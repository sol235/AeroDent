package com.diploma.aerodent.ui.photos;

import android.app.Application;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.diploma.aerodent.data.local.entity.Photo;
import com.diploma.aerodent.data.repository.PhotoRepository;
import com.diploma.aerodent.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PhotoViewModel extends AndroidViewModel {

    private final PhotoRepository repository;

    public PhotoViewModel(@NonNull Application application, PhotoRepository repository) {
        super(application);
        this.repository = repository;
    }

    public LiveData<List<Photo>> getPhotosForPatient(int patientId) {
        return repository.getPhotosForPatient(patientId);
    }

    public LiveData<List<Photo>> getPhotosForAppointment(int appointmentId) {
        return repository.getPhotosForAppointment(appointmentId);
    }

    public LiveData<Photo> getPhotoById(int photoId) {
        return repository.getPhotoById(photoId);
    }


    public File createImageFile(int patientId) throws IOException {
        return repository.createImageFile(patientId);
    }

    public void savePhoto(int patientId, Integer appointmentId, String filePath) {
        Photo photo = new Photo();
        photo.setPatientId(patientId);
        photo.setAppointmentId(appointmentId);
        photo.setFilePath(filePath);
        
        Date now = new Date();
        photo.setTakenAt(now);
        
        photo.setDescription(getApplication().getString(R.string.photo_taken_on) + new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(now));
        
        repository.insert(photo);
    }

    public void savePhotoFromUri(Uri uri, int patientId, Integer appointmentId) {
        Date now = new Date();
        String description = getApplication().getString(R.string.photo_taken_on) + new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(now);
        repository.savePhotoFromUri(uri, patientId, appointmentId, description);
    }

    public void deletePhoto(Photo photo) {
        repository.delete(photo);
    }
}

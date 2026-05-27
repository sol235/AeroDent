package com.diploma.aerodent.ui.photos;

import android.app.Application;
import android.content.Context;
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

    public void insertPhoto(Photo photo) {
        repository.insert(photo);
    }

    public File createImageFile(Context context, int patientId) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        
        // Private storage: files/AeroDent/Photos/patient_{id}/
        File storageDir = new File(context.getFilesDir(), "AeroDent/Photos/patient_" + patientId);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        
        return File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
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
        try {
            File photoFile = createImageFile(getApplication(), patientId);
            java.io.InputStream inputStream = getApplication().getContentResolver().openInputStream(uri);
            if (inputStream == null) return;
            
            java.io.OutputStream outputStream = new java.io.FileOutputStream(photoFile);
            
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
            
            outputStream.close();
            inputStream.close();
            
            savePhoto(patientId, appointmentId, photoFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deletePhoto(Photo photo) {
        if (photo.getFilePath() != null) {
            File file = new File(photo.getFilePath());
            if (file.exists()) {
                file.delete();
            }
        }
        repository.delete(photo);
    }
}

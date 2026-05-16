package com.diploma.aerodent.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

import com.diploma.aerodent.data.local.AppDatabase;
import com.diploma.aerodent.data.local.dao.PhotoDao;
import com.diploma.aerodent.data.local.entity.Photo;


public class PhotoRepository {

    private PhotoDao photoDao;
    private LiveData<List<Photo>> allPhotos;
    private Application application;

    public PhotoRepository(Application application) {
        this.application = application;
        AppDatabase db = AppDatabase.getDatabase(application);
        photoDao = db.photoDao();
        allPhotos = photoDao.getAllPhotos();
    }

    public LiveData<List<Photo>> getAllPhotos() {
        return allPhotos;
    }

    public LiveData<List<Photo>> getPhotosForPatient(int patientId) {
        return photoDao.getPhotosForPatient(patientId);
    }

    public LiveData<List<Photo>> getPhotosForAppointment(int appointmentId) {
        return photoDao.getPhotosForAppointment(appointmentId);
    }

    public LiveData<Photo> getPhotoById(int photoId) {
        return photoDao.getPhotoById(photoId);
    }

    public LiveData<Integer> getPhotoCountForPatient(int patientId) {
        return photoDao.getPhotoCountForPatient(patientId);
    }

    public void insert(Photo photo) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            photoDao.insert(photo);
        });
    }

    public void update(Photo photo) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            photoDao.update(photo);
        });
    }

    public void delete(Photo photo) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (photo.getFilePath() != null) {
                java.io.File file = new java.io.File(photo.getFilePath());
                if (file.exists()) {
                    file.delete();
                }
            }
            photoDao.delete(photo);
        });
    }

    public void deleteAllPhysicalFilesForPatient(int patientId) {
        // Called from PatientRepository or elsewhere
        List<String> filePaths = photoDao.getFilePathsForPatient(patientId);
        if (filePaths != null) {
            for (String path : filePaths) {
                if (path != null) {
                    java.io.File file = new java.io.File(path);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        }
        
        // Delete the directory
        java.io.File storageDir = new java.io.File(application.getFilesDir(), "AeroDent/Photos/patient_" + patientId);
        if (storageDir.exists() && storageDir.isDirectory()) {
            storageDir.delete();
        }
    }
}

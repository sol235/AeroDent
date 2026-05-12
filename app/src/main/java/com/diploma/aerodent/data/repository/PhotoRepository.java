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

    public PhotoRepository(Application application) {
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
            photoDao.delete(photo);
        });
    }
}

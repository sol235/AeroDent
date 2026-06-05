package com.diploma.aerodent.data.repository;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.diploma.aerodent.data.local.AppDatabase;
import com.diploma.aerodent.data.local.dao.PhotoDao;
import com.diploma.aerodent.data.local.entity.Photo;

public class PhotoRepository {
    private final PhotoDao photoDao;
    private final LiveData<List<Photo>> allPhotos;
    private final Application application;

    public PhotoRepository(PhotoDao photoDao, Application application) {
        this.photoDao = photoDao;
        this.application = application;
        this.allPhotos = photoDao.getAllPhotos();
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
                File file = new File(photo.getFilePath());
                if (file.exists() && !file.delete()) {
                    Log.w("PhotoRepository", "Failed to delete: " + photo.getFilePath());
                }
            }
            photoDao.delete(photo);
        });
    }

    public void deleteAllPhysicalFilesForPatient(int patientId) {
        List<String> filePaths = photoDao.getFilePathsForPatient(patientId);
        if (filePaths != null) {
            for (String path : filePaths) {
                if (path != null) {
                    File file = new File(path);
                    if (file.exists() && !file.delete()) {
                        Log.w("PhotoRepository", "Failed to delete: " + path);
                    }
                }
            }
        }

        // Delete the directory
        File storageDir = new File(application.getFilesDir(), "AeroDent/Photos/patient_" + patientId);
        if (storageDir.exists() && storageDir.isDirectory()) {
            if (!storageDir.delete()) {
                Log.w("PhotoRepository", "Failed to delete dir for patient " + patientId);
            }
        }
    }

    public void savePhotoFromUri(Uri uri, int patientId, Integer appointmentId, String description) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                File photoFile = createImageFile(patientId);
                InputStream inputStream = application.getContentResolver().openInputStream(uri);
                if (inputStream == null)
                    return;

                OutputStream outputStream = new FileOutputStream(photoFile);

                byte[] buf = new byte[1024];
                int len;
                while ((len = inputStream.read(buf)) > 0) {
                    outputStream.write(buf, 0, len);
                }

                outputStream.close();
                inputStream.close();

                Photo photo = new Photo();
                photo.setPatientId(patientId);
                photo.setAppointmentId(appointmentId);
                photo.setFilePath(photoFile.getAbsolutePath());
                photo.setTakenAt(new Date());
                photo.setDescription(description);

                photoDao.insert(photo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public File createImageFile(int patientId) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = new File(application.getFilesDir(), "AeroDent/Photos/patient_" + patientId);
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            Log.w("PhotoRepository", "Failed to create dir for patient " + patientId);
        }

        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
}

package com.diploma.aerodent.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.diploma.aerodent.data.local.entity.Photo;

import java.util.List;

@Dao
public interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Photo photo);

    @Update
    void update(Photo photo);

    @Delete
    void delete(Photo photo);

    @Query("DELETE FROM photos WHERE id = :photoId")
    void deleteById(int photoId);

    @Query("SELECT * FROM photos WHERE patientId = :patientId ORDER BY takenAt DESC")
    LiveData<List<Photo>> getPhotosForPatient(int patientId);

    @Query("SELECT * FROM photos WHERE patientId = :patientId ORDER BY takenAt DESC")
    List<Photo> getPhotosForPatientSync(int patientId);

    @Query("SELECT * FROM photos WHERE appointmentId = :appointmentId ORDER BY takenAt DESC")
    LiveData<List<Photo>> getPhotosForAppointment(int appointmentId);

    @Query("SELECT * FROM photos WHERE id = :photoId LIMIT 1")
    LiveData<Photo> getPhotoById(int photoId);

    @Query("SELECT * FROM photos ORDER BY takenAt DESC")
    LiveData<List<Photo>> getAllPhotos();

    @Query("SELECT COUNT(*) FROM photos WHERE patientId = :patientId")
    LiveData<Integer> getPhotoCountForPatient(int patientId);

    // Used to clean up private storage when a patient is deleted
    @Query("SELECT filePath FROM photos WHERE patientId = :patientId")
    List<String> getFilePathsForPatient(int patientId);
}
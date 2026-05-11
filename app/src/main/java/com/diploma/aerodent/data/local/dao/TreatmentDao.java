package com.diploma.aerodent.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.diploma.aerodent.data.local.entity.Treatment;

import java.util.List;


@Dao
public interface TreatmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Treatment treatment);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<Treatment> treatments);

    @Update
    void update(Treatment treatment);

    @Delete
    void delete(Treatment treatment);

    @Query("DELETE FROM treatments WHERE id = :treatmentId")
    void deleteById(int treatmentId);


    @Query("SELECT * FROM treatments WHERE appointmentId = :appointmentId")
    LiveData<List<Treatment>> getTreatmentsForAppointment(int appointmentId);

    @Query("SELECT * FROM treatments WHERE appointmentId = :appointmentId")
    List<Treatment> getTreatmentsForAppointmentSync(int appointmentId);

    @Query("SELECT * FROM treatments WHERE id = :treatmentId LIMIT 1")
    LiveData<Treatment> getTreatmentById(int treatmentId);

    // Used to show specific tooth for teeth display
    @Query("SELECT * FROM treatments WHERE patientId = :patientId AND toothNumber = :toothNumber ORDER BY id DESC")
    LiveData<List<Treatment>> getTreatmentsForTooth(int patientId, int toothNumber);

    @Query("SELECT * FROM treatments WHERE patientId = :patientId ORDER BY id DESC")
    LiveData<List<Treatment>> getTreatmentsForPatient(int patientId);

    @Query("SELECT * FROM treatments WHERE patientId = :patientId ORDER BY id DESC")
    List<Treatment> getTreatmentsForPatientSync(int patientId);

    @Query("SELECT * FROM treatments WHERE diagnosis = :diagnosisCode")
    LiveData<List<Treatment>> getTreatmentsByDiagnosis(String diagnosisCode);

    // Get unique treated teeth for teeth display coloring
    @Query("SELECT DISTINCT toothNumber FROM treatments WHERE patientId = :patientId")
    LiveData<List<Integer>> getTreatedToothNumbers(int patientId);
}

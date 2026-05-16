package com.diploma.aerodent.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.diploma.aerodent.data.local.entity.Patient;

import java.util.List;

@Dao
public interface PatientDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Patient patient);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<Patient> patients);

    @Update
    void update(Patient patient);

    @Delete
    void delete(Patient patient);

    @Query("SELECT * FROM patients ORDER BY lastName ASC, firstName ASC")
    LiveData<List<Patient>> getAllPatients();

    @Query("SELECT * FROM patients ORDER BY lastName ASC, firstName ASC")
    List<Patient> getAllPatientsSync();

    @Query("SELECT * FROM patients WHERE id = :patientId LIMIT 1")
    LiveData<Patient> getPatientById(int patientId);

    // Case-insensitive search by name
    @Query("SELECT * FROM patients WHERE firstName LIKE '%' || :query || '%' OR lastName LIKE '%' || :query || '%' ORDER BY lastName ASC")
    LiveData<List<Patient>> searchPatients(String query);

    @Query("SELECT * FROM patients WHERE egn = :egn LIMIT 1")
    LiveData<Patient> getPatientByEgn(String egn);

    // Lookup Health Insurance/НЗОК number for api integration
    @Query("SELECT * FROM patients WHERE nhifNumber = :nhifNumber LIMIT 1")
    LiveData<Patient> getPatientByNhifNumber(String nhifNumber);

    @Query("SELECT COUNT(*) FROM patients")
    LiveData<Integer> getPatientCount();
}
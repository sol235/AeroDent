package com.diploma.aerodent.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.diploma.aerodent.data.local.entity.ToothStatus;
import com.diploma.aerodent.data.local.model.DentalCondition;

import java.util.List;

@Dao
public interface ToothStatusDao {

    @Query("SELECT * FROM tooth_statuses")
    List<ToothStatus> getAllToothStatusesSync();

    @Query("SELECT * FROM tooth_statuses WHERE patientId = :patientId")
    LiveData<List<ToothStatus>> getToothStatusesForPatient(int patientId);

    @Query("SELECT * FROM tooth_statuses WHERE patientId = :patientId AND toothNumber = :toothNumber")
    LiveData<List<ToothStatus>> getToothStatusesForTooth(int patientId, int toothNumber);

    @Query("SELECT * FROM tooth_statuses WHERE patientId = :patientId AND toothNumber = :toothNumber")
    List<ToothStatus> getToothStatusesForToothSync(int patientId, int toothNumber);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ToothStatus toothStatus);

    @Update
    void update(ToothStatus toothStatus);

    @Query("DELETE FROM tooth_statuses WHERE patientId = :patientId AND toothNumber = :toothNumber AND conditionCode = :condition")
    void deleteStatus(int patientId, int toothNumber, DentalCondition condition);

    @Query("DELETE FROM tooth_statuses WHERE patientId = :patientId AND toothNumber = :toothNumber")
    void deleteAllStatusesForTooth(int patientId, int toothNumber);

    @Query("DELETE FROM tooth_statuses WHERE patientId = :patientId AND toothNumber = :toothNumber AND conditionCode IN (:conditions)")
    void deleteSpecificStatusesForTooth(int patientId, int toothNumber, List<DentalCondition> conditions);
}

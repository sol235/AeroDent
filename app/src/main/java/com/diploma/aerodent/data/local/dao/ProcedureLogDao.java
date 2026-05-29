package com.diploma.aerodent.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.diploma.aerodent.data.local.entity.ProcedureLog;

import java.util.List;


@Dao
public interface ProcedureLogDao {

    @Query("SELECT * FROM procedure_logs ORDER BY dateLogged DESC, id DESC")
    List<ProcedureLog> getAllProcedureLogsSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ProcedureLog log);

    @Update
    void update(ProcedureLog log);

    @Query("SELECT * FROM procedure_logs WHERE appointmentId = :appointmentId")
    LiveData<List<ProcedureLog>> getProcedureLogsForAppointment(int appointmentId);

    @Query("SELECT * FROM procedure_logs WHERE patientId = :patientId ORDER BY dateLogged DESC, id DESC")
    LiveData<List<ProcedureLog>> getProcedureLogsForPatient(int patientId);
}

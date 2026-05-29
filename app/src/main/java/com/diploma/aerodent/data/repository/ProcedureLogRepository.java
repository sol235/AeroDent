package com.diploma.aerodent.data.repository;

import androidx.lifecycle.LiveData;
import java.util.List;

import com.diploma.aerodent.data.local.AppDatabase;
import com.diploma.aerodent.data.local.dao.ProcedureLogDao;
import com.diploma.aerodent.data.local.entity.ProcedureLog;


public class ProcedureLogRepository {
    private final ProcedureLogDao procedureLogDao;

    public ProcedureLogRepository(ProcedureLogDao procedureLogDao) {
        this.procedureLogDao = procedureLogDao;
    }

    public LiveData<List<ProcedureLog>> getProcedureLogsForAppointment(int appointmentId) {
        return procedureLogDao.getProcedureLogsForAppointment(appointmentId);
    }

    public LiveData<List<ProcedureLog>> getProcedureLogsForPatient(int patientId) {
        return procedureLogDao.getProcedureLogsForPatient(patientId);
    }

    public List<ProcedureLog> getAllProcedureLogsSync() {
        return procedureLogDao.getAllProcedureLogsSync();
    }

    public void insert(ProcedureLog log) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            procedureLogDao.insert(log);
        });
    }

    public void update(ProcedureLog log) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            procedureLogDao.update(log);
        });
    }
}

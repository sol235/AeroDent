package com.diploma.aerodent.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

import com.diploma.aerodent.data.local.AppDatabase;
import com.diploma.aerodent.data.local.dao.ProcedureLogDao;
import com.diploma.aerodent.data.local.entity.ProcedureLog;


public class ProcedureLogRepository {

    private ProcedureLogDao procedureLogDao;

    public ProcedureLogRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        procedureLogDao = db.procedureLogDao();
    }

    public LiveData<List<ProcedureLog>> getProcedureLogsForAppointment(int appointmentId) {
        return procedureLogDao.getProcedureLogsForAppointment(appointmentId);
    }

    public LiveData<List<ProcedureLog>> getProcedureLogsForPatient(int patientId) {
        return procedureLogDao.getProcedureLogsForPatient(patientId);
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

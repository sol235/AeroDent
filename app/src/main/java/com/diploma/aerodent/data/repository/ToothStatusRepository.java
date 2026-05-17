package com.diploma.aerodent.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.diploma.aerodent.data.local.AppDatabase;
import com.diploma.aerodent.data.local.dao.ToothStatusDao;
import com.diploma.aerodent.data.local.entity.ToothStatus;
import com.diploma.aerodent.data.local.model.DentalCondition;

import java.util.List;

public class ToothStatusRepository {

    private final ToothStatusDao toothStatusDao;

    public ToothStatusRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        toothStatusDao = db.toothStatusDao();
    }

    public LiveData<List<ToothStatus>> getToothStatusesForPatient(int patientId) {
        return toothStatusDao.getToothStatusesForPatient(patientId);
    }

    public LiveData<List<ToothStatus>> getToothStatusesForTooth(int patientId, int toothNumber) {
        return toothStatusDao.getToothStatusesForTooth(patientId, toothNumber);
    }

    public void insert(ToothStatus toothStatus) {
        AppDatabase.databaseWriteExecutor.execute(() -> toothStatusDao.insert(toothStatus));
    }

    public void deleteStatus(int patientId, int toothNumber, DentalCondition condition) {
        AppDatabase.databaseWriteExecutor.execute(() -> toothStatusDao.deleteStatus(patientId, toothNumber, condition));
    }
}

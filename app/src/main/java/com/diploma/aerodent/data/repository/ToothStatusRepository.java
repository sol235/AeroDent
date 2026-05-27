package com.diploma.aerodent.data.repository;

import androidx.lifecycle.LiveData;
import com.diploma.aerodent.data.local.dao.ToothStatusDao;
import com.diploma.aerodent.data.local.entity.ToothStatus;
import com.diploma.aerodent.data.local.model.DentalCondition;

import java.util.List;

public class ToothStatusRepository {

    private final ToothStatusDao toothStatusDao;

    public ToothStatusRepository(ToothStatusDao toothStatusDao) {
        this.toothStatusDao = toothStatusDao;
    }

    public LiveData<List<ToothStatus>> getToothStatusesForPatient(int patientId) {
        return toothStatusDao.getToothStatusesForPatient(patientId);
    }

    public LiveData<List<ToothStatus>> getToothStatusesForTooth(int patientId, int toothNumber) {
        return toothStatusDao.getToothStatusesForTooth(patientId, toothNumber);
    }

    public List<ToothStatus> getToothStatusesForToothSync(int patientId, int toothNumber) {
        return toothStatusDao.getToothStatusesForToothSync(patientId, toothNumber);
    }

    public void insert(ToothStatus toothStatus) {
        toothStatusDao.insert(toothStatus);
    }

    public void update(ToothStatus toothStatus) {
        toothStatusDao.update(toothStatus);
    }

    public void deleteStatus(int patientId, int toothNumber, DentalCondition condition) {
        toothStatusDao.deleteStatus(patientId, toothNumber, condition);
    }

    public void deleteAllStatusesForTooth(int patientId, int toothNumber) {
        toothStatusDao.deleteAllStatusesForTooth(patientId, toothNumber);
    }

    public void deleteSpecificStatusesForTooth(int patientId, int toothNumber, List<DentalCondition> conditions) {
        toothStatusDao.deleteSpecificStatusesForTooth(patientId, toothNumber, conditions);
    }
}

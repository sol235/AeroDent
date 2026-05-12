package com.diploma.aerodent.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

import com.diploma.aerodent.data.local.AppDatabase;
import com.diploma.aerodent.data.local.dao.TreatmentDao;
import com.diploma.aerodent.data.local.entity.Treatment;


public class TreatmentRepository {

    private TreatmentDao treatmentDao;

    public TreatmentRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        treatmentDao = db.treatmentDao();
    }

    public LiveData<Treatment> getTreatmentById(int treatmentId) {
        return treatmentDao.getTreatmentById(treatmentId);
    }

    public LiveData<List<Treatment>> getTreatmentsForAppointment(int appointmentId) {
        return treatmentDao.getTreatmentsForAppointment(appointmentId);
    }

    public LiveData<List<Treatment>> getTreatmentsForTooth(int patientId, int toothNumber) {
        return treatmentDao.getTreatmentsForTooth(patientId, toothNumber);
    }

    public LiveData<List<Treatment>> getTreatmentsForPatient(int patientId) {
        return treatmentDao.getTreatmentsForPatient(patientId);
    }

    public LiveData<List<Treatment>> getTreatmentsByDiagnosis(String diagnosisCode) {
        return treatmentDao.getTreatmentsByDiagnosis(diagnosisCode);
    }

    public LiveData<List<Integer>> getTreatedToothNumbers(int patientId) {
        return treatmentDao.getTreatedToothNumbers(patientId);
    }

    public void insert(Treatment treatment) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            treatmentDao.insert(treatment);
        });
    }

    public void insertAll(List<Treatment> treatments) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            treatmentDao.insertAll(treatments);
        });
    }

    public void update(Treatment treatment) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            treatmentDao.update(treatment);
        });
    }

    public void delete(Treatment treatment) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            treatmentDao.delete(treatment);
        });
    }
}

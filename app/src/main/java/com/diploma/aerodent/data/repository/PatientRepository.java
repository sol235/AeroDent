package com.diploma.aerodent.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

import com.diploma.aerodent.data.local.AppDatabase;
import com.diploma.aerodent.data.local.dao.PatientDao;
import com.diploma.aerodent.data.local.entity.Patient;


public class PatientRepository {

    private PatientDao patientDao;
    private LiveData<List<Patient>> allPatients;

    public PatientRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        patientDao = db.patientDao();
        allPatients = patientDao.getAllPatients();
    }

    public LiveData<List<Patient>> getAllPatients() {
        return allPatients;
    }

    public LiveData<Patient> getPatientById(int id) {
        return patientDao.getPatientById(id);
    }

    public LiveData<List<Patient>> searchPatients(String query) {
        return patientDao.searchPatients(query);
    }
    
    public LiveData<Patient> getPatientByEgn(String egn) {
        return patientDao.getPatientByEgn(egn);
    }
    
    public LiveData<Patient> getPatientByNhifNumber(String nhifNumber) {
        return patientDao.getPatientByNhifNumber(nhifNumber);
    }

    public LiveData<Integer> getPatientCount() {
        return patientDao.getPatientCount();
    }

    public void insert(Patient patient) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            patientDao.insert(patient);
        });
    }

    public void update(Patient patient) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            patientDao.update(patient);
        });
    }

    public void delete(Patient patient) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            patientDao.delete(patient);
        });
    }
}

package com.diploma.aerodent.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

import com.diploma.aerodent.data.local.AppDatabase;
import com.diploma.aerodent.data.local.dao.PatientDao;
import com.diploma.aerodent.data.local.entity.Patient;


public class PatientRepository {

    private PatientDao patientDao;
    private PhotoRepository photoRepository;
    private LiveData<List<Patient>> allPatients;

    public PatientRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        patientDao = db.patientDao();
        photoRepository = new PhotoRepository(application);
        allPatients = patientDao.getAllPatients();
    }

    public LiveData<List<Patient>> getAllPatients() {
        return allPatients;
    }

    public LiveData<Patient> getPatientById(int id) {
        return patientDao.getPatientById(id);
    }


    public LiveData<Patient> getPatientByEgn(String egn) {
        return patientDao.getPatientByEgn(egn);
    }
    
    public LiveData<Patient> getPatientByZokNumber(String zokNumber) {
        return patientDao.getPatientByZokNumber(zokNumber);
    }

    public LiveData<Integer> getPatientCount() {
        return patientDao.getPatientCount();
    }

    public interface DuplicateCheckCallback {
        void onResult(boolean isDuplicate);
    }

    public void checkEgnExists(String egn, int excludePatientId, DuplicateCheckCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            int count = patientDao.checkEgnExistsSync(egn, excludePatientId);
            callback.onResult(count > 0);
        });
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
            photoRepository.deleteAllPhysicalFilesForPatient(patient.getId());
            patientDao.delete(patient);
        });
    }
}

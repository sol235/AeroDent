package com.diploma.aerodent.data.repository;

import androidx.lifecycle.LiveData;
import java.util.List;

import com.diploma.aerodent.data.local.AppDatabase;
import com.diploma.aerodent.data.local.dao.PatientDao;
import com.diploma.aerodent.data.local.entity.Patient;


public class PatientRepository {
    private final PatientDao patientDao;
    private final PhotoRepository photoRepository;
    private final LiveData<List<Patient>> allPatients;

    public PatientRepository(PatientDao patientDao, PhotoRepository photoRepository) {
        this.patientDao = patientDao;
        this.photoRepository = photoRepository;
        this.allPatients = patientDao.getAllPatients();
    }

    public LiveData<List<Patient>> getAllPatients() {
        return allPatients;
    }

    public List<Patient> getAllPatientsSync() {
        return patientDao.getAllPatientsSync();
    }

    public LiveData<Patient> getPatientById(int id) {
        return patientDao.getPatientById(id);
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

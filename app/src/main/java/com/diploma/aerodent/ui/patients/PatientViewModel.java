package com.diploma.aerodent.ui.patients;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.diploma.aerodent.data.local.entity.Patient;
import com.diploma.aerodent.data.repository.PatientRepository;
import com.diploma.aerodent.util.EgnUtils;
import com.diploma.aerodent.util.ValidationUtils;

import java.util.Date;
import java.util.List;

public class PatientViewModel extends AndroidViewModel {

    private final PatientRepository patientRepository;
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final LiveData<List<Patient>> searchResults;

    private final MutableLiveData<String> calculatedGender = new MutableLiveData<>();
    private final MutableLiveData<Date> calculatedDob = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isEgnValid = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> isEgnDuplicate = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isPhoneValid = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> isEmailValid = new MutableLiveData<>(true);

    public PatientViewModel(@NonNull Application application) {
        super(application);
        patientRepository = new PatientRepository(application);
        searchResults = Transformations.switchMap(searchQuery, query -> {
            if (query == null || query.trim().isEmpty()) {
                return patientRepository.getAllPatients();
            } else {
                return patientRepository.searchPatients(query.trim());
            }
        });
    }

    public LiveData<List<Patient>> getSearchResults() {
        return searchResults;
    }

    public void setSearchQuery(String query) {
        if (query == null) query = "";
        if (!query.equals(searchQuery.getValue())) {
            searchQuery.setValue(query);
        }
    }

    public LiveData<Patient> getPatientById(int id) {
        return patientRepository.getPatientById(id);
    }

    public LiveData<String> getCalculatedGender() {
        return calculatedGender;
    }

    public LiveData<Date> getCalculatedDob() {
        return calculatedDob;
    }

    public LiveData<Boolean> getIsEgnValid() {
        return isEgnValid;
    }

    public LiveData<Boolean> getIsEgnDuplicate() {
        return isEgnDuplicate;
    }

    public LiveData<Boolean> getIsPhoneValid() {
        return isPhoneValid;
    }

    public LiveData<Boolean> getIsEmailValid() {
        return isEmailValid;
    }

    public void processEgn(String egn, int excludePatientId) {
        if (egn != null && egn.length() == 10) {
            boolean valid = EgnUtils.isValidEgn(egn);
            isEgnValid.setValue(valid);
            if (valid) {
                calculatedGender.setValue(EgnUtils.getGender(egn));
                calculatedDob.setValue(EgnUtils.getBirthDate(egn));
                
                patientRepository.checkEgnExists(egn, excludePatientId, isDuplicate -> {
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        isEgnDuplicate.setValue(isDuplicate);
                    });
                });
            } else {
                calculatedGender.setValue(null);
                calculatedDob.setValue(null);
                isEgnDuplicate.setValue(false);
            }
        } else {
            isEgnValid.setValue(true); // Don't show error for partial EGN
            calculatedGender.setValue(null);
            calculatedDob.setValue(null);
            isEgnDuplicate.setValue(false);
        }
    }

    public boolean validatePatientData(String egn, String phone, String email) {
        boolean egnValid = EgnUtils.isValidEgn(egn);
        boolean phoneValid = ValidationUtils.isValidPhoneNumber(phone);
        boolean emailValid = ValidationUtils.isValidEmail(email);

        isEgnValid.setValue(egnValid);
        isPhoneValid.setValue(phoneValid);
        isEmailValid.setValue(emailValid);

        return egnValid && phoneValid && emailValid;
    }

    public interface SaveCallback {
        void onSuccess();
        void onDuplicateEgn();
    }

    public void savePatientWithCheck(Patient existingPatient, String firstName, String lastName, String egn, String gender,
                            String phone, String email, String nhifNumber, String nhifStatus, Date dob, String notes, SaveCallback callback) {
        
        int excludePatientId = (existingPatient != null) ? existingPatient.getId() : -1;
        patientRepository.checkEgnExists(egn, excludePatientId, isDuplicate -> {
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                if (isDuplicate) {
                    callback.onDuplicateEgn();
                } else {
                    savePatient(existingPatient, firstName, lastName, egn, gender, phone, email, nhifNumber, nhifStatus, dob, notes);
                    callback.onSuccess();
                }
            });
        });
    }

    public void savePatient(Patient existingPatient, String firstName, String lastName, String egn, String gender,
                            String phone, String email, String nhifNumber, String nhifStatus, Date dob, String notes) {
        Patient patient = (existingPatient != null) ? existingPatient : new Patient();
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setEgn(egn);
        patient.setGender(gender);
        patient.setPhoneNumber(phone);
        patient.setEmail(email);
        patient.setNhifNumber(nhifNumber);
        patient.setNhifStatus(nhifStatus);
        patient.setDateOfBirth(dob);
        patient.setNotes(notes);
        
        if (existingPatient == null) {
            patient.setCreatedAt(new Date());
            insert(patient);
        } else {
            update(patient);
        }
    }

    public void insert(Patient patient) {
        patientRepository.insert(patient);
    }

    public void update(Patient patient) {
        patientRepository.update(patient);
    }

    public void delete(Patient patient) {
        patientRepository.delete(patient);
    }
}

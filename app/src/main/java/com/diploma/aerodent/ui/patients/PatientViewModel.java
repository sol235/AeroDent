package com.diploma.aerodent.ui.patients;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.diploma.aerodent.data.local.entity.Patient;
import com.diploma.aerodent.data.repository.PatientRepository;
import com.diploma.aerodent.util.EgnUtils;

import java.util.Date;
import java.util.List;

public class PatientViewModel extends AndroidViewModel {

    private final PatientRepository patientRepository;
    private final LiveData<List<Patient>> allPatients;

    private final MutableLiveData<String> calculatedGender = new MutableLiveData<>();
    private final MutableLiveData<Date> calculatedDob = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isEgnValid = new MutableLiveData<>();

    public PatientViewModel(@NonNull Application application) {
        super(application);
        patientRepository = new PatientRepository(application);
        allPatients = patientRepository.getAllPatients();
    }

    public LiveData<List<Patient>> getAllPatients() {
        return allPatients;
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

    public void processEgn(String egn) {
        if (egn != null && egn.length() == 10) {
            boolean valid = EgnUtils.isValidEgn(egn);
            isEgnValid.setValue(valid);
            if (valid) {
                calculatedGender.setValue(EgnUtils.getGender(egn));
                calculatedDob.setValue(EgnUtils.getBirthDate(egn));
            } else {
                calculatedGender.setValue(null);
                calculatedDob.setValue(null);
            }
        } else {
            isEgnValid.setValue(true); // Don't show error for partial EGN
            calculatedGender.setValue(null);
            calculatedDob.setValue(null);
        }
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

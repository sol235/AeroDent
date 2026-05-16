package com.diploma.aerodent.ui.patients;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.diploma.aerodent.data.local.entity.Patient;
import com.diploma.aerodent.data.repository.PatientRepository;

import java.util.Date;
import java.util.List;

public class PatientViewModel extends AndroidViewModel {

    private final PatientRepository patientRepository;
    private final LiveData<List<Patient>> allPatients;

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

    public void savePatient(Patient existingPatient, String firstName, String lastName, String egn, 
                            String phone, String email, String nhifNumber, String nhifStatus, Date dob) {
        Patient patient = (existingPatient != null) ? existingPatient : new Patient();
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setEgn(egn);
        patient.setPhoneNumber(phone);
        patient.setEmail(email);
        patient.setNhifNumber(nhifNumber);
        patient.setNhifStatus(nhifStatus);
        patient.setDateOfBirth(dob);
        
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

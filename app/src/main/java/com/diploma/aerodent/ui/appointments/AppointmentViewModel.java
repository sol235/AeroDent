package com.diploma.aerodent.ui.appointments;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.diploma.aerodent.data.local.entity.Appointment;
import com.diploma.aerodent.data.local.entity.Patient;
import com.diploma.aerodent.data.repository.AppointmentRepository;
import com.diploma.aerodent.data.repository.PatientRepository;

import java.util.Date;
import java.util.List;

public class AppointmentViewModel extends AndroidViewModel {

    private AppointmentRepository repository;
    private PatientRepository patientRepository;
    private LiveData<List<Patient>> allPatients;

    public AppointmentViewModel(@NonNull Application application) {
        super(application);
        repository = new AppointmentRepository(application);
        patientRepository = new PatientRepository(application);
        allPatients = patientRepository.getAllPatients();
    }

    public LiveData<List<Patient>> getAllPatients() {
        return allPatients;
    }

    public void insert(Appointment appointment) {
        repository.insert(appointment);
    }

    public void update(Appointment appointment) {
        repository.update(appointment);
    }

    public void delete(Appointment appointment) {
        repository.delete(appointment);
    }

    public LiveData<Appointment> getAppointmentById(int id) {
        return repository.getAppointmentById(id);
    }
}

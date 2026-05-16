package com.diploma.aerodent.ui.appointments;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.diploma.aerodent.data.local.entity.Appointment;
import com.diploma.aerodent.data.local.entity.Patient;
import com.diploma.aerodent.data.repository.AppointmentRepository;
import com.diploma.aerodent.data.repository.PatientRepository;

import java.util.Date;
import java.util.List;

public class AppointmentViewModel extends AndroidViewModel {

    public static class AppointmentWithPatient {
        public final Appointment appointment;
        public final Patient patient;

        public AppointmentWithPatient(Appointment appointment, Patient patient) {
            this.appointment = appointment;
            this.patient = patient;
        }
    }

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

    public LiveData<Patient> getPatientById(int id) {
        return patientRepository.getPatientById(id);
    }

    public LiveData<AppointmentWithPatient> getAppointmentWithPatient(int appointmentId) {
        return Transformations.switchMap(repository.getAppointmentById(appointmentId), appointment -> {
            if (appointment == null) {
                MutableLiveData<AppointmentWithPatient> data = new MutableLiveData<>();
                data.setValue(null);
                return data;
            }
            return Transformations.map(patientRepository.getPatientById(appointment.getPatientId()), patient -> 
                new AppointmentWithPatient(appointment, patient)
            );
        });
    }

    public void saveAppointment(Appointment existingAppointment, int patientId, Date dateTime, 
                               String treatmentType, String notes, String status) {
        Appointment appointment = existingAppointment != null ? existingAppointment : new Appointment();
        appointment.setPatientId(patientId);
        appointment.setDateTime(dateTime);
        appointment.setTreatmentType(treatmentType);
        appointment.setNotes(notes);
        appointment.setStatus(status);
        
        if (existingAppointment == null) {
            appointment.setCreatedAt(new Date());
            insert(appointment);
        } else {
            update(appointment);
        }
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

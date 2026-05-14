package com.diploma.aerodent.ui.patients;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.diploma.aerodent.data.local.entity.Appointment;
import com.diploma.aerodent.data.local.entity.Patient;
import com.diploma.aerodent.data.local.entity.Payment;
import com.diploma.aerodent.data.local.entity.Treatment;
import com.diploma.aerodent.data.repository.AppointmentRepository;
import com.diploma.aerodent.data.repository.PatientRepository;
import com.diploma.aerodent.data.repository.PaymentRepository;
import com.diploma.aerodent.data.repository.TreatmentRepository;

import java.util.List;

public class PatientDetailViewModel extends AndroidViewModel {

    private PatientRepository patientRepository;
    private AppointmentRepository appointmentRepository;
    private TreatmentRepository treatmentRepository;
    private PaymentRepository paymentRepository;

    private MutableLiveData<Integer> patientId = new MutableLiveData<>();
    private LiveData<Patient> patient;
    private LiveData<List<Appointment>> appointments;
    private LiveData<List<Payment>> payments;
    private MediatorLiveData<List<PatientHistoryItem>> historyItems = new MediatorLiveData<>();

    public PatientDetailViewModel(@NonNull Application application) {
        super(application);
        patientRepository = new PatientRepository(application);
        appointmentRepository = new AppointmentRepository(application);
        treatmentRepository = new TreatmentRepository(application);
        paymentRepository = new PaymentRepository(application);

        patient = Transformations.switchMap(patientId, id -> patientRepository.getPatientById(id));
        appointments = Transformations.switchMap(patientId, id -> appointmentRepository.getAppointmentsForPatient(id));
        payments = Transformations.switchMap(patientId, id -> paymentRepository.getPaymentsForPatient(id));

        historyItems.addSource(appointments, appts -> combineHistory(appts, payments.getValue()));
        historyItems.addSource(payments, pmnts -> combineHistory(appointments.getValue(), pmnts));
    }

    private void combineHistory(List<Appointment> appts, List<Payment> pmnts) {
        if (appts == null) return;
        
        java.util.List<PatientHistoryItem> items = new java.util.ArrayList<>();
        for (Appointment appt : appts) {
            Payment foundPayment = null;
            if (pmnts != null) {
                for (Payment p : pmnts) {
                    if (p.getAppointmentId() == appt.getId()) {
                        foundPayment = p;
                        break;
                    }
                }
            }
            items.add(new PatientHistoryItem(appt, foundPayment));
        }
        historyItems.setValue(items);
    }

    public void setPatientId(int id) {
        patientId.setValue(id);
    }

    public LiveData<Patient> getPatient() {
        return patient;
    }

    public LiveData<List<PatientHistoryItem>> getHistoryItems() {
        return historyItems;
    }

    public LiveData<List<Treatment>> getTreatmentsForAppointment(int appointmentId) {
        return treatmentRepository.getTreatmentsForAppointment(appointmentId);
    }

    public LiveData<Payment> getPaymentForAppointment(int appointmentId) {
        return paymentRepository.getPaymentForAppointment(appointmentId);
    }
}

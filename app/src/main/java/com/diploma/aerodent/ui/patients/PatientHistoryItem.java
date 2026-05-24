package com.diploma.aerodent.ui.patients;

import com.diploma.aerodent.data.local.entity.Appointment;
import com.diploma.aerodent.data.local.entity.Payment;
import java.util.List;

public class PatientHistoryItem {
    private Appointment appointment;
    private List<Payment> payments;

    public PatientHistoryItem(Appointment appointment, List<Payment> payments) {
        this.appointment = appointment;
        this.payments = payments;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public List<Payment> getPayments() {
        return payments;
    }
}

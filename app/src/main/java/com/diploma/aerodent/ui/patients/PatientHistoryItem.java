package com.diploma.aerodent.ui.patients;

import com.diploma.aerodent.data.local.entity.Appointment;
import com.diploma.aerodent.data.local.entity.Payment;

public class PatientHistoryItem {
    private Appointment appointment;
    private Payment payment;

    public PatientHistoryItem(Appointment appointment, Payment payment) {
        this.appointment = appointment;
        this.payment = payment;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public Payment getPayment() {
        return payment;
    }
}

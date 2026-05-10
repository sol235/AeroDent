package com.diploma.aerodent.data.local.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import java.util.Date;


@Entity(tableName = "payments",
        foreignKeys = {
                @ForeignKey(
                        entity = Patient.class,
                        parentColumns = "id",
                        childColumns = "patientId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(
                        entity = Appointment.class,
                        parentColumns = "id",
                        childColumns = "appointmentId",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index("patientId"),
                @Index("appointmentId")
        })

public class Payment extends PatientRecordEntity {

    private int appointmentId;
    private double amount;
    private double nhifCovered;
    private double patientPays;
    private String status;
    private Date date;

    public Payment() {}


    public int getAppointmentId() {
        return appointmentId;
    }
    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getNhifCovered() {
        return nhifCovered;
    }
    public void setNhifCovered(double nhifCovered) {
        this.nhifCovered = nhifCovered;
    }

    public double getPatientPays() {
        return patientPays;
    }
    public void setPatientPays(double patientPays) {
        this.patientPays = patientPays;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isPaid() {
        return "PAID".equals(status);
    }
}

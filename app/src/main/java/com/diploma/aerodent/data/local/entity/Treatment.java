package com.diploma.aerodent.data.local.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;


@Entity(tableName = "treatments",
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

public class Treatment extends PatientRecordEntity {

    private int appointmentId;
    private int toothNumber;
    private String procedure;
    private String diagnosis;
    private String notes;

    public Treatment() {}


    public int getAppointmentId() {
        return appointmentId;
    }
    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getToothNumber() {
        return toothNumber;
    }
    public void setToothNumber(int toothNumber) {
        this.toothNumber = toothNumber;
    }

    public String getProcedure() {
        return procedure;
    }
    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public String getDiagnosis() {
        return diagnosis;
    }
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
}

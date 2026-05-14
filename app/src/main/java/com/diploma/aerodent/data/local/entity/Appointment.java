package com.diploma.aerodent.data.local.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import java.util.Date;


@Entity(tableName = "appointments",
        foreignKeys = @ForeignKey(
                entity = Patient.class,
                parentColumns = "id",
                childColumns = "patientId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("patientId")})

public class Appointment extends PatientRecordEntity {

    private Date dateTime;
    private String status;
    private String notes;
    private String treatmentType;
    private Date createdAt;

    @Ignore public static final String STATUS_SCHEDULED = "SCHEDULED";
    @Ignore public static final String STATUS_COMPLETED = "COMPLETED";
    @Ignore public static final String STATUS_CANCELLED = "CANCELLED";

    public Appointment() {}


    public Date getDateTime() {
        return dateTime;
    }
    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTreatmentType() {
        return treatmentType;
    }
    public void setTreatmentType(String treatmentType) {
        this.treatmentType = treatmentType;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}

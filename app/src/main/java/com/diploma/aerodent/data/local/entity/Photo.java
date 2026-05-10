package com.diploma.aerodent.data.local.entity;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import java.util.Date;


@Entity(tableName = "photos",
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
                        onDelete = ForeignKey.SET_NULL)
        },
        indices = {
                @Index("patientId"),
                @Index("appointmentId")
        })

public class Photo extends PatientRecordEntity {

    @Nullable
    private Integer appointmentId;
    private String filePath;
    private String description;
    private Date takenAt;

    public Photo() {
        this.takenAt = new Date();
    }


    @Nullable
    public Integer getAppointmentId() {
        return appointmentId;
    }
    public void setAppointmentId(@Nullable Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTakenAt() {
        return takenAt;
    }
    public void setTakenAt(Date takenAt) {
        this.takenAt = takenAt;
    }
}

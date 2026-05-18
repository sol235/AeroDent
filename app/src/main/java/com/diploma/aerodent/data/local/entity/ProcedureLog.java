package com.diploma.aerodent.data.local.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import java.util.Date;


@Entity(tableName = "procedure_logs",
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
public class ProcedureLog extends PatientRecordEntity {

    private Integer appointmentId;
    private int toothNumber;
    private Date dateLogged;
    private String entryType;
    private String diagnosis;
    private String actionTaken;
    private String surfaces;
    private String notes;

    public static final String TYPE_STATUS = "STATUS";
    public static final String TYPE_PROCEDURE = "PROCEDURE";

    private boolean isAnnulled = false;

    public ProcedureLog() {}

    public boolean isAnnulled() {
        return isAnnulled;
    }

    public void setAnnulled(boolean annulled) {
        isAnnulled = annulled;
    }

    public Integer getAppointmentId() {
        return appointmentId;
    }
    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getToothNumber() {
        return toothNumber;
    }
    public void setToothNumber(int toothNumber) {
        this.toothNumber = toothNumber;
    }

    public Date getDateLogged() {
        return dateLogged;
    }
    public void setDateLogged(Date dateLogged) {
        this.dateLogged = dateLogged;
    }

    public String getEntryType() {
        return entryType;
    }
    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }

    public String getDiagnosis() {
        return diagnosis;
    }
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getActionTaken() {
        return actionTaken;
    }
    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }

    public String getSurfaces() {
        return surfaces;
    }
    public void setSurfaces(String surfaces) {
        this.surfaces = surfaces;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
}

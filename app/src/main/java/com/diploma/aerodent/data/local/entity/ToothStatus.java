package com.diploma.aerodent.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.diploma.aerodent.data.local.model.DentalCondition;

import java.util.Date;

@Entity(tableName = "tooth_statuses",
        foreignKeys = @ForeignKey(
                entity = Patient.class,
                parentColumns = "id",
                childColumns = "patientId",
                onDelete = ForeignKey.CASCADE),
        indices = {
                @Index(value = {"patientId", "toothNumber", "conditionCode"}, unique = true)
        })
public class ToothStatus extends PatientRecordEntity {

    private int toothNumber;
    
    @ColumnInfo(name = "conditionCode")
    private DentalCondition condition;
    
    public static final String SURFACE_MESIAL = "M";
    public static final String SURFACE_OCCLUSAL = "O";
    public static final String SURFACE_DISTAL = "D";
    public static final String SURFACE_BUCCAL = "B";
    public static final String SURFACE_LINGUAL = "L";

    public static final String[] ALL_SURFACES = {
        SURFACE_MESIAL, SURFACE_OCCLUSAL, SURFACE_DISTAL, SURFACE_BUCCAL, SURFACE_LINGUAL
    };

    private String surfaces; // O,M,D,B,L
    private Date dateRecorded;

    public ToothStatus() {}

    public ToothStatus(int patientId, int toothNumber, DentalCondition condition, String surfaces, Date dateRecorded) {
        setPatientId(patientId);
        this.toothNumber = toothNumber;
        this.condition = condition;
        this.surfaces = surfaces;
        this.dateRecorded = dateRecorded;
    }

    public int getToothNumber() {
        return toothNumber;
    }

    public void setToothNumber(int toothNumber) {
        this.toothNumber = toothNumber;
    }

    public DentalCondition getCondition() {
        return condition;
    }

    public void setCondition(DentalCondition condition) {
        this.condition = condition;
    }

    public String getSurfaces() {
        return surfaces;
    }

    public void setSurfaces(String surfaces) {
        this.surfaces = surfaces;
    }

    public Date getDateRecorded() {
        return dateRecorded;
    }

    public void setDateRecorded(Date dateRecorded) {
        this.dateRecorded = dateRecorded;
    }
}

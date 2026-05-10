package com.diploma.aerodent.data.local.entity;

// extends BaseEntity, adds patient linking
public abstract class PatientRecordEntity extends BaseEntity {
    private int patientId; // The Foreign Key

    public int getPatientId() {
        return patientId;
    }
    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }
}
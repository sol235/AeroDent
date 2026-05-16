package com.diploma.aerodent.data.local.entity;

import androidx.room.Entity;

import java.util.Date;

@Entity(tableName = "patients")
public class Patient extends BaseEntity {

    private String firstName;
    private String lastName;
    private String egn;
    private String phoneNumber;
    private String email;
    private Date dateOfBirth;
    private String address;
    private String nhifNumber; // НЗОК номер
    private String nhifStatus; // НЗОК статус
    private String notes;
    private Date createdAt;

    // empty constructor for room db
    public Patient() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEgn() {
        return egn;
    }

    public void setEgn(String egn) {
        this.egn = egn;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNhifNumber() {
        return nhifNumber;
    }

    public void setNhifNumber(String nhifNumber) {
        this.nhifNumber = nhifNumber;
    }

    public String getNhifStatus() {
        return nhifStatus;
    }

    public void setNhifStatus(String nhifStatus) {
        this.nhifStatus = nhifStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

}

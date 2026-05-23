package com.diploma.aerodent.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.diploma.aerodent.data.local.model.UserRole;
import com.diploma.aerodent.data.local.model.DentalSpecialty;

import java.util.UUID;

@Entity(tableName = "users")
public class User {

    @PrimaryKey
    @NonNull
    private String id;
    
    @NonNull
    private String fullName;
    
    @NonNull
    private UserRole role;

    @NonNull
    private boolean isActive = true;

    private String pin;
    
    // NZOK
    private String uin; // УИН - Уникален идентификационен номер
    private DentalSpecialty specialty; // Специалност от номенклатура CL006

    public User() {
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    @NonNull
    public String getFullName() { return fullName; }
    public void setFullName(@NonNull String fullName) { this.fullName = fullName; }

    @NonNull
    public UserRole getRole() { return role; }
    public void setRole(@NonNull UserRole role) { this.role = role; }

    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getUin() { return uin; }
    public void setUin(String uin) { this.uin = uin; }

    public DentalSpecialty getSpecialty() { return specialty; }
    public void setSpecialty(DentalSpecialty specialty) { this.specialty = specialty; }
}

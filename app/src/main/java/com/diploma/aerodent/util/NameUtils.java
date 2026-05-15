package com.diploma.aerodent.util;

import com.diploma.aerodent.data.local.entity.Patient;

public class NameUtils {

    /**
     * Returns the patient's first and last name joined by a space.
     */
    public static String formatFirstLastName(Patient patient) {
        if (patient == null) return "";
        
        String first = patient.getFirstName() != null ? patient.getFirstName().trim() : "";
        String last = patient.getLastName() != null ? patient.getLastName().trim() : "";
        
        return (first + " " + last).trim();
    }

    /**
     * Returns initials from the patient's first and last name.
     */
    public static String getInitials(Patient patient) {
        if (patient == null) return "?";
        
        String first = patient.getFirstName() != null && !patient.getFirstName().trim().isEmpty() 
                ? patient.getFirstName().trim().substring(0, 1).toUpperCase() : "";
        String last = patient.getLastName() != null && !patient.getLastName().trim().isEmpty() 
                ? patient.getLastName().trim().substring(0, 1).toUpperCase() : "";
        
        String initials = first + last;
        return initials.isEmpty() ? "?" : initials;
    }
}

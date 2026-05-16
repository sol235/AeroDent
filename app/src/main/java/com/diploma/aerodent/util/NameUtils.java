package com.diploma.aerodent.util;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.Patient;

public class NameUtils {

    public static String formatFirstLastName(Patient patient) {
        if (patient == null) return "";
        
        String first = patient.getFirstName() != null ? patient.getFirstName().trim() : "";
        String last = patient.getLastName() != null ? patient.getLastName().trim() : "";
        
        return (first + " " + last).trim();
    }

    public static String getInitials(Patient patient) {
        if (patient == null) return "?";
        
        String first = patient.getFirstName() != null && !patient.getFirstName().trim().isEmpty() 
                ? patient.getFirstName().trim().substring(0, 1).toUpperCase() : "";
        String last = patient.getLastName() != null && !patient.getLastName().trim().isEmpty() 
                ? patient.getLastName().trim().substring(0, 1).toUpperCase() : "";
        
        String initials = first + last;
        return initials.isEmpty() ? "?" : initials;
    }

    public static int getGenderResourceId(String gender) {
        if (Patient.GENDER_MALE.equals(gender)) return R.string.gender_male;
        if (Patient.GENDER_FEMALE.equals(gender)) return R.string.gender_female;
        return R.string.gender_unknown;
    }
}

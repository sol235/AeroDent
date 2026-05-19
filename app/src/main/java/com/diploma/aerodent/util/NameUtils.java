package com.diploma.aerodent.util;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.Patient;

import java.util.ArrayList;
import java.util.List;

public class NameUtils {

    public static String formatFirstLastName(Patient patient) {
        if (patient == null) return "";
        
        String first = patient.getFirstName() != null ? patient.getFirstName().trim() : "";
        String last = patient.getLastName() != null ? patient.getLastName().trim() : "";
        
        return (first + " " + last).trim();
    }

    public static String formatFullName(Patient patient) {
        if (patient == null) return "";
        
        String first = patient.getFirstName() != null ? patient.getFirstName().trim() : "";
        String middle = patient.getMiddleName() != null ? patient.getMiddleName().trim() : "";
        String last = patient.getLastName() != null ? patient.getLastName().trim() : "";
        
        StringBuilder fullName = new StringBuilder();
        if (!first.isEmpty()) fullName.append(first);
        if (!middle.isEmpty()) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(middle);
        }
        if (!last.isEmpty()) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(last);
        }
        
        return fullName.toString();
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

    public static List<Patient> searchPatients(List<Patient> patients, String query) {
        if (patients == null) return new ArrayList<>();
        if (query == null || query.trim().isEmpty()) return new ArrayList<>(patients);

        String[] terms = query.toLowerCase().trim().split("\\s+");
        List<Patient> filtered = new ArrayList<>();
        
        for (Patient p : patients) {
            String fullName = formatFullName(p).toLowerCase();
            String egn = p.getEgn() != null ? p.getEgn().toLowerCase() : "";
            String phone = p.getPhoneNumber() != null ? p.getPhoneNumber().toLowerCase() : "";
            
            boolean matchesName = true;
            boolean matchesEgn = true;
            boolean matchesPhone = true;

            for (String term : terms) {
                if (!fullName.contains(term)) matchesName = false;
                if (!egn.contains(term)) matchesEgn = false;
                if (!phone.contains(term)) matchesPhone = false;
            }
            
            if (matchesName || matchesEgn || matchesPhone) {
                filtered.add(p);
            }
        }
        return filtered;
    }
}

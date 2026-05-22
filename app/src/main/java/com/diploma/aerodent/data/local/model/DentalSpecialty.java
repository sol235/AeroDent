package com.diploma.aerodent.data.local.model;

import com.diploma.aerodent.R;

public enum DentalSpecialty {
    GENERAL("2081", "64", R.string.specialty_general),
    PEDIATRIC("2082", "61", R.string.specialty_pediatric),
    ORAL_SURGERY("2083", "62", R.string.specialty_oral_surgery),
    MAXILLOFACIAL("2084", "68", R.string.specialty_maxillofacial);

    private final String nzisKey;
    private final String nhifCode;
    private final int displayName;

    DentalSpecialty(String nzisKey, String nhifCode, int displayName) {
        this.nzisKey = nzisKey;
        this.nhifCode = nhifCode;
        this.displayName = displayName;
    }

    public String getNzisKey() {
        return nzisKey;
    }

    public String getNhifCode() {
        return nhifCode;
    }

    public int getDisplayName() {
        return displayName;
    }
}

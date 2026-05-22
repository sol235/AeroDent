package com.diploma.aerodent.data.local.model;

import com.diploma.aerodent.R;

public enum UserRole {
    ADMIN(R.string.role_admin),
    DENTIST(R.string.role_dentist),
    ASSISTANT(R.string.role_assistant);

    private final int displayName;

    UserRole(int displayName) {
        this.displayName = displayName;
    }

    public int getDisplayName() {
        return displayName;
    }
}

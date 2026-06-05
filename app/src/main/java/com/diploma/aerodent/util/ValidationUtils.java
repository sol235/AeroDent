package com.diploma.aerodent.util;

import android.util.Patterns;
import java.util.regex.Pattern;

public class ValidationUtils {

    private static final String PHONE_REGEX = "^(\\+359|0)[0-9]{8,9}$";
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);

    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return true;
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) {
            return true;
        }
        String cleanedPhone = phone.replaceAll("[\\s\\-()]", "");

        return PHONE_PATTERN.matcher(cleanedPhone).matches();
    }

    public static boolean isValidZokNumber(String zok) {
        if (zok == null || zok.trim().isEmpty()) {
            return true;
        }
        return zok.trim().matches("^[0-9]{8}$");
    }
}

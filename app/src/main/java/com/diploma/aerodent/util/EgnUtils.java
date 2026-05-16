package com.diploma.aerodent.util;

import com.diploma.aerodent.data.local.entity.Patient;
import java.util.Calendar;
import java.util.Date;

public class EgnUtils {

    private static boolean validateFormat(String egn) {
        return egn != null && egn.length() == 10 && egn.matches("\\d{10}");
    }

   // Validating EGN

    public static boolean isValidEgn(String egn) {
        if (!validateFormat(egn)) {
            return false;
        }

        int[] weights = {2, 4, 8, 5, 10, 9, 7, 3, 6};
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(egn.charAt(i)) * weights[i];
        }

        int checksum = sum % 11;
        if (checksum == 10) {
            checksum = 0;
        }

        int lastDigit = Character.getNumericValue(egn.charAt(9));
        if (lastDigit != checksum) {
            return false;
        }

        // Validate date part
        return getBirthDate(egn) != null;
    }

    // Get date of birth from EGN

    public static Date getBirthDate(String egn) {
        if (!validateFormat(egn)) {
            return null;
        }

        int year = Integer.parseInt(egn.substring(0, 2));
        int month = Integer.parseInt(egn.substring(2, 4));
        int day = Integer.parseInt(egn.substring(4, 6));

        if (month >= 1 && month <= 12) {
            year += 1900;
        } else if (month >= 21 && month <= 32) {
            month -= 20;
            year += 1800;
        } else if (month >= 41 && month <= 52) {
            month -= 40;
            year += 2000;
        } else {
            return null;
        }

        try {
            Calendar cal = Calendar.getInstance();
            cal.setLenient(false);
            cal.set(year, month - 1, day, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();
        } catch (Exception e) {
            return null;
        }
    }

    // Get gender from EGN

    public static String getGender(String egn) {
        if (egn == null || egn.length() < 9) {
            return null;
        }
        int genderDigit = Character.getNumericValue(egn.charAt(8));
        return (genderDigit % 2 == 0) ? Patient.GENDER_MALE : Patient.GENDER_FEMALE;
    }
}

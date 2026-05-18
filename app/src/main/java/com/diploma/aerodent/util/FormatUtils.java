package com.diploma.aerodent.util;

import android.content.Context;
import com.diploma.aerodent.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    // Formats date and context like 01.06.2026 - appointment

    public static String formatEntryContext(Context context, Date date, Integer appointmentId) {
        String contextStr = (appointmentId == null) ? context.getString(R.string.context_previous)
                : context.getString(R.string.context_appointment);

        String dateStr = "";
        if (date != null) {
            dateStr = DATE_FORMAT.format(date) + " - ";
        }
        return dateStr + contextStr;
    }
}

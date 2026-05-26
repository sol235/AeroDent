package com.diploma.aerodent.util;

import android.content.Context;
import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.ToothStatus;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtils {

    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());


    public static String formatSurfaces(Context context, String surfaces) {
        if (surfaces == null || surfaces.isEmpty()) {
            return surfaces;
        }
        String[] parts = surfaces.split(",");
        StringBuilder localized = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            switch (part) {
                case ToothStatus.SURFACE_MESIAL:
                    localized.append(context.getString(R.string.surface_mesial));
                    break;
                case ToothStatus.SURFACE_OCCLUSAL:
                    localized.append(context.getString(R.string.surface_occlusal));
                    break;
                case ToothStatus.SURFACE_DISTAL:
                    localized.append(context.getString(R.string.surface_distal));
                    break;
                case ToothStatus.SURFACE_BUCCAL:
                    localized.append(context.getString(R.string.surface_vestibular));
                    break;
                case ToothStatus.SURFACE_LINGUAL:
                    localized.append(context.getString(R.string.surface_lingual));
                    break;
                default:
                    localized.append(part);
                    break;
            }
            if (i < parts.length - 1) {
                localized.append(",");
            }
        }
        return localized.toString();
    }

    // Formats date and context like 01.06.2026 - appointment

    public static String formatEntryContext(Context context, Date date, Integer appointmentId, String creatorName) {
        String contextStr = (appointmentId == null) ? context.getString(R.string.context_previous)
                : context.getString(R.string.context_appointment);

        String dateStr = "";
        if (date != null) {
            dateStr = DATETIME_FORMAT.format(date) + " - ";
        }
        
        String result = dateStr + contextStr;
        if (creatorName != null && !creatorName.isEmpty()) {
            result += " - " + creatorName;
        }
        return result;
    }
}

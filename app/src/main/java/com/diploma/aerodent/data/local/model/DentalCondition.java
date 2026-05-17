package com.diploma.aerodent.data.local.model;

import android.content.Context;
import com.diploma.aerodent.R;

public enum DentalCondition {

    // Pathology/Патология
    CARIES("C", R.string.condition_caries, Category.PATHOLOGY, true),
    PULPITIS("P", R.string.condition_pulpitis, Category.PATHOLOGY, false),
    FRACTURE("F", R.string.condition_fracture, Category.PATHOLOGY, true),

    // Restorations/Възстановявания
    OBTURATION("O", R.string.condition_obturation, Category.RESTORATION, true),
    ROOT_CANAL("Rc", R.string.condition_root_canal, Category.RESTORATION, false),
    RADICULAR_POST("Rp", R.string.condition_radicular_post, Category.RESTORATION, false),
    CROWN("K", R.string.condition_crown, Category.RESTORATION, false),
    CROWN_RETAINER("Kb", R.string.condition_crown_retainer, Category.RESTORATION, false),
    SPLINT("S", R.string.condition_splint, Category.RESTORATION, false),

    // Periodontology/Пародонтология
    CALCULUS("T", R.string.condition_calculus, Category.PERIODONTOLOGY, false),
    PERIODONTITIS("G", R.string.condition_periodontitis, Category.PERIODONTOLOGY, false),
    PERIODONTITIS_PA("Pa", R.string.condition_periodontitis_pa, Category.PERIODONTOLOGY, false),
    MOBILITY_1("M1", R.string.condition_mobility_1, Category.PERIODONTOLOGY, false),
    MOBILITY_2("M2", R.string.condition_mobility_2, Category.PERIODONTOLOGY, false),
    MOBILITY_3("M3", R.string.condition_mobility_3, Category.PERIODONTOLOGY, false),

    // Missing-Prosthetics/липсващи-Протези
    MISSING("E", R.string.condition_missing, Category.PROSTHETICS, false),
    IMPLANT("I", R.string.condition_implant, Category.PROSTHETICS, false),
    PONTIC_FIXED("B", R.string.condition_pontic_fixed, Category.PROSTHETICS, false),
    PONTIC_REMOVABLE("X", R.string.condition_pontic_removable, Category.PROSTHETICS, false),
    SUPERNUMERARY("D", R.string.condition_supernumerary, Category.PROSTHETICS, false),
    IMPACTED("Re", R.string.condition_impacted, Category.PROSTHETICS, false),

    // General
    HEALTHY("H", R.string.condition_healthy, Category.GENERAL, false);

    // Var for each enum const
    private final String code;
    private final int displayNameResId;
    private final Category category;
    private final boolean requiresSurfaces;

    // Enum for categories
    public enum Category {
        PATHOLOGY(R.string.category_pathology), RESTORATION(R.string.category_restoration),
        PERIODONTOLOGY(R.string.category_periodontology), PROSTHETICS(R.string.category_prosthetics),
        GENERAL(R.string.category_general);

        public final int titleResId;

        Category(int titleResId) {
            this.titleResId = titleResId;
        }

        public String getTitle(Context context) {
            return context.getString(titleResId);
        }
    }

    DentalCondition(String code, int displayNameResId, Category category, boolean requiresSurfaces) {
        this.code = code;
        this.displayNameResId = displayNameResId;
        this.category = category;
        this.requiresSurfaces = requiresSurfaces;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName(Context context) {
        return context.getString(displayNameResId);
    }

    public Category getCategory() {
        return category;
    }

    public boolean requiresSurfaces() {
        return requiresSurfaces;
    }

    public int getColorResId() {
        switch (category) {
        case PATHOLOGY:
            return R.color.dental_red;
        case RESTORATION:
            return R.color.dental_blue;
        case PERIODONTOLOGY:
            return R.color.dental_green;
        case PROSTHETICS:
            if (this == MISSING) {
                return R.color.dental_grey;
            }
            return R.color.dental_orange;
        case GENERAL:
        default:
            return 0;
        }
    }

    public String getColorHex(Context context) {
        int colorResId = getColorResId();
        if (colorResId == 0) return null;
        int color = context.getColor(colorResId);
        return String.format("#%06X", (0xFFFFFF & color));
    }

    public int getPriority() {
        switch (this) {
        case CARIES:
        case PULPITIS:
        case FRACTURE:
            return 100; // Pathology
        case CROWN:
        case CROWN_RETAINER:
        case OBTURATION:
        case ROOT_CANAL:
        case RADICULAR_POST:
        case SPLINT:
            return 80; // Restorations
        case MISSING:
        case IMPLANT:
        case PONTIC_FIXED:
        case PONTIC_REMOVABLE:
            return 60; // Missing/Prosthetics
        case CALCULUS:
        case PERIODONTITIS:
        case PERIODONTITIS_PA:
        case MOBILITY_1:
        case MOBILITY_2:
        case MOBILITY_3:
            return 40; // Periodontology
        case HEALTHY:
        default:
            return 0;
        }
    }

    // Reverse lookup for room database
    public static DentalCondition fromCode(String code) {
        if (code == null)
            return HEALTHY;
        for (DentalCondition condition : values()) {
            if (condition.code.equalsIgnoreCase(code)) {
                return condition;
            }
        }
        return HEALTHY;
    }
}

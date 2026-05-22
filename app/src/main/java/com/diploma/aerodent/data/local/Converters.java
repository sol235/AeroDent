package com.diploma.aerodent.data.local;

import androidx.room.TypeConverter;

import com.diploma.aerodent.data.local.model.DentalCondition;
import com.diploma.aerodent.data.local.model.DentalSpecialty;
import com.diploma.aerodent.data.local.model.UserRole;

import java.util.Date;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static DentalCondition fromConditionCode(String code) {
        return DentalCondition.fromCode(code);
    }

    @TypeConverter
    public static String conditionToCode(DentalCondition condition) {
        return condition == null ? null : condition.getCode();
    }

    @TypeConverter
    public static UserRole fromUserRoleName(String name) {
        if (name == null) return null;
        try {
            return UserRole.valueOf(name);
        } catch (IllegalArgumentException e) {
            return UserRole.DENTIST;
        }
    }

    @TypeConverter
    public static String userRoleToName(UserRole role) {
        return role == null ? null : role.name();
    }

    @TypeConverter
    public static DentalSpecialty fromSpecialtyName(String name) {
        if (name == null) return null;
        try {
            return DentalSpecialty.valueOf(name);
        } catch (IllegalArgumentException e) {
            return DentalSpecialty.GENERAL;
        }
    }

    @TypeConverter
    public static String specialtyToName(DentalSpecialty specialty) {
        return specialty == null ? null : specialty.name();
    }
}

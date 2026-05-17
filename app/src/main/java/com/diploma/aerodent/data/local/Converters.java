package com.diploma.aerodent.data.local;

import androidx.room.TypeConverter;

import com.diploma.aerodent.data.local.model.DentalCondition;

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
}

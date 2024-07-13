package org.jroadsign.quebec.montreal.src.rpasign.description.common;

import java.time.DayOfWeek;

public class GlobalFunctions {

    private GlobalFunctions() {
    }

    public static int convertMonthNameToNumber(String sMonth) {
        return switch (sMonth) {
            case GlobalConfigs.JANUARY -> 1;
            case GlobalConfigs.FEBRUARY -> 2;
            case GlobalConfigs.MARCH -> 3;
            case GlobalConfigs.APRIL -> 4;
            case GlobalConfigs.MAY -> 5;
            case GlobalConfigs.JUNE -> 6;
            case GlobalConfigs.JULY -> 7;
            case GlobalConfigs.AUGUST -> 8;
            case GlobalConfigs.SEPTEMBER -> 9;
            case GlobalConfigs.OCTOBER -> 10;
            case GlobalConfigs.NOVEMBER -> 11;
            case GlobalConfigs.DECEMBER -> 12;
            default -> throw new IllegalStateException("Unknown month abbreviation: " + sMonth);
        };
    }

    public static DayOfWeek convertToDayOfWeek(String sDayAbbr) {
        return switch (sDayAbbr) {
            case GlobalConfigs.MONDAY -> DayOfWeek.MONDAY;
            case GlobalConfigs.TUESDAY -> DayOfWeek.TUESDAY;
            case GlobalConfigs.WEDNESDAY -> DayOfWeek.WEDNESDAY;
            case GlobalConfigs.THURSDAY -> DayOfWeek.THURSDAY;
            case GlobalConfigs.FRIDAY -> DayOfWeek.FRIDAY;
            case GlobalConfigs.SATURDAY -> DayOfWeek.SATURDAY;
            case GlobalConfigs.SUNDAY -> DayOfWeek.SUNDAY;
            default -> throw new IllegalStateException("Unknown day abbreviation: " + sDayAbbr);
        };
    }

}
// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.src.rpasign.description;

import java.time.DayOfWeek;

public class GlobalFunction {

    public static int convertMonthNameToNumber(String sMonth) {
        return switch (sMonth) {
            case GlobalConfig.JANUARY -> 1;
            case GlobalConfig.FEBRUARY -> 2;
            case GlobalConfig.MARCH -> 3;
            case GlobalConfig.APRIL -> 4;
            case GlobalConfig.MAY -> 5;
            case GlobalConfig.JUNE -> 6;
            case GlobalConfig.JULY -> 7;
            case GlobalConfig.AUGUST -> 8;
            case GlobalConfig.SEPTEMBER -> 9;
            case GlobalConfig.OCTOBER -> 10;
            case GlobalConfig.NOVEMBER -> 11;
            case GlobalConfig.DECEMBER -> 12;
            default -> throw new IllegalStateException("Unknown month abbreviation: " + sMonth);
        };
    }

    public static DayOfWeek convertToDayOfWeek(String sDayAbbr) {
        return switch (sDayAbbr) {
            case GlobalConfig.MONDAY -> DayOfWeek.MONDAY;
            case GlobalConfig.TUESDAY -> DayOfWeek.TUESDAY;
            case GlobalConfig.WEDNESDAY -> DayOfWeek.WEDNESDAY;
            case GlobalConfig.THURSDAY -> DayOfWeek.THURSDAY;
            case GlobalConfig.FRIDAY -> DayOfWeek.FRIDAY;
            case GlobalConfig.SATURDAY -> DayOfWeek.SATURDAY;
            case GlobalConfig.SUNDAY -> DayOfWeek.SUNDAY;
            default -> throw new IllegalStateException("Unknown day abbreviation: " + sDayAbbr);
        };
    }

}
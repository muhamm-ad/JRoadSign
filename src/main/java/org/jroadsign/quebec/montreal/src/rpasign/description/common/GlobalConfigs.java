// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.src.rpasign.description.common;

import org.jroadsign.quebec.montreal.src.rpasign.description.AnnualMonthRange;
import org.jroadsign.quebec.montreal.src.rpasign.description.DailyTimeRange;
import org.jroadsign.quebec.montreal.src.rpasign.description.DurationMinutes;
import org.jroadsign.quebec.montreal.src.rpasign.description.WeeklyDays;

import java.util.*;

public class GlobalConfigs {

    private GlobalConfigs() {
    }

    public static final String TWO_DIGIT = "\\d{1,2}";
    private static final String RANGE_DELIMITER_PATTERN = "\\s*(AU?|-|ET)\\s*";


    /******************************************** DurationMinutes **************************************
     /* Define a regex pattern to capture {@link DurationMinutes}
     /* This will match patterns like "15 MIN", "100 MIN", etc.
     /**************************************************************************************************/
    public static final String DURATION_PATTERN = "(\\d+)%1$sMIN";


    /******************************************** DailyTimeRange ***************************************
     /* Define a regex pattern to capture a {@link DailyTimeRange}
     /* This will match patterns like "10 H 30 A 12 H 30", "9 H - 10 H", "8 H A 9 H", etc.
     /**************************************************************************************************/
    public static final String TIME_PATTERN = "(" + TWO_DIGIT + ")%1$sH%1$s(" + TWO_DIGIT + ")?";
    public static final String DAY_TIME_RANGE_PATTERN = TIME_PATTERN + "%2$s" + TIME_PATTERN;


    /******************************************** WeeklyDays *******************************************
     /* Define a regex pattern to capture a {@link WeeklyDays}
     /* This will match patterns like "LUN AU VEN", "MAR A JEU", "DIM - SAM", etc
     /**************************************************************************************************/
    public static final String ALL_TIME_PATTERN = "EN\\s+TOUT\\s+TEMPS";
    public static final String ALL_TIMES = "EN_TOUT_TEMPS";
    public static final String ALL_TIME_EXCEPT_PATTERN = ALL_TIME_PATTERN + "\\s+EXCEPTE";
    public static final String ALL_TIMES_EXCEPT = "EN_TOUT_TEMPS_EXCEPTE";
    public static final String SCHOOL_DAYS_PATTERN = "JOURS\\s+D'ECOLES?";
    public static final String SCHOOL_DAYS = "JOURS_D_ECOLES";
    public static final String CLASS_DAYS_PATTERN = "JOURS\\s+DE?\\s+CLASSE";
    public static final String CLASS_DAYS = "JOURS_DE_CLASSE";
    public static final String WEEK_END_PATTERN = "WEEKEND";
    public static final String WEEK_END = "WEEK_END";
    public static final String MONDAY_PATTERN = "LUN(?:DI)?";
    public static final String MONDAY = "LUN";
    public static final String TUESDAY_PATTERN = "MAR(?:DI)?";
    public static final String TUESDAY = "MAR";
    public static final String WEDNESDAY_PATTERN = "MER(?:CREDI)?";
    public static final String WEDNESDAY = "MER";
    public static final String THURSDAY_PATTERN = "JEU(?:DI)?";
    public static final String THURSDAY = "JEU";
    public static final String FRIDAY_PATTERN = "VEN(?:DREDI)?";
    public static final String FRIDAY = "VEN";
    public static final String SATURDAY_PATTERN = "SAM(?:EDI)?";
    public static final String SATURDAY = "SAM";
    public static final String SUNDAY_PATTERN = "DIM(?:ANCHE)?";
    public static final String SUNDAY = "DIM";
    public static final String WEEKLY_DAYS_EXPRESSION_PATTERN =
            ALL_TIME_PATTERN + "|" + ALL_TIME_EXCEPT_PATTERN + "|"
                    + SCHOOL_DAYS_PATTERN + "|" + CLASS_DAYS_PATTERN + "|" + WEEK_END_PATTERN;
    public static final String WEEKLY_DAYS_EXPRESSION_LITERAL_PATTERN =
            ALL_TIMES + "|" + ALL_TIMES_EXCEPT + "|" + SCHOOL_DAYS + "|" + CLASS_DAYS + "|" + WEEK_END;
    public static final String WEEKLY_DAYS_PATTERN =
            MONDAY_PATTERN + "|" + TUESDAY_PATTERN + "|" + WEDNESDAY_PATTERN + "|" + THURSDAY_PATTERN
                    + "|" + FRIDAY_PATTERN + "|" + SATURDAY_PATTERN + "|" + SUNDAY_PATTERN;
    public static final String WEEKLY_DAYS_LITERAL_PATTERN =
            MONDAY + "|" + TUESDAY + "|" + WEDNESDAY + "|" + THURSDAY + "|" + FRIDAY + "|" + SATURDAY + "|" + SUNDAY;

    public static final String WEEKLY_DAYS_RANGE_PATTERN =
            "(" + WEEKLY_DAYS_PATTERN + ")(" + RANGE_DELIMITER_PATTERN + "(" + WEEKLY_DAYS_PATTERN + "))*";
    public static final String WEEKLY_DAYS_RANGE_LITERAL_PATTERN =
            "((" + WEEKLY_DAYS_LITERAL_PATTERN + ")(-(" + WEEKLY_DAYS_LITERAL_PATTERN + "))?)" +
                    "(;((" + WEEKLY_DAYS_LITERAL_PATTERN + ")(-(" + WEEKLY_DAYS_LITERAL_PATTERN + "))?))*;*";

    public static final String WEEKLY_DAYS_RANGE_EXPRESSION_PATTERN =
            "(" + WEEKLY_DAYS_RANGE_PATTERN + ")|(" + WEEKLY_DAYS_EXPRESSION_PATTERN + ")";
    public static final String WEEKLY_DAYS_RANGE_EXPRESSION_LITERAL_PATTERN =
            "((" + WEEKLY_DAYS_RANGE_LITERAL_PATTERN + ")|(" + WEEKLY_DAYS_EXPRESSION_LITERAL_PATTERN + "))" +
                    "(;((" + WEEKLY_DAYS_RANGE_LITERAL_PATTERN + ")|(" + WEEKLY_DAYS_EXPRESSION_LITERAL_PATTERN + ")))*";

    public static final Map<String, String> WEEKLY_DAYS_ABBREVIATIONS_MAP = new HashMap<>();

    static {
        WEEKLY_DAYS_ABBREVIATIONS_MAP.put(MONDAY_PATTERN, MONDAY);
        WEEKLY_DAYS_ABBREVIATIONS_MAP.put(TUESDAY_PATTERN, TUESDAY);
        WEEKLY_DAYS_ABBREVIATIONS_MAP.put(WEDNESDAY_PATTERN, WEDNESDAY);
        WEEKLY_DAYS_ABBREVIATIONS_MAP.put(THURSDAY_PATTERN, THURSDAY);
        WEEKLY_DAYS_ABBREVIATIONS_MAP.put(FRIDAY_PATTERN, FRIDAY);
        WEEKLY_DAYS_ABBREVIATIONS_MAP.put(SATURDAY_PATTERN, SATURDAY);
        WEEKLY_DAYS_ABBREVIATIONS_MAP.put(SUNDAY_PATTERN, SUNDAY);
    }


    /******************************************* AnnualMonthRange *************************************
     /* Define a regex pattern to capture a {@link AnnualMonthRange}
     /* This will match patterns like "1 JAN AU 1 DEC", "JAN AU DEC", "1ER FEV - 1ER NOV", etc.
     /*************************************************************************************************/
    public static final String JANUARY_PATTERN = "JAN(?:V(?:IER)?)?";
    public static final String JANUARY = "JAN";
    public static final String FEBRUARY_PATTERN = "FEV(?:RIER)?";
    public static final String FEBRUARY = "FEV";
    public static final String MARCH_PATTERN = "MARS";
    public static final String MARCH = "MARS";
    public static final String APRIL_PATTERN = "AVR(?:IL)?";
    public static final String APRIL = "AVR";
    public static final String MAY_PATTERN = "MAI";
    public static final String MAY = "MAI";
    public static final String JUNE_PATTERN = "JUIN";
    public static final String JUNE = "JUIN";
    public static final String JULY_PATTERN = "JUIL(?:LET)?";
    public static final String JULY = "JUIL";
    public static final String AUGUST_PATTERN = "AOUT";
    public static final String AUGUST = "AOUT";
    public static final String SEPTEMBER_PATTERN = "SEPT(?:EMBRE)?";
    public static final String SEPTEMBER = "SEP";
    public static final String OCTOBER_PATTERN = "OCT(?:OBRE)?";
    public static final String OCTOBER = "OCT";
    public static final String NOVEMBER_PATTERN = "NOV(?:EMBRE)?";
    public static final String NOVEMBER = "NOV";
    public static final String DECEMBER_PATTERN = "DEC(?:EMBRE)?";
    public static final String DECEMBER = "DEC";

    public static final String ANNUAL_MONTH_PATTERN =
            JANUARY_PATTERN + "|" + FEBRUARY_PATTERN + "|" + MARCH_PATTERN + "|" + APRIL_PATTERN + "|"
                    + MAY_PATTERN + "|" + JUNE_PATTERN + "|" + JULY_PATTERN + "|" + AUGUST_PATTERN + "|"
                    + SEPTEMBER_PATTERN + "|" + OCTOBER_PATTERN + "|" + NOVEMBER_PATTERN + "|" + DECEMBER_PATTERN;
    public static final String ANNUAL_MONTH_LITERAL_PATTERN =
            JANUARY + "|" + FEBRUARY + "|" + MARCH + "|" + APRIL + "|" + MAY + "|" + JUNE + "|" + JULY + "|"
                    + AUGUST + "|" + SEPTEMBER + "|" + OCTOBER + "|" + NOVEMBER + "|" + DECEMBER;


    // Pattern for matching a date range in the format: DAY MONTH - DAY MONTH
    public static final String ANNUAL_MONTH_RANGE_PATTERN_FIRST =
            "(" + TWO_DIGIT + ")\\s*(" + ANNUAL_MONTH_PATTERN + ")" + RANGE_DELIMITER_PATTERN
                    + "(" + TWO_DIGIT + ")\\s*(" + ANNUAL_MONTH_PATTERN + ")";

    // Pattern for matching a date range in the format: MONTH DAY - MONTH DAY
    public static final String ANNUAL_MONTH_RANGE_PATTERN_SECOND =
            "(" + ANNUAL_MONTH_PATTERN + ")\\s*(" + TWO_DIGIT + ")" + RANGE_DELIMITER_PATTERN
                    + "(" + ANNUAL_MONTH_PATTERN + ")\\s*(" + TWO_DIGIT + ")";
    public static final String ANNUAL_MONTH_RANGE_PATTERN =
            "(" + ANNUAL_MONTH_RANGE_PATTERN_FIRST + "|" + ANNUAL_MONTH_RANGE_PATTERN_SECOND + ")";


    // Pattern for matching a date range in the format: DAY L_MONTH - DAY L_MONTH
    public static final String ANNUAL_MONTH_RANGE_LITERAL_PATTERN_FIRST =
            "(" + TWO_DIGIT + ")\\s(" + ANNUAL_MONTH_LITERAL_PATTERN + ")\\s-\\s("
                    + TWO_DIGIT + ")\\s(" + ANNUAL_MONTH_LITERAL_PATTERN + ")";

    // Pattern for matching a date range in the format: L_MONTH DAY - L_MONTH DAY
    public static final String ANNUAL_MONTH_RANGE_LITERAL_PATTERN_SECOND =
            "(" + ANNUAL_MONTH_LITERAL_PATTERN + ")\\s(" + TWO_DIGIT + ")\\s-\\s("
                    + ANNUAL_MONTH_LITERAL_PATTERN + ")\\s(" + TWO_DIGIT + ")";
    public static final String ANNUAL_MONTH_RANGE_LITERAL_PATTERN =
            "((" + ANNUAL_MONTH_RANGE_LITERAL_PATTERN_FIRST + ")|(" + ANNUAL_MONTH_RANGE_LITERAL_PATTERN_SECOND + "))";


    public static final Map<String, String> ANNUAL_MONTH_ABBREVIATIONS_MAP = new HashMap<>();

    static {
        ANNUAL_MONTH_ABBREVIATIONS_MAP.put(JANUARY_PATTERN, JANUARY);
        ANNUAL_MONTH_ABBREVIATIONS_MAP.put(FEBRUARY_PATTERN, FEBRUARY);
        //ANNUAL_MONTH_ABBREVIATIONS_MAP.put(MARCH_PATTERN, MARCH);
        ANNUAL_MONTH_ABBREVIATIONS_MAP.put(APRIL_PATTERN, APRIL);
        //ANNUAL_MONTH_ABBREVIATIONS_MAP.put(MAY_PATTERN, MAY);
        //ANNUAL_MONTH_ABBREVIATIONS_MAP.put(JUNE_PATTERN, JUNE);
        ANNUAL_MONTH_ABBREVIATIONS_MAP.put(JULY_PATTERN, JULY);
        //ANNUAL_MONTH_ABBREVIATIONS_MAP.put(AUGUST_PATTERN, AUGUST);
        ANNUAL_MONTH_ABBREVIATIONS_MAP.put(SEPTEMBER_PATTERN, SEPTEMBER);
        ANNUAL_MONTH_ABBREVIATIONS_MAP.put(OCTOBER_PATTERN, OCTOBER);
        ANNUAL_MONTH_ABBREVIATIONS_MAP.put(NOVEMBER_PATTERN, NOVEMBER);
        ANNUAL_MONTH_ABBREVIATIONS_MAP.put(DECEMBER_PATTERN, DECEMBER);
    }


    /******************************************* Other variables *******************************************/
    public static final List<String> LIST_OF_METADATA_TO_IGNORE =
            new ArrayList<>(Arrays.asList("STAT INT", "STAT INT DE", "TEMPS", "NO PARKING", "NON CONFORME"));

}
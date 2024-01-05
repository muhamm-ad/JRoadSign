// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.src.rpasign.description;

import java.util.regex.Pattern;

public class GlobalConfig {

    /**
     * {@link DurationMinutes}
     **/
    public static final String DURATION_PATTERN = "^(\\d+)\\sMIN$";
    public static final Pattern COMPILED_DURATION_PATTERN = Pattern.compile(DURATION_PATTERN);


    /**
     * {@link DailyTimeRange}
     **/
    public static final String TIME_PATTERN = "(\\d{1,2})H(\\d{0,2})?";
    public static final String DAY_TIME_RANGE_PATTERN = "^" + TIME_PATTERN + "-" + TIME_PATTERN + "$";
    public static final Pattern COMPILED_DAY_TIME_RANGE_PATTERN = Pattern.compile(DAY_TIME_RANGE_PATTERN);


    /**
     * {@link WeeklyDays}
     **/

    public static final String ALL_TIMES = "EN_TOUT_TEMPS";
    public static final String SCHOOL_DAYS = "JOURS_D_ECOLES";
    public static final String CLASS_DAYS = "JOURS_DE_CLASSE";
    public static final String WEEK_END = "WEEK_END";
    public static final String MONDAY = "LUN";
    public static final String TUESDAY = "MAR";
    public static final String WEDNESDAY = "MER";
    public static final String THURSDAY = "JEU";
    public static final String FRIDAY = "VEN";
    public static final String SATURDAY = "SAM";
    public static final String SUNDAY = "DIM";
    public static final String WEEKDAY_EXPRESSION_PATTERN =
            "(" + ALL_TIMES + "|" + SCHOOL_DAYS + "|" + CLASS_DAYS + "|" + WEEK_END + ")";
    public static final String WEEKDAY_PATTERN =
            "((LUN|MAR|MER|JEU|VEN|SAM|DIM)(-(LUN|MAR|MER|JEU|VEN|SAM|DIM))?)" +
                    "(;((LUN|MAR|MER|JEU|VEN|SAM|DIM)(-(LUN|MAR|MER|JEU|VEN|SAM|DIM))?))*;*";
    public static final Pattern COMPILED_WEEKDAY_PATTERN = Pattern.compile(
            "((" + WEEKDAY_PATTERN + ")|(" + WEEKDAY_EXPRESSION_PATTERN + "))" +
                    "(;((" + WEEKDAY_PATTERN + ")|(" + WEEKDAY_EXPRESSION_PATTERN + ")))*"
    );
    /**
     * {@link AnnualMonthRange}
     **/
    public static final String MONTH_NAME_PATTERN = "(JAN|FEV|MARS|AVR|MAI|JUIN|JUIL|AOUT|SEP|OCT|NOV|DEC)";
    public static final String ANNUAL_MONTH_RANGE_PATTERN =
            "^(\\d{1,2})\\s" + MONTH_NAME_PATTERN
                    + "\\s-\\s" +
                    "(\\d{1,2})\\s" + MONTH_NAME_PATTERN + "$";
    public static final Pattern COMPILED_ANNUAL_MONTH_RANGE_PATTERN = Pattern.compile(ANNUAL_MONTH_RANGE_PATTERN);

    public static final String JANUARY = "JAN";
    public static final String FEBRUARY = "FEV";
    public static final String MARCH = "MARS";
    public static final String APRIL = "AVR";
    public static final String MAY = "MAI";
    public static final String JUNE = "JUIN";
    public static final String JULY = "JUIL";
    public static final String AUGUST = "AOUT";
    public static final String SEPTEMBER = "SEP";
    public static final String OCTOBER = "OCT";
    public static final String NOVEMBER = "NOV";
    public static final String DECEMBER = "DEC";

}
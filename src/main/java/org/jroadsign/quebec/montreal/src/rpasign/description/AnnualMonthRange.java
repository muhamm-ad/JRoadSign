// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.src.rpasign.description;

import java.time.DateTimeException;
import java.time.MonthDay;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnnualMonthRange {
    private static final String MONTH_NAME_PATTERN = "(JAN|FEV|MARS|AVR|MAI|JUIN|JUIL|AOUT|SEP|OCT|NOV|DEC)";
    private static final String ANNUAL_MONTH_RANGE_PATTERN =
            "^(\\d{1,2})\\s" + MONTH_NAME_PATTERN + "\\s-\\s" + "(\\d{1,2})\\s" + MONTH_NAME_PATTERN + "$";
    private static final Pattern COMPILED_ANNUAL_MONTH_RANGE_PATTERN =
            Pattern.compile(ANNUAL_MONTH_RANGE_PATTERN);
    private static final String MSG_ERR_INVALID_FORMAT_S_ARG =
            "Invalid DailyTimeRange format: %s. Expected format: `startDay startMonth - endMonth endMonth`";
    private static final String MSG_ERR_START_AFTER_END =
            "Start date %s is after end date %s. AnnualMonthRange should be within the same year.";

    private MonthDay start;
    private MonthDay end;

    public AnnualMonthRange(String sAnnualMonthRange) {
        Matcher matcher = COMPILED_ANNUAL_MONTH_RANGE_PATTERN.matcher(sAnnualMonthRange);
        if (!matcher.find())
            throw new IllegalArgumentException(String.format(MSG_ERR_INVALID_FORMAT_S_ARG, sAnnualMonthRange));

        MonthDay pStart = parseMonthDay(matcher.group(1), matcher.group(2));
        MonthDay pEnd = parseMonthDay(matcher.group(3), matcher.group(4));

        validateAndSetRange(pStart, pEnd);
    }

    public AnnualMonthRange(MonthDay start, MonthDay end) {
        validateAndSetRange(start, end);
    }

    private void validateAndSetRange(MonthDay start, MonthDay end) {
        if (start.isAfter(end))
            throw new IllegalArgumentException(String.format(MSG_ERR_START_AFTER_END, start, end));
        this.start = start;
        this.end = end;
    }

    private int convertMonthNameToNumber(String sMonth) {
        return switch (sMonth) {
            case "JAN" -> 1;
            case "FEV" -> 2;
            case "MARS" -> 3;
            case "AVR" -> 4;
            case "MAI" -> 5;
            case "JUIN" -> 6;
            case "JUIL" -> 7;
            case "AOUT" -> 8;
            case "SEP" -> 9;
            case "OCT" -> 10;
            case "NOV" -> 11;
            case "DEC" -> 12;
            default -> throw new IllegalStateException("Unknown month abbreviation: " + sMonth);
        };
    }

    private MonthDay parseMonthDay(String sDay, String sMonth) {
        int day = Integer.parseInt(sDay);
        int month = convertMonthNameToNumber(sMonth);

        try {
            return MonthDay.of(month, day);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Invalid date provided: " + sMonth + " " + sDay, e);
        }
    }

    public MonthDay getStart() {
        return this.start;
    }

    public MonthDay getEnd() {
        return this.end;
    }

    public void setStart(MonthDay start) {
        if (start.isAfter(this.end))
            throw new IllegalArgumentException(String.format(MSG_ERR_START_AFTER_END, start, this.end));
        this.start = start;
    }

    public void setEnd(MonthDay end) {
        if (end.isBefore(this.start))
            throw new IllegalArgumentException(String.format(MSG_ERR_START_AFTER_END, this.start, end));
        this.end = end;
    }

    public boolean isWithinRange(MonthDay oMonthDay) {
        if (start.isBefore(end) || start.equals(end)) {
            return !oMonthDay.isBefore(start) && !oMonthDay.isAfter(end);
        } else {
            return !oMonthDay.isBefore(start) && oMonthDay.isAfter(end);
        }
    }

    @Override
    public String toString() {
        return "AnnualMonthRange{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }

}

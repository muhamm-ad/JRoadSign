// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.src.rpasign.description;

import java.time.DateTimeException;
import java.time.MonthDay;
import java.util.regex.Matcher;

public class AnnualMonthRange {
    private static final String MSG_ERR_INVALID_FORMAT_S_ARG =
            "Invalid AnnualMonthRange format: `%s`. Expected format: `startDay startMonth - endMonth endMonth`";
    private static final String MSG_ERR_START_AFTER_END =
            "Start date %s is after end date %s. AnnualMonthRange should be within the same year.";

    private MonthDay start;
    private MonthDay end;

    public AnnualMonthRange(String sAnnualMonthRange) throws StartAfterEndException {
        Matcher matcher = GlobalConfig.COMPILED_ANNUAL_MONTH_RANGE_PATTERN.matcher(sAnnualMonthRange);
        if (!matcher.find())
            throw new IllegalArgumentException(String.format(MSG_ERR_INVALID_FORMAT_S_ARG, sAnnualMonthRange));

        MonthDay pStart = parseMonthDay(matcher.group(1), matcher.group(2));
        MonthDay pEnd = parseMonthDay(matcher.group(3), matcher.group(4));

        validateAndSetRange(pStart, pEnd);
    }

    public AnnualMonthRange(MonthDay start, MonthDay end) throws StartAfterEndException {
        validateAndSetRange(start, end);
    }

    private void validateRange(MonthDay start, MonthDay end) throws StartAfterEndException {
        if (start.isAfter(end))
            throw new StartAfterEndException(String.format(MSG_ERR_START_AFTER_END, start, end));
    }

    private void validateAndSetRange(MonthDay start, MonthDay end) throws StartAfterEndException {
        validateRange(start, end);
        this.start = start;
        this.end = end;
    }

    private MonthDay parseMonthDay(String sDay, String sMonth) {
        int day = Integer.parseInt(sDay);
        int month = GlobalFunction.convertMonthNameToNumber(sMonth);

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

    public void setStart(MonthDay start) throws StartAfterEndException {
        validateRange(start, this.end);
        this.start = start;
    }

    public void setEnd(MonthDay end) throws StartAfterEndException {
        validateRange(this.start, end);
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

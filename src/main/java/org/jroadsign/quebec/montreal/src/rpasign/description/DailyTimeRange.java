// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.src.rpasign.description;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DailyTimeRange {

    private static final String TIME_PATTERN = "(\\d{1,2})H(\\d{0,2})?";
    private static final String DAY_TIME_RANGE_PATTERN = "^" + TIME_PATTERN + "-" + TIME_PATTERN + "$";
    private static final Pattern COMPILED_DAY_TIME_RANGE_PATTERN = Pattern.compile(DAY_TIME_RANGE_PATTERN);
    private static final String MSG_ERR_INVALID_FORMAT_S_ARG =
            "Invalid DailyTimeRange format: %s. Expected format: " + DAY_TIME_RANGE_PATTERN;
    private static final String MSG_ERR_START_AFTER_END =
            "Start time %s is after end time %s. DailyTimeRange should be within the same day.";

    private LocalTime start;
    private LocalTime end;

    public DailyTimeRange(String sDailyTimeRange) {
        Matcher matcher = COMPILED_DAY_TIME_RANGE_PATTERN.matcher(sDailyTimeRange);
        if (!matcher.find())
            throw new IllegalArgumentException(String.format(MSG_ERR_INVALID_FORMAT_S_ARG, sDailyTimeRange));

        LocalTime pStart = parseTime(matcher.group(1), matcher.group(2));
        LocalTime pEnd = parseTime(matcher.group(3), matcher.group(4));

        validateAndSetRange(pStart, pEnd);
    }

    public DailyTimeRange(LocalTime start, LocalTime end) {
        validateAndSetRange(start, end);
    }

    private void validateAndSetRange(LocalTime start, LocalTime end) {
        if (start.isAfter(end))
            throw new IllegalArgumentException(String.format(MSG_ERR_START_AFTER_END, start, end));
        this.start = start;
        this.end = end;
    }

    private LocalTime parseTime(String sHour, String sMinute) {
        int h = Integer.parseInt(sHour);
        int m = (sMinute != null && !sMinute.isEmpty()) ? Integer.parseInt(sMinute) : 0;

        try {
            return LocalTime.of(h, m);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Invalid time provided: " + sHour + " " + sMinute, e);
        }
    }

    public LocalTime getStart() {
        return this.start;
    }

    public LocalTime getEnd() {
        return this.end;
    }

    public void setStart(LocalTime start) {
        if (start.isAfter(this.end))
            throw new IllegalArgumentException(String.format(MSG_ERR_START_AFTER_END, start, this.end));
        this.start = start;
    }

    public void setEnd(LocalTime end) {
        if (end.isBefore(this.start))
            throw new IllegalArgumentException(String.format(MSG_ERR_START_AFTER_END, this.start, end));
        this.end = end;
    }

    public Duration getDuration() {
        return Duration.between(this.start, this.end);
    }

    public boolean isWithinRange(LocalTime time) {
        return (time.equals(this.start) || time.isAfter(this.start)) && time.isBefore(this.end);
    }

    @Override
    public String toString() {
        return "DailyTimeRange{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}

// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.src.rpasign.description;

import org.jroadsign.quebec.montreal.src.rpasign.description.common.GlobalConfigs;
import org.jroadsign.quebec.montreal.src.rpasign.description.exceptions.StartAfterEndException;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DailyTimeRange {

    public static final String MSG_ERR_INVALID_FORMAT_S_ARG =
            "Invalid DailyTimeRange format: %s. Expected format: "
                    + String.format(GlobalConfigs.DAY_TIME_RANGE_PATTERN, "", "-");
    public static final String MSG_ERR_START_AFTER_END =
            "Start time `%s` is after end time `%s`. DailyTimeRange should be within the same day.";
    private static final Pattern COMPILED_DAY_TIME_RANGE_PATTERN = Pattern.compile(
            "^" + String.format(GlobalConfigs.DAY_TIME_RANGE_PATTERN, "", "-") + "$");

    private Range<LocalTime> range;

    public DailyTimeRange(String sDailyTimeRange) throws StartAfterEndException {
        Matcher matcher = COMPILED_DAY_TIME_RANGE_PATTERN.matcher(sDailyTimeRange);
        if (!matcher.find())
            throw new IllegalArgumentException(String.format(MSG_ERR_INVALID_FORMAT_S_ARG, sDailyTimeRange));

        LocalTime pStart = parseTime(matcher.group(1), matcher.group(2));
        LocalTime pEnd = parseTime(matcher.group(3), matcher.group(4));

        validateAndSetRange(pStart, pEnd);
    }

    public DailyTimeRange(Range<LocalTime> oRange) throws StartAfterEndException {
        validateAndSetRange(oRange.getStart(), oRange.getEnd());
    }

    private void validateRange(LocalTime start, LocalTime end) throws StartAfterEndException {
        if (start.isAfter(end))
            throw new StartAfterEndException(
                    String.format(MSG_ERR_START_AFTER_END, start, end), new Range<>(start, end));
    }

    private void validateAndSetRange(LocalTime start, LocalTime end) throws StartAfterEndException {
        validateRange(start, end);
        range = new Range<>(start, end);
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

    public Range<LocalTime> getRange() {
        return range;
    }

    public void setRange(Range<LocalTime> range) {
        this.range = range;
    }

    public LocalTime getStart() {
        return range.getStart();
    }

    public void setStart(LocalTime start) throws StartAfterEndException {
        validateRange(start, range.getEnd());
        range.setStart(start);
    }

    public LocalTime getEnd() {
        return range.getEnd();
    }

    public void setEnd(LocalTime end) throws StartAfterEndException {
        validateRange(range.getStart(), end);
        range.setEnd(end);
    }

    public Duration getDuration() {
        return Duration.between(range.getStart(), range.getEnd());
    }

    public boolean isWithinRange(LocalTime time) {
        return (time.equals(range.getStart()) || time.isAfter(range.getStart())) && time.isBefore(range.getEnd());
    }

    @Override
    public String toString() {
        return "DailyTimeRange" + range;
    }
}

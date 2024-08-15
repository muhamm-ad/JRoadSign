package org.jroadsign.canada.quebec.montreal.rpasign.rpasigndesc;

import org.jetbrains.annotations.NotNull;
import org.jroadsign.canada.quebec.montreal.rpasign.rpasigndesc.common.GlobalConfigs;
import org.jroadsign.canada.quebec.montreal.rpasign.rpasigndesc.common.GlobalFunctions;
import org.jroadsign.canada.quebec.montreal.rpasign.rpasigndesc.exceptions.StartAfterEndException;
import org.json.JSONObject;

import java.time.DateTimeException;
import java.time.MonthDay;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author - <a href="https://github.com/muhamm-ad">muhamm-ad</a>
 * @project JRoadSign
 */
public class AnnualMonthRange {
    public static final String MSG_ERR_INVALID_FORMAT_S_ARG =
            "Invalid AnnualMonthRange format: `%s`. Expected format:"
                    + GlobalConfigs.ANNUAL_MONTH_RANGE_LITERAL_PATTERN_FIRST;
    public static final String MSG_ERR_START_AFTER_END =
            "Start date %s is after end date %s. AnnualMonthRange should be within the same year.";

    private static final Pattern COMPILED_ANNUAL_MONTH_RANGE_PATTERN =
            Pattern.compile("^" + GlobalConfigs.ANNUAL_MONTH_RANGE_LITERAL_PATTERN_FIRST + "$");


    private Range<MonthDay> range;

    public AnnualMonthRange(@NotNull String sAnnualMonthRange) throws StartAfterEndException {
        Matcher matcher = COMPILED_ANNUAL_MONTH_RANGE_PATTERN.matcher(sAnnualMonthRange);
        if (!matcher.find())
            throw new IllegalArgumentException(String.format(MSG_ERR_INVALID_FORMAT_S_ARG, sAnnualMonthRange));

        MonthDay pStart = parseMonthDay(matcher.group(1), matcher.group(2));
        MonthDay pEnd = parseMonthDay(matcher.group(3), matcher.group(4));

        validateAndSetRange(pStart, pEnd);
    }

    public AnnualMonthRange(Range<MonthDay> oRange) throws StartAfterEndException {
        validateAndSetRange(oRange.getStart(), oRange.getEnd());
    }

    private void validateRange(MonthDay start, MonthDay end) throws StartAfterEndException {
        if (start.isAfter(end))
            throw new StartAfterEndException(
                    String.format(MSG_ERR_START_AFTER_END, start, end), new Range<>(start, end));
    }

    private void validateAndSetRange(MonthDay start, MonthDay end) throws StartAfterEndException {
        validateRange(start, end);
        range = new Range<>(start, end);
    }

    private MonthDay parseMonthDay(String sDay, String sMonth) {
        int day = Integer.parseInt(sDay);
        int month = GlobalFunctions.convertMonthNameToNumber(sMonth);

        try {
            return MonthDay.of(month, day);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Invalid date provided: " + sMonth + " " + sDay, e);
        }
    }

    public Range<MonthDay> getRange() {
        return range;
    }

    public void setRange(Range<MonthDay> range) {
        this.range = range;
    }

    public MonthDay getStart() {
        return range.getStart();
    }

    public void setStart(MonthDay start) throws StartAfterEndException {
        validateRange(start, range.getEnd());
        range.setStart(start);
    }

    public MonthDay getEnd() {
        return range.getEnd();
    }

    public void setEnd(MonthDay end) throws StartAfterEndException {
        validateRange(range.getStart(), end);
        range.setEnd(end);
    }

    public boolean isWithinRange(MonthDay oMonthDay) {
        MonthDay start = range.getStart();
        MonthDay end = range.getEnd();
        if (start.isBefore(end) || start.equals(end)) {
            return !oMonthDay.isBefore(start) && !oMonthDay.isAfter(end);
        } else {
            return !oMonthDay.isBefore(start) && oMonthDay.isAfter(end);
        }
    }

    @Override
    public String toString() {
        return "AnnualMonthRange" + range;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("start", range.getStart());
        json.put("end", range.getEnd());
        return json;
    }
}

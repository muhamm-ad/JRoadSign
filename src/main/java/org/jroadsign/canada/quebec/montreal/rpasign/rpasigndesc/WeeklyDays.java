package org.jroadsign.canada.quebec.montreal.rpasign.rpasigndesc;

import org.jetbrains.annotations.NotNull;
import org.jroadsign.canada.quebec.montreal.rpasign.rpasigndesc.common.GlobalConfigs;
import org.jroadsign.canada.quebec.montreal.rpasign.rpasigndesc.common.GlobalFunctions;
import org.jroadsign.canada.quebec.montreal.rpasign.rpasigndesc.exceptions.WeeklyRangeExpException;

import java.time.DayOfWeek;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author - <a href="https://github.com/muhamm-ad">muhamm-ad</a>
 * @project JRoadSign
 */
public class WeeklyDays {
    private static final String MSG_ERR_INVALID_FORMAT_S_ARG =
            "Invalid WeeklyDays format: `%s`. Expected format: "
                    + GlobalConfigs.WEEKLY_DAYS_RANGE_EXPRESSION_LITERAL_PATTERN;
    private static final Pattern COMPILED_WEEKLY_DAY_RANGE_EXPRESSION_LITERAL_PATTERN = Pattern.compile(
            "^" + GlobalConfigs.WEEKLY_DAYS_RANGE_EXPRESSION_LITERAL_PATTERN + "$");


    private Set<DayOfWeek> days;

    public WeeklyDays() {
        this.days = new LinkedHashSet<>();
    }

    public WeeklyDays(@NotNull String sWeeklyDays) throws WeeklyRangeExpException {
        Matcher matcher = COMPILED_WEEKLY_DAY_RANGE_EXPRESSION_LITERAL_PATTERN.matcher(sWeeklyDays);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format(MSG_ERR_INVALID_FORMAT_S_ARG, sWeeklyDays));
        }

        this.days = new LinkedHashSet<>();
        processElements(sWeeklyDays);
    }

    public WeeklyDays(@NotNull WeekRangeExpression expression) {
        this.days = new LinkedHashSet<>();
        initializeFromExpression(expression);
    }

    private void processElements(@NotNull String sWeeklyDays) throws WeeklyRangeExpException {
        String[] elements = sWeeklyDays.split(";");
        boolean except = false;
        WeekRangeExpression exceptExp = null;
        for (String element : elements) {
            if (element.equalsIgnoreCase(GlobalConfigs.ALL_TIMES_EXCEPT)) {
                except = true;
                exceptExp = WeekRangeExpression.ALL_TIMES_EXCEPT;
            } else {
                try {
                    if (element.contains("-"))
                        handleInterval(element);
                    else
                        handleDay(element);
                } catch (IllegalArgumentException | IllegalStateException e) {
                    WeekRangeExpression expression = WeekRangeExpression.fromString(element);
                    if (expression != null) {
                        initializeFromExpression(expression);
                    } else {
                        throw new IllegalArgumentException(String.format(MSG_ERR_INVALID_FORMAT_S_ARG, element));
                    }
                }
            }
        }
        if (except) throw new WeeklyRangeExpException(exceptExp, this);
    }


    private void handleInterval(@NotNull String interval) {
        String[] daysInterval = interval.split("-");
        DayOfWeek start = GlobalFunctions.convertToDayOfWeek(daysInterval[0].trim());
        DayOfWeek end = GlobalFunctions.convertToDayOfWeek(daysInterval[1].trim());

        if (start == null || end == null) {
            throw new IllegalArgumentException(
                    String.format(MSG_ERR_INVALID_FORMAT_S_ARG, interval));
        }

        if (start.getValue() <= end.getValue()) {
            this.days.addAll(EnumSet.range(start, end));
        } else {
            this.days.addAll(EnumSet.range(start, DayOfWeek.SUNDAY));
            this.days.addAll(EnumSet.range(DayOfWeek.MONDAY, end));
        }
    }

    private void handleDay(String dayStr) {
        DayOfWeek day = GlobalFunctions.convertToDayOfWeek(dayStr);
        this.days.add(day);
    }

    private void initializeFromExpression(@NotNull WeekRangeExpression expression) {
        switch (expression) {
            case ALL_TIMES -> this.days = EnumSet.range(DayOfWeek.MONDAY, DayOfWeek.SUNDAY);
            case SCHOOL_DAYS -> this.days = EnumSet.range(DayOfWeek.MONDAY, DayOfWeek.FRIDAY);
            case WEEK_END -> this.days = EnumSet.range(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
            // case ALL_TIMES_EXCEPT ->
            default -> throw new IllegalStateException("Unknown expression: " + expression);
        }
    }

    public Set<DayOfWeek> getDays() {
        return this.days;
    }

    public void setDays(@NotNull Set<DayOfWeek> days) {
        this.days = days;
    }

    public void addDay(@NotNull DayOfWeek day) {
        this.days.add(day);
    }

    public void removeDay(@NotNull DayOfWeek day) {
        this.days.remove(day);
    }

    public boolean contains(@NotNull DayOfWeek day) {
        return this.days.contains(day);
    }

    public boolean isEmpty() {
        return this.days.isEmpty();
    }

    @Override
    public String toString() {
        return "WeeklyDays{" + days + '}';
    }

}


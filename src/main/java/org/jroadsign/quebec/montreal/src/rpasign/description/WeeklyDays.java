// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.src.rpasign.description;

import java.time.DayOfWeek;
import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Matcher;

public class WeeklyDays {
    private static final String MSG_ERR_INVALID_FORMAT_S_ARG =
            "Invalid WeeklyDays format: `%s`. Expected format: " +
                    "[Day Abbreviation] or [Day Abbreviation]-[Day Abbreviation]";
    private static final String MSG_ERR_UNKNOWN_DAY_ABR = "Unknown day abbreviation: %s";

    private EnumSet<DayOfWeek> days;

    public WeeklyDays() {
        this.days = EnumSet.noneOf(DayOfWeek.class);
    }

    public WeeklyDays(String sWeeklyDays) {
        validateStringInput(sWeeklyDays);

        this.days = EnumSet.noneOf(DayOfWeek.class);
        String[] elements = sWeeklyDays.split(";");

        if (processElements(elements)) {
            initializeFromExpression(WeekRangeExpression.ALL_TIMES_EXCEPT);
        }
    }

    public WeeklyDays(WeekRangeExpression expression) {
        this.days = EnumSet.noneOf(DayOfWeek.class);
        initializeFromExpression(expression);
    }

    private void validateStringInput(String input) {
        Matcher matcher = GlobalConfig.COMPILED_WEEKDAY_PATTERN.matcher(input);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format(MSG_ERR_INVALID_FORMAT_S_ARG, input));
        }
    }

    private boolean processElements(String[] elements) {
        boolean isAllTimeExcept = false;
        for (String element : elements) {
            if (element.equalsIgnoreCase(GlobalConfig.ALL_TIMES_EXCEPT)) {
                isAllTimeExcept = true;
            } else {
                processElement(element);
            }
        }
        return isAllTimeExcept;
    }

    private void processElement(String element) {
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

    private void initializeFromExpression(WeekRangeExpression expression) {
        switch (expression) {
            case ALL_TIMES -> this.days = EnumSet.range(DayOfWeek.MONDAY, DayOfWeek.SUNDAY);
            case SCHOOL_DAYS -> this.days = EnumSet.range(DayOfWeek.MONDAY, DayOfWeek.FRIDAY);
            case WEEK_END -> this.days = EnumSet.range(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
            case ALL_TIMES_EXCEPT -> {
                EnumSet<DayOfWeek> allDays = EnumSet.allOf(DayOfWeek.class);
                allDays.removeAll(this.days);
                this.days = allDays; // Set 'this.days' to be the remaining days
            }
            default -> throw new IllegalStateException("Unknown expression: " + expression);
        }
    }

    public Set<DayOfWeek> getDays() {
        return this.days;
    }

    public void setDays(EnumSet<DayOfWeek> days) {
        this.days = days;
    }

    public void addDay(DayOfWeek day) {
        this.days.add(day);
    }

    public void removeDay(DayOfWeek day) {
        this.days.remove(day);
    }

    public boolean contains(DayOfWeek day) {
        return this.days.contains(day);
    }

    @Override
    public String toString() {
        return "WeeklyDays{" + days + '}';
    }

    private void handleInterval(String interval) {
        String[] daysInterval = interval.split("-");
        DayOfWeek start = GlobalFunction.convertToDayOfWeek(daysInterval[0].trim());
        DayOfWeek end = GlobalFunction.convertToDayOfWeek(daysInterval[1].trim());

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
        DayOfWeek day = GlobalFunction.convertToDayOfWeek(dayStr);
        days.add(day);
    }

}


// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.src.rpasign.description;

import java.time.DayOfWeek;
import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WeeklyDays {
    private static final String WEEKDAY_PATTERN =
            "((LUN|MAR|MER|JEU|VEN|SAM|DIM)(-(LUN|MAR|MER|JEU|VEN|SAM|DIM))?)" +
                    "(;((LUN|MAR|MER|JEU|VEN|SAM|DIM)(-(LUN|MAR|MER|JEU|VEN|SAM|DIM))?))*;*";
    private static final Pattern COMPILED_WEEKDAY_PATTERN = Pattern.compile(WEEKDAY_PATTERN);
    private static final String MSG_ERR_INVALID_FORMAT_S_ARG =
            "Invalid WeeklyDays format: %s. Expected format: " +
                    "[Day Abbreviation] or [Day Abbreviation]-[Day Abbreviation]";
    private static final String MSG_ERR_UNKNOWN_DAY_ABR = "Unknown day abbreviation: %s";

    private EnumSet<DayOfWeek> days;

    public WeeklyDays() {
        this.days = EnumSet.noneOf(DayOfWeek.class);
    }

    public WeeklyDays(String sWeeklyDays) {
        Matcher matcher = COMPILED_WEEKDAY_PATTERN.matcher(sWeeklyDays);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(MSG_ERR_INVALID_FORMAT_S_ARG + sWeeklyDays);
        }
        this.days = EnumSet.noneOf(DayOfWeek.class);

        String[] elements = sWeeklyDays.split(";");

        for (String element : elements) {
            if (element.contains("-")) {
                handleInterval(element);
            } else {
                handleDay(element);
            }
        }
    }

    public WeeklyDays(WeekRangeExpression expression) {
        switch (expression) {
            case ALL_TIMES -> this.days = EnumSet.range(DayOfWeek.MONDAY, DayOfWeek.SUNDAY);
            case SCHOOL_DAYS -> this.days = EnumSet.range(DayOfWeek.MONDAY, DayOfWeek.FRIDAY);
            case WEEK_END -> this.days = EnumSet.range(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
            case EXCEPTE_LUN -> this.days = EnumSet.complementOf(EnumSet.of(DayOfWeek.MONDAY));
            case EXCEPTE_MAR -> this.days = EnumSet.complementOf(EnumSet.of(DayOfWeek.TUESDAY));
            case EXCEPTE_MER -> this.days = EnumSet.complementOf(EnumSet.of(DayOfWeek.WEDNESDAY));
            case EXCEPTE_JEU -> this.days = EnumSet.complementOf(EnumSet.of(DayOfWeek.THURSDAY));
            case EXCEPTE_VEN -> this.days = EnumSet.complementOf(EnumSet.of(DayOfWeek.FRIDAY));
            case EXCEPTE_SAM -> this.days = EnumSet.complementOf(EnumSet.of(DayOfWeek.SATURDAY));
            case EXCEPTE_DIM -> this.days = EnumSet.complementOf(EnumSet.of(DayOfWeek.SUNDAY));
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
        DayOfWeek start = convertToDayOfWeek(daysInterval[0].trim());
        DayOfWeek end = convertToDayOfWeek(daysInterval[1].trim());

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
        DayOfWeek day = convertToDayOfWeek(dayStr);
        if (day == null) {
            throw new IllegalArgumentException(
                    String.format(MSG_ERR_UNKNOWN_DAY_ABR, dayStr));
        }
        this.days.add(day);
    }

    private DayOfWeek convertToDayOfWeek(String sDayAbbr) {
        return switch (sDayAbbr) {
            case "LUN" -> DayOfWeek.MONDAY;
            case "MAR" -> DayOfWeek.TUESDAY;
            case "MER" -> DayOfWeek.WEDNESDAY;
            case "JEU" -> DayOfWeek.THURSDAY;
            case "VEN" -> DayOfWeek.FRIDAY;
            case "SAM" -> DayOfWeek.SATURDAY;
            case "DIM" -> DayOfWeek.SUNDAY;
            default -> throw new IllegalStateException("Unknown day abbreviation: " + sDayAbbr);

        };
    }


}


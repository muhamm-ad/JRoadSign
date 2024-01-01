package org.jroadsign.quebec.montreal.src.rpasign.description;

import java.time.DayOfWeek;
import java.util.EnumSet;

public class WeeklyDays {

    private EnumSet<DayOfWeek> days;

    public WeeklyDays(String sWeeklyDays) {
        String[] elements = sWeeklyDays.split(";");

        for (String element : elements) {
            if (element.contains("-")) {
                handleInterval(element);
            } else {
                handleDay(element);
            }
        }
    }

    private void handleInterval(String interval) {
        // TODO
    }

    private void handleDay(String dayStr) {
        // TODO
    }

    private DayOfWeek convertToDayOfWeek(String dayAbbr) {
        switch (dayAbbr) {
            case "LUN":
                return DayOfWeek.MONDAY;
            case "MAR":
                return DayOfWeek.TUESDAY;
            case "MER":
                return DayOfWeek.WEDNESDAY;
            case "JEU":
                return DayOfWeek.THURSDAY;
            case "VEN":
                return DayOfWeek.FRIDAY;
            case "SAM":
                return DayOfWeek.SATURDAY;
            case "DIM":
                return DayOfWeek.SUNDAY;
            default:
                return null;
        }
    }

    public EnumSet<DayOfWeek> getDays() {
        return this.days;
    }

    public boolean contains(DayOfWeek day) {
        return this.days.contains(day);
    }

    @Override
    public String toString() {
        return "WeeklyDays{" + days + '}';
    }

}


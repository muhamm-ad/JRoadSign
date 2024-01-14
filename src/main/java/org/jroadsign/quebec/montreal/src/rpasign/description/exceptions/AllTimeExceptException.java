package org.jroadsign.quebec.montreal.src.rpasign.description.exceptions;

import org.jroadsign.quebec.montreal.src.rpasign.description.WeeklyDays;

public class AllTimeExceptException extends Exception {
    private final WeeklyDays weeklyDaysInstance;

    public AllTimeExceptException(WeeklyDays weeklyDaysInstance) {
        this.weeklyDaysInstance = weeklyDaysInstance;
    }

    public WeeklyDays getWeeklyDaysInstance() {
        return weeklyDaysInstance;
    }
}

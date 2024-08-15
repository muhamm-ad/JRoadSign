package org.jroadsign.canada.quebec.montreal.rpasign.rpasigndesc.exceptions;

import org.jroadsign.canada.quebec.montreal.rpasign.rpasigndesc.WeekRangeExpression;
import org.jroadsign.canada.quebec.montreal.rpasign.rpasigndesc.WeeklyDays;

public class WeeklyRangeExpException extends Exception {
    private final WeeklyDays weeklyDays;
    private final WeekRangeExpression expression;

    public WeeklyRangeExpException(WeekRangeExpression expression, WeeklyDays weeklyDays) {
        this.weeklyDays = weeklyDays;
        this.expression = expression;
    }

    public WeeklyRangeExpException(WeekRangeExpression expression) {
        this.weeklyDays = null;
        this.expression = expression;
    }

    public WeeklyDays getWeeklyDays() {
        return weeklyDays;
    }

    public WeekRangeExpression getExpression() {
        return expression;
    }
}

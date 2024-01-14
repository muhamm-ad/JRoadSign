package org.jroadsign.quebec.montreal.src.rpasign.description.exceptions;

import org.jroadsign.quebec.montreal.src.rpasign.description.WeekRangeExpression;
import org.jroadsign.quebec.montreal.src.rpasign.description.WeeklyDays;

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

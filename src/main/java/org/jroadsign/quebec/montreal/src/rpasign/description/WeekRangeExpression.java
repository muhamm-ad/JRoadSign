// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.src.rpasign.description;

public enum WeekRangeExpression {
    ALL_TIMES(GlobalConfig.ALL_TIMES),
    ALL_TIMES_EXCEPT(GlobalConfig.ALL_TIMES_EXCEPT),
    SCHOOL_DAYS(GlobalConfig.SCHOOL_DAYS, GlobalConfig.CLASS_DAYS),
    WEEK_END(GlobalConfig.WEEK_END);

    private final String[] descriptions;

    WeekRangeExpression(String... descriptions) {
        this.descriptions = descriptions;
    }

    public static WeekRangeExpression fromString(String text) {
        for (WeekRangeExpression expression : WeekRangeExpression.values()) {
            for (String description : expression.descriptions) {
                if (description.equalsIgnoreCase(text)) {
                    return expression;
                }
            }
        }
        return null;
    }

    public String[] getDescriptions() {
        return descriptions;
    }
}

package org.jroadsign.quebec.montreal.src.rpasign.description;

import org.jetbrains.annotations.NotNull;
import org.jroadsign.quebec.montreal.src.rpasign.description.common.GlobalConfigs;

public enum WeekRangeExpression {
    ALL_TIMES(GlobalConfigs.ALL_TIMES),
    ALL_TIMES_EXCEPT(GlobalConfigs.ALL_TIMES_EXCEPT),
    SCHOOL_DAYS(GlobalConfigs.SCHOOL_DAYS, GlobalConfigs.CLASS_DAYS),
    WEEK_END(GlobalConfigs.WEEK_END);

    private final String[] descriptions;

    WeekRangeExpression(String... descriptions) {
        this.descriptions = descriptions;
    }

    public static WeekRangeExpression fromString(@NotNull String text) {
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

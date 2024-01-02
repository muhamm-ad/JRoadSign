// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.src.rpasign.description;

public enum WeekRangeExpression {
    ALL_TIMES("EN_TOUT_TEMPS"),
    SCHOOL_DAYS("JOURS_D_ECOLES", "JOURS_DE_CLASSE"),
    WEEK_END("WEEK_END"),
    EXCEPTE_LUN("SAUF_LUN", "SAUF_LUNDI"),
    EXCEPTE_MAR("SAUF_MAR", "SAUF_MARDI"),
    EXCEPTE_MER("SAUF_MER", "SAUF_MERCREDI"),
    EXCEPTE_JEU("SAUF_JEU", "SAUF_JEUDI"),
    EXCEPTE_VEN("SAUF_VEN", "SAUF_VENDREDI"),
    EXCEPTE_SAM("SAUF_SAM", "SAUF_SAMEDI"),
    EXCEPTE_DIM("SAUF_DIM", "SAUF_DIMANCHE");

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

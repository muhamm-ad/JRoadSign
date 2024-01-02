// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.rpasign.description;

import junit.framework.TestCase;
import org.jroadsign.quebec.montreal.src.rpasign.description.WeekRangeExpression;

import java.util.Arrays;

public class WeekRangeExpressionTest extends TestCase {

    public void testFromStringValidInputs() {
        assertEquals(WeekRangeExpression.ALL_TIMES, WeekRangeExpression.fromString("EN_TOUT_TEMPS"));
        assertEquals(WeekRangeExpression.SCHOOL_DAYS, WeekRangeExpression.fromString("JOURS_DE_CLASSE"));
        assertEquals(WeekRangeExpression.SCHOOL_DAYS, WeekRangeExpression.fromString("JOURS_D_ECOLES"));
        assertEquals(WeekRangeExpression.WEEK_END, WeekRangeExpression.fromString("WEEK_END"));
        assertEquals(WeekRangeExpression.EXCEPTE_MAR, WeekRangeExpression.fromString("SAUF_MARDI"));
        // TODO Add more valid input tests for each enum value
    }

    public void testFromStringInvalidInput() {
        assertNull(WeekRangeExpression.fromString("INVALID STRING"));
        assertNull(WeekRangeExpression.fromString(""));
        assertNull(WeekRangeExpression.fromString(" "));
        assertNull(WeekRangeExpression.fromString(null));
    }

    public void testGetDescriptions() {
        String[] expectedDescriptions = {"SAUF_LUN", "SAUF_LUNDI"};
        System.out.println(WeekRangeExpression.EXCEPTE_LUN.getDescriptions());
        // assertEquals(expectedDescriptions, WeekPeriodExpression.EXCEPTE_LUN.getDescriptions());
        assertTrue(Arrays.equals(expectedDescriptions, WeekRangeExpression.EXCEPTE_LUN.getDescriptions()));
        // TODO Add more tests for getDescriptions() for other enum values
    }
}

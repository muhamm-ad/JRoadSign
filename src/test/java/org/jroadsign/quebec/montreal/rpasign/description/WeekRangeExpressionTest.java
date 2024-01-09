// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.rpasign.description;

import junit.framework.TestCase;
import org.jroadsign.quebec.montreal.src.rpasign.description.WeekRangeExpression;
import org.jroadsign.quebec.montreal.src.rpasign.description.common.GlobalConfigs;
import org.junit.Test;

import java.util.Arrays;

public class WeekRangeExpressionTest extends TestCase {

    @Test
    public void testFromStringValidInputs() {
        assertEquals(WeekRangeExpression.ALL_TIMES, WeekRangeExpression.fromString(GlobalConfigs.ALL_TIMES));
        assertEquals(WeekRangeExpression.ALL_TIMES_EXCEPT, WeekRangeExpression.fromString(GlobalConfigs.ALL_TIMES_EXCEPT));
        assertEquals(WeekRangeExpression.SCHOOL_DAYS, WeekRangeExpression.fromString(GlobalConfigs.SCHOOL_DAYS));
        assertEquals(WeekRangeExpression.SCHOOL_DAYS, WeekRangeExpression.fromString(GlobalConfigs.CLASS_DAYS));
        assertEquals(WeekRangeExpression.WEEK_END, WeekRangeExpression.fromString(GlobalConfigs.WEEK_END));
        // TODO Add more valid input tests for each enum value
    }

    @Test
    public void testFromStringInvalidInput() {
        assertNull(WeekRangeExpression.fromString("INVALID STRING"));
        assertNull(WeekRangeExpression.fromString(""));
        assertNull(WeekRangeExpression.fromString(" "));
        assertNull(WeekRangeExpression.fromString(null));
    }

    @Test
    public void testGetDescriptions() {
        String[] expectedDescriptions = {GlobalConfigs.SCHOOL_DAYS, GlobalConfigs.CLASS_DAYS};
        assertTrue(Arrays.equals(expectedDescriptions, WeekRangeExpression.SCHOOL_DAYS.getDescriptions()));
        // TODO Add more tests for getDescriptions() for other enum values
    }
}

// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.rpasign.description;

import org.jroadsign.quebec.montreal.src.rpasign.description.WeekRangeExpression;
import org.jroadsign.quebec.montreal.src.rpasign.description.WeeklyDays;
import org.jroadsign.quebec.montreal.src.rpasign.description.common.GlobalConfigs;
import org.jroadsign.quebec.montreal.src.rpasign.description.exceptions.WeeklyRangeExpException;
import org.junit.Test;

import java.time.DayOfWeek;
import java.util.*;

import static org.junit.Assert.*;

public class WeeklyDaysTest {

    @Test
    public void testInvalidWeeklyDaysFormat() {
        assertThrows(IllegalArgumentException.class, () -> new WeeklyDays(""));
        assertThrows(IllegalArgumentException.class, () -> new WeeklyDays(" "));
        assertThrows(IllegalArgumentException.class, () -> new WeeklyDays("null"));
    }

    @Test
    public void testSingleValidDay() {
        try {
            WeeklyDays singleDay = new WeeklyDays("LUN");
            assertEquals(EnumSet.of(DayOfWeek.MONDAY), singleDay.getDays());
        } catch (WeeklyRangeExpException e) {
            fail("WeeklyRangeExpException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testInvalidDay() {
        assertThrows(IllegalArgumentException.class, () -> new WeeklyDays("VAR"));
    }

    @Test
    public void testExtraSemicolonAtStart() {
        assertThrows(IllegalArgumentException.class, () -> new WeeklyDays(";MER"));
    }

    @Test
    public void testExtraSemicolonAtEnd() {
        try {
            WeeklyDays extraSemicolonEnd = new WeeklyDays("MAR;");
            assertEquals(EnumSet.of(DayOfWeek.TUESDAY), extraSemicolonEnd.getDays());
        } catch (WeeklyRangeExpException e) {
            fail("WeeklyRangeExpException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testMultipleDays() {
        try {
            WeeklyDays multipleDays = new WeeklyDays("VEN;SAM");
            assertEquals(EnumSet.of(DayOfWeek.FRIDAY, DayOfWeek.SATURDAY), multipleDays.getDays());
        } catch (WeeklyRangeExpException e) {
            fail("WeeklyRangeExpException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testValidAndInvalidDays() {
        assertThrows(IllegalArgumentException.class, () -> new WeeklyDays("VEN;XYZ;MAR"));
    }

    @Test
    public void testUnorderedValidDays() {
        try {
            WeeklyDays unorderedDays = new WeeklyDays("VEN;JEU;MAR");
            assertEquals(EnumSet.of(DayOfWeek.TUESDAY, DayOfWeek.FRIDAY, DayOfWeek.THURSDAY), unorderedDays.getDays());

            List<DayOfWeek> expectedOrder = Arrays.asList(DayOfWeek.FRIDAY, DayOfWeek.THURSDAY, DayOfWeek.TUESDAY);
            assertEquals(expectedOrder, new ArrayList<>(unorderedDays.getDays()));

            WeeklyDays orderedDays = new WeeklyDays("LUN-JEU");
            expectedOrder = Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY);
            assertEquals(expectedOrder, new ArrayList<>(orderedDays.getDays()));

        } catch (WeeklyRangeExpException e) {
            fail("WeeklyRangeExpException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testIntervalSameDay() {
        try {
            WeeklyDays sameDayInterval = new WeeklyDays("MAR-MAR");
            EnumSet<DayOfWeek> expected = EnumSet.of(DayOfWeek.TUESDAY);
            assertEquals(expected, sameDayInterval.getDays());
        } catch (WeeklyRangeExpException e) {
            fail("WeeklyRangeExpException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testIntervalNotInOrder() {
        try {
            WeeklyDays notInOrderInterval = new WeeklyDays("VEN-LUN");
            EnumSet<DayOfWeek> expected = EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
            assertEquals(expected, notInOrderInterval.getDays());
        } catch (WeeklyRangeExpException e) {
            fail("WeeklyRangeExpException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testIntervalOneDay() {
        try {
            WeeklyDays oneDayInterval = new WeeklyDays("JEU-VEN");
            EnumSet<DayOfWeek> expected = EnumSet.of(DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
            assertEquals(expected, oneDayInterval.getDays());
        } catch (WeeklyRangeExpException e) {
            fail("WeeklyRangeExpException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testIntervalMoreDays() {
        try {
            WeeklyDays moreDaysInterval = new WeeklyDays("LUN-DIM");
            EnumSet<DayOfWeek> expected = EnumSet.allOf(DayOfWeek.class);
            assertEquals(expected, moreDaysInterval.getDays());
        } catch (WeeklyRangeExpException e) {
            fail("WeeklyRangeExpException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testAllTimesExpression() {
        try {
            WeeklyDays allTimes = new WeeklyDays(WeekRangeExpression.ALL_TIMES);
            EnumSet<DayOfWeek> expected = EnumSet.allOf(DayOfWeek.class);
            assertEquals(expected, allTimes.getDays());
        } catch (WeeklyRangeExpException e) {
            fail("WeeklyRangeExpException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testSchoolDaysExpression() {
        try {
            WeeklyDays schoolDays = new WeeklyDays(WeekRangeExpression.SCHOOL_DAYS);
            EnumSet<DayOfWeek> expected = EnumSet.range(DayOfWeek.MONDAY, DayOfWeek.FRIDAY);
            assertEquals(expected, schoolDays.getDays());
        } catch (WeeklyRangeExpException e) {
            fail("WeeklyRangeExpException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testWeekEndExpression() {
        try {
            WeeklyDays weekEnd = new WeeklyDays(WeekRangeExpression.WEEK_END);
            EnumSet<DayOfWeek> expected = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
            assertEquals(expected, weekEnd.getDays());
        } catch (WeeklyRangeExpException e) {
            fail("WeeklyRangeExpException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testComplexInputParsing() {
        try {
            WeeklyDays complexInput = new WeeklyDays("MAR-JEU;LUN;SAM");
            EnumSet<DayOfWeek> expected = EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.SATURDAY);
            assertEquals(expected, complexInput.getDays());

            complexInput = new WeeklyDays("LUN;MAR-JEU;SAM");
            expected = EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.SATURDAY);
            assertEquals(expected, complexInput.getDays());

            complexInput = new WeeklyDays("LUN;SAM;MAR-JEU");
            expected = EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.SATURDAY);
            assertEquals(expected, complexInput.getDays());
        } catch (WeeklyRangeExpException e) {
            fail("WeeklyRangeExpException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testAddingDaysMultipleTimes() {
        try {
            WeeklyDays multipleAdditions = new WeeklyDays("LUN;LUN;LUN");
            EnumSet<DayOfWeek> expected = EnumSet.of(DayOfWeek.MONDAY);
            assertEquals(expected, multipleAdditions.getDays());
        } catch (WeeklyRangeExpException e) {
            fail("WeeklyRangeExpException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testComplexInputWithRepeatedDays() {
        try {
            WeeklyDays complexInputWithRepeat = new WeeklyDays("LUN-MER;MER;VEN");
            EnumSet<DayOfWeek> expected = EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY);
            assertEquals(expected, complexInputWithRepeat.getDays());
        } catch (WeeklyRangeExpException e) {
            fail("WeeklyRangeExpException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testContainsMethod() {
        try {
            WeeklyDays weeklyDays = new WeeklyDays("MAR-JEU;LUN;SAM");
            assertTrue(weeklyDays.contains(DayOfWeek.MONDAY));   // Included day
            assertTrue(weeklyDays.contains(DayOfWeek.WEDNESDAY)); // Included day
            assertFalse(weeklyDays.contains(DayOfWeek.SUNDAY));   // Not included day
        } catch (WeeklyRangeExpException e) {
            fail("WeeklyRangeExpException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testSetDays() {
        WeeklyDays weeklyDays = new WeeklyDays();
        EnumSet<DayOfWeek> newDays = EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY);
        weeklyDays.setDays(newDays);
        assertEquals(newDays, weeklyDays.getDays());
    }

    @Test
    public void testSetEmptyDays() {
        WeeklyDays weeklyDays = new WeeklyDays();
        weeklyDays.setDays(EnumSet.noneOf(DayOfWeek.class));
        assertTrue(weeklyDays.getDays().isEmpty());
    }

    @Test
    public void testAddDay() {
        WeeklyDays weeklyDays = new WeeklyDays();
        weeklyDays.addDay(DayOfWeek.MONDAY);
        assertTrue(weeklyDays.contains(DayOfWeek.MONDAY));
        // Adding the same day again should not create duplicates
        weeklyDays.addDay(DayOfWeek.MONDAY);
        int count = Collections.frequency(weeklyDays.getDays(), DayOfWeek.MONDAY);
        assertEquals(1, count);
    }

    @Test
    public void testRemoveDay() {
        try {
            WeeklyDays weeklyDays = new WeeklyDays("LUN;MAR");
            weeklyDays.removeDay(DayOfWeek.MONDAY);
            assertFalse(weeklyDays.contains(DayOfWeek.MONDAY));
            // Removing a non-existing day should not cause any issue
            weeklyDays.removeDay(DayOfWeek.SUNDAY);
            assertFalse(weeklyDays.contains(DayOfWeek.SUNDAY));
        } catch (WeeklyRangeExpException e) {
            fail("WeeklyRangeExpException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testAddNullDay() {
        WeeklyDays weeklyDays = new WeeklyDays();
        assertThrows(IllegalArgumentException.class, () -> weeklyDays.addDay(null));
    }

    @Test
    public void testRemoveNullDay() {
        WeeklyDays weeklyDays = new WeeklyDays();
        assertThrows(IllegalArgumentException.class, () -> weeklyDays.removeDay(null));
    }

    @Test
    public void testClearAllDays() {
        try {
            WeeklyDays weeklyDays = new WeeklyDays("LUN;MAR;MER");
            weeklyDays.removeDay(DayOfWeek.MONDAY);
            weeklyDays.removeDay(DayOfWeek.TUESDAY);
            weeklyDays.removeDay(DayOfWeek.WEDNESDAY);
            assertTrue(weeklyDays.getDays().isEmpty());
        } catch (WeeklyRangeExpException e) {
            fail("WeeklyRangeExpException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testAllExceptExpression() {
        try {
            EnumSet<DayOfWeek> expected = EnumSet.allOf(DayOfWeek.class);

            WeeklyDays weeklyDays = new WeeklyDays(WeekRangeExpression.ALL_TIMES);
            assertEquals(expected, weeklyDays.getDays());

            weeklyDays = new WeeklyDays(GlobalConfigs.ALL_TIMES + ";LUN;VEN");
            assertEquals(expected, weeklyDays.getDays());

            weeklyDays = new WeeklyDays("SAM;MAR;" + GlobalConfigs.ALL_TIMES + ";VEN");
            assertEquals(expected, weeklyDays.getDays());

            weeklyDays = new WeeklyDays("MER;JEU;VEN;" + GlobalConfigs.ALL_TIMES);
            assertEquals(expected, weeklyDays.getDays());

        } catch (WeeklyRangeExpException e) {
            fail("WeeklyRangeExpException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testAllTimesExceptExpression() {
        try {
            new WeeklyDays(WeekRangeExpression.ALL_TIMES_EXCEPT);
        } catch (WeeklyRangeExpException e1) {
            assertEquals(EnumSet.allOf(DayOfWeek.class), e1.getWeeklyDays().getDays());
        }

        assertAllTimesExceptExpression(GlobalConfigs.ALL_TIMES_EXCEPT + ";LUN;VEN",
                EnumSet.complementOf(EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY)));

        assertAllTimesExceptExpression("SAM;MAR;" + GlobalConfigs.ALL_TIMES_EXCEPT + ";VEN",
                EnumSet.complementOf(EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.TUESDAY, DayOfWeek.FRIDAY)));

        assertAllTimesExceptExpression("MER;JEU;VEN;" + GlobalConfigs.ALL_TIMES_EXCEPT,
                EnumSet.complementOf(EnumSet.of(DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)));
    }

    private void assertAllTimesExceptExpression(String input, EnumSet<DayOfWeek> expected) {
        try {
            new WeeklyDays(input);
            fail("WeeklyRangeExpException was expected for input: " + input);
        } catch (WeeklyRangeExpException e) {
            assertEquals(expected, e.getWeeklyDays().getDays());
        }
    }


    @Test
    public void testIsEmpty() {
        try {
            WeeklyDays nonEmptyDays = new WeeklyDays("LUN");
            assertFalse(nonEmptyDays.isEmpty());

            WeeklyDays weeklyDays = new WeeklyDays();
            assertTrue(weeklyDays.isEmpty());

            weeklyDays.addDay(DayOfWeek.MONDAY);
            assertFalse(weeklyDays.isEmpty());

            weeklyDays.removeDay(DayOfWeek.MONDAY);
            assertTrue(weeklyDays.isEmpty());
        } catch (WeeklyRangeExpException e) {
            fail("WeeklyRangeExpException was thrown: " + e.getMessage());
        }
    }

}

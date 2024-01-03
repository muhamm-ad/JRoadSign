// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.rpasign.description;


import org.jroadsign.quebec.montreal.src.rpasign.description.WeekRangeExpression;
import org.jroadsign.quebec.montreal.src.rpasign.description.WeeklyDays;
import org.junit.Test;

import java.time.DayOfWeek;
import java.util.Collections;
import java.util.EnumSet;

import static org.junit.Assert.*;

public class WeeklyDaysTest {

    @Test
    public void testInvalidWeeklyDaysFormat() {
        // assertThrows(NullPointerException.class, () -> new WeeklyDays(null));
        assertThrows(IllegalArgumentException.class, () -> new WeeklyDays(""));
        assertThrows(IllegalArgumentException.class, () -> new WeeklyDays(" "));
        assertThrows(IllegalArgumentException.class, () -> new WeeklyDays("null"));
    }

    @Test
    public void testSingleValidDay() {
        WeeklyDays singleDay = new WeeklyDays("LUN");
        assertEquals(EnumSet.of(DayOfWeek.MONDAY), singleDay.getDays());
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
        WeeklyDays extraSemicolonEnd = new WeeklyDays("MAR;");
        assertEquals(EnumSet.of(DayOfWeek.TUESDAY), extraSemicolonEnd.getDays());
    }

    @Test
    public void testMultipleDays() {
        WeeklyDays multipleDays = new WeeklyDays("VEN;SAM");
        assertEquals(EnumSet.of(DayOfWeek.FRIDAY, DayOfWeek.SATURDAY), multipleDays.getDays());
    }

    @Test
    public void testValidAndInvalidDays() {
        assertThrows(IllegalArgumentException.class, () -> new WeeklyDays("VEN;XYZ;MAR"));
    }

    @Test
    public void testUnorderedValidDays() {
        WeeklyDays unorderedDays = new WeeklyDays("VEN;JEU;MAR");
        assertEquals(EnumSet.of(DayOfWeek.TUESDAY, DayOfWeek.FRIDAY, DayOfWeek.THURSDAY), unorderedDays.getDays());
    }

    @Test
    public void testIntervalSameDay() {
        WeeklyDays sameDayInterval = new WeeklyDays("MAR-MAR");
        EnumSet<DayOfWeek> expected = EnumSet.of(DayOfWeek.TUESDAY);
        assertEquals(expected, sameDayInterval.getDays());
    }

    @Test
    public void testIntervalNotInOrder() {
        WeeklyDays notInOrderInterval = new WeeklyDays("VEN-LUN");
        EnumSet<DayOfWeek> expected = EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
        assertEquals(expected, notInOrderInterval.getDays());
    }

    @Test
    public void testIntervalOneDay() {
        WeeklyDays oneDayInterval = new WeeklyDays("JEU-VEN");
        EnumSet<DayOfWeek> expected = EnumSet.of(DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
        assertEquals(expected, oneDayInterval.getDays());
    }

    @Test
    public void testIntervalMoreDays() {
        WeeklyDays moreDaysInterval = new WeeklyDays("LUN-DIM");
        EnumSet<DayOfWeek> expected = EnumSet.allOf(DayOfWeek.class);
        assertEquals(expected, moreDaysInterval.getDays());
    }

    @Test
    public void testAllTimesExpression() {
        WeeklyDays allTimes = new WeeklyDays(WeekRangeExpression.ALL_TIMES);
        EnumSet<DayOfWeek> expected = EnumSet.allOf(DayOfWeek.class);
        assertEquals(expected, allTimes.getDays());
    }

    @Test
    public void testSchoolDaysExpression() {
        WeeklyDays schoolDays = new WeeklyDays(WeekRangeExpression.SCHOOL_DAYS);
        EnumSet<DayOfWeek> expected = EnumSet.range(DayOfWeek.MONDAY, DayOfWeek.FRIDAY);
        assertEquals(expected, schoolDays.getDays());
    }

    @Test
    public void testWeekEndExpression() {
        WeeklyDays weekEnd = new WeeklyDays(WeekRangeExpression.WEEK_END);
        EnumSet<DayOfWeek> expected = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
        assertEquals(expected, weekEnd.getDays());
    }

    @Test
    public void testExceptTuesdayExpression() {
        WeeklyDays exceptTuesday = new WeeklyDays(WeekRangeExpression.EXCEPTE_MAR);
        EnumSet<DayOfWeek> expected = EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
        assertEquals(expected, exceptTuesday.getDays());
    }

    @Test
    public void testComplexInputParsing() {
        WeeklyDays complexInput = new WeeklyDays("MAR-JEU;LUN;SAM");
        EnumSet<DayOfWeek> expected = EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.SATURDAY);
        assertEquals(expected, complexInput.getDays());

        complexInput = new WeeklyDays("LUN;MAR-JEU;SAM");
        expected = EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.SATURDAY);
        assertEquals(expected, complexInput.getDays());

        complexInput = new WeeklyDays("LUN;SAM;MAR-JEU");
        expected = EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.SATURDAY);
        assertEquals(expected, complexInput.getDays());
    }

    @Test
    public void testAddingDaysMultipleTimes() {
        WeeklyDays multipleAdditions = new WeeklyDays("LUN;LUN;LUN");
        EnumSet<DayOfWeek> expected = EnumSet.of(DayOfWeek.MONDAY);
        assertEquals(expected, multipleAdditions.getDays());
    }

    @Test
    public void testComplexInputWithRepeatedDays() {
        WeeklyDays complexInputWithRepeat = new WeeklyDays("LUN-MER;MER;VEN");
        EnumSet<DayOfWeek> expected = EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY);
        assertEquals(expected, complexInputWithRepeat.getDays());
    }

    @Test
    public void testContainsMethod() {
        WeeklyDays weeklyDays = new WeeklyDays("MAR-JEU;LUN;SAM");
        assertTrue(weeklyDays.contains(DayOfWeek.MONDAY));   // Included day
        assertTrue(weeklyDays.contains(DayOfWeek.WEDNESDAY)); // Included day
        assertFalse(weeklyDays.contains(DayOfWeek.SUNDAY));   // Not included day
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
        WeeklyDays weeklyDays = new WeeklyDays("LUN;MAR");
        weeklyDays.removeDay(DayOfWeek.MONDAY);
        assertFalse(weeklyDays.contains(DayOfWeek.MONDAY));
        // Removing a non-existing day should not cause any issue
        weeklyDays.removeDay(DayOfWeek.SUNDAY);
        assertFalse(weeklyDays.contains(DayOfWeek.SUNDAY));
    }

    @Test
    public void testAddNullDay() {
        WeeklyDays weeklyDays = new WeeklyDays();
        assertThrows(NullPointerException.class, () -> weeklyDays.addDay(null));
    }

    /*    @Test
    public void testRemoveNullDay() {
        WeeklyDays weeklyDays = new WeeklyDays();
        assertThrows(NullPointerException.class, () -> weeklyDays.removeDay(null));
    }*/

    @Test
    public void testClearAllDays() {
        WeeklyDays weeklyDays = new WeeklyDays("LUN;MAR;MER");
        weeklyDays.removeDay(DayOfWeek.MONDAY);
        weeklyDays.removeDay(DayOfWeek.TUESDAY);
        weeklyDays.removeDay(DayOfWeek.WEDNESDAY);
        assertTrue(weeklyDays.getDays().isEmpty());
    }
}

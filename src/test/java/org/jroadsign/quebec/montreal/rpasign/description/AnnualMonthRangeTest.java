package org.jroadsign.quebec.montreal.rpasign.description;

import org.jroadsign.quebec.montreal.src.rpasign.description.AnnualMonthRange;
import org.jroadsign.quebec.montreal.src.rpasign.description.exceptions.StartAfterEndException;
import org.junit.Test;

import java.time.MonthDay;

import static org.junit.Assert.*;

public class AnnualMonthRangeTest {

    @Test
    public void whenInvalidInput_thenThrowException() {
        // assertThrows(IllegalArgumentException.class, () -> new AnnualMonthRange(null));
        assertThrows(IllegalArgumentException.class, () -> new AnnualMonthRange(""));
        assertThrows(IllegalArgumentException.class, () -> new AnnualMonthRange(" "));
        assertThrows(IllegalArgumentException.class, () -> new AnnualMonthRange("ABC"));
        assertThrows(IllegalArgumentException.class, () -> new AnnualMonthRange("01 MAR - 31 FEV"));
        assertThrows(IllegalArgumentException.class, () -> new AnnualMonthRange("29 FEV - 01 MAR"));
        assertThrows(IllegalArgumentException.class, () -> new AnnualMonthRange("32 JAN - 28 FEV"));
        assertThrows(IllegalArgumentException.class, () -> new AnnualMonthRange("32 JAN - 28 ABC"));
        assertThrows(IllegalArgumentException.class, () -> new AnnualMonthRange("15JAN - 15 FEV"));
        assertThrows(IllegalArgumentException.class, () -> new AnnualMonthRange("15 JAN-15 FEV"));
        assertThrows(IllegalArgumentException.class, () -> new AnnualMonthRange("15JAN - 15FEB"));
        assertThrows(IllegalArgumentException.class, () -> new AnnualMonthRange("15 JAN - 15FEB"));
        assertThrows(IllegalArgumentException.class, () -> new AnnualMonthRange(" 15 JAN - 15FEB"));
        assertThrows(IllegalArgumentException.class, () -> new AnnualMonthRange(" 15 JAN - 15FEB "));
        assertThrows(IllegalArgumentException.class, () -> new AnnualMonthRange("15 JAN - 15FEB "));
        assertThrows(IllegalArgumentException.class, () -> new AnnualMonthRange("-15 JAN - 15FEB "));
    }

    private void assertValidRange(String input, MonthDay expectedStart, MonthDay expectedEnd)
            throws StartAfterEndException {
        AnnualMonthRange range = new AnnualMonthRange(input);
        assertEquals(expectedStart, range.getStart());
        assertEquals(expectedEnd, range.getEnd());
    }

    @Test
    public void whenValidInput_thenCorrectlyParseRange() {
        try {
            assertValidRange("01 JAN - 31 DEC", MonthDay.of(1, 1), MonthDay.of(12, 31));
            assertValidRange("15 FEV - 15 MARS", MonthDay.of(2, 15), MonthDay.of(3, 15));
            assertValidRange("15 MAI - 15 MAI", MonthDay.of(5, 15), MonthDay.of(5, 15));
        } catch (StartAfterEndException e) {
            fail("StartAfterEndException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void whenDateWithinRange_thenCorrectlyEvaluate() {
        try {
            AnnualMonthRange range = new AnnualMonthRange("15 JAN - 31 DEC");
            assertTrue(range.isWithinRange(MonthDay.of(6, 15))); // Inside range
            assertFalse(range.isWithinRange(MonthDay.of(1, 12))); // Outside range
        } catch (StartAfterEndException e) {
            fail("StartAfterEndException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testInvalidDate() {
        assertThrows(StartAfterEndException.class, () -> new AnnualMonthRange("31 DEC - 01 JAN"));
    }

    @Test
    public void testSetStartWithValidDate() {
        try {
            AnnualMonthRange range = new AnnualMonthRange("01 JAN - 31 DEC");
            MonthDay newStart = MonthDay.of(2, 1);
            range.setStart(newStart);
            assertEquals(newStart, range.getStart());
        } catch (StartAfterEndException e) {
            fail("StartAfterEndException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testSetStartWithInvalidDate() {
        try {
            AnnualMonthRange range = new AnnualMonthRange("01 JAN - 31 OCT");
            MonthDay invalidStart = MonthDay.of(11, 11); // Invalid because it's after the end date
            assertThrows(StartAfterEndException.class, () -> range.setStart(invalidStart));
        } catch (StartAfterEndException e) {
            fail("StartAfterEndException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testSetEndWithValidDate() {
        try {
            AnnualMonthRange range = new AnnualMonthRange("01 JAN - 31 DEC");
            MonthDay newEnd = MonthDay.of(11, 30);
            range.setEnd(newEnd);
            assertEquals(newEnd, range.getEnd());
        } catch (StartAfterEndException e) {
            fail("StartAfterEndException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testSetEndWithInvalidDate() {
        try {
            AnnualMonthRange range = new AnnualMonthRange("15 MARS - 31 DEC");
            MonthDay invalidEnd = MonthDay.of(1, 15); // Invalid because it's before the start date
            assertThrows(StartAfterEndException.class, () -> range.setEnd(invalidEnd));
        } catch (StartAfterEndException e) {
            fail("StartAfterEndException was thrown: " + e.getMessage());
        }
    }
}
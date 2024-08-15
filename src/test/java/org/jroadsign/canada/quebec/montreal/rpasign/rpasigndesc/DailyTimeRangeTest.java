package org.jroadsign.canada.quebec.montreal.rpasign.rpasigndesc;

import org.jroadsign.canada.quebec.montreal.rpasign.rpasigndesc.exceptions.StartAfterEndException;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalTime;

import static org.junit.Assert.*;

/**
 * @author - <a href="https://github.com/muhamm-ad">muhamm-ad</a>
 * @project JRoadSign
 */
public class DailyTimeRangeTest {

    @Test
    public void whenInvalidInput_thenThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new DailyTimeRange(""));
        assertThrows(IllegalArgumentException.class, () -> new DailyTimeRange(" "));
        assertThrows(IllegalArgumentException.class, () -> new DailyTimeRange("null"));
        assertThrows(IllegalArgumentException.class, () -> new DailyTimeRange("25H00-27H00"));
        assertThrows(IllegalArgumentException.class, () -> new DailyTimeRange("12H70-15H90"));
        assertThrows(IllegalArgumentException.class, () -> new DailyTimeRange("14H00 -15H00"));
        assertThrows(IllegalArgumentException.class, () -> new DailyTimeRange("14H00 - 15H00"));
        assertThrows(IllegalArgumentException.class, () -> new DailyTimeRange("14H00- 15H00"));
        assertThrows(IllegalArgumentException.class, () -> new DailyTimeRange(" 14H00-15H00"));
        assertThrows(IllegalArgumentException.class, () -> new DailyTimeRange(" 14H00-15H00 "));
        assertThrows(IllegalArgumentException.class, () -> new DailyTimeRange("14H00-15H00 "));
        assertThrows(IllegalArgumentException.class, () -> new DailyTimeRange("14H00-15H 00 "));
        assertThrows(IllegalArgumentException.class, () -> new DailyTimeRange("14 H00-15H 00 "));
        assertThrows(IllegalArgumentException.class, () -> new DailyTimeRange("14H 00-15 H00 "));
    }

    private void assertValidRange(String input, LocalTime expectedStart, LocalTime expectedEnd,
                                  Duration expectedDuration) throws StartAfterEndException {
        DailyTimeRange dailyTimeRange = new DailyTimeRange(input);
        assertEquals(expectedStart, dailyTimeRange.getStart());
        assertEquals(expectedEnd, dailyTimeRange.getEnd());
        assertEquals(expectedDuration, dailyTimeRange.getDuration());
    }

    @Test
    public void whenValidInput_thenCorrectlyParseTimeRange() {
        try {
            assertValidRange("05H30-10H00", LocalTime.of(5, 30), LocalTime.of(10, 0), Duration.ofHours(4).plusMinutes(30));
            assertValidRange("00H00-12H00", LocalTime.of(0, 0), LocalTime.of(12, 0), Duration.ofHours(12));
            assertValidRange("13H-15H", LocalTime.of(13, 0), LocalTime.of(15, 0), Duration.ofHours(2));
            assertValidRange("1H-1H", LocalTime.of(1, 0), LocalTime.of(1, 0), Duration.ZERO);
            assertValidRange("3H30-15H", LocalTime.of(3, 30), LocalTime.of(15, 0), Duration.ofHours(11).plusMinutes(30));
            assertValidRange("23H-23H59", LocalTime.of(23, 0), LocalTime.of(23, 59), Duration.ofMinutes(59));
            assertValidRange("0H0-23H59", LocalTime.of(0, 0), LocalTime.of(23, 59), Duration.ofHours(23).plusMinutes(59));
            assertValidRange("12H-13H", LocalTime.of(12, 0), LocalTime.of(13, 0), Duration.ofHours(1));
            assertValidRange("2H15-4H45", LocalTime.of(2, 15), LocalTime.of(4, 45), Duration.ofHours(2).plusMinutes(30));
            assertValidRange("20H-21H", LocalTime.of(20, 0), LocalTime.of(21, 0), Duration.ofHours(1));
        } catch (StartAfterEndException e) {
            fail("StartAfterEndException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void whenTimeWithinRange_thenCorrectlyEvaluate() {
        try {
            DailyTimeRange dailyTimeRange = new DailyTimeRange("13H-15H");
            assertTrue(dailyTimeRange.isWithinRange(LocalTime.of(14, 30)));
            assertFalse(dailyTimeRange.isWithinRange(LocalTime.of(12, 30)));
            assertFalse(dailyTimeRange.isWithinRange(LocalTime.of(15, 1)));

            dailyTimeRange = new DailyTimeRange("12H-13H");
            assertTrue(dailyTimeRange.isWithinRange(LocalTime.of(12, 30)));
            assertFalse(dailyTimeRange.isWithinRange(LocalTime.of(11, 59)));
            assertFalse(dailyTimeRange.isWithinRange(LocalTime.of(13, 1)));

            dailyTimeRange = new DailyTimeRange("20H-21H");
            assertTrue(dailyTimeRange.isWithinRange(LocalTime.of(20, 59)));
            assertFalse(dailyTimeRange.isWithinRange(LocalTime.of(21, 1)));
        } catch (StartAfterEndException e) {
            fail("StartAfterEndException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testInvalidTime() {
        assertThrows(StartAfterEndException.class, () -> new DailyTimeRange("15H30-03H00"));
        assertThrows(StartAfterEndException.class, () -> new DailyTimeRange("15H30-00H00"));
    }

    @Test
    public void testSetStartWithValidTime() {
        try {
            DailyTimeRange dailyTimeRange = new DailyTimeRange("08H00-10H00");
            LocalTime newStartTime = LocalTime.of(7, 0);
            dailyTimeRange.setStart(newStartTime);
            assertEquals(newStartTime, dailyTimeRange.getStart());
        } catch (StartAfterEndException e) {
            fail("StartAfterEndException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testSetStartWithInvalidTime() {
        try {
            DailyTimeRange dailyTimeRange = new DailyTimeRange("08H00-10H00");
            LocalTime invalidStartTime = LocalTime.of(11, 0);  // Invalid because it's after the end time
            assertThrows(StartAfterEndException.class, () -> dailyTimeRange.setStart(invalidStartTime));
        } catch (StartAfterEndException e) {
            fail("StartAfterEndException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testSetEndWithValidTime() {
        try {
            DailyTimeRange dailyTimeRange = new DailyTimeRange("08H00-10H00");
            LocalTime newEndTime = LocalTime.of(11, 0);
            dailyTimeRange.setEnd(newEndTime);
            assertEquals(newEndTime, dailyTimeRange.getEnd());
        } catch (StartAfterEndException e) {
            fail("StartAfterEndException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testSetEndWithInvalidTime() {
        try {
            DailyTimeRange dailyTimeRange = new DailyTimeRange("08H00-10H00");
            LocalTime invalidEndTime = LocalTime.of(7, 0);  // Invalid because it's before the start time
            assertThrows(StartAfterEndException.class, () -> dailyTimeRange.setEnd(invalidEndTime));
        } catch (StartAfterEndException e) {
            fail("StartAfterEndException was thrown: " + e.getMessage());
        }
    }
}

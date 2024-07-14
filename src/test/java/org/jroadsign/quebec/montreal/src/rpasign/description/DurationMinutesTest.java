package org.jroadsign.quebec.montreal.src.rpasign.description;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class DurationMinutesTest {

    @Test
    public void testValidDurations() {
        assertEquals(15, new DurationMinutes("15 MIN").getDuration());
        assertEquals(120, new DurationMinutes("120 MIN").getDuration());
        assertEquals(5, new DurationMinutes("5 MIN").getDuration());
        assertEquals(60, new DurationMinutes("60 MIN").getDuration());
        assertEquals(0, new DurationMinutes("0 MIN").getDuration());
        assertEquals(0, new DurationMinutes(0).getDuration());
        assertEquals(15, new DurationMinutes(15).getDuration());
    }

    @Test
    public void testInvalidDurationFormat() {
        assertThrows(NullPointerException.class, () -> new DurationMinutes(null));
        assertThrows(IllegalArgumentException.class, () -> new DurationMinutes(""));
        assertThrows(IllegalArgumentException.class, () -> new DurationMinutes(" "));
        assertThrows(IllegalArgumentException.class, () -> new DurationMinutes("null"));
        assertThrows(IllegalArgumentException.class, () -> new DurationMinutes("120MIN"));
        assertThrows(IllegalArgumentException.class, () -> new DurationMinutes("120 H"));
        assertThrows(IllegalArgumentException.class, () -> new DurationMinutes(" MIN"));
        assertThrows(IllegalArgumentException.class, () -> new DurationMinutes("120 "));
    }

    @Test
    public void testNegativeDurationValue() {
        assertThrows(IllegalArgumentException.class, () -> new DurationMinutes("-10 MIN"));
        assertThrows(IllegalArgumentException.class, () -> new DurationMinutes(-10));
    }

    @Test
    public void testSetDurationWithValidValue() {
        DurationMinutes durationMinutes = new DurationMinutes(10);
        durationMinutes.setDuration(20);
        assertEquals(20, durationMinutes.getDuration());
    }

    @Test
    public void testSetDurationWithInvalidValue() {
        DurationMinutes durationMinutes = new DurationMinutes(10);
        assertThrows(IllegalArgumentException.class, () -> durationMinutes.setDuration(-5));
    }
}

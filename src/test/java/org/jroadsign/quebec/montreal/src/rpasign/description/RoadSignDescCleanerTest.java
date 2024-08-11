package org.jroadsign.quebec.montreal.src.rpasign.description;

import junit.framework.TestCase;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RoadSignDescCleanerTest extends TestCase {

    public void testCleanDescription() {
        // TODO : Add tests
    }

    public void testReformatDailyTimeIntervals_1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Access the private method
        Method method = RoadSignDescCleaner.class.getDeclaredMethod("reformatDailyTimeIntervals_1", String.class);
        method.setAccessible(true);

        // General patterns with parking prefix and duration prefix
        assertEquals("\\P 17H-23H59 MAR; \\P 00H00-17H MER",
                method.invoke(null, "\\P 17H MARDI A 17H MERCREDI"));
        assertEquals("120MIN 17H-23H59 MAR; 120MIN 00H00-17H MER",
                method.invoke(null, "120MIN - 17H MARDI A 17H MERCREDI"));
        assertEquals("\\P 120MIN 17H-23H59 MAR; \\P 120MIN 00H00-17H MER",
                method.invoke(null, "\\P 120MIN - 17H MARDI A 17H MERCREDI"));

        // Full day intervals
        assertEquals("00H-23H59 LUN; 00H00-23H59 MAR; 00H00-23H59 MER",
                method.invoke(null, "00H LUNDI A 23H59 MERCREDI"));
        assertEquals("\\P 07H00-23H59 VEN; \\P 00H00-23H59 SAM; \\P 00H00-07H DIM",
                method.invoke(null, "\\P 07H00 VEN A 07H DIMANCHE"));

        // Multiple day intervals
        assertEquals("17H-23H59 MAR; 00H00-17H MER; 17H-23H59 JEU; 00H00-17H VEN; 17H-23H59 SAM; 00H00-23H59 DIM; 00H00-17H LUN",
                method.invoke(null, "17H MAR A 17H MER - 17H JEU A 17H VEN - 17H SAM A 17H LUN"));
        assertEquals("\\P 17H-23H59 LUN; \\P 00H00-17H MAR; \\P 17H-23H59 MER; \\P 00H00-17H JEU; \\P 17H-23H59 VEN; \\P 00H00-17H SAM",
                method.invoke(null, "\\P LUN 17H À MAR 17H - MER 17H À JEU 17H - VEN 17H À SAM 17H"));
        assertEquals("120MIN 17H-23H59 LUN; 120MIN 00H00-17H MAR; 120MIN 17H-23H59 MER; 120MIN 00H00-17H JEU; 120MIN 17H-23H59 VEN; 120MIN 00H00-17H SAM",
                method.invoke(null, "120MIN - LUN 17H À MAR 17H - MER 17H À JEU 17H - VEN 17H À SAM 17H"));

        // Edge cases with short intervals and single day intervals
        assertEquals("22H-23H59 VEN; 00H00-23H59 SAM; 00H00-06H DIM",
                method.invoke(null, "22H VEN A 06H DIMANCHE"));
        assertEquals("06H30-23H59 SAM; 00H00-23H59 DIM; 00H00-10H LUN",
                method.invoke(null, "06H30 SAMEDI A 10H LUNDI"));
        assertEquals("\\P 23H-23H59 LUN; \\P 00H00-23H59 MAR; \\P 00H00-23H59 MER; \\P 00H00-23H59 JEU; \\P 00H00-23H VEN",
                method.invoke(null, "\\P LUN 23H À VEN 23H"));
        assertEquals("120MIN 12H-23H59 SAM; 120MIN 00H00-23H59 DIM; 120MIN 00H00-18H LUN",
                method.invoke(null, "120MIN - SAM 12H À LUN 18H"));

        // Covering cases with specific start and end times
        assertEquals("17H-23H59 MAR; 00H00-17H MER",
                method.invoke(null, "17H MARDI A 17H MERCREDI"));
        assertEquals("\\P 15H-23H59 MAR; \\P 00H00-23H59 MER; \\P 00H00-19H JEU",
                method.invoke(null, "\\P 15H MAR A 19H JEUDI"));
        assertEquals("08H24-23H59 SAM; 00H00-23H59 DIM; 00H00-09H00 LUN",
                method.invoke(null, "08H24 SAMEDI A 09H00 LUNDI"));

        // Complex multi-day intervals with different start and end times
        assertEquals("04H00-23H59 MER; 00H00-23H59 JEU; 00H00-12H VEN",
                method.invoke(null, "MER 04H00 À VEN 12H"));
        assertEquals("22H-23H59 VEN; 00H00-23H59 SAM; 00H00-06H DIM",
                method.invoke(null, "VEN 22H À DIM 06H"));
    }

    public void testReformatDailyTimeIntervals_2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = RoadSignDescCleaner.class.getDeclaredMethod("reformatDailyTimeIntervals_2", String.class);
        method.setAccessible(true);

        assertEquals("8H-12H LUN; 8H-12H MER; 8H-12H VEN; 13H-18H MAR; 13H-18H JEU",
                method.invoke(null, "8H À 12H LUN MER VEN 13H À 18H MAR JEU"));

        assertEquals("\\P 8H-12H LUN; \\P 8H-12H MER; \\P 8H-12H VEN; \\P 13H-18H MAR; \\P 13H-18H JEU",
                method.invoke(null, "\\P 8H À 12H LUN MER VEN 13H À 18H MAR JEU"));

        assertEquals("7H-11H LUN; 7H-11H MER; 7H-11H VEN; 12H-15H MAR; 12H-15H JEU",
                method.invoke(null, "7H À 11H LUN MER VEN 12H À 15H MAR JEU"));

        assertEquals("6H-10H LUN; 6H-10H MER; 12H-16H VEN; 12H-16H SAM",
                method.invoke(null, "6H À 10H LUN MER 12H À 16H VEN SAM"));

        // Test cases without duration prefix
        assertEquals("9H-12H LUN; 9H-12H MAR; 14H-17H MER; 14H-17H JEU; 14H-17H VEN",
                method.invoke(null, "LUN MAR 9H À 12H MER JEU VEN 14H À 17H"));

        assertEquals("\\P 9H30-12H LUN; \\P 9H30-12H MAR; \\P 14H-17H MER; \\P 14H-17H JEU; \\P 14H-17H VEN",
                method.invoke(null, "\\P LUN MAR 9H30 À 12H MER JEU VEN 14H À 17H"));

        assertEquals("10H32-13H MAR; 10H32-13H JEU; 16H24-19H LUN; 16H24-19H MER; 16H24-19H VEN",
                method.invoke(null, "MAR JEU 10H32 À 13H LUN MER VEN 16H24 À 19H"));

        assertEquals("\\P 7H-11H LUN; \\P 7H-11H MAR; \\P 14H-18H MER; \\P 14H-18H JEU",
                method.invoke(null, "\\P LUN MAR 7H À 11H MER JEU 14H À 18H"));

        // Test cases with duration prefix
        assertEquals("120MIN 9H-12H LUN; 120MIN 9H-12H MAR; 120MIN 14H-17H MER; 120MIN 14H-17H JEU; 120MIN 14H-17H VEN",
                method.invoke(null, "120MIN - LUN MAR 9H À 12H MER JEU VEN 14H À 17H"));

        assertEquals("\\P 90MIN 9H-12H LUN; \\P 90MIN 9H-12H MAR; \\P 90MIN 14H-17H MER; \\P 90MIN 14H-17H JEU; \\P 90MIN 14H-17H VEN",
                method.invoke(null, "\\P 90MIN - LUN MAR 9H À 12H MER JEU VEN 14H À 17H"));

        assertEquals("30MIN 9H-16H30 MAR; 30MIN 9H-16H30 MER; 30MIN 9H-16H30 VEN; 30MIN 12H-16H30 LUN; 30MIN 12H-16H30 JEU",
                method.invoke(null, "30MIN - MAR MER VEN - 9H À 16H30 - LUN JEU - 12H À 16H30"));

        assertEquals("\\P 06h30-07h30 LUN JEU 1 MARS AU 1 DEC.",
                method.invoke(null, "\\P 06h30-07h30 LUN JEU 1 MARS AU 1 DEC."));
    }

    public void testInsertSpaceBetweenLetterAndNumber() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Access the private method
        Method method = RoadSignDescCleaner.class.getDeclaredMethod("insertSpaceBetweenLetterAndNumber", String.class);
        method.setAccessible(true);

        // Test cases
        assertEquals("1A", method.invoke(null, "1A"));
        assertEquals("A 1", method.invoke(null, "A1"));
        assertEquals("B 1B 2B 3", method.invoke(null, "B1B2B3"));
        assertEquals("1H30", method.invoke(null, "1H30"));
        assertEquals("10H30", method.invoke(null, "10H30"));
        assertEquals("\\P 08h-11h MAR. VEN 1MARS AU 1 DEC.", method.invoke(null, "\\P 08h-11h MAR. VEN1MARS AU 1 DEC."));
        assertEquals("10H-20", method.invoke(null, "10H-20"));
    }

    public void testInsertSpaceBetweenDayAndMonth() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Access the private method
        Method method = RoadSignDescCleaner.class.getDeclaredMethod("insertSpaceBetweenDayAndMonth", String.class);
        method.setAccessible(true);

        // Test cases
        assertEquals("\\P 01 MARS A 10 DEC", method.invoke(null, "\\P 01MARS A 10 DEC"));
        assertEquals("\\P 01 FEVRIER A 10 AVRIL", method.invoke(null, "\\P 01 FEVRIER A 10AVRIL"));
        assertEquals("\\P 20 JANVIER A 10 DECEMBRE", method.invoke(null, "\\P 20JANVIER A 10DECEMBRE"));

        assertEquals("\\P MARS 01 A DEC 01", method.invoke(null, "\\P MARS01 A DEC01"));
        assertEquals("\\P DEC 01 A AVRIL 02", method.invoke(null, "\\P DEC 01 A AVRIL02"));
        assertEquals("\\P JUIN 01 A JUIL 01", method.invoke(null, "\\P JUIN01 A JUIL01"));
    }
}
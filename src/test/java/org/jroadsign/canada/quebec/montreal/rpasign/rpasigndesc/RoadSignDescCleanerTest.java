package org.jroadsign.canada.quebec.montreal.rpasign.rpasigndesc;

import junit.framework.TestCase;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author - <a href="https://github.com/muhamm-ad">muhamm-ad</a>
 * @project JRoadSign
 */
public class RoadSignDescCleanerTest extends TestCase {

    public void testCleanDescription() {
        // TODO : Add tests
    }

    public void testReformatDailyTimeIntervals_1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Access the private method
        Method method = RoadSignDescCleaner.class.getDeclaredMethod("reformatDailyTimeIntervals_1", String.class);
        method.setAccessible(true);

        // General patterns with parking prefix and duration prefix
        assertEquals("\\P 17H-23H59 MAR" + RoadSignDescCleaner.RULE_SEPARATOR + "\\P 00H00-17H MER",
                method.invoke(null, "\\P 17H MAR A 17H MER"));
        assertEquals("120MIN 17H-23H59 MAR" + RoadSignDescCleaner.RULE_SEPARATOR + "120MIN 00H00-17H MER",
                method.invoke(null, "120MIN - 17H MAR A 17H MER"));
        assertEquals("\\P 120MIN 17H-23H59 MAR" + RoadSignDescCleaner.RULE_SEPARATOR + "\\P 120MIN 00H00-17H MER",
                method.invoke(null, "\\P 120MIN - 17H MAR A 17H MER"));

        // Full day intervals
        assertEquals("00H-23H59 LUN" + RoadSignDescCleaner.RULE_SEPARATOR + "00H00-23H59 MAR" + RoadSignDescCleaner.RULE_SEPARATOR + "00H00-23H59 MER",
                method.invoke(null, "00H LUN A 23H59 MER"));
        assertEquals("\\P 07H00-23H59 VEN" + RoadSignDescCleaner.RULE_SEPARATOR + "\\P 00H00-23H59 SAM" + RoadSignDescCleaner.RULE_SEPARATOR + "\\P 00H00-07H DIM",
                method.invoke(null, "\\P 07H00 VEN A 07H DIM"));

        // Multiple day intervals
        assertEquals("17H-23H59 MAR" + RoadSignDescCleaner.RULE_SEPARATOR + "00H00-17H MER" + RoadSignDescCleaner.RULE_SEPARATOR + "17H-23H59 JEU" + RoadSignDescCleaner.RULE_SEPARATOR + "00H00-17H VEN" + RoadSignDescCleaner.RULE_SEPARATOR + "17H-23H59 SAM" + RoadSignDescCleaner.RULE_SEPARATOR + "00H00-23H59 DIM" + RoadSignDescCleaner.RULE_SEPARATOR + "00H00-17H LUN",
                method.invoke(null, "17H MAR A 17H MER - 17H JEU A 17H VEN - 17H SAM A 17H LUN"));
        assertEquals("\\P 17H-23H59 LUN" + RoadSignDescCleaner.RULE_SEPARATOR + "\\P 00H00-17H MAR" + RoadSignDescCleaner.RULE_SEPARATOR + "\\P 17H-23H59 MER" + RoadSignDescCleaner.RULE_SEPARATOR + "\\P 00H00-17H JEU" + RoadSignDescCleaner.RULE_SEPARATOR + "\\P 17H-23H59 VEN" + RoadSignDescCleaner.RULE_SEPARATOR + "\\P 00H00-17H SAM",
                method.invoke(null, "\\P LUN 17H À MAR 17H - MER 17H À JEU 17H - VEN 17H À SAM 17H"));
        assertEquals("120MIN 17H-23H59 LUN" + RoadSignDescCleaner.RULE_SEPARATOR + "120MIN 00H00-17H MAR" + RoadSignDescCleaner.RULE_SEPARATOR + "120MIN 17H-23H59 MER" + RoadSignDescCleaner.RULE_SEPARATOR + "120MIN 00H00-17H JEU" + RoadSignDescCleaner.RULE_SEPARATOR + "120MIN 17H-23H59 VEN" + RoadSignDescCleaner.RULE_SEPARATOR + "120MIN 00H00-17H SAM",
                method.invoke(null, "120MIN - LUN 17H À MAR 17H - MER 17H À JEU 17H - VEN 17H À SAM 17H"));

        // Edge cases with short intervals and single day intervals
        assertEquals("22H-23H59 VEN" + RoadSignDescCleaner.RULE_SEPARATOR + "00H00-23H59 SAM" + RoadSignDescCleaner.RULE_SEPARATOR + "00H00-06H DIM",
                method.invoke(null, "22H VEN A 06H DIM"));
        assertEquals("06H30-23H59 SAM" + RoadSignDescCleaner.RULE_SEPARATOR + "00H00-23H59 DIM" + RoadSignDescCleaner.RULE_SEPARATOR + "00H00-10H LUN",
                method.invoke(null, "06H30 SAM A 10H LUN"));
        assertEquals("\\P 23H-23H59 LUN" + RoadSignDescCleaner.RULE_SEPARATOR + "\\P 00H00-23H59 MAR" + RoadSignDescCleaner.RULE_SEPARATOR + "\\P 00H00-23H59 MER" + RoadSignDescCleaner.RULE_SEPARATOR + "\\P 00H00-23H59 JEU" + RoadSignDescCleaner.RULE_SEPARATOR + "\\P 00H00-23H VEN",
                method.invoke(null, "\\P LUN 23H À VEN 23H"));
        assertEquals("120MIN 12H-23H59 SAM" + RoadSignDescCleaner.RULE_SEPARATOR + "120MIN 00H00-23H59 DIM" + RoadSignDescCleaner.RULE_SEPARATOR + "120MIN 00H00-18H LUN",
                method.invoke(null, "120MIN - SAM 12H À LUN 18H"));

        // Covering cases with specific start and end times
        assertEquals("17H-23H59 MAR" + RoadSignDescCleaner.RULE_SEPARATOR + "00H00-17H MER",
                method.invoke(null, "17H MAR A 17H MER"));
        assertEquals("\\P 15H-23H59 MAR" + RoadSignDescCleaner.RULE_SEPARATOR + "\\P 00H00-23H59 MER" + RoadSignDescCleaner.RULE_SEPARATOR + "\\P 00H00-19H JEU",
                method.invoke(null, "\\P 15H MAR A 19H JEU"));
        assertEquals("08H24-23H59 SAM" + RoadSignDescCleaner.RULE_SEPARATOR + "00H00-23H59 DIM" + RoadSignDescCleaner.RULE_SEPARATOR + "00H00-09H00 LUN",
                method.invoke(null, "08H24 SAM A 09H00 LUN"));

        // Complex multi-day intervals with different start and end times
        assertEquals("04H00-23H59 MER" + RoadSignDescCleaner.RULE_SEPARATOR + "00H00-23H59 JEU" + RoadSignDescCleaner.RULE_SEPARATOR + "00H00-12H VEN",
                method.invoke(null, "MER 04H00 À VEN 12H"));
        assertEquals("22H-23H59 VEN" + RoadSignDescCleaner.RULE_SEPARATOR + "00H00-23H59 SAM" + RoadSignDescCleaner.RULE_SEPARATOR + "00H00-06H DIM",
                method.invoke(null, "VEN 22H À DIM 06H"));
    }

    public void testReformatDailyTimeIntervals_2_helper() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Access the private method
        Method method = RoadSignDescCleaner.class.getDeclaredMethod("reformatDailyTimeIntervals_2_helper", String.class);
        method.setAccessible(true);

        assertEquals("\\P 23H30-00H30 MAR A MER 1 MARS AU 1 DEC; \\P 23H30-00H30 VEN A SAM 1 MARS AU 1 DEC",
                method.invoke(null, "\\P 23H30-00H30 MAR A MER; VEN A SAM  1 MARS AU 1 DEC"));

        assertEquals("23H30-00H30 MAR A MER 1 AVRIL AU 1 DEC; 23H30-00H30 VEN A SAM 1 AVRIL AU 1 DEC",
                method.invoke(null, "23H30-00H30 MAR A MER; VEN A SAM 1 AVRIL AU 1 DEC"));

        assertEquals("8H À 12H LUN MER VEN 13H À 18H MAR JEU",
                method.invoke(null, "8H À 12H LUN MER VEN 13H À 18H MAR JEU"));
    }

    public void testReformatDailyTimeIntervals_2_handler() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Access the private method
        Method method = RoadSignDescCleaner.class.getDeclaredMethod("reformatDailyTimeIntervals_2", String.class);
        method.setAccessible(true);

        assertEquals("23H30-23H59 LUN 1 AVRIL AU 1 DEC" + RoadSignDescCleaner.RULE_SEPARATOR + "00H00-23H59 MAR 1 AVRIL AU 1 DEC" + RoadSignDescCleaner.RULE_SEPARATOR + "00H00-00H30 MER 1 AVRIL AU 1 DEC" + RoadSignDescCleaner.RULE_SEPARATOR + "23H30-23H59 JEU 1 AVRIL AU 1 DEC" + RoadSignDescCleaner.RULE_SEPARATOR + "00H00-00H30 VEN 1 AVRIL AU 1 DEC",
                method.invoke(null, "23H30-00H30 LUN A MER; JEU A VEN  1 AVRIL AU 1 DEC"));

        assertEquals("23H30-23H59 LUN 1 MARS AU 1 DEC" + RoadSignDescCleaner.RULE_SEPARATOR + "00H00-00H30 MAR 1 MARS AU 1 DEC" + RoadSignDescCleaner.RULE_SEPARATOR + "23H30-23H59 JEU 1 MARS AU 1 DEC" + RoadSignDescCleaner.RULE_SEPARATOR + "00H00-00H30 VEN 1 MARS AU 1 DEC",
                method.invoke(null, "23H30-00H30 LUN A MAR; JEU A VEN  1 MARS AU 1 DEC"));

        assertEquals("\\P 23H30-23H59 MAR 25 MARS AU 30 DEC" + RoadSignDescCleaner.RULE_SEPARATOR + "\\P 00H00-00H30 MER 25 MARS AU 30 DEC" + RoadSignDescCleaner.RULE_SEPARATOR + "\\P 23H30-23H59 VEN 25 MARS AU 30 DEC" + RoadSignDescCleaner.RULE_SEPARATOR + "\\P 00H00-00H30 SAM 25 MARS AU 30 DEC",
                method.invoke(null, "\\P 23H30-00H30 MAR A MER; VEN A SAM 25 MARS AU 30 DEC"));

        assertEquals("8H À 12H LUN MER VEN 13H À 18H MAR JEU",
                method.invoke(null, "8H À 12H LUN MER VEN 13H À 18H MAR JEU"));
    }

    public void testReformatDailyTimeIntervals_3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = RoadSignDescCleaner.class.getDeclaredMethod("reformatDailyTimeIntervals_3", String.class);
        method.setAccessible(true);

        assertEquals("8H À 12H LUN MER VEN" + RoadSignDescCleaner.RULE_SEPARATOR + "13H À 18H MAR JEU",
                method.invoke(null, "8H À 12H LUN MER VEN 13H À 18H MAR JEU"));

        assertEquals("MAR JEU 8H À 12H" + RoadSignDescCleaner.RULE_SEPARATOR + "LUN MER VEN 14H À 17H",
                method.invoke(null, "MAR JEU 8H À 12H LUN MER VEN 14H À 17H"));

        assertEquals("20MIN 8H À 12H LUN MER VEN" + RoadSignDescCleaner.RULE_SEPARATOR + "20MIN 13H À 18H MAR JEU",
                method.invoke(null, "20MIN 8H À 12H LUN MER VEN 13H À 18H MAR JEU"));

        assertEquals("40 MIN MAR JEU 8H À 12H" + RoadSignDescCleaner.RULE_SEPARATOR + "40 MIN LUN MER VEN 14H À 17H",
                method.invoke(null, "40 MIN MAR JEU 8H À 12H LUN MER VEN 14H À 17H"));

        assertEquals("\\P 8H À 12H LUN MER VEN" + RoadSignDescCleaner.RULE_SEPARATOR + "\\P 13H À 18H MAR JEU",
                method.invoke(null, "\\P 8H À 12H LUN MER VEN 13H À 18H MAR JEU"));

        assertEquals("\\P MAR JEU 8H À 12H" + RoadSignDescCleaner.RULE_SEPARATOR + "\\P LUN MER VEN 14H À 17H",
                method.invoke(null, "\\P MAR JEU 8H À 12H LUN MER VEN 14H À 17H"));

        assertEquals("\\P 120MIN 8H À 12H LUN MER VEN" + RoadSignDescCleaner.RULE_SEPARATOR + "\\P 120MIN 13H À 18H MAR JEU",
                method.invoke(null, "\\P 120MIN 8H À 12H LUN MER VEN 13H À 18H MAR JEU"));

        assertEquals("\\P 60 MIN MAR JEU 8H À 12H" + RoadSignDescCleaner.RULE_SEPARATOR + "\\P 60 MIN LUN MER VEN 14H À 17H",
                method.invoke(null, "\\P 60 MIN MAR JEU 8H À 12H LUN MER VEN 14H À 17H"));

        assertEquals("30 MIN MAR MER VEN 9H À 16H30" + RoadSignDescCleaner.RULE_SEPARATOR + "30 MIN LUN JEU 12H À 16H30",
                method.invoke(null, "30 MIN - MAR MER VEN - 9H À 16H30 - LUN JEU - 12H À 16H30"));

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
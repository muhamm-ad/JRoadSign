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
        Method method = RoadSignDescCleaner.class.getDeclaredMethod("reformatDailyTimeIntervals", String.class);
        method.setAccessible(true);

        // Test cases
        assertEquals("\\P MARS 01 A DEC 01",
                method.invoke(null, "\\P MARS 01 A DEC 01"));

        assertEquals("\\P 20H30-21H30 MAR ET JEU MARS AU 1 DEC",
                method.invoke(null, "\\P 20H30-21H30 MARDI ET JEUDI MARS AU 1 DEC"));

        assertEquals("\\P LIVRAISON SEULEMENT 08H-17H LUN AU VEN",
                method.invoke(null, "\\P LIVRAISON SEULEMENT 08H-17H LUN AU VEN"));

        assertEquals("17H-23H59 MAR; 00H00-17H MER",
                method.invoke(null, "17H MARDI A 17H MERCREDI"));

        assertEquals("15H-23H59 MAR; 00H00-23H59 MER; 00H00-19H JEU",
                method.invoke(null, "15H MAR A 19H JEUDI"));

        assertEquals("08H24-23H59 SAM; 00H00-23H59 DIM; 00H00-09H00 LUN",
                method.invoke(null, "08H24 SAMEDI A 09H00 LUNDI"));

        assertEquals("23H59-23H59 SAM; 00H00-23H59 DIM; 00H00-23H59 LUN",
                method.invoke(null, "23H59 SAMEDI A 23H59 LUNDI"));

        assertEquals("00H-23H59 LUN; 00H00-23H59 MAR; 00H00-23H59 MER",
                method.invoke(null, "00H LUNDI A 23H59 MERCREDI"));

        assertEquals("07H30-23H59 VEN; 00H00-23H59 SAM; 00H00-07H DIM",
                method.invoke(null, "07H30 VEN A 07H DIMANCHE"));

        assertEquals("09H-23H59 MER; 00H00-23H59 JEU; 00H00-14H10 VEN",
                method.invoke(null, "09H MER A 14H10 VEN"));

        assertEquals("12H00-23H59 SAM; 00H00-23H59 DIM; 00H00-23H59 LUN; 00H00-18H MAR",
                method.invoke(null, "12H00 SAM A 18H MARDI"));

        assertEquals("00H-23H59 LUN; 00H00-23H59 MAR; 00H00-23H59 MER",
                method.invoke(null, "00H LUNDI A 23H59 MERCREDI"));

        assertEquals("07H00-23H59 VEN; 00H00-23H59 SAM; 00H00-07H DIM",
                method.invoke(null, "07H00 VEN A 07H DIMANCHE"));

        assertEquals("09H00-23H59 MER; 00H00-23H59 JEU; 00H00-14H00 VEN",
                method.invoke(null, "09H00 MERCREDI A 14H00 VENDREDI"));

        assertEquals("12H-23H59 DIM; 00H00-23H59 LUN; 00H00-18H MAR",
                method.invoke(null, "12H DIMANCHE A 18H MARDI"));

        assertEquals("05H-23H59 MAR; 00H00-23H59 MER; 00H00-20H JEU",
                method.invoke(null, "05H MAR A 20H JEU"));

        assertEquals("06H30-23H59 SAM; 00H00-23H59 DIM; 00H00-10H LUN",
                method.invoke(null, "06H30 SAMEDI A 10H LUNDI"));

        assertEquals("04H00-23H59 MER; 00H00-23H59 JEU; 00H00-12H00 VEN",
                method.invoke(null, "04H00 MER A 12H00 VEN"));

        assertEquals("22H-23H59 VEN; 00H00-23H59 SAM; 00H00-06H DIM",
                method.invoke(null, "22H VEN A 06H DIMANCHE"));

        assertEquals("17H-23H59 MAR; 00H00-17H MER; 17H-23H59 JEU; 00H00-17H VEN; 17H-23H59 SAM; 00H00-23H59 DIM; 00H00-17H LUN",
                method.invoke(null, "17H MAR A 17H MER - 17H JEU A 17H VEN - 17H SAM A 17H LUN"));

        assertEquals("\\P 17H-23H59 MAR; \\P 00H00-17H MER; \\P 17H-23H59 JEU; \\P 00H00-17H VEN; \\P 17H-23H59 SAM; \\P 00H00-23H59 DIM; \\P 00H00-17H LUN",
                method.invoke(null, "\\P 17H MARDI A 17H MER - 17H JEUDI A 17H VEN - 17H SAM A17H LUN"));

        // Test cases with duration prefix
        assertEquals("\\P 120MIN 17H-23H59 MAR; \\P 120MIN 00H00-17H MER",
                method.invoke(null, "\\P 120MIN - 17H MARDI A 17H MERCREDI"));

        assertEquals("90MIN 15H-23H59 MAR; 90MIN 00H00-23H59 MER; 90MIN 00H00-19H JEU",
                method.invoke(null, "90MIN - 15H MAR A 19H JEUDI"));

        assertEquals("60MIN 08H24-23H59 SAM; 60MIN 00H00-23H59 DIM; 60MIN 00H00-09H00 LUN",
                method.invoke(null, "60MIN - 08H24 SAMEDI A 09H00 LUNDI"));

        assertEquals("30MIN 23H59-23H59 SAM; 30MIN 00H00-23H59 DIM; 30MIN 00H00-23H59 LUN",
                method.invoke(null, "30MIN - 23H59 SAMEDI A 23H59 LUNDI"));

        assertEquals("120MIN 00H-23H59 LUN; 120MIN 00H00-23H59 MAR; 120MIN 00H00-23H59 MER",
                method.invoke(null, "120MIN - 00H LUNDI A 23H59 MERCREDI"));
    }


    public void testReformatDailyTimeIntervals_2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Access the private method
        Method method = RoadSignDescCleaner.class.getDeclaredMethod("reformatDailyTimeIntervals", String.class);
        method.setAccessible(true);

        // Test cases for various scenarios
        assertEquals("120MIN 17H-23H59 LUN; 120MIN 00H00-17H MAR; 120MIN 17H-23H59 MER; 120MIN 00H00-17H JEU; 120MIN 17H-23H59 VEN; 120MIN 00H00-17H SAM",
                method.invoke(null, "120MIN - LUN 17H À MAR 17H - MER 17H À JEU 17H - VEN 17H À SAM 17H"));

        assertEquals("08H-23H59 LUN; 00H00-12H MAR; 12H-23H59 MAR; 00H00-18H MER",
                method.invoke(null, "LUN 08H À MAR 12H - MAR 12H À MER 18H"));

        assertEquals("09H-23H59 LUN; 00H00-09H MAR; 10H-23H59 MAR; 00H00-10H MER; 11H-23H59 MER; 00H00-11H JEU",
                method.invoke(null, "LUN 09H À MAR 09H - MAR 10H À MER 10H - MER 11H À JEU 11H"));

        assertEquals("\\P 17H-23H59 LUN; \\P 00H00-17H MAR; \\P 17H-23H59 MER; \\P 00H00-17H JEU; \\P 17H-23H59 VEN; \\P 00H00-17H SAM",
                method.invoke(null, "\\P LUN 17H À MAR 17H - MER 17H À JEU 17H - VEN 17H À SAM 17H"));

        assertEquals("60MIN 17H-23H59 VEN; 60MIN 00H00-17H SAM; 60MIN 17H-23H59 SAM; 60MIN 00H00-06H DIM",
                method.invoke(null, "60MIN - VEN 17H À SAM 17H - SAM 17H À DIM 06H"));

        assertEquals("22H-23H59 LUN; 00H00-23H59 MAR; 00H00-23H59 MER; 00H00-23H59 JEU; 00H00-22H VEN",
                method.invoke(null, "LUN 22H À VEN 22H"));

        assertEquals("09H00-23H59 LUN; 00H00-17H MAR; 17H-23H59 MER; 00H00-23H59 JEU; 00H00-18H VEN",
                method.invoke(null, "LUN 09H00 À MAR 17H - MER 17H À VEN 18H"));

        assertEquals("08H30-23H59 LUN; 00H00-23H59 MAR; 00H00-17H MER",
                method.invoke(null, "LUN 08H30 À MER 17H"));

        assertEquals("120MIN 12H-23H59 SAM; 120MIN 00H00-23H59 DIM; 120MIN 00H00-18H LUN",
                method.invoke(null, "120MIN - SAM 12H À LUN 18H"));

        assertEquals("23H-23H59 LUN; 00H00-23H59 MAR; 00H00-23H59 MER; 00H00-23H59 JEU; 00H00-23H VEN",
                method.invoke(null, "LUN 23H À VEN 23H"));

        assertEquals("07H-23H59 VEN; 00H00-23H59 SAM; 00H00-07H DIM",
                method.invoke(null, "VEN 07H À DIM 07H"));

        assertEquals("06H30-23H59 SAM; 00H00-23H59 DIM; 00H00-10H LUN",
                method.invoke(null, "SAM 06H30 À LUN 10H"));

        assertEquals("04H00-23H59 MER; 00H00-23H59 JEU; 00H00-12H VEN",
                method.invoke(null, "MER 04H00 À VEN 12H"));

        assertEquals("22H-23H59 VEN; 00H00-23H59 SAM; 00H00-06H DIM",
                method.invoke(null, "VEN 22H À DIM 06H"));

        assertEquals("120MIN 17H-23H59 LUN; 120MIN 00H00-17H MAR; 120MIN 17H-23H59 MER; 120MIN 00H00-17H JEU; 120MIN 17H-23H59 VEN; 120MIN 00H00-23H59 SAM; 120MIN 00H00-17H DIM",
                method.invoke(null, "120MIN - LUN 17H À MAR 17H - MER 17H À JEU 17H - VEN 17H À DIM 17H"));

        assertEquals("\\P 17H-23H59 LUN; \\P 00H00-17H MAR; \\P 17H-23H59 MER; \\P 00H00-17H JEU; \\P 17H-23H59 VEN; \\P 00H00-23H59 SAM; \\P 00H00-17H DIM",
                method.invoke(null, "\\P LUN 17H À MAR 17H - MER 17H À JEU 17H - VEN 17H À DIM 17H"));
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
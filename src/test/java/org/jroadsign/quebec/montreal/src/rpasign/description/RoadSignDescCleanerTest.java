package org.jroadsign.quebec.montreal.src.rpasign.description;

import junit.framework.TestCase;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RoadSignDescCleanerTest extends TestCase {

    public void testCleanDescription() {
        assertEquals("", RoadSignDescCleaner.cleanDescription(""));
        // TODO : Add tests
    }

    public void testTestCleanDescriptionCode() {
        // TODO : Add tests
    }

    public void testReformatDailyTimeIntervals()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Access the private method
        Method method = RoadSignDescCleaner.class.getDeclaredMethod("reformatDailyTimeIntervals", String.class);
        method.setAccessible(true);

        // Test cases
        assertEquals("\\P MARS 01 A DEC 01", method.invoke(null, "\\P MARS 01 A DEC 01"));
        assertEquals("\\P 20H30-21H30 MAR ET JEU, 1 MARS AU 1 DEC", method.invoke(null, "\\P 20H30-21H30 MARDI ET JEUDI, 1 MARS AU 1 DEC"));
        assertEquals("\\P LIVRAISON SEULEMENT 08H-17H LUN AU VEN", method.invoke(null, "\\P LIVRAISON SEULEMENT 08H-17H LUN AU VEN"));

        assertEquals("17H-23H59 MAR; 00H00-17H MER", method.invoke(null, "17H MARDI A 17H MERCREDI"));
        assertEquals("15H-23H59 MAR; 00H00-23H59 MER; 00H00-19H JEU", method.invoke(null, "15H MAR A 19H JEUDI"));
        assertEquals("08H24-23H59 SAM; 00H00-23H59 DIM; 00H00-09H00 LUN", method.invoke(null, "08H24 SAMEDI A 09H00 LUNDI"));
        assertEquals("23H59-23H59 SAM; 00H00-23H59 DIM; 00H00-23H59 LUN", method.invoke(null, "23H59 SAMEDI A 23H59 LUNDI"));
        assertEquals("00H-23H59 LUN; 00H00-23H59 MAR; 00H00-23H59 MER", method.invoke(null, "00H LUNDI A 23H59 MERCREDI"));
        assertEquals("07H30-23H59 VEN; 00H00-23H59 SAM; 00H00-07H DIM", method.invoke(null, "07H30 VEN A 07H DIMANCHE"));
        assertEquals("09H-23H59 MER; 00H00-23H59 JEU; 00H00-14H10 VEN", method.invoke(null, "09H MER A 14H10 VEN"));
        assertEquals("12H00-23H59 SAM; 00H00-23H59 DIM; 00H00-23H59 LUN; 00H00-18H MAR", method.invoke(null, "12H00 SAM A 18H MARDI"));
        assertEquals("00H-23H59 LUN; 00H00-23H59 MAR; 00H00-23H59 MER", method.invoke(null, "00H LUNDI A 23H59 MERCREDI"));
        assertEquals("07H00-23H59 VEN; 00H00-23H59 SAM; 00H00-07H DIM", method.invoke(null, "07H00 VEN A 07H DIMANCHE"));
        assertEquals("09H00-23H59 MER; 00H00-23H59 JEU; 00H00-14H00 VEN", method.invoke(null, "09H00 MERCREDI A 14H00 VENDREDI"));
        assertEquals("12H-23H59 DIM; 00H00-23H59 LUN; 00H00-18H MAR", method.invoke(null, "12H DIMANCHE A 18H MARDI"));
        assertEquals("05H-23H59 MAR; 00H00-23H59 MER; 00H00-20H JEU", method.invoke(null, "05H MAR A 20H JEU"));
        assertEquals("06H30-23H59 SAM; 00H00-23H59 DIM; 00H00-10H LUN", method.invoke(null, "06H30 SAMEDI A 10H LUNDI"));
        assertEquals("04H00-23H59 MER; 00H00-23H59 JEU; 00H00-12H00 VEN", method.invoke(null, "04H00 MER A 12H00 VEN"));
        assertEquals("22H-23H59 VEN; 00H00-23H59 SAM; 00H00-06H DIM", method.invoke(null, "22H VEN A 06H DIMANCHE"));

        assertEquals("17H-23H59 MAR; 00H00-17H MER; 17H-23H59 JEU; 00H00-17H VEN; 17H-23H59 SAM; 00H00-23H59 DIM; 00H00-17H LUN",
                method.invoke(null, "17H MAR A 17H MER - 17H JEU A 17H VEN - 17H SAM A 17H LUN"));

        assertEquals("\\P 17H-23H59 MAR; \\P 00H00-17H MER; \\P 17H-23H59 JEU; \\P 00H00-17H VEN; \\P 17H-23H59 SAM; \\P 00H00-23H59 DIM; \\P 00H00-17H LUN",
                method.invoke(null, "\\P 17H MARDI A 17H MER - 17H JEUDI A 17H VEN - 17H SAM A17H LUN"));
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

        assertEquals("\\P MARS 01 A DEC 01", method.invoke(null, "\\P MARS01 A DEC 01"));
        assertEquals("\\P DEC 01 A AVRIL 01", method.invoke(null, "\\P DEC 01 A AVRIL02"));
        assertEquals("\\P JUIN 01 A JUIL 01", method.invoke(null, "\\P JUIN01 A JUIL01"));
    }
}
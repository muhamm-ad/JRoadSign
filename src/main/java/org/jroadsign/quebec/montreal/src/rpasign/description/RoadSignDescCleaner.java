package org.jroadsign.quebec.montreal.src.rpasign.description;

import org.jetbrains.annotations.NotNull;
import org.jroadsign.quebec.montreal.src.rpasign.RpaSignCode;
import org.jroadsign.quebec.montreal.src.rpasign.description.common.GlobalConfigs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RoadSignDescCleaner {

    private RoadSignDescCleaner() {
    }

    public static final String RULE_SEPARATOR = " & ";

    /**
     * This method Cleans up the given description by performing several operations on it.
     * It normalizes the string to upper case, removes possible prefixes, extra spaces, unwanted characters,
     * misspellings, and adds spaces where needed.
     *
     * @param strDescription The original description to be cleaned.
     * @param code           The RpaSignCode used to determine if a specific cleaning operation is required.
     * @return A cleaned version of the original description.
     */
    public static @NotNull String cleanDescription(@NotNull String strDescription, @NotNull RpaSignCode code) {
        String cleanedDescription = strDescription.toUpperCase().trim();
        cleanedDescription = handleNoParking(removeUnnecessaryCharacters(cleanedDescription));

        if (code.getStr().startsWith("S") && !cleanedDescription.startsWith("\\P")) {
            cleanedDescription = "\\P " + cleanedDescription;
        }

        for (Map.Entry<String, String> entry : GlobalConfigs.WEEKLY_DAYS_ABBREVIATIONS_MAP.entrySet()) {
            String abbreviation = entry.getValue().trim();
            cleanedDescription = cleanedDescription.replaceAll(entry.getKey(), abbreviation).trim();
        }

        cleanedDescription = correctSpelling(cleanedDescription);
        cleanedDescription = insertSpacesWhereNeeded(cleanedDescription);

        cleanedDescription = switch (code) {
            case SLR_ST_75
                // ex : "\P 9H À 17H LUN MER VEN 15 NOV AU 15 MARS; 11H À 12H MERCREDI 15 MARS AU 15 NOV"
                    -> cleanedDescription.replace(";", RULE_SEPARATOR + "\\P");

            case SLR_ST_82, SLR_ST_84, SLR_ST_98
                // ex : "\P LUN MER VEN 8H À 12H - MAR JEU 13H À 17H"
                // ex : "\P 9H À 17H MAR JEU 15 NOV AU 15 MARS - 11H À 12H JEUDI 15 MARS AU 15 NOV"
                    -> cleanedDescription.replace(" -", RULE_SEPARATOR + "\\P");

            case SS_JM
                // ex : "\P 07h-16h LUN A VEN ET 07h-12h SAMEDI"
                    -> cleanedDescription.replace("ET", RULE_SEPARATOR + "\\P");

            case SD_OP
                // ex : "\P 18h-24h LUN A VEN  +  08h-24h SAM ET DIM"
                    -> cleanedDescription.replace("+", RULE_SEPARATOR + "\\P");

            case SLR_ST_80, SLR_ST_81, SLR_ST_111, SLR_ST_172, SLR_ST_174, SLR_ST_175
                // ex : "\P 17H MAR À 17H MER; 17H JEU À 17H VEN; 17H SAM À 17H LUN"
                // ex : "\P 17H MAR À 17H MER; \P 17H JEU À 17H VEN; \P 17H SAM À 17H LUN"
                // ex : "\P 120 MIN - LUN 17H À MAR 17H - MER 17H À JEU 17H - VEN 17H À SAM 17H"
                    -> reformatDailyTimeIntervals_1(cleanedDescription);
            case SB_NX, SB_NX_A, SB_NY, SB_NY_A
                // ex : "\P 23h30-00h30  MAR A MER, VEN A SAM  1 MARS AU 1 DEC. "
                    -> reformatDailyTimeIntervals_2(cleanedDescription);

            case SLR_ST_79, SLR_ST_105, SLR_ST_106, SLR_ST_107, SLR_ST_135
                // ex : "\P 8H À 12H LUN MER VEN 13H À 18H MAR JEU"
                // ex : "\P MAR JEU 8H À 12H LUN MER VEN 14H À 17H"
                // ex : "\P 30 MIN - MAR MER VEN - 9H À 16H30 - LUN JEU - 12H À 16H30"
                    -> reformatDailyTimeIntervals_3(cleanedDescription);

            default -> cleanedDescription;
        };

        return cleanedDescription.trim();
    }

    /**
     * This method removes unnecessary characters from the given description.
     *
     * @param description The original description to be cleaned.
     * @return A cleaned version of the original description.
     */
    private static @NotNull String removeUnnecessaryCharacters(@NotNull String description) {
        return description
                .replace(".", " ")
                .replace(",", ";")
                .replaceAll("\\s+", " ")
                .replace("É", "E")
                .replace("È", "E")
                .replace("Ê", "E")
                .replace("À", "A")
                .replace("1ER", "1")
                .replace("&", ";")
                .trim();
    }

    /**
     * This method handles the authorization case of parking.
     *
     * @param description The original description to be handled.
     * @return The description after handling the authorization case with '\P'.
     */
    private static @NotNull String handleNoParking(@NotNull String description) {
        return description
                .replace("(NO PARKING)", "\\P")
                .replace("/P", "\\P")
                .replace("\\P EXCEPTE", "\\P EN TOUT TEMPS EXCEPTE")
                .replaceAll("^EXCEPTE", "EN TOUT TEMPS EXCEPTE")
                .replaceAll("STAT\\.? INT\\.? (DE\\s*)?", "\\\\P ")
                .trim();
    }

    /**
     * This method reformats the time range string in the description, handling different interval patterns.
     * It is used to fix intervals like:
     * - "17H26 SAM A 18H40 LUN" to something like "17H26-23H59 SAM & 00H00-23H59 DIM & 00H00-18H40 LUN".
     * - "LUN 17H À MAR 17H - MER 17H À JEU 17H - VEN 17H À SAM 17H" to something like
     * "17H-23H59 LUN & 00H00-17H MAR; 17H-23H59 MER; 00H00-17H JEU; 17H-23H59 VEN; 00H00-17H SAM".
     * <p>
     * Additionally, if a duration prefix is present like "120MIN", it will be distributed across each interval.
     * If the description contains a parking restriction prefix (`\P`),
     * it ensures that the prefix is correctly applied to each segment.
     *
     * @param description The original description.
     * @return The description with the day-hour string reformatted.
     */
    private static @NotNull String reformatDailyTimeIntervals_1(@NotNull String description) {
        String descCopy = description;
        boolean isParkingAuthorized = !description.startsWith("\\P");

        String durationPrefixPattern = "(\\d+\\s*MIN)(?:\\s*-\\s*)?";
        Pattern durationPattern = Pattern.compile(durationPrefixPattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

        String extractedDurationPrefix = "";

        Matcher durationMatcher = durationPattern.matcher(descCopy);
        if (durationMatcher.find()) {
            extractedDurationPrefix = durationMatcher.group(1);
            descCopy = descCopy.substring(durationMatcher.end()).trim();  // Remove the duration prefix from the descCopy
        }

        String timePattern = "(\\d{1,2}\\s*H\\s*(?:\\d{1,2})?)";
        String dayPattern = "(" + GlobalConfigs.WEEKLY_DAYS_PATTERN + ")";

        String fullIntervalPattern1 = timePattern + "\\s*" + dayPattern + "\\s*(?:AU?)\\s*" + timePattern + "\\s*" + dayPattern;
        String fullIntervalPattern2 = dayPattern + "\\s*" + timePattern + "\\s*(?:À|A)\\s*" + dayPattern + "\\s*" + timePattern;

        Matcher matcher1 = Pattern.compile(fullIntervalPattern1, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(descCopy);
        Matcher matcher2 = Pattern.compile(fullIntervalPattern2, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(descCopy);

        Matcher selectedMatcher;
        boolean isPattern1Matched = matcher1.find();
        if (isPattern1Matched) {
            selectedMatcher = matcher1;
        } else if (matcher2.find()) {
            selectedMatcher = matcher2;
        } else {
            return description;
        }

        StringBuilder reformatted = new StringBuilder();

        do {
            String startDay, startTime, endDay, endTime;
            if (isPattern1Matched) {
                startTime = selectedMatcher.group(1).trim();
                startDay = selectedMatcher.group(2);
                endTime = selectedMatcher.group(3).trim();
                endDay = selectedMatcher.group(4);
            } else {
                startDay = selectedMatcher.group(1);
                startTime = selectedMatcher.group(2).trim();
                endDay = selectedMatcher.group(3);
                endTime = selectedMatcher.group(4).trim();
            }

            List<String> daysInRange = getDaysInRange(startDay, endDay);
            for (int i = 0; i < daysInRange.size(); i++) {
                if (!reformatted.isEmpty()) reformatted.append(RULE_SEPARATOR);

                reformatted
                        .append(isParkingAuthorized ? "" : "\\P ")
                        .append(extractedDurationPrefix.isEmpty() ? "" : extractedDurationPrefix + " ");

                if (i == 0) {
                    reformatted
                            .append(startTime).append("-23H59 ")
                            .append(daysInRange.get(i));
                } else if (i == daysInRange.size() - 1) {
                    reformatted
                            .append("00H00-").append(endTime)
                            .append(" ").append(daysInRange.get(i));
                } else {
                    reformatted
                            .append("00H00-23H59 ")
                            .append(daysInRange.get(i));
                }
            }
        } while (selectedMatcher.find());

        String finalDescription = reformatted.toString().trim();
        return finalDescription.isEmpty() ? description : finalDescription;
    }

    /**
     * This helper method reformats a description string containing time intervals, day intervals, and month intervals.
     * It handles cases where the intervals are provided in a format like:
     * - "\P 23H30-00H30 MAR A MER; VEN A SAM 1 MARS AU 1 DEC"
     * The method will convert this to:
     * "\P 23H30-00H30 MAR A MER 1 MARS AU 1 DEC & \P 23H30-00H30 VEN A SAM 1 MARS AU 1 DEC".
     * <p>
     * The method extracts the time intervals, day intervals, and month intervals, and ensures that they are correctly combined.
     * It also handles parking restriction prefixes (`\P`) and ensures that the prefix is applied consistently.
     *
     * @param description The original description string.
     * @return The reformatted description with time intervals, day intervals, and month intervals combined correctly.
     */
    private static @NotNull String reformatDailyTimeIntervals_2_helper(@NotNull String description) {
        String descCopy = description;
        boolean isParkingAuthorized = !description.startsWith("\\P");
        descCopy = descCopy.replace("\\P", "").trim();

        String timePattern = "\\d{1,2}\\s*H\\s*(?:\\d{1,2})?";
        String timeIntervalPattern = timePattern + "\\s*(?:-|A)" + timePattern;

        String dayIntervalPattern = "(?:" + GlobalConfigs.WEEKLY_DAYS_PATTERN + ")\\s*(?:AU?)\\s*(?:" + GlobalConfigs.WEEKLY_DAYS_PATTERN + ")";

        String dayMonthPattern = GlobalConfigs.TWO_DIGIT + "\\s*(?:" + GlobalConfigs.ANNUAL_MONTH_PATTERN + ")";
        String monthDayPattern = "(?:" + GlobalConfigs.ANNUAL_MONTH_PATTERN + ")\\s*" + GlobalConfigs.TWO_DIGIT;
        String monthIntervalPattern = "(?:" + dayMonthPattern + "\\s*(?:AU?)\\s*" + dayMonthPattern + ")|(?:" + monthDayPattern + "\\s*(?:AU?)\\s*" + monthDayPattern + ")";

        String pattern = "^(" + timeIntervalPattern + ")\\s*(" + dayIntervalPattern + "\\s*(?:;|,)\\s*" + dayIntervalPattern + ")\\s*(" + monthIntervalPattern + ")\\s*";

        Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(descCopy);

        StringBuilder reformatted = new StringBuilder();
        String finalDescription = "";

        if (matcher.find()) {
            String timeInterval = matcher.group(1);
            String dayIntervals = matcher.group(2);
            String monthsInterval = matcher.group(3);

            for (String dayInt : dayIntervals.split(";")) {
                dayInt = dayInt.trim();
                if (!reformatted.isEmpty()) reformatted.append("; ");
                reformatted
                        .append(isParkingAuthorized ? "" : "\\P ")
                        .append(timeInterval).append(" ")
                        .append(dayInt).append(" ")
                        .append(monthsInterval);
            }
        }

        finalDescription = reformatted.toString().trim();
        return (finalDescription.isEmpty()) ? description : finalDescription;
    }

    /**
     * This method reformats a description string that contains time intervals, day intervals, and month intervals,
     * into a more detailed breakdown where each day in the interval is specified with its respective time and month range.
     * It handles cases such as:
     * - "\P 23H30-00H30 MAR A MER 1 MARS AU 1 DEC & \P 23H30-00H30 VEN A SAM 1 MARS AU 1 DEC"
     * The method will convert this to:
     * "\P 23H30-23H59 MAR 1 MARS AU 1 DEC & \P 00H00-00H30 MER 1 MARS AU 1 DEC & \P 23H30-23H59 VEN 1 MARS AU 1 DEC & \P 00H00-00H30 SAM 1 MARS AU 1 DEC".
     * <p>
     * The method uses a helper method to initially process the input string into a more structured format,
     * and then further breaks down the intervals into individual days with accurate time intervals.
     *
     * @param description The original description string.
     * @return The reformatted description with detailed day and time intervals.
     */
    private static @NotNull String reformatDailyTimeIntervals_2(@NotNull String description) {

        String desc = reformatDailyTimeIntervals_2_helper(description);
        if (desc.equalsIgnoreCase(description)) return description;

        StringBuilder reformatted = new StringBuilder();
        String finalDescription = "";

        String timePattern = "\\d{1,2}\\s*H\\s*(?:\\d{1,2})?";
        String timeIntervalPattern = "(" + timePattern + ")\\s*(?:-|A)(" + timePattern + ")";

        String dayIntervalPattern = "(" + GlobalConfigs.WEEKLY_DAYS_PATTERN + ")\\s*(?:AU?)\\s*(" + GlobalConfigs.WEEKLY_DAYS_PATTERN + ")";

        String dayMonthPattern = GlobalConfigs.TWO_DIGIT + "\\s*(?:" + GlobalConfigs.ANNUAL_MONTH_PATTERN + ")";
        String monthDayPattern = "(?:" + GlobalConfigs.ANNUAL_MONTH_PATTERN + ")\\s*" + GlobalConfigs.TWO_DIGIT;
        String monthIntervalPattern = "((?:" + dayMonthPattern + "\\s*(?:AU?)\\s*" + dayMonthPattern + ")|(?:" + monthDayPattern + "\\s*(?:AU?)\\s*" + monthDayPattern + "))";

        String sPattern = "(?:\\\\P)?\\s*" + timeIntervalPattern + "\\s*" + dayIntervalPattern + "\\s*" + monthIntervalPattern;
        Pattern pattern = Pattern.compile(sPattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        String startDay, startTime, endDay, endTime, monthInterval;

        for (String rule : desc.split(";")) {
            boolean isParkingAuthorized = !description.startsWith("\\P");
            Matcher matcher = pattern.matcher(rule.trim());
            if (matcher.find()) {
                startTime = matcher.group(1).trim();
                endTime = matcher.group(2).trim();
                startDay = matcher.group(3);
                endDay = matcher.group(4);
                monthInterval = matcher.group(5);

                List<String> daysInRange = getDaysInRange(startDay, endDay);
                for (int i = 0; i < daysInRange.size(); i++) {
                    if (!reformatted.isEmpty()) reformatted.append(RULE_SEPARATOR);
                    reformatted.append(isParkingAuthorized ? "" : "\\P ");
                    if (i == 0) {
                        reformatted
                                .append(startTime).append("-23H59 ")
                                .append(daysInRange.get(i));
                    } else if (i == daysInRange.size() - 1) {
                        reformatted
                                .append("00H00-").append(endTime).append(" ")
                                .append(daysInRange.get(i));
                    } else {
                        reformatted
                                .append("00H00-23H59 ")
                                .append(daysInRange.get(i));
                    }
                    reformatted.append(" ").append(monthInterval);
                }
            }
        }

        finalDescription = reformatted.toString().trim();
        return finalDescription.isEmpty() ? description : finalDescription;
    }

    /**
     * This method reformats the time range string in the description, separating patterns.
     * It is used to fix intervals like:
     * - "8H À 12H LUN MER VEN 13H À 18H MAR JEU" to something like "8H À 12H LUN MER VEN & 13H À 18H MAR JEU".
     * - "MAR JEU 8H À 12H LUN MER VEN 14H À 17H" to something like "MAR JEU 8H À 12H & LUN MER VEN 14H À 17H".
     * - "30 MIN - MAR MER VEN - 9H À 16H30 - LUN JEU - 12H À 16H30" to something like "30 MIN MAR MER VEN 9H À 16H30 & 30 MIN LUN JEU 12H À 16H30".
     * <p>
     * Additionally, if a parking restriction prefix (`\P`) and/or duration prefix is present like "120MIN",
     * it ensures that they are correctly applied to each pattern.
     *
     * @param description The original description.
     * @return The reformatted description.
     */
    private static @NotNull String reformatDailyTimeIntervals_3(@NotNull String description) {
        String descCopy = description;
        boolean isParkingAuthorized = !descCopy.startsWith("\\P");
        descCopy = descCopy.replace("\\P", "").trim();

        // Extract the duration prefix if present
        String durationPrefixPattern = "(\\d+\\s*MIN)(?:\\s*-\\s*)?";
        Pattern durationPattern = Pattern.compile(durationPrefixPattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

        String extractedDurationPrefix = "";
        Matcher durationMatcher = durationPattern.matcher(descCopy);
        if (durationMatcher.find()) {
            extractedDurationPrefix = durationMatcher.group(1);
            descCopy = descCopy.substring(durationMatcher.end()).trim();  // Remove the duration prefix from the description
        }

        descCopy = descCopy.replaceAll("(\\s)?-(\\s)?", " ");

        String timeRangePattern = "\\d{1,2}\\s*H\\s*(?:\\d{1,2})?\\s*(?:À|A)\\s*\\d{1,2}\\s*H\\s*(?:\\d{1,2})?";
        String dayPattern = "(?:(?:" + GlobalConfigs.WEEKLY_DAYS_PATTERN + ")\\s*)+";

        String timeDayPattern = "(" + timeRangePattern + "\\s*" + dayPattern + ")";
        String dayTimePattern = "(" + dayPattern + "\\s*" + timeRangePattern + ")";

        Matcher matcher = Pattern.compile(timeDayPattern + "|" + dayTimePattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(descCopy);

        StringBuilder reformatted = new StringBuilder();
        while (matcher.find()) {
            if (!reformatted.isEmpty()) reformatted.append(RULE_SEPARATOR);
            reformatted
                    .append(isParkingAuthorized ? "" : "\\P ")
                    .append(extractedDurationPrefix.isEmpty() ? "" : extractedDurationPrefix + " ")
                    .append(matcher.group().trim());
        }

        return reformatted.isEmpty() ? description : reformatted.toString().trim();
    }


    private static @NotNull List<String> getDaysInRange(@NotNull String startDay, @NotNull String endDay) {
        List<String> daysOfWeek = Arrays.asList("LUN", "MAR", "MER", "JEU", "VEN", "SAM", "DIM");
        List<String> daysInRange = new ArrayList<>();

        int startIndex = daysOfWeek.indexOf(startDay);
        int endIndex = daysOfWeek.indexOf(endDay);

        if (startIndex == -1 || endIndex == -1) {
            throw new IllegalArgumentException("Invalid format of day of the week: " + startDay + ", " + endDay);
        }

        if (startIndex <= endIndex) {
            // Case where the start day is before or the same as the end day in the week
            for (int i = startIndex; i <= endIndex; i++) {
                daysInRange.add(daysOfWeek.get(i));
            }
        } else {
            // Case where the start day is after the end day (crossing the weekend)
            for (int i = startIndex; i < daysOfWeek.size(); i++) {
                daysInRange.add(daysOfWeek.get(i));
            }
            for (int i = 0; i <= endIndex; i++) {
                daysInRange.add(daysOfWeek.get(i));
            }
        }

        return daysInRange;
    }

    /**
     * This method corrects misspelled names in the description.
     *
     * @param description The original description.
     * @return The description with corrected names.
     */
    private static @NotNull String correctSpelling(@NotNull String description) {
        return description.replace("AVIL", GlobalConfigs.APRIL)
                .replace("AVRILS", GlobalConfigs.APRIL)
                .replace("MRS", GlobalConfigs.MARCH)
                .replace("MARSL", GlobalConfigs.MARCH)
                .replace("VEMDREDI", GlobalConfigs.FRIDAY);
    }

    /**
     * This method inserts spaces where needed in the given description.
     *
     * @param description The original description.
     * @return The description with spaces added where needed.
     */
    private static @NotNull String insertSpacesWhereNeeded(@NotNull String description) {
        description = insertSpaceBetweenLetterAndNumber(description);
        description = insertSpaceBetweenDayAndMonth(description);
        return description;
    }

    /**
     * This method inserts a space between a letter and a digit if one does not exist, except 'H' for times
     *
     * @param description The original description.
     * @return The description with spaces added between letters (except H or h) and digits.
     */
    private static @NotNull String insertSpaceBetweenLetterAndNumber(@NotNull String description) {
        /*return description
                .replaceAll("(\\d)([A-Za-z])", "$1 $2")
                .replaceAll("([A-Za-z])(\\d)", "$1 $2")
                .replaceAll("(\\d) ([Hh]) ([\\d-])", "$1$2$3")
                .replaceAll("(\\d) ([Hh])", "$1$2"); // for times*/

        Pattern letterNumberPattern = Pattern.compile("(\\p{L})(\\d)");
        Matcher letterNumberMatcher = letterNumberPattern.matcher(description);

        StringBuilder formattedDescription = new StringBuilder();
        while (letterNumberMatcher.find()) {
            if (!letterNumberMatcher.group(1).equalsIgnoreCase("H"))
                letterNumberMatcher.appendReplacement(
                        formattedDescription, letterNumberMatcher.group(1) + " " + letterNumberMatcher.group(2));
        }
        letterNumberMatcher.appendTail(formattedDescription);
        return formattedDescription.toString();
    }

    /**
     * This method adds a space between the day and month in the description if necessary.
     * It handles both "day-month" and "month-day" formats.
     *
     * @param description The original description.
     * @return The description with spaces added between day and month.
     */
    private static @NotNull String insertSpaceBetweenDayAndMonth(@NotNull String description) {
        // Define patterns to match both "day-month" and "month-day" formats
        String dayMonthPattern = "(" + GlobalConfigs.TWO_DIGIT + ")(" + GlobalConfigs.ANNUAL_MONTH_PATTERN + ")";
        String monthDayPattern = "(" + GlobalConfigs.ANNUAL_MONTH_PATTERN + ")(" + GlobalConfigs.TWO_DIGIT + ")";

        Pattern combinedPattern = Pattern.compile(
                "\\b(" + dayMonthPattern + "|" + monthDayPattern + ")\\b",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

        Matcher dateMatcher = combinedPattern.matcher(description);

        StringBuilder formattedDescription = new StringBuilder();
        while (dateMatcher.find()) {
            if (dateMatcher.group(1) != null && dateMatcher.group(2) != null) { // day-month format
                dateMatcher.appendReplacement(formattedDescription, dateMatcher.group(2) + " " + dateMatcher.group(3));
            } else if (dateMatcher.group(4) != null && dateMatcher.group(5) != null) { // month-day format
                dateMatcher.appendReplacement(formattedDescription, dateMatcher.group(4) + " " + dateMatcher.group(5));
            }
        }
        dateMatcher.appendTail(formattedDescription);
        return formattedDescription.toString();
    }


}

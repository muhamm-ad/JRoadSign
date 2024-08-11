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

        if (code.getCode().startsWith("S") && !cleanedDescription.startsWith("\\P")) {
            cleanedDescription = "\\P " + cleanedDescription;
        }

        cleanedDescription = reformatDailyTimeIntervals(cleanedDescription);
        cleanedDescription = correctSpelling(cleanedDescription);
        cleanedDescription = insertSpacesWhereNeeded(cleanedDescription);
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
                .replaceAll("\\s+", " ")
                .replace("É", "E")
                .replace("È", "E")
                .replace("Ê", "E")
                .replace("À", "A")
                .replace("1ER", "1")
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

    private static @NotNull String reformatDailyTimeIntervals(@NotNull String description) {
        String str = description;
        str = reformatDailyTimeIntervals_1(str);
        str = reformatDailyTimeIntervals_2(str);
        return str;
    }

    /**
     * This method reformats the time range string in the description, handling different interval patterns.
     * It is used to fix intervals like:
     * - "17H26 SAMEDI A 18H40 LUNDI" to something like "17H26-23H59 SAM; 00H00-23H59 DIM; 00H00-18H40 LUN".
     * - "LUN 17H À MAR 17H - MER 17H À JEU 17H - VEN 17H À SAM 17H" to something like
     * "17H-23H59 LUN; 00H00-17H MAR; 17H-23H59 MER; 00H00-17H JEU; 17H-23H59 VEN; 00H00-17H SAM".
     * <p>
     * Additionally, if a duration prefix is present like "120MIN", it will be distributed across each interval.
     * If the description contains a parking restriction prefix (`\P`),
     * it ensures that the prefix is correctly applied to each segment.
     *
     * @param description The original description.
     * @return The description with the day-hour string reformatted.
     */
    private static @NotNull String reformatDailyTimeIntervals_1(@NotNull String description) {

        boolean isParkingAuthorized = !description.startsWith("\\P");

        String durationPrefixPattern = "(\\d+\\s*MIN)(?:\\s*-\\s*)?";
        Pattern durationPattern = Pattern.compile(durationPrefixPattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

        String extractedDurationPrefix = "";

        Matcher durationMatcher = durationPattern.matcher(description);
        if (durationMatcher.find()) {
            extractedDurationPrefix = durationMatcher.group(1);
            description = description.substring(durationMatcher.end()).trim();  // Remove the duration prefix from the description
        }

        String timePattern = "(\\d{1,2}\\s*H\\s*(?:\\d{1,2})?)";
        String dayPattern = "(" + GlobalConfigs.WEEKLY_DAYS_PATTERN + ")";

        String fullIntervalPattern1 = timePattern + "\\s*" + dayPattern + "\\s*(?:AU?)\\s*" + timePattern + "\\s*" + dayPattern;
        String fullIntervalPattern2 = dayPattern + "\\s*" + timePattern + "\\s*(?:À|A)\\s*" + dayPattern + "\\s*" + timePattern;

        Pattern pattern1 = Pattern.compile(fullIntervalPattern1, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Pattern pattern2 = Pattern.compile(fullIntervalPattern2, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

        for (Map.Entry<String, String> entry : GlobalConfigs.WEEKLY_DAYS_ABBREVIATIONS_MAP.entrySet()) {
            String abbreviation = entry.getValue().trim();
            description = description.replaceAll(entry.getKey(), abbreviation).trim();
        }

        Matcher matcher1 = pattern1.matcher(description);
        Matcher matcher2 = pattern2.matcher(description);

        Matcher selectedMatcher;
        boolean isPattern1Matched = matcher1.find();

        if (isPattern1Matched) {
            selectedMatcher = matcher1;
        } else if (matcher2.find()) {
            selectedMatcher = matcher2;
        } else {
            return description;
        }

        StringBuilder reformattedIntervals = new StringBuilder();
        int segmentCount = 0;

        do {
            if (segmentCount >= 1)
                reformattedIntervals.append(isParkingAuthorized ? "; " : "; \\P ");
            else
                reformattedIntervals.append(isParkingAuthorized ? "" : "\\P ");
            segmentCount++;

            String startDay, startTime, endDay, endTime;
            if (isPattern1Matched) {
                startTime = selectedMatcher.group(1).replaceAll("\\s*", "");
                startDay = selectedMatcher.group(2);
                endTime = selectedMatcher.group(3).replaceAll("\\s*", "");
                endDay = selectedMatcher.group(4);
            } else {
                startDay = selectedMatcher.group(1);
                startTime = selectedMatcher.group(2).replaceAll("\\s*", "");
                endDay = selectedMatcher.group(3);
                endTime = selectedMatcher.group(4).replaceAll("\\s*", "");
            }

            List<String> daysInRange = getDaysInRange(startDay, endDay);
            for (int i = 0; i < daysInRange.size(); i++) {
                if (i == 0)
                    reformattedIntervals.append(startTime).append("-23H59 ").append(daysInRange.get(i)).append(isParkingAuthorized ? "; " : "; \\P ");
                else if (i == daysInRange.size() - 1)
                    reformattedIntervals.append("00H00-").append(endTime).append(" ").append(daysInRange.get(i));
                else
                    reformattedIntervals.append("00H00-23H59 ").append(daysInRange.get(i)).append(isParkingAuthorized ? "; " : "; \\P ");
            }
        } while (selectedMatcher.find());

        String finalDescription = reformattedIntervals.toString().trim();

        if (!extractedDurationPrefix.isEmpty()) {
            StringBuilder updatedDescription = new StringBuilder();

            for (String segment : finalDescription.split("; ")) {
                if (segment.startsWith("\\P"))
                    updatedDescription.append(segment.replace("\\P", "\\P " + extractedDurationPrefix)).append("; ");
                else
                    updatedDescription.append(extractedDurationPrefix + " ").append(segment).append("; ");
            }

            finalDescription = updatedDescription.toString().trim();
            if (finalDescription.endsWith(";"))
                finalDescription = finalDescription.substring(0, finalDescription.length() - 1);
        }

        return finalDescription.isEmpty() ? description : finalDescription;
    }

    /**
     * This method reformats the time range string in the description, handling specific interval patterns.
     * It is used to fix intervals like:
     * - "8H À 12H LUN MER VEN 13H À 18H MAR JEU" to something like "8H-12H LUN; 8H-12H MER; 8H-12H VEN; 13H-18H MAR; 13H-18H JEU".
     * - "MAR JEU 8H À 12H LUN MER VEN 14H À 17H" to something like "8H-12H MAR; 8H-12H JEU; 14H-17H LUN; 14H-17H MER; 14H-17H VEN".
     * <p>
     * Additionally, if a parking restriction prefix (`\P`) is present,
     * it ensures that the prefix is correctly applied to each segment.
     *
     * @param description The original description.
     * @return The description with the day-hour string reformatted.
     */
    private static @NotNull String reformatDailyTimeIntervals_2(@NotNull String description) {
        StringBuilder reformattedIntervals = new StringBuilder();
        boolean isParkingAuthorized = !description.startsWith("\\P");
        description = description.replace("\\P", "").trim();

        String timeRangePattern = "\\d{1,2}\\s*H\\s*(?:À|A)\\s*\\d{1,2}\\s*H";
        String dayPattern = "(?:(?:" + GlobalConfigs.WEEKLY_DAYS_PATTERN + ")\\s*)+";

        String timeDayPattern = "^(" + timeRangePattern + ")\\s*(" + dayPattern + ")";
        String dayTimePattern = "^(" + dayPattern + ")\\s*(" + timeRangePattern + ")";

        while (!description.isEmpty()) {
            Matcher timeDayMatcher = Pattern.compile(timeDayPattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(description);
            Matcher dayTimeMatcher = Pattern.compile(dayTimePattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(description);

            if (timeDayMatcher.find()) {
                String timeRange = timeDayMatcher.group(1)
                        .replaceAll("\\s+", "")
                        .replaceAll("A|À", "-");
                String[] days = timeDayMatcher.group(2).split("\\s+");
                for (String day : days) {
                    if (reformattedIntervals.length() > 0)
                        reformattedIntervals.append(isParkingAuthorized ? "; " : "; \\P ");
                    else
                        reformattedIntervals.append(isParkingAuthorized ? "" : "\\P ");
                    reformattedIntervals.append(timeRange).append(" ").append(day);
                }
                // Remove the matched portion from the description
                description = description.substring(timeDayMatcher.end()).trim();

            } else if (dayTimeMatcher.find()) {
                String[] days = dayTimeMatcher.group(1).split("\\s+");
                String timeRange = dayTimeMatcher.group(2)
                        .replaceAll("\\s+", "")
                        .replaceAll("A|À", "-");
                for (String day : days) {
                    if (reformattedIntervals.length() > 0)
                        reformattedIntervals.append(isParkingAuthorized ? "; " : "; \\P ");
                    else
                        reformattedIntervals.append(isParkingAuthorized ? "" : "\\P ");
                    reformattedIntervals.append(timeRange).append(" ").append(day);
                }
                // Remove the matched portion from the description
                description = description.substring(dayTimeMatcher.end()).trim();

            } else { // No more patterns matched, exit loop
                break;
            }
        }
        return reformattedIntervals.toString().isEmpty() ? description : reformattedIntervals.toString();
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

package org.jroadsign.quebec.montreal.src.rpasign.description;

import org.jetbrains.annotations.NotNull;
import org.jroadsign.quebec.montreal.src.rpasign.description.common.GlobalConfigs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RoadSignDescCleaner {

    private RoadSignDescCleaner() {
    }

    /**
     * This method cleans up the given description by performing several operations on it.
     * It normalizes the string to upper case, removes possible prefixes, extra spaces, unwanted characters,
     * misspellings, and adds spaces where needed.
     *
     * @param strDescription The original description to be cleaned.
     * @return A cleaned version of the original description.
     */
    public static @NotNull String cleanDescription(@NotNull String strDescription) {
        String cleanedDescription = strDescription.toUpperCase().trim();
        cleanedDescription = handleNoParking(removeUnnecessaryCharacters(cleanedDescription)).trim();

        // cleanedDescription = reformatDailyTimeIntervals(cleanedDescription);
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
                .replaceAll("\\s+", " ")
                .replace(".", "")
                .replace("É", "E")
                .replace("È", "E")
                .replace("Ê", "E")
                .replace("À", "A")
                .replace("1ER", "1");
    }

    private static @NotNull String handleNoParking(@NotNull String description) {
        return description
                .replace("(NO PARKING)", "\\P")
                .replaceAll("STAT. INT. (DE)?", "\\P")
                .replace("/P", "\\P")
                .replace("\\P EXCEPTE", "\\P EN TOUT TEMPS EXCEPTE");
    }


    /**
     * This method reformats the time range string in the description.
     * It is used to fix intervals like "SAM 17H A LUN 17H"
     * to something like "17H-23H59 SAM; 00H-23H59 DIM; 00H-17H LUN"
     *
     * @param description The original description.
     * @return The description with the day-hour string reformatted.
     */
    private static String reformatDailyTimeIntervals(String description) {

        String timePattern = "\\d{1,2}\\s*H\\s*(?:\\d{1,2})?"; // UPDATE: use the global variable

        String formattedTimePattern = "(" + String.format(timePattern, "\\s*") + ")";
        String formattedWeeklyDayPattern = "(" + GlobalConfigs.WEEKLY_DAYS_PATTERN + ")";

        Pattern pattern = Pattern.compile(formattedTimePattern + "\\s*" + formattedWeeklyDayPattern
                        + "\\s*(?:AU?)\\s*" + formattedTimePattern + formattedWeeklyDayPattern,
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
        );

        Matcher matcher = pattern.matcher(description);
        StringBuilder sb = new StringBuilder();

        if (matcher.find()) {
            String startTime = matcher.group(1);
            String startDay = matcher.group(2);
            String endTime = matcher.group(3);
            String endDay = matcher.group(4);

            // Convert the days into an ordered list, for example ["SAM", "DIM", "LUN"] for "SAM" to "LUN"
            List<String> days = getDaysInRange(startDay, endDay);

            for (int i = 0; i < days.size(); i++) {
                if (i == 0) {
                    sb.append(startTime).append("-23H59 ").append(days.get(i)).append("; ");
                } else if (i == days.size() - 1) {
                    sb.append("00H-").append(endTime).append(" ").append(days.get(i));
                } else {
                    sb.append("00H-23H59 ").append(days.get(i)).append("; ");
                }
            }
        }

        return sb.toString();
    }

    private static List<String> getDaysInRange(String startDay, String endDay) {
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
     * This method inserts a space between a letter and a number if one does not exist.
     *
     * @param description The original description.
     * @return The description with spaces added between letters and numbers.
     */
    private static @NotNull String insertSpaceBetweenLetterAndNumber(@NotNull String description) {
        Pattern letterNumberPattern = Pattern.compile("(\\p{L})(\\d)");
        Matcher letterNumberMatcher = letterNumberPattern.matcher(description);

        StringBuilder formattedDescription = new StringBuilder();
        while (letterNumberMatcher.find()) {
            letterNumberMatcher.appendReplacement(
                    formattedDescription, letterNumberMatcher.group(1) + " " + letterNumberMatcher.group(2));
        }
        letterNumberMatcher.appendTail(formattedDescription);
        return formattedDescription.toString();
    }

    /**
     * This method adds a space between day and month in the description if necessary.
     *
     * @param description The original description.
     * @return The description with spaces added between day and month.
     */
    private static @NotNull String insertSpaceBetweenDayAndMonth(@NotNull String description) {
        Pattern annualMonthRangePattern = Pattern.compile(
                "\\b((" + GlobalConfigs.TWO_DIGIT + ")(" + GlobalConfigs.ANNUAL_MONTH_PATTERN + "))\\b",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

        Matcher dateMatcher = annualMonthRangePattern.matcher(description);

        StringBuilder formattedDescription = new StringBuilder();
        while (dateMatcher.find()) {
            dateMatcher.appendReplacement(formattedDescription, dateMatcher.group(2) + " " + dateMatcher.group(3));
        }
        dateMatcher.appendTail(formattedDescription);
        return formattedDescription.toString();
    }

}

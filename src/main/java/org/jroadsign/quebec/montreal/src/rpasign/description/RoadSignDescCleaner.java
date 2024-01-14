// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.src.rpasign.description;

import org.jroadsign.quebec.montreal.src.rpasign.description.common.GlobalConfigs;

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
     * @param description The original description to be cleaned.
     * @return A cleaned version of the original description.
     */
    public static String cleanDescription(String description) {
        String cleanedDescription = description.toUpperCase().trim();
        cleanedDescription = removeUnnecessaryCharacters(cleanedDescription);

        //cleanedDescription = reformatDailyTimeIntervals(cleanedDescription);
        cleanedDescription = correctSpelling(cleanedDescription);
        cleanedDescription = insertSpacesWhereNeeded(cleanedDescription);
        return cleanedDescription;
    }

    /**
     * This method removes unnecessary characters from the given description.
     *
     * @param description The original description to be cleaned.
     * @return A cleaned version of the original description.
     */
    private static String removeUnnecessaryCharacters(String description) {
        return description
                .replace("\\P EXCEPTE", "EN TOUT TEMPS EXCEPTE")
                .replace("\\P", "")
                .replace("/P", "")
                .replaceAll("\\s+", " ")
                .replace(".", "")
                .replace("É", "E")
                .replace("È", "E")
                .replace("À", "A")
                .replace("1ER", "1");
    }

    /**
     * This method reformats time range string in the description.
     * It is used to fix intervals like "SAM 17H A LUN 17H".
     *
     * @param description The original description.
     * @return The description with day-hour string reformatted.
     */
    private static String reformatDailyTimeIntervals(String description) {

        // FIXME: intervals like SAM 17H A LUN 17H

        // Define a regex pattern for "DAY HOUR A DAY HOUR"
        String formattedTimePattern = String.format(GlobalConfigs.TIME_PATTERN, "\\\\s*");
        // String formattedWeeklyDayPattern = String.format(GlobalConfigs.TIME_PATTERN, "\\\\s*");
        String dayHourRegex =
                GlobalConfigs.WEEKLY_DAYS_PATTERN + formattedTimePattern
                        + "\\s*A\\s*" + GlobalConfigs.WEEKLY_DAYS_PATTERN + formattedTimePattern +
                        "|" + formattedTimePattern + GlobalConfigs.WEEKLY_DAYS_PATTERN
                        + "\\s*A\\s*" + formattedTimePattern + GlobalConfigs.WEEKLY_DAYS_PATTERN;

        Pattern dayHourPattern = Pattern.compile(
                dayHourRegex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

        Matcher dayHourMatcher = dayHourPattern.matcher(description);
        while (dayHourMatcher.find()) {
            String match = dayHourMatcher.group();

            String[] intervalParts = match.split(" A ");
            // Trim the strings before further processing
            intervalParts[0] = intervalParts[0].trim();
            intervalParts[1] = intervalParts[1].trim();

            // Determine if the match is in the format "DAY HOUR" or "HOUR DAY"
            boolean isDayHourFormat = Character.isLetter(intervalParts[0].charAt(0));

            String[] startParts;
            String[] endParts;

            if (isDayHourFormat) {
                startParts = intervalParts[0].split(" ");
                endParts = intervalParts[1].split(" ");
            } else {
                startParts = new String[]{intervalParts[0].split(" ")[1], intervalParts[0].split(" ")[0]};
                endParts = new String[]{intervalParts[1].split(" ")[1], intervalParts[1].split(" ")[0]};
            }

            String reformattedMatch = startParts[1] + " A 00H " + startParts[0]
                    + " - " + "00H A " + endParts[1] + " " + endParts[0];

            // Replace the original match with the reformatted match in the description
            description = description.replaceFirst(dayHourRegex, reformattedMatch);
        }

        return description;
    }

    /**
     * This method corrects misspelled names in the description.
     *
     * @param description The original description.
     * @return The description with corrected names.
     */
    private static String correctSpelling(String description) {
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
    private static String insertSpacesWhereNeeded(String description) {
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
    private static String insertSpaceBetweenLetterAndNumber(String description) {
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
    private static String insertSpaceBetweenDayAndMonth(String description) {
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

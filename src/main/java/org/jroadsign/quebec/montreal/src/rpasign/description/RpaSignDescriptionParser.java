// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.src.rpasign.description;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * // NOTE: Actually its parssing only the RoadSign of S type
 * This class is used to process a description string containing various types of information
 * (duration, day range, week range, year range, and additional informations).
 * It uses regular expressions to extract this information from the input string.
 */
public class RpaSignDescriptionParser {

    // Define a regex pattern to capture parking duration in minutes
    // This will match patterns like "15 MIN", "100 MIN", etc.
    private static final String DURATION_PATTERN = "\\b\\d+\\s*MIN\\b";
    // Define a regex pattern to capture a time range
    // This will match patterns like "10 H 30 À 12 H 30", "9 H - 10 H", "8 H À 9 H", etc.
    private static final String TIME_PATTERN = "\\s*(\\d{1,2})\\s*H\\s*(\\d{0,2})?\\s*";
    private static final String DAY_TIME_RANGE_PATTERN = "\\b" + TIME_PATTERN + "(-|À|A)" + TIME_PATTERN + "\\b";
    // Define a regex pattern to capture a day range
    // This will match patterns like "LUN AU VEN", "MAR À JEU", "DIM - SAM", etc.
    private static final String WEEKDAY_PATTERN =
            "(LUN(?:DI)?|MAR(?:DI)?|MER(?:CREDI)?|JEU(?:DI)?|VEN(?:DREDI)?|SAM(?:EDI)?|DIM(?:ANCHE)?)";

    private static final String WEEKDAY_EXPRESSION_PATTERN =
            "(JOURS\\s+D'(E|É)COLES?|JOURS\\s+DE?\\s+CLASSE|EN\\s+TOUT\\s+TEMPS)";
    private static final String WEEKLY_DAY_RANGE_PATTERN =
            "\\b(" + WEEKDAY_PATTERN + "\\s*(A(U)?|-|À)\\s*" + WEEKDAY_PATTERN +
                    "|" + WEEKDAY_PATTERN + "(\\s*ET\\s*" + WEEKDAY_PATTERN + ")*" +
                    "|" + WEEKDAY_EXPRESSION_PATTERN + ")\\b";
    // Define a regex pattern to capture a year range
    // This will match patterns like "1 JAN AU 1 DEC", "JAN AU DEC", "1ER FEV - 1ER NOV", etc.
    private static final String MONTH_NAME_PATTERN =
            "(JAN(?:V(?:IER)?)?|F(?:É|E)V(?:RIER)?|MARS|AVR(?:IL)?|MAI|JUIN|" +
                    "JUIL(?:LET)?|AOUT|SEPT(?:EMBRE)?|OCT(?:OBRE)?|NOV(?:EMBRE)?|D(?:É|E)C(?:EMBRE)?)";
    private static final String ANNUAL_MONTH_RANGE_PATTERN =
            "\\b((1ER|\\d{1,2})?\\s*" + MONTH_NAME_PATTERN + "\\s*(A(U)?|ET|-|À)\\s*" + "(1ER|\\d{1,2})?\\s*" + MONTH_NAME_PATTERN
                    + "|" +
                    MONTH_NAME_PATTERN + "\\s*(1ER|\\d{1,2})?\\s*(A(U)?|ET|-|À)\\s*" + MONTH_NAME_PATTERN + "\\s*(1ER|\\d{1,2})?)\\b";
    private static final Map<String, String> weekdayAbbreviations = new HashMap<>();

    static {
        weekdayAbbreviations.put("DIMANCHE", "DIM");
        weekdayAbbreviations.put("LUNDI", "LUN");
        weekdayAbbreviations.put("MARDI", "MAR");
        weekdayAbbreviations.put("MERCREDI", "MER");
        weekdayAbbreviations.put("JEUDI", "JEU");
        weekdayAbbreviations.put("VENDREDI", "VEN");
        weekdayAbbreviations.put("SAMEDI", "SAM");
    }

    private static final Map<String, String> monthAbbreviations = new HashMap<>();
    private static final List<String> LIST_OF_METADATA_TO_IGNORE =
            new ArrayList<>(Arrays.asList("STAT INT", "TEMPS", "NO PARKING", "NON CONFORME"));


    private String durationMinutes;
    private String dailyTimeRange;
    private String weeklyDayRange;
    private String annualMonthRange;
    private String additionalInfo;

    /**
     * Constructor to initialize the class with a given description,
     * It cleans up and processes the description, and then sets the class variables accordingly
     *
     * @param description The description to be parsed
     */
    public RpaSignDescriptionParser(String description) {
        // Clean the description string before processing
        description = cleanDescription(description);
        additionalInfo = description;

        extractInformations(description);
    }

    static {
        monthAbbreviations.put("JAN(?:V(?:IER)?)?", "JAN");
        monthAbbreviations.put("F(?:É|E)V(?:RIER)?", "FEV");
        // monthAbbreviations.put("MARS", "MAR");
        monthAbbreviations.put("AVR(?:IL)?", "AVR");
        // monthAbbreviations.put("JUIN?", "JUN");
        monthAbbreviations.put("JUIL(?:LET)?", "JUIL");
        // monthAbbreviations.put("AOUT", "AOU");
        monthAbbreviations.put("SEPT(?:EMBRE)?", "SEP");
        monthAbbreviations.put("OCT(?:OBRE)?", "OCT");
        monthAbbreviations.put("NOV(?:EMBRE)?", "NOV");
        monthAbbreviations.put("D(?:É|E)C(?:EMBRE)?", "DEC");
    }

    /**
     * Function to extract matches from a description using a given matcher
     * It uses the provided action to handle each match found
     *
     * @param description The description to extract matches from
     * @param matcher     The matcher to find matches
     * @param matchAction The action to be taken for each match found
     * @return The description with the matches removed
     */
    private String extractMatches(String description, Matcher matcher, java.util.function.Consumer<String> matchAction) {
        while (matcher.find()) {
            String match = matcher.group();
            matchAction.accept(match);
            description = description.replaceFirst(Pattern.quote(match), "");
        }

        return description;
    }

    /**
     * Function to append a match to a given string
     * It handles the case where the string might be null
     *
     * @param str   The string to append to
     * @param match The match to append
     * @return The string with the match appended
     */
    private String appendToExisting(String str, String match) {
        return (str == null ? "" : str + "; ") + match.trim();
    }


    // Getters for each property
    public String getDurationMinutes() {
        return durationMinutes;
    }

    public String getDailyTimeRange() {
        return dailyTimeRange;
    }

    public String getWeeklyDayRange() {
        return weeklyDayRange;
    }

    public String getAnnualMonthRange() {
        return annualMonthRange;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    /**
     * Function to process the given description
     * It extracts different components from the description using regular expressions
     *
     * @param description The description to be parsed
     */
    private void extractInformations(String description) {
        // Compile regular expressions for pattern matching
        Pattern parkingDurationPattern = Pattern.compile(DURATION_PATTERN, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Pattern dailyTimeRangePattern = Pattern.compile(DAY_TIME_RANGE_PATTERN, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Pattern weeklyDayRangePattern = Pattern.compile(WEEKLY_DAY_RANGE_PATTERN, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Pattern annualMonthRangePattern = Pattern.compile(ANNUAL_MONTH_RANGE_PATTERN, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

        // Create matchers for each pattern
        Matcher parkingDurationMatcher = parkingDurationPattern.matcher(description);
        Matcher dailyTimeRangeMatcher = dailyTimeRangePattern.matcher(description);
        Matcher weeklyDayRangeMatcher = weeklyDayRangePattern.matcher(description);
        Matcher annualMonthRangeMatcher = annualMonthRangePattern.matcher(description);

        // Process matchers and extract the required information
        description = extractMatches(description, parkingDurationMatcher, match -> durationMinutes = appendToExisting(durationMinutes, match));
        description = extractMatches(description, dailyTimeRangeMatcher, match -> dailyTimeRange = appendToExisting(dailyTimeRange, match));
        description = extractMatches(description, weeklyDayRangeMatcher, match -> weeklyDayRange = appendToExisting(weeklyDayRange, match));
        /*description = extractMatches(description, annualMonthRangeMatcher, match -> annualMonthRangeMatcher = appendMatch(annualMonthRange, match));*/
        while (annualMonthRangeMatcher.find()) {
            String match = annualMonthRangeMatcher.group();
            this.annualMonthRange = (this.getAnnualMonthRange() == null ? "" : this.annualMonthRange + "; ") + match;
            description = description.replaceFirst(ANNUAL_MONTH_RANGE_PATTERN, "");
        }

        // Clean all required information
        standardizeDurationMinutes();
        standardizeDailyTimeRange();
        standardizeWeeklyDayRange();
        standardizeAnnualMonthRange();
        cleanAdditionalInfo(description);
    }

    @Override
    public String toString() {
        return "RpaSignDescriptionParser{" +
                "durationMinutes='" + durationMinutes + '\'' +
                ", dailyTimeRange='" + dailyTimeRange + '\'' +
                ", weeklyDayRange='" + weeklyDayRange + '\'' +
                ", annualMonthRange='" + annualMonthRange + '\'' +
                ", additionalInfo='" + additionalInfo + '\'' +
                '}';
    }

    /**
     * This method reformats day-hour string in the description.
     * It is used to fix intervals like "SAM 17H À LUN 17H".
     *
     * @param description The original description.
     * @return The description with day-hour string reformatted.
     */
    private String reformatDailyTimeIntervals(String description) {

        // FIXME fix intervals like SAM 17H À LUN 17H

        // Define a regex pattern for "DAY HOUR À DAY HOUR"
        String dayHourRegex =
                WEEKDAY_PATTERN + TIME_PATTERN + "\\s*À\\s*" + WEEKDAY_PATTERN + TIME_PATTERN +
                        "|" + TIME_PATTERN + WEEKDAY_PATTERN + "\\s*À\\s*" + TIME_PATTERN + WEEKDAY_PATTERN;
        // TODO ensure that the day_pattern_1 is different to the the day_pattern_2
        // TODO if its the same day_pattern avoid repetition in the description

        Pattern dayHourPattern = Pattern.compile(
                dayHourRegex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

        Matcher dayHourMatcher = dayHourPattern.matcher(description);
        while (dayHourMatcher.find()) {
            String match = dayHourMatcher.group();

            String[] intervalParts = match.split(" À | A ");
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

            String reformattedMatch = startParts[1] + " À 00H " + startParts[0] + " - " + "00H À " + endParts[1] + " " + endParts[0];

            // Replace the original match with the reformatted match in the description
            description = description.replaceFirst(dayHourRegex, reformattedMatch);
        }

        return description;
    }

    /**
     * This method corrects misspelled month names in the description.
     *
     * @param description The original description.
     * @return The description with corrected month names.
     */
    private String correctMonthSpelling(String description) {
        return description.replace("AVIL", "AVRIL")
                .replace("AVRILS", "AVRIL")
                .replace("MRS", "MARS")
                .replace("MARSL", "MARS")
                .replace("VEMDREDI", "VENDREDI");
    }

    /**
     * This method inserts a space between a letter and a number if one does not exist.
     *
     * @param description The original description.
     * @return The description with spaces added between letters and numbers.
     */
    private String insertSpaceBetweenLetterAndNumber(String description) {
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
    private String insertSpaceBetweenDayAndMonth(String description) {
        Pattern annualMonthRangePattern = Pattern.compile(
                "\\b((\\d+)" + MONTH_NAME_PATTERN + ")\\b", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

        Matcher dateMatcher = annualMonthRangePattern.matcher(description);

        StringBuilder formattedDescription = new StringBuilder();
        while (dateMatcher.find()) {
            dateMatcher.appendReplacement(formattedDescription, dateMatcher.group(2) + " " + dateMatcher.group(3));
        }
        dateMatcher.appendTail(formattedDescription);
        return formattedDescription.toString();
    }

    /**
     * This method cleans up the description string before processing.
     * It normalizes the string to upper case, removes possible prefixes,
     * extra spaces, unwanted characters, misspellings, and adds spaces
     * between letters and numbers if required.
     *
     * @param description The original description to be cleaned.
     * @return A cleaned version of the original description.
     */
    private String cleanDescription(String description) {
        String cleanedDescription = description.toUpperCase().trim();

        // Remove possible prefixes (for road sign S type only)
        if (cleanedDescription.startsWith("\\P") || cleanedDescription.startsWith("/P")) {
            cleanedDescription = cleanedDescription.substring(2); // REVIEW
        }

        cleanedDescription = cleanedDescription.replaceAll("\\s+", " "); // remove extra spaces

        cleanedDescription = reformatDailyTimeIntervals(cleanedDescription);

        // Remove unwanted characters and misspellings
        cleanedDescription = cleanedDescription.replace(".", "").
                replace(",", "");
        cleanedDescription = correctMonthSpelling(cleanedDescription);

        // Add space between a letter and a number if there isn't one
        cleanedDescription = insertSpaceBetweenLetterAndNumber(cleanedDescription);

        // Add space between day number and month abbreviation if necessary
        cleanedDescription = insertSpaceBetweenDayAndMonth(cleanedDescription);

        return cleanedDescription;
    }

    private void standardizeDurationMinutes() {
        if (durationMinutes != null)
            durationMinutes = durationMinutes.trim().replaceAll("\\s+", " ");
    }

    private void standardizeDailyTimeRange() {
        if (dailyTimeRange == null)
            return;

        dailyTimeRange = dailyTimeRange
                .replaceAll("À", "-")
                .replaceAll("A", "-")
                .replaceAll("\\s+", "");
    }

    private void standardizeWeeklyDayRange() {
        if (weeklyDayRange == null)
            return;

        // Replace full day names with abbreviations
        for (Map.Entry<String, String> entry : weekdayAbbreviations.entrySet()) {
            String abbreviation = entry.getValue().trim();
            // Replace full day names with abbreviations
            weeklyDayRange = weeklyDayRange
                    .replaceAll(entry.getKey(), " " + abbreviation + " ")
                    .replaceAll("(\\b" + abbreviation + ")(AU|À|ET|-)", "$1 $2")
                    .replaceAll("(AU|À|ET|-)(\\b" + abbreviation + ")", "$1 $2");
        }

        // Normalize spacing and handle special cases like ranges and combinations
        weeklyDayRange = weeklyDayRange
                .replaceAll("\\sAU\\s", "-")
                .replaceAll("\\sA\\s", "-")
                .replaceAll("\\sÀ\\s", "-")
                .replaceAll("\\sET\\s", ";")
                .replaceAll("\\s*-\\s*", "-")
                .replaceAll("\\s*;\\s*", ";")
                .replaceAll("É", "E")
                .replaceAll("'", " ").trim()
                .replaceAll("\\s+", "_")
                .replaceAll("\\bJOURS_D_ECOLE\\b", "JOURS_D_ECOLES");
    }

    private String standardizeAnnualMonthRangePattern(String range) {
        String searchPattern = "\\b" + MONTH_NAME_PATTERN + "\\s*(1ER|\\d{1,2})?\\s*(A(U)?|ET|-|À)\\s*("
                + MONTH_NAME_PATTERN + ")\\s*(1ER|\\d{1,2})?\\b";
        Pattern pattern = Pattern.compile(searchPattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(range);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String startMonth = matcher.group(1);
            String startDay = matcher.group(2) != null ? matcher.group(2) : "1";
            String separation = matcher.group(3);
            String endMonth = matcher.group(4) != null ? matcher.group(5) : "1";
            String endDay = matcher.group(5);
            String replacement;
            if (matcher.group(4) != null) {
                replacement = startDay + " " + startMonth + " " + separation + " " + endDay + " " + endMonth;
            } else {
                replacement = startDay + " " + startMonth + " " + separation + " " + endMonth + " " + endDay;
            }
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);

        return sb.toString().trim();
    }

    private void standardizeAnnualMonthRange() {
        if (annualMonthRange == null)
            return;

        // First, standardize the pattern of the date ranges
        annualMonthRange = standardizeAnnualMonthRangePattern(annualMonthRange);

        // Replace full month names with abbreviations
        for (Map.Entry<String, String> entry : monthAbbreviations.entrySet()) {
            String abbreviation = entry.getValue().trim();
            // Replace full day names with abbreviations
            annualMonthRange = annualMonthRange
                    .replaceAll(entry.getKey(), " " + abbreviation + " ")
                    .replaceAll("(\\b" + abbreviation + ")(AU|À|ET|-)", "$1 $2")
                    .replaceAll("(AU|À|ET|-)(\\b" + abbreviation + ")", "$1 $2");
        }

        // Final formatting steps
        annualMonthRange = annualMonthRange
                .replaceAll("1ER", "1")
                .replaceAll("\\sAU\\s", " - ")
                .replaceAll("\\sA\\s", " - ")
                .replaceAll("\\sÀ\\s", " - ")
                .replaceAll("-", " - ")
                .replaceAll("\\sET\\s", " - ") // REVIEW
                .replaceAll("\\s+", " ");
    }

    /**
     * This method cleans additional information in the description.
     * It removes non-letter and non-number characters, extra spaces, and certain keywords.
     *
     * @param description The original description.
     */
    private void cleanAdditionalInfo(String description) {
        String cleanedDescription = description
                .replaceAll("[^\\p{L}\\p{N}\\s]", " ")
                .replaceAll("\\s+", " ")
                .replaceAll("(\\d+)\\s*X\\s*(\\d+)", "$1 X $2")
                .trim();

        cleanedDescription = (cleanedDescription.equalsIgnoreCase("DU")) ? "" : cleanedDescription;
        cleanedDescription = (cleanedDescription.equalsIgnoreCase("ET")) ? "" : cleanedDescription;

        // If the cleanedDescription ends with "DE", remove it
        if (cleanedDescription.toUpperCase().endsWith(" DE") || cleanedDescription.toUpperCase().endsWith(" DU")) {
            cleanedDescription = cleanedDescription.substring(0, cleanedDescription.length() - 3);
        }

        if (LIST_OF_METADATA_TO_IGNORE.contains(cleanedDescription)) cleanedDescription = "";

        additionalInfo = cleanedDescription;
    }
}

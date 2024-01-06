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

    /******************************************** Duration *********************************************
     /* Define a regex pattern to capture parking duration in minutes
     /* This will match patterns like "15 MIN", "100 MIN", etc.
     /**************************************************************************************************/
    private static final String DURATION_PATTERN = "\\b\\d+\\s*MIN\\b";


    /******************************************** Time Range *******************************************
     /* Define a regex pattern to capture a time range
     /* This will match patterns like "10 H 30 A 12 H 30", "9 H - 10 H", "8 H A 9 H", etc.
     /**************************************************************************************************/
    private static final String TIME_PATTERN = "\\s*(\\d{1,2})\\s*H\\s*(\\d{0,2})?\\s*";
    private static final String DAY_TIME_RANGE_PATTERN = "\\b" + TIME_PATTERN + "\\s*(AU?|-)\\s*" + TIME_PATTERN + "\\b";


    /******************************************** Day Range ********************************************
     /* Define a regex pattern to capture a day range
     /* This will match patterns like "LUN AU VEN", "MAR A JEU", "DIM - SAM", etc
     /**************************************************************************************************/
    private static final String WEEKDAY_PATTERN =
            "(LUN(?:DI)?|MAR(?:DI)?|MER(?:CREDI)?|JEU(?:DI)?|VEN(?:DREDI)?|SAM(?:EDI)?|DIM(?:ANCHE)?)";
    private static final String SCHOOL_DAYS_PATTERN = "JOURS\\s+D'ECOLES?";
    private static final String CLASS_DAYS_PATTERN = "JOURS\\s+DE?\\s+CLASSE";
    private static final String ALWAYS_PATTERN = "EN\\s+TOUT\\s+TEMPS(?:\\s+EXCEPTE)?";
    private static final String WEEKDAY_EXPRESSION_PATTERN =
            "(" + SCHOOL_DAYS_PATTERN + "|" + CLASS_DAYS_PATTERN + "|" + ALWAYS_PATTERN + ")";
    private static final String WEEKLY_DAY_RANGE_PATTERN =
            "\\b(" + WEEKDAY_PATTERN + "(\\s*(AU?|-|ET)\\s*" + WEEKDAY_PATTERN + ")*"
                    + "|" + WEEKDAY_EXPRESSION_PATTERN + ")\\b";
    private static final Map<String, String> weekdayAbbreviations = new HashMap<>();
    /******************************************* Year Range *******************************************
     /* Define a regex pattern to capture a year range
     /* This will match patterns like "1 JAN AU 1 DEC", "JAN AU DEC", "1ER FEV - 1ER NOV", etc.
     /*************************************************************************************************/
    private static final String MONTH_NAME_PATTERN =
            "(JAN(?:V(?:IER)?)?|FEV(?:RIER)?|MARS|AVR(?:IL)?|MAI|JUIN|" +
                    "JUIL(?:LET)?|AOUT|SEPT(?:EMBRE)?|OCT(?:OBRE)?|NOV(?:EMBRE)?|DEC(?:EMBRE)?)";
    private static final String ANNUAL_MONTH_RANGE_PATTERN_PRIM =
            "(1ER|\\d{1,2})?\\s*" + MONTH_NAME_PATTERN
                    + "\\s*(A(U)?|ET|-)\\s*"
                    + "(1ER|\\d{1,2})?\\s*" + MONTH_NAME_PATTERN;
    private static final String ANNUAL_MONTH_RANGE_PATTERN_SECOND =
            MONTH_NAME_PATTERN + "\\s*(1ER|\\d{1,2})?"
                    + "\\s*(A(U)?|ET|-)\\s*"
                    + MONTH_NAME_PATTERN + "\\s*(1ER|\\d{1,2})?";
    private static final String ANNUAL_MONTH_RANGE_PATTERN =
            "\\b(" + ANNUAL_MONTH_RANGE_PATTERN_PRIM + "|" + ANNUAL_MONTH_RANGE_PATTERN_SECOND + ")\\b";
    /******************************************* Other variables *******************************************/
    private static final List<String> LIST_OF_METADATA_TO_IGNORE =
            new ArrayList<>(Arrays.asList("STAT INT", "STAT INT DE", "TEMPS", "NO PARKING", "NON CONFORME"));
    private static final Map<String, String> monthAbbreviations = new HashMap<>();

    static {
        weekdayAbbreviations.put("LUNDI", GlobalConfig.MONDAY);
        weekdayAbbreviations.put("MARDI", GlobalConfig.TUESDAY);
        weekdayAbbreviations.put("MERCREDI", GlobalConfig.WEDNESDAY);
        weekdayAbbreviations.put("JEUDI", GlobalConfig.THURSDAY);
        weekdayAbbreviations.put("VENDREDI", GlobalConfig.FRIDAY);
        weekdayAbbreviations.put("SAMEDI", GlobalConfig.SATURDAY);
        weekdayAbbreviations.put("DIMANCHE", GlobalConfig.SUNDAY);
    }

    static {
        monthAbbreviations.put("JAN(?:V(?:IER)?)?", GlobalConfig.JANUARY);
        monthAbbreviations.put("FEV(?:RIER)?", GlobalConfig.FEBRUARY);
        //monthAbbreviations.put("MARS", GlobalConfig.MARCH);
        monthAbbreviations.put("AVR(?:IL)?", GlobalConfig.APRIL);
        //monthAbbreviations.put("MAI", GlobalConfig.MAY);
        //monthAbbreviations.put("JUIN?", GlobalConfig.JUNE);
        monthAbbreviations.put("JUIL(?:LET)?", GlobalConfig.JULY);
        //monthAbbreviations.put("AOUT", GlobalConfig.AUGUST);
        monthAbbreviations.put("SEPT(?:EMBRE)?", GlobalConfig.SEPTEMBER);
        monthAbbreviations.put("OCT(?:OBRE)?", GlobalConfig.OCTOBER);
        monthAbbreviations.put("NOV(?:EMBRE)?", GlobalConfig.NOVEMBER);
        monthAbbreviations.put("DEC(?:EMBRE)?", GlobalConfig.DECEMBER);
    }

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
     * This method reformats time range string in the description.
     * It is used to fix intervals like "SAM 17H A LUN 17H".
     *
     * @param description The original description.
     * @return The description with day-hour string reformatted.
     */
    private String reformatDailyTimeIntervals(String description) {

        // FIXME fix intervals like SAM 17H A LUN 17H

        // Define a regex pattern for "DAY HOUR A DAY HOUR"
        String dayHourRegex =
                WEEKDAY_PATTERN + TIME_PATTERN + "\\s*A\\s*" + WEEKDAY_PATTERN + TIME_PATTERN +
                        "|" + TIME_PATTERN + WEEKDAY_PATTERN + "\\s*A\\s*" + TIME_PATTERN + WEEKDAY_PATTERN;
        // TODO ensure that the day_pattern_1 is different to the the day_pattern_2
        // TODO if its the same day_pattern avoid repetition in the description

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
    private String correctSpelling(String description) {
        return description.replace("AVIL", GlobalConfig.APRIL)
                .replace("AVRILS", GlobalConfig.APRIL)
                .replace("MRS", GlobalConfig.MARCH)
                .replace("MARSL", GlobalConfig.MARCH)
                .replace("VEMDREDI", GlobalConfig.FRIDAY);
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
     * It normalizes the string to upper case, removes possible prefixes, extra spaces, unwanted characters,
     * misspellings, and adds spaces between letters and numbers if required.
     *
     * @param description The original description to be cleaned.
     * @return A cleaned version of the original description.
     */
    private String cleanDescription(String description) {
        String cleanedDescription = description.toUpperCase().trim();

        // Remove possible prefixes (for road sign S type only)
        cleanedDescription = cleanedDescription
                .replace("\\P EXCEPTE", "EN TOUT TEMPS EXCEPTE")
                .replace("\\P", "")
                .replace("/P", "")
                .replaceAll("\\s+", " "); // remove extra spaces


        // Remove unwanted characters and misspellings
        cleanedDescription = cleanedDescription
                .replace(".", "")
                .replace(",", "")
                .replaceAll("É", "E")
                .replaceAll("È", "E")
                .replaceAll("À", "A");

        // Reformat day time range
        cleanedDescription = reformatDailyTimeIntervals(cleanedDescription);

        cleanedDescription = correctSpelling(cleanedDescription);
        // Add space between a letter and a number if there isn't one
        cleanedDescription = insertSpaceBetweenLetterAndNumber(cleanedDescription);
        // Add space between day number and month abbreviation if necessary
        cleanedDescription = insertSpaceBetweenDayAndMonth(cleanedDescription);

        return cleanedDescription;
    }

    /**
     * This method standardizes the duration minutes in the description.
     * It removes leading and trailing spaces from the duration minutes field
     * and replaces multiple spaces with a single space.
     */
    private void standardizeDurationMinutes() {
        if (durationMinutes != null)
            durationMinutes = durationMinutes.trim().replaceAll("\\s+", " ");
    }

    /**
     * This method standardizes the daily time range in the description.
     * It replaces certain expressions with standardized format and removes unnecessary characters and spaces.
     */
    private void standardizeDailyTimeRange() {
        if (dailyTimeRange == null)
            return;

        dailyTimeRange = dailyTimeRange
                .replaceAll("A", "-")
                .replaceAll("\\s+", "")
                .replaceAll("24H", "00H")
                // .replaceAll("H", ":") // TODO
                .trim();
    }


    /**
     * Splits a weekly day range string into individual days or ranges of days.
     *
     * @param weeklyDayRange The weekly day range string to be split.
     * @return The split string, where individual days or ranges of days are separated by semicolons.
     */
    private String standardizeWeeklyDayRangePattern(String weeklyDayRange) {
        String patternString =
                "\\b" + WEEKDAY_PATTERN + "\\s*-\\s*" + WEEKDAY_PATTERN + "(\\s*-\\s*" + WEEKDAY_PATTERN + ")+\\b";
        Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(weeklyDayRange);
        StringBuilder rangeString = new StringBuilder();
        if (matcher.find()) {
            String[] days = weeklyDayRange.split("\\s*-\\s*");
            for (int i = 0; i < days.length - 1; i++) {
                rangeString.append(days[i]).append("-").append(days[i + 1]);
                if (i < days.length - 2) {
                    rangeString.append(";");
                }
            }
            return rangeString.toString();
        }
        return weeklyDayRange;
    }

    /**
     * Standardizes the weekly day range in the description.
     * It replaces full day names with abbreviations and performs normalization and formatting steps.
     */
    private void standardizeWeeklyDayRange() {
        if (weeklyDayRange == null)
            return;

        // Replace full day names with abbreviations
        for (Map.Entry<String, String> entry : weekdayAbbreviations.entrySet()) {
            String abbreviation = entry.getValue().trim();
            // Replace full day names with abbreviations
            weeklyDayRange = weeklyDayRange
                    .replaceAll(entry.getKey(), " " + abbreviation + " ")
                    .replaceAll("(\\b" + abbreviation + ")(AU|ET|-)", "$1 $2")
                    .replaceAll("(AU|ET|-)(\\b" + abbreviation + ")", "$1 $2")
                    .trim();
        }

        // Normalize spacing and handle special cases like ranges and combinations
        weeklyDayRange = standardizeWeeklyDayRangePattern(weeklyDayRange);
        weeklyDayRange = standardizeWeeklyDayRangePattern(weeklyDayRange)
                .replaceAll("\\sAU\\s", "-")
                .replaceAll("\\sA\\s", "-")
                .replaceAll("\\sET\\s", ";")
                .replaceAll("\\s*-\\s*", "-")
                .replaceAll("\\s*;\\s*", ";")
                .replaceAll("'", " ")
                .replaceAll("\\s+", "_")
                .replaceAll("\\bJOURS_D_ECOLE\\b", "JOURS_D_ECOLES")
                .trim();
    }

    /**
     * Standardizes the annual month range pattern in a given string.
     * Replaces full month names with abbreviations and performs final formatting steps.
     *
     * @param range The string containing the annual month range pattern to be standardized
     * @return The string with standardized annual month range pattern
     */
    private String standardizeAnnualMonthRangePattern(String range) {
        Pattern pattern = Pattern.compile(
                ANNUAL_MONTH_RANGE_PATTERN_SECOND, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(range);
        StringBuilder sb = new StringBuilder();

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


    /**
     * This method standardizes the annual month range in the description.
     * It replaces full month names with abbreviations and performs final formatting steps.
     */
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
                    .replaceAll("(\\b" + abbreviation + ")(AU|A|ET|-)", "$1 $2")
                    .replaceAll("(AU|A|ET|-)(\\b" + abbreviation + ")", "$1 $2");
        }

        // Final formatting steps
        annualMonthRange = annualMonthRange
                .replaceAll("1ER", "1")
                .replaceAll("\\sAU\\s", " - ")
                .replaceAll("\\sA\\s", " - ")
                .replaceAll("-", " - ")
                .replaceAll("\\sET\\s", " - ") // REVIEW
                .replaceAll("\\s*;\\s*", ";")
                .replaceAll("\\s+", " ")
                .trim();
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
                // If the cleanedDescription is "DE", "DU" or "ET" remove it
                .replaceAll("^\\s*DE\\s*$", "")
                .replaceAll("\\s+DE\\s*$", "")
                .replaceAll("^\\s*DU\\s*$", "")
                .replaceAll("^\\s+ET\\s*$", "").trim();

        if (LIST_OF_METADATA_TO_IGNORE.contains(cleanedDescription) || cleanedDescription.isEmpty())
            additionalInfo = null;
        else
            additionalInfo = cleanedDescription;
    }
}

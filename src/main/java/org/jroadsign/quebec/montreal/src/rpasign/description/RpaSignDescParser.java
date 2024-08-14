package org.jroadsign.quebec.montreal.src.rpasign.description;

import org.jetbrains.annotations.NotNull;
import org.jroadsign.quebec.montreal.src.rpasign.description.common.GlobalConfigs;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * // NOTE: Actually its parssing only the RoadSign of S type
 * This class is used to process a description string containing various types of information
 * (duration, day range, week range, year range, and additional informations).
 * It uses regular expressions to extract this information from the input string.
 */
public class RpaSignDescParser {

    private final boolean parkingAuthorized;
    private String durationMinutes;
    private String dailyTimeRange;
    private String weeklyDayRange;
    private String annualMonthRange;
    private final String additionalInfo;

    /**
     * Constructor to initialize the class with a given description,
     * It cleans up and processes the description, and then sets the class variables accordingly
     *
     * @param description The description to be parsed
     */
    public RpaSignDescParser(String description) {
        parkingAuthorized = !description.trim().startsWith("\\P");
        description = description.replace("\\P", "").trim();

        additionalInfo = cleanAdditionalInfo(extractInformations(description));
    }

    /**
     * Function to process the given description
     * It extracts different components from the description using regular expressions
     *
     * @param description The description to be parsed
     */
    private String extractInformations(String description) {
        // Compile regular expressions for pattern matching
        Pattern parkingDurationPattern = Pattern.compile(
                // TODO : add (?:MAX(?:IMUM))? befor and after
                "\\b(" + String.format(GlobalConfigs.DURATION_PATTERN, "\\s*") + ")\\b",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

        Pattern dailyTimeRangePattern = Pattern.compile(
                "\\b(" + String.format(GlobalConfigs.DAY_TIME_RANGE_PATTERN, "\\s*", "\\s*(AU?|-)\\s*") + ")\\b",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

        Pattern weeklyDayRangePattern = Pattern.compile(
                "\\b(" + GlobalConfigs.WEEKLY_DAYS_RANGE_EXPRESSION_PATTERN + ")\\b",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

        Pattern annualMonthRangePattern = Pattern.compile(
                "\\b(" + GlobalConfigs.ANNUAL_MONTH_RANGE_PATTERN + ")\\b",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

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
            description = description.replaceFirst(GlobalConfigs.ANNUAL_MONTH_RANGE_PATTERN, "");
        }

        // Clean all required information
        standardizeDurationMinutes();
        standardizeDailyTimeRange();
        standardizeWeeklyDayRange();
        standardizeAnnualMonthRange();

        return description;
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
    public boolean isParkingAuthorized() {
        return parkingAuthorized;
    }

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

    @Override
    public String toString() {
        return "RpaSignDescParser{" +
                "parkingAuthorized=" + parkingAuthorized +
                ", durationMinutes='" + durationMinutes + '\'' +
                ", dailyTimeRange='" + dailyTimeRange + '\'' +
                ", weeklyDayRange='" + weeklyDayRange + '\'' +
                ", annualMonthRange='" + annualMonthRange + '\'' +
                ", additionalInfo='" + additionalInfo + '\'' +
                '}';
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
                .replaceAll("24H(\\d{1,2})", "00H$1")
                .replaceAll("\\b24H\\b", "00H")
                .replaceAll("\\b-00?H(00)?\\b", "-23H59")
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
                "\\b(" + GlobalConfigs.WEEKLY_DAYS_PATTERN + ")\\s*-\\s*(" + GlobalConfigs.WEEKLY_DAYS_PATTERN
                        + ")(\\s*-\\s*(" + GlobalConfigs.WEEKLY_DAYS_PATTERN + "))+\\b";
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
        for (Map.Entry<String, String> entry : GlobalConfigs.DAY_OF_WEEK_PATTERN_MAP.entrySet()) {
            String abbreviation = entry.getValue().trim();
            // Replace full day names with abbreviations
            weeklyDayRange = weeklyDayRange
                    //.replaceAll(entry.getKey(), " " + abbreviation + " ") // already done in cleaning
                    .replaceAll("(\\b" + abbreviation + ")(AU|ET|-)", "$1 $2")
                    .replaceAll("(AU|ET|-)(\\b" + abbreviation + ")", "$1 $2")
                    .trim();
        }

        // Normalize spacing and handle special cases like ranges and combinations
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
    private @NotNull String standardizeAnnualMonthRangePattern(String range) {
        Pattern pattern = Pattern.compile(
                GlobalConfigs.ANNUAL_MONTH_RANGE_PATTERN_SECOND, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(range);
        StringBuilder sb = new StringBuilder();

        if (matcher.find()) {
            String startMonth = matcher.group(1);
            String startDay = matcher.group(2) != null ? matcher.group(2) : "1";
            String endMonth = matcher.group(3);
            String endDay = matcher.group(4) != null ? matcher.group(4) : "1";
            String replacement = startDay + " " + startMonth + " - " + endDay + " " + endMonth;
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
        for (Map.Entry<String, String> entry : GlobalConfigs.ANNUAL_MONTH_ABBREVIATIONS_MAP.entrySet()) {
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
    private String cleanAdditionalInfo(@NotNull String description) {
        String cleanedDescription = description
                .replaceAll("[^\\p{L}\\p{N}\\s]", " ")
                .replaceAll("\\s+", " ")
                .replaceAll("(\\d+)\\s*X\\s*(\\d+)", "$1 X $2")
                .replaceAll("\\s+DE\\s*$", "")
                .replaceAll("\\s+DU\\s*$", "")
                .replaceAll("^\\s*DE\\s+", "")
                .replaceAll("^\\s*DU\\s+", "")
                .replaceAll("^\\s*ET\\s+", "")
                .replaceAll("\\s*MAX\\s*$", "")
                .trim();

        if (GlobalConfigs.LIST_OF_METADATA_TO_IGNORE.contains(cleanedDescription) || cleanedDescription.isEmpty())
            return null;
        else
            return cleanedDescription;
    }
}

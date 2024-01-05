// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.src.rpasign.description;

import java.time.LocalTime;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RpaSignDescription {
    String additionalMetaData;
    private String stringDescription;
    private List<DurationMinutes> durationMinutesList;
    private List<DailyTimeRange> dailyTimeRangeList;
    private WeeklyDays weeklyDays; // REVIEW
    private List<AnnualMonthRange> annualMonthRangeList;


    public RpaSignDescription(String sDescription) {
        stringDescription = sDescription;
        RpaSignDescriptionParser rpaSignDescriptionParser = new RpaSignDescriptionParser(sDescription);

        if (rpaSignDescriptionParser.getDurationMinutes() != null) {
            durationMinutesList = new ArrayList<>();
            String[] tabDurationsMinutes = rpaSignDescriptionParser.getDurationMinutes().split(";");
            for (String element : tabDurationsMinutes)
                durationMinutesList.add(new DurationMinutes(element));
        }
        if (rpaSignDescriptionParser.getDailyTimeRange() != null) {
            dailyTimeRangeList = new ArrayList<>();
            String[] tabDailyTimeRanges = rpaSignDescriptionParser.getDailyTimeRange().split(";");
            for (String element : tabDailyTimeRanges) {
                try {
                    dailyTimeRangeList.add(new DailyTimeRange(element));
                } catch (StartAfterEndException e1) {
                    if (e1.getRange() != null && e1.getRange() instanceof Range<?>) {
                        Range<LocalTime> lastRange = (Range<LocalTime>) e1.getRange();
                        Range<LocalTime> newRange1 = new Range<>(LocalTime.of(0, 0), lastRange.getEnd());
                        Range<LocalTime> newRange2 = new Range<>(lastRange.getStart(), LocalTime.of(23, 59));
                        try {
                            dailyTimeRangeList.add(new DailyTimeRange(newRange1));
                            dailyTimeRangeList.add(new DailyTimeRange(newRange2));
                        } catch (StartAfterEndException e2) {
                            throw new IllegalArgumentException(
                                    String.format(DailyTimeRange.MSG_ERR_INVALID_FORMAT_S_ARG, element));
                        }
                    } else {
                        throw new IllegalArgumentException(
                                String.format(DailyTimeRange.MSG_ERR_INVALID_FORMAT_S_ARG, element));
                    }

                }
            }
        }
        if (rpaSignDescriptionParser.getWeeklyDayRange() != null) {
            weeklyDays = new WeeklyDays(rpaSignDescriptionParser.getWeeklyDayRange());
        }
        if (rpaSignDescriptionParser.getAnnualMonthRange() != null) {
            annualMonthRangeList = new ArrayList<>();
            String[] tabAnnualMonthRanges = rpaSignDescriptionParser.getAnnualMonthRange().split(";");
            for (String element : tabAnnualMonthRanges) {
                try {
                    annualMonthRangeList.add(new AnnualMonthRange(element));
                } catch (StartAfterEndException e1) {
                    if (e1.getRange() != null && e1.getRange() instanceof Range<?>) {
                        Range<MonthDay> lastRange = (Range<MonthDay>) e1.getRange();
                        Range<MonthDay> newRange1 = new Range<>(MonthDay.of(1, 1), lastRange.getEnd());
                        Range<MonthDay> newRange2 = new Range<>(lastRange.getStart(), MonthDay.of(12, 31));
                        try {
                            annualMonthRangeList.add(new AnnualMonthRange(newRange1));
                            annualMonthRangeList.add(new AnnualMonthRange(newRange2));
                        } catch (StartAfterEndException e2) {
                            throw new IllegalArgumentException(
                                    String.format(AnnualMonthRange.MSG_ERR_INVALID_FORMAT_S_ARG, element));
                        }
                    } else {
                        throw new IllegalArgumentException(
                                String.format(AnnualMonthRange.MSG_ERR_INVALID_FORMAT_S_ARG, element));
                    }
                }
            }
        }
        if (rpaSignDescriptionParser.getAdditionalInfo() != null) {
            additionalMetaData = rpaSignDescriptionParser.getAdditionalInfo();
        }
    }

    public RpaSignDescription(List<DurationMinutes> durationMinutesList,
                              List<DailyTimeRange> dailyTimeRangeList,
                              WeeklyDays weeklyDays,
                              List<AnnualMonthRange> annualMonthRangeList,
                              String additionalMetaData) {
        this.durationMinutesList = durationMinutesList;
        this.dailyTimeRangeList = dailyTimeRangeList;
        this.weeklyDays = weeklyDays;
        this.annualMonthRangeList = annualMonthRangeList;
        this.additionalMetaData = additionalMetaData;
    }

    public String getStringDescription() {
        return stringDescription;
    }

    public List<DurationMinutes> getDurationMinutesList() {
        return durationMinutesList != null ? durationMinutesList : Collections.emptyList();
    }

    public List<DailyTimeRange> getDailyTimeRangeList() {
        return dailyTimeRangeList != null ? dailyTimeRangeList : Collections.emptyList();
    }

    public WeeklyDays getWeeklyDays() {
        return weeklyDays;
    }

    public List<AnnualMonthRange> getAnnualMonthRangeList() {
        return annualMonthRangeList != null ? annualMonthRangeList : Collections.emptyList();
    }

    public String getAdditionalMetaData() {
        return additionalMetaData;
    }

    @Override
    public String toString() {
        return "RpaSignDescription{" +
                "durationMinutesList=" + durationMinutesList +
                ", dailyTimeRangeList=" + dailyTimeRangeList +
                ", weeklyDays=" + weeklyDays +
                ", annualMonthRange=" + annualMonthRangeList +
                ", additionalMetaData='" + additionalMetaData + '\'' +
                '}';
    }
}

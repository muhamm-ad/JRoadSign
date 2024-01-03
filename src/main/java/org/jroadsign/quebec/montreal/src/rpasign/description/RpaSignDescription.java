// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.src.rpasign.description;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RpaSignDescription {
    String additionalMetaData;
    private String stringDescription;
    private List<DurationMinutes> durationMinutesList;
    private List<DailyTimeRange> dailyTimeRangeList;
    private WeeklyDays weeklyDays; // REVIEW
    private List<AnnualMonthRange> annualMonthRange;


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
            for (String element : tabDailyTimeRanges)
                dailyTimeRangeList.add(new DailyTimeRange(element));
        }
        if (rpaSignDescriptionParser.getWeeklyDayRange() != null) {
            weeklyDays = new WeeklyDays(rpaSignDescriptionParser.getWeeklyDayRange());
        }
        if (rpaSignDescriptionParser.getAnnualMonthRange() != null) {
            annualMonthRange = new ArrayList<>();
            String[] tabAnnualMonthRanges = rpaSignDescriptionParser.getAnnualMonthRange().split(";");
            for (String element : tabAnnualMonthRanges)
                annualMonthRange.add(new AnnualMonthRange(element));
        }
        if (rpaSignDescriptionParser.getAdditionalInfo() != null) {
            additionalMetaData = rpaSignDescriptionParser.getAdditionalInfo();
        }
    }

    public RpaSignDescription(List<DurationMinutes> durationMinutesList,
                              List<DailyTimeRange> dailyTimeRangeList,
                              WeeklyDays weeklyDays,
                              List<AnnualMonthRange> annualMonthRange,
                              String additionalMetaData) {
        this.durationMinutesList = durationMinutesList;
        this.dailyTimeRangeList = dailyTimeRangeList;
        this.weeklyDays = weeklyDays;
        this.annualMonthRange = annualMonthRange;
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

    public List<AnnualMonthRange> getAnnualMonthRange() {
        return annualMonthRange != null ? annualMonthRange : Collections.emptyList();
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
                ", annualMonthRange=" + annualMonthRange +
                ", additionalMetaData='" + additionalMetaData + '\'' +
                '}';
    }
}

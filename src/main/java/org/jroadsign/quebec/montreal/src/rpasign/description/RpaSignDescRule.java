// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.src.rpasign.description;

import org.jroadsign.quebec.montreal.src.rpasign.description.exceptions.StartAfterEndException;

import java.time.LocalTime;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RpaSignDescRule {
    private List<DurationMinutes> durationMinutesList;
    private List<DailyTimeRange> dailyTimeRangeList;
    private WeeklyDays weeklyDays;  // REVIEW
    private List<AnnualMonthRange> annualMonthRangeList;
    private String ruleAdditionalMetaData;

    public RpaSignDescRule(List<DurationMinutes> durationMinutesList,
                           List<DailyTimeRange> dailyTimeRangeList,
                           WeeklyDays weeklyDays,
                           List<AnnualMonthRange> annualMonthRangeList,
                           String additionalMetaData) {
        this.durationMinutesList = durationMinutesList;
        this.dailyTimeRangeList = dailyTimeRangeList;
        this.weeklyDays = weeklyDays;
        this.annualMonthRangeList = annualMonthRangeList;
        this.ruleAdditionalMetaData = additionalMetaData;
    }

    public RpaSignDescRule(String sDescription) {
        RpaSignDescParser rpaSignDescParser = new RpaSignDescParser(sDescription);
        initDurationMinutesList(rpaSignDescParser);
        initDailyTimeRangeList(rpaSignDescParser);
        initWeeklyDays(rpaSignDescParser);
        initAnnualMonthRangeList(rpaSignDescParser);
        if (rpaSignDescParser.getAdditionalInfo() != null) {
            ruleAdditionalMetaData = rpaSignDescParser.getAdditionalInfo();
        }
    }

    private void initDurationMinutesList(RpaSignDescParser rpaSignDescParser) {
        if (rpaSignDescParser.getDurationMinutes() != null) {
            durationMinutesList = new ArrayList<>();
            String[] tabDurationsMinutes = rpaSignDescParser.getDurationMinutes().split(";");
            for (String element : tabDurationsMinutes)
                durationMinutesList.add(new DurationMinutes(element));
        }
    }

    private void initDailyTimeRangeList(RpaSignDescParser rpaSignDescParser) {
        if (rpaSignDescParser.getDailyTimeRange() != null) {
            dailyTimeRangeList = new ArrayList<>();
            String[] tabDailyTimeRanges = rpaSignDescParser.getDailyTimeRange().split(";");
            for (String element : tabDailyTimeRanges) {
                try {
                    dailyTimeRangeList.add(new DailyTimeRange(element));
                } catch (StartAfterEndException e1) {
                    if (e1.getRange() != null && e1.getRange() instanceof Range<?>) {
                        Range<LocalTime> lastRange = (Range<LocalTime>) e1.getRange();
                        Range<LocalTime> newRange1 = new Range<>(lastRange.getStart(), LocalTime.of(23, 59));
                        Range<LocalTime> newRange2 = new Range<>(LocalTime.of(0, 0), lastRange.getEnd());
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
    }

    private void initWeeklyDays(RpaSignDescParser rpaSignDescParser) {
        if (rpaSignDescParser.getWeeklyDayRange() != null) {
            weeklyDays = new WeeklyDays(rpaSignDescParser.getWeeklyDayRange());
        }
    }

    private void initAnnualMonthRangeList(RpaSignDescParser rpaSignDescParser) {
        if (rpaSignDescParser.getAnnualMonthRange() != null) {
            annualMonthRangeList = new ArrayList<>();
            String[] tabAnnualMonthRanges = rpaSignDescParser.getAnnualMonthRange().split(";");
            for (String element : tabAnnualMonthRanges) {
                try {
                    annualMonthRangeList.add(new AnnualMonthRange(element));
                } catch (StartAfterEndException e1) {
                    if (e1.getRange() != null && e1.getRange() instanceof Range<?>) {
                        Range<MonthDay> lastRange = (Range<MonthDay>) e1.getRange();
                        Range<MonthDay> newRange1 = new Range<>(lastRange.getStart(), MonthDay.of(12, 31));
                        Range<MonthDay> newRange2 = new Range<>(MonthDay.of(1, 1), lastRange.getEnd());
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

    public String getRuleAdditionalMetaData() {
        return ruleAdditionalMetaData;
    }

    public List<AnnualMonthRange> getAnnualMonthRangeList() {
        return annualMonthRangeList != null ? annualMonthRangeList : Collections.emptyList();
    }

    @Override
    public String toString() {
        return "RpaSignDescRule{" +
                "durationMinutesList=" + durationMinutesList +
                ", dailyTimeRangeList=" + dailyTimeRangeList +
                ", weeklyDays=" + weeklyDays +
                ", annualMonthRangeList=" + annualMonthRangeList +
                ", ruleAdditionalMetaData='" + ruleAdditionalMetaData + '\'' +
                '}';
    }
}
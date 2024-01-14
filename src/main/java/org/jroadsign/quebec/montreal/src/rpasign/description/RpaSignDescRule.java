// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.src.rpasign.description;

import org.jetbrains.annotations.NotNull;
import org.jroadsign.quebec.montreal.src.rpasign.description.exceptions.StartAfterEndException;
import org.jroadsign.quebec.montreal.src.rpasign.description.exceptions.WeeklyRangeExpException;

import java.time.LocalTime;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RpaSignDescRule {

    private static final MonthDay START_OF_MONTH_DAY = MonthDay.of(1, 1);
    private static final MonthDay END_OF_MONTH_DAY = MonthDay.of(12, 31);
    private static final LocalTime START_OF_DAY_HOUR = LocalTime.of(0, 0);
    private static final LocalTime END_OF_DAY_HOUR = LocalTime.of(23, 59);


    private List<DurationMinutes> durationMinutesList;
    private List<DailyTimeRange> dailyTimeRangeList;
    private WeeklyDays weeklyDays;
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

    private void initDurationMinutesList(@NotNull RpaSignDescParser rpaSignDescParser) {
        if (rpaSignDescParser.getDurationMinutes() != null) {
            durationMinutesList = new ArrayList<>();
            String[] tabDurationsMinutes = rpaSignDescParser.getDurationMinutes().split(";");
            for (String element : tabDurationsMinutes)
                durationMinutesList.add(new DurationMinutes(element));
        }
    }

    private void initDailyTimeRangeList(@NotNull RpaSignDescParser rpaSignDescParser) {
        if (rpaSignDescParser.getDailyTimeRange() != null) {
            dailyTimeRangeList = new ArrayList<>();
            processDailyTimeRangeList(
                    rpaSignDescParser.getDailyTimeRange().split(";")
            );
        }
    }

    private void processDailyTimeRangeList(String @NotNull [] split) {
        for (String element : split) {
            try {
                dailyTimeRangeList.add(new DailyTimeRange(element));
            } catch (StartAfterEndException e1) {
                processDailyTimeRangeListException(e1, element);
            }
        }
    }

    private void processDailyTimeRangeListException(@NotNull StartAfterEndException e1, String element) {
        if (hasValidRange(e1)) {
            try {
                Range<LocalTime> pastRange = (Range<LocalTime>) e1.getRange();
                addNewRangesToDailyTimeRangeList(pastRange);
            } catch (StartAfterEndException e2) {
                throwInvalidFormatArg(DailyTimeRange.MSG_ERR_INVALID_FORMAT_S_ARG, element);
            }
        } else {
            throwInvalidFormatArg(DailyTimeRange.MSG_ERR_INVALID_FORMAT_S_ARG, element);
        }
    }

    private void addNewRangesToDailyTimeRangeList(@NotNull Range<LocalTime> pastRange)
            throws StartAfterEndException {
        Range<LocalTime> newRange1 = new Range<>(pastRange.getStart(), END_OF_DAY_HOUR);
        Range<LocalTime> newRange2 = new Range<>(START_OF_DAY_HOUR, pastRange.getEnd());
        dailyTimeRangeList.add(new DailyTimeRange(newRange1));
        dailyTimeRangeList.add(new DailyTimeRange(newRange2));
    }


    private void initWeeklyDays(@NotNull RpaSignDescParser rpaSignDescParser) {
        try {
            if (rpaSignDescParser.getWeeklyDayRange() != null) {
                weeklyDays = new WeeklyDays(rpaSignDescParser.getWeeklyDayRange());
            }
        } catch (WeeklyRangeExpException e1) {
            processWeeklyDaysException(e1);
        }
    }

    private void processWeeklyDaysException(@NotNull WeeklyRangeExpException e1) {
        weeklyDays = e1.getWeeklyDays();
        if (Objects.requireNonNull(e1.getExpression()) == WeekRangeExpression.ALL_TIMES_EXCEPT) {
            List<DailyTimeRange> pastDailyTimeRangeList = new ArrayList<>(dailyTimeRangeList);
            dailyTimeRangeList.clear();

            for (DailyTimeRange pastRange : pastDailyTimeRangeList) {
                // REVIEW : check repetition
                Range<LocalTime> newRange1 = new Range<>(LocalTime.of(0, 0), pastRange.getStart());
                Range<LocalTime> newRange2 = new Range<>(pastRange.getEnd(), LocalTime.of(23, 59));
                try {
                    dailyTimeRangeList.add(new DailyTimeRange(newRange1));
                    dailyTimeRangeList.add(new DailyTimeRange(newRange2));
                } catch (StartAfterEndException e2) {
                    throwInvalidFormatArg(DailyTimeRange.MSG_ERR_INVALID_FORMAT_S_ARG,
                            newRange1 + " and/or " + newRange2);
                }
            }
        }
    }


    private void initAnnualMonthRangeList(@NotNull RpaSignDescParser rpaSignDescParser) {
        if (rpaSignDescParser.getAnnualMonthRange() != null) {
            annualMonthRangeList = new ArrayList<>();
            processAnnualMonthRangeList(
                    rpaSignDescParser.getAnnualMonthRange().split(";")
            );
        }
    }

    private void processAnnualMonthRangeList(String @NotNull [] tabAnnualMonthRanges) {
        for (String element : tabAnnualMonthRanges) {
            try {
                annualMonthRangeList.add(new AnnualMonthRange(element));
            } catch (StartAfterEndException e1) {
                processAnnualMonthRangeListException(e1, element);
            }
        }
    }

    private void processAnnualMonthRangeListException(@NotNull StartAfterEndException e1, String element) {
        if (hasValidRange(e1)) {
            try {
                Range<MonthDay> pastRange = (Range<MonthDay>) e1.getRange();
                addNewRangesToAnnualMonthRangeList(pastRange);
            } catch (StartAfterEndException e2) {
                throwInvalidFormatArg(AnnualMonthRange.MSG_ERR_INVALID_FORMAT_S_ARG, element);
            }
        } else {
            throwInvalidFormatArg(AnnualMonthRange.MSG_ERR_INVALID_FORMAT_S_ARG, element);
        }
    }

    private void addNewRangesToAnnualMonthRangeList(@NotNull Range<MonthDay> pastRange)
            throws StartAfterEndException {
        Range<MonthDay> newRange1 = new Range<>(pastRange.getStart(), END_OF_MONTH_DAY);
        Range<MonthDay> newRange2 = new Range<>(START_OF_MONTH_DAY, pastRange.getEnd());
        annualMonthRangeList.add(new AnnualMonthRange(newRange1));
        annualMonthRangeList.add(new AnnualMonthRange(newRange2));
    }

    private boolean hasValidRange(@NotNull StartAfterEndException exception) {
        return exception.getRange() != null && exception.getRange() instanceof Range;
    }

    private static void throwInvalidFormatArg(String msgErrInvalidFormatSArg, String element) {
        throw new IllegalArgumentException(
                String.format(msgErrInvalidFormatSArg, element));
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
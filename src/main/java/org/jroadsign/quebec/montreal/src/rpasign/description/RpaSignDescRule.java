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

    private List<DurationMinutes> listDurationMinutes;
    private List<DailyTimeRange> listDailyTimeRange;
    private WeeklyDays weeklyDays;
    private List<AnnualMonthRange> listAnnualMonthRange;
    private String additionalMetaData;

    public RpaSignDescRule(boolean park,
                           List<DurationMinutes> listDurationMinutes,
                           List<DailyTimeRange> listDailyTimeRange,
                           WeeklyDays weeklyDays,
                           List<AnnualMonthRange> listAnnualMonthRange,
                           String additionalMetaData) {
        this.listDurationMinutes = listDurationMinutes;
        this.listDailyTimeRange = listDailyTimeRange;
        this.weeklyDays = weeklyDays;
        this.listAnnualMonthRange = listAnnualMonthRange;
        this.additionalMetaData = additionalMetaData;
    }

    public RpaSignDescRule(String sDescription) {
        RpaSignDescParser rpaSignDescParser = new RpaSignDescParser(sDescription);
        initDurationMinutesList(rpaSignDescParser);
        initDailyTimeRangeList(rpaSignDescParser);
        initWeeklyDays(rpaSignDescParser);
        initAnnualMonthRangeList(rpaSignDescParser);
        if (rpaSignDescParser.getAdditionalInfo() != null) {
            additionalMetaData = rpaSignDescParser.getAdditionalInfo();
        }
    }

    private void initDurationMinutesList(@NotNull RpaSignDescParser rpaSignDescParser) {
        if (rpaSignDescParser.getDurationMinutes() != null) {
            listDurationMinutes = new ArrayList<>();
            String[] tabDurationsMinutes = rpaSignDescParser.getDurationMinutes().split(";");
            for (String element : tabDurationsMinutes)
                listDurationMinutes.add(new DurationMinutes(element));
        }
    }

    private void initDailyTimeRangeList(@NotNull RpaSignDescParser rpaSignDescParser) {
        if (rpaSignDescParser.getDailyTimeRange() != null) {
            listDailyTimeRange = new ArrayList<>();

            for (String s : rpaSignDescParser.getDailyTimeRange().split(";")) {
                try {
                    listDailyTimeRange.add(new DailyTimeRange(s));
                } catch (StartAfterEndException e1) {
                    if (hasRange(e1)) {
                        try {
                            Range<LocalTime> pastRange = (Range<LocalTime>) e1.getRange();
                            Range<LocalTime> newRange1 = new Range<>(pastRange.getStart(), END_OF_DAY_HOUR);
                            Range<LocalTime> newRange2 = new Range<>(START_OF_DAY_HOUR, pastRange.getEnd());
                            listDailyTimeRange.add(new DailyTimeRange(newRange1));
                            listDailyTimeRange.add(new DailyTimeRange(newRange2));
                        } catch (StartAfterEndException e2) {
                            throwInvalidFormatArg(DailyTimeRange.MSG_ERR_INVALID_FORMAT_S_ARG, s);
                        }
                    } else {
                        throwInvalidFormatArg(DailyTimeRange.MSG_ERR_INVALID_FORMAT_S_ARG, s);
                    }
                }
            }
        }
    }

    private void initWeeklyDays(@NotNull RpaSignDescParser rpaSignDescParser) {
        try {
            if (rpaSignDescParser.getWeeklyDayRange() != null) {
                weeklyDays = new WeeklyDays(rpaSignDescParser.getWeeklyDayRange());
            }
        } catch (WeeklyRangeExpException e1) {
            weeklyDays = e1.getWeeklyDays();
            if (Objects.requireNonNull(e1.getExpression()) == WeekRangeExpression.ALL_TIMES_EXCEPT) {
                List<DailyTimeRange> pastDailyTimeRangeList = new ArrayList<>(listDailyTimeRange);
                listDailyTimeRange.clear();

                for (DailyTimeRange pastRange : pastDailyTimeRangeList) {
                    // REVIEW : check repetition
                    Range<LocalTime> newRange1 = new Range<>(LocalTime.of(0, 0), pastRange.getStart());
                    Range<LocalTime> newRange2 = new Range<>(pastRange.getEnd(), LocalTime.of(23, 59));
                    try {
                        listDailyTimeRange.add(new DailyTimeRange(newRange1));
                        listDailyTimeRange.add(new DailyTimeRange(newRange2));
                    } catch (StartAfterEndException e2) {
                        throwInvalidFormatArg(DailyTimeRange.MSG_ERR_INVALID_FORMAT_S_ARG,
                                newRange1 + " and/or " + newRange2);
                    }
                }
            }
        }
    }

    private void initAnnualMonthRangeList(@NotNull RpaSignDescParser rpaSignDescParser) {
        if (rpaSignDescParser.getAnnualMonthRange() != null) {
            listAnnualMonthRange = new ArrayList<>();

            for (String s : rpaSignDescParser.getAnnualMonthRange().split(";")) {
                try {
                    listAnnualMonthRange.add(new AnnualMonthRange(s));
                } catch (StartAfterEndException e1) {
                    if (hasRange(e1)) {
                        try {
                            Range<MonthDay> pastRange = (Range<MonthDay>) e1.getRange();
                            Range<MonthDay> newRange1 = new Range<>(pastRange.getStart(), END_OF_MONTH_DAY);
                            Range<MonthDay> newRange2 = new Range<>(START_OF_MONTH_DAY, pastRange.getEnd());
                            listAnnualMonthRange.add(new AnnualMonthRange(newRange1));
                            listAnnualMonthRange.add(new AnnualMonthRange(newRange2));
                        } catch (StartAfterEndException e2) {
                            throwInvalidFormatArg(AnnualMonthRange.MSG_ERR_INVALID_FORMAT_S_ARG, s);
                        }
                    } else {
                        throwInvalidFormatArg(AnnualMonthRange.MSG_ERR_INVALID_FORMAT_S_ARG, s);
                    }
                }
            }
        }
    }

    private boolean hasRange(@NotNull StartAfterEndException exception) {
        return exception.getRange() != null && exception.getRange() instanceof Range;
    }

    private static void throwInvalidFormatArg(String msgErrInvalidFormatSArg, String element) {
        throw new IllegalArgumentException(
                String.format(msgErrInvalidFormatSArg, element));
    }

    public List<DurationMinutes> getListDurationMinutes() {
        return listDurationMinutes != null ? listDurationMinutes : Collections.emptyList();
    }

    public List<DailyTimeRange> getListDailyTimeRange() {
        return listDailyTimeRange != null ? listDailyTimeRange : Collections.emptyList();
    }

    public WeeklyDays getWeeklyDays() {
        return weeklyDays;
    }

    public List<AnnualMonthRange> getListAnnualMonthRange() {
        return listAnnualMonthRange != null ? listAnnualMonthRange : Collections.emptyList();
    }

    public String getAdditionalMetaData() {
        return additionalMetaData;
    }

    @Override
    public String toString() {
        return "RpaSignDescRule{" +
                ", listDurationMinutes=" + listDurationMinutes +
                ", listDailyTimeRange=" + listDailyTimeRange +
                ", weeklyDays=" + weeklyDays +
                ", listAnnualMonthRange=" + listAnnualMonthRange +
                ", additionalMetaData='" + additionalMetaData + '\'' +
                '}';
    }
}
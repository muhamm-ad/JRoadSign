package org.jroadsign.quebec.montreal.src.rpasign.description;

import org.jetbrains.annotations.NotNull;
import org.jroadsign.quebec.montreal.src.rpasign.description.common.GlobalConfigs;
import org.jroadsign.quebec.montreal.src.rpasign.description.common.GlobalFunctions;
import org.jroadsign.quebec.montreal.src.rpasign.description.exceptions.StartAfterEndException;
import org.jroadsign.quebec.montreal.src.rpasign.description.exceptions.WeeklyRangeExpException;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RpaSignDescRule {

    private static final MonthDay START_OF_MONTH_DAY = MonthDay.of(1, 1);
    private static final MonthDay END_OF_MONTH_DAY = MonthDay.of(12, 31);
    private static final LocalTime START_OF_DAY_HOUR = LocalTime.of(0, 0);
    private static final LocalTime END_OF_DAY_HOUR = LocalTime.of(23, 59);

    private boolean parkingAuthorized;
    private List<DurationMinutes> listDurationMinutes = new ArrayList<>();
    private List<DailyTimeRange> listDailyTimeRange = new ArrayList<>();
    private List<DayOfWeek> listDay = new ArrayList<>();
    private List<AnnualMonthRange> listAnnualMonthRange = new ArrayList<>();
    private String additionalMetaData = "";

    public RpaSignDescRule(boolean parkingAuthorized,
                           List<DurationMinutes> listDurationMinutes,
                           List<DailyTimeRange> listDailyTimeRange,
                           WeeklyDays listDay,
                           List<AnnualMonthRange> listAnnualMonthRange,
                           String additionalMetaData) {
        this.parkingAuthorized = parkingAuthorized;
        this.listDurationMinutes = listDurationMinutes;
        this.listDailyTimeRange = listDailyTimeRange;
        this.listDay = listDay.getDays().stream().toList();
        this.listAnnualMonthRange = listAnnualMonthRange;
        this.additionalMetaData = additionalMetaData;
    }

    public RpaSignDescRule(String strRuleDesc) {
        RpaSignDescParser rpaSignDescParser = new RpaSignDescParser(strRuleDesc);

        this.parkingAuthorized = rpaSignDescParser.isParkingAuthorized();
        initDurationMinutesList(rpaSignDescParser);
        initDailyTimeRangeList(rpaSignDescParser);
        initListDay(rpaSignDescParser);
        initAnnualMonthRangeList(rpaSignDescParser);
        initAdditionnalInfo(rpaSignDescParser);
    }

    private void initDurationMinutesList(@NotNull RpaSignDescParser rpaSignDescParser) {
        if (rpaSignDescParser.getDurationMinutes() != null) {
            String[] tabDurationsMinutes = rpaSignDescParser.getDurationMinutes().split(";");
            for (String element : tabDurationsMinutes)
                listDurationMinutes.add(new DurationMinutes(element.trim()));
        }
    }

    private void initDailyTimeRangeList(@NotNull RpaSignDescParser rpaSignDescParser) {
        if (rpaSignDescParser.getDailyTimeRange() != null) {
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

    private void initListDay(@NotNull RpaSignDescParser rpaSignDescParser) {
        try {
            if (rpaSignDescParser.getWeeklyDayRange() != null) {
                listDay = new WeeklyDays(rpaSignDescParser.getWeeklyDayRange()).getDays().stream().toList();
            }
        } catch (WeeklyRangeExpException e1) {
            listDay = e1.getWeeklyDays().getDays().stream().toList();
            if (Objects.requireNonNull(e1.getExpression()) == WeekRangeExpression.ALL_TIMES_EXCEPT) {
                this.parkingAuthorized = true;
            }
        }
    }

    private void initAnnualMonthRangeList(@NotNull RpaSignDescParser rpaSignDescParser) {
        if (rpaSignDescParser.getAnnualMonthRange() != null) {
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

    private void initAdditionnalInfo(RpaSignDescParser rpaSignDescParser) {
        if (rpaSignDescParser.getAdditionalInfo() != null) {
            additionalMetaData = rpaSignDescParser.getAdditionalInfo();
        }
    }

    private boolean hasRange(@NotNull StartAfterEndException exception) {
        return exception.getRange() != null && exception.getRange() instanceof Range;
    }

    private static void throwInvalidFormatArg(String msgErrInvalidFormatSArg, String element) {
        throw new IllegalArgumentException(String.format(msgErrInvalidFormatSArg, element));
    }

    public boolean isParkingAuthorized() {
        return parkingAuthorized;
    }

    public List<DurationMinutes> getListDurationMinutes() {
        return listDurationMinutes;
    }

    public List<DailyTimeRange> getListDailyTimeRange() {
        return listDailyTimeRange;
    }

    public List<DayOfWeek> getListDay() {
        return listDay;
    }

    public List<AnnualMonthRange> getListAnnualMonthRange() {
        return listAnnualMonthRange;
    }

    public String getAdditionalMetaData() {
        return additionalMetaData;
    }

    @Override
    public String toString() {
        return "RpaSignDescRule{" +
                "parkingAuthorized=" + parkingAuthorized +
                ", listDurationMinutes=" + listDurationMinutes +
                ", listDailyTimeRange=" + listDailyTimeRange +
                ", listDay=" + listDay +
                ", listAnnualMonthRange=" + listAnnualMonthRange +
                ", additionalMetaData='" + additionalMetaData + '\'' +
                '}';
    }

    private String getJsonListDay() {
        return GlobalFunctions.getJsonList(
                listDay.stream().map(day -> "\"" + GlobalConfigs.DAY_OF_WEEK_ABREVIATIONS_MAP.get(day) + "\"").toList(),
                String::valueOf
        );
    }


    public String toJson() {
        return "{" +
                "\"parkingAuthorized\": " + parkingAuthorized +
                ",\"listDurationMinutes\": " + GlobalFunctions.getJsonList(listDurationMinutes, duration -> Integer.toString(duration.getDuration())) +
                ",\"listDailyTimeRange\": " + GlobalFunctions.getJsonList(listDailyTimeRange, DailyTimeRange::toJson) +
                ",\"listDay\": " + getJsonListDay() +
                ",\"listAnnualMonthRange\": " + GlobalFunctions.getJsonList(listAnnualMonthRange, AnnualMonthRange::toJson) +
                ",\"additionalMetaData\": \"" + additionalMetaData + "\"" +
                '}';
    }
}
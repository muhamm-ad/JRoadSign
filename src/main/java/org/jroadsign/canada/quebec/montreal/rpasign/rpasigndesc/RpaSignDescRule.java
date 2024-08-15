package org.jroadsign.canada.quebec.montreal.rpasign.rpasigndesc;

import org.jetbrains.annotations.NotNull;
import org.jroadsign.canada.quebec.montreal.rpasign.rpasigndesc.common.GlobalConfigs;
import org.jroadsign.canada.quebec.montreal.rpasign.rpasigndesc.exceptions.StartAfterEndException;
import org.jroadsign.canada.quebec.montreal.rpasign.rpasigndesc.exceptions.WeeklyRangeExpException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author - <a href="https://github.com/muhamm-ad">muhamm-ad</a>
 * @project JRoadSign
 */
public class RpaSignDescRule {

    private static final MonthDay START_OF_MONTH_DAY = MonthDay.of(1, 1);
    private static final MonthDay END_OF_MONTH_DAY = MonthDay.of(12, 31);
    private static final LocalTime START_OF_DAY_HOUR = LocalTime.of(0, 0);
    private static final LocalTime END_OF_DAY_HOUR = LocalTime.of(23, 59);

    private boolean parkingAuthorized;
    private final List<DurationMinutes> listDurationMinutes = new ArrayList<>();
    private final List<DailyTimeRange> listDailyTimeRange = new ArrayList<>();
    private List<DayOfWeek> listDay = new ArrayList<>();
    private final List<AnnualMonthRange> listAnnualMonthRange = new ArrayList<>();
    private String additionalMetaData = "";

    public RpaSignDescRule(String strRuleDesc) {
        RpaSignDescParser rpaSignDescParser = new RpaSignDescParser(strRuleDesc);

        this.parkingAuthorized = rpaSignDescParser.isParkingAuthorized();
        initDurationMinutesList(rpaSignDescParser);
        initDailyTimeRangeList(rpaSignDescParser);
        initListDay(rpaSignDescParser);
        initAnnualMonthRangeList(rpaSignDescParser);
        initAdditionalInfo(rpaSignDescParser);
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

    private void initAdditionalInfo(RpaSignDescParser rpaSignDescParser) {
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

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("parkingAuthorized", parkingAuthorized);

        // Convert listDurationMinutes to a JSONArray of integers
        JSONArray durationArray = new JSONArray();
        for (DurationMinutes duration : listDurationMinutes) {
            durationArray.put(duration.getDuration());
        }
        json.put("listDurationMinutes", durationArray);

        // Convert listDailyTimeRange to a JSONArray of JSON objects
        JSONArray dailyTimeRangeArray = new JSONArray();
        for (DailyTimeRange timeRange : listDailyTimeRange) {
            dailyTimeRangeArray.put(timeRange.toJson());
        }
        json.put("listDailyTimeRange", dailyTimeRangeArray);

        // Convert listDay to a JSONArray of strings (day abbreviations)
        JSONArray dayArray = new JSONArray();
        for (DayOfWeek day : listDay) {
            dayArray.put(GlobalConfigs.DAY_OF_WEEK_ABREVIATIONS_MAP.get(day));
        }
        json.put("listDay", dayArray);

        // Convert listAnnualMonthRange to a JSONArray of JSON objects
        JSONArray annualMonthRangeArray = new JSONArray();
        for (AnnualMonthRange monthRange : listAnnualMonthRange) {
            annualMonthRangeArray.put(monthRange.toJson());
        }
        json.put("listAnnualMonthRange", annualMonthRangeArray);

        json.put("additionalMetaData", additionalMetaData);

        return json;
    }
}
// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.src.rpasign.description;

import java.time.MonthDay;

public class AnnualMonthRange {
    private MonthDay start;
    private MonthDay end;

    public AnnualMonthRange(String sAnnualMonthRange) {
        // TODO match a pattern if no throw exception

        MonthDay pStart = parseMonthDay(sAnnualMonthRange, sAnnualMonthRange); //FIXME
        MonthDay pEnd = parseMonthDay(sAnnualMonthRange, sAnnualMonthRange); // FIXME

        if (pStart.isAfter(pEnd) /* or parse incorrect */) {
            throw new IllegalArgumentException();
        }
        /*else {
        }*/
    }

    public AnnualMonthRange(MonthDay start, MonthDay end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException();
        } else {
            this.start = start;
            this.end = end;
        }
    }

    private MonthDay parseMonthDay(String month, String dayOfMonth) {
        // TODO
        return MonthDay.of(1, 1);
    }

    public MonthDay getStart() {
        return this.start;
    }

    public MonthDay getEnd() {
        return this.end;
    }

    public boolean isWithinRange(MonthDay oMonthDay) {
        return false;
    }

    @Override
    public String toString() {
        return "AnnualMonthRange{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }

}

// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.src.rpasign.description;

import java.time.Duration;
import java.time.LocalTime;

public class DailyTimeRange {
    private LocalTime start;
    private LocalTime end;

    public DailyTimeRange(String sDailyTimeRange) {

        // TODO match a pattern if no throw exception

        LocalTime pStart = parseTime(sDailyTimeRange, sDailyTimeRange); //FIXME
        LocalTime pEnd = parseTime(sDailyTimeRange, sDailyTimeRange); // FIXME

        if (pStart.isAfter(pEnd) /* or parse incorrect */) {
            throw new IllegalArgumentException();
        }
        /*else {
        }*/
    }

    public DailyTimeRange(LocalTime start, LocalTime end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException();
        } else {
            this.start = start;
            this.end = end;
        }
    }

    private LocalTime parseTime(String hour, String minute) {
        // TODO
        return LocalTime.of(0, 0);
    }

    public LocalTime getStart() {
        return this.start;
    }

    public LocalTime getEnd() {
        return this.end;
    }

    public Duration getDuration() {
        return Duration.between(this.start, this.end);
    }

    public boolean isWithinRange(LocalTime time) {
        return false;
    }

    @Override
    public String toString() {
        return "DailyTimeRange{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}

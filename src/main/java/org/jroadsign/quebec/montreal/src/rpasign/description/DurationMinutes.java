package org.jroadsign.quebec.montreal.src.rpasign.description;

public class DurationMinutes {

    private final int duration;

    public DurationMinutes(String sDurationMinutes) {
        this.duration = Integer.parseInt(sDurationMinutes);
    }

    public DurationMinutes(int durationMinutes) {
        this.duration = durationMinutes;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "DurationMinutes{" + duration + '}';
    }
}


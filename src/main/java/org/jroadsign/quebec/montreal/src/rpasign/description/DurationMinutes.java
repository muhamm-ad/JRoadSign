// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.src.rpasign.description;

import org.jroadsign.quebec.montreal.src.rpasign.description.common.GlobalConfigs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationMinutes {
    private static final String MSG_ERR_INVALID_FORMAT_S_ARG =
            "Invalid DurationMinutes format: `%s`. Expected format: "
                    + String.format(GlobalConfigs.DURATION_PATTERN, " ");
    private static final Pattern COMPILED_DURATION_PATTERN = Pattern.compile(
            "^" + String.format(GlobalConfigs.DURATION_PATTERN, " ") + "$");

    private int duration;

    public DurationMinutes(String sDurationMinutes) {
        Matcher matcher = COMPILED_DURATION_PATTERN.matcher(sDurationMinutes);
        if (!matcher.find()) {
            throw new IllegalArgumentException(String.format(MSG_ERR_INVALID_FORMAT_S_ARG, sDurationMinutes));
        }
        String matchedDuration = matcher.group().replace(" MIN", "").trim();
        this.duration = validateDuration(Integer.parseInt(matchedDuration));
    }

    public DurationMinutes(int durationMinutes) {
        this.duration = validateDuration(durationMinutes);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = validateDuration(duration);
    }

    private int validateDuration(int durationToValidate) {
        if (durationToValidate < 0) {
            throw new IllegalArgumentException("Duration cannot be negative: " + durationToValidate);
        }
        return durationToValidate;
    }

    @Override
    public String toString() {
        return "DurationMinutes{" + duration + '}';
    }
}
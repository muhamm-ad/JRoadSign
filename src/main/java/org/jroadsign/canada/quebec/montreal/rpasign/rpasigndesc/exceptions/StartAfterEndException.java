package org.jroadsign.canada.quebec.montreal.rpasign.rpasigndesc.exceptions;

import org.jroadsign.canada.quebec.montreal.rpasign.rpasigndesc.Range;

public class StartAfterEndException extends Exception {

    private Range<?> range;

    public StartAfterEndException(String message, Range<?> range) {
        super(message);
        this.range = range;
    }

    public StartAfterEndException(String message) {
        super(message);
    }

    public StartAfterEndException() {
        super();
    }

    public Range<?> getRange() {
        return range;
    }

}

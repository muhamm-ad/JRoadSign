package org.jroadsign.quebec.montreal.src.rpasign.description;

public class StartAfterEndException extends Exception {
    // Constructor that accepts a message
    public StartAfterEndException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a cause
    public StartAfterEndException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a cause
    public StartAfterEndException(Throwable cause) {
        super(cause);
    }

    // Optional: Constructor without arguments
    public StartAfterEndException() {
        super();
    }
}

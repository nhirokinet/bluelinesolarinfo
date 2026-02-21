package net.nhiroki.lib.bluelineastrolib.exceptions;

/**
 * Input date is out of date range supported by the system.
 */
public class UnsupportedDateRangeException extends Exception {
    public UnsupportedDateRangeException(String message) {
        super(message);
    }
}

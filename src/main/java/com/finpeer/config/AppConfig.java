package com.finpeer.config;

/** Application-wide constants (interest rate bounds, term limits, etc.) */
public class AppConfig {
    public static final double DEFAULT_INTEREST_RATE = 12.0;
    public static final double MIN_INTEREST_RATE = 5.0;
    public static final double MAX_INTEREST_RATE = 24.0;
    public static final int MIN_TERM_MONTHS = 3;
    public static final int MAX_TERM_MONTHS = 60;
}

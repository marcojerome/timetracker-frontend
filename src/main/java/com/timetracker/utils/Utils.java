package com.timetracker.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {

    // DateTimeFormatter instances for different formats
    private final static DateTimeFormatter legacyRequestFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final static DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private final static DateTimeFormatter legacyResponseFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'+0000'");

    /**
     * Converts a time string from ISO format to legacy request format.
     * @param time The time string in ISO format.
     * @return The time string in legacy request format.
     */
    public static String toLegacyRequestTimeFormat(String time) {
        return LocalDateTime.parse(time, isoFormatter).format(legacyRequestFormatter);
    }

    /**
     * Converts a time string from ISO format to legacy response format.
     * @param time The time string in ISO format.
     * @return The time string in legacy response format.
     */
    public static String toLegacyResponseTimeFormat(String time) {
        return LocalDateTime.parse(time, isoFormatter).format(legacyResponseFormatter);
    }

    /**
     * Converts a time string from legacy response format to ISO format.
     * @param time The time string in legacy response format.
     * @return The time string in ISO format.
     */
    public static String toIsoTimeFormat(String time) {
        return LocalDateTime.parse(time, legacyResponseFormatter).format(isoFormatter);
    }

    /**
     * Validates that the start time is before the end time in ISO format.
     * @param startTime The start time string in ISO format.
     * @param endTime The end time string in ISO format.
     * @return True if the start time is before the end time, false otherwise.
     */
    public static boolean validateStartEndTime(String startTime, String endTime) {
        return LocalDateTime.parse(startTime, isoFormatter).isBefore(LocalDateTime.parse(endTime, isoFormatter));
    }

    /**
     * Validates that the start time is before the end time in legacy response format.
     * @param startTime The start time string in legacy response format.
     * @param endTime The end time string in legacy response format.
     * @return True if the start time is before the end time, false otherwise.
     */
    public static boolean validateLegacyStartEndTime(String startTime, String endTime) {
        return LocalDateTime.parse(startTime, legacyResponseFormatter).isBefore(LocalDateTime.parse(endTime, legacyResponseFormatter));
    }
}


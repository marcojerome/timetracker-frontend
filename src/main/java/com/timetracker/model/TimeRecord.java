package com.timetracker.model;

import com.timetracker.utils.Utils;

import java.time.LocalDateTime;

public class TimeRecord {
    private String start;
    private String end;
    private String email;

    // Constructor to initialize TimeRecord
    public TimeRecord(String start, String end, String email) {
        this.start = start;
        this.end = end;
        this.email = email;
    }

    /**
     * Checks if any field (start, end, or email) is null.
     * @return True if any field is null, false otherwise.
     */
    public boolean anyFieldNull() {
        return start == null || end == null || email == null;
    }

    /**
     * Validates the record by checking for null fields and valid start-end time.
     * @return True if the record is valid, false otherwise.
     */
    public boolean isValid() {
        return !anyFieldNull() && validateStartEndTime();
    }

    /**
     * Validates that the start time is before the end time.
     * @return True if the start time is before the end time, false otherwise.
     */
    public boolean validateStartEndTime() {
        return Utils.validateLegacyStartEndTime(start, end);
    }

    /**
     * Converts the start time to ISO format.
     * @return The start time in ISO format.
     */
    public String getFormattedStart() {
        return Utils.toIsoTimeFormat(start);
    }

    /**
     * Converts the end time to ISO format.
     * @return The end time in ISO format.
     */
    public String getFormattedEnd() {
        return Utils.toIsoTimeFormat(end);
    }

    /**
     * Parses and returns the start time as a LocalDateTime object.
     * @return The start time as LocalDateTime.
     */
    public LocalDateTime getStartDateTime() {
        return LocalDateTime.parse(getFormattedStart());
    }

    /**
     * Parses and returns the end time as a LocalDateTime object.
     * @return The end time as LocalDateTime.
     */
    public LocalDateTime getEndDateTime() {
        return LocalDateTime.parse(getFormattedEnd());
    }

    /**
     * Extracts and returns the date part of the start time.
     * @return The start date as a string.
     */
    public String getStartDate() {
        return getFormattedStart().split("T")[0];
    }

    /**
     * Extracts and returns the time part of the start time.
     * @return The start time as a string.
     */
    public String getStartTime() {
        return getFormattedStart().split("T")[1];
    }

    /**
     * Extracts and returns the date part of the end time.
     * @return The end date as a string.
     */
    public String getEndDate() {
        return getFormattedEnd().split("T")[0];
    }

    /**
     * Extracts and returns the time part of the end time.
     * @return The end time as a string.
     */
    public String getEndTime() {
        return getFormattedEnd().split("T")[1];
    }

    // Getters and Setters
    public void setStart(String start) {
        this.start = start;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public String getEmail() {
        return email;
    }
}

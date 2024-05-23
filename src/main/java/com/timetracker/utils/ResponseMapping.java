package com.timetracker.utils;

import java.util.Map;

public class ResponseMapping {

    public final static Map<String, String> RESPONSES = Map.of(
        "SUBMIT_SUCCESS", "Time record submitted successfully",
        "SUBMIT_ERROR", "Error: Time record could not be submitted",
        "INVALID_TIME_RANGE", "Error: Invalid time range, start time must be before end time",
        "INVALID_USER", "Error: User does not exist in the system",
        "UNKNOWN_ERROR", "Error: Unknown error",
        "CONNECTION_ERROR", "Error: Could not connect to the external service"
    );

    public static String getMappedResponse(String key) {
        return RESPONSES.getOrDefault(key, RESPONSES.get("UNKNOWN_ERROR"));
    }

}

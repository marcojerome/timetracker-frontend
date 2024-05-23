package com.timetracker.controller;

import com.timetracker.model.TimeRecord;
import com.timetracker.service.TimeTrackerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class TimeTrackerControllerTest {

    @Mock
    private TimeTrackerService timeTrackerService;

    @InjectMocks
    private TimeTrackerController timeTrackerController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetRecords() {
        // Arrange
        String email = "test@example.com";
        List<TimeRecord> expectedRecords = new ArrayList<>();
        expectedRecords.add(new TimeRecord("2023-01-01T09:00", "2023-01-01T17:00", email));
        // Create and populate the model
        Model model = new ExtendedModelMap();

        // Mock the service response
        when(timeTrackerService.retrieveRecords(anyString(), anyInt(), anyInt(), anyBoolean(), anyBoolean()))
                .thenReturn(expectedRecords);

        when(timeTrackerService.checkExistingUser(anyString()))
                .thenReturn("USER_EXISTS");

        // Act
        String res = timeTrackerController.getTimeRecords(email, 1, false, model);

        // Assert
        assertEquals("records", res);
    }

    @Test
    public void testRecordTime() {
        // Arrange
        String email = "test@example.com";
        String startTime = "2023-01-01T09:00";
        String endTime = "2023-01-01T17:00";
        String expectedResponse = "redirect:/?recordResponse=SUBMIT_SUCCESS&email=test@example.com";

        // Mock the service response
        when(timeTrackerService.recordTime(anyString(), anyString(), anyString()))
                .thenReturn("SUBMIT_SUCCESS");


        // Act
        String response = timeTrackerController.recordTime(email, startTime, endTime, null);

        // Assert
        assertEquals(expectedResponse, response);
    }
}

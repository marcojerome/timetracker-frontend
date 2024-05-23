package com.timetracker.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.timetracker.model.TimeRecord;
import com.timetracker.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


import java.util.ArrayList;
import java.util.List;

public class TimeTrackerServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TimeTrackerService timeTrackerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        timeTrackerService.setRestTemplate(restTemplate);
    }

    @Test
    public void testRecordTime() {
        // Arrange
        String email = "test@example.com";
        String startTime = "2023-01-01T09:00";
        String endTime = "2023-01-01T17:00";

        // Mock response for postForObject
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class))).thenReturn(
                new ResponseEntity<>("SUBMIT_SUCCESS", null, HttpStatus.OK));

        // Act
        String result = timeTrackerService.recordTime(email, startTime, endTime);

        // Assert
        assertEquals("SUBMIT_SUCCESS", result);
    }

    @Test
    public void testGetRecords() {
        // Arrange
        String email = "test@example.com";
        String startTime = "2023-01-01T09:00";
        String endTime = "2023-01-01T17:00";

        List<TimeRecord> expectedRecords = new ArrayList<>();
        expectedRecords.add(new TimeRecord(Utils.toLegacyResponseTimeFormat(startTime), Utils.toLegacyResponseTimeFormat(endTime), email));

        // Mock response for getForObject
        TimeRecord[] mockRecords = expectedRecords.toArray(new TimeRecord[0]);
        when(restTemplate.getForObject(anyString(), eq(TimeRecord[].class))).thenReturn(mockRecords);

        // Act
        List<TimeRecord> records = timeTrackerService.retrieveRecords(email, 0, 10, true, false);

        // Assert
        assertEquals(expectedRecords, records);
    }
}


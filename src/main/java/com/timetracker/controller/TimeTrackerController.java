package com.timetracker.controller;

import com.timetracker.model.TimeRecord;
import com.timetracker.service.TimeTrackerService;
import com.timetracker.utils.ResponseMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class TimeTrackerController {

    @Autowired
    public TimeTrackerService timeTrackerService;

    /**
     * Handles requests to the root URL ("/").
     * Adds the record response message and email to the model to be displayed in the view.
     */
    @GetMapping("/")
    public String index(
            @RequestParam(required = false, defaultValue = "") String recordResponse,
            @RequestParam(required = false, defaultValue = "") String email,
            Model model) {

        // Maps the record response to a user-friendly message
        String processedRecordResponse = recordResponse.isEmpty() ? "" : ResponseMapping.getMappedResponse(recordResponse);

        // Adds attributes to the model to be used in the view
        model.addAttribute("recordResponse", processedRecordResponse);
        model.addAttribute("email", email);

        return "index";
    }

    /**
     * Handles requests to the "/records" URL.
     * Fetches time records for a given email and adds them to the model.
     */
    @GetMapping("/records")
    public String getTimeRecords(
            @RequestParam String email,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "false") boolean fetchMorePages,
            Model model) {

        // Checks if the user exists
        String userCheckResponse = timeTrackerService.checkExistingUser(email);
        if (!userCheckResponse.equals("USER_EXISTS")) {
            return "redirect:/?recordResponse=" + userCheckResponse + "&email=" + email;
        }

        // Fetches records by email
        List<TimeRecord> records = timeTrackerService.getRecordsByEmail(email, page, fetchMorePages);

        // Adds attributes to the model to be used in the view
        model.addAttribute("records", records);
        model.addAttribute("email", email);
        model.addAttribute("page", page);
        model.addAttribute("availablePages", timeTrackerService.getAvailablePages(email));
        model.addAttribute("bulkFetchPages", timeTrackerService.getBulkFetchPages());

        return "records";
    }

    /**
     * Handles POST requests to the "/record" URL.
     * Records time for a given email and redirects to the index with the response message.
     */
    @PostMapping("/record")
    public String recordTime(@RequestParam String email, @RequestParam String startTime, @RequestParam String endTime, Model model) {
        // Records time and gets the response message
        String response = timeTrackerService.recordTime(email, startTime, endTime);

        // Redirects to the index page with the response message and email
        return "redirect:/?recordResponse=" + response + "&email=" + email;
    }
}

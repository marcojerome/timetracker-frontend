package com.timetracker.service;

import com.timetracker.model.TimeRecord;
import com.timetracker.utils.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class TimeTrackerService {

    // Configuration properties with default values
    @Value("${timetracker.records_per_page:10}")
    private int recordsPerPage;

    @Value("${timetracker.bulk_fetch_pages:5}")
    private int bulkFetchPages;

    @Value("${timetracker.cache.max_time_to_live_min:5}")
    private int cacheTimeToLive;

    @Value("${timetracker.legacy_service.base_url:http://timetracker-legacy:8080}")
    private String baseUrl = "http://timetracker-legacy:8080";

    // Last cache cleanup time
    private LocalDateTime lastCacheClean = LocalDateTime.now();

    // Cache to store records by email
    private final ConcurrentMap<String, List<TimeRecord>> recordsByEmail = new ConcurrentHashMap<>();

    // RestTemplate for HTTP requests
    private RestTemplate restTemplate = new RestTemplate();

    // For testing purposes
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Retrieves records by email with pagination.
     * @param email The email to retrieve records for.
     * @param page The page number to retrieve.
     * @param fullSearch Whether to perform a full search.
     * @return The list of time records.
     */
    public List<TimeRecord> getRecordsByEmail(String email, int page, boolean fullSearch) {
        int offset = (page - 1) * recordsPerPage;
        return retrieveRecords(email, offset, recordsPerPage, true, fullSearch);
    }

    /**
     * Scheduled task to clean the cache every fixed rate (e.g., 60 seconds).
     */
    @Scheduled(fixedRate = 60000)
    public void cleanCache() {
        if (lastCacheClean.plusMinutes(cacheTimeToLive).isBefore(LocalDateTime.now())) {
            recordsByEmail.clear();
            lastCacheClean = LocalDateTime.now();
            System.out.println("Cache cleaned");
        }
    }

    /**
     * Queries records from the external service.
     * @param email The email to query records for.
     * @param offset The offset for pagination.
     * @param length The number of records to retrieve.
     * @return The list of queried time records.
     */
    private List<TimeRecord> queryRecords(String email, int offset, int length) {

            // Constructing the URL for the request
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .path("/records")
                    .queryParam("email", email)
                    .queryParam("offset", offset)
                    .queryParam("length", length)
                    .toUriString();

            // Performing the GET request
            TimeRecord[] records = restTemplate.getForObject(url, TimeRecord[].class);

            assert records != null;

            // Converting the array to a list and removing null values
            List<TimeRecord> recordsList = new ArrayList<>(Arrays.asList(records));
            recordsList.removeIf(Objects::isNull);

            return recordsList;

    }

    /**
     * Retrieves records and handles cache logic.
     * @param email The email to retrieve records for.
     * @param offset The offset for pagination.
     * @param length The number of records to retrieve.
     * @param considerNullField Whether to consider null fields.
     * @param bulkFetch Whether to perform bulk fetch.
     * @return The list of retrieved time records.
     */
    public List<TimeRecord> retrieveRecords(String email, int offset, int length, boolean considerNullField, boolean bulkFetch) {
        try {
            // Perform a full search if required
            if (bulkFetch) {
                int currentOffset = recordsByEmail.getOrDefault(email, new ArrayList<>()).size();
                List<TimeRecord> allRecords = Collections.synchronizedList(queryRecords(email, currentOffset, bulkFetchPages * recordsPerPage));
                recordsByEmail.merge(email, allRecords, (existing, newRecords) -> {
                    existing.addAll(newRecords);
                    return existing;
                });
            }

            List<TimeRecord> result;

            // Fetch records from cache if available
            if (recordsByEmail.containsKey(email)) {
                // Fetch and cache records if not enough records are available in cache
                if (recordsByEmail.get(email).size() < offset + length) {
                    int lastOffset = recordsByEmail.get(email).size();
                    List<TimeRecord> queriedRecords = queryRecords(email, lastOffset, offset + length - lastOffset);
                    recordsByEmail.get(email).addAll(queriedRecords);
                }
                // Get records from cache
                result = recordsByEmail.get(email).subList(offset, Math.min(offset + length, recordsByEmail.get(email).size()));
            } else {
                // Fetch and cache records if not available in cache
                result = queryRecords(email, 0, offset + length);
                recordsByEmail.put(email, Collections.synchronizedList(result));
                result = result.subList(Math.max(result.size() - length, 0), result.size());
            }

            // Remove invalid records if considerNullField is false
            result.removeIf(tr -> (!tr.isValid() && !considerNullField));

            // Sort the records by start date time
            result.sort(Comparator.comparing(TimeRecord::getStartDateTime));

            return result;

        // Catch any exceptions and return an empty list
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Calculates available pages based on records in cache.
     * @param email The email to calculate pages for.
     * @return The number of available pages.
     */
    public int getAvailablePages(String email) {
        int maxRecords = recordsByEmail.getOrDefault(email, new ArrayList<>()).size();
        return (int) Math.ceil((double) maxRecords / recordsPerPage);
    }

    public int getBulkFetchPages() {
        return bulkFetchPages;
    }

    /**
     * Checks if user exists by querying one record.
     * @param email The email to check.
     * @return the response message.
     */
    public String checkExistingUser(String email) {

        try {
            List<TimeRecord> records = queryRecords(email, 0, 1);
            return records.isEmpty() ? "INVALID_USER" : "USER_EXISTS";

        } catch (RestClientException | IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            return "CONNECTION_ERROR";
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return "UNKNOWN_ERROR";
        }


    }

    /**
     * Records time with start and end times, and submits it to the external service.
     * @param email The email to record time for.
     * @param startTime The start time to record.
     * @param endTime The end time to record.
     * @return The response message.
     */
    public String recordTime(String email, String startTime, String endTime) {
        try {
            if (!Utils.validateStartEndTime(startTime, endTime)) {
                return "INVALID_TIME_RANGE";
            }

            String url = baseUrl + "/records";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            String requestBody = "email=" + email + "&start=" + Utils.toLegacyRequestTimeFormat(startTime) + "&end=" + Utils.toLegacyRequestTimeFormat(endTime);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                return "SUBMIT_ERROR";
            }

            return "SUBMIT_SUCCESS";

        } catch (RestClientException | IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            return "CONNECTION_ERROR";
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return "SUBMIT_ERROR";
        }
    }
}

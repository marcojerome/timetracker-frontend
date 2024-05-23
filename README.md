
# TimeTracker Frontend

This project is a web frontend for the TimeTracker application, which keeps track of employee work times. The frontend is developed in Java using Spring Boot and Thymeleaf. It integrates with the legacy TimeTracker application deployed as a Docker container.

## Prerequisites

- Docker and Docker Compose installed
- Java Development Kit (JDK) 17 installed
- Maven installed


## Setup Instructions

### Clone the Repository

```sh
git clone <your-repository-url>
cd timetracker-frontend
```

### Build the Docker Image

Use the provided Dockerfile to build the Docker image:

```sh
docker build -t timetracker-frontend .
```

### Deploy the Application

Use docker-compose to deploy and start the frontend container:

```sh
docker-compose up
```

This will start the application, and it can be accessed at `http://localhost:8080`.

### Scripts

You can create a `start.sh` script for convenience:

```sh
#!/bin/bash

# Build the Docker image
docker build -t timetracker-frontend .

# Deploy the container
docker-compose up
```

Make the script executable:

```sh
chmod +x start.sh
```

Now, you can start your application using:

```sh
./start.sh
```

## Configuration

The `application.properties` file contains configuration settings for the TimeTracker Spring Boot application. Here are the main properties and their explanations:

1. **server.port**: This property defines the port on which the application will run. For example, `server.port=8080` will make the application accessible on port 8080.

2. **timetracker.records_per_page**: This property specifies the number of records to be displayed per page. For example, `timetracker.records_per_page=10` sets the default to 10 records per page.

3. **timetracker.bulk_fetch_pages**: This property indicates the number of pages to fetch in bulk when retrieving records. For example, `timetracker.bulk_fetch_pages=5` means 5 pages will be fetched at once.

4. **timetracker.cache.max_time_to_live_min**: This property sets the maximum time-to-live for cache entries, in minutes. For example, `timetracker.cache.max_time_to_live_min=5` sets the cache TTL to 5 minutes.

5. **timetracker.legacy_service.base_url**: This property specifies the base URL of the legacy TimeTracker service. For example, `timetracker.legacy_service.base_url=http://timetracker-legacy:8080` sets the base URL for the legacy service.


## Usage

### View Employee Records

1. Enter the email of the employee to view their time tracking records.
2. Click on "Get Records".

### Record Employee Time

1. Enter the email of the employee.
2. Enter the work start time.
3. Enter the work end time.
4. Click on "Record Time".

## Possible Improvements and Weaknesses

1. **Error Handling**: Enhance error handling for scenarios like network issues or invalid input.
2. **Date format**: Add better date format validation and handling.
3. **Unit Tests**: Implement more comprehensive tests.
4. **Logging**: Introduce more detailed logging.
5. **Storable Records**: Add the ability to store records in a database.
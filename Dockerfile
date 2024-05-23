# Use an official Java runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the compiled Java application jar file into the container
COPY target/timetracker-frontend-0.0.1-SNAPSHOT.jar /app/timetracker-frontend.jar

# Expose the port that the application runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/timetracker-frontend.jar"]
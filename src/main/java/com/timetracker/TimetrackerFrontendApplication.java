package com.timetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TimetrackerFrontendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TimetrackerFrontendApplication.class, args);
	}

}

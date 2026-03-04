package com.crisis360.crisis360_core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class Crisis360CoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(Crisis360CoreApplication.class, args);
	}
}

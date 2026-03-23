package com.trainingapp.trainingapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TrainingAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrainingAppApplication.class, args);
	}

}
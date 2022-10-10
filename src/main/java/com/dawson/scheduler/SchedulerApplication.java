package com.dawson.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.dawson.scheduler.repositories.CourseRepository;

@SpringBootApplication
public class SchedulerApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(SchedulerApplication.class, args);
	}

}

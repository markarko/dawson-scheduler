package com.dawson.scheduler.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dawson.scheduler.entities.Course;
import com.dawson.scheduler.repositories.CourseRepository;

@Service
public class CourseService {
	@Autowired
	CourseRepository courseRepository;
	public List<Course> findByCourseNumberContaining(String partialNumber){
		return courseRepository.findByCourseNumberContaining(partialNumber);
	}
	public Course save(Course course) {
		return courseRepository.save(course);
	}
	public List<Course> saveAll(List<Course> courses) {
		return courseRepository.saveAll(courses);
	}
	public Course findByCourseId(int courseId) {
		return courseRepository.findByCourseId(courseId);
	}
}
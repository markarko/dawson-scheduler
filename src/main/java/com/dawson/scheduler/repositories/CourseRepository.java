package com.dawson.scheduler.repositories;

import java.util.List;
import com.dawson.scheduler.entities.Course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface CourseRepository extends JpaRepository<Course, Integer>{
	public List<Course> findByCourseNumberContaining(String partialNumber);
	public Course findByCourseId(int courseId);
}

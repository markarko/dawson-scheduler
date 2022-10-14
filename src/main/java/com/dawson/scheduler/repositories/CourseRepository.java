package com.dawson.scheduler.repositories;

import java.util.List;
import com.dawson.scheduler.entities.Course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface CourseRepository extends JpaRepository<Course, Integer>{
	public List<Course> findByCourseNumberContaining(String partialNumber);
	public Course findByCourseId(int courseId);
	@Query(value = "SELECT * FROM courses c JOIN sections s USING(coid) WHERE s.seid = :sectionId", nativeQuery = true)
	public Course findBySectionId(@Param("sectionId") int sectionId);
}

package com.dawson.scheduler;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Time;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.dawson.scheduler.entities.Course;
import com.dawson.scheduler.entities.Schedule;
import com.dawson.scheduler.entities.Section;
import com.dawson.scheduler.repositories.CourseRepository;

@DataJpaTest 
public class CourseRepositoryTest {
	
	@Autowired
	CourseRepository courseRepository;

	public Course createCourse() {
		Time startTime1 = Time.valueOf("10:00:00");
		Time endTime1 = Time.valueOf("11:30:00");
		Time startTime2 = Time.valueOf("11:30:00");
		Time endTime2 = Time.valueOf("13:00:00");
		Time startTime3 = Time.valueOf("16:00:00");
		Time endTime3 = Time.valueOf("17:30:00");
		Time startTime4 = Time.valueOf("8:30:00");
		Time endTime4 = Time.valueOf("10:00:00");
		Time startTime5 = Time.valueOf("10:00:00");
		Time endTime5 = Time.valueOf("11:30:00");
		
		Time startTime6 = Time.valueOf("16:00:00");
		Time endTime6 = Time.valueOf("17:30:00");
		Time startTime7 = Time.valueOf("10:00:00");
		Time endTime7 = Time.valueOf("11:30:00");
		Time startTime8 = Time.valueOf("16:00:00");
		Time endTime8 = Time.valueOf("17:30:00");
		Time startTime9 = Time.valueOf("8:30:00");
		Time endTime9 = Time.valueOf("10:00:00");
		Time startTime10 = Time.valueOf("10:00:00");
		Time endTime10 = Time.valueOf("11:30:00");
		
		
		Schedule s1 = Schedule.builder()
				.dayOfWeek(2)
				.startTime(startTime1)
				.endTime(endTime1)
				.location("4H.15")
				.build();
		Schedule s2 = Schedule.builder()
				.dayOfWeek(2)
				.startTime(startTime2)
				.endTime(endTime2)
				.location("2F.14")
				.build();
		Schedule s3 = Schedule.builder()
				.dayOfWeek(2)
				.startTime(startTime3)
				.endTime(endTime3)
				.location("4C.1")
				.build();
		Schedule s4 = Schedule.builder()
				.dayOfWeek(5)
				.startTime(startTime4)
				.endTime(endTime4)
				.location("4H.15")
				.build();
		Schedule s5 = Schedule.builder()
				.dayOfWeek(5)
				.startTime(startTime5)
				.endTime(endTime5)
				.location("2F.14")
				.build();
		
		Schedule s6 = Schedule.builder()
				.dayOfWeek(2)
				.startTime(startTime6)
				.endTime(endTime6)
				.location("4C.1")
				.build();
		Schedule s7 = Schedule.builder()
				.dayOfWeek(3)
				.startTime(startTime7)
				.endTime(endTime7)
				.location("4H.15")
				.build();
		Schedule s8 = Schedule.builder()
				.dayOfWeek(3)
				.startTime(startTime8)
				.endTime(endTime8)
				.location("2F.14")
				.build();
		Schedule s9 = Schedule.builder()
				.dayOfWeek(6)
				.startTime(startTime9)
				.endTime(endTime9)
				.location("4H.19")
				.build();
		Schedule s10 = Schedule.builder()
				.dayOfWeek(6)
				.startTime(startTime10)
				.endTime(endTime10)
				.location("2F.14")
				.build();
		

		Section sec1 = Section.builder()
				.section(1)
				.teacher("Sriswetha Rajagopal")
				.schedules(List.of(s1, s2, s3, s4, s5))
				.build();
		Section sec2 = Section.builder()
				.section(2)
				.teacher("Dirk Dubois")
				.schedules(List.of(s6, s7, s8, s9, s10))
				.build();
				
		return Course.builder()
				.courseNumber("420-510-DW")
				.courseTitle("Programming V")
				.courseDescription("Some description")
				.sections(List.of(sec1, sec2))
				.build();
		
	}
	
	@Test
	public void saveCourseTest() {
		Course c = courseRepository.save(createCourse());
		assertTrue(c.getCourseId() > 0);		
	}
	
	@Test
	public void findByCourseNumberContainingTest(){
		courseRepository.save(createCourse());
		List<Course> c = courseRepository.findByCourseNumberContaining("420");	
		assertTrue(c.get(0).getCourseId() > 0);
		assertTrue(c.size() == 1);
	}
	
	@Test
	public void findByCourseIdTest(){
		courseRepository.save(createCourse());
		Course c = courseRepository.findByCourseId(1);	
		assertTrue(c.getCourseId() == 1);
	}
}

package com.dawson.scheduler;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Time;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.dawson.scheduler.entities.Course;
import com.dawson.scheduler.entities.Schedule;
import com.dawson.scheduler.entities.Section;
import com.dawson.scheduler.services.SectionService;

@SpringBootTest
public class SectionServiceTest {
	@Autowired
	private SectionService sectionService;
	
	@Test
	public void canAddSectionTest() {
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
		
		Time startTime11 = Time.valueOf("8:00:00");
		Time endTime11 = Time.valueOf("10:00:00");
		Time startTime12 = Time.valueOf("10:00:00");
		Time endTime12 = Time.valueOf("11:30:00");
		Time startTime13 = Time.valueOf("8:30:00");
		Time endTime13 = Time.valueOf("10:00:00");
		Time startTime14 = Time.valueOf("10:00:00");
		Time endTime14 = Time.valueOf("11:30:00");
		
		Time startTime15 = Time.valueOf("11:30:00");
		Time endTime15 = Time.valueOf("13:00:00");
		Time startTime16 = Time.valueOf("13:00:00");
		Time endTime16 = Time.valueOf("14:30:00");
		Time startTime17 = Time.valueOf("8:30:00");
		Time endTime17 = Time.valueOf("10:00:00");
		Time startTime18 = Time.valueOf("10:00:00");
		Time endTime18 = Time.valueOf("11:30:00");
		
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
		
		Schedule s11 = Schedule.builder()
				.dayOfWeek(3)
				.startTime(startTime11)
				.endTime(endTime11)
				.location("4H.17")
				.build();
		Schedule s12 = Schedule.builder()
				.dayOfWeek(3)
				.startTime(startTime12)
				.endTime(endTime12)
				.location("2F.16")
				.build();
		Schedule s13 = Schedule.builder()
				.dayOfWeek(6)
				.startTime(startTime13)
				.endTime(endTime13)
				.location("4H.17")
				.build();
		Schedule s14 = Schedule.builder()
				.dayOfWeek(6)
				.startTime(startTime14)
				.endTime(endTime14)
				.location("2F.16")
				.build();
		
		Schedule s15 = Schedule.builder()
				.dayOfWeek(2)
				.startTime(startTime15)
				.endTime(endTime15)
				.location("4H.15")
				.build();
		Schedule s16 = Schedule.builder()
				.dayOfWeek(2)
				.startTime(startTime16)
				.endTime(endTime16)
				.location("2F.14")
				.build();
		Schedule s17 = Schedule.builder()
				.dayOfWeek(4)
				.startTime(startTime17)
				.endTime(endTime17)
				.location("4H.15")
				.build();
		Schedule s18 = Schedule.builder()
				.dayOfWeek(4)
				.startTime(startTime18)
				.endTime(endTime18)
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
		Section sec3 = Section.builder()
				.section(1)
				.teacher("Jean-Claude Desrosiers")
				.schedules(List.of(s11, s12, s13, s14))
				.build();
		Section sec4 = Section.builder()
				.section(2)
				.teacher("Jean-Claude Desrosiers")
				.schedules(List.of(s15, s16, s17, s18))
				.build();
				
		Course c1 = Course.builder()
				.courseId(1)
				.courseNumber("420-510-DW")
				.courseTitle("Programming")
				.courseDescription("The course will focus on the use of algorithms and data structures to simulate real-life phenomena using an appropriate gaming framework. Projects are implemented using an object-oriented language.")
				.sections(List.of(sec1))
				.build();
		Course c2 = Course.builder()
				.courseId(2)
				.courseNumber("420-511-DW")
				.courseTitle("Mobile Development")
				.courseDescription("The course will focus on the development of applications within the Android environment. Students will learn how to analyze, design, construct, and implement an effective mobile application using the Android mobile development environment.")
				.sections(List.of(sec2))
				.build();
		
		Course c3 = Course.builder()
				.courseId(3)
				.courseNumber("420-520-DW")
				.courseTitle("Web Development")
				.courseDescription("The course will examine Web performance from the end-user perspective. Students are introduced to factors that impact browser loading and rendering time, tools that help in measuring performance, and patterns and tips to improve performance.")
				.sections(List.of(sec3))
				.build();
		Course c4 = Course.builder()
				.courseId(4)
				.courseNumber("420-540-DW")
				.courseTitle("Networking")
				.courseDescription("This course is designed to familiarize the student with modern data communications theory, concepts, and terminology, including the various communications media and protocols used to transmit and share information over various types of networks.")
				.sections(List.of(sec4))
				.build();
		

		List<Course> existingCourses = List.of(c1, c2, c3);
		Course courseToAdd = c4;
		
		assertFalse(sectionService.canAddSection(courseToAdd, existingCourses));

		existingCourses = List.of(c2, c3);
		courseToAdd = c1;
		
		assertFalse(sectionService.canAddSection(courseToAdd, existingCourses));
		
		existingCourses = List.of(c2, c3);
		courseToAdd = c4;
		
		assertTrue(sectionService.canAddSection(courseToAdd, existingCourses));
		
		existingCourses = List.of(c2, c3, c4);
		courseToAdd = c4;
		
		assertFalse(sectionService.canAddSection(courseToAdd, existingCourses));
		
	}
	
	@Test
	public void toMinutesTest() {
		String time1 = "3:15:00";
		int min1 = sectionService.toMinutes(time1);
		String time2 = "5:00:00";
		int min2 = sectionService.toMinutes(time2);
		assertTrue(min1 == 195);
		assertTrue(min2 == 300);
	}
}

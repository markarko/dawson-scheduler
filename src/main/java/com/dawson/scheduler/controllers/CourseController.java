package com.dawson.scheduler.controllers;

import java.util.ArrayList;
import java.util.List;
import java.sql.Time;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dawson.scheduler.entities.Course;
import com.dawson.scheduler.entities.Schedule;
import com.dawson.scheduler.entities.Section;
import com.dawson.scheduler.services.CourseService;
import com.dawson.scheduler.services.SectionService;

@Controller
@RequestMapping("/")
public class CourseController {
	
	class ScheduleTime{
		private final String startTime;
		private final String endTime;
		
		public ScheduleTime(String startTime, String endTime) {
			this.startTime = startTime;
			this.endTime = endTime;
		}
		public String getStartTime() {
			return this.startTime;
		}
		public String getEndTime() {
			return this.endTime;
		}
		public static String NumberToStringTime(double time) {
			return (int)time + ":" + (time % 1 == 0 ? "00" : "30");
		}
	}
	
	@Autowired
	private CourseService courseService;
	
	@Autowired
	private SectionService sectionService;
	
	//private List<Section> sections;
	
	private List<Course> selectedCourses;
	private List<Course> validCourses;
	private List<Section> validSections;
	
	public CourseController() {
		//this.sections = new ArrayList<>();
		this.selectedCourses = new ArrayList<>();
		this.validCourses = new ArrayList<>();
		this.validSections = new ArrayList<>();
	}
	

	//transform this to post
	@GetMapping("/")
	public String scheduleGet(Model model) {	
		
		model.addAttribute("courses", this.validCourses);
		model.addAttribute("sections", this.validSections);
		List<ScheduleTime> times = new ArrayList<>();
		int startTime = 7;
		int endTime = 22;
		double time = startTime;
		for (;time < endTime; time+=0.5f) {	
			times.add(new ScheduleTime(ScheduleTime.NumberToStringTime(time), ScheduleTime.NumberToStringTime(time+0.5f)));
		}
		model.addAttribute("times", times);
		return "index";
	}
	
	@PostMapping("/")
	public String schedulePost(@Param("courseAndSectionIds") String... courseAndSectionIds) {
		if (courseAndSectionIds != null) {
			this.validCourses.clear();
			this.validSections.clear();
			// Comes in pairs of course and section id ((course id, section id), (course id, section id)...)
			for (int i = 0; i < courseAndSectionIds.length; i+=2) {
				int cid = Integer.parseInt(courseAndSectionIds[i]);
				Course c = courseService.findByCourseId(cid);
				validCourses.add(c);
				int sid = Integer.parseInt(courseAndSectionIds[i+1]);
				Section s = sectionService.findBySectionId(sid);
				validSections.add(s);
			}
			System.out.println(this.validCourses);
			System.out.println(this.validSections);
		}
		return "redirect:/";
	}
	
	@GetMapping("/search")
	public String searchCourse(@Param("courseNumber") String courseNumber, Model model) {
		List<Course> coursesFound = null;
		
		if (courseNumber != null) {		
			
			coursesFound = courseService.findByCourseNumberContaining(courseNumber);
			if (coursesFound.size() == 0) {
				String courseNumStr = "\"" + courseNumber + "\"";
				sectionService.setErrorMessage(sectionService.getNoCoursesFoundError() + courseNumStr);
				model.addAttribute("error", sectionService.getErrorMessage());
			}
			model.addAttribute("error", sectionService.getErrorMessage());
			model.addAttribute("courses", coursesFound);
		} else {
			
			model.addAttribute("error", sectionService.getErrorMessage());
		}
		sectionService.setErrorMessage("");
		return "search";
	}
	
	@GetMapping("/schedules")
	public String showSchedules(Model model) {
		List<ScheduleTime> times = new ArrayList<>();
		int startTime = 7;
		int endTime = 22;
		double time = startTime;
		for (;time < endTime; time+=0.5f) {	
			times.add(new ScheduleTime(ScheduleTime.NumberToStringTime(time), ScheduleTime.NumberToStringTime(time+0.5f)));
		}
		model.addAttribute("times", times);
		
		if (this.selectedCourses.size() > 0) {
			int numItemsInComb = this.selectedCourses.size();
			int startIndex = 0;
			List<Section> sectionsToGetCombsFrom = new ArrayList<>();
			List<Course> coursesLinkedToSectionCombs = new ArrayList<>();
			List<List<Section>> allPossibleSchedulesAsSections = new ArrayList<>();
			List<List<Course>> allPossibleSchedulesAsCourses = new ArrayList<>();
			for (Course c : this.selectedCourses) {
				for (Section s : c.getSections()) {
					sectionsToGetCombsFrom.add(s);
					coursesLinkedToSectionCombs.add(c);
				}
			}
			
			sectionService.generateAllSchedules(numItemsInComb, startIndex, new ArrayList<Section>(), new ArrayList<Course>(), sectionsToGetCombsFrom, coursesLinkedToSectionCombs, allPossibleSchedulesAsSections, allPossibleSchedulesAsCourses);
			model.addAttribute("courses", allPossibleSchedulesAsCourses);
			model.addAttribute("sections", allPossibleSchedulesAsSections);
		}
		/*for (List<Section> secs : allPossibleSchedulesAsSections) {
			System.out.print("[");
			for (Section sec : secs) {
				System.out.print(sec.getSectionId() + ",");
			}
			System.out.println("]");
		}	*/
		
		return "schedules";
	}
	
	@PostMapping("/addCourse")
	public String addCourse(@Param("courseId") String courseId, @Param("sectionId") String sectionId) {
		if (sectionId != null && courseId != null) {
			Course course = courseService.findByCourseId(Integer.parseInt(courseId));
			if (Integer.parseInt(sectionId) != 0) {
				course.setSections(null);
				Section section = this.sectionService.findBySectionId(Integer.parseInt(sectionId));
				course.setSections(List.of(section));
			}
			if (courseService.canAddCourse(course, selectedCourses)) {
				this.selectedCourses.add(course);	
			}
			//this.sections.add(section);
		} 
		// Can't remove. Have to fix lazy proxy intialization fail
		System.out.println("--- " + this.selectedCourses);
		return "redirect:/schedules";
	}
	
	@GetMapping("/removeCourse")
	public String removeCourse(@Param("courseId") String courseId) {
		if (courseId != null) {
			for (int i = 0; i < this.selectedCourses.size(); i++) {
				if (this.selectedCourses.get(i).getCourseId() == Integer.parseInt(courseId)) {
					this.selectedCourses.remove(i);
					//this.sections.remove(i);
				}
			}
		}
		return "redirect:/";
	}

	//test data
	@GetMapping("/addCourses")
	public String saveAllCourse(Model model) {
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
		
		Time startTime19 = Time.valueOf("14:30:00");
		Time endTime19 = Time.valueOf("16:00:00");
		Time startTime20 = Time.valueOf("16:00:00");
		Time endTime20 = Time.valueOf("17:30:00");
		Time startTime21 = Time.valueOf("13:00:00");
		Time endTime21 = Time.valueOf("14:30:00");
		Time startTime22 = Time.valueOf("14:30:00");
		Time endTime22 = Time.valueOf("16:00:00");
		
		Time startTime23 = Time.valueOf("13:00:00");
		Time endTime23 = Time.valueOf("14:30:00");
		Time startTime24 = Time.valueOf("14:30:00");
		Time endTime24 = Time.valueOf("16:00:00");
		Time startTime25 = Time.valueOf("13:00:00");
		Time endTime25 = Time.valueOf("14:30:00");
		Time startTime26 = Time.valueOf("14:30:00");
		Time endTime26 = Time.valueOf("16:00:00");
		
		Time startTime27 = Time.valueOf("10:00:00");
		Time endTime27 = Time.valueOf("11:30:00");
		Time startTime28 = Time.valueOf("11:30:00");
		Time endTime28 = Time.valueOf("13:30:00");
		Time startTime29 = Time.valueOf("11:30:00");
		Time endTime29 = Time.valueOf("13:00:00");
		
		Time startTime30 = Time.valueOf("14:30:00");
		Time endTime30 = Time.valueOf("16:00:00");
		Time startTime31 = Time.valueOf("10:00:00");
		Time endTime31 = Time.valueOf("11:30:00");
		Time startTime32 = Time.valueOf("11:30:00");
		Time endTime32 = Time.valueOf("13:30:00");
		
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
		
		
		Schedule s19 = Schedule.builder()
				.dayOfWeek(4)
				.startTime(startTime19)
				.endTime(endTime19)
				.location("4H.19")
				.build();
		Schedule s20 = Schedule.builder()
				.dayOfWeek(4)
				.startTime(startTime20)
				.endTime(endTime20)
				.location("2F.14")
				.build();
		Schedule s21 = Schedule.builder()
				.dayOfWeek(6)
				.startTime(startTime21)
				.endTime(endTime21)
				.location("4H.17")
				.build();
		Schedule s22 = Schedule.builder()
				.dayOfWeek(6)
				.startTime(startTime22)
				.endTime(endTime22)
				.location("5B.2")
				.build();
		
		Schedule s23 = Schedule.builder()
				.dayOfWeek(4)
				.startTime(startTime23)
				.endTime(endTime23)
				.location("3E.10")
				.build();
		Schedule s24 = Schedule.builder()
				.dayOfWeek(4)
				.startTime(startTime24)
				.endTime(endTime24)
				.location("2F.24")
				.build();
		Schedule s25 = Schedule.builder()
				.dayOfWeek(6)
				.startTime(startTime25)
				.endTime(endTime25)
				.location("3E.10")
				.build();
		Schedule s26 = Schedule.builder()
				.dayOfWeek(6)
				.startTime(startTime26)
				.endTime(endTime26)
				.location("2F.24")
				.build();
		
		Schedule s27 = Schedule.builder()
				.dayOfWeek(4)
				.startTime(startTime27)
				.endTime(endTime27)
				.location("4H.15")
				.build();
		Schedule s28 = Schedule.builder()
				.dayOfWeek(4)
				.startTime(startTime28)
				.endTime(endTime28)
				.location("2F.14")
				.build();
		Schedule s29 = Schedule.builder()
				.dayOfWeek(5)
				.startTime(startTime29)
				.endTime(endTime29)
				.location("4H.19")
				.build();
		
		Schedule s30 = Schedule.builder()
				.dayOfWeek(2)
				.startTime(startTime30)
				.endTime(endTime30)
				.location("4H.6")
				.build();
		Schedule s31 = Schedule.builder()
				.dayOfWeek(5)
				.startTime(startTime31)
				.endTime(endTime31)
				.location("4H.25")
				.build();
		Schedule s32 = Schedule.builder()
				.dayOfWeek(5)
				.startTime(startTime32)
				.endTime(endTime32)
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
				
		Section sec5 = Section.builder()
				.section(1)
				.teacher("Jaya Nilakantan")
				.schedules(List.of(s19, s20, s21, s22))
				.build();
		Section sec6 = Section.builder()
				.section(2)
				.teacher("Patricia Campbell")
				.schedules(List.of(s23, s24, s25, s26))
				.build();
		Section sec7 = Section.builder()
				.section(1)
				.teacher("Victor Manuel Ponce Diaz")
				.schedules(List.of(s27, s28, s29))
				.build();
		Section sec8 = Section.builder()
				.section(2)
				.teacher("Carlton Davis")
				.schedules(List.of(s30, s31, s32))
				.build();
				
		Course course1 = Course.builder()
				.courseNumber("420-510-DW")
				.courseTitle("Programming")
				.courseDescription("The course will focus on the use of algorithms and data structures to simulate real-life phenomena using an appropriate gaming framework. Projects are implemented using an object-oriented language.")
				.sections(List.of(sec1, sec2))
				.build();
		Course course2 = Course.builder()
				.courseNumber("420-511-DW")
				.courseTitle("Mobile Development")
				.courseDescription("The course will focus on the development of applications within the Android environment. Students will learn how to analyze, design, construct, and implement an effective mobile application using the Android mobile development environment.")
				.sections(List.of(sec3, sec4))
				.build();
		
		Course course3 = Course.builder()
				.courseNumber("420-520-DW")
				.courseTitle("Web Development")
				.courseDescription("The course will examine Web performance from the end-user perspective. Students are introduced to factors that impact browser loading and rendering time, tools that help in measuring performance, and patterns and tips to improve performance.")
				.sections(List.of(sec5, sec6))
				.build();
		Course course4 = Course.builder()
				.courseNumber("420-540-DW")
				.courseTitle("Networking")
				.courseDescription("This course is designed to familiarize the student with modern data communications theory, concepts, and terminology, including the various communications media and protocols used to transmit and share information over various types of networks.")
				.sections(List.of(sec7, sec8))
				.build();
				
		this.courseService.saveAll(List.of(Course.copy(course1), Course.copy(course2), Course.copy(course3), Course.copy(course4)));
		return "redirect:/search";
	}	
}

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
import com.dawson.scheduler.parsers.RequestParser;
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
	
	@Autowired
	private RequestParser requestParser;
	
	private List<Course> selectedCourses;
	private List<Course> validCourses;
	private List<Section> validSections;
	private String errorMessage;
	
	public CourseController() {
		this.selectedCourses = new ArrayList<Course>();
		this.validCourses = new ArrayList<Course>();
		this.validSections = new ArrayList<Section>();
	}
	
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
				model.addAttribute("error", "No courses were found for the keyword " + courseNumStr);
			} 
			model.addAttribute("courses", coursesFound);
		} else {
			model.addAttribute("error", this.errorMessage);
		}
		return "search";
	}
	
	@GetMapping("/schedules")
	public String showSchedules(Model model) {
		this.errorMessage = "";
		List<ScheduleTime> times = new ArrayList<>();
		int startTime = 7;
		int endTime = 22;
		double time = startTime;
		for (;time < endTime; time+=0.5f) {	
			times.add(new ScheduleTime(ScheduleTime.NumberToStringTime(time), ScheduleTime.NumberToStringTime(time+0.5f)));
		}
		
		model.addAttribute("times", times);
		model.addAttribute("courses", this.selectedCourses);
		
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
			model.addAttribute("courseCombinations", allPossibleSchedulesAsCourses);
			model.addAttribute("sectionCombinations", allPossibleSchedulesAsSections);
		}
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
			} else {	
				this.errorMessage = "The course \"" + course.getCourseNumber() + "\" is already selected";
				return "redirect:/search";
			}
		}
		return "redirect:/schedules";
	}
	
	@GetMapping("/removeCourse")
	public String removeCourse(@Param("courseId") String courseId) {
		if (courseId != null) {
			for (int i = 0; i < this.selectedCourses.size(); i++) {
				if (this.selectedCourses.get(i).getCourseId() == Integer.parseInt(courseId)) {
					this.selectedCourses.remove(i);
				}
			}
		}
		return "redirect:/schedules";
	}

	//temporary way of adding courses to the database
	@GetMapping("/addCourses")
	public String saveAllCourse(Model model) {
		requestParser.parseHtml();
		return "redirect:/search";
	}	
}

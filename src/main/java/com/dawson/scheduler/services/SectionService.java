package com.dawson.scheduler.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dawson.scheduler.entities.Course;
import com.dawson.scheduler.entities.Schedule;
import com.dawson.scheduler.entities.Section;
import com.dawson.scheduler.repositories.SectionRepository;

import lombok.Getter;
import lombok.Setter;

@Service
@Getter
@Setter
public class SectionService {
	private String errorMessage = "";
	private String scheduleConflictError = "The schedules have conflicts";
	private String courseSelectedError = "This course is already selected";
	private String noCoursesFoundError = "No courses were found with the course number ";
	
	@Autowired
	private SectionRepository sectionRepository;
	@Autowired 
	private CourseService courseService;
	
	public Section findBySectionId(int sectionId) {
		return sectionRepository.findBySectionId(sectionId);
	}
	public int getIntValueOfWeekDay(String dayOfWeek) {
		switch(dayOfWeek.toLowerCase()) {
			case "sunday": return 1;
			case "monday": return 2;
			case "tuesday": return 3;
			case "wednesday": return 4;
			case "thursday": return 5;
			case "friday": return 6;
			case "saturday": return 7;
			default: throw new IllegalArgumentException("The entered string is not a week day or is mistyped");
		}
	}
	
	// works only when each course has only one section
	/*public boolean canAddSection(Course courseToAdd, List<Course> selectedCourses) {
		if (courseToAdd != null && selectedCourses != null) {
			if (selectedCourses.size() == 0) { return true; }
			for (Course c : selectedCourses) {
				if (c.getCourseId() == courseToAdd.getCourseId()) {
					errorMessage = courseSelectedError;
					System.out.println(courseSelectedError);
					return false;
				}
			}
			List<Section> selectedSections = new ArrayList<>();
			for (Course c : selectedCourses) {
				selectedSections.add(c.getSections().get(0));
			}
			for (Section selectedSection : selectedSections) {
				for (Schedule selectedSc : selectedSection.getSchedules()) {
					for (Schedule sToAdd : courseToAdd.getSections().get(0).getSchedules()) {
						if (selectedSc.getDayOfWeek() == sToAdd.getDayOfWeek()) {
	
							boolean startTimeIssue = toMinutes(""+sToAdd.getStartTime()) >  toMinutes(""+selectedSc.getStartTime())
												&&   toMinutes(""+sToAdd.getStartTime()) <  toMinutes(""+selectedSc.getEndTime());
							boolean endTimeIssue =   toMinutes(""+sToAdd.getEndTime())   >  toMinutes(""+selectedSc.getStartTime())
												&&   toMinutes(""+sToAdd.getEndTime())   <  toMinutes(""+selectedSc.getEndTime());
							boolean bothTimesIssue = toMinutes(""+sToAdd.getStartTime()) <= toMinutes(""+selectedSc.getStartTime())
			                         			&&	 toMinutes(""+sToAdd.getEndTime())   >= toMinutes(""+selectedSc.getEndTime());
	 
			                if (startTimeIssue || endTimeIssue || bothTimesIssue) {
			                	errorMessage = scheduleConflictError;
								System.out.println(scheduleConflictError);
								return false;
							}               
						}	
					}
				}
			}
		} else { 
			System.out.println("One of the parameters is null");
			return false; 
		}
		System.out.println("The course was added successfully");
		return true;
	}*/
	public boolean canAddSection(Section sectionToAdd, List<Section> selectedSections, Course courseToAdd, List<Course> selectedCourses) {
		if (courseToAdd != null && selectedCourses != null && sectionToAdd != null && selectedSections != null) {	
			if (selectedCourses.size() == 0) { return true; }
			// Probably move this loop to a separate method in course service
			for (Course c : selectedCourses) {
				if (c.getCourseId() == courseToAdd.getCourseId()) {
					errorMessage = courseSelectedError;
					System.out.println(courseSelectedError);
					return false;
				}
			}
			for (Section selectedSection : selectedSections) {
				for (Schedule selectedSc : selectedSection.getSchedules()) {
					for (Schedule sToAdd : sectionToAdd.getSchedules()) {
						if (selectedSc.getDayOfWeek() == sToAdd.getDayOfWeek()) {
	
							boolean startTimeIssue = toMinutes(""+sToAdd.getStartTime()) >  toMinutes(""+selectedSc.getStartTime())
												&&   toMinutes(""+sToAdd.getStartTime()) <  toMinutes(""+selectedSc.getEndTime());
							boolean endTimeIssue =   toMinutes(""+sToAdd.getEndTime())   >  toMinutes(""+selectedSc.getStartTime())
												&&   toMinutes(""+sToAdd.getEndTime())   <  toMinutes(""+selectedSc.getEndTime());
							boolean bothTimesIssue = toMinutes(""+sToAdd.getStartTime()) <= toMinutes(""+selectedSc.getStartTime())
			                         			&&	 toMinutes(""+sToAdd.getEndTime())   >= toMinutes(""+selectedSc.getEndTime());
	 
			                if (startTimeIssue || endTimeIssue || bothTimesIssue) {
			                	errorMessage = scheduleConflictError;
								System.out.println(scheduleConflictError);
								return false;
							}               
						}	
					}
				}
			}
		} else { 
			System.out.println("One of the parameters is null");
			return false; 
		}
		System.out.println("The course was added successfully");
		return true;
	}
	/**
	 * This method generates all possible schedules based on a list of courses
	 * @param numItemsInComb 			-The number of items inside each combination
	 * @param startIndex 				-Index at which we loop through the 
	 * @param comb 						-A single resulting combination of ${numItemsInComb} items. Starts off with an empty array
	 * @param coursesToGetCombFrom  	-List of courses from which we will generate all possible combinations
	 * @param allCombs 					-Contains all resulted combinations
	 * @param coursesLinkedToSections	-List of courses that are associated to the list of sections ${sectionsToGetCombFrom}
	 * 									 Example: the section at index 0 in ${sectionsToGetCombFrom} come from the course at index 0 in ${coursesLinkedToSections} 
	 */
	public void generateAllSchedules(int 				 numItemsInComb, 
									 int 				 startIndex, 
									 List<Section>   	 comb, 
									 List<Section> 		 sectionsToGetCombFrom, 
									 List<List<Section>> allCombs,
									 List<Course> 		 coursesLinkedToSections) {
		
        if (comb.size() == numItemsInComb){
            List<Section> newComb = new ArrayList<>();
            for (Section s : comb){
                newComb.add(s);
            }
            allCombs.add(newComb);
            return;
        }
        
        for (int i = startIndex; i < sectionsToGetCombFrom.size(); i++){
        	List<Course> coursesLinkedToSectionsInComb = new ArrayList<>();
        	for (Section s : comb) {
        		coursesLinkedToSectionsInComb.add(courseService.findBySectionId(s.getSectionId()));		
        	}
            if (canAddSection(sectionsToGetCombFrom.get(i), comb, coursesLinkedToSections.get(i), coursesLinkedToSectionsInComb)){
            	comb.add(sectionsToGetCombFrom.get(i));
                generateAllSchedules(numItemsInComb, i + 1, comb, sectionsToGetCombFrom, allCombs, coursesLinkedToSections);
                comb.remove(comb.size()-1);
            }
        }
    }
	
	public int toMinutes(String time) {
		String[] times = time.split(":");
		return Integer.parseInt(times[0]) * 60 + Integer.parseInt(times[1]);
	}
}

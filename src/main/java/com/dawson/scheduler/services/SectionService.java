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
	
	/**
	 * This method checks if a certain section can be added to the already selected sections by checking if their schedules have conflicts
	 * @param sectionToAdd 		-The section that we want to add to the selected sections
	 * @param selectedSections  -Sections that were previously selected
	 * @param courseToAdd		-The course that we want to add to the selected courses
	 * @param selectedCourses	-Courses that were previously selected
	 * @return
	 */

	public boolean canAddSection(Section sectionToAdd, List<Section> selectedSections, Course courseToAdd, List<Course> selectedCourses) {
		if (courseToAdd != null && selectedCourses != null && sectionToAdd != null && selectedSections != null) {	
			if (selectedCourses.size() == 0) { return true; }
			if (!courseService.canAddCourse(courseToAdd, selectedCourses)) { return false; }
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
		return true;
	}
	
	/**
	 * This method generates all possible schedules based on a list of courses
	 * @param numItemsInComb 			-The number of items inside each combination
	 * @param startIndex 				-Index at which we loop through the (starts at 0)
	 * @param sectionComb 				-A single resulting combination of ${numItemsInComb} items as sections. Starts off with an empty array
	 * @param courseComb 				-A single resulting combination of ${numItemsInComb} items as courses. Starts off with an empty array
	 * @param sectionsToGetCombFrom  	-List of sections from which we will generate all possible combinations
	 * @param coursesLinkedToSections	-List of courses that are associated to the list of sections ${sectionsToGetCombFrom}
	 * 									 Example: the section at index 0 in ${sectionsToGetCombFrom} come from the course at index 0 in ${coursesLinkedToSections} 
	 * @param allSectionCombs 			-Contains all resulted combinations as sections
	 * @param allCoursesCombs			-Contains all resulted combinations as courses
	 */
	public void generateAllSchedules(int 				 numItemsInComb, 
									 int 				 startIndex, 
									 List<Section>   	 sectionComb, 
									 List<Course>		 courseComb,
									 List<Section> 		 sectionsToGetCombFrom, 
									 List<Course> 		 coursesLinkedToSections,
									 List<List<Section>> allSectionCombs,
									 List<List<Course>>  allCoursesCombs ) {
		
        if (sectionComb.size() == numItemsInComb){
            List<Section> newSectionComb = new ArrayList<>();
            List<Course> newCourseComb = new ArrayList<>();
            for (Section s : sectionComb){
            	newSectionComb.add(s);
            }
            for (Course c : courseComb){
            	newCourseComb.add(c);
            }
            allSectionCombs.add(newSectionComb);
            allCoursesCombs.add(newCourseComb);
            return;
        }
        
        for (int i = startIndex; i < sectionsToGetCombFrom.size(); i++){
        	List<Course> coursesLinkedToSectionsInComb = new ArrayList<>();
        	for (Section s : sectionComb) {
        		coursesLinkedToSectionsInComb.add(courseService.findBySectionId(s.getSectionId()));		
        	}
            if (canAddSection(sectionsToGetCombFrom.get(i), sectionComb, coursesLinkedToSections.get(i), coursesLinkedToSectionsInComb)){
            	sectionComb.add(sectionsToGetCombFrom.get(i));
            	courseComb.add(coursesLinkedToSections.get(i));
                generateAllSchedules(numItemsInComb, i + 1, sectionComb, courseComb, sectionsToGetCombFrom, coursesLinkedToSections, allSectionCombs, allCoursesCombs);
                courseComb.remove(courseComb.size()-1);
                sectionComb.remove(sectionComb.size()-1);
            }
        }
    }
	
	public int toMinutes(String time) {
		String[] times = time.split(":");
		return Integer.parseInt(times[0]) * 60 + Integer.parseInt(times[1]);
	}
}

package com.dawson.scheduler.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dawson.scheduler.entities.Course;
import com.dawson.scheduler.entities.Schedule;
import com.dawson.scheduler.entities.Section;
import com.dawson.scheduler.repositories.SectionRepository;

@Service
public class SectionService {
	private String error;
	
	@Autowired
	private SectionRepository sectionRepository;
	
	public Section findBySectionId(int sectionId) {
		return sectionRepository.findBySectionId(sectionId);
	}
	/*public int getIntValueOfDayOfWeek(String dayOfWeek) {
		
	}*/
	public boolean canAddSection(Section scheduleToAdd, List<Section> selectedSchedules, Course course, List<Course> selectedCourses) {
		error = "";
		if (course != null && selectedCourses != null) {
			for (Course c : selectedCourses) {
				if (c.getCourseId() == course.getCourseId()) {
					error = "This course is already selected";
					System.out.println("Course is already selected");
					return false;
				}
			}
		}	
		if (scheduleToAdd != null && selectedSchedules != null) {
			for (Section selectedSection : selectedSchedules) {
				for (Schedule selectedSc : selectedSection.getSchedules()) {
					for (Schedule sToAdd : scheduleToAdd.getSchedules()) {
						if (selectedSc.getDayOfWeek() == sToAdd.getDayOfWeek()) {
	
							boolean startTimeIssue = toMinutes(""+sToAdd.getStartTime()) >  toMinutes(""+selectedSc.getStartTime())
												&&   toMinutes(""+sToAdd.getStartTime()) <  toMinutes(""+selectedSc.getEndTime());
							boolean endTimeIssue =   toMinutes(""+sToAdd.getEndTime())   >  toMinutes(""+selectedSc.getStartTime())
												&&   toMinutes(""+sToAdd.getEndTime())   <  toMinutes(""+selectedSc.getEndTime());
							boolean bothTimesIssue = toMinutes(""+sToAdd.getStartTime()) <= toMinutes(""+selectedSc.getStartTime())
			                         			&&	 toMinutes(""+sToAdd.getEndTime())   >= toMinutes(""+selectedSc.getEndTime());
	 
			                if (startTimeIssue || endTimeIssue || bothTimesIssue) {
			                	error = "The schedules have conflicts";
								System.out.println("The schedules have conflicts");
								return false;
							}               
						}			
					}
				}
			}
		} else { return false; }
		System.out.println("The course was added successfully");
		return true;
	}
	public int toMinutes(String time) {
		String[] times = time.split(":");
		return Integer.parseInt(times[0]) * 60 + Integer.parseInt(times[1]);
	}
	public String getError() {
		return this.error;
	}
	public void setError(String error) {
		this.error = error;
	}
}

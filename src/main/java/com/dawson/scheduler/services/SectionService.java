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
	private String message;
	
	@Autowired
	private SectionRepository sectionRepository;
	
	public Section findBySectionId(int sectionId) {
		return sectionRepository.findBySectionId(sectionId);
	}
	/*public int getIntValueOfDayOfWeek(String dayOfWeek) {
		
	}*/
	public boolean canAddSection(Section section, List<Section> selectedSections) {
		// Also validate if the course number is already selected
		// Instead of checking if the section is selected, check if the course number is selected, + 2 params 
		// param1 : course number / param2 : course number list
		// if it returns false, then redirect it back to search with the error message
		message = "";
		if (section != null && selectedSections != null) {
			for (Section sec : selectedSections) {
				for (Schedule selSc : sec.getWeeklyClass().getSchedules()) {
					for (Schedule sc : section.getWeeklyClass().getSchedules()) {
						if (sec.getSectionId() == section.getSectionId()) {
							message = "The section is already selected";
							System.out.println("Class is already selected");
							return false;
						} else if (selSc.getDayOfWeek() == sc.getDayOfWeek()) {

							boolean startTimeIssue = toMinutes(""+sc.getStartTime()) >  toMinutes(""+selSc.getStartTime())
												&&   toMinutes(""+sc.getStartTime()) <  toMinutes(""+selSc.getEndTime());
							boolean endTimeIssue =   toMinutes(""+sc.getEndTime())   >  toMinutes(""+selSc.getStartTime())
												&&   toMinutes(""+sc.getEndTime())   <  toMinutes(""+selSc.getEndTime());
							boolean bothTimesIssue = toMinutes(""+sc.getStartTime()) <= toMinutes(""+selSc.getStartTime())
			                         			&&	 toMinutes(""+sc.getEndTime())   >= toMinutes(""+selSc.getEndTime());
	 
			                if (startTimeIssue || endTimeIssue || bothTimesIssue) {
			                	message = "The schedules have conflicts";
								System.out.println("The schedules have conflicts");
								return false;
							}               
						}			
					}
				}
			}
		} else { return false; }
		return true;
	}
	public int toMinutes(String time) {
		String[] times = time.split(":");
		return Integer.parseInt(times[0]) * 60 + Integer.parseInt(times[1]);
	}
	public String getMessage() {
		return this.message;
	}
}

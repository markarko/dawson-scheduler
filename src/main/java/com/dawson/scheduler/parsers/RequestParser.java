package com.dawson.scheduler.parsers;

import java.io.Console;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.sql.Time;

import com.dawson.scheduler.entities.Course;
import com.dawson.scheduler.entities.Schedule;
import com.dawson.scheduler.entities.Section;
import com.dawson.scheduler.services.CourseService;
import com.dawson.scheduler.services.SectionService;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import java.util.List;
import com.gargoylesoftware.htmlunit.WebResponse;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileNotFoundException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class RequestParser 
{
	@Autowired
	SectionService sectionService;
	
	@Autowired
	CourseService courseService;
    
    public void parseHtml(){
        File file = new File("C:\\Users\\marko\\Desktop\\EclipseWorkspace\\dawson-scheduler\\src\\main\\java\\com\\dawson\\scheduler\\parsers\\response.txt");
		BufferedReader reader = null;
		int j = 0;
		try {
			// Remove all courses when doing the request
			
			reader = new BufferedReader(new FileReader(file));
			StringBuilder stringBuilder = new StringBuilder();
			String line = null;
			String ls = System.getProperty("line.separator");
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
			reader.close();
			String html = stringBuilder.toString();
			Document doc = Jsoup.parse(html);
            Elements courseWraps = doc.select("div.course-list-table div.course-wrap");
            
            // Bug with the course 311-912-DW : no sections, no schedules
            
            //Courses
            for (Element courseWrap : courseWraps){
            
            	
                Element courseNumberTitle = courseWrap.select("div.course-number-title").first();
                Element infoContainer = courseNumberTitle.select("div.info-container").first();
                
                Element courseTitle = infoContainer.select("div.ctitle").first();
                Element courseNumber = infoContainer.select("div.cnumber").first();
                Element description = null;                
                Elements sectionDetails = courseWrap.select("ul.section-details");
	
                List<Section> sectionEntities = new ArrayList<Section>();
                if (courseNumber.text().equals("311-912-DW")) continue;
                //Sections
                for (Element sectionDetail : sectionDetails){
                	
                	Elements rows = sectionDetail.select("li.row");
                    Element scheduleDetails = rows.last().selectFirst("div.col-md-10").selectFirst("table.schedule-details");
                    String potentialIntensive = scheduleDetails.selectFirst("tbody").select("tr").last().select("td").last().text();
                    if (potentialIntensive.equals("Intensive")) continue;
                    
                    if (rows.get(rows.size()-1).text().contains("Intensive")) {
                    	continue;
                    }
                    
                    Element section = null;
                    Element teacher = null;
                    Elements schedules = null;
                    
                    if (rows.size() == 6) {
                    	if (rows.get(0).selectFirst("label").text().equals("Section Title")) {
                    		section = rows.get(1).select("div.col-md-10").first();
                            teacher = rows.get(2).select("div.col-md-10").first();
                            description = rows.get(3).select("div.col-md-10").first();
                            schedules = rows.get(5).select("div.col-md-10").first().select("table tbody tr");
                    	} else if (rows.get(3).selectFirst("label").text().equals("Comment")) {
                    		section = rows.get(0).select("div.col-md-10").first();
                            teacher = rows.get(1).select("div.col-md-10").first();
                            description = rows.get(2).select("div.col-md-10").first();
                            schedules = rows.get(5).select("div.col-md-10").first().select("table tbody tr");
                    	} else {
                    		System.out.println("Something went wrong");
                    		continue;
                    	}
                    } else if (rows.size() == 7){
                    	section = rows.get(1).select("div.col-md-10").first();
                        teacher = rows.get(2).select("div.col-md-10").first();
                        description = rows.get(3).select("div.col-md-10").first();
                        schedules = rows.get(6).select("div.col-md-10").first().select("table tbody tr");
                    } else {
                    	section = rows.get(0).select("div.col-md-10").first();
                        teacher = rows.get(1).select("div.col-md-10").first();
                        description = rows.get(2).select("div.col-md-10").first();
                        schedules = rows.get(4).select("div.col-md-10").first().select("table tbody tr");
                    }
                    
                    

                    List<Schedule> scheduleEntities = new ArrayList<>();
                    for (Element schedule : schedules){
                        Elements cells = schedule.select("td");
                        Element dayOfWeek = cells.get(0);
                        Element times = cells.get(1);

                        List<Time> parsedTimes = parseStartAndEndTimes(times.text());
                        Element location = cells.get(2);
                        Schedule scheduleEntity = Schedule.builder()
                        		.dayOfWeek(sectionService.getIntValueOfWeekDay(dayOfWeek.text()))
                        		.startTime(parsedTimes.get(0))
                        		.endTime(parsedTimes.get(1))
                        		.location(location.text())
                        		.build();

                        scheduleEntities.add(scheduleEntity);
                    }
                    try {
                    	Section sectionEntity = Section.builder()
	                    		.section(Integer.parseInt(section.text()))
	                    		.schedules(scheduleEntities)
	                    		.teacher(teacher.text())
	                    		.build();      
	                    sectionEntities.add(sectionEntity);
                    } catch (NumberFormatException e) {
                    	System.out.println("Can't convert");
                    }
                }
                
                //Intensive courses that have only 1 section
                if (description == null) {
                	continue;
                }
                Course courseEntity = Course.builder()
                		.courseDescription(description.text().length() > 255 ? description.text().substring(0, 255) : description.text())
                		.courseNumber(courseNumber.text())
                		.courseTitle(courseTitle.text())
                		.sections(sectionEntities)
                		.build();
                courseService.save(courseEntity);           
            }
		} catch (FileNotFoundException e){
			System.out.println("File not found");
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		System.out.println("done");
    }
    
    //Refactor the code...
    public static List<Time> parseStartAndEndTimes(String startAndEndTime){
    	
        String[] startAndEndTimes = startAndEndTime.split("-");
        String startTimeStr = startAndEndTimes[0];
        //System.out.println(startAndEndTime);
        String endTimeStr = startAndEndTimes[1];
        
        String[] startTimeAmPmSep = startTimeStr.split(" ");
        
        if (startTimeAmPmSep[1].equals("PM") && !startTimeAmPmSep[0].split(":")[0].equals("12")){
            startTimeAmPmSep[0] = toHoursString(toMinutes(startTimeAmPmSep[0]) + 720);
        }
        String[] endTimeAmPmSep = endTimeStr.split(" ");
        if (endTimeAmPmSep[2].equals("PM") && !endTimeAmPmSep[1].split(":")[0].equals("12")){
            endTimeAmPmSep[1] = toHoursString(toMinutes(endTimeAmPmSep[1]) + 720);
        }
        Time startTime = Time.valueOf(startTimeAmPmSep[0]+":00");
        Time endTime = Time.valueOf(endTimeAmPmSep[1]+":00");
        List<Time> times = new ArrayList<Time>();
        times.add(startTime);
        times.add(endTime);
        return times;
        
    }

    public static int toMinutes(String time) {
		String[] times = time.split(":");
		return Integer.parseInt(times[0]) * 60 + Integer.parseInt(times[1]);
	}
    public static String toHoursString(int time){
        int hours = time / 60;
        int minutes = time % 60;
        return hours + ":" + (minutes < 10 ? "0" + minutes : minutes);
    }
    
}



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
import java.io.*;

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
            
            //Courses
            for (Element courseWrap : courseWraps){
            
            	
                Element courseNumberTitle = courseWrap.select("div.course-number-title").first();
                Element infoContainer = courseNumberTitle.select("div.info-container").first();
                
                Element courseTitle = infoContainer.select("div.ctitle").first();
                Element courseNumber = infoContainer.select("div.cnumber").first();
                Element description = null;
                
                
                
                //Sections
                Elements sectionDetails = courseWrap.select("ul.section-details");
                
                Elements rowsTests = sectionDetails.first().select("li.row");
                if (rowsTests.get(rowsTests.size()-1).text().contains("Intensive")) {
                	continue;
                }
                		
                List<Section> sectionEntities = new ArrayList<Section>();
                boolean canAddCourse = true;
                for (Element sectionDetail : sectionDetails){
                    Elements rows = sectionDetail.select("li.row");
                    Element section = rows.get(0).select("div.col-md-10").first();
                    Element teacher = rows.get(1).select("div.col-md-10").first();
                    description = rows.get(2).select("div.col-md-10").first();
                    Element scheduleWrapper = rows.get(4).select("div.col-md-10").first();
                    Elements schedules = scheduleWrapper.select("table tbody tr");
                    
                    //Testing data anomalies for some courses
                    if (courseNumber.text().equals("603-102-MQ"))
                    	System.out.println(section.text());
                    		
                    List<Schedule> scheduleEntities = new ArrayList<>();
                    for (Element schedule : schedules){
                        Elements cells = schedule.select("td");
                        Element dayOfWeek = cells.get(0);
                        Element times = cells.get(1);
                        //System.out.println(times.text());
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
                    	if (scheduleEntities.size() != 0) {
                    		   	
		                    Section sectionEntity = Section.builder()
		                    		.section(Integer.parseInt(section.text()))
		                    		.schedules(scheduleEntities)
		                    		.teacher(teacher.text())
		                    		.build();      
		                    sectionEntities.add(sectionEntity);
                    	}
                    } catch (NumberFormatException e) {
                    	canAddCourse = false;
                    	//System.out.println("Can't convert");
                    	//System.out.print("----------"+section.text());
                    }
                }
                Course courseEntity = Course.builder()
                		.courseDescription(description.text().length() > 255 ? description.text().substring(0, 255) : description.text())
                		.courseNumber(courseNumber.text())
                		.courseTitle(courseTitle.text())
                		.sections(sectionEntities)
                		.build();
                
                //If anything goes wrong, don't add the course. Temporary solution
                if (canAddCourse && sectionEntities.size() != 0)
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



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
    public static void main( String[] args )
    {
        /*Console console = System.console();
        char[] passwordNotParsed = console.readPassword();
        String password = new String(passwordNotParsed);
        login(password);*/
        //parseHtml();
        //parseStartAndEndTimes("5:00 AM - 6:30 PM");
    }
    /*public static void login(String password) {
        WebClient client = new WebClient();
        //client.getOptions().setJavaScriptEnabled(true);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setUseInsecureSSL(true);
        client.getOptions().setDownloadImages(false);
        client.getOptions().setPopupBlockerEnabled(true);
        client.getOptions().setRedirectEnabled(true);
        client.getOptions().setTimeout(30000);
        client.getOptions().setThrowExceptionOnScriptError(false);
    
        try {
            String loginUrl = "https://dawsoncollege.omnivox.ca/intr/Module/Identification/Login/Login.aspx";
            // Hiding warnings
            java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
            java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
    
            HtmlPage response = client.getPage(loginUrl);
            HtmlForm form = response.getFormByName("formLogin");
    
            String k = form.getInputByName("k").getValueAttribute();
    
            URL url = new URL(loginUrl);
            WebRequest loginRequest = new WebRequest(url, HttpMethod.POST);
    
            // Filling form requests
            ArrayList<NameValuePair> requestParams = new ArrayList<NameValuePair>();
            requestParams.add(new NameValuePair("NoDA", "2133905"));
            requestParams.add(new NameValuePair("PasswordEtu", password));
            requestParams.add(new NameValuePair("TypeIdentification", "Etudiant"));
            requestParams.add(new NameValuePair("TypeLogin", "PostSolutionLogin"));
            requestParams.add(new NameValuePair("k", k));
            loginRequest.setRequestParameters(requestParams);
    
            client.getPage(loginRequest);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");  
            LocalDateTime now = LocalDateTime.now();  
            String x = dtf.format(now);  
            String omnivoxTimetableRedirectUrl = "https://dawsoncollege.omnivox.ca/intr/Module/ServicesExterne/RedirectionServicesExternes.ashx?idService=1077&C=DAW&E=P&L=ANG&Ref="+x;
            HtmlPage response1 = client.getPage(omnivoxTimetableRedirectUrl);
            List<HtmlForm> form1 = response1.getForms();
            HtmlForm form2 = form1.get(0);
            String l = form2.getInputByName("timetable_search_nonce").getValueAttribute();

            ArrayList<NameValuePair> newRequestParams = new ArrayList<NameValuePair>();
            newRequestParams.add(new NameValuePair("action", "timetable_search"));
            newRequestParams.add(new NameValuePair("nonce", l));
            newRequestParams.add(new NameValuePair("specific_ed", ""));
            newRequestParams.add(new NameValuePair("discipline", ""));
            newRequestParams.add(new NameValuePair("general_ed", ""));
            newRequestParams.add(new NameValuePair("special_ed", ""));
            newRequestParams.add(new NameValuePair("course_title", "*"));
            newRequestParams.add(new NameValuePair("section", ""));
            newRequestParams.add(new NameValuePair("teacher", ""));
            newRequestParams.add(new NameValuePair("intensive", ""));
            newRequestParams.add(new NameValuePair("seats", ""));

            String timetableUrl = "https://timetable.dawsoncollege.qc.ca/wp-content/plugins/timetable/search.php";
            URL newUrl = new URL(timetableUrl);
            WebRequest fetchData = new WebRequest(newUrl, HttpMethod.POST);
            fetchData.setRequestParameters(newRequestParams);
            HtmlPage response3 = client.getPage(fetchData);

            WebResponse response2 = response3.getWebResponse();
            String content = response2.getContentAsString();

            PrintStream o = null;
            try {
                o = new PrintStream(new File("C:\\Users\\marko\\Desktop\\response.txt"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            System.setOut(o);
            System.out.println(content); 

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
    }*/
    public void parseHtml(){
        File file = new File("C:\\Users\\marko\\Desktop\\EclipseWorkspace\\dawson-scheduler\\src\\main\\java\\com\\dawson\\scheduler\\parsers\\response.txt");
		BufferedReader reader = null;
		try {
			courseService.deleteAll();
			courseService.flush();
			
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
            
            //List<Course> courseEntities = new ArrayList<>();
            
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
                	//System.out.println("intensive course");
                	continue;
                }
                		
                //System.out.println(courseTitle.text());
                List<Section> sectionEntities = new ArrayList<Section>();
                boolean canAddCourse = true;
                for (Element sectionDetail : sectionDetails){
                    Elements rows = sectionDetail.select("li.row");
                    Element section = rows.get(0).select("div.col-md-10").first();
                    Element teacher = rows.get(1).select("div.col-md-10").first();
                    description = rows.get(2).select("div.col-md-10").first();
                    //System.out.println(section.text());
                    //System.out.println(teacher.text());
                    //System.out.println(description.text());
                    Element scheduleWrapper = rows.get(4).select("div.col-md-10").first();
                    Elements schedules = scheduleWrapper.select("table tbody tr");
                    
                    if (courseNumber.text().equals("603-102-MQ"))
                    	//System.out.println(courseNumber.text());
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
                        //System.out.println(parsedTimes.get(0));
                        //System.out.println(parsedTimes.get(1));
                        //System.out.println(scheduleEntity);
                        scheduleEntities.add(scheduleEntity);
                        
                        //System.out.println(dayOfWeek.text());
                        //System.out.println(times.text());
                        //System.out.println(location.text());
                    }
                    try {
                    	if (scheduleEntities.size() != 0) {
                    		   	
		                    Section sectionEntity = Section.builder()
		                    		.section(Integer.parseInt(section.text()))
		                    		.schedules(scheduleEntities)
		                    		.teacher(teacher.text())
		                    		.build();
		                    
		                    //System.out.println(scheduleEntities);
		                    
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
        //System.out.println(startTimeAmPmSep[0]);
        //System.out.println(endTimeAmPmSep[1]);
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



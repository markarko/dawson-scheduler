const collegeStartTime = "7:00:00";
const collegeTimes = collegeStartTime.split(':');
const collegeStartTimeInMinutes = (+collegeTimes[0] * 60 + +collegeTimes[1]);
const colors = ["red", "green", "orange", "blue", "purple"];


function generateSchedule(courses, sections){
 	let grids = document.querySelectorAll(".grid");
	if (sections !== undefined && courses !== undefined){
		for (i in sections){
			let schedules = sections[i].schedules;
			let course = courses[i];
			let section = sections[i];
			for (j in schedules){
				let schedule = schedules[j];
				let gridRows = getGridRowStartAndEnd(collegeStartTime, schedule.startTime, schedule.endTime);
				let gridRowStart = gridRows[0];
				let gridRowEnd = gridRows[1];
				
				let scheduleGrid = document.querySelector("#schedule");
				let parent = document.createElement("div");
				let weeklyClass = document.createElement("p");
				parent.appendChild(weeklyClass);
				scheduleGrid.appendChild(parent);
				
				parent.style.background = colors[i];
				parent.style.gridColumnStart = schedule.dayOfWeek + 1;
				parent.style.gridColumnEnd = schedule.dayOfWeek + 2;
				parent.style.gridRowStart = gridRowStart;
				parent.style.gridRowEnd = gridRowEnd;   
				parent.style.border = "1px solid black";
				parent.style.textAlign = "center";
				parent.style.display = "flex";
				parent.style.alignItems = "center";
				parent.style.justifyContent = "center";
				
				weeklyClass.style.color = "white";
				weeklyClass.style.margin = "0";
				weeklyClass.style.fontSize = "0.75rem";
				weeklyClass.style.lineHeight = "0.75rem";
				let sectionText = section.section > 10 ? "s.0000" : "s.00000";
				weeklyClass.innerHTML = course.courseTitle + "<br>" + course.courseNumber + "<br>" + sectionText + section.section + "<br>" + schedule.location;	
			}
		}
	} else {
		console.log("Sections or courses are undefined");
	}
}

function getGridRowStartAndEnd(collegeStartTime, startTime, endTime){
	let classStartTimes = startTime.split(':');
	let startTimeInMinutes = (+classStartTimes[0] * 60 + +classStartTimes[1]);

	let classEndTimes = endTime.split(':');
	let endTimeInMinutes = (+classEndTimes[0] * 60 + +classEndTimes[1]);

	let gridRowStart = ((startTimeInMinutes - collegeStartTimeInMinutes) / 30) + 2;
	let gridRowEnd = ((endTimeInMinutes - collegeStartTimeInMinutes) / 30) + 2;
	return [gridRowStart, gridRowEnd];
}
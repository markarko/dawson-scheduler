const collegeStartTime = "7:00:00";
const collegeTimes = collegeStartTime.split(':');
const collegeStartTimeInMinutes = (+collegeTimes[0] * 60 + +collegeTimes[1]);
const colors = ["red", "green", "orange", "blue", "purple", "pink", "grey"];
	
let gridParent = document.querySelector("#grid-parent");
let message = document.querySelector("#message");
let clicked = false;

function generateAllSchedules(courses, sections){
	message.textContent = "";
	
	if (sections !== null && courses !== null){
		if (courses.length == 0) { 
			message.textContent = "No schedules available";
			return;
		} else {
			message.textContent = courses.length > 1 ? courses.length + " schedules were generated" : "1 schedule was generated";
		}
		if (clicked) return;
		clicked = true;
		for (i in sections){		
			createGrid();
			let scheduleGrids = document.querySelectorAll(".schedule");
			let scheduleGrid = scheduleGrids[i];
			let scheduleInfos = document.querySelectorAll(".schedule-info");
			let scheduleInfo = scheduleInfos[i];
			
			let form = document.createElement("form");
			form.setAttribute("action", "/");
			form.setAttribute("method", "post");
			scheduleInfo.appendChild(form);
			let nameAttr = "courseAndSectionIds";
			
			for (j in sections[i]){
				let course = courses[i][j];
				let section = sections[i][j];
				let schedules = section.schedules;
				
				let input = document.createElement("input");
				input.setAttribute("type", "hidden");
				input.setAttribute("name", nameAttr);
				input.setAttribute("value", course.courseId);
				form.appendChild(input);
				input = document.createElement("input");
				input.setAttribute("type", "hidden");
				input.setAttribute("name", nameAttr);
				input.setAttribute("value", section.sectionId);
				form.appendChild(input);
				
				let courseInfo = document.createElement("p");
				courseInfo.innerHTML = +j+1 + ": " + course.courseTitle + "<br>" + course.courseNumber + " sec:" + section.section;
				scheduleInfo.appendChild(courseInfo);

				
				for (k in schedules){
					let schedule = schedules[k];
					let gridRows = getGridRowStartAndEnd(collegeStartTime, schedule.startTime, schedule.endTime);
					let gridRowStart = gridRows[0];
					let gridRowEnd = gridRows[1];
					
					let parent = document.createElement("div");
					let weeklyClass = document.createElement("p");
					
					parent.appendChild(weeklyClass);
					scheduleGrid.appendChild(parent);
					
					parent.style.background = colors[j];
					parent.style.gridColumnStart = schedule.dayOfWeek;
					parent.style.gridColumnEnd = schedule.dayOfWeek + 1;
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
					weeklyClass.textContent = +j+1;		
				}
			}
			let submitButton = document.createElement("button");
			submitButton.setAttribute("type", "submit");
			submitButton.textContent = "select";
			form.appendChild(submitButton);
		}
		
	} else {
		message.textContent = "No courses are selected";
	}
}
function createGrid(){
	let grid = document.createElement("div");
	gridParent.appendChild(grid);
	grid.classList.add("grid");
	
	let gridContainer = document.createElement("div");
	gridContainer.classList.add("grid-container");
	grid.appendChild(gridContainer);
	
	let schedule = document.createElement("div");
	gridContainer.appendChild(schedule);
	schedule.classList.add("schedule");
	
	let table = document.createElement("table");
	gridContainer.appendChild(table);
	let thead = document.createElement("thead");
	table.appendChild(thead);
	let tbody = document.createElement("tbody");
	table.appendChild(tbody);
	let theadRow = document.createElement("tr");
	thead.appendChild(theadRow);
	
	let th = document.createElement("th");
	th.textContent = "S";
	theadRow.appendChild(th);
	th = document.createElement("th");
	th.textContent = "M";
	theadRow.appendChild(th);
	th = document.createElement("th");
	th.textContent = "T";
	theadRow.appendChild(th);
	th = document.createElement("th");
	th.textContent = "W";
	theadRow.appendChild(th);
	th = document.createElement("th");
	th.textContent = "T";
	theadRow.appendChild(th);
	th = document.createElement("th");
	th.textContent = "F";
	theadRow.appendChild(th);
	th = document.createElement("th");
	th.textContent = "S";
	theadRow.appendChild(th);
	
	for (let i = 0; i < 30; i++){
		let tr = document.createElement("tr");
		tbody.appendChild(tr);
		for (let j = 0; j < 7; j++){
			let td = document.createElement("td");
			tr.appendChild(td);
		}
	}
	
	let scheduleInfo = document.createElement("div");
	scheduleInfo.classList.add("schedule-info");
	grid.appendChild(scheduleInfo);
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
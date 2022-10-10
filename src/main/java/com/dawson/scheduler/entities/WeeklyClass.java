package com.dawson.scheduler.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "classes")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WeeklyClass {
	@Id
	@Column(name = "clid")
	@SequenceGenerator(
            name = "class_sequence",
            sequenceName = "class_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "class_sequence"
    )
	private int classId;
	@Column(name = "teacher")
	private String classTeacher;
	@Column(name = "description")
	private String classDescription;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "weekly_class_id")
	private List<Schedule> schedules;
	
	public static WeeklyClass copy(WeeklyClass other) {
		List<Schedule> schedules = new ArrayList<>();
		for (Schedule s : other.getSchedules()) {
			schedules.add(Schedule.copy(s));
		}
		return new WeeklyClass(other.getClassId(), other.getClassTeacher(), other.getClassDescription(), schedules);
	}
}

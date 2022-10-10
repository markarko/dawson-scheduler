package com.dawson.scheduler.entities;

import java.sql.Time;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "schedules")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Schedule {
	@Id
	@Column(name = "scid")
	@SequenceGenerator(
            name = "schedule_sequence",
            sequenceName = "schedule_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "schedule_sequence"
    )
	private int scheduleId;
	private int dayOfWeek;
	private Time startTime;
	private Time endTime;
	private String location;
	
	public static Schedule copy(Schedule other) {
		return new Schedule(other.getScheduleId(), other.getDayOfWeek(), other.getStartTime(), other.getEndTime(), other.getLocation());
	}
}

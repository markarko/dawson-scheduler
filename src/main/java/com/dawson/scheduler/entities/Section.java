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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sections")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Section {
	@Id
	@Column(name = "seid")
	@SequenceGenerator(
            name = "section_sequence",
            sequenceName = "section_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "section_sequence"
    )
	
	private int sectionId;
	private int section;
	private String teacher;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "seid")
	private List<Schedule> schedules;
	
	public static Section copy(Section other) {
		List<Schedule> schedules = new ArrayList<>();
		for (Schedule s : other.getSchedules()) {
			schedules.add(Schedule.copy(s));
		}
		return new Section(other.getSectionId(), other.getSection(), other.getTeacher(), schedules);
	}
}

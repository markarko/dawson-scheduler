package com.dawson.scheduler.entities;

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
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "section_id")
	private WeeklyClass weeklyClass;
	
	public static Section copy(Section other) {
		return new Section(other.getSectionId(), other.getSection(), WeeklyClass.copy(other.getWeeklyClass()));
	}
}

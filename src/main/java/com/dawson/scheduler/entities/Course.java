package com.dawson.scheduler.entities;

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
import javax.persistence.UniqueConstraint;
import javax.persistence.FetchType;

import org.springframework.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses", uniqueConstraints=@UniqueConstraint(columnNames = { "number" }))
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Course {
	@Id
	@Column(name = "coid")
	@SequenceGenerator(
            name = "course_sequence",
            sequenceName = "course_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "course_sequence"
    )
	private int courseId;
	
	@Column(name = "number")
	private String courseNumber;
	
	@Column(name = "title")
	private String courseTitle;	
	
	@Column(name = "description")
	private String courseDescription;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "coid")
	private List<Section> sections;
	
	public static Course copy(Course other) {
		List<Section> sections = new ArrayList<>();
		for (Section s : other.getSections()) {
			sections.add(Section.copy(s));
		}
		return new Course(other.getCourseId(), other.getCourseNumber(), other.getCourseTitle(), other.getCourseDescription(), sections);
	}
}

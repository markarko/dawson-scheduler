package com.dawson.scheduler.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dawson.scheduler.entities.Section;

@Repository
public interface SectionRepository extends JpaRepository<Section, Integer>{
	public Section findBySectionId(int sectionId);
}

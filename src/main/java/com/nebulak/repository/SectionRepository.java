package com.nebulak.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nebulak.model.Section;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {


}
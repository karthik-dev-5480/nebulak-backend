package com.nebulak.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nebulak.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // No code is needed inside the interface
}
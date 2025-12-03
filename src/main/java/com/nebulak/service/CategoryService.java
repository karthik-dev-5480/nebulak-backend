package com.nebulak.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nebulak.model.Category;
import com.nebulak.repository.CategoryRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category createCategory(Category category) {
        // The .save() method handles both creating new entries and updating existing ones.
        // Since the incoming category has no ID, JPA knows to create a new one.
        return categoryRepository.save(category);
    }
    public Category getCategory(Long categoryId) { // Changed parameter to Long
       
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + categoryId));
    }
	public List<Category> getAllCategories() {
		return categoryRepository.findAll();
	}
    
    
}

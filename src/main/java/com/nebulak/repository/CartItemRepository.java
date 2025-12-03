package com.nebulak.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nebulak.model.Cart;
import com.nebulak.model.CartItem;
import com.nebulak.model.Course;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // Finds a specific CartItem by its Cart and Course
    Optional<CartItem> findByCartAndCourse(Cart cart, Course course);
}
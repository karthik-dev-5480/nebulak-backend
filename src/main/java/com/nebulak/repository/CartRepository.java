package com.nebulak.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nebulak.model.Cart;
import com.nebulak.model.User;

public interface CartRepository extends JpaRepository<Cart, Long> {
    // Finds the cart associated with a specific user
    Cart findByUser(User user);
}
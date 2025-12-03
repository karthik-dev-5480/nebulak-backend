package com.nebulak.repository;

import com.nebulak.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Optional<Order> findByPaymentDetails_RazorpayOrderId(String razorpayOrderId);

}
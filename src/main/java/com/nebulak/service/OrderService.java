package com.nebulak.service;

import com.nebulak.dto.CheckoutResponse;
import com.nebulak.model.Course;
import com.nebulak.model.Order;
import com.nebulak.model.OrderItem;
import com.nebulak.model.User;
import com.nebulak.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    
	private final OrderRepository orderRepository;
    private final UserCourseEnrollmentService enrollmentService;
    private final CartService cartService; // <-- NEW: To fetch items from the cart

    // Constructor with new dependency
    public OrderService(OrderRepository orderRepository, UserCourseEnrollmentService enrollmentService, CartService cartService) {
        this.orderRepository = orderRepository;
        this.enrollmentService = enrollmentService;
        this.cartService = cartService; // <-- Injected
    }
    
    @Transactional
    public Order createOrderForCheckout(User user, CheckoutResponse checkout, String razorpayOrderId) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        
        order.setOrderStatus("PENDING"); 
        order.getPaymentDetails().setTransactionStatus("CREATED");
        order.getPaymentDetails().setRazorpayOrderId(razorpayOrderId);
        order.setTotalPrice(checkout.getCheckoutPrice());

List<Course> coursesInCart = cartService.getCoursesInCart(user); 
        
        // 2. Map the courses to OrderItem entities and link them to the new Order
        List<OrderItem> orderItems = coursesInCart.stream()
            .map(course -> {
                OrderItem item = new OrderItem();
                item.setOrder(order); // CRUCIAL: Set the back-reference to the new Order
                item.setCourse(course);
                item.setPrice(course.getDiscountedPrice() != null ? course.getDiscountedPrice() : course.getPrice()); // Use discounted price if available
                return item;
            })
            .collect(Collectors.toList());

        // 3. Set the populated list of OrderItems on the Order entity
        order.setOrderItems(orderItems);
        return orderRepository.save(order);
    }
    
    @Transactional
    public Order updateOrderPaymentSuccess(String rzpOrderId, String rzpPaymentId, String rzpSignature) 
        throws NotFoundException {
        
        Optional<Order> optionalOrder = orderRepository.findByPaymentDetails_RazorpayOrderId(rzpOrderId);
        
        if (optionalOrder.isEmpty()) {
            throw new NotFoundException();
        }
        
        Order order = optionalOrder.get();
        
        order.getPaymentDetails().setRazorpayPaymentId(rzpPaymentId);
        order.getPaymentDetails().setRazorpaySignature(rzpSignature);
        order.getPaymentDetails().setTransactionStatus("SUCCESS");
        
        order.setOrderStatus("PLACED"); 
        for (OrderItem item : order.getOrderItems()) {
            // Assuming OrderItem has a getCourse() method
            Course courseToEnroll = item.getCourse();
            System.out.print(courseToEnroll);
            
            if (courseToEnroll != null) {
                enrollmentService.enrollUserInCourse(order.getUser(), courseToEnroll);
                System.out.print(order.getUser());
            }
       }
        
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrderPaymentFailure(String rzpOrderId, String failureReason) 
        throws NotFoundException {
        
        Optional<Order> optionalOrder = orderRepository.findByPaymentDetails_RazorpayOrderId(rzpOrderId);
        
        if (optionalOrder.isEmpty()) {
            throw new NotFoundException();
        }
        
        Order order = optionalOrder.get();
        
        order.getPaymentDetails().setTransactionStatus("FAILED");
        order.setOrderStatus("FAILED"); 
        
        return orderRepository.save(order);
    }
}
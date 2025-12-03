package com.nebulak.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nebulak.config.JwtProvider;
import com.nebulak.dto.CheckoutResponse;
import com.nebulak.dto.PaymentOrderRequest;
import com.nebulak.dto.PaymentVerificationRequest;
import com.nebulak.model.User;
import com.nebulak.repository.UserRepository;
import com.nebulak.service.CartService;
import com.nebulak.service.OrderService;
import com.nebulak.service.RazorpayService;
import com.razorpay.Order;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
	private final JwtProvider jwtProvider;
	private final UserRepository userRepository;
	private final CartService cartService;
	private final RazorpayService razorpayService;
	private final OrderService orderService;
	
	// Create a final instance of ObjectMapper for JSON conversion
	private final ObjectMapper objectMapper = new ObjectMapper(); 

	public PaymentController(
			JwtProvider jwtProvider,
			UserRepository userRepository,
			CartService cartService,
			RazorpayService razorpayService,
	        OrderService orderService
		) {
			this.jwtProvider = jwtProvider;
			this.userRepository = userRepository;
			this.cartService = cartService;
			this.razorpayService=razorpayService;
	        this.orderService = orderService;
		}
	
	@PostMapping("/createorder")
	public ResponseEntity<Map<String, Object>> createOrder(
	        @RequestHeader("Authorization") String authHeader,
	        @RequestBody PaymentOrderRequest request
	    ) {
    	String jwt = null;
    	
    	 if (authHeader != null && authHeader.startsWith("Bearer ")) {
 	        jwt = authHeader; // Remove "Bearer " prefix
 	    } else {
 	        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
 	    }
    	
    	String email = jwtProvider.getEmailFromToken(jwt);
		User user = userRepository.findByEmail(email); // User retrieval for context/security
		
		if (user == null) {
		    return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		
		Order rzp_order = null;
		Map<String, Object> responseMap = null;
		
		CheckoutResponse checkout = null;
		try {
			checkout = cartService.findCheckout(user, request.getCouponCode());
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Double checkoutPrice=checkout.getCheckoutPrice();
		long priceInPaise = Math.round(checkoutPrice.doubleValue() * 100);
		
		try {
			rzp_order = razorpayService.createRazorpayOrder( priceInPaise , "INR" );
			
			String razorpayOrderId = rzp_order.get("id");
            
            orderService.createOrderForCheckout(user, checkout, razorpayOrderId);
		
			responseMap = objectMapper.readValue(
			    rzp_order.toString(), 
			    new TypeReference<Map<String, Object>>() {}
			);
			
		} catch (Exception e) {
			System.err.println("Error creating Razorpay Order: " + e.getMessage());
			// Return a 500 Internal Server Error if Razorpay fails
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); 
		}
    	
		// Return the serializable Map
        return ResponseEntity.ok(responseMap);
    }
	@PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody PaymentVerificationRequest request
    ) {
		String jwt = null;
    	
   	 if (authHeader != null && authHeader.startsWith("Bearer ")) {
	        jwt = authHeader; // Remove "Bearer " prefix
	    } else {
	        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
	    }
   	
   	String email = jwtProvider.getEmailFromToken(jwt);
		User user = userRepository.findByEmail(email); // User retrieval for context/security
		
		if (user == null) {
		    return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
        try {
            boolean isValidSignature = razorpayService.verifyPaymentSignature(
                request.getRazorpay_order_id(),
                request.getRazorpay_payment_id(),
                request.getRazorpay_signature()
            );

            if (isValidSignature) {
            	orderService.updateOrderPaymentSuccess(
                        request.getRazorpay_order_id(),
                        request.getRazorpay_payment_id(),
                        request.getRazorpay_signature()
                    );
            	cartService.clearCart(user);
                return new ResponseEntity<>("Payment verification successful and enrollment processed.", HttpStatus.OK);
            } else {
            	orderService.updateOrderPaymentFailure(
                        request.getRazorpay_order_id(), 
                        "SIGNATURE_MISMATCH"
                    );
                return new ResponseEntity<>("Payment verification failed: Invalid Signature.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
        	System.err.println("Error during payment verification: " + e.getMessage());
            try {
               orderService.updateOrderPaymentFailure(
                   request.getRazorpay_order_id(), 
                   "INTERNAL_VERIFICATION_ERROR"
               );
            } catch (NotFoundException notFound) {}
            return new ResponseEntity<>("An internal error occurred during verification.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

package com.nebulak.controller; // Adjust package name as needed

import com.nebulak.config.JwtProvider;
import com.nebulak.dto.CartResponse;
import com.nebulak.dto.CheckoutResponse;
import com.nebulak.model.Cart;
import com.nebulak.model.CartItem;
import com.nebulak.model.User;
import com.nebulak.repository.CartRepository;
import com.nebulak.repository.UserRepository;
import com.nebulak.service.CartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

	private final JwtProvider jwtProvider;
	private final UserRepository userRepository;
	private final CartService cartService;
	@Autowired
    private CartRepository cartRepository;

	public CartController(
		JwtProvider jwtProvider,
		UserRepository userRepository,
		CartService cartService
	) {
		this.jwtProvider = jwtProvider;
		this.userRepository = userRepository;
		this.cartService = cartService;
	}
	
    @GetMapping("/user")
    public ResponseEntity<CartResponse> findUserCart( @RequestHeader("Authorization") String authHeader) {
    	String jwt = null;
    	 if (authHeader != null && authHeader.startsWith("Bearer ")) {
 	        jwt = authHeader;
 	    } 
    	String email = jwtProvider.getEmailFromToken(jwt);
		User user = userRepository.findByEmail(email);
		CartResponse cartResponse; 
		try {
            cartResponse = cartService.findCartByUser(user);
            	return new ResponseEntity<>(cartResponse, HttpStatus.OK);
             
        } catch (NotFoundException e) {
        	Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setTotalPrice(0.0);
            cartRepository.save(newCart);
            try {
				cartResponse = cartService.findCartByUser(user);
				return new ResponseEntity<>(cartResponse, HttpStatus.OK);
			} catch (NotFoundException e1) {
				// TODO Auto-generated catch block
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
            
        } catch (Exception e) {
             return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/user/checkout")
    public ResponseEntity<CheckoutResponse> findUserCheckout( @RequestParam(value = "couponCode", required = false) String couponCode ,@RequestHeader("Authorization") String authHeader) {
    	String jwt = null;
    	 if (authHeader != null && authHeader.startsWith("Bearer ")) {
 	        jwt = authHeader;
 	    } 
    	String email = jwtProvider.getEmailFromToken(jwt);
		User user = userRepository.findByEmail(email);
		CheckoutResponse checkoutResponse;
		try {
            checkoutResponse = cartService.findCheckout(user, couponCode);
            return new ResponseEntity<>(checkoutResponse, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
             return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	@PostMapping("/addtocart")
	public ResponseEntity<?> addToCart(
            @RequestParam Long courseId,
	        @RequestHeader("Authorization") String authHeader) {
        	    String jwt = null;
	    if (authHeader != null && authHeader.startsWith("Bearer ")) {
	        jwt = authHeader;
	    } else {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header is missing or invalid.");
	    }
	    try {
			String email = jwtProvider.getEmailFromToken(jwt);
			User user = userRepository.findByEmail(email);
			if (user == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
			}
			Cart updatedCart = cartService.addCourseToCart(user, courseId);
			return ResponseEntity.ok(updatedCart);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding course to cart: " + e.getMessage());
		}
	}
	@DeleteMapping("/removeitem/{cartItemId}")
    public ResponseEntity<?> deleteCartItem(
            @PathVariable Long cartItemId,
	        @RequestHeader("Authorization") String authHeader) {
        try {
        	 String jwt = authHeader;
        	String email = jwtProvider.getEmailFromToken(jwt);
			User user = userRepository.findByEmail(email);
            cartService.removeCartItem(user, cartItemId); 
            return ResponseEntity.ok().body("Cart item removed successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error removing item from cart: " + e.getMessage());
        }
    }
}
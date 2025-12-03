package com.nebulak.service; 
import com.nebulak.dto.CartResponse;
import com.nebulak.dto.CheckoutResponse;
import com.nebulak.model.Cart;
import com.nebulak.model.CartItem;
import com.nebulak.model.Course; 
import com.nebulak.model.User;    
import com.nebulak.repository.CartItemRepository;
import com.nebulak.repository.CartRepository;
import com.nebulak.repository.CourseRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CourseRepository courseRepository;
    
    public Cart findByUser(User user) {
    	
        return cartRepository.findByUser(user);
    }
    
    public Cart addCourseToCart(User user, Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
        Cart cart = cartRepository.findByUser(user);
        if(cart==null) {
        	Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setTotalPrice(0.0);
            cart=newCart;
        	
        };
        		
        Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndCourse(cart, course);
        if (existingCartItem.isPresent()) {
            System.out.println("Course already exists in the cart for user: " + user.getEmail());
            return cart;
        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setCart(cart); 
            newCartItem.setCourse(course);
            cart.getCartItems().add(newCartItem);
            return cartRepository.save(cart);
        }
    }
   
    public void removeCartItem(User user, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new RuntimeException("Cart item not found with ID: " + cartItemId));
    Cart userCart = cartRepository.findByUser(user);
            

        if (!cartItem.getCart().getId().equals(userCart.getId())) {
            throw new RuntimeException("Cart item with ID " + cartItemId + " does not belong to the authenticated user's cart.");
        }
        userCart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        cartRepository.save(userCart);
    }
    
    public List<Course> getCoursesInCart(User user) {
        Cart cart = cartRepository.findByUser(user);
        
        if (cart == null) {
            return new ArrayList<>();
        }

        List<Course> courses = new ArrayList<>();
        for (CartItem item : cart.getCartItems()) {
            courses.add(item.getCourse());
        }
        return courses;
    }

	public CartResponse findCartByUser(User user) throws NotFoundException {
		// TODO Auto-generated method stub
		Cart cart = cartRepository.findByUser(user);
        
        if (cart == null) {
            // Handle case where cart doesn't exist for the user (e.g., throw 404/return empty)
            throw new NotFoundException();
        }
        double calculatedTotalPrice = 0.0;
        double calculatedDiscount= 0.0;
        double calculatedCartTotal = 0.0;
  
        for (CartItem item : cart.getCartItems()) {
            double courseDiscountedPrice = item.getCourse().getDiscountedPrice() > 0 
                                 ? item.getCourse().getDiscountedPrice() 
                                 : item.getCourse().getPrice();
            double coursePrice =  item.getCourse().getPrice() > 0 
                    ?  item.getCourse().getPrice() 
                    :  item.getCourse().getDiscountedPrice() ;
            calculatedTotalPrice += coursePrice;
            calculatedDiscount += coursePrice-courseDiscountedPrice;
            calculatedCartTotal+=courseDiscountedPrice;
            
            
        }
        return new CartResponse(
                cart.getId(),
                cart.getCartItems(),
                calculatedTotalPrice,
                calculatedDiscount,
                calculatedCartTotal
            );
		
	}
	public CheckoutResponse findCheckout(User user, String couponCode) throws NotFoundException {
		// TODO Auto-generated method stub
		Cart cart = cartRepository.findByUser(user);
        
        if (cart == null) {
            // Handle case where cart doesn't exist for the user (e.g., throw 404/return empty)
            throw new NotFoundException();
        }
        double taxGst= 0.0;
        double calculatedCartTotal = 0.0;
        double couponAmount = 0.0;
        String couponCodeApplied =null;
        if (couponCode==null) {
        	couponAmount = 0.0;
        	
        }
        else {
        	couponAmount = 1000.0;
        	couponCodeApplied = couponCode;
        	
        }
        System.out.println(couponCode);
  
        for (CartItem item : cart.getCartItems()) {
            double courseDiscountedPrice = item.getCourse().getDiscountedPrice() > 0 
                                 ? item.getCourse().getDiscountedPrice() 
                                 : item.getCourse().getPrice();
            double coursePrice =  item.getCourse().getPrice() > 0 
                    ?  item.getCourse().getPrice() 
                    :  item.getCourse().getDiscountedPrice() ;
            
            calculatedCartTotal+=courseDiscountedPrice;
            
            
        }
        return new CheckoutResponse(
                
        		calculatedCartTotal,
                taxGst,
                calculatedCartTotal-couponAmount,
                couponAmount,
                0,
                couponCodeApplied
            );
		
	}

	@Transactional
	public void clearCart(User user) {
        // 1. Find the user's Cart
		Cart cart = cartRepository.findByUser(user);
		
        // Check if a cart exists for the user
		if (cart != null) {
            // 2. Delete all associated CartItems (assuming cascade isn't set up to do this automatically)
            // It's safer to delete them explicitly if you only want to clear the items and keep the Cart entity.
            cartItemRepository.deleteAll(cart.getCartItems());
            
            // 3. Clear the in-memory list and reset totals on the Cart entity
            cart.getCartItems().clear();
            cart.setTotalPrice(0.0);
            
            // 4. Save the updated (now empty) Cart
            cartRepository.save(cart);
		}
	}
}
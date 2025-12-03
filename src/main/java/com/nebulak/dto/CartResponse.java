package com.nebulak.dto;


import com.nebulak.model.CartItem; // Assuming CartItem model is used

import java.util.Set;

public class CartResponse {

    private Long id;
    private Set<CartItem> cartItems;
    
    // Financial summary fields required by the frontend
    private double subtotal;        // Sum of original prices
    private double discountAmount;  // Total savings
    private double totalPrice;      // Final price (sum of discounted prices)

    // Constructors
    public CartResponse() {
    }

    public CartResponse(Long id, Set<CartItem> cartItems, double subtotal, double discountAmount, double totalPrice) {
        this.id = id;
        this.cartItems = cartItems;
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.totalPrice = totalPrice;
    }

    // Getters and Setters (omitted for brevity, but required)
    
    // Example Getter:
    public Long getId() {
        return id;
    }

	public Set<CartItem> getCartItems() {
		return cartItems;
	}

	public void setCartItems(Set<CartItem> cartItems) {
		this.cartItems = cartItems;
	}

	public double getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
	}

	public double getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(double discountAmount) {
		this.discountAmount = discountAmount;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public void setId(Long id) {
		this.id = id;
	}
    
    // ... all other Getters and Setters for the fields above
}
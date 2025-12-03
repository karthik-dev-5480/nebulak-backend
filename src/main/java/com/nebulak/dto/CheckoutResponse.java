package com.nebulak.dto;

public class CheckoutResponse {
	 private double cartTotal;        // Sum of original prices
	    private double taxGst;  // Total savings
	    private double checkoutPrice;      // Final price (sum of discounted prices)
	    private double couponAmount;
	    private int cartItems;
	    private String couponCodeApplied;
	    
	    
	    
		public String getCouponCodeApplied() {
			return couponCodeApplied;
		}
		public void setCouponCodeApplied(String couponCodeApplied) {
			this.couponCodeApplied = couponCodeApplied;
		}
		public double getCartTotal() {
			return cartTotal;
		}
		public void setCartTotal(double cartTotal) {
			this.cartTotal = cartTotal;
		}
		public double getTaxGst() {
			return taxGst;
		}
		public void setTaxGst(double taxGst) {
			this.taxGst = taxGst;
		}
		public double getCheckoutPrice() {
			return checkoutPrice;
		}
		public void setCheckoutPrice(double checkoutPrice) {
			this.checkoutPrice = checkoutPrice;
		}
		public double getCouponAmount() {
			return couponAmount;
		}
		public void setCouponAmount(double couponAmount) {
			this.couponAmount = couponAmount;
		}
		public int getCartItems() {
			return cartItems;
		}
		public void setCartItems(int cartItems) {
			this.cartItems = cartItems;
		}
		public CheckoutResponse(double cartTotal, double taxGst, double checkoutPrice, double couponAmount,
				int cartItems, String couponCodeApplied) {
			super();
			this.cartTotal = cartTotal;
			this.taxGst = taxGst;
			this.checkoutPrice = checkoutPrice;
			this.couponAmount = couponAmount;
			this.cartItems = cartItems;
			this.couponCodeApplied=couponCodeApplied;
		}
	
}

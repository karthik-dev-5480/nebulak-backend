package com.nebulak.dto;

//DTO to capture the success response body from the frontend

//DTO to capture the success response body from the frontend
public class PaymentVerificationRequest {
 private String razorpay_order_id;
 private String razorpay_payment_id;
 private String razorpay_signature;

 // Getters and Setters (Ensure exact naming matches the frontend response)
 public String getRazorpay_order_id() {
     return razorpay_order_id;
 }

 public void setRazorpay_order_id(String razorpay_order_id) {
     this.razorpay_order_id = razorpay_order_id;
 }

 public String getRazorpay_payment_id() {
     return razorpay_payment_id;
 }

 public void setRazorpay_payment_id(String razorpay_payment_id) {
     this.razorpay_payment_id = razorpay_payment_id;
 }

 public String getRazorpay_signature() {
     return razorpay_signature;
 }

 public void setRazorpay_signature(String razorpay_signature) {
     this.razorpay_signature = razorpay_signature;
 }
}
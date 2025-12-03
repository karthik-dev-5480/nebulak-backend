package com.nebulak.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

// 1. Mark the class as embeddable
@Embeddable 
public class PaymentDetails {

    @Column(name = "razorpay_order_id")
    private String razorpayOrderId;
    
    @Column(name = "razorpay_payment_id")
    private String razorpayPaymentId; // This is crucial for confirmation

    @Column(name = "razorpay_signature", length = 256)
    private String razorpaySignature;
    
    @Column(name = "payment_method")
    private String paymentMethod = "Razorpay"; // e.g., Razorpay, PayPal, Stripe

    @Column(name = "transaction_status")
    private String transactionStatus = "PENDING"; // e.g., PENDING, SUCCESS, FAILED


    public PaymentDetails() {}

    public PaymentDetails(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }
    
   
    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }
    
    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }

    public String getRazorpaySignature() { return razorpaySignature; }
    public void setRazorpaySignature(String razorpaySignature) { this.razorpaySignature = razorpaySignature; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTransactionStatus() { return transactionStatus; }
    public void setTransactionStatus(String transactionStatus) { this.transactionStatus = transactionStatus; }
}
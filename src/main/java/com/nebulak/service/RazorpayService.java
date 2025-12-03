package com.nebulak.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;

@Service
public class RazorpayService {
	private final RazorpayClient razorpay;
	@Value("${razorpay.key.id}")
    private String keyId;
    
    @Value("${razorpay.key.secret}")
    private String keySecret;

    public RazorpayService(@Value("${razorpay.key.id}") String keyId, @Value("${razorpay.key.secret}") String keySecret) throws Exception {
        this.razorpay = new RazorpayClient(keyId, keySecret);
    }
    
    public Order createRazorpayOrder(Long amount, String currency) throws Exception {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount); 
        orderRequest.put("currency", currency); 
        orderRequest.put("receipt", "receipt_id_" + System.currentTimeMillis());
        orderRequest.put("payment_capture", 1); 
        return razorpay.orders.create(orderRequest);
    }
    
    public boolean verifyPaymentSignature(String orderId, String paymentId, String razorpaySignature) {
        try {
            String payload = orderId + '|' + paymentId;
            return Utils.verifySignature(payload, razorpaySignature, keySecret);
            
        } catch (Exception e) {
            System.err.println("Error during signature verification utility: " + e.getMessage());
            return false;
        }
    }
}

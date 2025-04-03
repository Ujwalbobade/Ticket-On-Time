package com.example.paymentService.paymnetgateway;

import com.example.paymentService.Configuration.RazorpayConfig;
import com.example.paymentService.DTO.Order;
import com.example.paymentService.Model.Payment;
import com.example.paymentService.Model.PaymentStatus;
import com.example.paymentService.Model.PaymenyMethod;
import com.example.paymentService.Repository.PaymentRepository;
import com.razorpay.PaymentLink;
import org.hibernate.annotations.CurrentTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.json.JSONObject;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class RazorpayPaymentGateway implements PaymentGateway{
    @Autowired
    private RazorpayClient razorpayClient;

    @Autowired
    private RazorpayConfig razorpayConfig;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public String getPaymentLink(String email, String phonenumber, String orderid, String name, Long amount) {
        long currentTimeInSeconds = Instant.now().getEpochSecond();
        long expireByTimestamp = currentTimeInSeconds + (20 * 60);
        LocalDateTime currentDateTime = LocalDateTime.now();

        Payment paymentRecord = new Payment();
        paymentRecord.setPaymentMethod(PaymenyMethod.UPI);
        paymentRecord.setPaymentDate(currentDateTime);
        paymentRecord.setAmount(amount);
        paymentRecord.setBookingId(orderid);
        paymentRecord.setUserId(name);

        // Step 1: Fetch and validate order data from external service
        String ordersServiceUrl = "http://orders-service/api/orders/" + orderid; // URL of the OrdersService
        Order order;
        try {
            ResponseEntity<Order> response = restTemplate.getForEntity(ordersServiceUrl, Order.class);
            order = response.getBody();

            if (order == null) {
                throw new RuntimeException("Order not found for ID: " + orderid);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch order details from OrdersService: " + e.getMessage(), e);
        }

        // Validate amount and other order details
        if (!order.getEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("Email does not match the order record.");
        }
        if (!order.getPhoneno().equals(phonenumber)) {
            throw new RuntimeException("Phone number does not match the order record.");
        }
        if (!order.getAmount().equals(amount)) {
            throw new RuntimeException("Amount does not match the order record.");
        }

        try {
            // Step 2: Create payment link with Razorpay
            RazorpayClient razorpay = new RazorpayClient(System.getenv("RAZORPAY_KEY"), System.getenv("RAZORPAY_SECRET"));

            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("amount", amount);
            paymentLinkRequest.put("currency", "INR");
            paymentLinkRequest.put("accept_partial", true);
            paymentLinkRequest.put("first_min_partial_amount", 100);
            paymentLinkRequest.put("expire_by", expireByTimestamp);
            paymentLinkRequest.put("reference_id", orderid);
            paymentLinkRequest.put("description", "Payment for policy no #23456");

            JSONObject customer = new JSONObject();
            customer.put("name", name);
            customer.put("contact", phonenumber);
            customer.put("email", email);
            paymentLinkRequest.put("customer", customer);

            JSONObject notify = new JSONObject();
            notify.put("sms", true);
            notify.put("email", true);
            paymentLinkRequest.put("notify", notify);

            paymentLinkRequest.put("reminder_enable", true);

            JSONObject notes = new JSONObject();
            notes.put("policy_name", "Jeevan Bima");
            paymentLinkRequest.put("notes", notes);

            paymentLinkRequest.put("callback_url", "https://example-callback-url.com/");
            paymentLinkRequest.put("callback_method", "get");

            PaymentLink payment = razorpay.paymentLink.create(paymentLinkRequest);

            // Step 3: Save payment record
            if (payment != null) {
                paymentRecord.setStatus(PaymentStatus.SUCCESS);
                paymentRepository.save(paymentRecord); // Save to database
                return payment.get("short_url").toString();
            } else {
                paymentRecord.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(paymentRecord); // Save to database
                throw new RuntimeException("Failed to create payment link.");
            }
        } catch (RazorpayException e) {
            paymentRecord.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(paymentRecord); // Save failure to database
            throw new RuntimeException("Error while creating payment link: " + e.getMessage(), e);
        }
    }


}

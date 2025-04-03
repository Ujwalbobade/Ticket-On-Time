package com.example.paymentService.paymnetgateway;

public interface PaymentGateway {
    String getPaymentLink(String eamil, String phonenumber, String orderid, String name, Long amount);
}

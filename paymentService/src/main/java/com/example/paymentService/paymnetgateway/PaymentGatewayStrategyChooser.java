package com.example.paymentService.paymnetgateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentGatewayStrategyChooser {
    @Autowired
    private RazorpayPaymentGateway razorpayPaymentGateway;

    public PaymentGateway paymentmethod(String r){
        if(r=="RazorPay"){
            return razorpayPaymentGateway;
        }
        return null;
    }

}

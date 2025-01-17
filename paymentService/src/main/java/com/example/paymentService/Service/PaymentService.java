package com.example.paymentService.Service;

import com.example.paymentService.Model.Payment;
import com.example.paymentService.Model.PaymentStatus;
import com.example.paymentService.Model.PaymenyMethod;
import com.example.paymentService.Repository.PaymentRepository;
import com.example.paymentService.paymnetgateway.PaymentGateway;
import com.example.paymentService.paymnetgateway.PaymentGatewayStrategyChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PaymentService {
    @Autowired
    private PaymentGatewayStrategyChooser paymentchosser;
    @Autowired
    private  PaymentGateway paymentGateway;
    @Autowired
    private PaymentRepository paymentRepository;
    public String initiatePayment(String eamil, String phonenumber, String orderid, String name, Long amount, String PaymentType){
        PaymentGateway paymentGateway=paymentchosser.paymentmethod(PaymentType);
        String link=paymentGateway.getPaymentLink(eamil, phonenumber, orderid, name, amount);
        Payment p = new Payment();

        p.setPaymentMethod(PaymenyMethod.UPI);
        p.setAmount(amount);
        p.setStatus(PaymentStatus.SUCCESS);
        p.setBookingId(orderid);
        paymentRepository.save(p);



        return link;
    }
}

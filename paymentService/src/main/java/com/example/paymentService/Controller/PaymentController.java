package com.example.paymentService.Controller;

import com.example.paymentService.DTO.InitiatePaymentRequestDto;
import com.example.paymentService.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;
    @PostMapping()
    public String InitiatePayment(@RequestBody InitiatePaymentRequestDto initiatePaymentRequestDto){
            return paymentService.initiatePayment(initiatePaymentRequestDto.getEmail(),initiatePaymentRequestDto.getPhonenumber()
            ,initiatePaymentRequestDto.getOrderid(),initiatePaymentRequestDto.getName(),initiatePaymentRequestDto.getAmount(),"RazorPay");
    }

}

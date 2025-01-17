package com.example.paymentService.DTO;

import lombok.Data;

@Data
public class InitiatePaymentRequestDto {
    String email;
    String phonenumber;
    String orderid;
    String name;
    Long amount;
}

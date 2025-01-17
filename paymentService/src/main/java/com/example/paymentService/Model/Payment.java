package com.example.paymentService.Model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.Date;
@Data
@Document
@NoArgsConstructor
public class Payment {
    @MongoId
    private String paymentId; // Unique identifier for the payment
    private String bookingId; // Reference to the booking related to the payment
    private double amount;    // The payment amount
    private PaymenyMethod paymentMethod; // Method of payment (e.g., Credit Card, Wallet, etc.)
    private LocalDateTime paymentDate;   // Date and time of payment
    private String userId;    // Identifier for the user making the payment
    private PaymentStatus status;
}
//5432
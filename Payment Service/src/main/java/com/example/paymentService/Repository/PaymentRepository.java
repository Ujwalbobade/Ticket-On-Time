package com.example.paymentService.Repository;

import com.example.paymentService.Model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


public interface PaymentRepository  extends MongoRepository<Payment,String> {

}

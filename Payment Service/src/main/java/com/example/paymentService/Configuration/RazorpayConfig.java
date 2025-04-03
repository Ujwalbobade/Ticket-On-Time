package com.example.paymentService.Configuration;

import com.razorpay.RazorpayClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@Slf4j
public class RazorpayConfig {
    @Value("${razorpay_id}")
    private String razorpayid;

    @Value("${razorpaySecret_id}")
    private String razorpaySecret;

    private static final String CURRENCY="USD";
    @Bean
    public RazorpayClient getRazorpayClient() {
        log.debug("Initializing RazorpayClient with ID: {}", razorpayid);
        try {
            return new RazorpayClient(razorpayid, razorpaySecret);
        } catch (Exception e) {
            log.error("Error initializing RazorpayClient: ", e);
            throw new RuntimeException("Failed to initialize RazorpayClient", e);
        }
    }
}

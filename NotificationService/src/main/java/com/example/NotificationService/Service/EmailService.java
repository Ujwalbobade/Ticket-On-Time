package com.example.NotificationService.Service;


import com.example.NotificationService.DTOs.BookingDetailsDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

@Service
public class EmailService{

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
    @Autowired
    private RestTemplate restTemplate;  // Used to call the Booking Service

    public void sendBookingConfirmation(Long bookingId) throws MessagingException {
        // Fetch booking details from Booking Service
        BookingDetailsDto bookingDetails = restTemplate.getForObject(
                "http://BOOKING-SERVICE/booking/" + bookingId,
                BookingDetailsDto.class
        );

        if (bookingDetails == null) {
            throw new RuntimeException("Booking details not found!");
        }

        // Prepare Thymeleaf template variables
        Context context = new Context();
        context.setVariable("name", bookingDetails.getName());
        context.setVariable("event", bookingDetails.getEvent());
        context.setVariable("date", bookingDetails.getDate());
        context.setVariable("seat", bookingDetails.getSeat());

        // Generate HTML content
        String htmlContent = templateEngine.process("email_template", context);

        // Prepare email
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
        helper.setTo(bookingDetails.getEmail());
        helper.setSubject("Booking Confirmation for " + bookingDetails.getEvent());
        helper.setText(htmlContent, true);

        // Send email
        mailSender.send(message);
    }
}

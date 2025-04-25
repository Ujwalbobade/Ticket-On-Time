package com.example.NotificationService.Controller;



import com.example.NotificationService.Service.EmailService;
import com.example.NotificationService.Service.EmailServiceHtml;
import com.google.zxing.WriterException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailServiceHtml emailServiceHtml;

    @GetMapping("/send-html-email")
    public String sendHtmlEmail(
            @RequestParam String to,
            @RequestParam String name,
            @RequestParam String event,
            @RequestParam String date,
            @RequestParam String seat) throws MessagingException, IOException, WriterException {
        // emailServiceHtml.sendHtmlEmail(to, "Booking Confirmation", name, event, date, seat);
        emailServiceHtml.sendEmailWithAttachments(to, name, event, date, seat);
        return "HTML email sent successfully!";
    }

    @GetMapping("/send-email")
    public String sendEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String body) {
        emailService.sendEmail(to, subject, body);
        return "Email sent successfully!";
    }

    @GetMapping("/send-confirmation/{bookingId}")
    public String sendBookingEmail(@PathVariable Long bookingId) {
        try {
            emailService.sendBookingConfirmation(bookingId);
            return "Booking confirmation email sent successfully!";
        } catch (MessagingException e) {
            return "Error sending email: " + e.getMessage();
        }
    }
}

package com.example.JWTlogin.Service;

import com.example.JWTlogin.JWT.JWTutil;
import com.example.JWTlogin.Model.AppUser;
import com.example.JWTlogin.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmailService {

    @Autowired
    private UserRepository userRepository;  // To find user by email

    // To generate reset token
    @Autowired
    private JWTutil jwts;


    @Autowired
    private JavaMailSender mailSender;  // To send email (Spring's mail sender)

    public boolean sendPasswordResetEmail( String email) {
        Optional<AppUser> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            AppUser user = userOptional.get();

            // Generate a secure, time-limited password reset token
            String resetToken = JWTutil.generateToken1(email);

            // Construct the password reset URL
            String resetLink = "https://localhost:8080/reset-password?token=" + resetToken;

            // Send the reset link to the user's email
            sendEmail(user.getEmail(), resetLink);

            return true;
        }

        return false;
    }

    private void sendEmail(String email, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText("Click the link below to reset your password:\n" + resetLink);

        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send password reset email.", e);
        }
    }

}


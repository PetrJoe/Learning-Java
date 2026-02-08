package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Account Verification");
        message.setText("Your verification code is: " + code);
        
        try {
            mailSender.send(message);
        } catch (Exception e) {
            // Log error but don't fail the request in demo mode if config is invalid
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
        }
    }
}

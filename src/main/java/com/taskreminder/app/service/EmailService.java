package com.taskreminder.app.service;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String body) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);

        } catch (MessagingException ex) {
            throw new RuntimeException("Failed to send email", ex);
        }
    }

    @Async
    public void sendOtpEmail(String to, String otp) {
        String subject = "Verify your account";
        String body = """
            <div style="font-family: Arial; padding: 20px;">
                <h2>Account Verification</h2>
                <p>Your OTP is:</p>
                <h1>%s</h1>
                <p>This OTP expires in 10 minutes.</p>
            </div>
            """.formatted(otp);

        sendEmail(to, subject, body);
    }
}

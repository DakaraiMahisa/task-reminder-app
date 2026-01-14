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
    public void sendResetPasswordEmail(String toEmail, String resetLink) {

        String subject = "Reset Your Password - Task Reminder App";
        String body = buildResetPasswordEmailBody(resetLink);

        sendEmail(toEmail, subject, body);
    }
    private String buildResetPasswordEmailBody(String resetLink) {
        return """
        <html>
        <body style="font-family: Arial, sans-serif;">
            <h2>Password Reset Request</h2>
            <p>You requested to reset your password.</p>
            <p>
                <a href="%s"
                   style="padding:10px 16px;
                          background:#2563eb;
                          color:white;
                          text-decoration:none;
                          border-radius:4px;">
                   Reset Password
                </a>
            </p>
            <p>This link will expire in <b>30 minutes</b>.</p>
            <p>If you did not request this, please ignore this email.</p>
            <br/>
            <p>Regards,<br/>Task Reminder Team</p>
        </body>
        </html>
        """.formatted(resetLink);
    }

}

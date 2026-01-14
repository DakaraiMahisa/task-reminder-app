package com.taskreminder.app.service;

import com.taskreminder.app.entity.PasswordResetToken;
import com.taskreminder.app.entity.User;
import com.taskreminder.app.repository.PasswordResetTokenRepository;
import com.taskreminder.app.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public void sendResetLink(String email) {

        userRepository.findByEmail(email).ifPresent(user -> {

            tokenRepository.deleteByUser(user);

            String token = UUID.randomUUID().toString();
            String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(encodedToken);
            resetToken.setUser(user);
            resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));

            tokenRepository.save(resetToken);

            String resetLink =
                    "http://localhost:8080/auth/reset-password?token=" + encodedToken;
            System.out.println("Your reset link is:" + resetLink);
            emailService.sendResetPasswordEmail(user.getEmail(), resetLink);
        });
    }
    public void resetPassword(String token, String newPassword) {

        PasswordResetToken resetToken =
                tokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new IllegalArgumentException("Invalid token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(resetToken);
    }
    public boolean isTokenValid(String token) {
        return tokenRepository.findByToken(token)
                .filter(t -> !t.isUsed())
                .filter(t -> t.getExpiryDate().isAfter(LocalDateTime.now()))
                .isPresent();
    }

}
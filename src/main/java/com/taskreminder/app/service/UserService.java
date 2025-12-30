package com.taskreminder.app.service;

import com.taskreminder.app.entity.User;
import com.taskreminder.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    public User register(User user){
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw  new RuntimeException("Email already registered");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerified(false);

        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        // TODO: send OTP via email

        return userRepository.save(user);
    }

    public boolean verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getOtp().equals(otp) &&
                user.getOtpExpiry().isAfter(LocalDateTime.now())) {

            user.setVerified(true);
            user.setOtp(null);
            user.setOtpExpiry(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }
    private String generateOtp() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }

}

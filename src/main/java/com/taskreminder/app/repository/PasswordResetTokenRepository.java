package com.taskreminder.app.repository;

import com.taskreminder.app.entity.PasswordResetToken;
import com.taskreminder.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUser(User user);
}

package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.model.PasswordResetToken;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.repository.PasswordResetTokenRepository;
import com.tournament.app.footycup.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final int RESET_TOKEN_EXPIRATION_MINUTES = 60;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailProducer emailProducer;

    @Value("${app.reset-password.base-url:http://localhost:4200/reset-password}")
    private String resetPasswordBaseUrl;

    public void sendResetLink(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        optionalUser.ifPresent(user -> {
            passwordResetTokenRepository.deleteByUser(user);

            String tokenValue = UUID.randomUUID().toString();
            LocalDateTime expiration = LocalDateTime.now().plusMinutes(RESET_TOKEN_EXPIRATION_MINUTES);
            PasswordResetToken resetToken = new PasswordResetToken(tokenValue, expiration, user);
            passwordResetTokenRepository.save(resetToken);

            String resetLink = String.format("%s?token=%s", resetPasswordBaseUrl, tokenValue);
            String subject = "FootyCup password reset";
            String content = "Hi " + user.getFirstname() + ",\n\n" +
                    "We received a request to reset your FootyCup password. " +
                    "You can reset it using the link below within the next " + RESET_TOKEN_EXPIRATION_MINUTES + " minutes.\n\n" +
                    resetLink + "\n\n" +
                    "If you did not request a password reset, please ignore this message.";

            emailProducer.sendEmail(user.getEmail(), subject, content);
        });
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(resetToken);
            throw new IllegalArgumentException("Invalid or expired reset token");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.deleteByUser(user);
    }
}
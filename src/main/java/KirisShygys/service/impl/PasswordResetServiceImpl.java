package KirisShygys.service.impl;

import KirisShygys.entity.PasswordResetToken;
import KirisShygys.entity.User;
import KirisShygys.repository.PasswordResetTokenRepository;
import KirisShygys.service.PasswordResetService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public PasswordResetServiceImpl(PasswordResetTokenRepository passwordResetTokenRepository) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Override
    public PasswordResetToken createResetToken(User user) {
        String tokenValue = java.util.UUID.randomUUID().toString();
        PasswordResetToken token = new PasswordResetToken(
                tokenValue,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );
        return passwordResetTokenRepository.save(token);
    }

    @Override
    public PasswordResetToken validateResetToken(String token) {
        Optional<PasswordResetToken> optionalToken = passwordResetTokenRepository.findByToken(token);
        if (optionalToken.isEmpty()) {
            throw new IllegalStateException("Invalid or expired password reset token.");
        }
        PasswordResetToken resetToken = optionalToken.get();
        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token has expired.");
        }
        return resetToken;
    }

    @Override
    public void deleteResetToken(PasswordResetToken token) {
        passwordResetTokenRepository.delete(token);
    }
}

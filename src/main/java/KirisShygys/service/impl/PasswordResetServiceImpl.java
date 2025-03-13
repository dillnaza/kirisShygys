package KirisShygys.service.impl;

import KirisShygys.entity.PasswordResetToken;
import KirisShygys.entity.User;
import KirisShygys.exception.InvalidTokenException;
import KirisShygys.exception.TokenExpiredException;
import KirisShygys.repository.PasswordResetTokenRepository;
import KirisShygys.service.PasswordResetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetServiceImpl.class);
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public PasswordResetServiceImpl(PasswordResetTokenRepository passwordResetTokenRepository) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Override
    public PasswordResetToken createResetToken(User user) {
        String tokenValue = UUID.randomUUID().toString();
        PasswordResetToken token = new PasswordResetToken(
                tokenValue,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );
        logger.info("Generated password reset token for user {}", user.getEmail());
        return passwordResetTokenRepository.save(token);
    }

    @Override
    public PasswordResetToken validateResetToken(String token) {
        Optional<PasswordResetToken> optionalToken = passwordResetTokenRepository.findByToken(token);
        if (optionalToken.isEmpty()) {
            logger.warn("Invalid password reset token: {}", token);
            throw new InvalidTokenException("Invalid password reset token.");
        }
        PasswordResetToken resetToken = optionalToken.get();
        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            logger.warn("Expired password reset token: {}", token);
            throw new TokenExpiredException("Password reset token has expired.");
        }
        return resetToken;
    }

    @Override
    public void deleteResetToken(PasswordResetToken token) {
        passwordResetTokenRepository.delete(token);
        logger.info("Deleted password reset token: {}", token.getToken());
    }

    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void deleteExpiredTokens() {
        passwordResetTokenRepository.deleteAllExpiredTokens(LocalDateTime.now());
        logger.info("Expired password reset tokens deleted.");
    }
}

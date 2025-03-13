package KirisShygys.service.impl;

import KirisShygys.entity.ConfirmationToken;
import KirisShygys.exception.InvalidTokenException;
import KirisShygys.exception.TokenExpiredException;
import KirisShygys.repository.ConfirmationTokenRepository;
import KirisShygys.service.ConfirmationTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

    private static final Logger logger = LoggerFactory.getLogger(ConfirmationTokenServiceImpl.class);
    private final ConfirmationTokenRepository confirmationTokenRepository;

    public ConfirmationTokenServiceImpl(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    @Override
    public ConfirmationToken validateToken(String token) {
        Optional<ConfirmationToken> optionalToken = confirmationTokenRepository.findByToken(token);
        if (optionalToken.isEmpty()) {
            logger.warn("Invalid confirmation token: {}", token);
            throw new InvalidTokenException("Invalid confirmation token.");
        }
        ConfirmationToken confirmationToken = optionalToken.get();
        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            logger.warn("Expired confirmation token: {}", token);
            throw new TokenExpiredException("Confirmation token has expired.");
        }
        logger.info("Valid confirmation token: {}", token);
        return confirmationToken;
    }

    @Override
    public void deleteToken(ConfirmationToken token) {
        confirmationTokenRepository.delete(token);
        logger.info("Deleted confirmation token: {}", token.getToken());
    }

    @Override
    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
        logger.info("Saved confirmation token: {}", token.getToken());
    }
}

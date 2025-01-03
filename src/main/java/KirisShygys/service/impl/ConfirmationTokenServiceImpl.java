package KirisShygys.service.impl;

import KirisShygys.entity.ConfirmationToken;
import KirisShygys.entity.User;
import KirisShygys.repository.ConfirmationTokenRepository;
import KirisShygys.service.ConfirmationTokenService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    public ConfirmationTokenServiceImpl(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    @Override
    public ConfirmationToken validateToken(String token) {
        Optional<ConfirmationToken> optionalToken = confirmationTokenRepository.findByToken(token);
        if (optionalToken.isEmpty()) {
            throw new IllegalStateException("Invalid or expired confirmation token.");
        }
        ConfirmationToken confirmationToken = optionalToken.get();
        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token has expired.");
        }
        return confirmationToken;
    }

    @Override
    public void deleteToken(ConfirmationToken token) {
        confirmationTokenRepository.delete(token);
    }

    @Override
    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }
}

package KirisShygys.service;

import KirisShygys.entity.ConfirmationToken;
import KirisShygys.entity.User;

import java.util.Optional;

public interface ConfirmationTokenService {
    ConfirmationToken createToken(User user);
    ConfirmationToken validateToken(String token);
    void deleteToken(ConfirmationToken token);
    void saveConfirmationToken(ConfirmationToken token);
}

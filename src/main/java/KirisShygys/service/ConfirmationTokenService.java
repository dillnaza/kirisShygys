package KirisShygys.service;

import KirisShygys.entity.ConfirmationToken;
import KirisShygys.entity.User;

public interface ConfirmationTokenService {
    ConfirmationToken validateToken(String token);
    void deleteToken(ConfirmationToken token);
    void saveConfirmationToken(ConfirmationToken token);
}

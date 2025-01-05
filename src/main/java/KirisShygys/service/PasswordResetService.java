package KirisShygys.service;

import KirisShygys.entity.PasswordResetToken;
import KirisShygys.entity.User;

public interface PasswordResetService {
    PasswordResetToken createResetToken(User user);
    PasswordResetToken validateResetToken(String token);
    void deleteResetToken(PasswordResetToken token);
}

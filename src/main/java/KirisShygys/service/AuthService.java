package KirisShygys.service;

import KirisShygys.entity.User;

import java.util.Map;

public interface AuthService {
    User register(User user);
    String confirmEmail(String token);
    Map<String, String> login(Map<String, String> loginRequest);
    void requestPasswordReset(Map<String, String> request);
    void resetPassword(String token, Map<String, String> request);
    String refreshToken(Map<String, String> request);
}

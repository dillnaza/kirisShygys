package KirisShygys.service.impl;

import KirisShygys.entity.ConfirmationToken;
import KirisShygys.entity.PasswordResetToken;
import KirisShygys.entity.User;
import KirisShygys.exception.ForbiddenException;
import KirisShygys.exception.InvalidTokenException;
import KirisShygys.exception.TokenExpiredException;
import KirisShygys.service.*;
import KirisShygys.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final CategoryService categoryService;
    private final PasswordResetService resetService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserService userService,
                           CategoryService categoryService,
                           PasswordResetService resetService,
                           AuthenticationManager authenticationManager,
                           JwtUtil jwtUtil,
                           ConfirmationTokenService confirmationTokenService,
                           EmailService emailService,
                           PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.resetService = resetService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.confirmationTokenService = confirmationTokenService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(User user) {
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userService.saveUser(user);
        categoryService.createDefaultCategories(savedUser);
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), savedUser
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        String confirmLink = "http://localhost:8080/api/auth/confirm?token=" + token;
        String link = "kirisShygys://confirm?token=" + token;
        String htmlContent = "<div style=\"font-family: Arial, sans-serif; max-width: 600px; padding: 20px; border: 1px solid #ddd; border-radius: 8px;\">" +
                "<h2 style=\"color: #333;\">Confirm Your Email Address</h2>" +
                "<p>Thank you for registering! To complete your sign-up and activate your account, please confirm your email address by clicking the button below:</p>" +
                "<div style=\"text-align: center; margin: 20px 0;\">" +
                "<a href=\"" + link + "\" " +
                "style=\"background-color: #28a745; color: #fff; padding: 12px 20px; text-decoration: none; border-radius: 5px; display: inline-block;\">" +
                "Confirm Email</a>" +
                "</div>" +
                "<p>If you did not create an account, please ignore this email.</p>" +
                "<p style=\"color: #888; font-size: 12px;\">If the button above does not work, you can use this link: <br>" +
                "<a href=\"" + link + "\" style=\"color: #007bff;\">" + link + "</a>" +
                "</p>" +
                "<hr style=\"border: none; border-top: 1px solid #ddd;\">" +
                "<p style=\"font-size: 12px; color: #888;\">This is an automated email, please do not reply.</p>" +
                "</div>";
        emailService.sendHtmlEmail(user.getEmail(), "Confirm Your Email", htmlContent);
        return savedUser;
    }

    @Override
    public String confirmEmail(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.validateToken(token);
        User user = confirmationToken.getUser();
        user.setEnabled(true);
        userService.saveUser(user);
        confirmationTokenService.deleteToken(confirmationToken);
        return "Email confirmed successfully.";
    }

    @Override
    public Map<String, String> login(Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!user.isEnabled()) {
            throw new ForbiddenException("Account is not activated");
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        String accessToken = jwtUtil.generateToken(email, 60);
        String refreshToken = jwtUtil.generateToken(email, 1440);
        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );
    }


    @Override
    public void requestPasswordReset(Map<String, String> request) {
        String email = request.get("email");
        if (email == null) {
            throw new IllegalArgumentException("Email must be provided");
        }
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User with this email does not exist");
        }
        User user = userOptional.get();
        PasswordResetToken token = resetService.createResetToken(user);
        String resetLink = "http://localhost:8080/api/auth/reset-password/confirm?token=" + token.getToken();
        String link = "kirisShygys://reset-password?token=" + token.getToken();
        String htmlContent = "<div style=\"font-family: Arial, sans-serif; max-width: 600px; padding: 20px; border: 1px solid #ddd; border-radius: 8px;\">" +
                "<h2 style=\"color: #333;\">Password Reset Request</h2>" +
                "<p>We received a request to reset your password. If you made this request, click the button below to proceed:</p>" +
                "<div style=\"text-align: center; margin: 20px 0;\">" +
                "<a href=\"" + link + "\" " +
                "style=\"background-color: #007bff; color: #fff; padding: 12px 20px; text-decoration: none; border-radius: 5px; display: inline-block;\">" +
                "Reset Password</a>" +
                "</div>" +
                "<p>If you did not request this, please ignore this email. Your password will remain unchanged.</p>" +
                "<p style=\"color: #888; font-size: 12px;\">If the button above does not work, you can use this link: <br>" +
                "<a href=\"" + link + "\" style=\"color: #007bff;\">" + link + "</a>" +
                "</p>" +
                "<hr style=\"border: none; border-top: 1px solid #ddd;\">" +
                "<p style=\"font-size: 12px; color: #888;\">This is an automated email, please do not reply.</p>" +
                "</div>";
        emailService.sendHtmlEmail(user.getEmail(), "Reset Password", htmlContent);
    }

    @Override
    public void resetPassword(String token, Map<String, String> request) {
        try {
            PasswordResetToken resetToken = resetService.validateResetToken(token);
            User user = resetToken.getUser();
            String newPassword = request.get("newPassword");
            if (newPassword == null || newPassword.length() < 6) {
                throw new IllegalArgumentException("New password must be at least 6 characters long");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            userService.saveUser(user);
            resetService.deleteResetToken(resetToken);
        } catch (InvalidTokenException | TokenExpiredException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public String refreshToken(Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        String email = jwtUtil.extractUsername(refreshToken);
        return jwtUtil.generateToken(email, 60);
    }
}

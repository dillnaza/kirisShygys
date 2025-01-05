package KirisShygys.controller;

import KirisShygys.entity.ConfirmationToken;
import KirisShygys.entity.PasswordResetToken;
import KirisShygys.entity.User;
import KirisShygys.service.PasswordResetService;
import KirisShygys.util.JwtUtil;
import KirisShygys.service.UserService;
import KirisShygys.service.EmailService;
import KirisShygys.service.ConfirmationTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordResetService resetService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService,
                          PasswordResetService resetService, AuthenticationManager authenticationManager, JwtUtil jwtUtil, ConfirmationTokenService confirmationTokenService,
                          EmailService emailService,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.resetService = resetService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.confirmationTokenService = confirmationTokenService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userService.getUserByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email is already in use");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userService.saveUser(user);
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                savedUser
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        String link = "http://localhost:8080/api/auth/confirm?token=" + token;
        emailService.sendEmail(savedUser.getEmail(), "Confirm your email", "Click the link to confirm your email: " + link);
        return ResponseEntity.ok("Registration successful. Check your email to confirm your account.");
    }

    @GetMapping("/confirm")
    public ResponseEntity<?> confirmEmail(@RequestParam("token") String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.validateToken(token);
        if (confirmationToken == null) {
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }
        User user = confirmationToken.getUser();
        user.setEnabled(true);
        userService.saveUser(user);
        confirmationTokenService.deleteToken(confirmationToken);
        return ResponseEntity.ok("Email confirmed successfully. You can now log in at /login.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            String accessToken = jwtUtil.generateToken(authentication.getName(), 15);
            String refreshToken = jwtUtil.generateToken(authentication.getName(), 1440);
            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);
            return ResponseEntity.ok(tokens);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(403).body("Invalid email or password");
        }
    }

    @PostMapping("/reset-password/request")
    public ResponseEntity<?> requestPasswordReset(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        Optional<User> userOpt = userService.getUserByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found.");
        }
        User user = userOpt.get();
        PasswordResetToken resetToken = resetService.createResetToken(user);
        String link = "http://localhost:8080/api/auth/reset-password/confirm?token=" + resetToken.getToken();
        emailService.sendEmail(user.getEmail(), "Reset Password", "Click the link to reset your password: " + link);
        return ResponseEntity.ok("Password reset link sent to email.");
    }

    @PostMapping("/reset-password/confirm")
    public ResponseEntity<?> confirmResetPassword(@RequestParam("token") String token,
                                                  @RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");
        PasswordResetToken resetToken = resetService.validateResetToken(token);
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.saveUser(user);
        resetService.deleteResetToken(resetToken);
        return ResponseEntity.ok("Password updated successfully.");
    }
}

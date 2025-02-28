package KirisShygys.controller;

import KirisShygys.entity.ConfirmationToken;
import KirisShygys.entity.PasswordResetToken;
import KirisShygys.entity.User;
import KirisShygys.service.PasswordResetService;
import KirisShygys.util.JwtUtil;
import KirisShygys.service.UserService;
import KirisShygys.service.EmailService;
import KirisShygys.service.ConfirmationTokenService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is already in use"));
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
        return ResponseEntity.ok(Map.of(
                "message", "Registration successful. Check your email to confirm your account.",
                "confirmationLink", link
        ));
    }

    @GetMapping("/confirm")
    public ResponseEntity<?> confirmEmail(@RequestParam("token") String token) {
        try {
            ConfirmationToken confirmationToken = confirmationTokenService.validateToken(token);
            if (confirmationToken == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid or expired token"));
            }
            User user = confirmationToken.getUser();
            user.setEnabled(true);
            userService.saveUser(user);
            confirmationTokenService.deleteToken(confirmationToken);
            return ResponseEntity.ok(Map.of("message", "Email confirmed successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "An unexpected error occurred."));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email and password must be provided."));
        }
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid email or password."));
        }
        User user = optionalUser.get();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            String accessToken = jwtUtil.generateToken(authentication.getName(), 15);
            String refreshToken = jwtUtil.generateToken(authentication.getName(), 1440);
            return ResponseEntity.ok(Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken
            ));
        } catch (org.springframework.security.authentication.DisabledException e) {
            return ResponseEntity.status(403).body(Map.of("message", "Email is not verified. Please verify your email."));
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid email or password."));
        }
    }

    @PostMapping("/reset-password/request")
    public ResponseEntity<?> requestPasswordReset(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            PasswordResetToken resetToken = resetService.createResetToken(user);
            String link = "kirisShygys://reset-password?token=" + resetToken.getToken();
            String htmlContent = "<p>Click <a href=\"" + link + "\">here</a> to reset your password.</p>"
                    + "<p><b>" + link + "</b></p>";
            emailService.sendHtmlEmail(user.getEmail(), "Reset Password", htmlContent);
        }
        return ResponseEntity.ok(Map.of("message", "If the email exists, a reset link will be sent to it."));
    }

    @PostMapping("/reset-password/confirm")
    public ResponseEntity<?> ResetPassword(@RequestParam("token") String token,
                                                  @RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");
        try {
            PasswordResetToken resetToken = resetService.validateResetToken(token);
            if (resetToken == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid or expired token"));
            }
            User user = resetToken.getUser();
            user.setPassword(passwordEncoder.encode(newPassword));
            userService.saveUser(user);
            resetService.deleteResetToken(resetToken);
            return ResponseEntity.ok(Map.of("message", "Password updated successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "An unexpected error occurred."));
        }
    }
}
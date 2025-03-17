package KirisShygys.controller;

import KirisShygys.entity.User;
import KirisShygys.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User registeredUser = authService.register(user);
            return ResponseEntity.ok(Map.of(
                    "message", "Registration successful. Check your email to confirm your account."
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/confirm")
    public ResponseEntity<?> confirmEmail(@RequestParam("token") String token) {
        try {
            String message = authService.confirmEmail(token);
            return ResponseEntity.ok(Map.of("message", message));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        Map<String, String> tokens = authService.login(loginRequest);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/reset-password/request")
    public ResponseEntity<?> requestPasswordReset(@RequestBody Map<String, String> request) {
        authService.requestPasswordReset(request);
        return ResponseEntity.ok(Map.of("message", "Password reset email sent."));
    }

    @PostMapping("/reset-password/confirm")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestBody Map<String, String> request) {
        authService.resetPassword(token, request);
        return ResponseEntity.ok(Map.of("message", "Password updated successfully."));
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        String newAccessToken = authService.refreshToken(request);
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }
}

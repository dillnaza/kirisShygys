package KirisShygys.controller;

import KirisShygys.dto.OAuth2Response;
import KirisShygys.entity.User;
import KirisShygys.service.UserService;
import KirisShygys.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth/oauth2")
public class OAuth2Controller {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public OAuth2Controller(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/authenticate")
    public ResponseEntity<?> authenticate(@AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User != null) {
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");
            Optional<User> existingUser = userService.findByEmail(email);
            User user;
            if (existingUser.isPresent()) {
                user = existingUser.get();
            } else {
                user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setEnabled(true);
                user.setPassword("OAUTH2_USER");
                userService.saveUser(user);
            }
            String accessToken = jwtUtil.generateToken(user.getEmail(), 15);
            String refreshToken = jwtUtil.generateToken(user.getEmail(), 1440);
            return ResponseEntity.ok(new OAuth2Response(
                    user.getName(),
                    user.getEmail(),
                    accessToken,
                    refreshToken
            ));
        }
        return ResponseEntity.status(401).body("Authentication failed or user data not available.");
    }
}
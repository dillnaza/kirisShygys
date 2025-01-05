package KirisShygys.controller;

import KirisShygys.dto.OAuth2Response;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/oauth2")
public class OAuth2Controller {

    @GetMapping("/success")
    public ResponseEntity<OAuth2Response> handleSuccess(@AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User != null) {
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");
            OAuth2Response response = new OAuth2Response(
                    name,
                    email
            );
            return ResponseEntity.ok(response);
        }
        OAuth2Response errorResponse = new OAuth2Response(
                null,
                null
        );
        return ResponseEntity.status(401).body(errorResponse);
    }
}

package KirisShygys.controller;

import KirisShygys.service.StreakService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/streak")
public class StreakController {

    private final StreakService streakService;

    public StreakController(StreakService streakService) {
        this.streakService = streakService;
    }

    private String getAuthToken(HttpServletRequest request) {
        return (String) request.getAttribute("AuthToken");
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getStreakStatus(HttpServletRequest request) {
        String token = getAuthToken(request);
        boolean hasTodayFire = streakService.hasTodayFire(token);
        int streakCount = streakService.getStreakCount(token);
        return ResponseEntity.ok(Map.of(
                "hasTodayTransaction", hasTodayFire,
                "streakCount", streakCount
        ));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> lightFire(HttpServletRequest request) {
        streakService.lightFireManually(getAuthToken(request));
        return ResponseEntity.ok(Map.of("success", true));
    }
}
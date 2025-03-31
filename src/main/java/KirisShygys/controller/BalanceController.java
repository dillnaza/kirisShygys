package KirisShygys.controller;

import KirisShygys.dto.BalanceDTO;
import KirisShygys.service.BalanceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/balance")
public class BalanceController {

    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    private String getAuthToken(HttpServletRequest request) {
        return (String) request.getAttribute("AuthToken");
    }

    @GetMapping
    public ResponseEntity<BalanceDTO> getUserBalance(HttpServletRequest request) {
        return ResponseEntity.ok(balanceService.getUserBalance(getAuthToken(request)));
    }

    @GetMapping("/period")
    public ResponseEntity<BalanceDTO> getUserBalanceByPeriod(HttpServletRequest request,
                                                             @RequestParam LocalDate startDate,
                                                             @RequestParam LocalDate endDate) {
        return ResponseEntity.ok(balanceService.getUserBalanceByPeriod(getAuthToken(request), startDate, endDate));
    }
}
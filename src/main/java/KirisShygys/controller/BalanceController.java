package KirisShygys.controller;

import KirisShygys.dto.BalanceDTO;
import KirisShygys.service.BalanceService;
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

    @GetMapping
    public ResponseEntity<BalanceDTO> getUserBalance(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(balanceService.getUserBalance(token.replace("Bearer ", "")));
    }

    @GetMapping("/period")
    public ResponseEntity<BalanceDTO> getUserBalanceByPeriod(@RequestHeader("Authorization") String token,
                                                             @RequestParam LocalDate startDate,
                                                             @RequestParam LocalDate endDate) {
        return ResponseEntity.ok(balanceService.getUserBalanceByPeriod(token.replace("Bearer ", ""), startDate, endDate));
    }
}

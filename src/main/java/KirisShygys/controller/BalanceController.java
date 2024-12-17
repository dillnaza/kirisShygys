package KirisShygys.controller;

import KirisShygys.entity.Balance;
import KirisShygys.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/balances")
public class BalanceController {
    @Autowired
    private BalanceService balanceService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Balance>> getBalancesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(balanceService.getBalancesByUserId(userId));
    }
}


package KirisShygys.controller;

import KirisShygys.entity.Transaction;
import KirisShygys.entity.User;
import KirisShygys.service.BalanceService;
import KirisShygys.service.TransactionService;
import KirisShygys.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/balance")
public class BalanceController {

    private final BalanceService balanceService;
    private final TransactionService transactionService;
    private final UserService userService;

    public BalanceController(BalanceService balanceService, TransactionService transactionService, UserService userService) {
        this.balanceService = balanceService;
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getUserDashboard(Principal principal) {
        Optional<User> user = userService.findByEmail(principal.getName());

        if (user.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }

        BigDecimal totalBalance = balanceService.getUserTotalBalance(user.get());
        BigDecimal totalIncome = balanceService.getUserIncome(user.get());
        BigDecimal totalExpenses = balanceService.getUserExpenses(user.get());
        List<Transaction> transactions = transactionService.getUserTransactions(user.get());

        Map<String, Object> response = new HashMap<>();
        response.put("balance", totalBalance);
        response.put("income", totalIncome);
        response.put("expenses", totalExpenses);
        response.put("transactions", transactions);

        return ResponseEntity.ok(response);
    }
}

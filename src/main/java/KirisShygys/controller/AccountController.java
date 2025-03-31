package KirisShygys.controller;

import KirisShygys.entity.Account;
import KirisShygys.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    private String getAuthToken(HttpServletRequest request) {
        return (String) request.getAttribute("AuthToken");
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAccounts(HttpServletRequest request) {
        return ResponseEntity.ok(accountService.getAll(getAuthToken(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getById(HttpServletRequest request, @PathVariable Long id) {
        return ResponseEntity.ok(accountService.getById(getAuthToken(request), id));
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(HttpServletRequest request, @RequestBody Account account) {
        return ResponseEntity.ok(accountService.create(getAuthToken(request), account));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(HttpServletRequest request, @PathVariable Long id, @RequestBody Account updatedAccount) {
        return ResponseEntity.ok(accountService.update(getAuthToken(request), id, updatedAccount));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(HttpServletRequest request, @PathVariable Long id) {
        accountService.delete(getAuthToken(request), id);
        return ResponseEntity.noContent().build();
    }
}

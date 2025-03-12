package KirisShygys.controller;

import KirisShygys.entity.Account;
import KirisShygys.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;


    public AccountController(AccountService accountService) {
        this.accountService = accountService;

    }

    @GetMapping
    public ResponseEntity<List<Account>> getAccounts(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(accountService.getAccounts(token.replace("Bearer ", "")));
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestHeader("Authorization") String token, @RequestBody Account account) {
        return ResponseEntity.ok(accountService.createAccount(token.replace("Bearer ", ""), account));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody Account updatedAccount) {
        return ResponseEntity.ok(accountService.updateAccount(token.replace("Bearer ", ""), id, updatedAccount));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        accountService.deleteAccount(token.replace("Bearer ", ""), id);
        return ResponseEntity.noContent().build();
    }
}